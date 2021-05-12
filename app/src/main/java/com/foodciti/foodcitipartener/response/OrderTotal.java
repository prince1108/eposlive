package com.foodciti.foodcitipartener.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by quflip1 on 11-01-2018.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderTotal implements Parcelable {
    @JsonProperty("card")
    private OrderTotalCard orderTotalCard;
    @JsonProperty("cash")
    private OrderTotalCash orderTotalCash;
    @JsonProperty("pickUp")
    private OrderTotalPickUp orderTotalPickUp;
    @JsonProperty("delivery")
    private OrderTotalDelivery orderTotalDelivery;
    @JsonProperty("total")
    private OrderTotalOverAll orderTotalOverAll;

    public OrderTotalEODPojo getOrderTotalWorldPay() {
        return orderTotalWorldPay;
    }

    public void setOrderTotalWorldPay(OrderTotalEODPojo orderTotalWorldPay) {
        this.orderTotalWorldPay = orderTotalWorldPay;
    }

    @JsonProperty("worldpay")
    private OrderTotalEODPojo orderTotalWorldPay;
    public OrderTotalPaypal getOrderTotalPaypal() {
        return orderTotalPaypal;
    }

    public void setOrderTotalPaypal(OrderTotalPaypal orderTotalPaypal) {
        this.orderTotalPaypal = orderTotalPaypal;
    }

    @JsonProperty("paypal")
    private OrderTotalPaypal orderTotalPaypal;

    public OrderTotalCard getOrderTotalCard() {
        return orderTotalCard;
    }

    public void setOrderTotalCard(OrderTotalCard orderTotalCard) {
        this.orderTotalCard = orderTotalCard;
    }

    public OrderTotalCash getOrderTotalCash() {
        return orderTotalCash;
    }

    public void setOrderTotalCash(OrderTotalCash orderTotalCash) {
        this.orderTotalCash = orderTotalCash;
    }

    public OrderTotalPickUp getOrderTotalPickUp() {
        return orderTotalPickUp;
    }

    public void setOrderTotalPickUp(OrderTotalPickUp orderTotalPickUp) {
        this.orderTotalPickUp = orderTotalPickUp;
    }

    public OrderTotalDelivery getOrderTotalDelivery() {
        return orderTotalDelivery;
    }

    public void setOrderTotalDelivery(OrderTotalDelivery orderTotalDelivery) {
        this.orderTotalDelivery = orderTotalDelivery;
    }

    public OrderTotalOverAll getOrderTotalOverAll() {
        return orderTotalOverAll;
    }

    public void setOrderTotalOverAll(OrderTotalOverAll orderTotalOverAll) {
        this.orderTotalOverAll = orderTotalOverAll;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.orderTotalCard, flags);
        dest.writeParcelable(this.orderTotalCash, flags);
        dest.writeParcelable(this.orderTotalPickUp, flags);
        dest.writeParcelable(this.orderTotalDelivery, flags);
        dest.writeParcelable(this.orderTotalOverAll, flags);
    }

    public OrderTotal() {
    }

    protected OrderTotal(Parcel in) {
        this.orderTotalCard = in.readParcelable(OrderTotalCard.class.getClassLoader());
        this.orderTotalCash = in.readParcelable(OrderTotalCash.class.getClassLoader());
        this.orderTotalPickUp = in.readParcelable(OrderTotalPickUp.class.getClassLoader());
        this.orderTotalDelivery = in.readParcelable(OrderTotalDelivery.class.getClassLoader());
        this.orderTotalOverAll = in.readParcelable(OrderTotalOverAll.class.getClassLoader());
    }

    public static final Creator<OrderTotal> CREATOR = new Creator<OrderTotal>() {
        @Override
        public OrderTotal createFromParcel(Parcel source) {
            return new OrderTotal(source);
        }

        @Override
        public OrderTotal[] newArray(int size) {
            return new OrderTotal[size];
        }
    };
}
