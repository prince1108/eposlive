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
import com.foodciti.foodcitipartener.adapters.DriverReportAdapter;
import com.foodciti.foodcitipartener.dialogs.CustomFileDialog;
import com.foodciti.foodcitipartener.dialogs.DriverSalesDetailsDialog;
import com.foodciti.foodcitipartener.realm_entities.Driver;
import com.foodciti.foodcitipartener.realm_entities.OrderCustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.OrderPostalInfo;
import com.foodciti.foodcitipartener.realm_entities.Purchase;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.ListToCSV;
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
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class DriverReportFragment extends Fragment {
    private static final String TAG = "DriverReportFragment";

    private RecyclerView driverReportRV;
    private DriverReportAdapter driverReportAdapter;
    private Realm realm;

    final Calendar myCalendar = Calendar.getInstance();
    final String myFormat = "MM/dd/yy"; //In which you need put here
    final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
    private SimpleDateFormat simpleDateFormat;
    private EditText startDate, endDate;
    private Button search, exportCSV;
    private RadioGroup reportTypeRG;
    private RadioButton dailyReportRB, weeklyReportRB, monthlyReportRB;
    private Date mDate1, mDate2;

    private TextView time, total, cashAmount, cardAmount, deliveryCharges;
    private String poundSym;

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

    private RealmResults<Driver> driverRealmResultsAsync;

    private void updateLabel(EditText dateField) {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateField.setText(sdf.format(myCalendar.getTime()));
    }

    public static DriverReportFragment newInstance() {
        DriverReportFragment fragment = new DriverReportFragment();
        return fragment;
    }

    @Override
    public void onDestroy() {
        if (driverRealmResultsAsync != null)
            driverRealmResultsAsync.removeAllChangeListeners();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver_report, container, false);
        realm = RealmManager.getLocalInstance();
        poundSym = getString(R.string.pound_symbol);
        simpleDateFormat = new SimpleDateFormat("dd MMM, yyyy");
        driverReportRV = view.findViewById(R.id.driverReportRV);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        driverReportRV.setLayoutManager(linearLayoutManager);

        driverReportAdapter = new DriverReportAdapter(getActivity(), null);
        driverReportAdapter.setCallback((position, start, end) -> {
            final Driver driver = driverReportAdapter.getDrivers().get(position);
            DriverSalesDetailsDialog driverSalesDetailsDialog = DriverSalesDetailsDialog.newInstance(driver.getId(), start, end);
            driverSalesDetailsDialog.show(getFragmentManager(), null);
        });

        driverReportRV.setAdapter(driverReportAdapter);


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
        exportCSV = view.findViewById(R.id.export_csv);

        reportTypeRG = view.findViewById(R.id.charges_type_group);
        dailyReportRB = view.findViewById(R.id.daily);
        weeklyReportRB = view.findViewById(R.id.weekly);
        monthlyReportRB = view.findViewById(R.id.monthly);

        search.setOnClickListener(v -> {
            reportTypeRG.clearCheck();
            filter(mDate1, mDate2);
        });

        reportTypeRG.setOnCheckedChangeListener((group, checkedId) -> {
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
        });

        time = view.findViewById(R.id.time);
        total = view.findViewById(R.id.right_panel_total);
        cashAmount = view.findViewById(R.id.cash);
        cardAmount = view.findViewById(R.id.card);
        deliveryCharges = view.findViewById(R.id.delivery_charges);

        exportCSV.setOnClickListener(v -> {
            if (driverReportAdapter.getDrivers() == null)
                return;
            CustomFileDialog customFileDialog = CustomFileDialog.newInstance();
            customFileDialog.setCallback((fileDialog, file) -> {
                List<Driver> drivers = driverReportAdapter.getDrivers();
                List<List<String>> listList = new ArrayList<>();
                for (Driver driver : drivers) {
                    RealmQuery<Purchase> orderRealmQuery = realm.where(Purchase.class).equalTo("driver.id", driver.getId());
                    if (mDate1 != null && mDate2 != null)
                        orderRealmQuery.and().between("timestamp", mDate1.getTime(), mDate2.getTime());
                    double revenue = orderRealmQuery.sum("total").doubleValue();
                    final long totalDeliveries = orderRealmQuery.count();

                    List<String> strings = new ArrayList<>();

                    final String name = driver.getDriver_name();
                    final String vehicleNo = driver.getDriver_vehicle_no();
                    final String total = String.format("%.2f", revenue);
                    final String deliveries = totalDeliveries + "";
                    final StringBuilder userInfoBuilder = new StringBuilder();

                    RealmResults<Purchase> orders = orderRealmQuery.findAll();
                    if (orders != null) {
                        Number maxId = orders.max("id");
                        if (maxId == null)
                            return;
                        OrderCustomerInfo customerInfo = orderRealmQuery.findAll().where().equalTo("id", maxId.longValue()).findFirst().getOrderCustomerInfo();

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
                    }

                    strings.add(name);
                    strings.add(vehicleNo);
                    strings.add(total);
                    strings.add(deliveries);
                    strings.add(userInfoBuilder.toString());

                    listList.add(strings);
                }
                List<String> headers = new ArrayList<>();
                headers.add("Driver Name");
                headers.add("Vehicle No");
                headers.add("Total Collection");
                headers.add("Total Deliveries");
                headers.add("Last Delivery");

                StringBuilder salesDateSB = new StringBuilder("driver_report_");
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


        dailyReportRB.setChecked(true);
        return view;
    }

    private void resetFields() {
        time.setText("NA");
        total.setText("NA");
        cashAmount.setText("NA");
        cardAmount.setText("NA");
        deliveryCharges.setText("NA");
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
        driverReportAdapter.setDrivers(null);
        driverReportAdapter.notifyDataSetChanged();
        RealmQuery<Purchase> realmQueryForTotal = realm.where(Purchase.class).isNotNull("driver");
        RealmQuery<Purchase> realmQueryForCash = realm.where(Purchase.class).equalTo("paymentMode", Constants.PAYMENT_TYPE_CASH).isNotNull("driver");
        RealmQuery<Purchase> realmQueryForCard = realm.where(Purchase.class).equalTo("paymentMode", Constants.PAYMENT_TYPE_CARD).isNotNull("driver");
        RealmQuery<Purchase> realmQueryForDeliveryCharges = realm.where(Purchase.class).isNotNull("driver");

        if (start != null) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(start);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 1);
            final Date startDateMin = calendar.getTime();

            if (end == null) {
                Log.e(TAG, "-------------mDate2 is NULL");
                end = calendar.getTime();
            }
            calendar.setTime(end);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            final Date endDateMax = calendar.getTime();

            realmQueryForTotal.and().between("timestamp", startDateMin.getTime(), endDateMax.getTime());

            StringBuilder salesDateSB = new StringBuilder();
            salesDateSB.append(" (").append(simpleDateFormat.format(start));
            if (!start.equals(end)) {
                salesDateSB.append(" to " + simpleDateFormat.format(end));
            }
            salesDateSB.append(")");
            time.setText(salesDateSB);

            final double totalAmt = realmQueryForTotal.sum("total").doubleValue();
            final String formattedTotal = String.format("%.2f", totalAmt);
            total.setText(poundSym + " " + formattedTotal);

            if (totalAmt == 0) // if no sale
                return;

            Log.e(TAG, "--------------cash count: " + realmQueryForCash.count());
            final Number cashAmt = realmQueryForCash
                    .and().between("timestamp", startDateMin.getTime(), endDateMax.getTime()).sum("total");
            final Number cardAmt = realmQueryForCard
                    .and().between("timestamp", startDateMin.getTime(), endDateMax.getTime())
                    .sum("total");

            final Number deliveryChargesAmt = realmQueryForDeliveryCharges
                    .and().between("timestamp", startDateMin.getTime(), endDateMax.getTime())
                    .sum("deliveryCharges");

            if (cashAmt != null) {
                Log.e(TAG, "---------cash total: " + cardAmt);
                final String cashAmtStr = String.format("%.2f", cashAmt.doubleValue());
                cashAmount.setText(cashAmtStr);
            }

            if (cardAmt != null) {
                final String cardAmtStr = String.format("%.2f", cardAmt.doubleValue());
                cardAmount.setText(cardAmtStr);
            }

            if (deliveryChargesAmt != null) {
                final String deliveryChargesAmtStr = String.format("%.2f", deliveryChargesAmt.doubleValue());
                deliveryCharges.setText(deliveryChargesAmtStr);
            }

            driverRealmResultsAsync = realm.where(Driver.class).findAllAsync();
            driverRealmResultsAsync.addChangeListener(results -> {
                RealmList<Driver> driverRealmList = new RealmList<>();
                driverRealmList.addAll(results);
                driverReportAdapter.setDrivers(results);
                driverReportAdapter.setStart(startDateMin);
                driverReportAdapter.setEnd(endDateMax);
                driverReportAdapter.notifyDataSetChanged();
//                driverReportRV.setAdapter(driverReportAdapter);
            });

            return;
        }

        /*RealmResults<Driver> drivers=realm.where(Driver.class).findAllAsync();
        drivers.addChangeListener(results->{
            RealmList<Driver> driverRealmList=new RealmList<>();
            driverRealmList.addAll(results);
            DriverReportAdapter driverReportAdapter=new DriverReportAdapter(getActivity(), results);
            driverReportRV.setAdapter(driverReportAdapter);
        });*/
    }
}
