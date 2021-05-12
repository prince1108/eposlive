package com.foodciti.foodcitipartener.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.keyboards.ExtendedKeyBoard;
import com.foodciti.foodcitipartener.realm_entities.Vendor;
import com.foodciti.foodcitipartener.utils.Preferences;
import com.foodciti.foodcitipartener.utils.RealmManager;
import com.foodciti.foodcitipartener.utils.SessionManager;

import java.util.concurrent.atomic.AtomicReference;

import io.realm.Realm;

import static com.foodciti.foodcitipartener.utils.AppConfig.MyPREFERENCES;

public class VendorInfoActivity extends AppCompatActivity implements View.OnKeyListener, View.OnFocusChangeListener {
    private String TAG="VendorInfoActivity";
    public static final String INTENT_ARG_EDITMODE="editmode";
    private EditText nameET, titleET, telephoneET, pinET, addressET, addressET2, resaurentIDET,addressET3, vatNoET, companyNoET, adminPassET,optInHome;
    private Realm realm;
    private Vendor vendor;
    private ExtendedKeyBoard keyBoard;
    private boolean isEditMode=false;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_info);
        sessionManager=new SessionManager(this);
        isEditMode = getIntent().getBooleanExtra(INTENT_ARG_EDITMODE, false);
        initViews();
        /*findViewById(R.id.submit).setOnClickListener(v->{
            done();
        });*/
    }

    private void setTempData() {
        titleET.setText("1");
        nameET.setText("1");
        pinET.setText("1");
        telephoneET.setText("1");
        vatNoET.setText("1");
        companyNoET.setText("1");
        adminPassET.setText("1");
        optInHome.setText("yes");
        resaurentIDET.setText("1");
        addressET.setText("1");
        addressET2.setText("1");
    }

    private void done() {
        if(!isEditMode) {
            if (saveInfo()) {
                startActivity(new Intent(VendorInfoActivity.this, NewCustomerInfoActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Make sure you fill all the mandatory fields", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (!saveInfo()) {
                Toast.makeText(this, "Make sure you fill all the mandatory fields", Toast.LENGTH_SHORT).show();
            } else {
                onBackPressed();
            }
        }
    }

    private boolean saveInfo() {
        AtomicReference<Boolean> booleanAtomicReference = new AtomicReference<>(false);
        final String title = titleET.getText().toString().trim();
        final String name = nameET.getText().toString().trim();
        final String pin = pinET.getText().toString().trim();
        final String telephone =telephoneET.getText().toString().trim();
        final String vatNo =vatNoET.getText().toString().trim();
        final String companyNo = companyNoET.getText().toString().trim();
        final String adminPassword = adminPassET.getText().toString().trim();
        final String _optInHome = optInHome.getText().toString().trim();
        final  String restroID=resaurentIDET.getText().toString().trim();
        String add1=addressET.getText().toString().trim();
        String add2=addressET2.getText().toString().trim();
        final String address = add1+","+add2;

        if(!title.isEmpty() && !name.isEmpty() && !pin.isEmpty() && !address.isEmpty() && !telephone.isEmpty() && !adminPassword.isEmpty()&& !_optInHome.isEmpty()&&_optInHome.equalsIgnoreCase("yes")||_optInHome.equalsIgnoreCase("no")) {
            realm.executeTransaction(r -> {
                vendor.setName(name);
                vendor.setTitle(title);
                vendor.setPin(pin);
                vendor.setTel_no(telephone);
                vendor.setAddress(address);
                vendor.setVatNo(vatNo);
                vendor.setCompanyNo(companyNo);
                vendor.setAdmin_password(adminPassword);
                vendor.setOptInHomePage(_optInHome);
                vendor.setRestroID(restroID);
//                sessionManager.setFoodTruckId(restroID);
            });
            booleanAtomicReference.set(true);
        }

        return booleanAtomicReference.get();
    }

    @Override
    protected void onResume() {
        super.onResume();
        realm = RealmManager.getLocalInstance();
        vendor = realm.where(Vendor.class).findFirst();
        if(vendor!=null) {
            titleET.setText(vendor.getTitle());
            nameET.setText(vendor.getName());
            telephoneET.setText(vendor.getTel_no());
            pinET.setText(vendor.getPin());
            try {
                String[] arrSplit=null;
                if(vendor.getAddress()!=null&&!vendor.getAddress().equalsIgnoreCase("")) {
                    arrSplit = vendor.getAddress().split(",");
                    if (arrSplit != null && arrSplit[0] != null)
                        addressET.setText(arrSplit[0]);
                    if (arrSplit[1] != null)
                        addressET2.setText(arrSplit[1]);
                }
            }catch (Exception e){

            }

            vatNoET.setText(vendor.getVatNo());
            adminPassET.setText(vendor.getAdmin_password());
            optInHome.setText(vendor.getOptInHomePage());
            companyNoET.setText(vendor.getCompanyNo());
            resaurentIDET.setText(vendor.getRestroID());
        } else {
            realm.executeTransaction(r->{
                Number maxId = r.where(Vendor.class).max("id");
                long nextId = (maxId == null) ? 1 : maxId.longValue();
                vendor = r.createObject(Vendor.class, nextId);
            });
        }

        setTempData();
    }

    private void initViews() {
        nameET = findViewById(R.id.nameET);
        nameET.setShowSoftInputOnFocus(false);
        nameET.setOnKeyListener(this);
        nameET.setOnFocusChangeListener(this);

        titleET = findViewById(R.id.titleET);
        titleET.setShowSoftInputOnFocus(false);
        titleET.setOnKeyListener(this);
        titleET.setOnFocusChangeListener(this);

        telephoneET = findViewById(R.id.telET);
        telephoneET.setShowSoftInputOnFocus(false);
        telephoneET.setOnKeyListener(this);
        telephoneET.setOnFocusChangeListener(this);

        pinET = findViewById(R.id.pinET);
        pinET.setShowSoftInputOnFocus(false);
        pinET.setOnKeyListener(this);
        pinET.setOnFocusChangeListener(this);

        addressET = findViewById(R.id.addressET);
        addressET.setShowSoftInputOnFocus(false);
        addressET.setOnKeyListener(this);
        addressET.setOnFocusChangeListener(this);

        addressET2 = findViewById(R.id.addressET2);
        addressET2.setShowSoftInputOnFocus(false);
        addressET2.setOnKeyListener(this);
        addressET2.setOnFocusChangeListener(this);

        resaurentIDET = findViewById(R.id.resaurentIDET);
        resaurentIDET.setShowSoftInputOnFocus(false);
        resaurentIDET.setOnKeyListener(this);
        resaurentIDET.setOnFocusChangeListener(this);


        vatNoET = findViewById(R.id.vatNoET);
        vatNoET.setShowSoftInputOnFocus(false);
        vatNoET.setOnKeyListener(this);
        vatNoET.setOnFocusChangeListener(this);

        companyNoET = findViewById(R.id.companyNoET);
        companyNoET.setShowSoftInputOnFocus(false);
        companyNoET.setOnKeyListener(this);
        companyNoET.setOnFocusChangeListener(this);

        adminPassET = findViewById(R.id.adminPassET);
        adminPassET.setShowSoftInputOnFocus(false);
        adminPassET.setOnKeyListener(this);
        adminPassET.setOnFocusChangeListener(this);

        optInHome = findViewById(R.id.optInHome);
        optInHome.setShowSoftInputOnFocus(false);
        optInHome.setOnKeyListener(this);
        optInHome.setOnFocusChangeListener(this);

        keyBoard = findViewById(R.id.keyBoard);
        Button skip = findViewById(R.id.skip);
        skip.setOnClickListener(v->{
            startActivity(new Intent(VendorInfoActivity.this, NewCustomerInfoActivity.class));
            finish();
        });

        if(isEditMode)
            skip.setVisibility(View.GONE);
        else
            skip.setVisibility(View.VISIBLE);


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
                done();
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
