package com.foodciti.foodcitipartener.realm_entities;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.Ignore;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;

public class OrderAddon extends RealmObject implements Serializable {
    @PrimaryKey
    public long id;
    public String name = "";
    public int color = -1, itemposition = -1;
    public double price;

    @LinkingObjects("orderAddons")
    public final RealmResults<OrderMenuCategory> orderMenuCategories=null;
    public boolean isNoAddon;
    public boolean selected;

    @Ignore
    public CartItem cartItem;
    public long last_updated = -1l;

    public void copy(Addon addon) {
        this.name = addon.name;
        this.price = addon.price;
    }

    @Override
    public String toString() {
        return "OrderAddon{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color=" + color +
                ", itemposition=" + itemposition +
                ", price=" + price +
                ", orderMenuCategories=" + orderMenuCategories +
                ", isNoAddon=" + isNoAddon +
                ", selected=" + selected +
                ", cartItem=" + cartItem +
                ", last_updated=" + last_updated +
                '}';
    }
}
