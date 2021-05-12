package com.foodciti.foodcitipartener.models;

public class RemarksStatusBean {
    private String remarksType;
    private int remarksColor;

    public RemarksStatusBean() {

    }

    public RemarksStatusBean(String remarksType, int remarksColor) {
        this.remarksType = remarksType;
        this.remarksColor = remarksColor;
    }

    public String getRemarksType() {
        return remarksType;
    }

    public void setRemarksType(String remarksType) {
        this.remarksType = remarksType;
    }

    public int getRemarksColor() {
        return remarksColor;
    }

    public void setRemarksColor(int remarksColor) {
        this.remarksColor = remarksColor;
    }

}