package com.foodciti.foodcitipartener.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent arg1) {
        System.out.println("SentReceiver.onReceive");
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                System.out.println("SentReceiver.onReceive " + arg1.hasExtra("c_no"));
                Toast.makeText(context, "SMS SENT... to " + arg1.getExtras().getString("c_no"), Toast.LENGTH_SHORT)
                        .show();
//                context.startActivity(new Intent(context, ChooseOption.class));
//                overridePendingTransition(R.anim.animation, R.anim.animation2);
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                Toast.makeText(context, "Generic failure",
                        Toast.LENGTH_SHORT).show();
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                Toast.makeText(context, "No service",
                        Toast.LENGTH_SHORT).show();
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                Toast.makeText(context, "Null PDU", Toast.LENGTH_SHORT)
                        .show();
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                Toast.makeText(context, "Radio off",
                        Toast.LENGTH_SHORT).show();
                break;
        }

    }
}