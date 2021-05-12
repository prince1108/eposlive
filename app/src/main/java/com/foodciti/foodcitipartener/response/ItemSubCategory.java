package com.foodciti.foodcitipartener.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemSubCategory implements Parcelable {
    @JsonProperty("_id")
    private String id;
    @JsonProperty("restaurent_id")
    private String restaurent_id;
    @JsonProperty("option_sub_category")
    private String option_sub_category;
    @JsonProperty("option_price")
    private int option_price;
    @JsonProperty("option_name")
    private String option_name;
    @JsonProperty("option_cumpulsory")
    private boolean itemTag;
    @JsonProperty("option_price_show")
    private boolean option_price_show;
    @JsonProperty("option_quantity_ordered")
    private int option_quantity_ordered;
    @JsonProperty("option_stock")
    private int option_stock;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRestaurent_id() {
        return restaurent_id;
    }

    public void setRestaurent_id(String restaurent_id) {
        this.restaurent_id = restaurent_id;
    }

    public String getOption_sub_category() {
        return option_sub_category;
    }

    public void setOption_sub_category(String option_sub_category) {
        this.option_sub_category = option_sub_category;
    }

    public int getOption_price() {
        return option_price;
    }

    public void setOption_price(int option_price) {
        this.option_price = option_price;
    }

    public String getOption_name() {
        return option_name;
    }

    public void setOption_name(String option_name) {
        this.option_name = option_name;
    }

    public boolean isItemTag() {
        return itemTag;
    }

    public void setItemTag(boolean itemTag) {
        this.itemTag = itemTag;
    }

    public boolean isOption_price_show() {
        return option_price_show;
    }

    public void setOption_price_show(boolean option_price_show) {
        this.option_price_show = option_price_show;
    }

    public int getOption_quantity_ordered() {
        return option_quantity_ordered;
    }

    public void setOption_quantity_ordered(int option_quantity_ordered) {
        this.option_quantity_ordered = option_quantity_ordered;
    }

    public int getOption_stock() {
        return option_stock;
    }

    public void setOption_stock(int option_stock) {
        this.option_stock = option_stock;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.restaurent_id);
        dest.writeString(this.option_sub_category);
        dest.writeInt(this.option_price);
        dest.writeString(this.option_name);
        dest.writeByte(this.itemTag ? (byte) 1 : (byte) 0);
        dest.writeByte(this.option_price_show ? (byte) 1 : (byte) 0);
        dest.writeInt(this.option_quantity_ordered);
        dest.writeInt(this.option_stock);
    }

    public ItemSubCategory() {
    }

    protected ItemSubCategory(Parcel in) {
        this.id = in.readString();
        this.restaurent_id = in.readString();
        this.option_sub_category = in.readString();
        this.option_price = in.readInt();
        this.option_name = in.readString();
        this.itemTag = in.readByte() != 0;
        this.option_price_show = in.readByte() != 0;
        this.option_quantity_ordered = in.readInt();
        this.option_stock = in.readInt();
    }

    public static final Parcelable.Creator<ItemSubCategory> CREATOR = new Parcelable.Creator<ItemSubCategory>() {
        @Override
        public ItemSubCategory createFromParcel(Parcel source) {
            return new ItemSubCategory(source);
        }

        @Override
        public ItemSubCategory[] newArray(int size) {
            return new ItemSubCategory[size];
        }
    };
}
