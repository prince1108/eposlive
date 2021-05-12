package com.foodciti.foodcitipartener.utils;

/**
 * Created by quflip1 on 11-03-2017.
 */

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by ADMIN on 27-04-2016.
 */
public class SessionManager {
    // LogCat tag
    public static final String USER_ISLOGIN = "islogin";
    public static final String USER_ISADMIN= "isAdmin";
    private static final String PREF_NAME = "com.foodciti";
    private static final String FOODTRUCK_ID = "foodtruck_id";
    private static final String USER_ID = "user_id";
    private static final String USER_NAME = "user_name";
    private static final String USER_TOKEN = "user_token";
    private static final String RESTAURANT_NAME = "restaurant_name";
    private static final String RESTAURANT_PHONENUMBER = "restaurant_phonenumber";
    private static final String RESTAURANT_LOCATION = "restaurant_location";
    private static final String RESTAURANT_POSTAL_CODE = "restaurant_postal_code";
    private static final String RESTAURANT_STATUS = "status";
    private static String TAG = SessionManager.class.getSimpleName();
    private static SessionManager SINGLETON;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    public static SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public static SharedPreferences getDefaultPref(Context context) {
        if (pref == null) {
            pref = context.getSharedPreferences(
                    PREF_NAME, Context.MODE_PRIVATE);
        }

        return pref;
    }

    public static void clearpreferences(Context context) {
        SharedPreferences.Editor editor = getDefaultPref(context).edit();
        editor.clear();
        editor.commit();
    }

    public static SessionManager get(Context context) {
        if (SINGLETON == null) {
            SINGLETON = new SessionManager(context);
        }
        return SINGLETON;
    }

    public void setFoodTruckId(String foodTruckId) {
        editor.putString(FOODTRUCK_ID, foodTruckId);
        editor.commit();
    }

    public void setUserId(String userId) {
        editor.putString(USER_ID, userId);
        editor.commit();
    }

    public void setUserName(String userName) {
        editor.putString(USER_NAME, userName);
        editor.commit();
    }

    public void setRestaurantStatus(String status) {
        editor.putString(RESTAURANT_STATUS, status);
        editor.commit();
    }
    public String getRestaurantStatus() {
        return pref.getString(RESTAURANT_STATUS, "1");
    }

    public void setRestaurantName(String restaurantName) {
        editor.putString(RESTAURANT_NAME, restaurantName);
        editor.commit();
    }

    public void setRestaurantPhonenumber(String restaurantPhonenumber) {
        editor.putString(RESTAURANT_PHONENUMBER, restaurantPhonenumber);
        editor.commit();
    }


    public void setRestaurantLocation(String restaurantLocation) {
        editor.putString(RESTAURANT_LOCATION, restaurantLocation);
        editor.commit();
    }

    public void setRestaurantPostalCode(String restaurantPostalCode) {
        editor.putString(RESTAURANT_POSTAL_CODE, restaurantPostalCode);
        editor.commit();
    }

    public void setUserToken(String userToken) {
        editor.putString(USER_TOKEN, userToken);
        editor.commit();
    }

    public String getUserToken() {
        return pref.getString(USER_TOKEN, "");
    }

    public String getUserName() {
        return pref.getString(USER_NAME, "");
    }

    public String getRestaurantName() {
        return pref.getString(RESTAURANT_NAME, "");
    }
    public String getRestaurantLocation() {
        return pref.getString(RESTAURANT_LOCATION, "");
    }
    public String getRestaurantPostalCode() {
        return pref.getString(RESTAURANT_POSTAL_CODE, "");
    }
    public String getRestaurantPhonenumber() {
        return pref.getString(RESTAURANT_PHONENUMBER, "");
    }

    public void clearUserId() {
        editor.putString(USER_ID, "");
        editor.commit();
    }

    public void clearFoodTruck() {
        editor.putString(FOODTRUCK_ID, "");
        editor.commit();
    }

    public String getUserId() {
        return pref.getString(USER_ID, "");
    }

    public String getFoodTruckId() {
        return pref.getString(FOODTRUCK_ID, "");
    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(USER_ISLOGIN, isLoggedIn);
        // commit changes
        editor.commit();

    }

    public void setAdmin(boolean isAdmin) {

        editor.putBoolean(USER_ISADMIN, isAdmin);
        // commit changes
        editor.commit();

    }
    public boolean isAdmin(){
        return pref.getBoolean(USER_ISADMIN, false);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(USER_ISLOGIN, false);
    }
}

