//package com.foodciti.foodcitipartener.activities;
//
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.InputType;
//import android.text.TextWatcher;
//import android.widget.EditText;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.AppCompatCheckBox;
//
//import com.foodciti.foodcitipartener.R;
//import com.foodciti.foodcitipartener.utils.AppConfig;
//import com.foodciti.foodcitipartener.utils.Preferences;
//
//public class Billing extends AppCompatActivity {
//
//    private RadioGroup radioGroup;
//    private RadioButton deliveryChargesRB, serviceChargesRB;
//    private EditText amountET;
//    private AppCompatCheckBox cash, card;
//    private SharedPreferences sharedPreferences;
//    private SharedPreferences.Editor editor;
//    private float deliveryCharges, serviceCharges;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_billing);
//        sharedPreferences = getSharedPreferences(AppConfig.MyPREFERENCES, MODE_PRIVATE);
//        editor = sharedPreferences.edit();
//        initViews();
//        deliveryChargesRB.performClick();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        editor.putFloat(Preferences.DELIVERY_CHARGES, deliveryCharges);
//        editor.putFloat(Preferences.SERVICE_CHARGES, serviceCharges);
//        editor.commit();
//    }
//
//    private void initViews() {
//        findViewById(R.id.close).setOnClickListener(v -> {
//            onBackPressed();
//        });
//        radioGroup = findViewById(R.id.report_type_group);
//        deliveryChargesRB = findViewById(R.id.delivery_charges);
//        serviceChargesRB = findViewById(R.id.serviceCharges);
//        amountET = findViewById(R.id.amount);
//        amountET.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
//        amountET.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (deliveryChargesRB.isChecked()) {
//                    if (s.toString().trim().matches("^[0-9]*[\\.]?[0-9]+")) {
//                        deliveryCharges = Float.parseFloat(s.toString().trim());
//                        editor.putFloat(Preferences.DELIVERY_CHARGES, deliveryCharges);
//                        editor.commit();
//                    }
//                } else {
//                    if (s.toString().trim().matches("^[0-9]*[\\.]?[0-9]+")) {
//                        serviceCharges = Float.parseFloat(s.toString().trim());
//                        editor.putFloat(Preferences.SERVICE_CHARGES, serviceCharges);
//                        editor.commit();
//                    }
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//        cash = findViewById(R.id.cash);
//        card = findViewById(R.id.card);
//
//        /*float amountChargeable=sharedPreferences.getFloat(Preferences.AMOUNT_CHARGEABLE, 0);
//        amountET.setText(amountChargeable+"");*/
//        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
//            switch (checkedId) {
//                case R.id.delivery_charges: {
//                    boolean deliveryChargesCashApplicable = sharedPreferences.getBoolean(Preferences.DELIVERY_CHARGES_CASH_APPLICABLE, false);
//                    boolean deliveryChargesCardApplicable = sharedPreferences.getBoolean(Preferences.DELIVERY_CHARGES_CARD_APPLICABLE, false);
//                    cash.setChecked(deliveryChargesCashApplicable);
//                    card.setChecked(deliveryChargesCardApplicable);
//                    amountET.setText(sharedPreferences.getFloat(Preferences.DELIVERY_CHARGES, 0) + "");
//                }
//                break;
//                case R.id.serviceCharges: {
//                    boolean serviceChargesCashApplicable = sharedPreferences.getBoolean(Preferences.SERVICE_CHARGES_CASH_APPLICABLE, false);
//                    boolean serviceChargesCardApplicable = sharedPreferences.getBoolean(Preferences.SERVICE_CHARGES_CARD_APPLICABLE, false);
//                    cash.setChecked(serviceChargesCashApplicable);
//                    card.setChecked(serviceChargesCardApplicable);
//                    amountET.setText(sharedPreferences.getFloat(Preferences.SERVICE_CHARGES, 0) + "");
//                }
//                break;
//            }
//        });
//
//        cash.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (deliveryChargesRB.isChecked()) {
//                if (isChecked) {
//                    editor.putBoolean(Preferences.DELIVERY_CHARGES_CASH_APPLICABLE, true);
//                } else {
//                    editor.putBoolean(Preferences.DELIVERY_CHARGES_CASH_APPLICABLE, false);
//                }
//            } else if (serviceChargesRB.isChecked()) {
//                if (isChecked) {
//                    editor.putBoolean(Preferences.SERVICE_CHARGES_CASH_APPLICABLE, true);
//                } else {
//                    editor.putBoolean(Preferences.SERVICE_CHARGES_CASH_APPLICABLE, false);
//                }
//            }
//            editor.commit();
//        });
//
//        card.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (deliveryChargesRB.isChecked()) {
//                if (isChecked) {
//                    editor.putBoolean(Preferences.DELIVERY_CHARGES_CARD_APPLICABLE, true);
//                } else {
//                    editor.putBoolean(Preferences.DELIVERY_CHARGES_CARD_APPLICABLE, false);
//                }
//            } else if (serviceChargesRB.isChecked()) {
//                if (isChecked) {
//                    editor.putBoolean(Preferences.SERVICE_CHARGES_CARD_APPLICABLE, true);
//                } else {
//                    editor.putBoolean(Preferences.SERVICE_CHARGES_CARD_APPLICABLE, false);
//                }
//            }
//            editor.commit();
//        });
//    }
//}
