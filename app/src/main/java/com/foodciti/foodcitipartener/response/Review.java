package com.foodciti.foodcitipartener.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by quflip1 on 10-03-2017.
 */


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Review implements Parcelable {
    @JsonProperty("_id")
    private String reviewId;
    @JsonProperty("item_review")
    private String itemReview;
    @JsonProperty("user_type")
    private User user;

    public String getReviewId() {
        return reviewId;
    }

    public String getItemReview() {
        return itemReview;
    }

    public User getUser() {
        return user;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public void setItemReview(String itemReview) {
        this.itemReview = itemReview;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.reviewId);
        dest.writeString(this.itemReview);
        dest.writeParcelable(this.user, flags);
    }

    public Review() {
    }

    protected Review(Parcel in) {
        this.reviewId = in.readString();
        this.itemReview = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
