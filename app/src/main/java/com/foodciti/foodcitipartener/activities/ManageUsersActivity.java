package com.foodciti.foodcitipartener.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.CustomerInfoAdapter;
import com.foodciti.foodcitipartener.keyboards.ExtendedKeyBoard;
import com.foodciti.foodcitipartener.realm_entities.CustomerInfo;
import com.foodciti.foodcitipartener.utils.RealmManager;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

public class ManageUsersActivity extends AppCompatActivity implements View.OnFocusChangeListener, View.OnKeyListener {

    private static final String TAG = "ManageUsersActivity";
    private RecyclerView userinfoRV;
    private CustomerInfoAdapter customerInfoAdapter;
    private Realm realm;
    private ProgressBar progressBar;
    private EditText searchET;
    private RealmResults<CustomerInfo> searchResultsAsync;
    private ExtendedKeyBoard keyBoard;

    private void initViews() {
        keyBoard = findViewById(R.id.keyBoard);
        userinfoRV = findViewById(R.id.userinfoRV);
        RecyclerView.LayoutManager linearLayout = new LinearLayoutManager(this);
        userinfoRV.setLayoutManager(linearLayout);

        progressBar = findViewById(R.id.loader);
        progressBar.setVisibility(View.VISIBLE);
        searchResultsAsync = realm.where(CustomerInfo.class).isNotEmpty("phone").findAllAsync();
        searchResultsAsync.addChangeListener(result -> {
            customerInfoAdapter = new CustomerInfoAdapter(ManageUsersActivity.this, result, realm);
            userinfoRV.setAdapter(customerInfoAdapter);
            progressBar.setVisibility(View.GONE);
        });

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        searchET = findViewById(R.id.search);
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                progressBar.setVisibility(View.VISIBLE);
                searchResultsAsync = realm.where(CustomerInfo.class).contains("phone", s.toString().trim(), Case.INSENSITIVE).or().contains("name", s.toString().trim(), Case.INSENSITIVE).findAllAsync();
                searchResultsAsync.addChangeListener(result -> {
                    customerInfoAdapter = new CustomerInfoAdapter(ManageUsersActivity.this, result, realm);
                    userinfoRV.setAdapter(customerInfoAdapter);
                    progressBar.setVisibility(View.GONE);
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchET.setShowSoftInputOnFocus(false);
        searchET.setOnKeyListener(this);
        searchET.setOnFocusChangeListener(this);

        searchET.requestFocus();
    }

    @Override
    protected void onPause() {
        if (searchResultsAsync != null)
            searchResultsAsync.removeAllChangeListeners();
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);
        realm = RealmManager.getLocalInstance();

        initViews();
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
            case ExtendedKeyBoard.KEYCODE_DONE:
                Log.e(TAG, "-------------------keycode: " + keyCode);
//                saveUserData("ENTRY");
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
