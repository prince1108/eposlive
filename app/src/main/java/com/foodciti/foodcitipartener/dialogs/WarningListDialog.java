package com.foodciti.foodcitipartener.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.WarningListAdapter;
import com.foodciti.foodcitipartener.keyboards.ExtendedKeyBoard;
import com.foodciti.foodcitipartener.realm_entities.CustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.RemarkType;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class WarningListDialog extends DialogFragment implements View.OnFocusChangeListener,View.OnKeyListener{
    private static final String TAG = "WarningListDialog";

    private static final String OPTION_ALL = "All";
    private static final String OPTION_WARNINGS = "Warnings";
    private RecyclerView warningRV;
    private Spinner spinner;
    private String type = "All";

    private RealmResults<CustomerInfo> customerInfoRealmResultsAsync = null;
    private ExtendedKeyBoard keyBoard;
    private WarningListDialog(){}

    public static WarningListDialog newInstance() {
        WarningListDialog warningListDialog = new WarningListDialog();
        warningListDialog.setCancelable(true);
        return warningListDialog;
    }


    @Override
    public void onDestroyView() {
        if (customerInfoRealmResultsAsync != null)
            customerInfoRealmResultsAsync.removeAllChangeListeners();
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_warninglist_dialog, container,
                false);
        Realm realm = RealmManager.getLocalInstance();
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int height=display.getHeight();
        keyBoard = rootView.findViewById(R.id.keyBoard);
        int hight=(int)(height*0.50);
        keyBoard.getLayoutParams().height = hight;
        keyBoard.requestLayout();
        View closebtn = rootView.findViewById(R.id.close);
        closebtn.setOnClickListener(v -> {
            dismiss();
        });

        final EditText searchET = rootView.findViewById(R.id.search_text);
        searchET.setShowSoftInputOnFocus(false);
        searchET.setOnKeyListener(this);
        searchET.setOnFocusChangeListener(this);
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final RealmQuery<CustomerInfo> customerInfoRealmQuery = realm.where(CustomerInfo.class).isNotEmpty("remarkStatus");
                if (type.equals(OPTION_WARNINGS)) {
                    customerInfoRealmQuery.and().notEqualTo("remarkStatus", "Normal");
                } else if (!type.equals(OPTION_ALL)) {
                    customerInfoRealmQuery.and().equalTo("remarkStatus", type);
                }
                if (!s.toString().trim().isEmpty()) {
                    customerInfoRealmQuery.and()
                            .beginGroup().contains("phone", s.toString().trim(), Case.INSENSITIVE).or().contains("name", s.toString().trim(), Case.INSENSITIVE)
                            .endGroup();
                }
                customerInfoRealmResultsAsync = customerInfoRealmQuery.findAllAsync();
                customerInfoRealmResultsAsync.addChangeListener(results -> {
                    warningRV.setAdapter(new WarningListAdapter(getActivity(), results));
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        warningRV = rootView.findViewById(R.id.warningRV);
        warningRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        warningRV.setHasFixedSize(true);


        spinner = rootView.findViewById(R.id.warning_spinner);
//        final RealmQuery<CustomerInfo> customerInfoRealmQuery=realm.where(CustomerInfo.class).isNotEmpty("remarks");
        RealmResults<RemarkType> remarkTypes = realm.where(RemarkType.class).findAll();
        List<String> strings = new ArrayList<>();
        strings.add(OPTION_ALL);
        strings.add(OPTION_WARNINGS);
        for (RemarkType rt : remarkTypes)
            strings.add(rt.getType());
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, strings);
        spinner.setAdapter(stringArrayAdapter);
        spinner.setSelection(1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = strings.get(position);
                final RealmQuery<CustomerInfo> customerInfoRealmQuery = realm.where(CustomerInfo.class).isNotEmpty("remarkStatus");
                if (type.equals(OPTION_WARNINGS)) {
                    customerInfoRealmQuery.and().notEqualTo("remarkStatus", "Normal");
                } else if (!type.equals(OPTION_ALL)) {
                    customerInfoRealmQuery.and().equalTo("remarkStatus", type);
                }
                customerInfoRealmResultsAsync = customerInfoRealmQuery.findAllAsync();
                customerInfoRealmResultsAsync.addChangeListener(results -> {
                    warningRV.setAdapter(new WarningListAdapter(getActivity(), results));
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*Button search=rootView.findViewById(R.id.search);
        search.setOnClickListener(v->{
            final RealmQuery<CustomerInfo> customerInfoRealmQuery=realm.where(CustomerInfo.class).isNotEmpty("remarkStatus");
            if(type.equals(OPTION_WARNINGS)) {
                customerInfoRealmQuery.and().notEqualTo("remarkStatus", "Normal");
            } else if(!type.equals(OPTION_ALL)) {
                customerInfoRealmQuery.and().equalTo("remarkStatus",type);
            }
            if(!searchET.getText().toString().trim().isEmpty())
                customerInfoRealmQuery.and().like("phone", searchET.getText().toString().trim());
            RealmResults<CustomerInfo> customerInfos=customerInfoRealmQuery.findAll();
            warningRV.setAdapter(new WarningListAdapter(getActivity(), customerInfos));
        });*/

        customerInfoRealmResultsAsync = realm.where(CustomerInfo.class).isNotEmpty("remarkStatus").and().notEqualTo("remarkStatus", "Normal").findAllAsync();
        customerInfoRealmResultsAsync.addChangeListener(results -> {
            warningRV.setAdapter(new WarningListAdapter(getActivity(), results));
        });

        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels);
            int height = (int) (getResources().getDisplayMetrics().heightPixels);

            dialog.getWindow().setLayout(width, height);
        }
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
            keyBoard.setInputConnection(ic);
        }
    }
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Log.e(TAG, "----------------------keyEvent---------------------");
        switch (keyCode) {
//            case ExtendedKeyBoard.KEYCODE_DONE:
//                Log.e(TAG, "-------------------keycode: " + keyCode);
////                saveUserData("ENTRY");
//                return true;
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
