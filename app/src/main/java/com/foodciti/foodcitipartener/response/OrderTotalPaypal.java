package com.foodciti.foodcitipartener.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderTotalPaypal implements Parcelable {
    @JsonProperty("order_unpaid_count")
    private String orderPaypalCount;
    @JsonProperty("order_total_unpaid")
    private double orderTotalPaypal;

    public OrderTotalPaypal(){
    }

    protected OrderTotalPaypal(Parcel in) {
        orderPaypalCount = in.readString();
        orderTotalPaypal = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderPaypalCount);
        dest.writeDouble(orderTotalPaypal);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderTotalPaypal> CREATOR = new Creator<OrderTotalPaypal>() {
        @Override
        public OrderTotalPaypal createFromParcel(Parcel in) {
            return new OrderTotalPaypal(in);
        }

        @Override
        public OrderTotalPaypal[] newArray(int size) {
            return new OrderTotalPaypal[size];
        }
    };

    public String getOrderPaypalCount() {
        return orderPaypalCount;
    }

    public void setOrderPaypalCount(String orderPaypalCount) {
        this.orderPaypalCount = orderPaypalCount;
    }

    public double getOrderTotalPaypal() {
        return orderTotalPaypal;
    }

    public void setOrderTotalPaypal(double orderTotalPaypal) {
        this.orderTotalPaypal = orderTotalPaypal;
    }
}
