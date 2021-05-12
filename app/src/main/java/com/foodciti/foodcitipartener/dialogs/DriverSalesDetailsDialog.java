package com.foodciti.foodcitipartener.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.DriverSalesDetailAdapter;
import com.foodciti.foodcitipartener.realm_entities.OrderCustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.OrderPostalInfo;
import com.foodciti.foodcitipartener.realm_entities.Purchase;
import com.foodciti.foodcitipartener.utils.ListToCSV;
import com.foodciti.foodcitipartener.utils.PrintHelper;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class DriverSalesDetailsDialog extends DialogFragment {
    private static final String TAG = "DriverSalesDialog";
    private RecyclerView salesDetailsRV;
    private DriverSalesDetailAdapter driverSalesDetailAdapter;
    private RealmResults<Purchase> orderRealmResultsAsync;
    private Realm realm;
    private long driverId;
    private Date start, end;

    private static final String ARG_DRIVER_ID = "driverId";
    private static final String ARG_DATE_START = "startDate";
    private static final String ARG_DATE_END = "endDate";

    private TextView reportDate;

    private final String myFormat = "MM/dd/yy"; //In which you need put here
    private final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.UK);

    private PrintHelper printHelper;

    private DriverSalesDetailsDialog(){}

    public static DriverSalesDetailsDialog newInstance(long driverId) {
        DriverSalesDetailsDialog driverSalesDetailsDialog = new DriverSalesDetailsDialog();
        Bundle args = new Bundle();
        args.putLong(ARG_DRIVER_ID, driverId);
        driverSalesDetailsDialog.setArguments(args);
        driverSalesDetailsDialog.setCancelable(true);
        return driverSalesDetailsDialog;
    }

    public static DriverSalesDetailsDialog newInstance(long driverId, Date start, Date end) {
        DriverSalesDetailsDialog driverSalesDetailsDialog = new DriverSalesDetailsDialog();
        Bundle args = new Bundle();
        args.putLong(ARG_DRIVER_ID, driverId);
        args.putSerializable(ARG_DATE_START, start);
        args.putSerializable(ARG_DATE_END, end);
        driverSalesDetailsDialog.setArguments(args);
        return driverSalesDetailsDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            driverId = getArguments().getLong(ARG_DRIVER_ID);
            start = (Date) getArguments().getSerializable(ARG_DATE_START);
            end = (Date) getArguments().getSerializable(ARG_DATE_END);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.driver_sales_details_layout, container, false);
        realm = RealmManager.getLocalInstance();
        salesDetailsRV = view.findViewById(R.id.salesDetailsRV);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        salesDetailsRV.setLayoutManager(linearLayoutManager);
        salesDetailsRV.setHasFixedSize(true);

        reportDate = view.findViewById(R.id.report_date);
        StringBuilder reportDateSB = new StringBuilder();
        reportDateSB.append(" (").append(sdf.format(start));
        if (!start.equals(end)) {
            reportDateSB.append(" to " + sdf.format(end));
        }
        reportDateSB.append(")");
        reportDate.setText(reportDateSB);

        view.findViewById(R.id.close).setOnClickListener(v -> {
            dismiss();
        });

        RealmQuery<Purchase> orderRealmQuery = realm.where(Purchase.class).equalTo("driver.id", driverId)
                .and().between("timestamp", start.getTime(), end.getTime())
                .sort("timestamp", Sort.DESCENDING);

        orderRealmResultsAsync = orderRealmQuery.findAllAsync();
        orderRealmResultsAsync.addChangeListener(results -> {
            Log.e(TAG, "------------result size: " + results.size());
            driverSalesDetailAdapter = new DriverSalesDetailAdapter(getActivity(), results);
            salesDetailsRV.setAdapter(driverSalesDetailAdapter);
        });

        Button exportCSV = view.findViewById(R.id.export_csv);
        exportCSV.setOnClickListener(v -> {
            CustomFileDialog customFileDialog = CustomFileDialog.newInstance();
            customFileDialog.setCallback((fileDialog, folder) -> {
                List<Purchase> orders = driverSalesDetailAdapter.getOrderRealmList();
                if (orders == null)
                    return;
                List<List<String>> listList = new ArrayList<>();
                for (Purchase order : orders) {
                    List<String> strings = new ArrayList<>();
                    final String orderNo = order.getId() + "";
                    final String total = String.format("%.2f", order.getTotal());
                    final String date = sdf.format(new Date(order.getTimestamp()));
                    final String type = order.getOrderType();
                    final String paymentMode = order.getPaymentMode();
                    final String status = (order.isPaid()) ? "Completed" : "Pending";

                    final OrderCustomerInfo customerInfo = order.getOrderCustomerInfo();
                    StringBuilder userInfoBuilder = new StringBuilder();
                    if (customerInfo != null) {
                        Log.e(TAG, "-------------------userinfo not null--------------------");
                        OrderPostalInfo postalInfo = customerInfo.getOrderPostalInfo();
                        if (!customerInfo.getName().trim().isEmpty())
                            userInfoBuilder.append(customerInfo.getName()).append("; ");
                        if (!customerInfo.getPhone().trim().isEmpty())
                            userInfoBuilder.append(customerInfo.getPhone()).append("; ");
                        if (!customerInfo.getHouse_no().trim().isEmpty())
                            userInfoBuilder.append(customerInfo.getHouse_no()).append("; ");
                        if (postalInfo != null) {
                            userInfoBuilder.append(postalInfo.getA_PostCode()).append("; ");
                            userInfoBuilder.append(postalInfo.getAddress());
                        }
                    } else {
                        Log.e(TAG, "-------------------userinfo is null--------------------");
                        userInfoBuilder.append("NA");
                    }

                    strings.add(orderNo);
                    strings.add(total);
                    strings.add(userInfoBuilder.toString());
                    strings.add(date);
                    strings.add(type);
                    strings.add(paymentMode);

                    listList.add(strings);
                }
                List<String> headers = new ArrayList<>();
                headers.add("Order#");
                headers.add("Total Amount");
                headers.add("Customer");
                headers.add("Date");
                headers.add("Order Type");
                headers.add("Payment Type");

                StringBuilder salesDateSB = new StringBuilder("delivery_report_");
                salesDateSB.append("(").append(simpleDateFormat.format(start));
                if (!start.equals(end)) {
                    salesDateSB.append("__to__" + simpleDateFormat.format(end));
                }
                salesDateSB.append(")");

                File f = ListToCSV.For(listList, headers, folder, salesDateSB.toString() + ".csv");
                Log.e(TAG, "-----------file path: " + f.getPath());
                fileDialog.dismiss();
            });
            customFileDialog.show(getFragmentManager(), null);
        });

        printHelper = new PrintHelper(getActivity());
        ImageView print = view.findViewById(R.id.print);
        print.setOnClickListener(v -> {
            printHelper.printDriverReport(driverId, start, end);
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "----------------onDestroyView---------------------");
        if (orderRealmResultsAsync != null)
            orderRealmResultsAsync.removeAllChangeListeners();
        super.onDestroyView();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.98);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.98);
            dialog.getWindow().setLayout(width, height);
        }
    }
}
