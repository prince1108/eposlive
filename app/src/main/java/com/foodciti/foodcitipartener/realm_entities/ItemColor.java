package com.foodciti.foodcitipartener.realm_entities;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ItemColor extends RealmObject {
    @PrimaryKey
    private long id;
    private String hexCode = "";
    private String name = "";

    public long getId() {
        return id;
    }

    public String getHexCode() {
        return hexCode;
    }

    public String getName() {
        return name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setHexCode(String hexCode) {
        this.hexCode = hexCode;
    }

    public void setName(String name) {
        this.name = name;
    }
}
