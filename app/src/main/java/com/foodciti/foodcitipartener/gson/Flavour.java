package com.foodciti.foodcitipartener.gson;

import java.io.Serializable;

public class Flavour implements Serializable {
    private String itemFlavourName;
    private String itemFlavourPrice;

    public String getItemFlavourName() {
        return itemFlavourName;
    }

    public void setItemFlavourName(String itemFlavourName) {
        this.itemFlavourName = itemFlavourName;
    }

    public String getItemFlavourPrice() {
        return itemFlavourPrice;
    }

    public void setItemFlavourPrice(String itemFlavourPrice) {
        this.itemFlavourPrice = itemFlavourPrice;
    }

    @Override
    public String toString() {
        return "Flavour{" +
                "itemFlavourName='" + itemFlavourName + '\'' +
                ", itemFlavourPrice='" + itemFlavourPrice + '\'' +
                '}';
    }
}
