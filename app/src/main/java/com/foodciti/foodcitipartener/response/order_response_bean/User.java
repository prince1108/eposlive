
package com.foodciti.foodcitipartener.response.order_response_bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {

    @SerializedName("_id")
    @Expose
    private String userId;
    @SerializedName("user_token")
    @Expose
    private String userToken;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("ph_no")
    @Expose
    private Integer phNo;
    @SerializedName("login_type")
    @Expose
    private Integer loginType;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("postal_code")
    @Expose
    private String postalCode;
    @SerializedName("city")
    @Expose
    private String city;

    protected User(Parcel in) {
        userId = in.readString();
        userToken = in.readString();
        userName = in.readString();
        if (in.readByte() == 0) {
            phNo = null;
        } else {
            phNo = in.readInt();
        }
        if (in.readByte() == 0) {
            loginType = null;
        } else {
            loginType = in.readInt();
        }
        address = in.readString();
        postalCode = in.readString();
        city = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(userToken);
        dest.writeString(userName);
        if (phNo == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(phNo);
        }
        if (loginType == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(loginType);
        }
        dest.writeString(address);
        dest.writeString(postalCode);
        dest.writeString(city);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String id) {
        this.userId = id;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getPhNo() {
        return phNo;
    }

    public void setPhNo(Integer phNo) {
        this.phNo = phNo;
    }

    public Integer getLoginType() {
        return loginType;
    }

    public void setLoginType(Integer loginType) {
        this.loginType = loginType;
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

}
