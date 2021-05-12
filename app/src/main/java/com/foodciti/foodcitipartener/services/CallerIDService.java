package com.foodciti.foodcitipartener.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foodciti.foodcitipartener.activities.SplashScreen;
import com.foodciti.foodcitipartener.utils.SerialPortManager;
import com.foodciti.foodcitipartener.utils.SessionManager;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.HashMap;


public class CallerIDService extends Service {
    private static final String TAG = "CallerIDService";
    public static final String INTENT_RESET_BUFFER = "reset_buffer";
    public static final String INTENT_CALLER_ID = "caller_id";
    public static final String RECEIVED_BUFFER = "buffer";
    public static final String RECEIVED_BUFFER_SIZE = "size";
    public static final String INTENT_RECEIVED_PHONE_NO = "intent_telephone";
    public static final String RECEIVED_PHONE_NO = "telephone";
    private SerialPortManager manager = null;
    private InputStream mInputStream1 = null;
    private ReadThread mReadThread = null;
    private volatile boolean isStop, callerIdCaptured=false;
    private LocalBroadcastManager localBroadcastManager;

    private int bufferIndexFSK = 0;
    private byte[] receivedBuffer = new byte[64];
    private byte[] cmd = new byte[]{0x1D, 0x49, 0x43};

    private String lastTelephone;

    public static CallerIDService callerIDService;

