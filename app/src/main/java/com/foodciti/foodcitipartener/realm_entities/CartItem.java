package com.foodciti.foodcitipartener.realm_entities;

import android.os.Parcelable;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.Ignore;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;

/*public class CartItem implements Serializable {
    public boolean selected;
    public MenuItem menuItem;
    public int count;
    public int categoryIndex = -1;
    public int menuItemIndex = -1;
    public String name;
    public double price;
    public List<Addon> addons = new ArrayList<>();

    @Override
    public String toString() {
        return "CartItem{" +
                "selected=" + selected +
                ", menuItem=" + menuItem +
                ", count=" + count +
                ", categoryIndex=" + categoryIndex +
                ", menuItemIndex=" + menuItemIndex +
                ", addons=" + addons +
                '}';
    }
}*/


public class CartItem extends RealmObject {
    @PrimaryKey
    public long id;
    @Ignore
    public boolean selected;
    public MenuItem menuItem;
    public int count;
    public int categoryIndex = -1;
    public int menuItemIndex = -1;
    public String name = "", comment = "";
    public double price;
    public RealmList<Addon> addons;
    public boolean isRestored = false;
    public boolean isFree = false;
    public Boolean hasAddonOfOtherCatg = false;

    @LinkingObjects("cartItems")
    public final RealmResults<Table> tables = null;


    @Override
    public String toString() {
        return "CartItem{" +
                "selected=" + selected +
                ", menuItem=" + menuItem +
                ", count=" + count +
                ", categoryIndex=" + categoryIndex +
                ", menuItemIndex=" + menuItemIndex +
                ", addons=" + addons +
                '}';
    }
}