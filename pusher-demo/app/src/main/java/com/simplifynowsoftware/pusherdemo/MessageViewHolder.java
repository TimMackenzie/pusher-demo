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

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Tim on 7/14/2016.
 */
public class MessageViewHolder extends RecyclerView.ViewHolder {
    protected TextView mInitials1;
    protected TextView mName1;
    protected TextView mMessage1;
    protected TextView mInitials2;
    protected TextView mName2;
    protected TextView mMessage2;

    protected RelativeLayout mChatGroup1;
    protected RelativeLayout mChatGroup2;

    public MessageViewHolder(View v) {
        super(v);
        mInitials1  = (TextView) v.findViewById(R.id.chat_initials_1);
        mName1      = (TextView) v.findViewById(R.id.chat_name_1);
        mMessage1   = (TextView) v.findViewById(R.id.chat_message_1);

        mChatGroup1 = (RelativeLayout) v.findViewById(R.id.chat_group_1);

        mInitials2  = (TextView) v.findViewById(R.id.chat_initials_2);
        mName2      = (TextView) v.findViewById(R.id.chat_name_2);
        mMessage2   = (TextView) v.findViewById(R.id.chat_message_2);

        mChatGroup2 = (RelativeLayout) v.findViewById(R.id.chat_group_2);
    }
}