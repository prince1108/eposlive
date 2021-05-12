package com.foodciti.foodcitipartener.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import com.foodciti.foodcitipartener.realm_entities.Exlogger;
import com.foodciti.foodcitipartener.utils.CheckConnection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by ravish on 23-06-2017.
 */

public class ExceptionLogger {
    private Context mContext;
    // Tags Table Columns names
    private static final String KEY_ERROR_ID = "error_id";
    private static final String KEY_ERROR_TYPE = "errorType";
    private static final String KEY_ERROR_MESSAGE = "errorMessage";
    private static final String KEY_SCREEN_NAME = "screenName";
    //    private static final String KEY_DEVICE_ID = "deviceID";
    private static final String KEY_DEVICE_INFO = "deviceInfo";
    private static final String KEY_NETWORK = "networkStatus";
    //    private static final String KEY_LOCATION = "location";
    private static final String KEY_DATE = "dateTime";
    private static final String TABLE_EXCEPTION = "ExceptionLogs";
    SharedPreferences sharedPref;

    public ExceptionLogger(Context context) {
        this.mContext = context;
        sharedPref = mContext.getSharedPreferences("MyPref", 0);
    }

    private boolean existsColumnInTable(SQLiteDatabase inDatabase, String Table, String columnToCheck) {
        try {
            //query 1 row
            Cursor mCursor = inDatabase.rawQuery("SELECT * FROM " + Table + " LIMIT 0", null);

            //getColumnIndex gives us the index (0 to ...) of the column - otherwise we get a -1
            if (mCursor.getColumnIndex(columnToCheck) != -1)
                return true;
            else
                return false;

        } catch (Exception Exp) {
            //something went wrong. Missing the database? The table?
            Log.e("existsColumnInTable", "When checking whether a column exists in the table, an error occurred: " + Exp.getMessage());
            return false;
        }
    }


    public void addException(Exlogger logger) {
        String networkStatus = "false";
        String osversion = android.os.Build.VERSION.SDK;
        String device = Build.MANUFACTURER;
        String model = android.os.Build.MODEL;
        String deviceDetails = "OS=" + osversion + "," + "Device=" + device + "," + "Model=" + model;
        if (CheckConnection.isNetworkAvailable(mContext)) {
            networkStatus = "true";
        } else {
            networkStatus = "false";
        }

        SQLiteDatabase sqldb = new MyDatabase(mContext).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ERROR_TYPE, logger.getErrorType());
        values.put(KEY_ERROR_MESSAGE, logger.getErrorMessage());
        values.put(KEY_SCREEN_NAME, logger.getScreenName());
        values.put(KEY_DEVICE_INFO, deviceDetails);
        values.put(KEY_NETWORK, networkStatus);
        values.put(KEY_DATE, getCurrentTimeUsingCalendar());
        try {
            // Inserting Row
            long l = sqldb.insert(TABLE_EXCEPTION, null, values);
            System.out.print("====exception added" + l);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Closing database connection
        sqldb.close();
    }

    // Getting All Exceptions
    public List<Exlogger> getAllExceptions() {
        List<Exlogger> list = new ArrayList<Exlogger>();
        String selectQuery = "SELECT * FROM " + TABLE_EXCEPTION + " ORDER BY " + KEY_ERROR_ID + " DESC";
        SQLiteDatabase sqldb = new MyDatabase(mContext).getWritableDatabase();
        Cursor cursor = sqldb.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Exlogger data = new Exlogger();
                data.setErrorType(cursor.getString(1));
                data.setErrorMessage(cursor.getString(2));
                data.setScreenName(cursor.getString(3));
//                data.setDeviceID(cursor.getString(4));
                data.setDeviceInfo(cursor.getString(4));
                data.setNetworkStatus(cursor.getString(5));
                data.setDateTime(cursor.getString(6));
                // Adding contact to list
                list.add(data);
            }

            while (cursor.moveToNext());
        }
        cursor.close();
        sqldb.close();

        // return contact list
        return list;
    }

    private String getCurrentTimeUsingCalendar() {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm aa");
        String formattedDate = dateFormat.format(date);
        return formattedDate;
    }

    public int getCount() {
        String selectQuery = "SELECT count(*) FROM " + TABLE_EXCEPTION;
        SQLiteDatabase sqldb = new MyDatabase(mContext).getWritableDatabase();
        Cursor cursor = sqldb.rawQuery(selectQuery, null);
        int cc = 0;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                cc = cursor.getInt(0);
            }

            while (cursor.moveToNext());
        }
        cursor.close();
        sqldb.close();

        // return contact list
        return cc;
    }

    public void deleteAllEntries() {
        try {
            SQLiteDatabase sqldb = new MyDatabase(mContext).getWritableDatabase();
            sqldb.execSQL("DELETE FROM " + TABLE_EXCEPTION);
            sqldb.close();
        } catch (Exception e) {
            Log.e("Dsds", e.toString());
        }
    }
}
