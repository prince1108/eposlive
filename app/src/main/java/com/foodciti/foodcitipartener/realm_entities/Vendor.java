package com.foodciti.foodcitipartener.realm_entities;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Vendor extends RealmObject {
    @PrimaryKey
    private long id;
    private String name="";
    private String title="";

    public String getRestroID() {
        return restroID;
    }

    public void setRestroID(String restroID) {
        this.restroID = restroID;
    }

    private String restroID="";
    private String tel_no="";
    private String pin="";
    private String address="";
    private String vatNo="";
    private String companyNo="";
    private String admin_password;
    private String optInHomePage;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTel_no() {
        return tel_no;
    }

    public void setTel_no(String tel_no) {
        this.tel_no = tel_no;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVatNo() {
        return vatNo;
    }

    public void setVatNo(String vatNo) {
        this.vatNo = vatNo;
    }

    public String getCompanyNo() {
        return companyNo;
    }

    public void setCompanyNo(String companyNo) {
        this.companyNo = companyNo;
    }

    public String getAdmin_password() {
        return admin_password;
    }

    public void setAdmin_password(String admin_password) {
        this.admin_password = admin_password;
    }

    public String getOptInHomePage() {
        return optInHomePage;
    }

    public void setOptInHomePage(String optInHomePage) {
        this.optInHomePage = optInHomePage;
    }

    @Override
    public String toString() {
        return "Vendor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", tel_no='" + tel_no + '\'' +
                ", pin='" + pin + '\'' +
                ", address='" + address + '\'' +
                ", vatNo='" + vatNo + '\'' +
                ", companyNo='" + companyNo + '\'' +
                ", admin_password='" + admin_password + '\'' +
                ", optInHomePage='" + optInHomePage + '\'' +
                ", restroID='" + restroID + '\'' +
                '}';
    }
}
