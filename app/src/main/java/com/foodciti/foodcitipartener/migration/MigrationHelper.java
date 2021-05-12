package com.foodciti.foodcitipartener.migration;

import io.realm.DynamicRealm;

public interface MigrationHelper {
    public void migrate(DynamicRealm dynamicRealm) throws Exception;
}
