package com.foodciti.foodcitipartener.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by quflip1 on 17-03-2017.
 */


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order implements Parcelable {
    @JsonProperty("_id")
    private String orderId;
    @JsonProperty("order_status")
    private int orderStatus;
    @JsonProperty("restaurent_id")
    private String foodTruckId;
    @JsonProperty("customer_id")
    private User user;
    @JsonProperty("user_type")
    private boolean userType;
    @JsonProperty("order_time")
    private String orderTime;
    @JsonProperty("order_issue_comments")
    private String orderIssueComments;
    @JsonProperty("order_location")
    private String orderLocation;
    @JsonProperty("payment_id")
    private String paymentId;
    @JsonProperty("order_rating")
    private double orderRating;
    @JsonProperty("items")
    private List<OrderedItem> orderedItemList;
    @JsonProperty("order_special_instruction")
    private String orderSpecialInstruction;
    @JsonProperty("order_total")
    private double orderTotal;
    @JsonProperty("order_delivery")
    private String orderDelivery;
    @JsonProperty("order_paid")
    private String orderPaid;
    @JsonProperty("order_sub_total")
    private String orderSubtotal;
    @JsonProperty("discount")
    private String discount;
    @JsonProperty("tax")
    private String tax;
    @JsonProperty("delivery_charges")
    private String deliveryCharges;
    @JsonProperty("forwarded_restaurent_id")
    private String forwardedRestaurantId;
    @JsonProperty("isforwarded")
    private boolean isForwarded;
    @JsonProperty("orderno")
    private int orderNo;
    @JsonProperty("freeDrinkText")
    private String freenDrinkText;

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public String getFreenDrinkText() {
        return freenDrinkText;
    }

    public void setFreenDrinkText(String freenDrinkText) {
        this.freenDrinkText = freenDrinkText;
    }

    public Order() {
    }

    protected Order(Parcel in) {
        orderId = in.readString();
        orderStatus = in.readInt();
        foodTruckId = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        userType = in.readByte() != 0;
        orderTime = in.readString();
        orderIssueComments = in.readString();
        orderLocation = in.readString();
        paymentId = in.readString();
        orderRating = in.readDouble();
        orderedItemList = in.createTypedArrayList(OrderedItem.CREATOR);
        orderSpecialInstruction = in.readString();
        orderTotal = in.readDouble();
        orderDelivery = in.readString();
        orderPaid = in.readString();
        orderSubtotal = in.readString();
        discount = in.readString();
        tax = in.readString();
        deliveryCharges = in.readString();
        forwardedRestaurantId = in.readString();
        isForwarded = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderId);
        dest.writeInt(orderStatus);
        dest.writeString(foodTruckId);
        dest.writeParcelable(user, flags);
        dest.writeByte((byte) (userType ? 1 : 0));
        dest.writeString(orderTime);
        dest.writeString(orderIssueComments);
        dest.writeString(orderLocation);
        dest.writeString(paymentId);
        dest.writeDouble(orderRating);
        dest.writeTypedList(orderedItemList);
        dest.writeString(orderSpecialInstruction);
        dest.writeDouble(orderTotal);
        dest.writeString(orderDelivery);
        dest.writeString(orderPaid);
        dest.writeString(orderSubtotal);
        dest.writeString(discount);
        dest.writeString(tax);
        dest.writeString(deliveryCharges);
        dest.writeString(forwardedRestaurantId);
        dest.writeByte((byte) (isForwarded ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getFoodTruckId() {
        return foodTruckId;
    }

    public void setFoodTruckId(String foodTruckId) {
        this.foodTruckId = foodTruckId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isUserType() {
        return userType;
    }

    public void setUserType(boolean userType) {
        this.userType = userType;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderIssueComments() {
        return orderIssueComments;
    }

    public void setOrderIssueComments(String orderIssueComments) {
        this.orderIssueComments = orderIssueComments;
    }

    public String getOrderLocation() {
        return orderLocation;
    }

    public void setOrderLocation(String orderLocation) {
        this.orderLocation = orderLocation;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public double getOrderRating() {
        return orderRating;
    }

    public void setOrderRating(double orderRating) {
        this.orderRating = orderRating;
    }

    public List<OrderedItem> getOrderedItemList() {
        return orderedItemList;
    }

    public void setOrderedItemList(List<OrderedItem> orderedItemList) {
        this.orderedItemList = orderedItemList;
    }

    public String getOrderSpecialInstruction() {
        return orderSpecialInstruction;
    }

    public void setOrderSpecialInstruction(String orderSpecialInstruction) {
        this.orderSpecialInstruction = orderSpecialInstruction;
    }

    public double getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(double orderTotal) {
        this.orderTotal = orderTotal;
    }

    public String getOrderDelivery() {
        return orderDelivery;
    }

    public void setOrderDelivery(String orderDelivery) {
        this.orderDelivery = orderDelivery;
    }

    public String getOrderPaid() {
        return orderPaid;
    }

    public void setOrderPaid(String orderPaid) {
        this.orderPaid = orderPaid;
    }

    public String getOrderSubtotal() {
        return orderSubtotal;
    }

    public void setOrderSubtotal(String orderSubtotal) {
        this.orderSubtotal = orderSubtotal;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(String deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public String getForwardedRestaurantId() {
        return forwardedRestaurantId;
    }

    public void setForwardedRestaurantId(String forwardedRestaurantId) {
        this.forwardedRestaurantId = forwardedRestaurantId;
    }

    public boolean isForwarded() {
        return isForwarded;
    }

    public void setForwarded(boolean forwarded) {
        isForwarded = forwarded;
    }
}
