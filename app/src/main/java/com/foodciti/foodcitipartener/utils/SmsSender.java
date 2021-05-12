package com.foodciti.foodcitipartener.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.ArrayList;

public class SmsSender {
    private String numbers;
    private Context context;
    private BroadcastReceiver sendBroadcastReceiver;
    private BroadcastReceiver deliveryBroadcastReceiver;
    private MessageStatusListener messageStatusListener;

    public SmsSender(Context context) {
        this.context = context;
        if (!(context instanceof MessageStatusListener))
            throw new RuntimeException("context must implement SmsSender.MessageStatusListener");
        messageStatusListener = (MessageStatusListener) context;
        sendBroadcastReceiver = new SentReceiver();
        deliveryBroadcastReceiver = new DeliverReceiver(messageStatusListener);
    }

    public SmsSender(Context context, MessageStatusListener messageStatusListener) {
        this.context = context;
        sendBroadcastReceiver = new SentReceiver();
        this.messageStatusListener = messageStatusListener;
        deliveryBroadcastReceiver = new DeliverReceiver(messageStatusListener);
    }

    public void unregisterReceiver() throws Exception {
        if (sendBroadcastReceiver != null) {
            context.unregisterReceiver(sendBroadcastReceiver);
        }
        if (deliveryBroadcastReceiver != null) {
            context.unregisterReceiver(deliveryBroadcastReceiver);
        }
    }

    public void sendSMS(final String phoneNumber, String message) {
        IntentFilter sentIF = new IntentFilter(phoneNumber);

        IntentFilter delIF = new IntentFilter(phoneNumber);

        context.registerReceiver(sendBroadcastReceiver, sentIF);
        context.registerReceiver(deliveryBroadcastReceiver, delIF);

        Intent intent = new Intent(phoneNumber);
        Intent intentDel = new Intent(phoneNumber);
        intentDel.putExtra("c_no", phoneNumber);
        intent.putExtra("c_no", phoneNumber);

        /*PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
                intentDel, PendingIntent.FLAG_CANCEL_CURRENT);*/
        try {
            SmsManager sms = SmsManager.getDefault();

//            sms.sendTextMessage(phoneNumber, "", message, sentPI, deliveredPI);


//            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> parts = sms.divideMessage(message);
            ArrayList<PendingIntent> sentIntents = new ArrayList<>();
            ArrayList<PendingIntent> deliveryIntents = new ArrayList<>();

            for (int i = 0; i < parts.size(); i++) {
                sentIntents.add(PendingIntent.getBroadcast(context, 0, intent, 0));
                deliveryIntents.add(PendingIntent.getBroadcast(context, 0, intentDel, 0));
            }


            sms.sendMultipartTextMessage(phoneNumber, null, parts, sentIntents, deliveryIntents);


        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("MainActivity.sendSMS EXCEPTION: " + ex.getMessage());
        }
    }

    public static class DeliverReceiver extends BroadcastReceiver {

        private MessageStatusListener listener;

        public DeliverReceiver(MessageStatusListener mListener) {
            listener = mListener;
        }

        @Override
        public void onReceive(Context context, Intent arg1) {
            System.out.println("DeliverReceiver.onReceive");
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(context, "SMS DELIVERED..." + arg1.getExtras().getString("c_no"),
                            Toast.LENGTH_SHORT).show();
                    listener.onMessageDelivered(arg1.getExtras().getString("c_no"));
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(context, "NOT delivered...",
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }

    public interface MessageStatusListener {
        void onMessageDelivered(String number);
    }
}
