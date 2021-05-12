package com.foodciti.foodcitipartener.utils;

import android.util.Log;

import java.util.HashSet;
import java.util.Set;

import io.realm.Realm;

public class RealmManager {
    private static final String TAG = "RealmManager";
    private static final ThreadLocal<Realm> realmThreadLocal = new ThreadLocal<>();
    private static final Set<ThreadLocal<Realm>> THREAD_LOCAL_SET = new HashSet<>();

    private RealmManager() {}
    private static Realm openLocalInstance() {
        if(realmThreadLocal.get() == null) {
            Realm realm = Realm.getDefaultInstance();
            realmThreadLocal.set(realm);
            THREAD_LOCAL_SET.add(realmThreadLocal);
        }
        return realmThreadLocal.get();
    }

    public static Realm getLocalInstance() {
        Realm realm = realmThreadLocal.get();
        if(realm == null) {
            Log.e(TAG,"-------------------No open Realms were found on this thread, opening new Realm------------------------");
            realm = openLocalInstance();
        }
        return realm;
    }

    public static void closeLocalInstance() {
        Realm realm = realmThreadLocal.get();
        if(realm == null) {
            Log.e(TAG,"------------------------Can't close a Realm that is not open---------------------------------");
            return;
        }
        realm.close();
        /*if(Realm.getLocalInstanceCount(Realm.getDefaultConfiguration()) <= 0) {
            realmThreadLocal.set(null);
        }*/
        realmThreadLocal.remove();
    }

    public static void getRealmRefCount() {
        Log.d(TAG, "-----------------ref count: "+Realm.getLocalInstanceCount(Realm.getDefaultConfiguration()));
    }
}
