package com.foodciti.foodcitipartener.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foodciti.foodcitipartener.activities.SplashScreen;
import com.foodciti.foodcitipartener.utils.Application;
import com.foodciti.foodcitipartener.utils.PrintUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;

import static com.foodciti.foodcitipartener.utils.AppConfig.MyPREFERENCES;

public class PrintService extends Service {
    private final String TAG = "PrintService";
    private Application mApplication;
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private SharedPreferences shared;

    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver printRequestBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] byteArrayExtra = intent.getByteArrayExtra(PrintUtils.PRINT_DATA);
            Log.d(TAG, "----------bill\n"+(new String(byteArrayExtra)));
            try {
                mOutputStream.write(byteArrayExtra);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[64];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private void onDataReceived(byte[] buffer, int size) {
        StringBuilder printReception = new StringBuilder();
        for (int i = 0; i < size; i++) {
            String s = Integer.toHexString((int) buffer[i]);//String.valueOf(((char)buffer[i]));
            printReception.append(s + ' ');
        }

        Log.e(TAG, "---------------------printReception: " + printReception);
    }

    private void setupPrinterSerialPort() {
        try {
            mSerialPort = mApplication.getPrintSerialPort();
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

            /* Create a receiving thread */
            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (SecurityException e) {
            Log.e("SECURITY_EXP", "" + e.toString());
        } catch (IOException e) {
            Log.e("IO_EXP", "" + e.toString());
        } catch (InvalidParameterException e) {
            Log.e("INV_PARA_EXP", "" + e.toString());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PrintUtils.ACTION_PRINT_REQUEST);
        localBroadcastManager.registerReceiver(printRequestBroadcastReceiver, intentFilter);
        shared = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        mApplication = (Application)getApplication();

        // todo uncomment the following if commented
        setupPrinterSerialPort();

        Intent intent = new Intent(SplashScreen.INTENT_SERVICE_STARTED);
        intent.putExtra(SplashScreen.ARG_SERVICE_NAME, "Print Service");
        localBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(printRequestBroadcastReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }
}
