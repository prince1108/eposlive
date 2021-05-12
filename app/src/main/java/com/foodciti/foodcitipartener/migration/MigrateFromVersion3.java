package com.foodciti.foodcitipartener.migration;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class MigrateFromVersion3 implements MigrationHelper {
    private final String TAG = "MigrateFromVersion3";
    @Override
    public void migrate(DynamicRealm dynamicRealm) throws Exception{
        modifyVendorSchema(dynamicRealm.getSchema());
    }

    private void modifyVendorSchema(RealmSchema schema) throws Exception {
        Log.d(TAG, "------------modifyVendorSchema--------------------");
        try {
            RealmObjectSchema objectSchema = schema.get("Vendor");
            if(objectSchema.hasField("vatNo") || objectSchema.hasField("title"))
                return;
            Log.d(TAG, "------------adding field 'vatNo'");
            objectSchema.addField("title", String.class);
            objectSchema.addField("vatNo", String.class);
        } finally {

        }
    }
}
