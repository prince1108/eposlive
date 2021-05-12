package com.foodciti.foodcitipartener.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.keyboards.ExtendedKeyBoard;
import com.foodciti.foodcitipartener.keyboards.NumPad;
import com.foodciti.foodcitipartener.utils.AppConfig;
import com.foodciti.foodcitipartener.utils.Preferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeliveryBillingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeliveryBillingFragment extends Fragment implements View.OnKeyListener, View.OnFocusChangeListener {
    private static final String TAG = "DeliveryBilling";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private NumPad numPad;
    private RadioGroup reportTypeRG, applyOptionsRG, pounPercentRG;
    private RadioButton deliveryChargesRB, serviceChargesRB, cashRB, cardRB, bothRB, poundRB, percentRB;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private float deliveryCharges, serviceCharges;

    public DeliveryBillingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeliveryBillingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeliveryBillingFragment newInstance(String param1, String param2) {
        DeliveryBillingFragment fragment = new DeliveryBillingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
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
    public void onPause() {
        editor.putFloat(Preferences.DELIVERY_CHARGES_DELIVERY, deliveryCharges);
        editor.putFloat(Preferences.SERVICE_CHARGES_DELIVERY, serviceCharges);
        editor.commit();
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences(AppConfig.MyPREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_delivery_billing, container, false);
        numPad = view.findViewById(R.id.keyBoard);
        EditText amountET = view.findViewById(R.id.amount);
        amountET.setShowSoftInputOnFocus(false);
        amountET.setOnKeyListener(this);
        amountET.setOnFocusChangeListener(this);
        amountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (deliveryChargesRB.isChecked()) {
                    if (s.toString().trim().matches("^[0-9]*[\\.]?[0-9]+")) {
                        deliveryCharges = Float.parseFloat(s.toString().trim());
                        editor.putFloat(Preferences.DELIVERY_CHARGES_DELIVERY, deliveryCharges);
                        editor.commit();
                    }
                } else {
                    if (s.toString().trim().matches("^[0-9]*[\\.]?[0-9]+")) {
                        serviceCharges = Float.parseFloat(s.toString().trim());
                        editor.putFloat(Preferences.SERVICE_CHARGES_DELIVERY, serviceCharges);
                        editor.commit();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        poundRB = view.findViewById(R.id.pound);
        percentRB = view.findViewById(R.id.percent);

        reportTypeRG = view.findViewById(R.id.charges_type_group);
        pounPercentRG = view.findViewById(R.id.pound_percent);
        reportTypeRG.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.delivery_charges: {
                    int deliveryChargesApplicableOn = sharedPreferences.getInt(Preferences.DELIVERY_CHARGE_OPTION_FOR_DELIVERY, Preferences.OPTION_CASH);
                    boolean isPercent = sharedPreferences.getBoolean(Preferences.DELIVERY_CHARGES_PERCENTAGE_FOR_DELIVERY, false);
                    if (isPercent)
                        percentRB.setChecked(true);
                    else
                        poundRB.setChecked(true);
                    switch (deliveryChargesApplicableOn) {
                        case 0:
                            cashRB.setChecked(true);
                            break;
                        case 1:
                            cardRB.setChecked(true);
                            break;
                        case 2:
                            bothRB.setChecked(true);
                            break;
                    }
                    amountET.setText(sharedPreferences.getFloat(Preferences.DELIVERY_CHARGES_DELIVERY, 0) + "");
                }
                break;
                case R.id.service_charges: {
                    int serviceChargesApplicableOn = sharedPreferences.getInt(Preferences.SERVICE_CHARGE_OPTION_FOR_DELIVERY, Preferences.OPTION_CASH);
                    boolean isPercent = sharedPreferences.getBoolean(Preferences.SERVICE_CHARGES_PERCENTAGE_FOR_DELIVERY, false);
                    if (isPercent)
                        percentRB.setChecked(true);
                    else
                        poundRB.setChecked(true);
                    switch (serviceChargesApplicableOn) {
                        case 0:
                            cashRB.setChecked(true);
                            break;
                        case 1:
                            cardRB.setChecked(true);
                            break;
                        case 2:
                            bothRB.setChecked(true);
                            break;
                    }
                    amountET.setText(sharedPreferences.getFloat(Preferences.SERVICE_CHARGES_DELIVERY, 0) + "");
                }
                break;
            }
        });


        pounPercentRG.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.pound:
                    if (deliveryChargesRB.isChecked())
                        editor.putBoolean(Preferences.DELIVERY_CHARGES_PERCENTAGE_FOR_DELIVERY, false);
                    else
                        editor.putBoolean(Preferences.SERVICE_CHARGES_PERCENTAGE_FOR_DELIVERY, false);
                    editor.commit();
                    break;
                case R.id.percent:
                    if (deliveryChargesRB.isChecked())
                        editor.putBoolean(Preferences.DELIVERY_CHARGES_PERCENTAGE_FOR_DELIVERY, true);
                    else
                        editor.putBoolean(Preferences.SERVICE_CHARGES_PERCENTAGE_FOR_DELIVERY, true);
                    editor.commit();
                    break;
            }
        });

        applyOptionsRG = view.findViewById(R.id.apply_options);
        applyOptionsRG.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.cash: {
                    if (deliveryChargesRB.isChecked()) {
                        editor.putInt(Preferences.DELIVERY_CHARGE_OPTION_FOR_DELIVERY, Preferences.OPTION_CASH);
                    } else if (serviceChargesRB.isChecked()) {
                        editor.putInt(Preferences.SERVICE_CHARGE_OPTION_FOR_DELIVERY, Preferences.OPTION_CASH);
                    }
                    editor.commit();
                }
                break;
                case R.id.card: {
                    if (deliveryChargesRB.isChecked()) {
                        editor.putInt(Preferences.DELIVERY_CHARGE_OPTION_FOR_DELIVERY, Preferences.OPTION_CARD);
                    } else if (serviceChargesRB.isChecked()) {
                        editor.putInt(Preferences.SERVICE_CHARGE_OPTION_FOR_DELIVERY, Preferences.OPTION_CARD);
                    }
                    editor.commit();
                }
                break;
                case R.id.both: {
                    if (deliveryChargesRB.isChecked()) {
                        editor.putInt(Preferences.DELIVERY_CHARGE_OPTION_FOR_DELIVERY, Preferences.OPTION_BOTH);
                    } else if (serviceChargesRB.isChecked()) {
                        editor.putInt(Preferences.SERVICE_CHARGE_OPTION_FOR_DELIVERY, Preferences.OPTION_BOTH);
                    }
                    editor.commit();
                }
                break;
            }
        });

        deliveryChargesRB = view.findViewById(R.id.delivery_charges);
        serviceChargesRB = view.findViewById(R.id.service_charges);
        cashRB = view.findViewById(R.id.cash);
        cardRB = view.findViewById(R.id.card);
        bothRB = view.findViewById(R.id.both);

        // set checked radio buttons
        deliveryChargesRB.setChecked(true);

        return view;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        EditText editText = null;
        InputConnection ic = null;
        if (v instanceof EditText) {
            editText = (EditText) v;
            ic = editText.onCreateInputConnection(new EditorInfo());
        }

        if (hasFocus && ic != null) {
            numPad.setInputConnection(ic);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Log.e(TAG, "----------------------keyEvent---------------------");
        switch (keyCode) {
            case ExtendedKeyBoard.KEYCODE_DONE:
                Log.e(TAG, "-------------------keycode: " + keyCode);
                return true;
            case KeyEvent.KEYCODE_CLEAR:
                Log.e(TAG, "-------------------keycode: " + keyCode);
                if (v instanceof EditText) {
                    EditText et = (EditText) v;
                    et.setText("");
                }
                return true;
        }
        return false;
    }
}
