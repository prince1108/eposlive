package com.foodciti.foodcitipartener.migration;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class MigrateFromVersion12 implements MigrationHelper {
    private final String TAG = "MigrateFromVersion11";
    @Override
    public void migrate(DynamicRealm dynamicRealm) throws Exception{
        modifyMenuItemSchema(dynamicRealm.getSchema());
    }

    private void modifyMenuItemSchema(RealmSchema schema) throws Exception {
        Log.d(TAG, "------------modifyMenuItemSchema--------------------");
        try {
            RealmObjectSchema objectSchema = schema.get("MenuItem");
            if(objectSchema.hasField("printerSetting"))
                return;
            objectSchema.addField("printerSetting", Integer.class);

            if(objectSchema.hasField("closeCounter"))
                return;
            objectSchema.addField("closeCounter", Integer.class);

            if(objectSchema.hasField("asAddon"))
                return;
            objectSchema.addField("asAddon", Boolean.class);
        } finally {

        }
    }
}
