package com.foodciti.foodcitipartener.gson;

import java.io.Serializable;

public class PostcodePattern implements Serializable {
    private String startString;
    private String city;
    private boolean isSelected;

    public String getStartString() {
        return startString;
    }

    public void setStartString(String startString) {
        this.startString = startString;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "PostcodePattern{" +
                "startString='" + startString + '\'' +
                ", city='" + city + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }
}
