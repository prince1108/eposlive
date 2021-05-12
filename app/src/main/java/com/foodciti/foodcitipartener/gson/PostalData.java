package com.foodciti.foodcitipartener.gson;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PostalData implements Serializable, Parcelable {
    private Long id, info_last_updated = -1l;

    @Expose
    @SerializedName("postcode")
    private String postcode="";

    @Expose
    @SerializedName("address")
    private String address="";

    @Expose
    @SerializedName("city")
    private String city="";

    public String getHouseno() {
        return houseno;
    }

    public void setHouseno(String houseno) {
        this.houseno = houseno;
    }

    @Expose
    @SerializedName("houseno")
    private String houseno="";

    public PostalData(){}
    protected PostalData(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        if (in.readByte() == 0) {
            info_last_updated = null;
        } else {
            info_last_updated = in.readLong();
        }
        postcode = in.readString();
        address = in.readString();
        city = in.readString();
        houseno= in.readString();
    }

    public static final Creator<PostalData> CREATOR = new Creator<PostalData>() {
        @Override
        public PostalData createFromParcel(Parcel in) {
            return new PostalData(in);
        }

        @Override
        public PostalData[] newArray(int size) {
            return new PostalData[size];
        }
    };

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getInfo_last_updated() {
        return info_last_updated;
    }

    public void setInfo_last_updated(Long info_last_updated) {
        this.info_last_updated = info_last_updated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "PostalData{" +
                "id=" + id +
                ", info_last_updated=" + info_last_updated +
                ", postcode='" + postcode + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", houseno='" + houseno + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(city);
        dest.writeString(postcode);
        dest.writeString(address);
        dest.writeString(houseno);
    }
}
