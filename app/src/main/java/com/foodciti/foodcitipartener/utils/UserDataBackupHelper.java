/*
package com.foodciti.foodcitipartener.utils;

import android.app.Activity;
import android.util.Log;

import com.foodciti.foodcitipartener.gson.PostalData;
import com.foodciti.foodcitipartener.gson.UserData;
import com.foodciti.foodcitipartener.realm_entities.CustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.PostalInfo;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class UserDataBackupHelper extends BackupHelper {
    private static final String TAG = "UserDataBackupHelper";
    private Activity context;

    public UserDataBackupHelper(Activity context) {
        super(context);
        this.context = context;
    }

    @Override
    public List<File> backup() {
        List<File> files = new ArrayList<>();
        try {
            List<UserData> userData = new ArrayList<>();
            List<PostalData> postalData = new ArrayList<>();
            for (CustomerInfo u : realm.where(CustomerInfo.class).findAll()) {
                UserData uData = new UserData();
                uData.setId(u.getId());
                String name = (u.getName() == null) ? "" : u.getName().trim();
                uData.setName(name);
                String phone = (u.getPhone() == null) ? "" : u.getPhone().trim();
                uData.setPhone(u.getPhone().trim());
                String house_no = (u.getHouse_no() == null) ? "" : u.getHouse_no().trim();
                uData.setHouse_no(house_no);
                uData.setPostcode(u.getPostalInfo().getId());
                String remarks = (u.getRemarks() == null) ? "" : u.getRemarks().trim();
                uData.setRemarks(remarks);
                String remarkStatus = (u.getRemarkStatus() == null) ? "" : u.getRemarkStatus().trim();
                uData.setRemarkStatus(remarkStatus);
                uData.setUser_visited_date_time(u.getUser_visited_date_time());
                userData.add(uData);
            }
            files.add(serialize(userData, "userdata.json", false));

            for (PostalInfo p : realm.where(PostalInfo.class).findAll()) {
                PostalData pData = new PostalData();
                pData.setId(p.getId());
                pData.setPostcode(p.getA_PostCode());
                pData.setAddress(p.getAddress());
                postalData.add(pData);
            }
            files.add(serialize(postalData, "postaldata.json", false));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }

    @Override
    public List<File> backup(boolean append) {
        List<File> files = new ArrayList<>();
        try {
            List<UserData> userData = new ArrayList<>();
            List<PostalData> postalData = new ArrayList<>();
            List<CustomerInfo> customerInfoList;

            */
