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
public class OrderTotalDelivery implements Parcelable {
    @JsonProperty("order_delivery_count")
    private String orderDeliveryCount;
    @JsonProperty("order_total_delivery")
    private double orderTotalDelivery;

    public String getOrderDeliveryCount() {
        return orderDeliveryCount;
    }

    public void setOrderDeliveryCount(String orderDeliveryCount) {
        this.orderDeliveryCount = orderDeliveryCount;
    }

    public double getOrderTotalDelivery() {
        return orderTotalDelivery;
    }

    public void setOrderTotalDelivery(double orderTotalDelivery) {
        this.orderTotalDelivery = orderTotalDelivery;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.orderDeliveryCount);
        dest.writeDouble(this.orderTotalDelivery);
    }

    public OrderTotalDelivery() {
    }

    protected OrderTotalDelivery(Parcel in) {
        this.orderDeliveryCount = in.readString();
        this.orderTotalDelivery = in.readDouble();
    }

    public static final Creator<OrderTotalDelivery> CREATOR = new Creator<OrderTotalDelivery>() {
        @Override
        public OrderTotalDelivery createFromParcel(Parcel source) {
            return new OrderTotalDelivery(source);
        }

        @Override
        public OrderTotalDelivery[] newArray(int size) {
            return new OrderTotalDelivery[size];
        }
    };
}
