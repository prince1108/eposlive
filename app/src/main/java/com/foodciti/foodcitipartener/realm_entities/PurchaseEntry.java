package com.foodciti.foodcitipartener.realm_entities;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;

public class PurchaseEntry extends RealmObject {
    @PrimaryKey
    private long id;
    private int count;
    private long customerOrderId;

    @LinkingObjects("purchaseEntries")
    public final RealmResults<Purchase> purchases=null;

    private double price;
    private OrderMenuItem orderMenuItem;
    private RealmList<OrderAddon> orderAddons;
    private String additionalNote = "";

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getCustomerOrderId() {
        return customerOrderId;
    }

    public void setCustomerOrderId(long customerOrderId) {
        this.customerOrderId = customerOrderId;
    }


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public OrderMenuItem getOrderMenuItem() {
        return orderMenuItem;
    }

    public void setOrderMenuItem(OrderMenuItem orderMenuItem) {
        this.orderMenuItem = orderMenuItem;
    }

    public RealmList<OrderAddon> getOrderAddons() {
        return orderAddons;
    }

    public void setOrderAddons(RealmList<OrderAddon> orderAddons) {
        this.orderAddons = orderAddons;
    }

    /*public void setOrderAddon(List<Addon> addons) {
        for(Addon a: addons) {
            orderAddons.
        }
    }

    private OrderAddon toOrderAddon(Addon addon) {

    }*/

    public String getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(String additionalNote) {
        this.additionalNote = additionalNote;
    }
}
