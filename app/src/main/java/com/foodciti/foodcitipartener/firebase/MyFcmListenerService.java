/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foodciti.foodcitipartener.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.activities.BasicMenuActivity;
import com.foodciti.foodcitipartener.dialogs.OrderInfo;
import com.foodciti.foodcitipartener.utils.SessionManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;


public class MyFcmListenerService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    String mUrl, is_breaking, article_id, body, dailyMotion_id = "";
    NotificationManager mNotificationManager;
    Intent customIntent = null;
    String bigStyleUrl = "";
    SessionManager sessionManager;
    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        sessionManager = new SessionManager(this);
        sessionManager.setUserToken(s);
        System.out.println("Refreshed token " + s);
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
//        if (GreCampaignSdk.handleNotification(this,remoteMessage.getData())) {
//            return;
//        }
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        if (remoteMessage.getData().containsKey("url"))
            mUrl = remoteMessage.getData().get("url");
        if (remoteMessage.getData().containsKey("isBreaking"))
            is_breaking = remoteMessage.getData().get("isBreaking");
        if (remoteMessage.getData().containsKey("article_id"))
            article_id = remoteMessage.getData().get("article_id");
        if (remoteMessage.getData().containsKey("dailymotion_stream_id"))
            dailyMotion_id = remoteMessage.getData().get("dailymotion_stream_id");
        if (remoteMessage.getData().containsKey("image_path"))
            bigStyleUrl = remoteMessage.getData().get("image_path");
        if (remoteMessage.getData().containsKey("body"))
            body = remoteMessage.getData().get("body");
        CustomNotification(body, bigStyleUrl);
    }


    public void CustomNotification(String message, String imageUrl) {
        String channelId = "food-01";
        String channelName = "Foodciti";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        int count = 0;
        Random rand = new Random();
        int value = rand.nextInt(100);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final int requestCode = (int) System.currentTimeMillis() / 1000;
        // Using RemoteViews to bind custom layouts into Notification
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            getNotificationManagerService().createNotificationChannel(mChannel);
        }

        Bitmap bitmap = getBitmapfromUrl(imageUrl);
        NotificationCompat.BigPictureStyle notifystyle = new NotificationCompat.BigPictureStyle();
        notifystyle.bigPicture(bitmap);

        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.customnotification);
        remoteViews.setImageViewBitmap(R.id.bannerImageView, bitmap);
        remoteViews.setImageViewResource(R.id.imagenotileft, R.drawable.ns_logo_header);
        remoteViews.setTextViewText(R.id.text, message);
        remoteViews.setTextViewText(R.id.time, "12:30");
        // Open NotificationView Class on Notification Click
        Bitmap icon = BitmapFactory.decodeResource(getBaseContext().getResources(),
                R.drawable.ns_logo_header);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                // Set Icon
                .setSmallIcon(R.drawable.im_push)
                // Set Ticker Message
                .setTicker(getString(R.string.customnotificationticker))
                .setContentTitle(message)
                .setLargeIcon(icon)
                // Dismiss Notification
                .setAutoCancel(true)
                // Set PendingIntent into Notification
                // Set RemoteViews into Notification
                .setCustomBigContentView(remoteViews);

        if (is_breaking.equalsIgnoreCase("0")) {
//            customIntent = new Intent(this, BasicMenuActivity.class);
            sendRefreshPayment();
        }
//        builder.setContentIntent(PendingIntent.getActivity(this, requestCode, customIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), PendingIntent.FLAG_ONE_SHOT));
//        builder.setDefaults(Notification.DEFAULT_SOUND);
//        builder.setDefaults(Notification.DEFAULT_ALL);
//        getNotificationManagerService().notify(value, builder.build());
    }
    private void sendRefreshPayment() {
        Intent intent = new Intent("RefreshPayment");
        intent.putExtra("message", "P");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    public String replace(String str, int index, char replace) {
        if (str == null) {
            return str;
        } else if (index < 0 || index >= str.length()) {
            return str;
        }
        char[] chars = str.toCharArray();
        chars[index] = replace;
        return String.valueOf(chars);
    }

    protected NotificationManager getNotificationManagerService() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }


    private static Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL e = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) e.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        } catch (Exception var5) {
            var5.printStackTrace();
            return null;
        }
    }
}