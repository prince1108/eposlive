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
public class OrderTotalPickUp implements Parcelable {
    @JsonProperty("order_pick_up_count")
    private String orderPickUpCount;
    @JsonProperty("order_total_pick_up")
    private double orderTotalPickUp;


    public String getOrderPickUpCount() {
        return orderPickUpCount;
    }

    public void setOrderPickUpCount(String orderPickUpCount) {
        this.orderPickUpCount = orderPickUpCount;
    }

    public double getOrderTotalPickUp() {
        return orderTotalPickUp;
    }

    public void setOrderTotalPickUp(double orderTotalPickUp) {
        this.orderTotalPickUp = orderTotalPickUp;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.orderPickUpCount);
        dest.writeDouble(this.orderTotalPickUp);
    }

    public OrderTotalPickUp() {
    }

    protected OrderTotalPickUp(Parcel in) {
        this.orderPickUpCount = in.readString();
        this.orderTotalPickUp = in.readDouble();
    }

    public static final Creator<OrderTotalPickUp> CREATOR = new Creator<OrderTotalPickUp>() {
        @Override
        public OrderTotalPickUp createFromParcel(Parcel source) {
            return new OrderTotalPickUp(source);
        }

        @Override
        public OrderTotalPickUp[] newArray(int size) {
            return new OrderTotalPickUp[size];
        }
    };
}