/*final Type USERDATA = new TypeToken<List<UserData>>() {
            }.getType();
            List<UserData> userDataList = this.<UserData>deserialize(USERDATA, "userdata.json");
            if(userDataList==null)
                userDataList=new ArrayList<>();
            if(userDataList.size()>0) {
                UserData lastUser = userDataList.get(userDataList.size() - 1);
                customerInfoList = realm.where(CustomerInfoActivity.class).greaterThan("id", lastUser.getId())
                        .or().greaterThan("info_last_updated", lastUser.getInfo_last_updated()).findAll();
            }
            else*//*

            customerInfoList = realm.where(CustomerInfo.class).findAll();
            if (customerInfoList != null) {
                for (CustomerInfo u : customerInfoList) {
                    UserData uData = new UserData();
                    uData.setId(u.getId());
                    String name = (u.getName() == null) ? "" : u.getName().trim();
                    uData.setName(name);
                    String phone = (u.getPhone() == null) ? "" : u.getPhone().trim();
                    uData.setPhone(phone);
                    String house_no = (u.getHouse_no() == null) ? "" : u.getHouse_no().trim();
                    uData.setHouse_no(house_no);
                    if (u.getPostalInfo() != null)
                        uData.setPostcode(u.getPostalInfo().getId());
                    else
                        uData.setPostcode(-1l);
                    String remarks = (u.getRemarks() == null) ? "" : u.getRemarks().trim();
                    uData.setRemarks(remarks);
                    String remarkStatus = (u.getRemarkStatus() == null) ? "" : u.getRemarkStatus().trim();
                    uData.setRemarkStatus(remarkStatus);
                    uData.setUser_visited_date_time(u.getUser_visited_date_time());
                    uData.setInfo_last_updated(u.getInfo_last_updated());
//                    userDataList.add(uData);
                    userData.add(uData);
                }
//                files.add(serialize(userDataList, "userdata.json", append));
                files.add(serialize(userData, "userdata.json", append));
            } else
                Log.e(TAG, "--------userdata already latest-----------------");


            List<PostalInfo> postalInfoList;
            postalInfoList = realm.where(PostalInfo.class).findAll();
            if (postalInfoList != null) {
                for (PostalInfo p : postalInfoList) {
                    PostalData pData = new PostalData();
                    pData.setId(p.getId());
                    pData.setPostcode(p.getA_PostCode());
                    pData.setAddress(p.getAddress());
                    pData.setInfo_last_updated(p.getInfo_last_updated());
//                    postalDataList.add(pData);
                    postalData.add(pData);
                }
//                files.add(serialize(postalDataList, "postaldata.json", append));
                files.add(serialize(postalData, "postaldata.json", append));
            } else
                Log.e(TAG, "--------postaldata already latest-----------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }

    @Override
    public Boolean restore() {
        AtomicReference<Boolean> success = new AtomicReference<>(false);
        realm.executeTransaction(r -> {
            try {
                final Type USERDATA = new TypeToken<List<UserData>>() {
                }.getType();
                final Type POSTALDATA = new TypeToken<List<PostalData>>() {
                }.getType();
                List<UserData> userData = this.<UserData>deserialize(USERDATA, "userdata.json");
                List<PostalData> postalData = this.<PostalData>deserialize(POSTALDATA, "postaldata.json");
                Log.e(TAG, "--------------------userdata: " + userData.size());
                Log.e(TAG, "--------------------postaldata: " + postalData.size());

                for (PostalData p : postalData) {
                    PostalInfo postalInfo = r.where(PostalInfo.class).equalTo("id", p.getId()).findFirst();
                    if (postalInfo != null) {
                        if (postalInfo.getInfo_last_updated() < p.getInfo_last_updated()) {
                            postalInfo.setA_PostCode(p.getPostcode().trim());
                            postalInfo.setAddress(p.getAddress().trim());
                            postalInfo.setInfo_last_updated(p.getInfo_last_updated());
                        }
                        continue;
                    }
                    postalInfo = r.createObject(PostalInfo.class, p.getId());
                    postalInfo.setA_PostCode(p.getPostcode().trim());
                    postalInfo.setAddress(p.getAddress().trim());
                    postalInfo.setInfo_last_updated(p.getInfo_last_updated());
                }

                for (UserData u : userData) {
                    CustomerInfo customerInfo = r.where(CustomerInfo.class).equalTo("id", u.getId()).findFirst();
                    if (customerInfo != null) {
                        if (customerInfo.getInfo_last_updated() < u.getInfo_last_updated()) {
                            customerInfo.setName(u.getName().trim());
                            customerInfo.setPhone(u.getPhone().trim());
                            customerInfo.setHouse_no(u.getHouse_no().trim());
                            customerInfo.setPostalInfo(r.where(PostalInfo.class).equalTo("id", u.getPostcode()).findFirst());
                            customerInfo.setRemarks(u.getRemarks().trim());
                            customerInfo.setRemarkStatus(u.getRemarkStatus().trim());
                            customerInfo.setUser_visited_date_time(u.getUser_visited_date_time());
                            customerInfo.setInfo_last_updated(u.getInfo_last_updated());
                        }
                        continue;
                    }
                    customerInfo = r.createObject(CustomerInfo.class, u.getId());
                    customerInfo.setName(u.getName().trim());
                    customerInfo.setPhone(u.getPhone().trim());
                    customerInfo.setHouse_no(u.getHouse_no().trim());
                    customerInfo.setPostalInfo(r.where(PostalInfo.class).equalTo("id", u.getPostcode()).findFirst());
                    customerInfo.setRemarks(u.getRemarks().trim());
                    customerInfo.setRemarkStatus(u.getRemarkStatus().trim());
                    customerInfo.setUser_visited_date_time(u.getUser_visited_date_time());
                    customerInfo.setInfo_last_updated(u.getInfo_last_updated());
                }

                success.set(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return success.get();
    }
}
*/
