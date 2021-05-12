package com.foodciti.foodcitipartener.migration;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class MigrateFromVersion13 implements MigrationHelper {
    private final String TAG = "MigrateFromVersion13";
    @Override
    public void migrate(DynamicRealm dynamicRealm) throws Exception{
        modifyMenuItemSchema(dynamicRealm.getSchema());
    }

    private void modifyMenuItemSchema(RealmSchema schema) throws Exception {
        Log.d(TAG, "------------modifyMenuItemSchema--------------------");
        try {
            RealmObjectSchema objectSchema = schema.get("Addon");
            if(objectSchema.hasField("parent"))
                return;
            objectSchema.addField("parent", String.class);

            if(objectSchema.hasField("flavours"))
                return;
            objectSchema.addField("flavours", String.class);

        } finally {

        }
    }
}
