package com.foodciti.foodcitipartener.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foodciti.foodcitipartener.utils.SessionManager;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class FirebaseBackgroundService extends Service {
    private String TAG = "FirebaseBackgroundService";
    DatabaseReference mFirebaseDatabase;
    Set<String> dataQueue = new HashSet<>();
    private void firebaseListener() {

        Log.d(TAG, "Start Service: new ");
        //isNetworkAvailable();
        //get reference to the orders node
//        Log.e("Restroid", "" + SessionManager.get(this).getFoodTruckId());
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
//                String val = dataSnapshot.getValue(String.class);
                String key = dataSnapshot.getKey();
                String status = dataSnapshot.child("order_status").getValue(String.class);
                Log.e("Ravi-ORDER_KEY", "" + key + ", ---" + status);
                if (status != null && status.equalsIgnoreCase("0")) {
                    if(!dataQueue.contains(key)) {
                        dataQueue.add(key);
                        Log.e("Ravi-ORDER_KEY", "" + key + ", " + status);
                        sendDataKey(key);
                    }
                }
//                Log.e("Ravi-ORDER_KEY", "" + key + ", " + status);
//                for(DataSnapshot ds : dataSnapshot.getChildren()) {
//                    String status = ds.child("order_status").getValue(String.class);
//                    Log.d("Ravi-ORDER_KEY", status);
//                }
//                for (DataSnapshot child : dataSnapshot.getChildren()) {
//                    final String orderkey = child.getKey();
//                    String order_status = "";
//                    try {
//                        HashMap<String, String> data = (HashMap<String, String>) child.getValue();
//                        order_status = data.get("order_status");
//                        Log.e("Ravi-ORDER_KEY", "" + orderkey + ", " + order_status);
//                        if (dataQueue.contains(orderkey)) {
//                            continue;
//                        } else {
//                            dataQueue.add(orderkey);
////                            sendDataKey(orderkey);
//                            if (order_status.contains("0")) {
//                                sendDataKey(orderkey);
//                            }
//                        }
//
//                    } catch (Exception e) {
//                        Log.e("FDB_EXP", "" + e.toString());
//                    }
//                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.e("Restroid", "" + SessionManager.get(this).getFoodTruckId());
//                for (DataSnapshot child : dataSnapshot.getChildren()) {
//                    final String orderkey = child.getKey();
//                    String order_status = "";
//                    try {
//                        HashMap<String, String> data = (HashMap<String, String>) child.getValue();
//                        order_status = data.get("order_status");
//                        Log.e("Ravi-ORDER_KEY", "" + orderkey + ", " + order_status);
//                        if(dataQueue.contains(orderkey)){
//                            continue;
//                        }else{
//                            dataQueue.add(orderkey);
////                            sendDataKey(orderkey);
//                            if (order_status.contains("0")) {
//                                sendDataKey(orderkey);
//                            }
//                        }
//
//                    } catch (Exception e) {
//                        Log.e("FDB_EXP", "" + e.toString());
//                    }
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d(TAG, "onCancelled: new ");
//            }
//        };
        if(mFirebaseDatabase==null){
            mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
            mFirebaseDatabase.child(SessionManager.get(this).getFoodTruckId()).addChildEventListener(childEventListener);
        }else {
            mFirebaseDatabase.child(SessionManager.get(this).getFoodTruckId()).addChildEventListener(childEventListener);
        }
    }

    private void sendDataKey(String key) {
        Intent intent = new Intent("fireDB");
        intent.putExtra("dataKey", key);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Timer().scheduleAtFixedRate(new TimerTask() {

            private Handler handler = new Handler() {
                @Override
                public void dispatchMessage(Message msg) {
                    firebaseListener();
//                    Toast.makeText(getApplicationContext(),""+(counter+=1),Toast.LENGTH_SHORT).show();
                }
            };

            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }, 5 * 1000, 60 * 1000);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
