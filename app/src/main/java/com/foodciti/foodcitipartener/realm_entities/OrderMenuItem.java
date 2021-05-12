package com.foodciti.foodcitipartener.realm_entities;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.Ignore;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;

public class OrderMenuItem extends RealmObject implements Serializable {
    @PrimaryKey
    public long id;
//    public MenuItem parent;
    public String type = "";
    @Ignore
    public boolean isSelected;
    public int itemPosition;
    public String name = "";
    public double collectionPrice, deliveryPrice, price;
    public int color = -1;
//    public RealmList<MenuItem> flavours;

    @LinkingObjects("orderMenuItems")
    public final RealmResults<OrderMenuCategory> orderMenuCategory=null;
    public long last_updated = -1l;

    @Override
    public String
    toString() {
        return "MenuItem{" +
                "id=" + id +
//                ", parent=" + parent +
                ", type='" + type + '\'' +
                ", isSelected=" + isSelected +
                ", itemPosition=" + itemPosition +
                ", name='" + name + '\'' +
                ", collectionPrice=" + collectionPrice +
                ", deliveryPrice=" + deliveryPrice +
                ", price=" + price +
                ", color=" + color +
//                ", flavours=" + flavours +
                ", menuCategory=" + orderMenuCategory +
                '}';
    }
}

