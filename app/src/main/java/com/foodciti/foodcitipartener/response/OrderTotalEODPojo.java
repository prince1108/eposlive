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
public class OrderTotalEODPojo implements Parcelable {
    @JsonProperty("order_unpaid_count")
    private String orderUnpaidCount;
    @JsonProperty("order_total_unpaid")
    private double orderTotalUnpaid;

    public String getOrderUnpaidCount() {
        return orderUnpaidCount;
    }

    public void setOrderUnpaidCount(String orderUnpaidCount) {
        this.orderUnpaidCount = orderUnpaidCount;
    }

    public double getOrderTotalUnpaid() {
        return orderTotalUnpaid;
    }

    public void setOrderTotalUnpaid(double orderTotalUnpaid) {
        this.orderTotalUnpaid = orderTotalUnpaid;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.orderUnpaidCount);
        dest.writeDouble(this.orderTotalUnpaid);
    }

    public OrderTotalEODPojo() {
    }

    protected OrderTotalEODPojo(Parcel in) {
        this.orderUnpaidCount = in.readString();
        this.orderTotalUnpaid = in.readDouble();
    }

    public static final Creator<OrderTotalEODPojo> CREATOR = new Creator<OrderTotalEODPojo>() {
        @Override
        public OrderTotalEODPojo createFromParcel(Parcel source) {
            return new OrderTotalEODPojo(source);
        }

        @Override
        public OrderTotalEODPojo[] newArray(int size) {
            return new OrderTotalEODPojo[size];
        }
    };
}
