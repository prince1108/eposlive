package com.foodciti.foodcitipartener.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by quflip1 on 30-11-2017.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubInfo implements Parcelable {

    @JsonProperty("_id")
    private String id;
    @JsonProperty("option_sub_category")
    private String subCategory;
    @JsonProperty("option_price")
    private String subItemPrice;
    @JsonProperty("option_name")
    private String subItemName;
    @JsonProperty("restaurent_id")
    private String restaurnetId;
    @JsonProperty("option_cumpulsory")
    private String optionCumpulsory;
    @JsonProperty("option_price_show")
    private String optionPriceShow;
    @JsonProperty("option_quantity_ordered")
    private int quantityOrdered;
    @JsonProperty("option_stock")
    private int optionStock;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getSubItemPrice() {
        return subItemPrice;
    }

    public void setSubItemPrice(String subItemPrice) {
        this.subItemPrice = subItemPrice;
    }

    public String getSubItemName() {
        return subItemName;
    }

    public void setSubItemName(String subItemName) {
        this.subItemName = subItemName;
    }

    public String getRestaurnetId() {
        return restaurnetId;
    }

    public void setRestaurnetId(String restaurnetId) {
        this.restaurnetId = restaurnetId;
    }

    public String getOptionCumpulsory() {
        return optionCumpulsory;
    }

    public void setOptionCumpulsory(String optionCumpulsory) {
        this.optionCumpulsory = optionCumpulsory;
    }

    public String getOptionPriceShow() {
        return optionPriceShow;
    }

    public void setOptionPriceShow(String optionPriceShow) {
        this.optionPriceShow = optionPriceShow;
    }

    public int getQuantityOrdered() {
        return quantityOrdered;
    }

    public void setQuantityOrdered(int quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }

    public int getOptionStock() {
        return optionStock;
    }

    public void setOptionStock(int optionStock) {
        this.optionStock = optionStock;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.subCategory);
        dest.writeString(this.subItemPrice);
        dest.writeString(this.subItemName);
        dest.writeString(this.restaurnetId);
        dest.writeString(this.optionCumpulsory);
        dest.writeString(this.optionPriceShow);
        dest.writeInt(this.quantityOrdered);
        dest.writeInt(this.optionStock);
    }

    public SubInfo() {
    }

    protected SubInfo(Parcel in) {
        this.id = in.readString();
        this.subCategory = in.readString();
        this.subItemPrice = in.readString();
        this.subItemName = in.readString();
        this.restaurnetId = in.readString();
        this.optionCumpulsory = in.readString();
        this.optionPriceShow = in.readString();
        this.quantityOrdered = in.readInt();
        this.optionStock = in.readInt();
    }

    public static final Creator<SubInfo> CREATOR = new Creator<SubInfo>() {
        @Override
        public SubInfo createFromParcel(Parcel source) {
            return new SubInfo(source);
        }

        @Override
        public SubInfo[] newArray(int size) {
            return new SubInfo[size];
        }
    };
}
