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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.simplifynowsoftware.pusherdemo.data.PusherMessage;

import java.util.List;

/**
 * Created by Tim on 7/14/2016.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {
    private List<PusherMessage> messageList;
    private String mMyId;

    public MessageAdapter(List<PusherMessage> messageList, final String myId) {
        this.messageList = messageList;
        mMyId = myId;
    }

    public void setId(final String myId) {
        mMyId = myId;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public void onBindViewHolder(MessageViewHolder viewHolder, int i) {
        PusherMessage pm = messageList.get(i);

        if(mMyId.equals(pm.getSenderId())) {
            final Context context = viewHolder.mName2.getContext();
            viewHolder.mInitials2.setText(pm.getSenderName().substring(0,1));
            viewHolder.mName2.setText(context.getString(R.string.message_from_format_short, pm.getFormattedTime()));

            viewHolder.mMessage2.setText(pm.getMessage());

            viewHolder.mChatGroup1.setVisibility(View.GONE);
            viewHolder.mChatGroup2.setVisibility(View.VISIBLE);
        } else {
            final Context context = viewHolder.mName1.getContext();
            viewHolder.mInitials1.setText(pm.getSenderName().substring(0,1));
            viewHolder.mName1.setText(context.getString(R.string.message_from_format, pm.getSenderName(), pm.getFormattedTime()));
            viewHolder.mMessage1.setText(pm.getMessage());

            viewHolder.mChatGroup1.setVisibility(View.VISIBLE);
            viewHolder.mChatGroup2.setVisibility(View.GONE);
        }
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.chat_line, viewGroup, false);

        return new MessageViewHolder(itemView);
    }
}

