/*
 * Copyright 2016 Simplify Now, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.simplifynowsoftware.pusherdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.rest.data.Result;
import com.simplifynowsoftware.pusherdemo.data.PusherMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Tim on 7/12/2016.
 *
 * This is a simple demo app to show text send and receive using Pusher and a public channel
 */
public class ChatActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    protected static final String LOGTAG        = "PusherDemo";

    // If using this event/channel combo, messages will pop up on dev console
    protected static final String EVENT_TEST    = "my_event";
    protected static final String CHANNEL_TEST  = "test_channel";

    protected static final String EVENT         = "my-event";
    protected static final String CHANNEL       = "messages";

    protected static final String KEY_MESSAGE   = "message";
    protected static final String KEY_SENDER    = "senderName";
    protected static final String KEY_SENDER_ID = "senderId";
    protected static final String KEY_TIME      = "time";

    protected com.pusher.client.Pusher mPusherReceiver;
    protected com.pusher.client.channel.Channel mChannel;
    protected com.pusher.rest.Pusher mPusherSender;

    protected String mPusherAppId;
    protected String mPusherKey;
    protected String mPusherSecret;

    protected String mUserName;
    protected String mUserId;
    protected boolean mUseTestChannel;
    protected String mEventName;
    protected String mChannelName;
    protected SubscriptionEventListener mListener;

    protected Dialog mDialog;

    protected List<PusherMessage> mMessageList;

    protected boolean mSkipNextClear;

    @BindView(R.id.pusher_new_message) TextView mNewMessage;
    @BindView(R.id.message_list) RecyclerView mMessageListView;

    // Data to save through rotation or other configuration changes
    public class ChatConfig {
        public String userId;
        public boolean useTestChannel;
        public List<PusherMessage> messageList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ButterKnife.bind(this);

        // Ensure we have valid API keys
        getPusherKeys();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Generate user name from device ID
        mUserId = UUID.randomUUID().toString().substring(0, 6);
        mUserName = mUserId;

        mMessageListView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mMessageListView.setLayoutManager(llm);

        mMessageList = new ArrayList<>();
        mMessageListView.setAdapter(new MessageAdapter(mMessageList, mUserName));

        ChatConfig config = (ChatConfig)getLastCustomNonConfigurationInstance();
        if(null != config) {
            mUserName = config.userId;
            setTestChannel(config.useTestChannel);
            mMessageList.addAll(config.messageList);

            ((MessageAdapter)mMessageListView.getAdapter()).setId(mUserId);

            mSkipNextClear = true;
        } else {
            setTestChannel(false);
        }

        initPusher();
    }

    @Override
    public void onDestroy() {
        if(null != mDialog) {
            mDialog.dismiss();
            mDialog = null;
        }

        super.onDestroy();
    }

    public Object onRetainCustomNonConfigurationInstance() {
        ChatConfig config = new ChatConfig();
        config.userId = mUserName;
        config.useTestChannel = mUseTestChannel;
        config.messageList = mMessageList;

        return config;
    }

    protected void setTestChannel(final boolean useTest) {
        mUseTestChannel = useTest;

        if(mUseTestChannel){
            mEventName = EVENT_TEST;
            mChannelName = CHANNEL_TEST;
        } else {
            mEventName = EVENT;
            mChannelName = CHANNEL;
        }
    }

    protected void getPusherKeys() {
        mPusherAppId = getString(R.string.pusher_appid);
        mPusherKey = getString(R.string.pusher_key);
        mPusherSecret = getString(R.string.pusher_secret);

        final String invalid = getString(R.string.pusher_value_invalid);

        if(invalid.equals(mPusherAppId)) {
            Log.e(LOGTAG, "Invalid Pusher keys, cannot continue." +
                "\nYou must sign up at Pusher.com and create an app." +
                "\nThen set the API keys (appId/key/secret) in pusher_keys.xml");
            finish();
        }
    }

    protected void initPusher() {
        // Set up sender
        mPusherSender = new com.pusher.rest.Pusher(mPusherAppId, mPusherKey, mPusherSecret);

        // Now set up receiver
        mPusherReceiver = new com.pusher.client.Pusher(mPusherKey);
        mPusherReceiver.connect();

        bindChannel();
    }

    protected void bindChannel() {
        if(null != mChannel && mChannel.isSubscribed()) {
            unBindChannel();
        }

        mChannel = mPusherReceiver.subscribe(mChannelName);

        mListener = new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                Gson gson = new Gson();
                PusherMessage message = gson.fromJson(data, PusherMessage.class);

                Log.d(LOGTAG, "Message: " + data);///

                mMessageList.add(message);

                mMessageListView.post(new Runnable() {
                    public void run() {
                        mMessageListView.getAdapter().notifyDataSetChanged();
                    }
                });
            }
        };

        mChannel.bind(mEventName, mListener);

        setTitle(mChannelName);

        if(mSkipNextClear) {
            mSkipNextClear = false;
        } else {
            clearMessages();
        }
    }

    protected void unBindChannel() {
        mChannel.unbind(mEventName, mListener);
        mPusherReceiver.unsubscribe(mChannelName);
    }

    public void postMessage(final String message) {
        final Map<String, String> data = new HashMap<>();
        data.put(KEY_MESSAGE, message);
        data.put(KEY_SENDER, mUserName);
        data.put(KEY_SENDER_ID, mUserId);
        data.put(KEY_TIME, Long.toString(new Date().getTime()));

        new Thread(new Runnable() {
            @Override
            public void run() {
                Result result = mPusherSender.trigger(mChannelName, mEventName, data);

                if(result.getStatus() != Result.Status.SUCCESS) {
                    Log.w(LOGTAG, "Failed to send message - " + result.getHttpStatus().toString() + ": " + result.getMessage());
                }
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_share) {
            ChatStatic.sendEmail(this);
        } else if (id == R.id.nav_send) {
            ChatStatic.sendEmail(this);
        } else if (id == R.id.nav_rename) {
            changeUserName();
        } else if (id == R.id.nav_revert) {
            clearMessages();
        } else if (id == R.id.nav_switchtest) {
            setTestChannel(!mUseTestChannel);
            bindChannel();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onClickSend(final View view) {
        final String nextMessage = mNewMessage.getText().toString();

        if(!TextUtils.isEmpty(nextMessage)){
            postMessage(nextMessage);
            mNewMessage.setText("");
        }

        hideKeyboard();
    }

    protected void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    protected void changeUserName() {
        final AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
        alt_bld.setCancelable(false);

        alt_bld.setTitle(getString(R.string.title_username));
        alt_bld.setMessage(getString(R.string.message_username, mUserName));
        final EditText et = new EditText(this);
        alt_bld.setView(et);

        alt_bld.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
                final String name = et.getText().toString();
                if(!TextUtils.isEmpty(name)) {
                    mUserName = et.getText().toString();
                }

                dialog.dismiss();
            }
        });

        alt_bld.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
                dialog.dismiss();
            }
        });

        mDialog = alt_bld.create();
        mDialog.show();
    }

    protected void clearMessages() {
        mMessageList.clear();

        mMessageListView.post(new Runnable() {
            public void run() {
                mMessageListView.getAdapter().notifyDataSetChanged();
            }
        });
    }
}