package com.foodciti.foodcitipartener.realm_entities;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class AddonItemCategory extends RealmObject {
    @PrimaryKey
    public long id;

    private int itemposition = -1;

    public int getItemposition() {
        return itemposition;
    }

    public void setItemposition(int itemposition) {
        this.itemposition = itemposition;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int status = 0;

    private String name = "";

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public long getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(long categoryid) {
        this.categoryid = categoryid;
    }

    private String price = "";

    public long categoryid;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void copy(AddonItemCategory addonCategory) {
        name = addonCategory.getName();
        price = addonCategory.getPrice();
    }


    @Override
    public String toString() {
        return name;
    }

}
