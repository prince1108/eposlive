package com.foodciti.foodcitipartener.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by quflip1 on 28-11-2017.
 */



@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubItem implements Parcelable {
    @JsonProperty("_id")
    private String id;
    @JsonProperty("item_name")
    private String itemName;
    @JsonProperty("item_id")
    private String itemId;
    @JsonProperty("ordered_quantity")
    private String orderedQuantity;
    @JsonProperty("suboptions")
    private List<SubOptions> subOptions;

    protected SubItem(Parcel in) {
        id = in.readString();
        itemName = in.readString();
        itemId = in.readString();
        orderedQuantity = in.readString();
        subOptions = in.createTypedArrayList(SubOptions.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(itemName);
        dest.writeString(itemId);
        dest.writeString(orderedQuantity);
        dest.writeTypedList(subOptions);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SubItem> CREATOR = new Creator<SubItem>() {
        @Override
        public SubItem createFromParcel(Parcel in) {
            return new SubItem(in);
        }

        @Override
        public SubItem[] newArray(int size) {
            return new SubItem[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getOrderedQuantity() {
        return orderedQuantity;
    }

    public void setOrderedQuantity(String orderedQuantity) {
        this.orderedQuantity = orderedQuantity;
    }

    public SubItem() {
    }

    public List<SubOptions> getSubOptions() {
        return subOptions;
    }

    public void setSubOptions(List<SubOptions> subOptions) {
        this.subOptions = subOptions;
    }
}
