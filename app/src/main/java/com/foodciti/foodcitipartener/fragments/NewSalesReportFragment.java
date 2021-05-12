package com.foodciti.foodcitipartener.fragments;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.SalesOrderAdapter;
import com.foodciti.foodcitipartener.dialogs.CustomFileDialog;
import com.foodciti.foodcitipartener.realm_entities.OrderCustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.OrderPostalInfo;
import com.foodciti.foodcitipartener.realm_entities.Purchase;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.ListToCSV;
import com.foodciti.foodcitipartener.utils.PrintHelper;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class NewSalesReportFragment extends Fragment {
    private static final String TAG = "NewSalesReportFragment";

    private String poundSym;
    private RecyclerView salesReportRV;

    private SimpleDateFormat simpleDateFormat;
    private TextView time, total, discount, delivery_charges, service_charges, total_order_type_collection, total_order_type_delivery, total_order_type_table;
    private Realm realm;

    final Calendar myCalendar = Calendar.getInstance();
    final String myFormat = "MM/dd/yy"; //In which you need put here
    final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
    private EditText startDate, endDate;
    private Button search, exportCsv;
    private View print;
    private RadioGroup reportTypeRG;
    private RadioButton dailyReportRB, weeklyReportRB, monthlyReportRB;
    private Date mDate1, mDate2;

    // keeping in global scope to prevent garbage collection
    private RealmResults<Purchase> orderRealmResultsAsync;
    private SalesOrderAdapter salesOrderAdapter = null;

    private PrintHelper printHelper;

    public static final int OPEN_FILE_DIALOG = 11;

    final DatePickerDialog.OnDateSetListener dateSetListener1 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            myCalendar.set(Calendar.HOUR_OF_DAY, 23);
            myCalendar.set(Calendar.MINUTE, 59);
            mDate1 = myCalendar.getTime();
            updateLabel(startDate);
        }
    };

    final DatePickerDialog.OnDateSetListener dateSetListener2 = (view, year, monthOfYear, dayOfMonth) -> {
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        myCalendar.set(Calendar.HOUR_OF_DAY, 23);
        myCalendar.set(Calendar.MINUTE, 59);
        mDate2 = myCalendar.getTime();
        updateLabel(endDate);
    };

    public static NewSalesReportFragment newInstance() {
        NewSalesReportFragment fragment = new NewSalesReportFragment();
        return fragment;
    }

    private void updateLabel(EditText dateField) {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateField.setText(sdf.format(myCalendar.getTime()));
        /*if (sales_by_date.isChecked()) {
            if (!date1.getText().toString().trim().isEmpty())
                go.setEnabled(true);
        }
        if (sales_between_date.isChecked()) {
            if (!date1.getText().toString().trim().isEmpty() && !date2.getText().toString().trim().isEmpty())
                go.setEnabled(true);
        }*/
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "---------------onPause--------------------------");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_sales_report, container, false);
        ;
        poundSym = getString(R.string.pound_symbol);
        realm = RealmManager.getLocalInstance();
        simpleDateFormat = new SimpleDateFormat("dd MMM, yyyy");
        startDate = view.findViewById(R.id.startDate);
        startDate.setOnClickListener(v -> {
            new DatePickerDialog(getActivity(), dateSetListener1, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
        endDate = view.findViewById(R.id.endDate);
        endDate.setOnClickListener(v -> {
            new DatePickerDialog(getActivity(), dateSetListener2, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
        search = view.findViewById(R.id.search);
        exportCsv = view.findViewById(R.id.export_csv);
        exportCsv.setOnClickListener(v -> {

            CustomFileDialog customFileDialog = CustomFileDialog.newInstance();
            customFileDialog.setCallback((fileDialog, file) -> {
                List<Purchase> orders = salesOrderAdapter.getOrders();
                List<List<String>> listList = new ArrayList<>();
                for (Purchase order : orders) {
                    List<String> strings = new ArrayList<>();
                    final String type = order.getOrderType();
                    final String orderNo = order.getId() + "";
                    final String date = sdf.format(new Date(order.getTimestamp()));
                    final String paymentMode = order.getPaymentMode();
                    final String status = (order.isPaid()) ? "Completed" : "Pending";
                    final String total = String.format("%.2f", order.getTotal());

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

                    strings.add(type);
                    strings.add(orderNo);
                    strings.add(date);
                    strings.add(paymentMode);
                    strings.add(status);
                    strings.add(total);
                    strings.add(userInfoBuilder.toString());

                    listList.add(strings);
                }
                List<String> headers = new ArrayList<>();
                headers.add("Type");
                headers.add("Order No");
                headers.add("Date");
                headers.add("Payment Mode");
                headers.add("Status");
                headers.add("Total");
                headers.add("Customer");

                StringBuilder salesDateSB = new StringBuilder();
                salesDateSB.append("(").append(simpleDateFormat.format(mDate1));
                if (!mDate1.equals(mDate2)) {
                    salesDateSB.append("__to__" + simpleDateFormat.format(mDate2));
                }
                salesDateSB.append(")");

                File f = ListToCSV.For(listList, headers, file, salesDateSB.toString() + ".csv");
                Log.e(TAG, "-----------file path: " + f.getPath());
                fileDialog.dismiss();
            });
            customFileDialog.show(getFragmentManager(), null);
        });

        time = view.findViewById(R.id.time);
        total = view.findViewById(R.id.right_panel_total);
        discount = view.findViewById(R.id.cash);
        delivery_charges = view.findViewById(R.id.card);
        service_charges = view.findViewById(R.id.delivery_charges);
        total_order_type_collection = view.findViewById(R.id.total_order_type_collection);
        total_order_type_delivery = view.findViewById(R.id.delivered_by);
        total_order_type_table = view.findViewById(R.id.total_order_type_table);

        salesReportRV = view.findViewById(R.id.salesRV);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        salesReportRV.setLayoutManager(linearLayoutManager);
        salesReportRV.setHasFixedSize(true);

        salesOrderAdapter = new SalesOrderAdapter(getActivity(), null);
        salesReportRV.setAdapter(salesOrderAdapter);

        search.setOnClickListener(v -> {
            reportTypeRG.clearCheck();
            filter(mDate1, mDate2);
        });

        reportTypeRG = view.findViewById(R.id.charges_type_group);
        dailyReportRB = view.findViewById(R.id.daily);
        weeklyReportRB = view.findViewById(R.id.weekly);
        monthlyReportRB = view.findViewById(R.id.monthly);

        reportTypeRG.setOnCheckedChangeListener((group, checkedId) -> {
            getSales(checkedId);
        });

        printHelper = new PrintHelper(getActivity());

        print = view.findViewById(R.id.print);
        print.setOnClickListener(v -> {
            printHelper.printSalesReport(new Date(), new Date());
        });


        dailyReportRB.setChecked(true);
        return view;
    }

    private void resetFields() {
        time.setText("NA");
        total.setText("NA");
        discount.setText("NA");
        delivery_charges.setText("NA");
        service_charges.setText("NA");
        total_order_type_collection.setText("NA");
        total_order_type_delivery.setText("NA");
        total_order_type_table.setText("NA");
    }

    private void getSales(int checkedId) {
        switch (checkedId) {
            case R.id.daily:
                getSalesDaily();
                break;
            case R.id.weekly:
                getSalesWeekly();
                break;
            case R.id.monthly:
                getSalesMonthly();
                break;
        }
    }

    private void getSalesDaily() {
        Calendar calendar = Calendar.getInstance();
        mDate1 = calendar.getTime();

        final String formattedDate = sdf.format(mDate1);
        startDate.setText(formattedDate);
        endDate.setText(formattedDate);

        filter(mDate1, mDate1);
    }

    private void getSalesWeekly() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK));
        mDate1 = calendar.getTime();

        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
        mDate2 = calendar.getTime();

        final String formattedDate1 = sdf.format(mDate1);
        startDate.setText(formattedDate1);

        final String formattedDate2 = sdf.format(mDate2);
        endDate.setText(formattedDate2);

        filter(mDate1, mDate2);
    }

    private void getSalesMonthly() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        mDate1 = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        mDate2 = calendar.getTime();

        final String formattedDate1 = sdf.format(mDate1);
        startDate.setText(formattedDate1);

        final String formattedDate2 = sdf.format(mDate2);
        endDate.setText(formattedDate2);

        filter(mDate1, mDate2);
    }

    private void filter(Date start, Date end) {
        resetFields();
        salesOrderAdapter.setOrders(null);
        salesOrderAdapter.notifyDataSetChanged();
        RealmQuery<Purchase> realmQuery = realm.where(Purchase.class);
        RealmQuery<Purchase> realmQueryForCollection = realm.where(Purchase.class).equalTo("orderType", Constants.TYPE_COLLECTION);
        RealmQuery<Purchase> realmQueryForDelivery = realm.where(Purchase.class).equalTo("orderType", Constants.TYPE_DELIVERY);
        RealmQuery<Purchase> realmQueryForTable = realm.where(Purchase.class).equalTo("orderType", Constants.TYPE_TABLE);

        if (start != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(start);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 1);
            Date startDateMin = calendar.getTime();

            if (end == null) {
                Log.e(TAG, "-------------mDate2 is NULL");
                end = calendar.getTime();
            }
            calendar.setTime(end);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            Date endDateMax = calendar.getTime();

            realmQuery.between("timestamp", startDateMin.getTime(), endDateMax.getTime());
            realmQueryForCollection.between("timestamp", startDateMin.getTime(), endDateMax.getTime());
            realmQueryForDelivery.between("timestamp", startDateMin.getTime(), endDateMax.getTime());
            realmQueryForTable.between("timestamp", startDateMin.getTime(), endDateMax.getTime());

            StringBuilder salesDateSB = new StringBuilder();
            salesDateSB.append(" (").append(simpleDateFormat.format(start));
            if (!start.equals(end)) {
                salesDateSB.append(" to " + simpleDateFormat.format(end));
            }
            salesDateSB.append(")");
            time.setText(salesDateSB);
        }

            /*Long ordernum = null;
            try {
                ordernum = Long.parseLong(orderNo.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (ordernum != null)
                realmQuery.and().equalTo("id", ordernum.longValue());*/

        double totalAmount = realmQuery.sum("total").doubleValue();
        String formattedTotal = String.format("%.2f", totalAmount);
        total.setText(poundSym + " " + formattedTotal);

        double discountAmount = realmQuery.sum("discount").doubleValue();
        final String formattedDiscount = String.format("%.2f", discountAmount);
        discount.setText(formattedDiscount);

        double deliveryCharges = realmQuery.sum("deliveryCharges").doubleValue();
        final String formattedDeliveryCharges = String.format("%.2f", deliveryCharges);
        delivery_charges.setText(formattedDeliveryCharges);

        double serviceCharges = realmQuery.sum("serviceCharges").doubleValue();
        final String formattedServiceCharges = String.format("%.2f", serviceCharges);
        service_charges.setText(formattedServiceCharges);

        long totalCollectionOrders = realmQueryForCollection.count();
        long totalDeliveryOrders = realmQueryForDelivery.count();
        long totalTableOrders = realmQueryForTable.count();

        total_order_type_collection.setText(totalCollectionOrders + "");
        total_order_type_delivery.setText(totalDeliveryOrders + "");
        total_order_type_table.setText(totalTableOrders + "");

        Log.e(TAG, "-------------getting results async-------------");
        orderRealmResultsAsync = realmQuery.sort("timestamp", Sort.DESCENDING).findAllAsync();
        orderRealmResultsAsync.addChangeListener(rs -> {
            Log.e(TAG, "-----------------size: " + rs.size());
//            salesReportRV.setAdapter(new SalesOrderAdapter(getActivity(), rs));
            salesOrderAdapter.setOrders(rs);
            salesOrderAdapter.notifyDataSetChanged();
        });
    }
}
