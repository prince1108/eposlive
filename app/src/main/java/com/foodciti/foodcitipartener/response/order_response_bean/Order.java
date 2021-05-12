
package com.foodciti.foodcitipartener.response.order_response_bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Order implements Parcelable {

    @SerializedName("_id")
    @Expose
    private String orderId;
    @SerializedName("order_status")
    @Expose
    private Integer orderStatus;
    @SerializedName("restaurent_id")
    @Expose
    private String foodTruckId;
    @SerializedName("customer_id")
    @Expose
    private User user;
    @SerializedName("user_type")
    @Expose
    private Boolean userType;
    @SerializedName("order_time")
    @Expose
    private String orderTime;
    @SerializedName("order_total")
    @Expose
    private Integer orderTotal;
    @SerializedName("order_special_instruction")
    @Expose
    private String orderSpecialInstruction;
    @SerializedName("payment_id")
    @Expose
    private String paymentId;
    @SerializedName("order_coupon_code")
    @Expose
    private String orderCouponCode;
    @SerializedName("order_sub_total")
    @Expose
    private Integer orderSubTotal;
    @SerializedName("discount")
    @Expose
    private Integer discount;
    @SerializedName("tax")
    @Expose
    private Integer tax;
    @SerializedName("delivery_charges")
    @Expose
    private Integer deliveryCharges;
    @SerializedName("order_tat")
    @Expose
    private Object orderTat;
    @SerializedName("order_paid")
    @Expose
    private String orderPaid;
    @SerializedName("order_delivery")
    @Expose
    private String orderDelivery;
    @SerializedName("order_rating")
    @Expose
    private Integer orderRating;
    @SerializedName("items")
    @Expose
    private List<OrderedItem> orderedItemList = null;

    protected Order(Parcel in) {
        orderId = in.readString();
        if (in.readByte() == 0) {
            orderStatus = null;
        } else {
            orderStatus = in.readInt();
        }
        foodTruckId = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        byte tmpUserType = in.readByte();
        userType = tmpUserType == 0 ? null : tmpUserType == 1;
        orderTime = in.readString();
        if (in.readByte() == 0) {
            orderTotal = null;
        } else {
            orderTotal = in.readInt();
        }
        orderSpecialInstruction = in.readString();
        paymentId = in.readString();
        orderCouponCode = in.readString();
        if (in.readByte() == 0) {
            orderSubTotal = null;
        } else {
            orderSubTotal = in.readInt();
        }
        if (in.readByte() == 0) {
            discount = null;
        } else {
            discount = in.readInt();
        }
        if (in.readByte() == 0) {
            tax = null;
        } else {
            tax = in.readInt();
        }
        if (in.readByte() == 0) {
            deliveryCharges = null;
        } else {
            deliveryCharges = in.readInt();
        }
        orderPaid = in.readString();
        orderDelivery = in.readString();
        if (in.readByte() == 0) {
            orderRating = null;
        } else {
            orderRating = in.readInt();
        }
        orderedItemList = in.createTypedArrayList(OrderedItem.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderId);
        if (orderStatus == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(orderStatus);
        }
        dest.writeString(foodTruckId);
        dest.writeParcelable(user, flags);
        dest.writeByte((byte) (userType == null ? 0 : userType ? 1 : 2));
        dest.writeString(orderTime);
        if (orderTotal == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(orderTotal);
        }
        dest.writeString(orderSpecialInstruction);
        dest.writeString(paymentId);
        dest.writeString(orderCouponCode);
        if (orderSubTotal == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(orderSubTotal);
        }
        if (discount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(discount);
        }
        if (tax == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(tax);
        }
        if (deliveryCharges == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(deliveryCharges);
        }
        dest.writeString(orderPaid);
        dest.writeString(orderDelivery);
        if (orderRating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(orderRating);
        }
        dest.writeTypedList(orderedItemList);
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

    public void setOrderId(String id) {
        this.orderId = id;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getFoodTruckId() {
        return foodTruckId;
    }

    public void setFoodTruckId(String restaurentId) {
        this.foodTruckId = restaurentId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User customerId) {
        this.user = customerId;
    }

    public Boolean getUserType() {
        return userType;
    }

    public void setUserType(Boolean userType) {
        this.userType = userType;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public Integer getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(Integer orderTotal) {
        this.orderTotal = orderTotal;
    }

    public String getOrderSpecialInstruction() {
        return orderSpecialInstruction;
    }

    public void setOrderSpecialInstruction(String orderSpecialInstruction) {
        this.orderSpecialInstruction = orderSpecialInstruction;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getOrderCouponCode() {
        return orderCouponCode;
    }

    public void setOrderCouponCode(String orderCouponCode) {
        this.orderCouponCode = orderCouponCode;
    }

    public Integer getOrderSubTotal() {
        return orderSubTotal;
    }

    public void setOrderSubTotal(Integer orderSubTotal) {
        this.orderSubTotal = orderSubTotal;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Integer getTax() {
        return tax;
    }

    public void setTax(Integer tax) {
        this.tax = tax;
    }

    public Integer getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(Integer deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public Object getOrderTat() {
        return orderTat;
    }

    public void setOrderTat(Object orderTat) {
        this.orderTat = orderTat;
    }

    public String getOrderPaid() {
        return orderPaid;
    }

    public void setOrderPaid(String orderPaid) {
        this.orderPaid = orderPaid;
    }

    public String getOrderDelivery() {
        return orderDelivery;
    }

    public void setOrderDelivery(String orderDelivery) {
        this.orderDelivery = orderDelivery;
    }

    public Integer getOrderRating() {
        return orderRating;
    }

    public void setOrderRating(Integer orderRating) {
        this.orderRating = orderRating;
    }

    public List<OrderedItem> getOrderedItemList() {
        return orderedItemList;
    }

    public void setOrderedItemList(List<OrderedItem> orderedItemList) {
        this.orderedItemList = orderedItemList;
    }

}
