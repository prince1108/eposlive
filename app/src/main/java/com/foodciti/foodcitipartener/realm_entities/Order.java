package com.foodciti.foodcitipartener.realm_entities;

import com.foodciti.foodcitipartener.utils.Constants;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Order extends RealmObject {
    @PrimaryKey
    private long id;
    private RealmList<OrderTuple> orderTuples;
    private CustomerInfo customerInfo;
    private double total, subTotal, discount, extra, deliveryCharges, serviceCharges;
    private long timestamp, deliveryTime;
    private String paymentMode = "", orderType = "";
    private Driver driver;
    private boolean isDelivered = true;
    private Table table;

    public double getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(double deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public double getServiceCharges() {
        return serviceCharges;
    }

    public void setServiceCharges(double serviceCharges) {
        this.serviceCharges = serviceCharges;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
        isDelivered = (orderType.equals(Constants.TYPE_DELIVERY)) ? false : true;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RealmList<OrderTuple> getOrderTuples() {
        return orderTuples;
    }

    public void setOrderTuples(RealmList<OrderTuple> orderTuples) {
        this.orderTuples = orderTuples;
    }

    public CustomerInfo getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(CustomerInfo customerInfo) {
        this.customerInfo = customerInfo;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getExtra() {
        return extra;
    }

    public void setExtra(double extra) {
        this.extra = extra;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(long deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderTuples=" + orderTuples +
                ", customerInfo=" + customerInfo +
                ", total=" + total +
                ", subTotal=" + subTotal +
                ", discount=" + discount +
                ", extra=" + extra +
                ", timestamp=" + timestamp +
                '}';
    }
}
