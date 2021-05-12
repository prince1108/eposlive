package com.foodciti.foodcitipartener.realm_entities;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class CustomerInfo extends RealmObject {
    @PrimaryKey
    private long id;
    public PostalInfo postalInfo;
    private String name = "";
    private String phone = "";
    private String house_no = "";
    private String remarks = "";
    private String remarkStatus = "";
    private boolean isMatched;
    private long user_visited_date_time = -1l, info_last_updated = -1l;
    @Ignore
    private String msgDeliveryStatus;
    @Ignore
    private boolean isSelected;

    public CustomerInfo() {
        name = "";
        phone = "";
        house_no = "";
        remarks = "";
        remarkStatus = "";
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public long getInfo_last_updated() {
        return info_last_updated;
    }

    public void setInfo_last_updated(long info_last_updated) {
        this.info_last_updated = info_last_updated;
    }

    public long getUser_visited_date_time() {
        return user_visited_date_time;
    }

    public void setUser_visited_date_time(long user_visited_date_time) {
        this.user_visited_date_time = user_visited_date_time;
    }

    public String getHouse_no() {
        return house_no;
    }

    public void setHouse_no(String house_no) {
        this.house_no = house_no;
    }

    public String getMsgDeliveryStatus() {
        return msgDeliveryStatus;
    }

    public void setMsgDeliveryStatus(String msgDeliveryStatus) {
        this.msgDeliveryStatus = msgDeliveryStatus;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public PostalInfo getPostalInfo() {
        return postalInfo;
    }

    public void setPostalInfo(PostalInfo postalInfo) {
        this.postalInfo = postalInfo;
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
       /* StringBuilder ph=new StringBuilder(phone);
        if(!phone.startsWith("0"))
            ph.insert(0,"0");
        this.phone = ph.toString();*/
        this.phone = phone;
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

    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }

    @Override
    public String toString() {
        return "CustomerInfoActivity{" +
                "id=" + id +
                ", postalInfo=" + postalInfo +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", remarks='" + remarks + '\'' +
                ", remarkStatus='" + remarkStatus + '\'' +
                ", isMatched=" + isMatched +
                '}';
    }
}
