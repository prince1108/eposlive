package com.foodciti.foodcitipartener.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by quflip1 on 18-06-2017.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderMetaResponse implements Parcelable {

    @JsonProperty("_id")
    private String metaId;
    @JsonProperty("order_otp")
    private String orderOtp;

    public String getMetaId() {
        return metaId;
    }

    public void setMetaId(String metaId) {
        this.metaId = metaId;
    }

    public String getOrderOtp() {
        return orderOtp;
    }

    public void setOrderOtp(String orderOtp) {
        this.orderOtp = orderOtp;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.metaId);
        dest.writeString(this.orderOtp);
    }

    public OrderMetaResponse() {
    }

    protected OrderMetaResponse(Parcel in) {
        this.metaId = in.readString();
        this.orderOtp = in.readString();
    }

    public static final Creator<OrderMetaResponse> CREATOR = new Creator<OrderMetaResponse>() {
        @Override
        public OrderMetaResponse createFromParcel(Parcel source) {
            return new OrderMetaResponse(source);
        }

        @Override
        public OrderMetaResponse[] newArray(int size) {
            return new OrderMetaResponse[size];
        }
    };
}
