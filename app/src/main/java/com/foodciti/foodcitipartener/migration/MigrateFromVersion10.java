package com.foodciti.foodcitipartener.migration;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class MigrateFromVersion10 implements MigrationHelper{
    private final String TAG = "MigrateFromVersion7";
    @Override
    public void migrate(DynamicRealm dynamicRealm) throws Exception{
        modifyVendorSchema(dynamicRealm.getSchema());
    }

    private void modifyVendorSchema(RealmSchema schema) throws Exception {
        Log.d(TAG, "------------modifyVendorSchema--------------------");
        try {
            RealmObjectSchema objectSchema = schema.get("Vendor");
            if(objectSchema.hasField("restroID"))
                return;
            Log.d(TAG, "------------adding field 'restroID'");
            objectSchema.addField("restroID", String.class);
        } finally {

        }
    }
}
