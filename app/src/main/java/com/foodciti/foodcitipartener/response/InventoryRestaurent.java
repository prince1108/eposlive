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
public class  InventoryRestaurent implements Parcelable {
    @JsonProperty("_id")
    private String id;
    @JsonProperty("restaurent_postal_code")
    private String postalCode;
    @JsonProperty("restaurent_closing_timing_two")
    private String closingTimeTwo;
    @JsonProperty("restaurent_starting_timing_two")
    private String startingTimeTwo;
    @JsonProperty("restaurent_closing_timing_one")
    private String closingTimeOne;
    @JsonProperty("restaurent_starting_timing_one")
    private String startingTimeOne;
    @JsonProperty("restaurent_tag")
    private String tag;
    @JsonProperty("ph_no")
    private String phoneNumber;
    @JsonProperty("restaurent_name")
    private String restaurentName;
    @JsonProperty("restaurent_location")
    private String location;
    @JsonProperty("item_list")
    private List<InventoryItem> inventoryItemList;
    @JsonProperty("restaurent_cusine")
    private List<String> cusines;
    @JsonProperty("restaurent_open_status")
    private int openStatus;
    @JsonProperty("restaurent_total_votes")
    private String totalVotes;
    @JsonProperty("restaurent_rating")
    private double rating;
    @JsonProperty("restaurent_shutdown")
    private Boolean restaurentShutdown;
    @JsonProperty("restaurent_delivery_range")
    private double restuarent_delivery_range;
    @JsonProperty("restaurent_order_flag")
    private String restaurentOrderFlag;
    @JsonProperty("restaurent_discount_overall")
    private String restaurentDiscountOverall;
    @JsonProperty("restaurent_minimum_order_value")
    private String restaurentMinimumOrderValue;
    @JsonProperty("restaurent_delivery_charge")
    private String restaurentDeliveryCharge;

    @JsonProperty("restaurent_pickup_availability")
    private Boolean restaurent_pickup_availability;
    @JsonProperty("restaurent_delivery_availability")
    private Boolean restaurent_delivery_availability;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getClosingTimeTwo() {
        return closingTimeTwo;
    }

    public void setClosingTimeTwo(String closingTimeTwo) {
        this.closingTimeTwo = closingTimeTwo;
    }

    public String getStartingTimeTwo() {
        return startingTimeTwo;
    }

    public void setStartingTimeTwo(String startingTimeTwo) {
        this.startingTimeTwo = startingTimeTwo;
    }

    public String getClosingTimeOne() {
        return closingTimeOne;
    }

    public void setClosingTimeOne(String closingTimeOne) {
        this.closingTimeOne = closingTimeOne;
    }

    public String getStartingTimeOne() {
        return startingTimeOne;
    }

