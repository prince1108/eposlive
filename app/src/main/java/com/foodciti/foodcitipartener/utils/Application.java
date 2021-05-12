package com.foodciti.foodcitipartener.utils;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.foodciti.foodcitipartener.migration.DataMigration;
import com.foodciti.foodcitipartener.services.CallerIDService;
import com.foodciti.foodcitipartener.services.FirebaseBackgroundService;
import com.foodciti.foodcitipartener.services.PrintService;
import com.foodciti.foodcitipartener.services.SmsService;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;
import android_serialport_api.SerialPortFinder;
import io.realm.Realm;
import io.realm.RealmConfiguration;


public class Application extends android.app.Application {
    private static final String TAG = "EPOS Application";
    public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
    private SerialPort mSerialPort = null;
    private boolean mIsBound;
    private static Application mInstance;
    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {
            /* Read serial port parameters */
            SharedPreferences sp = getSharedPreferences("android_serialport_api.sample_preferences", MODE_PRIVATE);
            String path = sp.getString("DEVICE", "");
            int baudrate = Integer.decode(sp.getString("BAUDRATE", "-1"));

            /* Check parameters */
            if ((path.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }

            /* Open the serial port */
            mSerialPort = new SerialPort(new File(path), baudrate, 0, true);
        }
        return mSerialPort;
    }

    public SerialPort getPrintSerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {
            /* Open the serial port */
            mSerialPort = new SerialPort(new File("/dev/ttyS1"), 115200, 0, true);
        }
        return mSerialPort;
    }

    public SerialPort getCtmDisplaySerialPort() throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {
            /* Open the serial port */
            mSerialPort = new SerialPort(new File("/dev/ttyS3"), 9600, 0, false);
        }
        return mSerialPort;
    }

    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }

    private CallerIDService mBoundService;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has
            // been established, giving us the service object we can use
            // to interact with the service.  Because we have bound to a
            // explicit service that we know is running in our own
            // process, we can cast its IBinder to a concrete class and
            // directly access it.
            mBoundService = ((CallerIDService.LocalBinder) service).getService();

            // Tell the user about this for our demo.
            Toast.makeText(Application.this,
                    "service connected",
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has
            // been unexpectedly disconnected -- that is, its process
            // crashed. Because it is running in our same process, we
            // should never see this happen.
            mBoundService = null;
            Toast.makeText(Application.this,
                    "service disconnected",
                    Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
       // Fabric.with(this, new Crashlytics());
        initRealm();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false);
        startService(new Intent(this, FirebaseBackgroundService.class));
       /* try {
            startService(new Intent(this, CallerIDService.class));
            startService(new Intent(this, SmsService.class));
            startService(new Intent(this, PrintService.class));
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }*/
//        bindService(new Intent(this, CallerIDService.class),mConnection, Context.BIND_AUTO_CREATE);
    }
    public static synchronized Application getInstance() {
        return mInstance;
    }

    public void initRealm() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("efeskebab.realm")
                .schemaVersion(10)   // Do not change this unless you know what you are doing
//                .deleteRealmIfMigrationNeeded()
                .migration(new DataMigration())
                .build();
        Realm.setDefaultConfiguration(config);
    }

    @Override
    public void onTerminate() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
        RealmManager.closeLocalInstance();
        Log.d(TAG, "----------------------------------terminating application------------------------------------------");
        super.onTerminate();
    }
}
