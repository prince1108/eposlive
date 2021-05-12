package com.foodciti.foodcitipartener.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by quflip1 on 23-01-2017.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderedItem implements Parcelable {
    @JsonProperty("_id")
    private String itemId;
    @JsonProperty("item_id")
    private ItemData itemData;
    @JsonProperty("ordered_quantity")
    private String quantity;
    @JsonProperty("order_sub_item")
    private List<SubItem> subItemList;
    @JsonProperty("totalPrice")
    private String totalPrice;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public ItemData getItemData() {
        return itemData;
    }

    public void setItemData(ItemData itemData) {
        this.itemData = itemData;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public List<SubItem> getSubItemList() {
        return subItemList;
    }

    public void setSubItemList(List<SubItem> subItemList) {
        this.subItemList = subItemList;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.itemId);
        dest.writeParcelable(this.itemData, flags);
        dest.writeString(this.quantity);
        dest.writeTypedList(this.subItemList);
        dest.writeString(this.totalPrice);
    }

    public OrderedItem() {
    }

    protected OrderedItem(Parcel in) {
        this.itemId = in.readString();
        this.itemData = in.readParcelable(ItemData.class.getClassLoader());
        this.quantity = in.readString();
        this.subItemList = in.createTypedArrayList(SubItem.CREATOR);
        this.totalPrice = in.readString();
    }

    public static final Parcelable.Creator<OrderedItem> CREATOR = new Parcelable.Creator<OrderedItem>() {
        @Override
        public OrderedItem createFromParcel(Parcel source) {
            return new OrderedItem(source);
        }

        @Override
        public OrderedItem[] newArray(int size) {
            return new OrderedItem[size];
        }
    };
}
