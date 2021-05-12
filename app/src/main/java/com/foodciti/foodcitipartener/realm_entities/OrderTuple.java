package com.foodciti.foodcitipartener.realm_entities;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class OrderTuple extends RealmObject {
    @PrimaryKey
    private long id;
    private int count;
    private long customerOrderId;
    private double price;
    private MenuItem menuItem;
    private RealmList<Addon> addons;
    private String additionalNote = "";

    public String getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(String additionalNote) {
        this.additionalNote = additionalNote;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getCustomerOrderId() {
        return customerOrderId;
    }

    public void setCustomerOrderId(long customerOrderId) {
        this.customerOrderId = customerOrderId;
    }

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

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public RealmList<Addon> getAddons() {
        return addons;
    }

    public void setAddons(RealmList<Addon> addons) {
        this.addons = addons;
    }

    @Override
    public String toString() {
        return "OrderTuple{" +
                "id=" + id +
                ", count=" + count +
                ", menuItem=" + menuItem +
                ", addons=" + addons +
                '}';
    }
}
