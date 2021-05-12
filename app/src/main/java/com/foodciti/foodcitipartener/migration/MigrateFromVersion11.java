package com.foodciti.foodcitipartener.migration;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class MigrateFromVersion11 implements MigrationHelper {
    private final String TAG = "MigrateFromVersion10";
    @Override
    public void migrate(DynamicRealm dynamicRealm) throws Exception{
        modifyPostcodeSchema(dynamicRealm.getSchema());
    }

    private void modifyPostcodeSchema(RealmSchema schema) throws Exception {
        Log.d(TAG, "------------modifyPostcodeSchema--------------------");
        try {
            RealmObjectSchema objectSchema = schema.get("PostalInfo");
            if(objectSchema.hasField("houseno"))
                return;
            Log.d(TAG, "------------adding field 'vatNo'");
            objectSchema.addField("houseno", String.class);
        } finally {

        }
    }
}
