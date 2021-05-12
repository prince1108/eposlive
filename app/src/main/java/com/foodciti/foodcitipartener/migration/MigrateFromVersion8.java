package com.foodciti.foodcitipartener.migration;

import android.util.Log;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class MigrateFromVersion8 implements MigrationHelper{
    private final String TAG = "MigrateFromVersion8";
    @Override
    public void migrate(DynamicRealm dynamicRealm) throws Exception{
        modifyVendorSchema(dynamicRealm.getSchema());
    }

    private void modifyVendorSchema(RealmSchema schema) throws Exception {
        Log.d(TAG, "------------modifyVendorSchema--------------------");
        try {
            RealmObjectSchema objectSchema = schema.get("Purchase");
            if(objectSchema.hasField("orderTimeStamp"))
                return;
            Log.d(TAG, "------------adding field 'orderTimeStamp'");
            objectSchema.addField("orderTimeStamp", Date.class);
        } finally {

        }
    }
}
