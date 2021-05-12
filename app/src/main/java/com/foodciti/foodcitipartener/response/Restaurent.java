package com.foodciti.foodcitipartener.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by quflip1 on 01-12-2017.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Restaurent implements Parcelable {
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
    @JsonProperty("restaurent_name")
    private String restaurentName;
    @JsonProperty("restaurent_location")
    private String location;
    @JsonProperty("item_list")
    private List<RestaurentItem> restaurentItemList;
    @JsonProperty("restaurent_cusine")
    private List<String> cusines;
    @JsonProperty("restaurent_open_status")
    private int openStatus;
    @JsonProperty("restaurent_total_votes")
    private String totalVotes;
    @JsonProperty("restaurent_rating")
    private double rating;

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

    public List<RestaurentItem> getRestaurentItemList() {
        return restaurentItemList;
    }

    public void setRestaurentItemList(List<RestaurentItem> restaurentItemList) {
        this.restaurentItemList = restaurentItemList;
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
        dest.writeString(this.restaurentName);
        dest.writeString(this.location);
        dest.writeTypedList(this.restaurentItemList);
        dest.writeStringList(this.cusines);
        dest.writeInt(this.openStatus);
        dest.writeString(this.totalVotes);
        dest.writeDouble(this.rating);
    }

    public Restaurent() {
    }

    protected Restaurent(Parcel in) {
        this.id = in.readString();
        this.postalCode = in.readString();
        this.closingTimeTwo = in.readString();
        this.startingTimeTwo = in.readString();
        this.closingTimeOne = in.readString();
        this.startingTimeOne = in.readString();
        this.tag = in.readString();
        this.restaurentName = in.readString();
        this.location = in.readString();
        this.restaurentItemList = in.createTypedArrayList(RestaurentItem.CREATOR);
        this.cusines = in.createStringArrayList();
        this.openStatus = in.readInt();
        this.totalVotes = in.readString();
        this.rating = in.readDouble();
    }

    public static final Creator<Restaurent> CREATOR = new Creator<Restaurent>() {
        @Override
        public Restaurent createFromParcel(Parcel source) {
            return new Restaurent(source);
        }

        @Override
        public Restaurent[] newArray(int size) {
            return new Restaurent[size];
        }
    };
}