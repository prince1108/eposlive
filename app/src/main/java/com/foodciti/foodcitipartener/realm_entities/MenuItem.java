package com.foodciti.foodcitipartener.realm_entities;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class MenuItem extends RealmObject implements Serializable {
    @PrimaryKey
    public long id;
    public MenuItem parent;
    public String type = "";
    @Ignore
    public boolean isSelected;
    public int itemPosition;
    public String name = "";
    public double collectionPrice, deliveryPrice, price;
    public int color = -1;
    public int printerSetting = -1;
    public int closeCounter = -1;
    public boolean asAddon;
    public RealmList<MenuItem> flavours;

    public MenuCategory menuCategory;
    public long last_updated = -1l;

    @Override
    public String
    toString() {
        return "MenuItem{" +
                "id=" + id +
                ", parent=" + parent +
                ", type='" + type + '\'' +
                ", isSelected=" + isSelected +
                ", itemPosition=" + itemPosition +
                ", name='" + name + '\'' +
                ", collectionPrice=" + collectionPrice +
                ", deliveryPrice=" + deliveryPrice +
                ", price=" + price +
                ", color=" + color +
                ", printerSetting=" + printerSetting +
                ", closeCounter=" + closeCounter +
                ", asAddon=" + asAddon +
                ", flavours=" + flavours +
                ", menuCategory=" + menuCategory +
                '}';
    }
}