    public void setStartingTimeOne(String startingTimeOne) {
        this.startingTimeOne = startingTimeOne;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRestaurentName() {
        return restaurentName;
    }

    public void setRestaurentName(String restaurentName) {
        this.restaurentName = restaurentName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<InventoryItem> getInventoryItemList() {
        return inventoryItemList;
    }

    public void setInventoryItemList(List<InventoryItem> inventoryItemList) {
        this.inventoryItemList = inventoryItemList;
    }

    public List<String> getCusines() {
        return cusines;
    }

    public void setCusines(List<String> cusines) {
        this.cusines = cusines;
    }

    public int getOpenStatus() {
        return openStatus;
    }

    public void setOpenStatus(int openStatus) {
        this.openStatus = openStatus;
    }

    public String getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(String totalVotes) {
        this.totalVotes = totalVotes;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Boolean getRestaurentShutdown() {
        return restaurentShutdown;
    }

    public void setRestaurentShutdown(Boolean restaurentShutdown) {
        this.restaurentShutdown = restaurentShutdown;
    }

    public double getRestuarent_delivery_range() {
        return restuarent_delivery_range;
    }

    public void setRestuarent_delivery_range(double restuarent_delivery_range) {
        this.restuarent_delivery_range = restuarent_delivery_range;
    }

    public String getRestaurentOrderFlag() {
        return restaurentOrderFlag;
    }

    public void setRestaurentOrderFlag(String restaurentOrderFlag) {
        this.restaurentOrderFlag = restaurentOrderFlag;
    }

    public String getRestaurentDiscountOverall() {
        return restaurentDiscountOverall;
    }

    public void setRestaurentDiscountOverall(String restaurentDiscountOverall) {
        this.restaurentDiscountOverall = restaurentDiscountOverall;
    }

    public String getRestaurentMinimumOrderValue() {
        return restaurentMinimumOrderValue;
    }

    public void setRestaurentMinimumOrderValue(String restaurentMinimumOrderValue) {
        this.restaurentMinimumOrderValue = restaurentMinimumOrderValue;
    }

    public String getRestaurentDeliveryCharge() {
        return restaurentDeliveryCharge;
    }

    public void setRestaurentDeliveryCharge(String restaurentDeliveryCharge) {
        this.restaurentDeliveryCharge = restaurentDeliveryCharge;
    }

    public Boolean getRestaurent_pickup_availability() {
        return restaurent_pickup_availability;
    }

    public void setRestaurent_pickup_availability(Boolean restaurent_pickup_availability) {
        this.restaurent_pickup_availability = restaurent_pickup_availability;
    }

    public Boolean getRestaurent_delivery_availability() {
        return restaurent_delivery_availability;
    }

    public void setRestaurent_delivery_availability(Boolean restaurent_delivery_availability) {
        this.restaurent_delivery_availability = restaurent_delivery_availability;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.postalCode);
        dest.writeString(this.closingTimeTwo);
        dest.writeString(this.startingTimeTwo);
        dest.writeString(this.closingTimeOne);
        dest.writeString(this.startingTimeOne);
        dest.writeString(this.tag);
        dest.writeString(this.phoneNumber);
        dest.writeString(this.restaurentName);
        dest.writeString(this.location);
        dest.writeTypedList(this.inventoryItemList);
        dest.writeStringList(this.cusines);
        dest.writeInt(this.openStatus);
        dest.writeString(this.totalVotes);
        dest.writeDouble(this.rating);
        dest.writeValue(this.restaurentShutdown);
        dest.writeDouble(this.restuarent_delivery_range);
        dest.writeString(this.restaurentOrderFlag);
        dest.writeString(this.restaurentDiscountOverall);
        dest.writeString(this.restaurentMinimumOrderValue);
        dest.writeString(this.restaurentDeliveryCharge);
        dest.writeValue(this.restaurent_pickup_availability);
        dest.writeValue(this.restaurent_delivery_availability);
    }

    public InventoryRestaurent() {
    }

    protected InventoryRestaurent(Parcel in) {
        this.id = in.readString();
        this.postalCode = in.readString();
        this.closingTimeTwo = in.readString();
        this.startingTimeTwo = in.readString();
        this.closingTimeOne = in.readString();
        this.startingTimeOne = in.readString();
        this.tag = in.readString();
        this.phoneNumber = in.readString();
        this.restaurentName = in.readString();
        this.location = in.readString();
        this.inventoryItemList = in.createTypedArrayList(InventoryItem.CREATOR);
        this.cusines = in.createStringArrayList();
        this.openStatus = in.readInt();
        this.totalVotes = in.readString();
        this.rating = in.readDouble();
        this.restaurentShutdown = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.restuarent_delivery_range = in.readDouble();
        this.restaurentOrderFlag = in.readString();
        this.restaurentDiscountOverall = in.readString();
        this.restaurentMinimumOrderValue = in.readString();
        this.restaurentDeliveryCharge = in.readString();
        this.restaurent_pickup_availability = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.restaurent_delivery_availability = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Parcelable.Creator<InventoryRestaurent> CREATOR = new Parcelable.Creator<InventoryRestaurent>() {
        @Override
        public InventoryRestaurent createFromParcel(Parcel source) {
            return new InventoryRestaurent(source);
        }

        @Override
        public InventoryRestaurent[] newArray(int size) {
            return new InventoryRestaurent[size];
        }
    };
}
