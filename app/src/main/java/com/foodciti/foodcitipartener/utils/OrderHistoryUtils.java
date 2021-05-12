package com.foodciti.foodcitipartener.utils;

import com.foodciti.foodcitipartener.realm_entities.Addon;
import com.foodciti.foodcitipartener.realm_entities.CustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.MenuCategory;
import com.foodciti.foodcitipartener.realm_entities.MenuItem;
import com.foodciti.foodcitipartener.realm_entities.OrderAddon;
import com.foodciti.foodcitipartener.realm_entities.OrderCustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.OrderMenuCategory;
import com.foodciti.foodcitipartener.realm_entities.OrderMenuItem;
import com.foodciti.foodcitipartener.realm_entities.OrderPostalInfo;
import com.foodciti.foodcitipartener.realm_entities.PostalInfo;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class OrderHistoryUtils {
    private static Realm realm;

    public static void setRealm(Realm realm) {
        OrderHistoryUtils.realm = realm;
    }

    public static OrderCustomerInfo toOrderCustomerInfo(CustomerInfo customerInfo) {
        if(customerInfo==null)
            return null;
        if(realm==null || realm.isClosed())
         realm = RealmManager.getLocalInstance();
        Number maxId= realm.where(OrderCustomerInfo.class).max("id");
        long nextId = (maxId==null)? 1: maxId.longValue()+1;
        OrderCustomerInfo orderCustomerInfo = realm.createObject(OrderCustomerInfo.class, nextId);
        orderCustomerInfo.setName(customerInfo.getName());
        orderCustomerInfo.setHouse_no(customerInfo.getHouse_no());
        orderCustomerInfo.setPhone(customerInfo.getPhone());
        orderCustomerInfo.setOrderPostalInfo(toOrderPostalInfo(customerInfo.postalInfo));
        orderCustomerInfo.setRemarks(customerInfo.getRemarks());
        orderCustomerInfo.setRemarkStatus(customerInfo.getRemarkStatus());
        return orderCustomerInfo;
    }

    public static OrderPostalInfo toOrderPostalInfo(PostalInfo postalInfo) {
        if(postalInfo==null)
            return null;

        if(realm==null || realm.isClosed())
            realm = RealmManager.getLocalInstance();
        Number maxId= realm.where(OrderPostalInfo.class).max("id");
        long nextId = (maxId==null)? 1: maxId.longValue()+1;
        OrderPostalInfo orderPostalInfo = realm.createObject(OrderPostalInfo.class, nextId);
        orderPostalInfo.setA_PostCode(postalInfo.getA_PostCode());
        orderPostalInfo.setAddress(postalInfo.getAddress());
        return orderPostalInfo;
    }

    public static List<OrderAddon> toOrderAddons(List<Addon> addons) {
        if(realm==null || realm.isClosed())
            realm = RealmManager.getLocalInstance();
        List<OrderAddon> orderAddons = new ArrayList<>();
        for(Addon addon: addons) {
            Number maxOrderAddon = realm.where(OrderAddon.class).max("id");
            long nextOrderAddonId = (maxOrderAddon == null) ? 1 : maxOrderAddon.longValue() + 1;
            OrderAddon orderAddon = realm.createObject(OrderAddon.class, nextOrderAddonId);
            orderAddon.color = addon.color;
            orderAddon.isNoAddon = addon.isNoAddon;
            orderAddon.name = addon.name;
            orderAddon.price = addon.price;
            orderAddons.add(orderAddon);
        }
        return orderAddons;
    }

    public static OrderMenuItem toOrderMenuItem(MenuItem menuItem) {
        if(realm==null || realm.isClosed())
            realm = RealmManager.getLocalInstance();
        Number maxOrderMenuItem = realm.where(OrderMenuItem.class).max("id");
        long nextOrderMenuiItemId = (maxOrderMenuItem==null)? 1: maxOrderMenuItem.longValue()+1;
        OrderMenuItem orderMenuItem = realm.createObject(OrderMenuItem.class, nextOrderMenuiItemId);
        orderMenuItem.collectionPrice = menuItem.collectionPrice;
        orderMenuItem.deliveryPrice = menuItem.deliveryPrice;
        orderMenuItem.name = menuItem.name;
        orderMenuItem.price = menuItem.price;
        orderMenuItem.type = menuItem.type;
        OrderMenuCategory orderMenuCategory = toOrderMenuCategory(menuItem.menuCategory);
        if(orderMenuCategory!=null)
            orderMenuCategory.orderMenuItems.add(orderMenuItem);
        return orderMenuItem;
    }


    public static OrderMenuCategory toOrderMenuCategory(MenuCategory menuCategory) {
        if(menuCategory==null)
            return null;
        if(realm==null || realm.isClosed())
            realm = RealmManager.getLocalInstance();
        Number maxOrderMenuCategory = realm.where(OrderMenuCategory.class).max("id");
        long nextId = (maxOrderMenuCategory==null)? 1: maxOrderMenuCategory.longValue()+1;
        OrderMenuCategory orderMenuCategory = realm.createObject(OrderMenuCategory.class, nextId);
        orderMenuCategory.setName(menuCategory.getName());
        orderMenuCategory.orderAddons.addAll(toOrderAddons(menuCategory.addons));
        orderMenuCategory.orderAddons.addAll(toOrderAddons(menuCategory.noAddons));
        return  orderMenuCategory;
    }
}
