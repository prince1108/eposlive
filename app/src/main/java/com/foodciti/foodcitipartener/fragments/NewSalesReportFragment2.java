package com.foodciti.foodcitipartener.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.SalesOrderAdapter;
import com.foodciti.foodcitipartener.db.ExceptionLogger;
import com.foodciti.foodcitipartener.dialogs.CustomFileDialog;
import com.foodciti.foodcitipartener.realm_entities.Exlogger;
import com.foodciti.foodcitipartener.realm_entities.OrderCustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.OrderPostalInfo;
import com.foodciti.foodcitipartener.realm_entities.Purchase;
import com.foodciti.foodcitipartener.realm_entities.Vendor;
import com.foodciti.foodcitipartener.response.GenericResponse;
import com.foodciti.foodcitipartener.response.OrderTotal;
import com.foodciti.foodcitipartener.rest.RetroClient;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.Consts;
import com.foodciti.foodcitipartener.utils.InternetConnection;
import com.foodciti.foodcitipartener.utils.ListToCSV;
import com.foodciti.foodcitipartener.utils.Preferences;
import com.foodciti.foodcitipartener.utils.PrintUtils;
import com.foodciti.foodcitipartener.utils.RealmManager;
import com.foodciti.foodcitipartener.utils.SessionManager;

