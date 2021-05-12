package com.foodciti.foodcitipartener.migration;

import android.util.Log;

import com.foodciti.foodcitipartener.realm_entities.MenuItem;

import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmList;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class MigrateFromVersion9 implements MigrationHelper {
    private final String TAG = "MigrateFromVersion9";
    @Override
    public void migrate(DynamicRealm dynamicRealm) throws Exception {
        modifyCartItemSchema(dynamicRealm.getSchema());
//        modifyVendorSchema(dynamicRealm.getSchema());
    }

    private void modifyCartItemSchema(RealmSchema schema) throws Exception {
        Log.d(TAG, "------------modifyVendorSchema--------------------");
        try {
            RealmObjectSchema objectSchema = schema.get("CartItem");
            if (objectSchema.hasField("hasAddonOfOtherCatg"))
                return;
            Log.d(TAG, "------------adding field 'hasAddonOfOtherCatg'");
            objectSchema.addField("hasAddonOfOtherCatg", Boolean.class);
        } finally {

        }
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
