package com.foodciti.foodcitipartener.services;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foodciti.foodcitipartener.activities.SplashScreen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SmsService extends Service {
    private final String TAG = "SmsService";
    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    public static final String SMS_SENT_ACTION = "sms_sent";
    public static final String SMS_DELIVERED_ACTION = "sms_delivered";
    public static final String SMS_BUNDLE = "pdus";
    private BroadcastReceiver smsReceiver;

    private int startMode = START_NOT_STICKY;
    private IBinder iBinder;
    private boolean mAllowRebind;

    private SmsSentBroadcast smsSentBroadcast;
    private SmsDeliveredBroadcast smsDeliveredBroadcast;

    private LocalBroadcastManager localBroadcastManager;

    private final Set<BroadcastReceiver> broadcastReceivers = new HashSet<>();

    public static SmsService instance=null;

    /**
     * A client is binding to the service with bindService()
     */
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        iBinder = new LocalBinder();
        return iBinder;
    }

    /**
     * Called when all clients have unbound with unbindService()
     */
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /**
     * Called when a client is binding to the service with bindService()
     */
    @Override
    public void onRebind(Intent intent) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return startMode;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        final IntentFilter theFilter = new IntentFilter();
        theFilter.addAction(ACTION);
        this.smsReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // Do whatever you need it to do when it receives the broadcast
                // Example show a Toast message...
                Bundle intentExtras = intent.getExtras();
                if (intentExtras != null) {
                    Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
                    String smsMessageStr = "";
                    if(sms!=null) {
                        for (Object sm : sms) {
                            String format = intentExtras.getString("format");
                            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sm);

                            String smsBody = smsMessage.getMessageBody();
                            String address = smsMessage.getOriginatingAddress();

                            smsMessageStr += "SMS From: " + address + "\n";
                            smsMessageStr += smsBody + "\n";
                        }
                        showSuccessfulBroadcast(smsMessageStr);
                    }
                }
            }
        };
        // Registers the receiver so that your service will listen for
        // broadcasts
        this.registerReceiver(this.smsReceiver, theFilter);

        Intent intent = new Intent(SplashScreen.INTENT_SERVICE_STARTED);
        intent.putExtra(SplashScreen.ARG_SERVICE_NAME, "SMS Service");
        localBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this.smsReceiver);
        for(BroadcastReceiver b: broadcastReceivers) {
            this.unregisterReceiver(b);
        }
    }

    private void showSuccessfulBroadcast(String msg) {
        Toast.makeText(this, "New SMS " + msg, Toast.LENGTH_LONG)
                .show();
    }

    public List<String> refreshSmsInbox() {
        List<String> stringList = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        try(Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null)) {
            int indexBody = smsInboxCursor.getColumnIndex("body");
            int indexAddress = smsInboxCursor.getColumnIndex("address");
            if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return stringList;

            do {
                String str = "SMS From: " + smsInboxCursor.getString(indexAddress) +
                        "\n" + smsInboxCursor.getString(indexBody) + "\n";
                stringList.add(str);
            } while (smsInboxCursor.moveToNext());
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return stringList;
    }

    public void sendSms(String phoneNumber, String message) {
        phoneNumber = phoneNumber.trim();

        IntentFilter sentIF = new IntentFilter(SMS_SENT_ACTION);
        IntentFilter delIF = new IntentFilter(SMS_DELIVERED_ACTION);

       /* this.registerReceiver(sendBroadcastReceiver, sentIF);
        this.registerReceiver(deliveryBroadcastReceiver, delIF);*/

        Intent intent = new Intent(SMS_SENT_ACTION);
        Intent intentDel = new Intent(SMS_DELIVERED_ACTION);
        intentDel.putExtra("c_no", phoneNumber);
        intent.putExtra("c_no", phoneNumber);

        /*PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
                intentDel, PendingIntent.FLAG_CANCEL_CURRENT);*/
        try {
            SmsManager sms = SmsManager.getDefault();

//            sms.sendTextMessage(phoneNumber, "", message, sentPI, deliveredPI);

            ArrayList<String> parts = sms.divideMessage(message);
            ArrayList<PendingIntent> sentIntents = new ArrayList<>();
            ArrayList<PendingIntent> deliveryIntents = new ArrayList<>();

            int msgParts = parts.size();
            SmsSentBroadcast smsSentBroadcast = new SmsSentBroadcast(msgParts);
            broadcastReceivers.add(smsSentBroadcast);
            this.registerReceiver(smsSentBroadcast, sentIF);

            SmsDeliveredBroadcast smsDeliveredBroadcast = new SmsDeliveredBroadcast();
            broadcastReceivers.add(smsDeliveredBroadcast);
            this.registerReceiver(smsDeliveredBroadcast, delIF);

            PendingIntent piSent = PendingIntent.getBroadcast(this, 0 ,intent, 0 );
            PendingIntent piDel = PendingIntent.getBroadcast(this, 0 ,intentDel, 0 );

            if(parts.size()==1) {
                String msg = parts.get(0);
                sms.sendTextMessage(phoneNumber,null, msg, piSent, piDel);
            } else {

                for (int i = 0; i < parts.size(); i++) {
                    sentIntents.add(piSent);
                    deliveryIntents.add(piDel);
                }


                sms.sendMultipartTextMessage(phoneNumber, null, parts, sentIntents, deliveryIntents);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("MainActivity.sendSMS EXCEPTION: " + ex.getMessage());
        }

    }

    public class SmsSentBroadcast extends BroadcastReceiver {
        private int msgParts;

        public SmsSentBroadcast(int msgParts) {
            this.msgParts = msgParts;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "SMS onReceive intent received.");
            boolean anyError = false;
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                case SmsManager.RESULT_ERROR_NULL_PDU:
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    anyError = true;
                    break;
            }
            msgParts--;
            if (msgParts == 0) {
//                hideProgressBar();
                if (anyError) {
                    Toast.makeText(context,
                            "Sms Sending failed",
                            Toast.LENGTH_SHORT).show();
                } else {
                    //success

                }

                unregisterReceiver(this);
                broadcastReceivers.remove(this);
            }

        }
    }


    public class SmsDeliveredBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "-------------------received delivery broadcast intent: "+intent.getAction());
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(context, "SMS DELIVERED..." + intent.getExtras().getString("c_no"),
                            Toast.LENGTH_SHORT).show();
//                    listener.onMessageDelivered(intent.getExtras().getString("c_no"));
                    Intent i = new Intent(SMS_DELIVERED_ACTION);
                    i.putExtra("c_no", intent.getExtras().getString("c_no"));
                    localBroadcastManager.sendBroadcast(intent);
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(context, "NOT delivered...",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
            unregisterReceiver(this);
            broadcastReceivers.remove(this);
        }
    }

    public class LocalBinder extends Binder {
        public SmsService getService() {
            return SmsService.this;
        }
    }

}

