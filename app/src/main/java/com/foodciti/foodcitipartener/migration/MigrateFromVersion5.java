package com.foodciti.foodcitipartener.migration;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class MigrateFromVersion5 implements MigrationHelper {
    private final String TAG = "MigrateFromVersion5";
    @Override
    public void migrate(DynamicRealm dynamicRealm) throws Exception{
        modifyVendorSchema(dynamicRealm.getSchema());
    }

    private void modifyVendorSchema(RealmSchema schema) throws Exception {
        Log.d(TAG, "------------modifyVendorSchema--------------------");
        try {
            RealmObjectSchema objectSchema = schema.get("Vendor");
            if(objectSchema.hasField("companyNo"))
                return;
            Log.d(TAG, "------------adding field 'companyNo'");
            objectSchema.addField("companyNo", String.class);
        } finally {

        }
    }
}
