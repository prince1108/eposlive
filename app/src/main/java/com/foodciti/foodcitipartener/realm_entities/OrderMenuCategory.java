package com.foodciti.foodcitipartener.realm_entities;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class OrderMenuCategory extends RealmObject {
    @PrimaryKey
    public long id;
    private String name = "";
    @Ignore
    private boolean isSelected;
    public int color = -1;
    private int itemposition = -1, printOrder = -1;

    public int getItemposition() {
        return itemposition;
    }

    public void setItemposition(int itemposition) {
        this.itemposition = itemposition;
    }

    public int getPrintOrder() {
        return printOrder;
    }

    public void setPrintOrder(int printOrder) {
        this.printOrder = printOrder;
    }

    public RealmList<OrderMenuItem> orderMenuItems;
    public RealmList<OrderAddon> orderAddons;
    public long last_updated;
//    public PrintOrderCategory printOrderCategory = null;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return name;
    }
}