import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.content.Context.MODE_PRIVATE;
import static com.foodciti.foodcitipartener.utils.AppConfig.MyPREFERENCES;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewSalesReportFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewSalesReportFragment2 extends Fragment {
    private static final String TAG = "SalesReportFragment";

    private String poundSym;
    private RecyclerView salesReportRV;

    private SimpleDateFormat simpleDateFormat;
    private TextView totalTotal, totalInclTaxes, totalExclTaxes, collectionTotal, collectionInclTaxes, collectionExclTaxes, deliveryTotal, deliveryInclTaxes, deliveryExclTaxes,
            cardTotal, cardInclTaxes, cardExclTaxes, cashTotal, phone_total, cashInclTaxes, phone_incl_taxes, cashExclTaxes, phone_excl_taxes, tableTotal, tableInclTaxes, tableExclTaxes;
    private Realm realm;

    private TextView orderTotal, collectionOrderNumber, collectionOrderTotal,
            deliveryOrderNumber, deliveryOrderTotal, cardOrderNumber, cardOrderTotal,
            cashOrderNumber, cashOrderTotal, orderNumberText, paypalNumberText, paypalOrderTotal, worldpayNumberText, worldpayOrderTotal;

    final Calendar myCalendar = Calendar.getInstance();
    final String myFormat = "MM/dd/yy"; //In which you need put here
    final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
    private EditText startDate, endDate;
    private Button search, exportCsv, printSummary, endOfDay,eod_online;
    private ToggleButton toggleDetails;
    private View print;
    private RadioGroup reportTypeRG;
    private RadioButton dailyReportRB;
    private Date mDate1, mDate2;
    private long eod_prev = 1;
    private ConstraintLayout saleDetailsPanel;
    private String onlineOrderCount = "0.0", sum_of_onlineOrder = "0.0";
    // keeping in global scope to prevent garbage collection
//    private RealmResults<Order> orderRealmResultsAsync;
    private RealmResults<Purchase> orderRealmResultsAsync;
    GenericResponse<OrderTotal> mResponse;
    private SalesOrderAdapter salesOrderAdapter = null;
    //Preferences to save the last EOD
    protected SharedPreferences sharedPreferences;
    protected SharedPreferences.Editor editor;
    private OnEodRun eodRunListener;
    LocalBroadcastManager localBroadcastManager;
    ConstraintLayout constraintLayout;
    ExceptionLogger logger;

    public interface OnEodRun {
        void onEodRun();
    }

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


    public static NewSalesReportFragment2 newInstance() {
        NewSalesReportFragment2 fragment = new NewSalesReportFragment2();
        return fragment;
    }

    private void updateLabel(EditText dateField) {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateField.setText(sdf.format(myCalendar.getTime()));
    }

    private NewSalesReportFragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewSalesReportFragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static NewSalesReportFragment2 newInstance(String param1, String param2) {
        NewSalesReportFragment2 fragment = new NewSalesReportFragment2();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SessionManager.get(getActivity()).isLoggedIn()) {
            showOrderTotalAndPrint();
        }
    }

    @Override
    public void onDestroy() {
        if (orderRealmResultsAsync != null)
            orderRealmResultsAsync.removeAllChangeListeners();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        sharedPreferences = getActivity().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        editor = getActivity().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        logger = new ExceptionLogger(getActivity());
        View view = inflater.inflate(R.layout.fragment_new_sales_report_fragment2, container, false);
        poundSym = getString(R.string.pound_symbol);
        realm = RealmManager.getLocalInstance();
        simpleDateFormat = new SimpleDateFormat("dd MMM, yyyy");
        initViews(view);
        return view;
    }

    private void initViews(View rootView) {
        startDate = rootView.findViewById(R.id.startDate);
        startDate.setOnClickListener(v -> {
            new DatePickerDialog(getActivity(), dateSetListener1, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
        endDate = rootView.findViewById(R.id.endDate);
        endDate.setOnClickListener(v -> {
            new DatePickerDialog(getActivity(), dateSetListener2, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
        search = rootView.findViewById(R.id.search);
        constraintLayout = rootView.findViewById(R.id.eposonline_body2);
        if (SessionManager.get(getActivity()).isLoggedIn()) {
            constraintLayout.setVisibility(View.VISIBLE);
        } else {
            constraintLayout.setVisibility(View.GONE);
        }
        exportCsv = rootView.findViewById(R.id.export_csv);
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

        totalTotal = rootView.findViewById(R.id.total_total);
        totalInclTaxes = rootView.findViewById(R.id.total_incl_taxes);
        totalExclTaxes = rootView.findViewById(R.id.total_excl_taxes);
        totalExclTaxes.setVisibility(View.GONE);
        collectionTotal = rootView.findViewById(R.id.collection_total);
        collectionInclTaxes = rootView.findViewById(R.id.collection_incl_taxes);
        collectionExclTaxes = rootView.findViewById(R.id.collection_excl_taxes);
        collectionExclTaxes.setVisibility(View.GONE);
        deliveryTotal = rootView.findViewById(R.id.delivery_total);
        deliveryInclTaxes = rootView.findViewById(R.id.delivery_incl_taxes);
        deliveryExclTaxes = rootView.findViewById(R.id.delivery_excl_taxes);
        deliveryExclTaxes.setVisibility(View.GONE);
        tableTotal = rootView.findViewById(R.id.table_total);
        tableInclTaxes = rootView.findViewById(R.id.table_incl_taxes);
        tableExclTaxes = rootView.findViewById(R.id.table_excl_taxes);
        tableExclTaxes.setVisibility(View.GONE);
        cardTotal = rootView.findViewById(R.id.card_total);
        cardInclTaxes = rootView.findViewById(R.id.card_incl_taxes);
        cardInclTaxes.setVisibility(View.GONE);
        cardExclTaxes = rootView.findViewById(R.id.card_excl_taxes);
        cashTotal = rootView.findViewById(R.id.cash_total);
        phone_total = rootView.findViewById(R.id.phone_total);
        cashInclTaxes = rootView.findViewById(R.id.cash_incl_taxes);
        phone_excl_taxes = rootView.findViewById(R.id.phone_excl_taxes);
        phone_excl_taxes.setVisibility(View.GONE);
        phone_incl_taxes = rootView.findViewById(R.id.phone_incl_taxes);
        cashExclTaxes = rootView.findViewById(R.id.cash_excl_taxes);
        cashExclTaxes.setVisibility(View.GONE);
        /********************************Online Views****************************/
        orderTotal = rootView.findViewById(R.id.orderTotal);
        orderNumberText = rootView.findViewById(R.id.orderNumberText);
        collectionOrderNumber = rootView.findViewById(R.id.collectionOrderNumber);
        collectionOrderTotal = rootView.findViewById(R.id.collectionOrderTotal);
        deliveryOrderNumber = rootView.findViewById(R.id.deliveryOrderNumber);
        deliveryOrderTotal = rootView.findViewById(R.id.deliveryOrderTotal);
        cardOrderNumber = rootView.findViewById(R.id.cardOrderNumber);
        cardOrderTotal = rootView.findViewById(R.id.cardOrderTotal);
        cashOrderNumber = rootView.findViewById(R.id.cashOrderNumber);
        cashOrderTotal = rootView.findViewById(R.id.cashOrderTotal);
        paypalNumberText = rootView.findViewById(R.id.paypalOrderNumber);
        paypalOrderTotal = rootView.findViewById(R.id.paypalOrderTotal);
        worldpayNumberText = rootView.findViewById(R.id.worldpayOrderNumber);
        worldpayOrderTotal = rootView.findViewById(R.id.worldpayOrderTotal);

        /************************************************************************/
        saleDetailsPanel = rootView.findViewById(R.id.sale_details_panel);
        salesReportRV = rootView.findViewById(R.id.salesRV);
        printSummary = rootView.findViewById(R.id.summary);
        printSummary.setOnClickListener(v -> {
//            printHelper.printSalesReport(new Date(), new Date());
            printSalesReport(mDate1, mDate2);
            if (SessionManager.get(getActivity()).isLoggedIn()) {
                if (mResponse != null)
                    printOnlineReport(mResponse);
            }

        });
        endOfDay = rootView.findViewById(R.id.eod);
        eod_online= rootView.findViewById(R.id.eod_online);
        eod_online.setOnClickListener(view -> {
            if (SessionManager.get(getActivity()).isLoggedIn()) {
                if (mResponse != null)
                    printOnlineReport(mResponse);
            }
        });
        endOfDay.setOnClickListener(v -> {
            long eod = sharedPreferences.getLong(Preferences.LAST_EOD_TIME, 1);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("End of the day");
            builder.setMessage("Are you sure you want to do end of the day?");
            // add a button
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // do something like...
                    if (eod != 1) {
                        Date startDate = new Date(eod);
                        Date enddate = new Date();
                        //filter(startDate,enddate);
                        printSalesReport(startDate, enddate);
                        eod_prev = eod;
                        editor.putLong(Preferences.LAST_EOD_TIME, enddate.getTime()).commit();
                        long eodTest = sharedPreferences.getLong(Preferences.LAST_EOD_TIME, 1);
                        Log.e(TAG, "EOD Stored in Preference " + String.valueOf(new Date(eodTest)));
                    } else {
                        Date startdate = new Date(eod);
                        Date enddate = new Date();
                        editor.putLong(Preferences.LAST_EOD_TIME, enddate.getTime()).commit();
                        //filter(startdate,enddate);
                        printSalesReport(startdate, enddate);
//                        if (SessionManager.get(getActivity()).isLoggedIn()) {
//                            if (mResponse != null)
//                                printOnlineReport(mResponse);
//                        }

                    }
                    eodRunListener.onEodRun();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();
            //printEodReport();
        });
        toggleDetails = rootView.findViewById(R.id.toggle_details);
        toggleDetails.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                saleDetailsPanel.setVisibility(View.VISIBLE);
            else
                saleDetailsPanel.setVisibility(View.GONE);
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        salesReportRV.setLayoutManager(linearLayoutManager);
        salesReportRV.setHasFixedSize(true);

        salesOrderAdapter = new SalesOrderAdapter(getActivity(), null);
        salesReportRV.setAdapter(salesOrderAdapter);

        search.setOnClickListener(v -> {
            reportTypeRG.clearCheck();
            filter(mDate1, mDate2);
        });

        reportTypeRG = rootView.findViewById(R.id.charges_type_group);
        dailyReportRB = rootView.findViewById(R.id.daily);
//        weeklyReportRB = rootView.findViewById(R.id.weekly);
//        monthlyReportRB = rootView.findViewById(R.id.monthly);

        reportTypeRG.setOnCheckedChangeListener((group, checkedId) -> {
            Log.d(TAG, "--------------------checked Id: " + checkedId);
            RadioButton rb = rootView.findViewById(checkedId);
            if (rb != null && rb.isChecked())
                getSales(checkedId);
        });

//        printHelper = new PrintHelper(getActivity());

        dailyReportRB.setChecked(true);

    }

    private void resetFields() {
        totalTotal.setText("NA");
        totalInclTaxes.setText("NA");
        totalExclTaxes.setText("NA");
        collectionTotal.setText("NA");
        collectionInclTaxes.setText("NA");
        collectionExclTaxes.setText("NA");
        deliveryTotal.setText("NA");
        deliveryInclTaxes.setText("NA");
        deliveryExclTaxes.setText("NA");
        cardTotal.setText("NA");
        cardInclTaxes.setText("NA");
        cardExclTaxes.setText("NA");
        cashTotal.setText("NA");
        cashInclTaxes.setText("NA");
        cashExclTaxes.setText("NA");
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
        Log.d(TAG, "-------------------------getSalesDaily--------------------------");
        Calendar calendar = GregorianCalendar.getInstance();
        long eod = sharedPreferences.getLong(Preferences.LAST_EOD_TIME, new Date().getTime() - 86400000);
        Log.e("DateInLong", new Date(eod).toString());
        if (eod != 1) {
            mDate1 = new Date(eod);
            mDate2 = new Date();
        } else {
            mDate1 = calendar.getTime();
            mDate2 = calendar.getTime();
        }

        final String formattedDate = sdf.format(mDate1);
        startDate.setText(formattedDate);
        endDate.setText(sdf.format(mDate2));

        filter(mDate1, mDate2);
    }

    private void getSalesWeekly() {
        Log.d(TAG, "---------------------------getSalesWeekly------------------------");
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK));

        mDate1 = calendar.getTime();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
        mDate2 = calendar.getTime();

        final String formattedDate1 = sdf.format(mDate1);
        startDate.setText(formattedDate1);
        Log.e("StartDateWeekly", formattedDate1);
        final String formattedDate2 = sdf.format(mDate2);
        endDate.setText(formattedDate2);
        Log.e("ENdDateWeekLy", formattedDate2);

        filter(mDate1, mDate2);
    }

    private void getSalesMonthly() {
        Log.d(TAG, "----------------------------getSalesMonthly-----------------------");
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
        Log.d(TAG, "filter: start:- " + start + "\t end:- " + end);
        resetFields();
        salesOrderAdapter.setOrders(null);
        salesOrderAdapter.notifyDataSetChanged();
        RealmQuery<Purchase> realmQuery = realm.where(Purchase.class);
        RealmQuery<Purchase> realmQueryForCollection = realm.where(Purchase.class).equalTo("orderType", Constants.TYPE_COLLECTION);
        RealmQuery<Purchase> realmQueryForDelivery = realm.where(Purchase.class).equalTo("orderType", Constants.TYPE_DELIVERY);
        RealmQuery<Purchase> realmQueryForTable = realm.where(Purchase.class).equalTo("orderType", Constants.TYPE_TABLE);
        RealmQuery<Purchase> realmQueryForCard = realm.where(Purchase.class).equalTo("paymentMode", Constants.PAYMENT_TYPE_CARD);
        RealmQuery<Purchase> realmQueryForCash = realm.where(Purchase.class).equalTo("paymentMode", Constants.PAYMENT_TYPE_CASH);
        RealmQuery<Purchase> realmQueryForPhone = realm.where(Purchase.class).equalTo("paymentMode", Constants.PAYMENT_TYPE_PHONE);

        if (start != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(start);
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR));
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND));
//            Date startDateMin = calendar.getTime();
            Date startDateMin = start;
            //Date startDateMin= start;
            if (end == null) {
                Log.e(TAG, "-------------mDate2 is NULL");
                end = calendar.getTime();
            }
            calendar.setTime(end);
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR));
            calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND));
