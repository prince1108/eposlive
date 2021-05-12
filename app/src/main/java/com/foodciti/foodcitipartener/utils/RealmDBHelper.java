package com.foodciti.foodcitipartener.utils;

import android.content.Context;

import com.foodciti.foodcitipartener.realm_entities.MenuCategory;

import java.util.Date;

import io.realm.Realm;

public class RealmDBHelper {
    private Realm realm;
    private Context context;

    public RealmDBHelper(Context context, Realm realm) {
        this.context = context;
        this.realm = realm;
    }

    // should be executed within the boundaries of transaction
    public MenuCategory getCategoryCommon() {
        MenuCategory category = realm.where(MenuCategory.class).equalTo("name", Constants.MENUCATEGORY_COMMON).findFirst();
        if (category == null) {
            Number maxId = realm.where(MenuCategory.class).max("id");
            long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
            category = realm.createObject(MenuCategory.class, nextId);
            category.setName(Constants.MENUCATEGORY_COMMON);
            category.last_updated = new Date().getTime();
        }
        return category;
    }
}
