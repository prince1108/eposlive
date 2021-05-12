package com.foodciti.foodcitipartener.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by quflip1 on 29-11-2017.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InventoryItem implements Parcelable {
    @JsonProperty("_id")
    private String id;
    @JsonProperty("item_price")
    private double itemPrice;
    @JsonProperty("item_discount_price")
    private double itemDiscountPrice;
    @JsonProperty("item_category")
    private String itemCategory;
    @JsonProperty("item_description")
    private String itemDescription;
    @JsonProperty("item_tag")
    private String itemTag;
    @JsonProperty("item_name")
    private String itemName;
    @JsonProperty("item_sub_category")
    private List<ItemSubCategory> itemSubCategory;
    @JsonProperty("item_quantity_ordered")
    private int itemQuantityOrdered;
    @JsonProperty("item_stock")
    private int itemStock;
    @JsonProperty("item_illustrations")
    private List<String> itemIllustration ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public double getItemDiscountPrice() {
        return itemDiscountPrice;
    }

    public void setItemDiscountPrice(double itemDiscountPrice) {
        this.itemDiscountPrice = itemDiscountPrice;
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

    public List<ItemSubCategory> getItemSubCategory() {
        return itemSubCategory;
    }

    public void setItemSubCategory(List<ItemSubCategory> itemSubCategory) {
        this.itemSubCategory = itemSubCategory;
    }

    public int getItemQuantityOrdered() {
        return itemQuantityOrdered;
    }

    public void setItemQuantityOrdered(int itemQuantityOrdered) {
        this.itemQuantityOrdered = itemQuantityOrdered;
    }

    public int getItemStock() {
        return itemStock;
    }

    public void setItemStock(int itemStock) {
        this.itemStock = itemStock;
    }

    public List<String> getItemIllustration() {
        return itemIllustration;
    }

    public void setItemIllustration(List<String> itemIllustration) {
        this.itemIllustration = itemIllustration;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeDouble(this.itemPrice);
        dest.writeDouble(this.itemDiscountPrice);
        dest.writeString(this.itemCategory);
        dest.writeString(this.itemDescription);
        dest.writeString(this.itemTag);
        dest.writeString(this.itemName);
        dest.writeTypedList(this.itemSubCategory);
        dest.writeInt(this.itemQuantityOrdered);
        dest.writeInt(this.itemStock);
        dest.writeStringList(this.itemIllustration);
    }

    public InventoryItem() {
    }

    protected InventoryItem(Parcel in) {
        this.id = in.readString();
        this.itemPrice = in.readDouble();
        this.itemDiscountPrice = in.readDouble();
        this.itemCategory = in.readString();
        this.itemDescription = in.readString();
        this.itemTag = in.readString();
        this.itemName = in.readString();
        this.itemSubCategory = in.createTypedArrayList(ItemSubCategory.CREATOR);
        this.itemQuantityOrdered = in.readInt();
        this.itemStock = in.readInt();
        this.itemIllustration = in.createStringArrayList();
    }

    public static final Parcelable.Creator<InventoryItem> CREATOR = new Parcelable.Creator<InventoryItem>() {
        @Override
        public InventoryItem createFromParcel(Parcel source) {
            return new InventoryItem(source);
        }

        @Override
        public InventoryItem[] newArray(int size) {
            return new InventoryItem[size];
        }
    };
}

