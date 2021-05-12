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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.CompletedOrdersAdapter;
import com.foodciti.foodcitipartener.dialogs.OrderHistoryDetailsDialog;
import com.foodciti.foodcitipartener.realm_entities.Order;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CompletedOrdersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompletedOrdersFragment extends Fragment implements CompletedOrdersAdapter.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "CompletedOrdersFragment";
    private RecyclerView completedOrdersRV;
    private Realm realm;
    private Date mDate1, mDate2;
    private EditText fromDate, toDate;
    private Button go;

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
            updateLabel(fromDate);
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
            updateLabel(toDate);
        }

    };

    private void updateLabel(EditText dateField) {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateField.setText(sdf.format(myCalendar.getTime()));
        if (!fromDate.getText().toString().trim().isEmpty())
            go.setEnabled(true);
        /*if (sales_between_date.isChecked()) {
            if(!date1.getText().toString().trim().isEmpty() && !date2.getText().toString().trim().isEmpty())
                go.setEnabled(true);
        }*/
    }

    private void resetFields() {
        /*count_collection.setText("0");
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
        sales_total.setText("0");*/
    }

    public CompletedOrdersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CompletedOrdersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CompletedOrdersFragment newInstance(String param1, String param2) {
        CompletedOrdersFragment fragment = new CompletedOrdersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static CompletedOrdersFragment newInstance() {
        CompletedOrdersFragment fragment = new CompletedOrdersFragment();
        return fragment;
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
        return inflater.inflate(R.layout.fragment_completed_orders, container, false);
    }

    @Override
    public void onDestroy() {
//        RealmManager.closeRealmFor(getActivity());
        super.onDestroy();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        completedOrdersRV = view.findViewById(R.id.completedOrdersRV);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity());
        completedOrdersRV.setLayoutManager(lm);
        completedOrdersRV.setHasFixedSize(true);
        realm = RealmManager.getLocalInstance();
        fromDate = view.findViewById(R.id.fromDate);
        toDate = view.findViewById(R.id.toDate);
        go = view.findViewById(R.id.go);
        go.setOnClickListener(v -> {
            resetFields();
            RealmResults<Order> orders = null;
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

            orders = realm.where(Order.class).equalTo("isPaid", true).between("timestamp", start.getTime(), end.getTime()).findAll();

//            if(!orders.isEmpty())
            completedOrdersRV.setAdapter(new CompletedOrdersAdapter(getActivity(), orders, CompletedOrdersFragment.this, realm));
        });

        fromDate.setOnClickListener(v -> {
            new DatePickerDialog(getActivity(), dateSetListener1, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        toDate.setOnClickListener(v -> {
            new DatePickerDialog(getActivity(), dateSetListener2, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        RealmResults<Order> orders = realm.where(Order.class).equalTo("isPaid", true).findAllAsync();
        orders.addChangeListener(rs -> {
            completedOrdersRV.setAdapter(new CompletedOrdersAdapter(getActivity(), rs, CompletedOrdersFragment.this, realm));
        });
    }

    @Override
    public void onClickShowDetails(long orderId) {
        Log.e(TAG, "--------------oredrId: " + orderId);
        OrderHistoryDetailsDialog addition = OrderHistoryDetailsDialog.getInstance(orderId);
        addition.show(getActivity().getSupportFragmentManager(), null);
    }
}