    public CallerIDService() {
    }

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public CallerIDService getService() {
            return CallerIDService.this;
        }
    }

    public boolean isCallerIdCaptured() {
        return callerIdCaptured;
    }

    public void setCallerIdCaptured(boolean callerIdCaptured) {
        this.callerIdCaptured = callerIdCaptured;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    @Override
    public void onCreate() {
        //Fabric.with(this, new Crashlytics());
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        super.onCreate();
        callerIDService = this;

        Intent intent = new Intent(SplashScreen.INTENT_SERVICE_STARTED);
        intent.putExtra(SplashScreen.ARG_SERVICE_NAME, "CallerID Service");
        localBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        stopread();
        destroyPhonePortListeningVariables();
        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "----------Service destroyed----------------------");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // todo uncomment following if commented
        setPhonePortListnerVariables();
        startread();
        return START_NOT_STICKY;
    }

    private void setPhonePortListnerVariables() {
        // initializing serial port manager to listen telephone number from port
        try {
            if (manager == null) {
                manager = new SerialPortManager();
                if (manager.openSerialPort("/dev/ttyS3", 9600, false)) {
                    Log.d(TAG, "---------successfully opened SerialPort--------------");
                } else {
                    FirebaseCrashlytics.getInstance().log("Failed to open serial port");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().log(e.getStackTrace().toString());
        }
    }

    private void destroyPhonePortListeningVariables() {
        try {
            if (manager != null)
                manager.closeSerialPort();
            if (mInputStream1 != null) {
                mInputStream1.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().log(e.getStackTrace().toString());
        }

    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            Looper.prepare();
            while (!isStop && !isInterrupted()) {
                Log.i(TAG, "read....");
                int size;
                byte[] buffer = new byte[64];
                if (mInputStream1 == null) {
                    Log.i(TAG, "inputStream is null.");
                    FirebaseCrashlytics.getInstance().log("inputStream is null in readthread");
                    return;
                }
                try {
                    size = mInputStream1.read(buffer);
                    Log.i(TAG, "--read: " + size + " bytes..." + buffer);
                    onDataReceivedCallerId(buffer, size);
                } catch (final Exception e) {
                    e.printStackTrace();
                    FirebaseCrashlytics.getInstance().log(e.getStackTrace().toString());
                    Toast.makeText(CallerIDService.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } finally {

                }
            }
            Log.i(TAG, "read thread end.");
        }

        public void cancel() {
            interrupt();
        }
    }

    private void onDataReceivedCallerId(final byte[] buffer, final int size) throws Exception {
        Log.i(TAG, "onDataReceived ---> " + size + "///" + bytes2HexString(buffer, size));
//        Crashlytics.log(Log.ERROR, "onDataReceived", size + "///" + bytes2HexString(buffer, size));
        /*Intent intent=new Intent(INTENT_CALLER_ID);
        intent.putExtra(RECEIVED_BUFFER, buffer);
        intent.putExtra(RECEIVED_BUFFER_SIZE, size);
        localBroadcastManager.sendBroadcast(intent);*/
        processBuffer(buffer, size);
    }

    private static String bytes2HexString(byte[] b, int size) {
        String ret = "";
        for (int i = 0; i < size; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = "0x0" + hex.toUpperCase();
            } else {
                hex = "0x" + hex.toUpperCase();
            }

            if (hex.equals("0x00")) {
                ret += hex + '\n';
            } else {
                ret += hex + ',';
            }

        }
        return ret;
    }

    public void startread() {
        try {

            if (mInputStream1 == null) {
                mInputStream1 = manager.getInputStream();
                if (mInputStream1 == null) {
//                    Toast.makeText(this, "mInputSteam1 is null", Toast.LENGTH_SHORT).show();
                    FirebaseCrashlytics.getInstance().log("mInputSteam1 is null");
                }

            }

            isStop = false;
//		foundFSK = false;
       /* for (int i = 0; i < 64; i++) {
            receivedBuffer[i] = 0;
        }*/
//       localBroadcastManager.sendBroadcast(new Intent(INTENT_RESET_BUFFER));
            resetBuffer();

            if (mReadThread == null) {
                mReadThread = new ReadThread();
            }
            mReadThread.start();

            Log.i(TAG, "start read");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().log(e.getStackTrace().toString());
//                Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    public void stopread() {
        isStop = true;
        if (mReadThread != null) {
            mReadThread.cancel();
            mReadThread = null;
        }
        Log.i(TAG, "stop read");
    }


 /*   private void processBuffer(byte[] buffer, int size) {
        System.out.println("bufferIndexFSK = " + bufferIndexFSK);
        if (bufferIndexFSK > 0) {
            for (int i = 0; i < size; i++) {
                int index = bufferIndexFSK++;
                if (index >= 64) break;
                receivedBuffer[index] = buffer[i];
            }
        } else {
            int i;
            for (i = 0; i < size; i++) {
                if ((buffer[i] == 0x04) || //start byte for China
                        (buffer[i] == (byte) 0x80) || //start byte for UK
                        (buffer[i] == 0x43)) {  //start byte for Caller ID
                    receivedBuffer[0] = buffer[i];//
                    bufferIndexFSK = 1;
                    i++;
                    break;
                }
            }

            if (bufferIndexFSK > 0) {
                for (; i < size; i++) {
                    receivedBuffer[bufferIndexFSK++] = buffer[i];
                }
            }
        }

        if (bufferIndexFSK > 1) {
            if (bufferIndexFSK >= receivedBuffer[1] + 2) {//received
                try {
                    refreshTel(false);
                } catch (Exception e) {
                    e.printStackTrace();
//                    Crashlytics.logException(e);
                }
                bufferIndexFSK = 0;
            } else if (bufferIndexFSK >= 11) {//Caller ID board
                if (receivedBuffer[0] == 0x43 &&
                        receivedBuffer[1] == 0x54 &&
                        receivedBuffer[2] == 0x45) {
                    try {
                        refreshTel(true);
                    } catch (Exception e) {
                        e.printStackTrace();
//                        Crashlytics.logException(e);
                    }
                    bufferIndexFSK = 0;
                }
            }
        }
        System.out.println("onDataReceived ---> " + bytes2HexString(buffer, size));
    }*/

    private void refreshTel(boolean isCallerID) throws Exception {
        int byteIndex = 0;
        int byteCout;

        //show Caller ID board
        if (isCallerID) {
            Time t = new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
            t.setToNow(); // 取得系统时间。
//		    String time = (t.month+1) + "-" + t.monthDay+" "+t.hour+":"+t.minute;  //+t.second;
		   /* String time = String.format("%02d", t.month+1) + "-" + String.format("%02d", t.monthDay)+ " " +
		    		 String.format("%02d", t.hour) + ":" + String.format("%02d", t.minute);*/
            String time = String.format("%02d", t.hour) + ":" + String.format("%02d", t.minute) + ":" + String.format("%02d", t.second);


            StringBuilder callerID = new StringBuilder();
            for (int i = 0; i < receivedBuffer.length; i++) {
                char c = (char) receivedBuffer[byteIndex + i];
                callerID.append(c);
            }
            for (int i = 0; i < 64; i++) {
                receivedBuffer[i] = 0;
            }
            return;
        }

        //show tel
        if (receivedBuffer[0] == 0x04) {//start byte for China
            byteIndex = 2;    //The 3rd byte is start byte of the Calling time
            byteCout = 8;    //Total bytes of the Calling time
        } else if (receivedBuffer[0] == (byte) 0x80) {//start byte for UK
            byteIndex = 4;                    //The 5th byte is start byte of the Calling time

            while (receivedBuffer[byteIndex] < 0x30) byteIndex++;

            byteCout = receivedBuffer[byteIndex - 1];    //Total bytes of the Calling time
        } else {
//               info.append("Error:  receivedBuffer[0] = " + Integer.toHexString(receivedBuffer[0] & 0xFF) +"\n");
//            Crashlytics.log("Error:  receivedBuffer[0] = " + Integer.toHexString(receivedBuffer[0] & 0xFF));
           /* receivedBuffer=null;
            receivedBuffer=new byte[64];
            resetBuffer();*/

           Log.e(TAG, "Error:  receivedBuffer[0] = " + Integer.toHexString(receivedBuffer[0] & 0xFF) +"\n");
            return;
        }

        String in_time = ""
                + (char) receivedBuffer[byteIndex] + (char) receivedBuffer[byteIndex + 1] + "-"
                + (char) receivedBuffer[byteIndex + 2] + (char) receivedBuffer[byteIndex + 3] + " "
                + (char) receivedBuffer[byteIndex + 4] + (char) receivedBuffer[byteIndex + 5] + ":"
                + (char) receivedBuffer[byteIndex + 6] + (char) receivedBuffer[byteIndex + 7];

        //int k = receivedBuffer[1];

        if (receivedBuffer[0] == 0x04) {//start byte for China
            byteIndex += byteCout;    //start byte of the Calling number
            byteCout = receivedBuffer[1] - 8;    //Total bytes of the Calling number
        } else if (receivedBuffer[0] == (byte) 0x80) {//start byte for UK
            byteIndex += byteCout + 2;    //start byte of the Calling number
            byteCout = receivedBuffer[byteIndex - 1];    //Total bytes of the Calling number
        } else {

            //  info.append("Error2:  receivedBuffer[0] = " + Integer.toHexString(receivedBuffer[0] & 0xFF) +"\n");
//            Crashlytics.log("Error2:  receivedBuffer[0] = " + Integer.toHexString(receivedBuffer[0] & 0xFF));

         /*   receivedBuffer=null;
            receivedBuffer=new byte[64];
            resetBuffer();*/

            Log.e(TAG, "Error2:  receivedBuffer[0] = " + Integer.toHexString(receivedBuffer[0] & 0xFF) +"\n");
            return;
        }

        final StringBuilder in_tel = new StringBuilder();
        for (int i = 0; i < byteCout; i++) {
            char c = (char) receivedBuffer[byteIndex + i];
            in_tel.append(c);
        }
        Log.d(TAG, "-------------------------------------------------------------------");
        Log.d(TAG,"time=" + in_time);
        Log.d(TAG, "tel=" + in_tel);

        resetBuffer();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CallerIDService.this, "New Telephone: " + in_tel, Toast.LENGTH_SHORT).show();
            }
        });

        lastTelephone = in_tel.toString();
        callerIdCaptured = false;
        Intent intent = new Intent(INTENT_CALLER_ID);
        intent.putExtra(RECEIVED_PHONE_NO, in_tel.toString());
        localBroadcastManager.sendBroadcast(intent);
        /*stopread();
        startread();*/
    }

    boolean isAdd = false;
    boolean isBroadID = false;
    private int receiveIndex  = 0;

    private void processBuffer(byte[] buffer, int size) throws Exception {
        for(int i = 0; i < size; i++){
            if(buffer[i] == 0x04 || buffer[i] == (byte)0x80 || buffer[i] == 0x43) { //0x43 板子id
                receivedBuffer[0] = buffer[i];//
                receiveIndex = 0;
                isAdd = true;
                isBroadID = false;
            }else {
                if(receiveIndex >= receivedBuffer.length) {
                    receiveIndex = 0;
                    isAdd = false;
                    isBroadID = false;
                }
                if (isAdd ) {
                    receiveIndex++;
                    receivedBuffer[receiveIndex] = buffer[i];//

                    if(receiveIndex==1) { //判断数据长度
                        if(receivedBuffer[0] ==  (byte)0x80) {
                            if(receivedBuffer[1] < (byte)0x0D || receivedBuffer[1] > (byte)0x20) {
                                receiveIndex = 0;
                                isAdd = false;
                            }
                        }

                    }

                    if(receiveIndex==2) { //判断是否是板子id
                        if(receivedBuffer[0] ==  (byte)0x43) {
                            if(receivedBuffer[1] == 0x54 && receivedBuffer[2] == 0x45 ){
                                isBroadID = true;  //isisCallerID
                            }else{

                            }
                        }

                    }
                }
                //

            }

            if(isAdd && isBroadID) {
                refreshTel(true);
                receiveIndex = 0;
                isAdd = false;
                isBroadID = false;
            }

            if(isAdd && receiveIndex >= receivedBuffer[1] + 2){//received
                refreshTel(false);
                receiveIndex = 0;
                isAdd = false;
            }
        }
    }

    private void resetBuffer() {
        for (int i = 0; i < 64; i++) {
            receivedBuffer[i] = 0;
        }
    }

    public String getLastTelephone() {
        return lastTelephone;
    }

    public void setLastTelephone(String lastTelephone) {
        this.lastTelephone = lastTelephone;
    }
}
