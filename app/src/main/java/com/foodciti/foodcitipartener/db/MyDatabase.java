package com.foodciti.foodcitipartener.db;

/**
 * Created by king on 2/7/2017.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDatabase extends SQLiteOpenHelper {
    private final String DATABASE_NAME = "eposonline";
    private static final int VERSON = 3;
    //Playlist table
    private static final String TABLE_EXCEPTION = "ExceptionLogs";
    // Tags Table Columns names
    private static final String KEY_ERROR_ID = "error_id";
    private static final String KEY_ERROR_TYPE = "errorType";
    private static final String KEY_ERROR_MESSAGE = "errorMessage";
    private static final String KEY_SCREEN_NAME = "screenName";
    private static final String KEY_DEVICE_INFO = "deviceInfo";
    private static final String KEY_NETWORK = "networkStatus";
//    private static final String KEY_LOCATION = "location";
    private static final String KEY_DATE = "dateTime";

    public MyDatabase(Context context) {
        super(context, "eposonline", null, VERSON);
    }

    @Override
    public void onCreate(SQLiteDatabase sqdb) {
        String CREATE_TABLE_EXCEPTION = "CREATE TABLE " + TABLE_EXCEPTION + "(" + KEY_ERROR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + KEY_ERROR_TYPE +
                " TEXT," + KEY_ERROR_MESSAGE + " TEXT," + KEY_SCREEN_NAME + " TEXT,"  +
                KEY_DEVICE_INFO + " TEXT," + KEY_NETWORK + " TEXT," +
                KEY_DATE + " TEXT " + ")";
        Log.i("table", CREATE_TABLE_EXCEPTION);
        sqdb.execSQL(CREATE_TABLE_EXCEPTION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqdb, int oldVerson, int newVerson) {
        sqdb.execSQL("drop table if exists " + TABLE_EXCEPTION);
        onCreate(sqdb);
    }
}

