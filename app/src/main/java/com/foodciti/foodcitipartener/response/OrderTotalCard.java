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
public class OrderTotalCard implements Parcelable {
    @JsonProperty("order_paid_count")
    private String orderPaidCount;
    @JsonProperty("order_total_paid")
    private double orderTotalPaid;


    public String getOrderPaidCount() {
        return orderPaidCount;
    }

    public void setOrderPaidCount(String orderPaidCount) {
        this.orderPaidCount = orderPaidCount;
    }

    public double getOrderTotalPaid() {
        return orderTotalPaid;
    }

    public void setOrderTotalPaid(double orderTotalPaid) {
        this.orderTotalPaid = orderTotalPaid;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.orderPaidCount);
        dest.writeDouble(this.orderTotalPaid);
    }

    public OrderTotalCard() {
    }

    protected OrderTotalCard(Parcel in) {
        this.orderPaidCount = in.readString();
        this.orderTotalPaid = in.readDouble();
    }

    public static final Creator<OrderTotalCard> CREATOR = new Creator<OrderTotalCard>() {
        @Override
        public OrderTotalCard createFromParcel(Parcel source) {
            return new OrderTotalCard(source);
        }

        @Override
        public OrderTotalCard[] newArray(int size) {
            return new OrderTotalCard[size];
        }
    };
}
