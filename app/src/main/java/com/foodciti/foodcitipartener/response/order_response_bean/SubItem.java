
package com.foodciti.foodcitipartener.response.order_response_bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SubItem implements Parcelable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("item_name")
    @Expose
    private String itemName;
    @SerializedName("item_id")
    @Expose
    private String itemId;
    @SerializedName("suboptions")
    @Expose
    private List<SubOptions> subOptions = null;
    @SerializedName("ordered_quantity")
    @Expose
    private Integer orderedQuantity;

    protected SubItem(Parcel in) {
        id = in.readString();
        itemName = in.readString();
        itemId = in.readString();
        subOptions = in.createTypedArrayList(SubOptions.CREATOR);
        if (in.readByte() == 0) {
            orderedQuantity = null;
        } else {
            orderedQuantity = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(itemName);
        dest.writeString(itemId);
        dest.writeTypedList(subOptions);
        if (orderedQuantity == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(orderedQuantity);
        }
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

    public List<SubOptions> getSubOptions() {
        return subOptions;
    }

    public void setSubOptions(List<SubOptions> suboptions) {
        this.subOptions = suboptions;
    }

    public Integer getOrderedQuantity() {
        return orderedQuantity;
    }

    public void setOrderedQuantity(Integer orderedQuantity) {
        this.orderedQuantity = orderedQuantity;
    }

}
