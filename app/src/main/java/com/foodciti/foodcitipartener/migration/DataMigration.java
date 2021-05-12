package com.foodciti.foodcitipartener.migration;

import android.util.Log;

import com.foodciti.foodcitipartener.gson.PostalData;
import com.foodciti.foodcitipartener.realm_entities.Driver;
import com.foodciti.foodcitipartener.realm_entities.Order;
import com.foodciti.foodcitipartener.realm_entities.OrderCustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.PurchaseEntry;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmList;
import io.realm.RealmMigration;

public class DataMigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        MigrationConfig.Builder builder = new MigrationConfig.Builder();

        // Migrate to version 1: Add a new class.
        // Example:
        // public Person extends RealmObject {
        //     private String name;
        //     private int age;
        //     // getters and setters left out for brevity
        // }
        if (oldVersion == 1) {
            final Type REALMLIST_TYPE = new TypeToken<RealmList<PurchaseEntry>>() {
            }.getType();
            Log.d("DataMigration", "----------------starting migration from version "+oldVersion+" to version "+(oldVersion+1));

            /*createOrderPostalInfo(schema);
            createOrderCustomerInfoSchema(schema);
            createOrderMenuItemSchema(schema);
            createOrderAddonSchema(schema);
            createOrderMenuCategorySchema(schema);
            createPurchaseEntrySchema(schema);
            createPurchaseSchema(schema);*/

            builder.addMigrationHelper(new MigrateFromVersion1());

            oldVersion++;
        }

        if(oldVersion == 2) {
            Log.d("DataMigration", "----------------starting migration from version "+oldVersion+" to version "+(oldVersion+1));
            builder.addMigrationHelper(new MigrateFromVersion2());
            oldVersion++;
        }

        if(oldVersion == 3) {
            Log.d("DataMigration", "----------------starting migration from version "+oldVersion+" to version "+(oldVersion+1));
            builder.addMigrationHelper(new MigrateFromVersion3());
            oldVersion++;
        }

        if(oldVersion == 4) {
            Log.d("DataMigration", "----------------starting migration from version "+oldVersion+" to version "+(oldVersion+1));
            builder.addMigrationHelper(new MigrateFromVersion4());
            oldVersion++;
        }

        if(oldVersion == 5) {
            Log.d("DataMigration", "----------------starting migration from version "+oldVersion+" to version "+(oldVersion+1));
            builder.addMigrationHelper(new MigrateFromVersion5());
            oldVersion++;
        }

        if(oldVersion == 6) {
            Log.d("DataMigration", "----------------starting migration from version "+oldVersion+" to version "+(oldVersion+1));
            builder.addMigrationHelper(new MigrateFromVersion6());
            oldVersion++;
        }
        if(oldVersion == 7) {
            Log.d("DataMigration", "----------------starting migration from version "+oldVersion+" to version "+(oldVersion+1));
            builder.addMigrationHelper(new MigrateFromVersion7());
            oldVersion++;
        }
        if(oldVersion == 8) {
            Log.d("DataMigration", "----------------starting migration from version "+oldVersion+" to version "+(oldVersion+1));
            builder.addMigrationHelper(new MigrateFromVersion8());
            oldVersion++;
        }
        //        Changes by Rashmi
        if (oldVersion == 9) {
            Log.d("DataMigration", "----------------starting migration from version " + oldVersion + " to version " + (oldVersion + 1));
            builder.addMigrationHelper(new MigrateFromVersion9());
            oldVersion++;
        }
        //        Changes by Ravi
        if (oldVersion == 10) {
            Log.d("DataMigration", "----------------starting migration from version " + oldVersion + " to version " + (oldVersion + 1));
            builder.addMigrationHelper(new MigrateFromVersion10());
            oldVersion++;
        }
//        //        Changes by Ravi
        if (oldVersion == 11) {
            Log.d("DataMigration", "----------------starting migration from version " + oldVersion + " to version " + (oldVersion + 1));
            builder.addMigrationHelper(new MigrateFromVersion11());
            oldVersion++;
        }
        if (oldVersion == 12) {
            Log.d("DataMigration", "----------------starting migration from version " + oldVersion + " to version " + (oldVersion + 1));
            builder.addMigrationHelper(new MigrateFromVersion12());
            oldVersion++;
        }
        if (oldVersion == 13) {
            Log.d("DataMigration", "----------------starting migration from version " + oldVersion + " to version " + (oldVersion + 1));
            builder.addMigrationHelper(new MigrateFromVersion13());
            oldVersion++;
        }

        MigrationConfig migrationConfig = builder.build();
        migrationConfig.doMigration(realm);

    }
}
