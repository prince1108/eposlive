package com.foodciti.foodcitipartener.realm_entities;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PostalInfo extends RealmObject implements Serializable {
    @PrimaryKey
    private long id;
    private long info_last_updated = -1l;
    private String address = "", A_PostCode = "";
    private String city="";

    public String getHouseno() {
        return houseno;
    }

    public void setHouseno(String houseno) {
        this.houseno = houseno;
    }

    private String houseno="";

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void copy(PostalInfo postalInfo) {
        if (postalInfo.getA_PostCode() != null) {
            address = postalInfo.getAddress();
            A_PostCode = postalInfo.getA_PostCode().replaceAll("\\s+", "");
        }
    }

    public long getInfo_last_updated() {
        return info_last_updated;
    }

    public void setInfo_last_updated(long info_last_updated) {
        this.info_last_updated = info_last_updated;
    }

    public String getA_PostCode() {
        return A_PostCode;
    }

    public void setA_PostCode(String a_PostCode) {
        A_PostCode = a_PostCode;
    }

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

    @Override
    public String toString() {
        return A_PostCode;
    }
}
