package com.foodciti.foodcitipartener.models;

import com.foodciti.foodcitipartener.realm_entities.CartItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SplitBill implements Serializable {
    private String name;
    private List<CartItem> cartItemList = new ArrayList<>();
    private double totalBill;
    private long timestamp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CartItem> getCartItemList() {
        return cartItemList;
    }

    public double getTotalBill() {
        return totalBill;
    }

    public void setTotalBill(double totalBill) {
        this.totalBill = totalBill;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
