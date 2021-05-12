package com.foodciti.foodcitipartener.realm_entities;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Driver extends RealmObject {
    @PrimaryKey
    private long id;
    private String driver_name = "", driver_vehicle_no = "";
    private int color;
    private boolean isAvailable = true, enabled = true;
    private long registrationDate;

    public long getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(long registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getDriver_vehicle_no() {
        return driver_vehicle_no;
    }

    public void setDriver_vehicle_no(String driver_vehicle_no) {
        this.driver_vehicle_no = driver_vehicle_no;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
