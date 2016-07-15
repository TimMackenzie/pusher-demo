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
import android.content.Intent;

/**
 * Created by Tim on 7/14/2016.
 */
public class ChatStatic {
    public static void sendEmail(final Context context) {
        Intent sendIntent = new Intent(android.content.Intent.ACTION_SEND);
        sendIntent.setType("message/rfc822");
        sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, context.getString(R.string.email_subject));
        sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, context.getString(R.string.email_body));
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.email_prompt)));
    }
}
