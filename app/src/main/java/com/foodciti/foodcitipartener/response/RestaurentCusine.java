package com.foodciti.foodcitipartener.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by quflip1 on 10-04-2017.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestaurentCusine implements Parcelable {
    @JsonProperty("foodtruck_cusine")
    private List<String> restaurentCusine;

    public List<String> getFoodTruckCusine() {
        return restaurentCusine;
    }

    public void setFoodTruckCusine(List<String> foodTruckCusine) {
        this.restaurentCusine = foodTruckCusine;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.restaurentCusine);
    }

    public RestaurentCusine() {
    }

    protected RestaurentCusine(Parcel in) {
        this.restaurentCusine = in.createStringArrayList();
    }

    public static final Creator<RestaurentCusine> CREATOR = new Creator<RestaurentCusine>() {
        @Override
        public RestaurentCusine createFromParcel(Parcel source) {
            return new RestaurentCusine(source);
        }

        @Override
        public RestaurentCusine[] newArray(int size) {
            return new RestaurentCusine[size];
        }
    };
}
