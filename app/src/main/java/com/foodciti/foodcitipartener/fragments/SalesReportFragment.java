package com.foodciti.foodcitipartener.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.realm_entities.Order;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SalesReportFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SalesReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SalesReportFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "SalesReportFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView count_collection, count_delivery, count_table, count_total, count_collection_cash,
            count_collection_card, count_delivery_cash, count_delivery_card, count_table_cash, count_table_card;
    private TextView sales_collection_cash, sales_collection_card, sales_delivery_cash, sales_delivery_card, sales_table_cash, sales_table_card,
            sales_collection, sales_delivery, sales_table, sales_total;

    private TextView label;
    private EditText date1, date2;
    private RadioGroup radioGroup;
    private LinearLayout date2_container;

    private Date mDate1, mDate2;
    private Button go;
    private RadioButton sales_today, sales_by_date, sales_between_date;
    private OnFragmentInteractionListener mListener;

    private Realm realm;
    final Calendar myCalendar = Calendar.getInstance();

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
            updateLabel(date1);
        }

    };
    final DatePickerDialog.OnDateSetListener dateSetListener2 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            myCalendar.set(Calendar.HOUR_OF_DAY, 23);
            myCalendar.set(Calendar.MINUTE, 59);
            mDate2 = myCalendar.getTime();
            updateLabel(date2);
        }

    };

    public SalesReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SalesReportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SalesReportFragment newInstance(String param1, String param2) {
        SalesReportFragment fragment = new SalesReportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static SalesReportFragment newInstance() {
        SalesReportFragment fragment = new SalesReportFragment();
        return fragment;
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "---------------onPause--------------------------");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sales_report, container, false);
    }

    private void updateLabel(EditText dateField) {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateField.setText(sdf.format(myCalendar.getTime()));
        if (sales_by_date.isChecked()) {
            if (!date1.getText().toString().trim().isEmpty())
                go.setEnabled(true);
        }
        if (sales_between_date.isChecked()) {
            if (!date1.getText().toString().trim().isEmpty() && !date2.getText().toString().trim().isEmpty())
                go.setEnabled(true);
        }
    }

    private void processReport(RealmResults<Order> orders) {
        int collectionCount = 0, collectionCashCount = 0, collectionCardCount = 0, collectionSales = 0, collectionCashSales = 0, collectionCardSales = 0;
        int deliveryCount = 0, deliveryCashCount = 0, deliveryCardCount = 0, deliverySales = 0, deliveryCashSales = 0, deliveryCardSales = 0;
        int tableCount = 0, tableCashCount = 0, tableCardCount = 0, tableSales = 0, tableCashSales = 0, tableCardSales = 0;
        int totalCount = 0, totalSales = 0;
        for (Order c : orders) {
            if (c.getOrderType().equals(Constants.TYPE_COLLECTION)) {
                collectionCount += 1;
                collectionSales += c.getTotal();
                if (c.getPaymentMode().equals(Constants.PAYMENT_TYPE_CASH)) {
                    collectionCashCount += 1;
                    collectionCashSales += c.getTotal();
                } else if (c.getPaymentMode().equals(Constants.PAYMENT_TYPE_CARD)) {
                    collectionCardCount += 1;
                    collectionCardSales += c.getTotal();
                }
            } else if (c.getOrderType().equals(Constants.TYPE_DELIVERY)) {
                deliveryCount += 1;
                deliverySales += c.getTotal();
                if (c.getPaymentMode().equals(Constants.PAYMENT_TYPE_CASH)) {
                    deliveryCashCount += 1;
                    deliveryCashSales += c.getTotal();
                } else if (c.getPaymentMode().equals(Constants.PAYMENT_TYPE_CARD)) {
                    deliveryCardCount += 1;
                    deliveryCardSales += c.getTotal();
                }
            } else {
                tableCount += 1;
                tableSales += c.getTotal();
                if (c.getPaymentMode().equals(Constants.PAYMENT_TYPE_CASH)) {
                    tableCashCount += 1;
                    tableCashSales += c.getTotal();
                } else if (c.getPaymentMode().equals(Constants.PAYMENT_TYPE_CARD)) {
                    tableCardCount += 1;
                    tableCardSales += c.getTotal();
                }
            }
        }

        totalCount = collectionCount + deliveryCount + tableCount;
        totalSales = collectionSales + deliverySales + tableSales;
        count_collection.setText(collectionCount + "");
        sales_collection.setText(collectionSales + "");
        count_collection_cash.setText(collectionCashCount + "");
        count_collection_card.setText(collectionCardCount + "");
        sales_collection_cash.setText(collectionCashSales + "");
        sales_collection_card.setText(collectionCardSales + "");

        count_delivery.setText(deliveryCount + "");
        sales_delivery.setText(deliverySales + "");
        count_delivery_cash.setText(deliveryCashCount + "");
        count_delivery_card.setText(deliveryCardCount + "");
        sales_delivery_cash.setText(deliveryCashSales + "");
        sales_delivery_card.setText(deliveryCardSales + "");

        count_table.setText(tableCount + "");
        sales_table.setText(tableSales + "");
        count_table_cash.setText(tableCashCount + "");
        count_table_card.setText(tableCardCount + "");
        sales_table_cash.setText(tableCashSales + "");
        sales_table_card.setText(tableCardSales + "");

        count_total.setText(totalCount + "");
        sales_total.setText(totalSales + "");
    }

    private void resetFields() {
        count_collection.setText("0");
        sales_collection.setText("0");
        count_collection_cash.setText("0");
        count_collection_card.setText("0");
        sales_collection_cash.setText("0");
        sales_collection_card.setText("0");

        count_delivery.setText("0");
        sales_delivery.setText("0");
        count_delivery_cash.setText("0");
        count_delivery_card.setText("0");
        sales_delivery_cash.setText("0");
        sales_delivery_card.setText("0");

        count_total.setText("0");
        sales_total.setText("0");
    }

    @Override
    public void onDestroy() {
//        RealmManager.closeRealmFor(getActivity());
        super.onDestroy();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        realm = RealmManager.getLocalInstance();
        count_collection = view.findViewById(R.id.count_collection);
        count_delivery = view.findViewById(R.id.count_delivery);
        count_table = view.findViewById(R.id.count_table);
        count_collection_cash = view.findViewById(R.id.count_collection_cash);
        count_collection_card = view.findViewById(R.id.count_collection_card);
        count_delivery_cash = view.findViewById(R.id.count_delivery_cash);
        count_delivery_card = view.findViewById(R.id.count_delivery_card);
        count_table_cash = view.findViewById(R.id.count_table_cash);
        count_table_card = view.findViewById(R.id.count_table_card);
        count_total = view.findViewById(R.id.count_total);

        sales_collection = view.findViewById(R.id.sale_collection);
        sales_delivery = view.findViewById(R.id.sale_delivery);
        sales_table = view.findViewById(R.id.sale_table);
        sales_collection_cash = view.findViewById(R.id.sale_collection_cash);
        sales_collection_card = view.findViewById(R.id.sale_collection_card);
        sales_delivery_cash = view.findViewById(R.id.sale_delivery_cash);
        sales_delivery_card = view.findViewById(R.id.sale_delivery_card);
        sales_table_cash = view.findViewById(R.id.sale_table_cash);
        sales_table_card = view.findViewById(R.id.sale_table_card);
        sales_total = view.findViewById(R.id.sale_total);

        sales_today = view.findViewById(R.id.sales_today);
        sales_by_date = view.findViewById(R.id.sales_by_date);
        sales_between_date = view.findViewById(R.id.sales_between_date);

        date2_container = view.findViewById(R.id.date2_container);
        label = view.findViewById(R.id.label);

        go = view.findViewById(R.id.go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetFields();
                RealmResults<Order> orders = null;
                if (sales_today.isChecked()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 1);
                    Date todayStart = calendar.getTime();

                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    Date todayEnd = calendar.getTime();
                    orders = realm.where(Order.class).between("timestamp", todayStart.getTime(), todayEnd.getTime()).findAll();
                } else if (sales_by_date.isChecked()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(mDate1);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 1);
                    Date start = calendar.getTime();

                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    Date end = calendar.getTime();

                    orders = realm.where(Order.class).between("timestamp", start.getTime(), end.getTime()).findAll();

                } else if (sales_between_date.isChecked()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(mDate1);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 1);
                    Date start = calendar.getTime();

                    calendar.setTime(mDate2);
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    Date end = calendar.getTime();

                    orders = realm.where(Order.class).between("timestamp", start.getTime(), end.getTime()).findAll();
                }

                if (!orders.isEmpty())
                    processReport(orders);

            }
        });


        date1 = view.findViewById(R.id.date1);
        date1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), dateSetListener1, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        date2 = view.findViewById(R.id.date2);
        date2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), dateSetListener2, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        radioGroup = view.findViewById(R.id.charges_type_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.sales_today:
                        date2_container.setVisibility(View.INVISIBLE);
                        date1.setVisibility(View.INVISIBLE);
                        go.setEnabled(true);
                        Log.e(TAG, "sales_today");

                        break;
                    case R.id.sales_by_date:
                        go.setEnabled(false);
                        date1.setVisibility(View.VISIBLE);
                        date2_container.setVisibility(View.INVISIBLE);
                        Log.e(TAG, "sales_by_date");

                        break;
                    case R.id.sales_between_date:
                        go.setEnabled(false);
                        date1.setVisibility(View.VISIBLE);
                        date2_container.setVisibility(View.VISIBLE);
                        Log.e(TAG, "sales_between_date");

                        break;
                }
            }
        });

        ((RadioButton) view.findViewById(R.id.sales_today)).setChecked(true);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
