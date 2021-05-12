package com.foodciti.foodcitipartener.utils;

import android.app.Activity;
import android.widget.Toast;

import com.foodciti.foodcitipartener.gson.Flavour;
import com.foodciti.foodcitipartener.gson.Item;
import com.foodciti.foodcitipartener.gson.PostalData;
import com.foodciti.foodcitipartener.gson.RemarkTypeJson;
import com.foodciti.foodcitipartener.gson.UserData;
import com.foodciti.foodcitipartener.realm_entities.Addon;
import com.foodciti.foodcitipartener.realm_entities.CustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.MenuCategory;
import com.foodciti.foodcitipartener.realm_entities.MenuItem;
import com.foodciti.foodcitipartener.realm_entities.PostalInfo;
import com.foodciti.foodcitipartener.realm_entities.RemarkType;

import java.util.List;

import io.realm.Realm;


public class DataInitializers {

    public static void initPostalInfo(Realm realm, final List<PostalData> postalInfos) {
        for (PostalData p : postalInfos) {
            Number maxId = realm.where(PostalInfo.class).max("id");
            // If there are no rows, currentId is null, so the next id must be 1
            // If currentId is not null, increment it by 1
            long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
            // User object created with the new Primary key
            PostalInfo postalInfo = realm.createObject(PostalInfo.class, nextId);
            postalInfo.setA_PostCode(p.getPostcode());
            postalInfo.setAddress(p.getAddress());
            postalInfo.setCity(p.getCity());
            postalInfo.setHouseno(p.getHouseno());
        }
    }

    public static void initUserInfo(Realm realm, final List<UserData> userInfos) {
        for (UserData u : userInfos) {
            Number maxId = realm.where(CustomerInfo.class).max("id");
            // If there are no rows, currentId is null, so the next id must be 1
            // If currentId is not null, increment it by 1
            long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
            // User object created with the new Primary key
            CustomerInfo customerInfo = realm.createObject(CustomerInfo.class, nextId);
            customerInfo.setName(u.getName());
            customerInfo.setPhone(u.getPhone());
            customerInfo.setPostalInfo(realm.where(PostalInfo.class).equalTo("A_PostCode", u.getPostcode()).findFirst());
        }
    }

    public static void initRemarkTypes(Realm realm, final List<RemarkTypeJson> remarkTypeJsons, List<Integer> colors) {
        for (RemarkTypeJson remarkTypeJson : remarkTypeJsons) {
            Number maxId = realm.where(RemarkType.class).max("id");
            // If there are no rows, currentId is null, so the next id must be 1
            // If currentId is not null, increment it by 1
            long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
            RemarkType remarkType = realm.createObject(RemarkType.class, nextId);
            remarkType.setType(remarkTypeJson.getType());
            remarkType.setColor(colors.get((int) nextId - 1));
        }
    }

    public static void initProductCategory(Activity activity, final List<MenuCategory> productCategories) {
        Realm realm = RealmManager.getLocalInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (MenuCategory c : productCategories) {
//                 realm.copyToRealm(p);
                    Number maxId = realm.where(MenuCategory.class).max("id");
                    // If there are no rows, currentId is null, so the next id must be 1
                    // If currentId is not null, increment it by 1
                    long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
                    // User object created with the new Primary key
                    MenuCategory menuCategory = realm.createObject(MenuCategory.class, nextId);
                    menuCategory.copy(c);
                }
            }
        });
    }

    public static void initMenuItems(Activity activity, final List<Item> menuSubcategories) {
        Realm realm = RealmManager.getLocalInstance();
        for (Item i : menuSubcategories) {
            realm.executeTransaction(r -> {
                try {
                    MenuCategory category = realm.where(MenuCategory.class).equalTo("id", i.getId()).findFirst();
                    if (category != null) {

                        Number maxId = realm.where(MenuItem.class).max("id");
                        // If there are no rows, currentId is null, so the next id must be 1
                        // If currentId is not null, increment it by 1
                        long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
                        // User object created with the new Primary key

                        MenuItem menuItem = realm.createObject(MenuItem.class, nextId);
                        menuItem.menuCategory = category;
                        category.menuItems.add(menuItem);
                        menuItem.itemPosition = category.menuItems.size() - 1;
                        menuItem.name = i.getName();
                        menuItem.collectionPrice = i.getPrice();
                        menuItem.deliveryPrice = i.getPrice();
                        menuItem.type = "specific";
                        for (Flavour flavour : i.getFlavours()) {

                            Number maxId_ = realm.where(MenuItem.class).max("id");
                            // If there are no rows, currentId is null, so the next id must be 1
                            // If currentId is not null, increment it by 1
                            long nextId_ = (maxId_ == null) ? 1 : maxId_.longValue() + 1;

                            MenuItem menuItemFlavour = realm.createObject(MenuItem.class, nextId_);
                            menuItemFlavour.menuCategory = category;
                            menuItemFlavour.name = flavour.getItemFlavourName();
                            menuItemFlavour.price = Double.parseDouble(flavour.getItemFlavourPrice());
                            menuItemFlavour.type = "category";
                            menuItem.flavours.add(menuItemFlavour);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void initAddons(Activity activity, final List<Addon> addons) {
        Realm realm = RealmManager.getLocalInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (Addon a : addons) {
                    Number maxId = realm.where(Addon.class).max("id");
                    // If there are no rows, currentId is null, so the next id must be 1
                    // If currentId is not null, increment it by 1
                    long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
                    // User object created with the new Primary key

                    Addon addon = realm.createObject(Addon.class, nextId);
                    addon.copy(a);
                }
            }
        });
    }

    public static void initNoAddons(Activity activity, final List<Addon> noAddons) {
        Realm realm = RealmManager.getLocalInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (Addon a : noAddons) {
                    Number maxId = realm.where(Addon.class).max("id");
                    // If there are no rows, currentId is null, so the next id must be 1
                    // If currentId is not null, increment it by 1
                    long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
                    // User object created with the new Primary key

                    Addon addon = realm.createObject(Addon.class, nextId);
                    a.price = -1;
                    addon.copy(a);
                }
            }
        });
    }

    public static void initCommonItems(Activity activity, final List<Item> items) {
        Realm realm = RealmManager.getLocalInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (Item item : items) {
                    Number maxId = realm.where(MenuItem.class).max("id");
                    long nextId = (maxId == null) ? 0 : maxId.longValue() + 1;
                    MenuItem menuItem = realm.createObject(MenuItem.class, nextId);
                    menuItem.name = item.getName();
                    menuItem.itemPosition = (int) ((nextId % items.size()) - 1);
                    menuItem.collectionPrice = item.getPrice();
                    menuItem.deliveryPrice = item.getPrice();
                    menuItem.type = "common";
                }
            }
        });
    }
}
