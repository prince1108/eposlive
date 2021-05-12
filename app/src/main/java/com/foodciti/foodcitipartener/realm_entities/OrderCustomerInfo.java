package com.foodciti.foodcitipartener.realm_entities;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class OrderCustomerInfo extends RealmObject {
    @PrimaryKey
    private long id;
    public OrderPostalInfo orderPostalInfo;
    private String name = "";
    private String phone = "";
    private String house_no = "";
    private String remarks = "";
    private String remarkStatus = "";

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public OrderPostalInfo getOrderPostalInfo() {
        return orderPostalInfo;
    }

    public void setOrderPostalInfo(OrderPostalInfo orderPostalInfo) {
        this.orderPostalInfo = orderPostalInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHouse_no() {
        return house_no;
    }

    public void setHouse_no(String house_no) {
        this.house_no = house_no;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getRemarkStatus() {
        return remarkStatus;
    }

    public void setRemarkStatus(String remarkStatus) {
        this.remarkStatus = remarkStatus;
    }
}
