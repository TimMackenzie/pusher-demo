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

package com.simplifynowsoftware.pusherdemo.data;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Tim on 7/12/2016.
 */
public class PusherMessage {
    String message;
    String senderName;
    String senderId;
    String time;

    public String getMessage() {
        return message;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getFormattedTime() {
        Date d = new Date(Long.valueOf(time));
        return DateFormat.getTimeInstance().format(d);
    }
}