//            Date endDateMax = calendar.getTime();
            Date endDateMax = end;
            //Date endDateMax=end;
/*
            realmQuery.between("timestamp", startDateMin.getTime(), endDateMax.getTime());
            realmQueryForCollection.between("timestamp", startDateMin.getTime(), endDateMax.getTime());
            realmQueryForDelivery.between("timestamp", startDateMin.getTime(), endDateMax.getTime());
            realmQueryForTable.between("timestamp", startDateMin.getTime(), endDateMax.getTime());
            realmQueryForCard.between("timestamp", startDateMin.getTime(), endDateMax.getTime());
            realmQueryForCash.between("timestamp", startDateMin.getTime(), endDateMax.getTime());

 */


            realmQuery.between("orderTimeStamp", startDateMin, endDateMax);
            realmQueryForCollection.between("orderTimeStamp", startDateMin, endDateMax);
            realmQueryForDelivery.between("orderTimeStamp", startDateMin, endDateMax);
            realmQueryForTable.between("orderTimeStamp", startDateMin, endDateMax);
            realmQueryForCard.between("orderTimeStamp", startDateMin, endDateMax);
            realmQueryForCash.between("orderTimeStamp", startDateMin, endDateMax);
            realmQueryForPhone.between("orderTimeStamp", startDateMin, endDateMax);

            StringBuilder salesDateSB = new StringBuilder();
            salesDateSB.append(" (").append(simpleDateFormat.format(start));
            if (!start.equals(end)) {
                salesDateSB.append(" to " + simpleDateFormat.format(end));
            }
            salesDateSB.append(")");
//            time.setText(salesDateSB);
        }


        totalTotal.setText(realmQuery.count() + "");
        double totalAmount = realmQuery.sum("total").doubleValue();
        double discountTotal = realmQuery.sum("discount").doubleValue();
        double extraTotal = realmQuery.sum("extra").doubleValue();
        String formattedTotal = String.format("%.2f", totalAmount + extraTotal - discountTotal);
        totalExclTaxes.setText(formattedTotal);

        double totalDeliveryCharges = realmQuery.sum("deliveryCharges").doubleValue();
        double totalServiceCharges = realmQuery.sum("serviceCharges").doubleValue();
        String formattedTotalInclTaxes = String.format("%.2f", totalAmount + totalDeliveryCharges + totalServiceCharges);
        totalInclTaxes.setText(formattedTotalInclTaxes);

        collectionTotal.setText(realmQueryForCollection.count() + "");
        double totalAmountForCollection = realmQueryForCollection.sum("total").doubleValue();
        double discountTotalForCollection = realmQueryForCollection.sum("discount").doubleValue();
        double extraTotalForCollection = realmQueryForCollection.sum("extra").doubleValue();
        String formattedTotalForCollection = String.format("%.2f", totalAmountForCollection - discountTotalForCollection + extraTotalForCollection);
        collectionExclTaxes.setText(formattedTotalForCollection);

        double totalDeliveryChargesForCollection = realmQueryForCollection.sum("deliveryCharges").doubleValue();
        double totalServiceChargesForCollection = realmQueryForCollection.sum("serviceCharges").doubleValue();
        String formattedTotalInclTaxesForCollection = String.format("%.2f", totalAmountForCollection + totalDeliveryChargesForCollection + totalServiceChargesForCollection);
        collectionInclTaxes.setText(formattedTotalInclTaxesForCollection);

        deliveryTotal.setText(realmQueryForDelivery.count() + "");
        double totalAmountForDelivery = realmQueryForDelivery.sum("total").doubleValue();
        double discountTotalForDelivery = realmQueryForDelivery.sum("discount").doubleValue();
        double extraTotalForDelivery = realmQueryForDelivery.sum("extra").doubleValue();
        String formattedTotalForDelivery = String.format("%.2f", totalAmountForDelivery - discountTotalForDelivery + extraTotalForDelivery);
        deliveryExclTaxes.setText(formattedTotalForDelivery);

        double totalDeliveryChargesForDelivery = realmQueryForDelivery.sum("deliveryCharges").doubleValue();
        double totalServiceChargesForDelivery = realmQueryForDelivery.sum("serviceCharges").doubleValue();
        String formattedTotalInclTaxesForDelivery = String.format("%.2f", totalAmountForDelivery + totalDeliveryChargesForDelivery + totalServiceChargesForDelivery);
        deliveryInclTaxes.setText(formattedTotalInclTaxesForDelivery);

        tableTotal.setText(realmQueryForTable.count() + "");
        double totalAmountForTable = realmQueryForTable.sum("total").doubleValue();
        double discountTotalForTable = realmQueryForTable.sum("discount").doubleValue();
        double extraTotalForTable = realmQueryForTable.sum("extra").doubleValue();
        String formattedTotalForTable = String.format("%.2f", totalAmountForTable - discountTotalForTable + extraTotalForTable);
        tableExclTaxes.setText(formattedTotalForTable);

        double totalDeliveryChargesForTable = realmQueryForTable.sum("deliveryCharges").doubleValue();
        double totalServiceChargesForTable = realmQueryForTable.sum("serviceCharges").doubleValue();
        String formattedTotalInclTaxesForTable = String.format("%.2f", totalAmountForTable + totalDeliveryChargesForTable + totalServiceChargesForTable);
        tableInclTaxes.setText(formattedTotalInclTaxesForTable);

        cardTotal.setText(realmQueryForCard.count() + "");
        double totalAmountForCard = realmQueryForCard.sum("total").doubleValue();
        double discountTotalForCard = realmQueryForCard.sum("discount").doubleValue();
        double extraTotalForCard = realmQueryForCard.sum("extra").doubleValue();
        String formattedTotalForCard = String.format("%.2f", totalAmountForCard - discountTotalForCard + extraTotalForCard);
        cardExclTaxes.setText(formattedTotalForCard);

        double totalDeliveryChargesForCard = realmQueryForCard.sum("deliveryCharges").doubleValue();
        double totalServiceChargesForCard = realmQueryForCard.sum("serviceCharges").doubleValue();
        String formattedTotalInclTaxesForCard = String.format("%.2f", totalAmountForCard + totalDeliveryChargesForCard + totalServiceChargesForCard);
        cardInclTaxes.setText(formattedTotalInclTaxesForCard);

        cashTotal.setText(realmQueryForCash.count() + "");
        double totalAmountForCash = realmQueryForCash.sum("total").doubleValue();
        double discountTotalForCash = realmQueryForCash.sum("discount").doubleValue();
        double extraTotalForCash = realmQueryForCash.sum("extra").doubleValue();
        String formattedTotalForCash = String.format("%.2f", totalAmountForCash - discountTotalForCash + extraTotalForCash);
        cashExclTaxes.setText(formattedTotalForCash);

        //PaybyPhone
        phone_total.setText(realmQueryForPhone.count() + "");

        double totalAmountForPhone = realmQueryForPhone.sum("total").doubleValue();
        double totalDeliveryChargesForPhone = realmQueryForPhone.sum("deliveryCharges").doubleValue();
        double totalServiceChargesForPhone = realmQueryForPhone.sum("serviceCharges").doubleValue();
        String formattedTotalInclTaxesForPhone = String.format("%.2f", totalAmountForPhone + totalDeliveryChargesForPhone + totalServiceChargesForPhone);
        phone_incl_taxes.setText(formattedTotalInclTaxesForPhone);


        double discountTotalForPhone = realmQueryForPhone.sum("discount").doubleValue();
        double extraTotalForPhone = realmQueryForCash.sum("extra").doubleValue();
        String formattedTotalForPhone = String.format("%.2f", totalAmountForPhone - discountTotalForPhone + extraTotalForPhone);
        phone_excl_taxes.setText(formattedTotalForPhone);


