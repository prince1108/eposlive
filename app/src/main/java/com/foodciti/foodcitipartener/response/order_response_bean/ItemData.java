
package com.foodciti.foodcitipartener.response.order_response_bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ItemData implements Parcelable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("item_price")
    @Expose
    private Integer itemPrice;
    @SerializedName("item_category_id")
    @Expose
    private String itemCategoryId;
    @SerializedName("item_category")
    @Expose
    private String itemCategory;
    @SerializedName("item_description")
    @Expose
    private String itemDescription;
    @SerializedName("item_tag")
    @Expose
    private String itemTag;
    @SerializedName("item_name")
    @Expose
    private String itemName;
    @SerializedName("item_img")
    @Expose
    private String itemImg;
    @SerializedName("item_isonline")
    @Expose
    private Boolean itemIsonline;
    @SerializedName("item_sub_category")
    @Expose
    private List<String> itemSubCategory = null;
    @SerializedName("item_quantity_ordered")
    @Expose
    private Integer itemQuantityOrdered;
    @SerializedName("item_stock")
    @Expose
    private Integer itemStock;
    @SerializedName("item_illustrations")
    @Expose
    private List<Object> itemIllustrations = null;

    protected ItemData(Parcel in) {
        id = in.readString();
        if (in.readByte() == 0) {
            itemPrice = null;
        } else {
            itemPrice = in.readInt();
        }
        itemCategoryId = in.readString();
        itemCategory = in.readString();
        itemDescription = in.readString();
        itemTag = in.readString();
        itemName = in.readString();
        itemImg = in.readString();
        byte tmpItemIsonline = in.readByte();
        itemIsonline = tmpItemIsonline == 0 ? null : tmpItemIsonline == 1;
        itemSubCategory = in.createStringArrayList();
        if (in.readByte() == 0) {
            itemQuantityOrdered = null;
        } else {
            itemQuantityOrdered = in.readInt();
        }
        if (in.readByte() == 0) {
            itemStock = null;
        } else {
            itemStock = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        if (itemPrice == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(itemPrice);
        }
        dest.writeString(itemCategoryId);
        dest.writeString(itemCategory);
        dest.writeString(itemDescription);
        dest.writeString(itemTag);
        dest.writeString(itemName);
        dest.writeString(itemImg);
        dest.writeByte((byte) (itemIsonline == null ? 0 : itemIsonline ? 1 : 2));
        dest.writeStringList(itemSubCategory);
        if (itemQuantityOrdered == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(itemQuantityOrdered);
        }
        if (itemStock == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(itemStock);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ItemData> CREATOR = new Creator<ItemData>() {
        @Override
        public ItemData createFromParcel(Parcel in) {
            return new ItemData(in);
        }

        @Override
        public ItemData[] newArray(int size) {
            return new ItemData[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(Integer itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemCategoryId() {
        return itemCategoryId;
    }

    public void setItemCategoryId(String itemCategoryId) {
        this.itemCategoryId = itemCategoryId;
    }

    public String getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemTag() {
        return itemTag;
    }

    public void setItemTag(String itemTag) {
        this.itemTag = itemTag;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemImg() {
        return itemImg;
    }

    public void setItemImg(String itemImg) {
        this.itemImg = itemImg;
    }

    public Boolean getItemIsonline() {
        return itemIsonline;
    }

    public void setItemIsonline(Boolean itemIsonline) {
        this.itemIsonline = itemIsonline;
    }

    public List<String> getItemSubCategory() {
        return itemSubCategory;
    }

    public void setItemSubCategory(List<String> itemSubCategory) {
        this.itemSubCategory = itemSubCategory;
    }

    public Integer getItemQuantityOrdered() {
        return itemQuantityOrdered;
    }

    public void setItemQuantityOrdered(Integer itemQuantityOrdered) {
        this.itemQuantityOrdered = itemQuantityOrdered;
    }

    public Integer getItemStock() {
        return itemStock;
    }

    public void setItemStock(Integer itemStock) {
        this.itemStock = itemStock;
    }

    public List<Object> getItemIllustrations() {
        return itemIllustrations;
    }

    public void setItemIllustrations(List<Object> itemIllustrations) {
        this.itemIllustrations = itemIllustrations;
    }

}
