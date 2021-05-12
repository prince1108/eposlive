package com.foodciti.foodcitipartener.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.CustomerReportsAdapter;
import com.foodciti.foodcitipartener.dialogs.CustomFileDialog;
import com.foodciti.foodcitipartener.realm_entities.CustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.Order;
import com.foodciti.foodcitipartener.realm_entities.OrderCustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.PostalInfo;
import com.foodciti.foodcitipartener.realm_entities.Purchase;
import com.foodciti.foodcitipartener.utils.ListToCSV;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class CustomerReportsFragment extends Fragment {
    private static final String TAG = "CustomerReportsFragment";

    private RecyclerView customerReportRV;
    private CustomerReportsAdapter customerReportsAdapter;
    private Realm realm;

    private RealmResults<Purchase> orderRealmResultsAsync;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");

    public static CustomerReportsFragment newInstance() {
        CustomerReportsFragment fragment = new CustomerReportsFragment();
        return fragment;
    }

    @Override
    public void onDestroy() {
        if (orderRealmResultsAsync != null)
            orderRealmResultsAsync.removeAllChangeListeners();
        super.onDestroy();
    }

    private CustomerInfo toCustomerInfo(OrderCustomerInfo orderCustomerInfo) {
        CustomerInfo customerInfo = realm.where(CustomerInfo.class).equalTo("phone", orderCustomerInfo.getPhone()).findFirst();
        return customerInfo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_reports, container, false);
        customerReportRV = view.findViewById(R.id.customerReportRV);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        customerReportRV.setLayoutManager(linearLayoutManager);
        realm = RealmManager.getLocalInstance();

        RealmQuery<Purchase> orderRealmQuery = realm.where(Purchase.class).isNotNull("orderCustomerInfo");
        Log.e(TAG, "--------------total customers: " + orderRealmQuery.count());
        orderRealmResultsAsync = orderRealmQuery.findAllAsync();
        orderRealmResultsAsync.addChangeListener(results -> {
            RealmList<CustomerInfo> customerInfoRealmList = new RealmList<>();
            for (Purchase order : results) {
                CustomerInfo customerInfo = toCustomerInfo(order.getOrderCustomerInfo());
                if (!customerInfoRealmList.contains(customerInfo) && customerInfo!=null)
                    customerInfoRealmList.add(customerInfo);
            }
            customerReportsAdapter = new CustomerReportsAdapter(getActivity(), customerInfoRealmList);
            customerReportRV.setAdapter(customerReportsAdapter);
        });

        Button exportCSV = view.findViewById(R.id.export_csv);
        exportCSV.setOnClickListener(v -> {
            if (customerReportsAdapter.getCustomerInfoList() == null)
                return;


            CustomFileDialog customFileDialog = CustomFileDialog.newInstance();
            customFileDialog.setCallback((fileDialog, file) -> {
                List<CustomerInfo> customerInfos = customerReportsAdapter.getCustomerInfoList();
                List<List<String>> listList = new ArrayList<>();
                for (CustomerInfo c : customerInfos) {
                    RealmQuery<Purchase> realmQuery = realm.where(Purchase.class).equalTo("orderCustomerInfo.phone", c.getPhone());
                    if (realmQuery.count() == 0)
                        return;

                    List<String> strings = new ArrayList<>();
                    final String name = c.getName();
                    final StringBuilder userInfoBuilder = new StringBuilder();
                    if (c != null) {
                        Log.e(TAG, "-------------------userinfo not null--------------------");
                        PostalInfo postalInfo = c.getPostalInfo();
                        /*if (!c.getName().trim().isEmpty())
                            userInfoBuilder.append(c.getName()).append("; ");
                        if (!c.getPhone().trim().isEmpty())
                            userInfoBuilder.append(c.getPhone()).append("; ");*/
                        if (!c.getHouse_no().trim().isEmpty())
                            userInfoBuilder.append(c.getHouse_no()).append("; ");
                        if (postalInfo != null) {
                            userInfoBuilder.append(postalInfo.getA_PostCode()).append("; ");
                            userInfoBuilder.append(postalInfo.getAddress());
                        }
                    } else {
                        Log.e(TAG, "-------------------userinfo is null--------------------");
                        userInfoBuilder.append("NA");
                    }

                    final String phone = c.getPhone();
                    final long totalOrders = realmQuery.count();
                    final String totalOrderStr = totalOrders + "";
                    String lastOrderStr = "";

                    Number lastOrder = realmQuery.max("id");
                    long lastOrderId = -1;
                    if (lastOrder != null) {
                        lastOrderId = lastOrder.longValue();
                        Log.e(TAG, "-------------lastorderId: " + lastOrderId);
                        long timeStamp = realmQuery.and().equalTo("id", lastOrderId).findFirst().getTimestamp();
                        lastOrderStr = simpleDateFormat.format(new Date(timeStamp));
                    }

                    strings.add(name);
                    strings.add(userInfoBuilder.toString());
                    strings.add(phone);
                    strings.add(totalOrderStr);
                    strings.add(lastOrderStr);

                    listList.add(strings);
                }
                List<String> headers = new ArrayList<>();
                headers.add("Name");
                headers.add("Address");
                headers.add("Contact No");
                headers.add("Total Orders");
                headers.add("Last Order");

                StringBuilder salesDateSB = new StringBuilder("customer_report_");
                salesDateSB.append("(").append(simpleDateFormat.format(new Date()));
                /*if (!mDate1.equals(mDate2)) {
                    salesDateSB.append("__to__" + simpleDateFormat.format(mDate2));
                }*/
                salesDateSB.append(")");

                File f = ListToCSV.For(listList, headers, file, salesDateSB + ".csv");
                Log.e(TAG, "-----------file path: " + f.getPath());
                fileDialog.dismiss();
            });
            customFileDialog.show(getFragmentManager(), null);
        });
        return view;
    }


}
