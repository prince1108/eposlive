package com.foodciti.foodcitipartener.migration;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class MigrateFromVersion6 implements MigrationHelper{
    private final String TAG = "MigrateFromVersion6";
    @Override
    public void migrate(DynamicRealm dynamicRealm) throws Exception{
        modifyVendorSchema(dynamicRealm.getSchema());
    }

    private void modifyVendorSchema(RealmSchema schema) throws Exception {
        Log.d(TAG, "------------modifyVendorSchema--------------------");
        try {
            RealmObjectSchema objectSchema = schema.get("Vendor");
            if(objectSchema.hasField("admin_password"))
                return;
            Log.d(TAG, "------------adding field 'admin_password'");
            objectSchema.addField("admin_password", String.class);
        } finally {

        }
    }
}
