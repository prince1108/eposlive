package com.foodciti.foodcitipartener.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by quflip1 on 17-03-2017.
 */

public class InternetConnection {

    /**
     * CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT
     */
    public static boolean checkConnection(Context context)
    {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }
}