/////////////////////////
        double totalDeliveryChargesForCash = realmQueryForCash.sum("deliveryCharges").doubleValue();
        double totalServiceChargesForCash = realmQueryForCash.sum("serviceCharges").doubleValue();
        String formattedTotalInclTaxesForCash = String.format("%.2f", totalAmountForCash + totalDeliveryChargesForCash + totalServiceChargesForCash);
        cashInclTaxes.setText(formattedTotalInclTaxesForCash);

        Log.e(TAG, "-------------getting results async-------------");
        orderRealmResultsAsync = realmQuery.sort("timestamp", Sort.DESCENDING).findAllAsync();
        orderRealmResultsAsync.addChangeListener(rs -> {
            Log.e(TAG, "-----------------size: " + rs.size());
            salesOrderAdapter.setOrders(rs);
            salesOrderAdapter.notifyDataSetChanged();
        });
    }

    private boolean printSalesReport(Date fromDate, Date toDate) {
        String provider = "";
        String vendor = "";
        String tel_no = "";
        String pin = "";
        String loc = "";
        String vatNo = "";
        String companyNo = "";
        if (toDate == null)
            toDate = fromDate;

        Realm realm = Realm.getDefaultInstance();
        Vendor vendorInfo = realm.where(Vendor.class).findFirst();
        if (vendorInfo == null) {
            Toast.makeText(getActivity(), "Vendor Info is NULL", Toast.LENGTH_SHORT).show();
            return false;
        }
        provider = vendorInfo.getTitle();
        vendor = vendorInfo.getName();
        tel_no = vendorInfo.getTel_no();
        pin = vendorInfo.getPin();
        loc = vendorInfo.getAddress();
        vatNo = vendorInfo.getVatNo() == null ? "" : vendorInfo.getVatNo();
        companyNo = vendorInfo.getCompanyNo() == null ? "" : vendorInfo.getCompanyNo();
        final String POUND = "\u00A3";
        boolean returnValue = true;


        final String myFormat = "dd/MM/yy"; //In which you need put here
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);
        /*calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 1);*/
        Date startDateMin = calendar.getTime();

        calendar.setTime(toDate);
        /*calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);*/
        Date endDateMax = calendar.getTime();

        byte[] text = null;
        byte[] alignment = null;
        byte[] bold = null;
        byte[] size = null;
        byte[] cursorPosition = null;
