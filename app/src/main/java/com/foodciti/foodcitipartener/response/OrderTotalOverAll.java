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
public class OrderTotalOverAll implements Parcelable {
    @JsonProperty("order_total_count")
    private String orderTotalCount;
    @JsonProperty("order_total")
    private double orserTotal;

    public String getOrderTotalCount() {
        return orderTotalCount;
    }

    public void setOrderTotalCount(String orderTotalCount) {
        this.orderTotalCount = orderTotalCount;
    }

    public double getOrserTotal() {
        return orserTotal;
    }

    public void setOrserTotal(double orserTotal) {
        this.orserTotal = orserTotal;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.orderTotalCount);
        dest.writeDouble(this.orserTotal);
    }

    public OrderTotalOverAll() {
    }

    protected OrderTotalOverAll(Parcel in) {
        this.orderTotalCount = in.readString();
        this.orserTotal = in.readDouble();
    }

    public static final Creator<OrderTotalOverAll> CREATOR = new Creator<OrderTotalOverAll>() {
        @Override
        public OrderTotalOverAll createFromParcel(Parcel source) {
            return new OrderTotalOverAll(source);
        }

        @Override
        public OrderTotalOverAll[] newArray(int size) {
            return new OrderTotalOverAll[size];
        }
    };
}
