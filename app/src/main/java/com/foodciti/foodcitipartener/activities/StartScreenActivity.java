package com.foodciti.foodcitipartener.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.PincodeArrayAdapter;
import com.foodciti.foodcitipartener.realm_entities.PostalInfo;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class StartScreenActivity extends AppCompatActivity {
    private static final String TAG = "StartScreenActivity";
    private PincodeArrayAdapter pincodeArrayAdapter;
    private Spinner autoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        Realm realm = Realm.getDefaultInstance();
        /*realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Vendor vendor=realm.createObject(Vendor.class,1);
                vendor.setName(name.getText().toString().trim());
                vendor.setTel_no(telephone.getText().toString().trim());
            }
        });*/

        EditText name = findViewById(R.id.name);
        EditText telephone = findViewById(R.id.telephone);


        final List<PostalInfo> postalInfoList = new ArrayList<>();
        RealmResults<PostalInfo> postalInfos = realm.where(PostalInfo.class).findAll();
        postalInfoList.addAll(postalInfos);
        /*RealmResults<PostalInfo> postalInfos=realm.where(PostalInfo.class).findAllAsync();
        postalInfos.addChangeListener(new RealmChangeListener<RealmResults<PostalInfo>>() {
            @Override
            public void onChange(RealmResults<PostalInfo> postalInfos) {
                postalInfoList.addAll(postalInfos);
                pincodeArrayAdapter.notifyDataSetChanged();
                Log.e(TAG,"------------size: "+postalInfoList.size());
            }
        });*/
        pincodeArrayAdapter = new PincodeArrayAdapter(this, R.layout.postalinfo_spinner_item, postalInfoList);
        autoCompleteTextView = findViewById(R.id.postCode);
        autoCompleteTextView.setAdapter(pincodeArrayAdapter);
        autoCompleteTextView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PostalInfo postalInfo = (PostalInfo) parent.getItemAtPosition(position);
                Toast.makeText(StartScreenActivity.this, postalInfo.getA_PostCode(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
