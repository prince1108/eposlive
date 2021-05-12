package com.foodciti.foodcitipartener.migration;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import io.realm.DynamicRealm;
import io.realm.RealmSchema;

public class MigrationConfig {
    private final String TAG = "MigrationConfig";
    private static final List<MigrationHelper> migrationHelpers=new LinkedList<>();
    private MigrationConfig() {}

    public void doMigration(DynamicRealm dynamicRealm) {
        try {
            for (MigrationHelper migrationHelper: migrationHelpers) {
                migrationHelper.migrate(dynamicRealm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        migrationHelpers.clear();
    }

    public static class Builder {
        public Builder addMigrationHelper(MigrationHelper migrationHelper) {
            migrationHelpers.add(migrationHelper);
            return this;
        }

        public MigrationConfig build() {
            return new MigrationConfig();
        }
    }
}
