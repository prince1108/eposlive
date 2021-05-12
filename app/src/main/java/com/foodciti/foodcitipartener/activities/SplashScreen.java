package com.foodciti.foodcitipartener.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.gson.PostalData;
import com.foodciti.foodcitipartener.realm_entities.PostalInfo;
import com.foodciti.foodcitipartener.realm_entities.Vendor;
import com.foodciti.foodcitipartener.services.CallerIDService;
import com.foodciti.foodcitipartener.services.FirebaseBackgroundService;
import com.foodciti.foodcitipartener.services.PrintService;
import com.foodciti.foodcitipartener.services.SmsService;
import com.foodciti.foodcitipartener.utils.DataInitializers;
import com.foodciti.foodcitipartener.utils.RealmManager;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

import io.realm.Realm;

public class SplashScreen extends AppCompatActivity {
    private final String TAG = "SplashScreen";
    public static final String INTENT_SERVICE_STARTED = "service_started";
    public static final String ARG_SERVICE_NAME = "service_name";
    private final int NUM_SERVICES = 3;
    private int counter=0;
    private TextView progressMessage;
    private Handler handler;

    private BroadcastReceiver servicesStartedBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            counter++;
            Log.d(TAG, "Counter: "+counter);
            String name = intent.getStringExtra(ARG_SERVICE_NAME);
            progressMessage.append(name+" Started \n");
            if(counter == NUM_SERVICES) {
                handler.postDelayed(()->{
                    progressMessage.setText("Starting Application");
                }, 1000);
                handler.postDelayed(()->{
//                    startActivity(new Intent(SplashScreen.this, NewCustomerInfoActivity.class));
                    Realm realm = RealmManager.getLocalInstance();
                    Vendor vendor = realm.where(Vendor.class).isNotNull("name").isNotNull("tel_no").findFirst();
                    if(vendor==null) {
                        Log.d(TAG, "Ravi: "+"VendorInfoActivity");
                        Intent intent1 = new Intent(SplashScreen.this, VendorInfoActivity.class);
                        startActivity(intent1);
                    }
                    else
                        Log.d(TAG, "Ravi: "+"NewCustomerInfoActivity");
                        startActivity(new Intent(SplashScreen.this, NewCustomerInfoActivity.class));
                    finish();
                }, 2000);
            }
        }
    };

    private LocalBroadcastManager localBroadcastManager;


    @Override
    protected void onPause() {
        localBroadcastManager.unregisterReceiver(servicesStartedBroadcast);
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        handler = new Handler(Looper.getMainLooper());
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        progressMessage = findViewById(R.id.progress_msg);
        progressMessage.setText("");

        handler.postDelayed(()->{
//                    startActivity(new Intent(SplashScreen.this, NewCustomerInfoActivity.class));
            Realm realm = RealmManager.getLocalInstance();
            Vendor vendor = realm.where(Vendor.class).isNotNull("name").isNotNull("tel_no").findFirst();
            if(vendor==null) {
                Log.d(TAG, "Ravi: "+"VendorInfoActivity");
                Intent intent1 = new Intent(SplashScreen.this, VendorInfoActivity.class);
                startActivity(intent1);
            }
            else
                Log.d(TAG, "Ravi: "+"NewCustomerInfoActivity");
            startActivity(new Intent(SplashScreen.this, NewCustomerInfoActivity.class));
            finish();
        }, 2000);

//        localBroadcastManager.registerReceiver(servicesStartedBroadcast, new IntentFilter(INTENT_SERVICE_STARTED));
//        try {
//            startService(new Intent(this, CallerIDService.class));
//            startService(new Intent(this, SmsService.class));
//            startService(new Intent(this, PrintService.class));
////            startService(new Intent(this,FirebaseBackgroundService.class));
//        } catch (Exception e) {
//            e.printStackTrace();
//            FirebaseCrashlytics.getInstance().log(e.getStackTrace().toString());
//        }
    }
}
