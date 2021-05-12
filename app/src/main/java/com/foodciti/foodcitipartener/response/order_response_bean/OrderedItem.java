
package com.foodciti.foodcitipartener.response.order_response_bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderedItem implements Parcelable {

    @SerializedName("_id")
    @Expose
    private String itemId;
    @SerializedName("item_id")
    @Expose
    private ItemData itemData;
    @SerializedName("order_sub_item")
    @Expose
    private List<SubItem> subItemList = null;
    @SerializedName("totalPrice")
    @Expose
    private Integer totalPrice;
    @SerializedName("ordered_quantity")
    @Expose
    private Integer quantity;

    protected OrderedItem(Parcel in) {
        itemId = in.readString();
        itemData = in.readParcelable(ItemData.class.getClassLoader());
        subItemList = in.createTypedArrayList(SubItem.CREATOR);
        if (in.readByte() == 0) {
            totalPrice = null;
        } else {
            totalPrice = in.readInt();
        }
        if (in.readByte() == 0) {
            quantity = null;
        } else {
            quantity = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemId);
        dest.writeParcelable(itemData, flags);
        dest.writeTypedList(subItemList);
        if (totalPrice == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(totalPrice);
        }
        if (quantity == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(quantity);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderedItem> CREATOR = new Creator<OrderedItem>() {
        @Override
        public OrderedItem createFromParcel(Parcel in) {
            return new OrderedItem(in);
        }

        @Override
        public OrderedItem[] newArray(int size) {
            return new OrderedItem[size];
        }
    };

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String id) {
        this.itemId = id;
    }

    public ItemData getItemData() {
        return itemData;
    }

    public void setItemData(ItemData itemData) {
        this.itemData = itemData;
    }

    public List<SubItem> getSubItemList() {
        return subItemList;
    }

    public void setSubItemList(List<SubItem> subItemList) {
        this.subItemList = subItemList;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer orderedQuantity) {
        this.quantity = orderedQuantity;
    }

}
