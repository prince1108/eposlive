
package com.foodciti.foodcitipartener.response.order_response_bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubOptions implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("Selected")
    @Expose
    private Boolean selected;

    protected SubOptions(Parcel in) {
        name = in.readString();
        price = in.readString();
        byte tmpSelected = in.readByte();
        selected = tmpSelected == 0 ? null : tmpSelected == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(price);
        dest.writeByte((byte) (selected == null ? 0 : selected ? 1 : 2));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SubOptions> CREATOR = new Creator<SubOptions>() {
        @Override
        public SubOptions createFromParcel(Parcel in) {
            return new SubOptions(in);
        }

        @Override
        public SubOptions[] newArray(int size) {
            return new SubOptions[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

}
