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
public class User implements Parcelable {
    @JsonProperty("_id")
    private String userId;
    @JsonProperty("email_id")
    private String emailId;
    @JsonProperty("ph_no")
    private String phNo;
    @JsonProperty("login_type")
    private int loginType;
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("address")
    private String address;
    @JsonProperty("postal_code")
    private String postalCode;
    @JsonProperty("city")
    private String city;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPhNo() {
        return phNo;
    }

    public void setPhNo(String phNo) {
        this.phNo = phNo;
    }

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.emailId);
        dest.writeString(this.phNo);
        dest.writeInt(this.loginType);
        dest.writeString(this.userName);
        dest.writeString(this.address);
        dest.writeString(this.postalCode);
        dest.writeString(this.city);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.userId = in.readString();
        this.emailId = in.readString();
        this.phNo = in.readString();
        this.loginType = in.readInt();
        this.userName = in.readString();
        this.address = in.readString();
        this.postalCode = in.readString();
        this.city = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
