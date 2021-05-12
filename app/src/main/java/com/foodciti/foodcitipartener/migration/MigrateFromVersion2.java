package com.foodciti.foodcitipartener.migration;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class MigrateFromVersion2 implements MigrationHelper {
    private final String TAG = "MigrateFromVersion2";
    @Override
    public void migrate(DynamicRealm dynamicRealm) throws Exception{
        modifyPurchaseSchema(dynamicRealm.getSchema());
    }

    private void modifyPurchaseSchema(RealmSchema schema) throws Exception {
        Log.d(TAG, "------------modifyPurchaseSchema--------------------");
        try {
            RealmObjectSchema objectSchema = schema.get("Purchase");
            if(objectSchema.hasField("paid"))
                return;
            Log.d(TAG, "------------renaming field 'isDelivered' to 'paid'");
            objectSchema.renameField("isDelivered", "paid");

            if(schema.contains("Vendor"))
                return;
            schema.create("Vendor")
                    .addField("id", long.class, FieldAttribute.PRIMARY_KEY)
                    .addField("name", String.class)
                    .addField("tel_no", String.class)
                    .addField("pin", String.class)
                    .addField("address", String.class);
        } finally {

        }
    }
}
