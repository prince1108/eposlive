package com.foodciti.foodcitipartener.realm_entities;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class OrderPostalInfo extends RealmObject {
    @PrimaryKey
    private long id;
    private String address = "", A_PostCode = "";

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getA_PostCode() {
        return A_PostCode;
    }

    public void setA_PostCode(String a_PostCode) {
        A_PostCode = a_PostCode;
    }
}
