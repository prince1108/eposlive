package com.foodciti.foodcitipartener.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by quflip1 on 13-05-2017.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestaurentUser implements Parcelable {
    @JsonProperty("user_id")
    private String restaurentUserId;

    @JsonProperty("email_id")
    private String emailId;

    @JsonProperty("user_token")
    private String userToken;

    public String getFoodTruckUserId() {
        return restaurentUserId;
    }

    public void setFoodTruckUserId(String foodTruckUserId) {
        this.restaurentUserId = foodTruckUserId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.restaurentUserId);
        dest.writeString(this.emailId);
        dest.writeString(this.userToken);
    }

    public RestaurentUser() {
    }

    protected RestaurentUser(Parcel in) {
        this.restaurentUserId = in.readString();
        this.emailId = in.readString();
        this.userToken = in.readString();
    }

    public static final Creator<RestaurentUser> CREATOR = new Creator<RestaurentUser>() {
        @Override
        public RestaurentUser createFromParcel(Parcel source) {
            return new RestaurentUser(source);
        }

        @Override
        public RestaurentUser[] newArray(int size) {
            return new RestaurentUser[size];
        }
    };
}
