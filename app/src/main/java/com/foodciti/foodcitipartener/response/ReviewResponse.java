package com.foodciti.foodcitipartener.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by quflip1 on 10-03-2017.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewResponse implements Parcelable {
    @JsonProperty("_id")
    private String reviewResponseId;
    @JsonProperty("item_id")
    private String itemId;
    @JsonProperty("review")
    private List<Review> reviewList;

    public String getReviewResponseId() {
        return reviewResponseId;
    }

    public String getItemId() {
        return itemId;
    }

    public List<Review> getReviewList() {
        return reviewList;
    }

    public void setReviewResponseId(String reviewResponseId) {
        this.reviewResponseId = reviewResponseId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setReviewList(List<Review> reviewList) {
        this.reviewList = reviewList;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.reviewResponseId);
        dest.writeString(this.itemId);
        dest.writeTypedList(this.reviewList);
    }

    public ReviewResponse() {
    }

    protected ReviewResponse(Parcel in) {
        this.reviewResponseId = in.readString();
        this.itemId = in.readString();
        this.reviewList = in.createTypedArrayList(Review.CREATOR);
    }

    public static final Creator<ReviewResponse> CREATOR = new Creator<ReviewResponse>() {
        @Override
        public ReviewResponse createFromParcel(Parcel source) {
            return new ReviewResponse(source);
        }

        @Override
        public ReviewResponse[] newArray(int size) {
            return new ReviewResponse[size];
        }
    };
}
