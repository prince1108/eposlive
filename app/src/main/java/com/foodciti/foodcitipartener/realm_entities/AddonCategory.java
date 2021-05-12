package com.foodciti.foodcitipartener.realm_entities;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class AddonCategory extends RealmObject {
    @PrimaryKey
    public long id;

    private String name = "";
    private int itemposition = -1;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int status = 0;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isIscompalsory() {
        return iscompalsory;
    }

    public void setIscompalsory(boolean iscompalsory) {
        this.iscompalsory = iscompalsory;
    }

    private boolean iscompalsory;

    public int getItemposition() {
        return itemposition;
    }

    public void setItemposition(int itemposition) {
        this.itemposition = itemposition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void copy(AddonCategory addonCategory) {
        name = addonCategory.getName();
        iscompalsory = addonCategory.isIscompalsory();
    }


    @Override
    public String toString() {
        return name;
    }
}
