package com.foodciti.foodcitipartener.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.DriverDetailsListAdapter;
import com.foodciti.foodcitipartener.realm_entities.Driver;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.util.Date;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;

public class ManageDriversActivity extends AppCompatActivity implements DriverDetailsListAdapter.OnClickListener {
    private static final String TAG = "ManageDriversActivity";
    private RecyclerView driverDetailsRV;
    private DriverDetailsListAdapter driverDetailsListAdapter;
    private Realm realm;
    private Button addDriver;
    private Random rnd;
    private EditText driverSearch;
    private View close;

    private RealmResults<Driver> driverRealmResultsAsync = null;

    private void initViews() {
        driverDetailsRV = findViewById(R.id.driver_details_RV);
        driverDetailsRV.setHasFixedSize(true);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        driverDetailsRV.setLayoutManager(lm);
        driverRealmResultsAsync = realm.where(Driver.class).findAllAsync();
        driverRealmResultsAsync.addChangeListener(rs -> {
            driverDetailsListAdapter = new DriverDetailsListAdapter(ManageDriversActivity.this, rs);
            driverDetailsRV.setAdapter(driverDetailsListAdapter);
        });


        addDriver = findViewById(R.id.add_new_driver);
        addDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog alert = new AlertDialog.Builder(ManageDriversActivity.this).create();
                LayoutInflater inflater1 = (LayoutInflater) ManageDriversActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View view1 = inflater1.inflate(R.layout.driver_add_view, null);
                alert.setView(view1);

                final EditText driver, vechicle_number;
                TextView delivery_btn;
                driver = view1.findViewById(R.id.driver);
                vechicle_number = view1.findViewById(R.id.vechicle_number);
                ImageView close;
                close = view1.findViewById(R.id.close);

                delivery_btn = view1.findViewById(R.id.delivery_btn);


                delivery_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (driver.getText().toString().equals("") || vechicle_number.getText().toString().equals("")) {

                        } else {
//                            Random rnd = new Random(System.currentTimeMillis());
//                            int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
//                            final int color = Color.argb(255, rnd.nextInt(50), rnd.nextInt(50), rnd.nextInt(50));
                            int tmpColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(200), rnd.nextInt(200));
                            while (realm.where(Driver.class).equalTo("color", tmpColor).count() > 0) {
                                tmpColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(200), rnd.nextInt(200));
                            }
                            Log.e(TAG, "-------color: " + tmpColor);
                            final int color = tmpColor;

                            realm.executeTransaction(realm -> {
                                Number max = realm.where(Driver.class).max("id");
                                long nextId = (max == null) ? 1 : max.longValue() + 1;
                                Driver d = realm.createObject(Driver.class, nextId);
                                d.setColor(color);
                                d.setDriver_name(driver.getText().toString().trim());
                                d.setDriver_vehicle_no(vechicle_number.getText().toString().trim());
                                d.setRegistrationDate(new Date().getTime());
//                                driverDetailsListAdapter.notifyDataSetChanged();
                            });

                            Toast.makeText(ManageDriversActivity.this, "Driver Added Successfully !", Toast.LENGTH_LONG).show();
                            alert.dismiss();
                        }
                    }
                });

                /*final Map<String, Integer> colorMap = new CommonMethods(ManageDriversActivity.this).getColorMap();
                Spinner colorSpinner=view1.findViewById(R.id.colorSpinner);
                ArrayAdapter<String> integerArrayAdapter = new ArrayAdapter<>(ManageDriversActivity.this, android.R.layout.simple_spinner_item, new ArrayList<>(colorMap.keySet()));
                integerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                colorSpinner.setAdapter(integerArrayAdapter);
                colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Integer color = colorMap.get(parent.getItemAtPosition(position));
                        Toast.makeText(parent.getContext(), "Selected: " + color, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
*/
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                    }
                });
                alert.show();
            }
        });

        driverSearch = findViewById(R.id.search_driver);

        driverSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                driverRealmResultsAsync = realm.where(Driver.class).contains("driver_name", s.toString().trim())
                        .or().contains("driver_vehicle_no", s.toString().trim()).findAllAsync();
                driverRealmResultsAsync.addChangeListener(rs -> {
                    driverDetailsListAdapter = new DriverDetailsListAdapter(ManageDriversActivity.this, rs);
                    driverDetailsRV.setAdapter(driverDetailsListAdapter);
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        close = findViewById(R.id.close);
        close.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manage_drivers);
        realm = RealmManager.getLocalInstance();
        rnd = new Random(System.currentTimeMillis());
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        realm = RealmManager.getRealmFor(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
//        RealmManager.closeRealmFor(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (driverRealmResultsAsync != null)
            driverRealmResultsAsync.removeAllChangeListeners();
        super.onPause();
    }

    @Override
    public void onClick(long driverId) {
        Log.e(TAG, "---------driverId: " + driverId);
    }
}
