package com.foodciti.foodcitipartener.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by quflip1 on 06-04-2017.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemCategories implements Parcelable {
    @JsonProperty("_id")
    private String itemId;
    @JsonProperty("restaurent_id")
    private String restaurentID;
    @JsonProperty("category_name")
    private List<String> categoryList;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getRestaurentID() {
        return restaurentID;
    }

    public void setRestaurentID(String restaurentID) {
        this.restaurentID = restaurentID;
    }

    public List<String> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<String> categoryList) {
        this.categoryList = categoryList;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.itemId);
        dest.writeString(this.restaurentID);
        dest.writeStringList(this.categoryList);
    }

    public ItemCategories() {
    }

    protected ItemCategories(Parcel in) {
        this.itemId = in.readString();
        this.restaurentID = in.readString();
        this.categoryList = in.createStringArrayList();
    }

    public static final Creator<ItemCategories> CREATOR = new Creator<ItemCategories>() {
        @Override
        public ItemCategories createFromParcel(Parcel source) {
            return new ItemCategories(source);
        }

        @Override
        public ItemCategories[] newArray(int size) {
            return new ItemCategories[size];
        }
    };
}