//        byte[] tabs=null;
        final byte[] dividerLine = PrintUtils.getBytes("-----------------------");

        try {

            List<Byte> bytes = new LinkedList<>();

            byte[] internationalCharcters = PrintUtils.setInternationalCharcters('3');
            PrintUtils.copyBytesToList(bytes, internationalCharcters);

            cursorPosition = PrintUtils.setCursorPosition(0);
            alignment = PrintUtils.setAlignment('1');
            bold = PrintUtils.setBold(true);
            size = PrintUtils.setWH('4');
            text = PrintUtils.getBytes(sdf.format(new Date()) + "\n");
            PrintUtils.copyBytesToList(bytes, cursorPosition, alignment, bold, size, text);


            text = PrintUtils.getBytes(provider);
            PrintUtils.copyBytesToList(bytes, text);

            size = PrintUtils.setWH('2');
            bold = PrintUtils.setBold(false);
            text = dividerLine;
            PrintUtils.copyBytesToList(bytes, size, bold, PrintUtils.getLF(1), text, PrintUtils.getLF(1));

            bold = PrintUtils.setBold(true);
            text = PrintUtils.getBytes(vendor.replace(",", "\n") + "\n");
            PrintUtils.copyBytesToList(bytes, bold, text);

            String location = loc.replace(",", "\n");
            bold = PrintUtils.setBold(false);
            text = PrintUtils.getBytes(location);
            PrintUtils.copyBytesToList(bytes, bold, text);

            text = PrintUtils.getBytes(pin);
            PrintUtils.copyBytesToList(bytes, text, PrintUtils.getLF(1));

            text = PrintUtils.getBytes("Tel:" + tel_no);
            PrintUtils.copyBytesToList(bytes, text, PrintUtils.getLF(1));

            if (!vatNo.isEmpty()) {
                text = PrintUtils.getBytes("VAT No:" + vatNo);
                PrintUtils.copyBytesToList(bytes, text, PrintUtils.getLF(1));
            }
            if (!companyNo.isEmpty()) {
                text = PrintUtils.getBytes("Company No:" + companyNo);
                PrintUtils.copyBytesToList(bytes, text, PrintUtils.getLF(1));
            }


            text = dividerLine;
            PrintUtils.copyBytesToList(bytes, PrintUtils.getLF(1), text, PrintUtils.getLF(1));

            alignment = PrintUtils.setAlignment('0');
            size = PrintUtils.setWH('0');
            cursorPosition = PrintUtils.setCursorPosition(300);
            text = PrintUtils.getBytes("Orders");
            PrintUtils.copyBytesToList(bytes, alignment, size, cursorPosition, text);

            cursorPosition = PrintUtils.setCursorPosition(396);
            text = PrintUtils.getBytes("GBP" + "\n");
            PrintUtils.copyBytesToList(bytes, cursorPosition, text);

            //    ---------------------------------------- From realm ------------------------------------------

            RealmQuery<Purchase> realmQuery = realm.where(Purchase.class);
            RealmQuery<Purchase> realmQueryForCollection = realm.where(Purchase.class).equalTo("orderType", Constants.TYPE_COLLECTION);
            RealmQuery<Purchase> realmQueryForDelivery = realm.where(Purchase.class).equalTo("orderType", Constants.TYPE_DELIVERY);
            RealmQuery<Purchase> realmQueryForTable = realm.where(Purchase.class).equalTo("orderType", Constants.TYPE_TABLE);
            RealmQuery<Purchase> realmQueryForCard = realm.where(Purchase.class).equalTo("paymentMode", Constants.PAYMENT_TYPE_CARD);
            RealmQuery<Purchase> realmQueryForCash = realm.where(Purchase.class).equalTo("paymentMode", Constants.PAYMENT_TYPE_CASH);
            RealmQuery<Purchase> realmQueryForPhone = realm.where(Purchase.class).equalTo("paymentMode", Constants.PAYMENT_TYPE_PHONE);

            realmQuery.between("timestamp", startDateMin.getTime(), endDateMax.getTime());
            realmQueryForCollection.between("timestamp", startDateMin.getTime(), endDateMax.getTime());
            realmQueryForDelivery.between("timestamp", startDateMin.getTime(), endDateMax.getTime());
            realmQueryForTable.between("timestamp", startDateMin.getTime(), endDateMax.getTime());
            realmQueryForCard.between("timestamp", startDateMin.getTime(), endDateMax.getTime());
            realmQueryForCash.between("timestamp", startDateMin.getTime(), endDateMax.getTime());
            realmQueryForPhone.between("timestamp", startDateMin.getTime(), endDateMax.getTime());

            double totalAmount = realmQuery.sum("total").doubleValue();
            String formattedTotal = String.format("%.2f", totalAmount);
            double discountAmount = realmQuery.sum("discount").doubleValue();
            final String formattedDiscount = String.format("%.2f", discountAmount);
            double deliveryCharges = realmQuery.sum("deliveryCharges").doubleValue();
            final String formattedDeliveryCharges = String.format("%.2f", deliveryCharges);
            double serviceCharges = realmQuery.sum("serviceCharges").doubleValue();
            final String formattedServiceCharges = String.format("%.2f", serviceCharges);
            long totalCollectionOrders = realmQueryForCollection.count();
            long totalDeliveryOrders = realmQueryForDelivery.count();
            long totalTableOrders = realmQueryForTable.count();


            cursorPosition = PrintUtils.setCursorPosition(0);
            text = PrintUtils.getBytes("Collection");
            PrintUtils.copyBytesToList(bytes, cursorPosition, text);

            cursorPosition = PrintUtils.setCursorPosition(300);
            text = PrintUtils.getBytes(Long.toString(realmQueryForCollection.count()));
            PrintUtils.copyBytesToList(bytes, cursorPosition, text);

            cursorPosition = PrintUtils.setCursorPosition(396);
            text = PrintUtils.getBytes(String.format("%.2f", realmQueryForCollection.sum("total")));
            PrintUtils.copyBytesToList(bytes, cursorPosition, text, PrintUtils.getLF(1));

            cursorPosition = PrintUtils.setCursorPosition(0);
            text = PrintUtils.getBytes("Delivery");
            PrintUtils.copyBytesToList(bytes, cursorPosition, text);

            cursorPosition = PrintUtils.setCursorPosition(300);
            text = PrintUtils.getBytes(Long.toString(realmQueryForDelivery.count()));
            PrintUtils.copyBytesToList(bytes, cursorPosition, text);

            cursorPosition = PrintUtils.setCursorPosition(396);
            text = PrintUtils.getBytes(String.format("%.2f", realmQueryForDelivery.sum("total")));
            PrintUtils.copyBytesToList(bytes, cursorPosition, text, PrintUtils.getLF(1));

            cursorPosition = PrintUtils.setCursorPosition(0);
            alignment = PrintUtils.setAlignment('1');
            size = PrintUtils.setWH('2');
            text = dividerLine;
            PrintUtils.copyBytesToList(bytes, cursorPosition, alignment, size, text, PrintUtils.getLF(1));

            alignment = PrintUtils.setAlignment('0');
            size = PrintUtils.setWH('0');
            PrintUtils.copyBytesToList(bytes, alignment, size);

            text = PrintUtils.getBytes("Table");
            PrintUtils.copyBytesToList(bytes, text);

            cursorPosition = PrintUtils.setCursorPosition(300);
            text = PrintUtils.getBytes(Long.toString(realmQueryForTable.count()));
            PrintUtils.copyBytesToList(bytes, cursorPosition, text);

            cursorPosition = PrintUtils.setCursorPosition(396);
            text = PrintUtils.getBytes(String.format("%.2f", realmQueryForTable.sum("total")));
            PrintUtils.copyBytesToList(bytes, cursorPosition, text, PrintUtils.getLF(1));

            cursorPosition = PrintUtils.setCursorPosition(0);
            alignment = PrintUtils.setAlignment('1');
            size = PrintUtils.setWH('2');
            text = dividerLine;
            PrintUtils.copyBytesToList(bytes, cursorPosition, alignment, size, text, PrintUtils.getLF(1));

            alignment = PrintUtils.setAlignment('0');
            size = PrintUtils.setWH('0');
            PrintUtils.copyBytesToList(bytes, alignment, size);

            text = PrintUtils.getBytes("Card");
            PrintUtils.copyBytesToList(bytes, text);

            cursorPosition = PrintUtils.setCursorPosition(300);
            text = PrintUtils.getBytes(Long.toString(realmQueryForCard.count()));
            PrintUtils.copyBytesToList(bytes, cursorPosition, text);

            cursorPosition = PrintUtils.setCursorPosition(396);
            text = PrintUtils.getBytes(String.format("%.2f", realmQueryForCard.sum("total")));
            PrintUtils.copyBytesToList(bytes, cursorPosition, text, PrintUtils.getLF(1));

            /********************Card Phone***************************/
            text = PrintUtils.getBytes("Card (Phone)");
            PrintUtils.copyBytesToList(bytes, text);

            cursorPosition = PrintUtils.setCursorPosition(300);
            text = PrintUtils.getBytes(Long.toString(realmQueryForPhone.count()));
            PrintUtils.copyBytesToList(bytes, cursorPosition, text);

            cursorPosition = PrintUtils.setCursorPosition(396);
            text = PrintUtils.getBytes(String.format("%.2f", realmQueryForPhone.sum("total")));
            PrintUtils.copyBytesToList(bytes, cursorPosition, text, PrintUtils.getLF(1));

            /**********************************************************/

            /********************Online***************************/
            text = PrintUtils.getBytes(Constants.PAYMENT_TYPE_ONLINE);
            PrintUtils.copyBytesToList(bytes, text);

            cursorPosition = PrintUtils.setCursorPosition(300);
            text = PrintUtils.getBytes(onlineOrderCount);
            PrintUtils.copyBytesToList(bytes, cursorPosition, text);

            cursorPosition = PrintUtils.setCursorPosition(396);
            text = PrintUtils.getBytes(sum_of_onlineOrder);
            PrintUtils.copyBytesToList(bytes, cursorPosition, text, PrintUtils.getLF(1));

            /**********************************************************/
            cursorPosition = PrintUtils.setCursorPosition(0);
            text = PrintUtils.getBytes("Cash");
            PrintUtils.copyBytesToList(bytes, cursorPosition, text);

            cursorPosition = PrintUtils.setCursorPosition(300);
            text = PrintUtils.getBytes(Long.toString(realmQueryForCash.count()));
            PrintUtils.copyBytesToList(bytes, cursorPosition, text);

            cursorPosition = PrintUtils.setCursorPosition(396);
            text = PrintUtils.getBytes(String.format("%.2f", realmQueryForCash.sum("total")));
            PrintUtils.copyBytesToList(bytes, cursorPosition, text, PrintUtils.getLF(1));

            cursorPosition = PrintUtils.setCursorPosition(0);
            alignment = PrintUtils.setAlignment('1');
            size = PrintUtils.setWH('2');
            bold = PrintUtils.setBold(false);
            text = dividerLine;
            PrintUtils.copyBytesToList(bytes, cursorPosition, alignment, bold, size, text);

            /*bold = PrintUtils.setBold(true);
            alignment = PrintUtils.setAlignment('0');
            text = PrintUtils.getBytes("Total: ");
            PrintUtils.copyBytesToList(bytes, alignment, bold, text);

            cursorPosition = PrintUtils.setCursorPosition(396);
            text = PrintUtils.getBytes(String.format("%.2f", realmQuery.sum("total")));
            PrintUtils.copyBytesToList(bytes, cursorPosition, text);*/

            bold = PrintUtils.setBold(true);
            alignment = PrintUtils.setAlignment('0');
            text = PrintUtils.getBytes("Total:");
            PrintUtils.copyBytesToList(bytes, PrintUtils.getLF(1), bold, alignment, text);

            cursorPosition = PrintUtils.setCursorPosition(396);
            String total = String.format("%.2f", realmQuery.sum("total"));
            text = PrintUtils.getBytes(total);
            PrintUtils.copyBytesToList(bytes, cursorPosition, text, PrintUtils.getLF(1));

            cursorPosition = PrintUtils.setCursorPosition(0);
            alignment = PrintUtils.setAlignment('1');
            bold = PrintUtils.setBold(true);
            text = dividerLine;
            PrintUtils.copyBytesToList(bytes, cursorPosition, alignment, bold, text, PrintUtils.getLF(1));

            bold = PrintUtils.setBold(false);
            String time = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.UK).format(new Date());
            text = PrintUtils.getBytes(time);
            PrintUtils.copyBytesToList(bytes, bold, text);

            text = dividerLine;
            PrintUtils.copyBytesToList(bytes, PrintUtils.getLF(1), text, PrintUtils.getLF(1));

            size = PrintUtils.setWH('4');
            bold = PrintUtils.setBold(true);
            text = PrintUtils.getBytes("www.foodciti.co.uk");
            PrintUtils.copyBytesToList(bytes, size, bold, text, PrintUtils.getLF(2), PrintUtils.cutPaper());

            byte[] data = PrintUtils.toPrimitiveBytes(bytes);
            Intent intent = new Intent(PrintUtils.ACTION_PRINT_REQUEST);
            intent.putExtra(PrintUtils.PRINT_DATA, data);
            localBroadcastManager.sendBroadcast(intent);

        } catch (Exception ex) {
            Exlogger exlogger = new Exlogger();
            exlogger.setErrorType("Print Error");
            exlogger.setErrorMessage(ex.getMessage());
            exlogger.setScreenName("NewSalesReportFragment2->>printSalesReport() function");
            logger.addException(exlogger);
            returnValue = false;
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            realm.close();
        }
        return returnValue;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEodRun) {
            eodRunListener = (OnEodRun) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEodRun");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        eodRunListener = null;
    }

    protected void showEodRunDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("End of the day");
        builder.setMessage("Are you sure you want to do end of the day?");
        // add a button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // do something like...
                eodRunListener.onEodRun();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void showOrderTotalAndPrint() {
        if (InternetConnection.checkConnection(getActivity())) {
            Log.e("SHOW_TOTAL", "show total");
            //If the internet is working then only make the request
            Observable<GenericResponse<OrderTotal>> results = RetroClient.getApiService().getTotalMatrix(SessionManager.get(getActivity()).getFoodTruckId());

            new CompositeDisposable().add(results.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GenericResponse<OrderTotal>>() {
                        @Override
                        public void accept(GenericResponse<OrderTotal> response) throws Exception {
                            Log.e("SHOW_TOTAL_1", "" + response.getStatus());
                            if (response.getStatus().equals(Consts.IS_SUCCESS)) {
                                OrderTotal orderData = response.getData();
                                if (orderData != null) {
                                    mResponse = response;
                                    if (orderData.getOrderTotalCash() != null) {
                                        cashOrderNumber.setText(orderData.getOrderTotalCash().getOrderUnpaidCount());
                                        cashOrderTotal.setText("£ " + String.format("%.2f", orderData.getOrderTotalCash().getOrderTotalUnpaid()));
                                    } else {
                                        cashOrderNumber.setText("0");
                                        cashOrderTotal.setText("£ " + "00.00");
                                    }
                                    if (orderData.getOrderTotalCard() != null) {
                                        cardOrderNumber.setText(orderData.getOrderTotalCard().getOrderPaidCount());
                                        cardOrderTotal.setText("£ " + String.format("%.2f", orderData.getOrderTotalCard().getOrderTotalPaid()));
                                    } else {
                                        cardOrderNumber.setText("0");
                                        cardOrderTotal.setText("£ " + "00.00");
                                    }
                                    if (orderData.getOrderTotalDelivery() != null) {
                                        deliveryOrderNumber.setText(orderData.getOrderTotalDelivery().getOrderDeliveryCount());
                                        deliveryOrderTotal.setText("£ " + String.format("%.2f", orderData.getOrderTotalDelivery().getOrderTotalDelivery()));
                                    } else {
                                        deliveryOrderNumber.setText("0");
                                        deliveryOrderTotal.setText("£ " + "00.00");
                                    }
                                    if (orderData.getOrderTotalPickUp() != null) {
                                        collectionOrderNumber.setText(orderData.getOrderTotalPickUp().getOrderPickUpCount());
                                        collectionOrderTotal.setText("£ " + String.format("%.2f", orderData.getOrderTotalPickUp().getOrderTotalPickUp()));
                                    } else {
                                        collectionOrderNumber.setText("0");
                                        collectionOrderTotal.setText("£ " + "00.00");
                                    }
                                    if (orderData.getOrderTotalOverAll() != null) {
                                        orderNumberText.setText(orderData.getOrderTotalOverAll().getOrderTotalCount());
                                        orderTotal.setText("£ " + String.format("%.2f", orderData.getOrderTotalOverAll().getOrserTotal()));

                                    } else {
                                        orderNumberText.setText("0");
                                        orderTotal.setText("£ " + "00.00");
                                    }

                                    if (orderData.getOrderTotalPaypal() != null) {
                                        paypalNumberText.setText(orderData.getOrderTotalPaypal().getOrderPaypalCount());
                                        paypalOrderTotal.setText("£ " + String.format("%.2f", orderData.getOrderTotalPaypal().getOrderTotalPaypal()));

                                    } else {
                                        paypalNumberText.setText("0");
                                        paypalOrderTotal.setText("£ " + "00.00");
                                    }

                                    if (orderData.getOrderTotalWorldPay() != null) {
                                        worldpayNumberText.setText(orderData.getOrderTotalWorldPay().getOrderUnpaidCount());
                                        worldpayOrderTotal.setText("£ " + String.format("%.2f", orderData.getOrderTotalWorldPay().getOrderTotalUnpaid()));

                                    } else {
                                        worldpayNumberText.setText("0");
                                        worldpayOrderTotal.setText("£ " + "00.00");
                                    }
                                }

                            }
//                            setCookingTimes(restaurantId);
//                            printOnlineReport(mResponse);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.e("SHOW_TOTAL_ERROR", "" + throwable.toString());
                        }
                    }));
        }
    }


    boolean printOnlineReport(GenericResponse<OrderTotal> response) {

        final String POUND = "\u00A3";

        byte[] b = POUND.getBytes(Charset.forName("UTF-8"));

        boolean returnValue = true;
        try {
            int jNum = 0;

            byte[] printText22 = new byte[102400];

            byte[] oldText = setAlignCenter('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setInternationalCharcters('3');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('4');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk("FoodCiti");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(SessionManager.get(getActivity()).getRestaurantName() + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            String location = SessionManager.get(getActivity()).getRestaurantLocation();
            int spacecount = commacount(location);
            //Toast.makeText(this, SessionManager.get(this).getRestaurantName() + "\n"+SessionManager.get(this).getRestaurantLocation(), Toast.LENGTH_LONG).show();
            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            if (spacecount >= 1) {
                oldText = getGbk(location.substring(0, location.indexOf(',')) + "\n" + location.substring(location.indexOf(',') + 1) + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            } else {
                oldText = getGbk(location + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }


            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(SessionManager.get(getActivity()).getRestaurantPostalCode() + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Tel:" + SessionManager.get(getActivity()).getRestaurantPhonenumber());
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setAlignCenter('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('5');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(250);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Orders");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setCusorPosition(420);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('5');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("GBP" + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setAlignCenter('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            // Collection print text
            oldText = setWH('5');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Collection ");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('5');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(260);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            if (response.getData().getOrderTotalPickUp() != null) {

                oldText = getGbk(String.format(" %02d", Integer.parseInt(response.getData().getOrderTotalPickUp().getOrderPickUpCount())));
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

            } else {
                oldText = getGbk("00");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }


            oldText = setWH('5');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(400);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            if (response.getData().getOrderTotalPickUp() != null) {

                oldText = getGbk(String.format("%.2f", response.getData().getOrderTotalPickUp().getOrderTotalPickUp()) + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

            } else {
                oldText = getGbk("00.00" + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }


            oldText = setAlignCenter('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            // Delivery print text
            oldText = setWH('5');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Delivery ");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(270);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            if (response.getData().getOrderTotalDelivery() != null) {

                oldText = getGbk(String.format("%02d", Integer.parseInt(response.getData().getOrderTotalDelivery().getOrderDeliveryCount())));
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

            } else {
                oldText = getGbk("00");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }


            oldText = setCusorPosition(400);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('5');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            if (response.getData().getOrderTotalDelivery() != null) {

                oldText = getGbk(String.format("%.2f", response.getData().getOrderTotalDelivery().getOrderTotalDelivery()) + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

            } else {
                oldText = getGbk("00.00" + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }


            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            // card print text
            if (response.getData().getOrderTotalCard() != null
                    && Integer.parseInt(response.getData().getOrderTotalCard().getOrderPaidCount()) > 0) {
                oldText = setWH('5');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = getGbk("Card ");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setCusorPosition(270);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('5');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                if (response.getData().getOrderTotalCard() != null) {

                    oldText = getGbk(String.format(" %02d", Integer.parseInt(response.getData().getOrderTotalCard().getOrderPaidCount())));
                    System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                    jNum += oldText.length;

                } /*else {

                oldText = getGbk("00");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

            }*/

                oldText = setCusorPosition(400);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                if (response.getData().getOrderTotalCard() != null) {

                    oldText = getGbk(String.format("%.2f", response.getData().getOrderTotalCard().getOrderTotalPaid()) + "\n");
                    System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                    jNum += oldText.length;

                } /*else {

                oldText = getGbk("00.00" + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

            }*/
            }


            // cash print text
            if (response.getData().getOrderTotalCash() != null
                    && Integer.parseInt(response.getData().getOrderTotalCash().getOrderUnpaidCount()) > 0) {
                oldText = setAlignCenter('0');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;


                oldText = setWH('5');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = getGbk("Cash ");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setCusorPosition(270);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                if (response.getData().getOrderTotalCash() != null) {

                    oldText = getGbk(String.format(" %02d", Integer.parseInt(response.getData().getOrderTotalCash().getOrderUnpaidCount())));
                    System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                    jNum += oldText.length;

                } /*else {
                oldText = getGbk("00");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }*/


                oldText = setCusorPosition(400);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('5');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                if (response.getData().getOrderTotalCash() != null) {

                    oldText = getGbk(String.format("%.2f", response.getData().getOrderTotalCash().getOrderTotalUnpaid()) + "\n");
                    System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                    jNum += oldText.length;

                } /*else {
                oldText = getGbk("00.00" + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }*/
            }


            // paypal print text
            if (response.getData().getOrderTotalPaypal() != null
                    && Integer.parseInt(response.getData().getOrderTotalPaypal().getOrderPaypalCount()) > 0) {
                oldText = getGbk("Paypal ");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setCusorPosition(270);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                if (response.getData().getOrderTotalPaypal() != null) {

                    oldText = getGbk(String.format(" %02d", Integer.parseInt(response.getData().getOrderTotalPaypal().getOrderPaypalCount())));
                    System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                    jNum += oldText.length;

                } /*else {
                oldText = getGbk("00");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }*/


                oldText = setCusorPosition(400);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('5');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                if (response.getData().getOrderTotalPaypal() != null) {

                    oldText = getGbk(String.format("%.2f", response.getData().getOrderTotalPaypal().getOrderTotalPaypal()) + "\n");
                    System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                    jNum += oldText.length;

                } /*else {
                oldText = getGbk("00.00" + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }*/
            }


            // worldpay print text
            if (response.getData().getOrderTotalWorldPay() != null
                    && Integer.parseInt(response.getData().getOrderTotalWorldPay().getOrderUnpaidCount()) > 0) {
                oldText = getGbk("WorldPay ");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setCusorPosition(270);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                if (response.getData().getOrderTotalWorldPay() != null) {

                    oldText = getGbk(String.format(" %02d", Integer.parseInt(response.getData().getOrderTotalWorldPay().getOrderUnpaidCount())));
                    System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                    jNum += oldText.length;

                } /*else {
                oldText = getGbk("00");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }*/


                oldText = setCusorPosition(400);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('5');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                if (response.getData().getOrderTotalWorldPay() != null) {

                    oldText = getGbk(String.format("%.2f", response.getData().getOrderTotalWorldPay().getOrderTotalUnpaid()) + "\n");
                    System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                    jNum += oldText.length;

                } /*else {
                oldText = getGbk("00.00" + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }*/
            }


            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Total : ");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setAlignCenter('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(270);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            if (response.getData().getOrderTotalOverAll() != null) {

                oldText = getGbk(String.format("%02d", Integer.parseInt(response.getData().getOrderTotalOverAll().getOrderTotalCount())));
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

            } else {
                oldText = getGbk("00");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }


            oldText = setCusorPosition(400);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            if (response.getData().getOrderTotalOverAll() != null) {

                oldText = getGbk(String.format("%.2f", response.getData().getOrderTotalOverAll().getOrserTotal()) + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

            } else {
                oldText = getGbk("00.00" + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }


            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            String strTmp2 = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.UK).format(new Date());
            oldText = getGbk(strTmp2);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n-----------------------\n\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('4');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk("www.foodciti.co.uk");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

           /* String s = new String(printText22);
            Toast.makeText(this, s, Toast.LENGTH_LONG).show();*/

            oldText = CutPaper();
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            Intent intent = new Intent(PrintUtils.ACTION_PRINT_REQUEST);
            intent.putExtra(PrintUtils.PRINT_DATA, printText22);
            localBroadcastManager.sendBroadcast(intent);
//            mOutputStream.write(printText22);

        } catch (Exception ex) {
            Exlogger exlogger = new Exlogger();
            exlogger.setErrorType("Print Error");
            exlogger.setErrorMessage(ex.getMessage());
            exlogger.setScreenName("NewSalesReportFragment2->>printOnlineReport() function");
            logger.addException(exlogger);
            returnValue = false;
            //  Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        return returnValue;
    }

    public static byte[] getGbk(String paramString) {
        byte[] arrayOfByte = null;
        try {
            arrayOfByte = paramString.getBytes("GBK");
        } catch (Exception ex) {

        }
        return arrayOfByte;

    }

    public static byte[] setCusorPosition(int position) {
        byte[] returnText = new byte[4];
        returnText[0] = 0x1B;
        returnText[1] = 0x24;
        returnText[2] = (byte) (position % 256);
        returnText[3] = (byte) (position / 256);
        return returnText;
    }

    public static byte[] setBold(boolean paramBoolean) {
        byte[] arrayOfByte = new byte[3];
        arrayOfByte[0] = 0x1B;
        arrayOfByte[1] = 0x45;
        if (paramBoolean) {
            arrayOfByte[2] = 0x01;
        } else {
            arrayOfByte[2] = 0x00;
        }
        return arrayOfByte;
    }

    public static byte[] setAlignCenter(char paramChar) {
        byte[] arrayOfByte = new byte[3];
        arrayOfByte[0] = 0x1B;
        arrayOfByte[1] = 0x61;

        switch (paramChar) {
            case '1':
                arrayOfByte[2] = 0x01;
                break;
            case '2':
                arrayOfByte[2] = 0x02;
                break;
            default:
                arrayOfByte[2] = 0x00;
                break;
        }
        return arrayOfByte;
    }

    public static byte[] setWH(char paramChar) {
        byte[] arrayOfByte = new byte[3];
        arrayOfByte[0] = 0x1D;
        arrayOfByte[1] = 0x21;

        switch (paramChar) {
            case '1':
                arrayOfByte[2] = 0x0F;

                break;
            case '2':
                arrayOfByte[2] = 0x10;
                break;
            case '3':
                arrayOfByte[2] = 0x01;
                break;
            case '4':
                arrayOfByte[2] = 0x11;
                break;
            default:
                arrayOfByte[2] = 0x00;
                break;
        }

        return arrayOfByte;
    }

    public static byte[] setInternationalCharcters(char paramChar) {
        byte[] arrayOfByte = new byte[3];
        arrayOfByte[0] = 0x1B;
        arrayOfByte[1] = 0x52;
        //arrayOfByte.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), Consts.COMFORTAA_BOLD));
        switch (paramChar) {
            case '1':
                arrayOfByte[2] = 0x01;//France charactrer set
                break;
            case '2':
                arrayOfByte[2] = 0x02;//Germany charactrer set
                break;
            case '3':
                arrayOfByte[2] = 0x03;//UK charactrer set
                break;
            default:
                arrayOfByte[2] = 0x00;//USA charactrer set
                break;
        }

        return arrayOfByte;
    }


    public static byte[] CutPaper()   //切纸； GS V 66D 0D
    {
        byte[] arrayOfByte = new byte[]{0x1D, 0x56, 0x42, 0x00};
        return arrayOfByte;
    }

    private int commacount(String s) {
        int i;
        int c = 0;
        for (i = 0, c = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == ',')
                c++;
        }
        return c;
    }


}
