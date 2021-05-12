package com.foodciti.foodcitipartener.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.AddressAdapter;
import com.foodciti.foodcitipartener.adapters.OrderAdapter;
import com.foodciti.foodcitipartener.adapters.SpinnerAdapter;
import com.foodciti.foodcitipartener.dialogs.CustomAlertDialog;
import com.foodciti.foodcitipartener.dialogs.CustomTextDialog;
import com.foodciti.foodcitipartener.dialogs.CustomerAddressDialoge;
import com.foodciti.foodcitipartener.dialogs.ItemSubDetails;
import com.foodciti.foodcitipartener.dialogs.OrderInfo;
import com.foodciti.foodcitipartener.dialogs.TablesFullViewDialog;
import com.foodciti.foodcitipartener.gson.Item;
import com.foodciti.foodcitipartener.gson.PostalData;
import com.foodciti.foodcitipartener.gson.RemarkTypeJson;
import com.foodciti.foodcitipartener.gson.UserData;
import com.foodciti.foodcitipartener.keyboards.ExtendedKeyBoard;
import com.foodciti.foodcitipartener.keyboards.NumPad;
import com.foodciti.foodcitipartener.realm_entities.Addon;
import com.foodciti.foodcitipartener.realm_entities.CustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.MenuCategory;
import com.foodciti.foodcitipartener.realm_entities.MenuItem;
import com.foodciti.foodcitipartener.realm_entities.PostalInfo;
import com.foodciti.foodcitipartener.realm_entities.RemarkType;
import com.foodciti.foodcitipartener.response.GenericResponse;
import com.foodciti.foodcitipartener.response.Order;
import com.foodciti.foodcitipartener.response.OrderedItem;
import com.foodciti.foodcitipartener.response.SubItem;
import com.foodciti.foodcitipartener.rest.RetroClient;
import com.foodciti.foodcitipartener.services.CallerIDService;
import com.foodciti.foodcitipartener.services.FirebaseBackgroundService;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.Consts;
import com.foodciti.foodcitipartener.utils.DataInitializers;
import com.foodciti.foodcitipartener.utils.InternetConnection;
import com.foodciti.foodcitipartener.utils.RealmManager;
import com.foodciti.foodcitipartener.utils.RecyclerViewItemSelectionAfterLayoutUpdate;
import com.foodciti.foodcitipartener.utils.SessionManager;
import com.foodciti.foodcitipartener.utils.StringHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static com.foodciti.foodcitipartener.utils.AppConfig.MyPREFERENCES;

public class NewCustomerInfoActivity extends AppCompatActivity implements View.OnClickListener, AddressAdapter.AddressAdapterCAllback
        , View.OnKeyListener, OrderAdapter.OnItemClickListener, View.OnFocusChangeListener,OrderInfo.OnClickListener {

    private static final String TAG = "NewCustomerInfoActivity";
//    private DatabaseReference mFirebaseDatabase;
//    private FirebaseDatabase mFirebaseInstance;
    private Realm realm;
    private Set<String> dataQueue = new HashSet<>();
    private ExtendedKeyBoard primaryKeyboard;
    private NumPad numPad;
    private String foodTruckId="";
    private RecyclerView customerDataRV;
    private AddressAdapter adapter;

    private EditText postCodeET, telephoneNoET, houseNoET, cityET, remarksET, nameET;
    private Spinner remarkStatusSpinner;

    int[] remarksColor = {R.color.menuList, R.color.addItemColor, R.color.pink, R.color.colorAccent, R.color.colorVividTangerine};
    private static final int MENU_SCREEN_REQUEST = 101;
    private SpinnerAdapter spinnerAdapter;

    private ProgressBar progressBar;
    private CustomerInfo infoBean = null;

    private DisposableSubscriber<PostalInfo> postalInfoDisposableSubscriber=null;
    private final List<RealmChangeListener> realmChangeListeners = new ArrayList<>();  // keeps strond reference and prevents early garbage collection

    private LocalBroadcastManager localBroadcastManager;

    private BroadcastReceiver telephoneBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String telephone = intent.getStringExtra(CallerIDService.RECEIVED_PHONE_NO);
            telephoneNoET.setText(telephone);
            CallerIDService.callerIDService.setCallerIdCaptured(true);
        }
    };

    public static <E extends RealmObject> Object[] createBeanIfNotExists(Class<E> aClass, String primarykeyField, String primaryKey, Realm realm) {
        AtomicReference<E> beanRef = new AtomicReference<>();
        E bean = realm.where(aClass).equalTo(primarykeyField, primaryKey).findFirst();
        beanRef.set(bean);
        final Object[] results = new Object[2];
        results[1] = Boolean.valueOf(false);
        if (bean == null) {
            Log.e(TAG, "-----------------creating new bean of type: " + aClass.getSimpleName());
            if (!realm.isInTransaction()) {
                realm.executeTransaction(r -> {
                    Number maxId = r.where(aClass).max("id");
                    long nextId = (maxId == null) ? 0 : maxId.longValue() + 1;
                    beanRef.set(r.createObject(aClass, nextId));
                    results[1] = Boolean.valueOf(true);
                });
            } else {
                Number maxId = realm.where(aClass).max("id");
                long nextId = (maxId == null) ? 0 : maxId.longValue() + 1;
                beanRef.set(realm.createObject(aClass, nextId));
                results[1] = Boolean.valueOf(true);
            }
        } else
            Log.e(TAG, "-----------------bean exists of type: " + aClass.getSimpleName());
        results[0] = beanRef.get();
        return results;
    }

    public CustomerInfo insertUserInfoForNonDeliveryOrders() {
        final AtomicReference<CustomerInfo> atomicReference = new AtomicReference<>();
        realm.executeTransaction(r -> {
            CustomerInfo bean = null;
            final String phone = telephoneNoET.getText().toString().trim();
            final String name = nameET.getText().toString().trim();
            final String postCode = postCodeET.getText().toString().trim();
            final String houseNo = houseNoET.getText().toString().trim();
            final String city = cityET.getText().toString().trim();

            if (phone.isEmpty() && name.isEmpty() && postCode.isEmpty() && houseNo.isEmpty() && city.isEmpty()) {
                bean = r.where(CustomerInfo.class)
                        .isNull("postalInfo").and()
                        .isEmpty("name").and()
                        .isEmpty("phone").and()
                        .isEmpty("house_no").findFirst();
                if (bean == null) {
                    Number maxId = r.where(CustomerInfo.class).max("id");
                    long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
                    bean = r.createObject(CustomerInfo.class, nextId);
                }
                atomicReference.set(bean);
            } else {
                if (!phone.isEmpty()&&phone.length()>8) {
                    Object[] results = createBeanIfNotExists(CustomerInfo.class, "phone", phone, realm);
                    bean = (CustomerInfo) results[0];
                    Boolean justCreated = (Boolean) results[1];
                    if (justCreated.booleanValue())
                        bean.setPhone(phone);
                } else {
                    Number maxId = r.where(CustomerInfo.class).max("id");
                    long nextId = (maxId == null) ? 0 : maxId.longValue() + 1;
                    bean = r.createObject(CustomerInfo.class, nextId);
                }
                atomicReference.set(bean);
                if (!name.isEmpty())
                    atomicReference.get().setName(name);
                if (!postCode.isEmpty()) {
                    Object[] results = createBeanIfNotExists(PostalInfo.class, "A_PostCode", postCode, realm);
                    Log.e(TAG, "------------------fetched results for postal code-----------------");
                    if (results[0] == null) {
                        Log.e(TAG, "-----------fetched postal code is null-------------");
                    }
                    PostalInfo postalInfo = (PostalInfo) results[0];
                    if (results[1] == null) {
                        Log.e(TAG, "-----------fetched boolean result is null-------------");
                    }
                    Boolean justCreated = (Boolean) results[1];
                    if (justCreated) {
                        Log.e(TAG, "----------------justCreated-------------------");
                        postalInfo.setA_PostCode(postCode);
                        Log.e(TAG, "----------------------postcode set------------------------");
                    }
                    if (!city.isEmpty())
                        postalInfo.setAddress(city);
                    atomicReference.get().setPostalInfo(postalInfo);
                }
                if (!houseNo.isEmpty())
                    atomicReference.get().setHouse_no(houseNo);

                Log.e(TAG, "----------------setting remarks-----------------");
                atomicReference.get().setRemarks(remarksET.getText().toString().trim());
                RemarkType remarkType = (RemarkType) remarkStatusSpinner.getSelectedItem();
                if (remarkType != null) {
                    Log.e(TAG, "----------------remarkType not null-----------------");
                    atomicReference.get().setRemarkStatus(remarkType.getType());
                } else {
                    Log.e(TAG, "----------------remarks null-----------------");
                }
            }
        });
        Log.e(TAG, "----------------returning from method-----------------");
        return atomicReference.get();
    }

    private CustomerInfo insertUserInfo() {
        AtomicReference<CustomerInfo> bean = new AtomicReference<>();
        realm.executeTransaction(r -> {
            bean.set(r.where(CustomerInfo.class).equalTo("phone", telephoneNoET.getText().toString().trim()).findFirst());
            if (bean.get() != null) {
                PostalInfo postalInfo = r.where(PostalInfo.class).equalTo("A_PostCode", postCodeET.getText().toString().trim()).findFirst();
                if (postalInfo == null) {
                    Number max = r.where(PostalInfo.class).max("id");
                    long next = (max == null) ? 1 : max.longValue() + 1;
                    postalInfo = r.createObject(PostalInfo.class, next);
                    postalInfo.setA_PostCode(postCodeET.getText().toString().trim());
                    postalInfo.setAddress(cityET.getText().toString().trim());
                    postalInfo.setInfo_last_updated(new Date().getTime());
                } else {
                    postalInfo.setAddress(cityET.getText().toString().trim());
                    postalInfo.setInfo_last_updated(new Date().getTime());
                }
                bean.get().setPostalInfo(postalInfo);
                bean.get().setName(nameET.getText().toString().trim());
                bean.get().setHouse_no(houseNoET.getText().toString().trim());
                bean.get().setUser_visited_date_time(new Date().getTime());
                bean.get().setInfo_last_updated(new Date().getTime());
                bean.get().setRemarks(remarksET.getText().toString().trim());
                RemarkType remarkType = (RemarkType) remarkStatusSpinner.getSelectedItem();
                if (remarkType != null)
                    bean.get().setRemarkStatus(remarkType.getType());
                return;
            }

            Number maxId = r.where(CustomerInfo.class).max("id");
            long nextId = (maxId == null) ? 0 : maxId.longValue() + 1;
            bean.set(r.createObject(CustomerInfo.class, nextId));
            RealmQuery<PostalInfo> realmQuery = r.where(PostalInfo.class);
            realmQuery.equalTo("A_PostCode", postCodeET.getText().toString().trim());
            PostalInfo postalInfo = realmQuery.findFirst();
            if (postalInfo == null) {
                Number max = r.where(PostalInfo.class).max("id");
                long next = (max == null) ? 1 : max.longValue() + 1;
                postalInfo = r.createObject(PostalInfo.class, next);
                postalInfo.setA_PostCode(postCodeET.getText().toString().trim());
                postalInfo.setAddress(cityET.getText().toString().trim());
                postalInfo.setInfo_last_updated(new Date().getTime());
            } else {
                postalInfo.setAddress(cityET.getText().toString().trim());
                postalInfo.setInfo_last_updated(new Date().getTime());
            }
            bean.get().setPostalInfo(postalInfo);
            bean.get().setName(nameET.getText().toString().trim());
            bean.get().setPhone(telephoneNoET.getText().toString().trim());
            bean.get().setHouse_no(houseNoET.getText().toString().trim());
            bean.get().setRemarks(remarksET.getText().toString());
            RemarkType remarkType = (RemarkType) remarkStatusSpinner.getSelectedItem();
            if (remarkType != null)
                bean.get().setRemarkStatus(remarkType.getType());
            bean.get().setUser_visited_date_time(new Date().getTime());
        });
        return bean.get();
    }

    private void clearFields() {
        nameET.setText("");
        postCodeET.setText("");
        telephoneNoET.setText("");
        houseNoET.setText("");
        cityET.setText("");
    }

    private void saveUserData(String from) {
        if (!TextUtils.isEmpty(postCodeET.getText().toString())) {
            if (!TextUtils.isEmpty(telephoneNoET.getText().toString())) {
                if (!TextUtils.isEmpty(houseNoET.getText().toString())) {
                    if (!TextUtils.isEmpty(cityET.getText().toString())) {
                        if (telephoneNoET.getText().toString().length()>8) {
                            infoBean = insertUserInfo();
                        }
                        if (from.equalsIgnoreCase("DELIVERY")) {
                            Intent intent = new Intent(this, BasicMenuActivity.class);
                            if(infoBean!=null)
                                intent.putExtra("USER_DATA_ID", infoBean.getId());
                            intent.putExtra("ORDER_TYPE", Constants.TYPE_DELIVERY);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "User information saved", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "City or Street name required", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "House number required", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Phone number required", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Postal Code required", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvDelivery:
            case R.id.tvDeliveryIcon:
            case R.id.delivery_btn: {
                SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("type_mode","delivery");
                editor.commit();
                Log.d(TAG, "onClick: hello" + sharedPreferences.getString("type_mode","none"));
                try {
                    infoBean = insertUserInfoForNonDeliveryOrders();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(this, BasicMenuActivity.class);
                if (infoBean != null) {
                    intent.putExtra("USER_DATA_ID", infoBean.getId());
                } else
                    Log.e(TAG, "-----------------infobean is null-----------------");
                intent.putExtra("ORDER_TYPE", Constants.TYPE_DELIVERY);
                startActivityForResult(intent, MENU_SCREEN_REQUEST);
            }
            //saveUserData("DELIVERY");
            break;

            case R.id.collection_btn:
            case R.id.tvCollection:
            case R.id.collection_icon: {
                try {
                    infoBean = insertUserInfoForNonDeliveryOrders();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(this, BasicMenuActivity.class);
                if (infoBean != null) {
                    intent.putExtra("USER_DATA_ID", infoBean.getId());
                } else
                    Log.e(TAG, "-----------------infobean is null-----------------");
                intent.putExtra("ORDER_TYPE", Constants.TYPE_COLLECTION);
                startActivityForResult(intent, MENU_SCREEN_REQUEST);
            }
            break;

            case R.id.tvTable:
            case R.id.table_icon:
            case R.id.table_btn: {
                infoBean = insertUserInfoForNonDeliveryOrders();
                Intent intent = new Intent(this, BasicTableMenuActivity.class);
                if (infoBean != null) {
                    intent.putExtra("USER_DATA_ID", infoBean.getId());
                }
                intent.putExtra("ORDER_TYPE", Constants.TYPE_TABLE);
                startActivity(intent);
            }
            break;

            case R.id.remark:
                CustomTextDialog customTextDialog = CustomTextDialog.getInstance();
                customTextDialog.setMessage(remarksET.getText().toString().trim());
                if (!remarksET.getText().toString().trim().isEmpty())
                    customTextDialog.show(getSupportFragmentManager(), null);
                break;
        }
    }

    public void hideKeyBoard(View v) {
        final InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public void onClickAddress(int position) {
        PostalInfo postalInfo = adapter.getPostalInfos().get(position);
        postCodeET.setText(postalInfo.getA_PostCode());
        cityET.setText("");
        String address = (postalInfo.getAddress() == null) ? "" : postalInfo.getAddress().trim();
        String[] tokens = StringHelper.capitalizeEachWordAfterComma(address).split(",");
        List<String> stringList = Arrays.asList(tokens);
        Iterator<String> stringIterator = stringList.iterator();
        while (stringIterator.hasNext()) {
            final String str = stringIterator.next();
            if (stringIterator.hasNext())
                cityET.append(str.trim() + ",\n");
            else
                cityET.append(str.trim());
        }
        houseNoET.requestFocus();
    }

    @Override
    public void onDeletePostalInfo(long id, int position) {
        PostalInfo postalInfo = adapter.getPostalInfos().get(position);
        CustomAlertDialog customAlertDialog = CustomAlertDialog.getInstance();
        customAlertDialog.setTitle("Delete PostalInfo");
        customAlertDialog.setMessage("Are you sure you want to delete this postalinfo?");
        customAlertDialog.setPositiveButton("Yes", dialog -> {
            dialog.dismiss();
            realm.executeTransaction(r->{
                postalInfo.deleteFromRealm();
                adapter.notifyItemRemoved(position);
            });
        });
        customAlertDialog.setNegativeButton("No", dialog -> {
            dialog.dismiss();
        });

        customAlertDialog.show(getSupportFragmentManager(), null);
    }

    private void initRecyclerViews() {
        adapter = new AddressAdapter(this, null);
        customerDataRV.setAdapter(adapter);
    }

    private void initializeViews() {
        nameET = findViewById(R.id.name);
        postCodeET = findViewById(R.id.postcode);
        telephoneNoET = findViewById(R.id.telephone);
        houseNoET = findViewById(R.id.houseNo);
        cityET = findViewById(R.id.customerAddress);
        remarksET = findViewById(R.id.remark);
        remarksET.setOnClickListener(this);
        customerDataRV = findViewById(R.id.suggestion_field);
        remarkStatusSpinner = findViewById(R.id.remark_status_spinner);

        // click listener
        postCodeET.setOnClickListener(this);
        telephoneNoET.setOnClickListener(this);

        // text change listener
        postCodeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    cityET.setText("");
                    postCodeET.setTypeface(postCodeET.getTypeface(), Typeface.ITALIC);
                    if(adapter !=null && adapter.getPostalInfos()==null)
                        setPostalCodes();
                    else if(adapter!=null && adapter.getPostalInfos().isEmpty())
                        setPostalCodes();
                } else {
                    String str = s.toString();
                    if (!str.equals(str.toUpperCase())) {
                        str = str.toUpperCase();
                        postCodeET.setText(str);
                        postCodeET.setSelection(postCodeET.length()); //fix reverse texting
                    }
                    postCodeET.setTypeface(postCodeET.getTypeface(), Typeface.BOLD);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().trim().equals(s.toString().trim().toUpperCase()) || s.toString().trim().isEmpty())
                    return;


                try {
                    RealmResults<PostalInfo> dynamicSearchResults = realm.where(PostalInfo.class).contains("A_PostCode", s.toString().trim(), Case.INSENSITIVE).findAllAsync();
                    RealmChangeListener<RealmResults<PostalInfo>> infoRealmChangeListener = new RealmChangeListener<RealmResults<PostalInfo>>() {
                        @Override
                        public void onChange(RealmResults<PostalInfo> results) {
                            if(results == null || !results.isValid() || results.isEmpty()) {
                                Log.e(TAG, "---------------------invalid result obj-------------------");
                            } else {
                                adapter.setPostalInfos(results);
                                adapter.notifyDataSetChanged();
                            }
                            dynamicSearchResults.removeChangeListener(this);
                            realmChangeListeners.remove(this);
                        }
                    };

                    realmChangeListeners.add(infoRealmChangeListener);
                    dynamicSearchResults.addChangeListener(infoRealmChangeListener);


                    PostalInfo postalInfo = realm.where(PostalInfo.class).equalTo("A_PostCode", s.toString().trim()).findFirstAsync();
                    RealmChangeListener<PostalInfo> changeListener = new RealmChangeListener<PostalInfo>() {
                        @Override
                        public void onChange(PostalInfo pI) {
                            if(pI == null || !pI.isValid()) {
                                Log.e(TAG, "---------------pI is NULL----------------------");
                            } else {
                                Log.e(TAG, "---------------postal info found-------------"+s.toString());
                                cityET.setText("");
                                String[] addsr = StringHelper.capitalizeEachWordAfterComma(pI.getAddress().trim()).split(",");
                                List<String> stringList = Arrays.asList(addsr);
                                Iterator<String> stringIterator = stringList.iterator();
                                while (stringIterator.hasNext()) {
                                    final String st = stringIterator.next();
                                    if (stringIterator.hasNext())
                                        cityET.append(st.trim() + ",\n");
                                    else
                                        cityET.append(st.trim());
                                }
                                CustomerAddressDialoge tablesFullViewDialog = CustomerAddressDialoge.newInstance();
                                Bundle bundle = new Bundle();
                                bundle.putString("PostCode",s.toString());
                                tablesFullViewDialog.setArguments(bundle);
                                tablesFullViewDialog.setCallback((dialog, houseNo) -> {
                                    houseNoET.setText(houseNo);
                                    dialog.dismiss();
                                });
                                tablesFullViewDialog.show(getSupportFragmentManager(), null);

                            }
                            postalInfo.removeChangeListener(this);
                            realmChangeListeners.remove(this);
                        }
                    };
                    realmChangeListeners.add(changeListener);
                    postalInfo.addChangeListener(changeListener);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        telephoneNoET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*if (s.length() <10) {
                    nameET.setText("");
                    postCodeET.setText("");
                    houseNoET.setText("");
                    cityET.setText("");
                    remarksET.setText("");
                    remarkStatusSpinner.setSelection(0);
                    telephoneNoET.setTypeface(telephoneNoET.getTypeface(), Typeface.ITALIC);*/
                telephoneNoET.setTypeface(telephoneNoET.getTypeface(), Typeface.BOLD);
                CustomerInfo customerInfo = realm.where(CustomerInfo.class).equalTo("phone", s.toString()).findFirst();
                try {
                    if (customerInfo != null && customerInfo.isValid()) {
//                                customerInfo.setMatched(true);
                        String address = "";
                        PostalInfo postalInfo = customerInfo.getPostalInfo();
                        if (postalInfo != null) {
                            if (postalInfo.getA_PostCode() != null)
                                postCodeET.setText(customerInfo.getPostalInfo().getA_PostCode());
                            address = (postalInfo.getAddress() == null) ? "" : postalInfo.getAddress().trim();
                        }
                        String name = (customerInfo.getName() == null) ? "" : customerInfo.getName().trim();
                        String houseNo = (customerInfo.getHouse_no() == null) ? "" : customerInfo.getHouse_no().trim();
                        String remarks = (customerInfo.getRemarks() == null) ? "" : customerInfo.getRemarks().trim();
                        nameET.setText(name);
                        houseNoET.setText(houseNo);

                        cityET.setText("");
                        String[] tokens = StringHelper.capitalizeEachWordAfterComma(address).split(",");
                        List<String> stringList = Arrays.asList(tokens);
                        Iterator<String> stringIterator = stringList.iterator();
                        while (stringIterator.hasNext()) {
                            final String str = stringIterator.next();
                            if (stringIterator.hasNext())
                                cityET.append(str.trim() + ",\n");
                            else
                                cityET.append(str.trim());
                        }

                        remarksET.setText(remarks);

                        if (customerInfo.getRemarkStatus() != null) {
                            int position = spinnerAdapter.indexOf(customerInfo.getRemarkStatus().trim());
                            if (position != -1)
                                remarkStatusSpinner.setSelection(position);
                            else
                                remarkStatusSpinner.setSelection(0);
                        }
//                        postCodeET.requestFocus();
                    } else if(!customerInfo.isValid()){
                        nameET.setText("");
                        postCodeET.setText("");
                        houseNoET.setText("");
                        cityET.setText("");
                        remarksET.setText("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        nameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    nameET.setTypeface(nameET.getTypeface(), Typeface.ITALIC);
                } else {
                    String str = s.toString();
                    if (!str.equals(str.toUpperCase())) {
                        str = str.toUpperCase();
                        nameET.setText(str);
                        nameET.setSelection(nameET.length()); //fix reverse texting
                    }
                    nameET.setTypeface(nameET.getTypeface(), Typeface.BOLD);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        houseNoET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    houseNoET.setTypeface(houseNoET.getTypeface(), Typeface.ITALIC);
                } else {
                    String str = s.toString();
                    if (!str.equals(str.toUpperCase())) {
                        str = str.toUpperCase();
                        houseNoET.setText(str);
                        houseNoET.setSelection(houseNoET.length()); //fix reverse texting
                    }
                    houseNoET.setTypeface(houseNoET.getTypeface(), Typeface.BOLD);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cityET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    cityET.setTypeface(cityET.getTypeface(), Typeface.ITALIC);
                } else {
                    String str = s.toString();
                    if (!str.equals(str.toUpperCase())) {
                        str = str.toUpperCase();
                        cityET.setText(str);
                        cityET.setSelection(cityET.length()); //fix reverse texting
                    }
                    cityET.setTypeface(cityET.getTypeface(), Typeface.BOLD);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        remarksET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    remarksET.setTypeface(remarksET.getTypeface(), Typeface.ITALIC);
                } else {
                    String str = s.toString();
                    if (!str.equals(str.toUpperCase())) {
                        str = str.toUpperCase();
                        remarksET.setText(str);
                        remarksET.setSelection(remarksET.length()); //fix reverse texting
                    }
                    remarksET.setTypeface(remarksET.getTypeface(), Typeface.BOLD);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        primaryKeyboard = findViewById(R.id.keyBoard);
        numPad = findViewById(R.id.numPad);

        telephoneNoET.setShowSoftInputOnFocus(false);
        telephoneNoET.setOnKeyListener(this);
        telephoneNoET.setOnFocusChangeListener(this);

        postCodeET.setShowSoftInputOnFocus(false);
        postCodeET.setOnKeyListener(this);
        postCodeET.setOnFocusChangeListener(this);

        nameET.setShowSoftInputOnFocus(false);
        nameET.setOnKeyListener(this);
        nameET.setOnFocusChangeListener(this);

        houseNoET.setShowSoftInputOnFocus(false);
        houseNoET.setOnKeyListener(this);
        houseNoET.setOnFocusChangeListener(this);

        cityET.setShowSoftInputOnFocus(false);
        cityET.setOnKeyListener(this);
        cityET.setOnFocusChangeListener(this);

        remarksET.setShowSoftInputOnFocus(false);
        remarksET.setOnKeyListener(this);
        remarksET.setOnFocusChangeListener(this);

        progressBar = findViewById(R.id.progress);
    }

    private void setSpinner() {
        RealmResults<RemarkType> remarkTypes = realm.where(RemarkType.class).findAll();
        spinnerAdapter = new SpinnerAdapter(NewCustomerInfoActivity.this, remarkTypes);

        remarkStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                remarksET.setBackgroundColor(remarkTypes.get(position).getColor());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        remarkStatusSpinner.setAdapter(spinnerAdapter);
        remarkStatusSpinner.setSelection(0);
    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive( Context context, Intent intent ) {
            String key = intent.getStringExtra("dataKey");
            getStatusOrderList(key,0);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(NewCustomerInfoActivity.this).registerReceiver(receiver,
                new IntentFilter("fireDB"));
//        startService(new Intent(this, FirebaseBackgroundService.class));
//        firebaseListener();
        Log.e(TAG, "--------------------onResume called-------------------------");
        realm = RealmManager.getLocalInstance();
        if (realm == null)
            Log.e(TAG, "---------------realm is null-----------------");
        else
            Log.e(TAG, "-----------------------realm is not null-------------------");
        clearFields();
        initRecyclerViews();
        setSpinner();

        new Thread(()->{
            Realm realm = RealmManager.getLocalInstance();
            try {
                if (realm.where(PostalInfo.class).count() == 0)
                    initPostalCodes(realm);
                if (realm.where(CustomerInfo.class).count() == 0)
//                    initUserInfo(realm);// commented by ravi
                /*realm.executeTransaction(r->{
                    r.where(CustomerInfo.class).findAll().deleteAllFromRealm();
                });*/
                if (realm.where(RemarkType.class).count() == 0)
                    initDefaultRemarksTypes(realm);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                RealmManager.closeLocalInstance();
            }
            /*new Handler(Looper.getMainLooper()).post(()->{
                setPostalCodes();
            });*/
        }).start();

        nameET.setText("");
        remarksET.setText("");
        telephoneNoET.setText("");
        houseNoET.setText("");
        postCodeET.setText("");
        telephoneNoET.requestFocus();
        cityET.setText("");
        infoBean = null;

        localBroadcastManager.registerReceiver(telephoneBroadcast, new IntentFilter(CallerIDService.INTENT_CALLER_ID));

        if(CallerIDService.callerIDService==null) {
            Toast.makeText(this, "callerid service not running", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!CallerIDService.callerIDService.isCallerIdCaptured()) {
            String phoneNo = CallerIDService.callerIDService.getLastTelephone();
            if(phoneNo!=null && !phoneNo.trim().isEmpty())
                telephoneNoET.setText(phoneNo);
            CallerIDService.callerIDService.setCallerIdCaptured(true);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(NewCustomerInfoActivity.this).unregisterReceiver(receiver);
        Log.e(TAG, "-----------------------------------onPause called-------------------------------");
        localBroadcastManager.unregisterReceiver(telephoneBroadcast);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_customerinfo2);
//        Realm.init(this);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
//        callerIDProcessor=new CallerIDProcessor(this);
        realm = RealmManager.getLocalInstance();
        initializeViews();

    }

//    private void initDummyData() {
//        if (realm.where(MenuCategory.class).count() == 0)
//            initProductCategories();
//        if (realm.where(MenuItem.class).count() == 0) {
//            initMenuItems();
//            initCommonItems();
//            realm.executeTransaction(r -> {
//                for (MenuCategory category : realm.where(MenuCategory.class).findAll()) {
//                    for (MenuItem item : realm.where(MenuItem.class).equalTo("type", Constants.ITEM_TYPE_COMMON).findAll()) {
////                    item.menuCategory=category;
//                        category.menuItems.add(item);
//                    }
//                }
//            });
//        }
//        if (realm.where(Addon.class).count() == 0) {
//            initAddons();
//            initNoAddons();
//        }
//
//        realm.executeTransaction(r -> {
//            MenuCategory menuCategory = realm.where(MenuCategory.class).findFirst();
//            if (menuCategory.addons.isEmpty()) {
//                for (Addon a : realm.where(Addon.class).findAll()) {
//                    a.menuCategory = menuCategory;
//                    menuCategory.addons.add(a);
//                }
//            }
//        });
//
//    }

    private void initPostalCodes(Realm realm) {
        Log.d(TAG, "-------------------------initPostalCodes--------------------------");
        realm.executeTransaction(r->{
            try {
                InputStream ins = getResources().openRawResource(
                        getResources().getIdentifier("generic_postcode",
                                "raw", getPackageName()));
                Gson gson = new Gson();
                JsonReader jsonReader = new JsonReader(new InputStreamReader(ins));

                final Type POSTALINFO_TYPE = new TypeToken<List<PostalData>>() {
                }.getType();
                List<PostalData> postalInfos = gson.fromJson(jsonReader, POSTALINFO_TYPE);
                Log.d(TAG, "----------------------gson postcodes: "+postalInfos);
                DataInitializers.initPostalInfo(r, postalInfos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

//    private void initUserInfo(Realm realm) {
//        Log.d(TAG,  "---------------initUserInfo--------------------");
//        realm.executeTransaction(r->{
//            InputStream ins = getResources().openRawResource(
//                    getResources().getIdentifier("generic_customerinfo",
//                            "raw", getPackageName()));
//
//            Gson gson = new Gson();
//            JsonReader jsonReader = new JsonReader(new InputStreamReader(ins));
//
//            final Type USERINFO_TYPE = new TypeToken<List<UserData>>() {
//            }.getType();
//            List<UserData> userInfos = gson.fromJson(jsonReader, USERINFO_TYPE);
//
//            Log.d(TAG,"--------------gson users: "+userInfos);
//            DataInitializers.initUserInfo(r, userInfos);
//        });
//    }

    private void initDefaultRemarksTypes(Realm realm) {
        realm.executeTransaction(r->{
            InputStream ins = getResources().openRawResource(
                    getResources().getIdentifier("default_remarks",
                            "raw", getPackageName()));

            Gson gson = new Gson();
            JsonReader jsonReader = new JsonReader(new InputStreamReader(ins));

            final Type USERINFO_TYPE = new TypeToken<List<RemarkTypeJson>>() {
            }.getType();
            List<RemarkTypeJson> remarkTypes = gson.fromJson(jsonReader, USERINFO_TYPE);
            List<Integer> integers = new ArrayList<>();
            for (int c : remarksColor) {
                integers.add(ContextCompat.getColor(this, c));
            }
            DataInitializers.initRemarkTypes(r, remarkTypes, integers);
        });

    }

//    private void initProductCategories() {
//        InputStream ins = getResources().openRawResource(
//                getResources().getIdentifier("product_category",
//                        "raw", getPackageName()));
//
//        Gson gson = new Gson();
//        JsonReader jsonReader = new JsonReader(new InputStreamReader(ins));
//
//        final Type PRODUCT_CATEGORY = new TypeToken<List<MenuCategory>>() {
//        }.getType();
//        List<MenuCategory> productCategories = gson.fromJson(jsonReader, PRODUCT_CATEGORY);
//        DataInitializers.initProductCategory(this, productCategories);
//    }

//    private void initMenuItems() {
//        InputStream ins = getResources().openRawResource(
//                getResources().getIdentifier("items",
//                        "raw", getPackageName()));
//
//        Gson gson = new Gson();
//        JsonReader jsonReader = new JsonReader(new InputStreamReader(ins));
//
//        final Type MENU_ITEMS = new TypeToken<List<Item>>() {
//        }.getType();
//        List<Item> items = gson.fromJson(jsonReader, MENU_ITEMS);
//        DataInitializers.initMenuItems(this, items);
//    }

//    private void initAddons() {
//        InputStream ins = getResources().openRawResource(
//                getResources().getIdentifier("addons",
//                        "raw", getPackageName()));
//
//        Gson gson = new Gson();
//        JsonReader jsonReader = new JsonReader(new InputStreamReader(ins));
//
//        final Type FLAVOUR = new TypeToken<List<Addon>>() {
//        }.getType();
//        List<Addon> addons = gson.fromJson(jsonReader, FLAVOUR);
//        DataInitializers.initAddons(this, addons);
//    }

//    private void initNoAddons() {
//        InputStream ins = getResources().openRawResource(
//                getResources().getIdentifier("no_add_items",
//                        "raw", getPackageName()));
//
//        Gson gson = new Gson();
//        JsonReader jsonReader = new JsonReader(new InputStreamReader(ins));
//
//        final Type FLAVOUR = new TypeToken<List<Addon>>() {
//        }.getType();
//        List<Addon> addons = gson.fromJson(jsonReader, FLAVOUR);
//        DataInitializers.initNoAddons(this, addons);
//    }

//    private void initCommonItems() {
//        InputStream ins = getResources().openRawResource(
//                getResources().getIdentifier("common_items",
//                        "raw", getPackageName()));
//
//        Gson gson = new Gson();
//        JsonReader jsonReader = new JsonReader(new InputStreamReader(ins));
//
//        final Type MENU_ITEMS = new TypeToken<List<Item>>() {
//        }.getType();
//        List<Item> items = gson.fromJson(jsonReader, MENU_ITEMS);
//        DataInitializers.initCommonItems(this, items);
//    }


    private void setPostalCodes() {
        Log.d(TAG, "--------------------------setPostalCodes------------------------------");
        RealmResults<PostalInfo> postalInfos = realm.where(PostalInfo.class).findAllAsync();
        progressBar.setVisibility(View.VISIBLE);

        RealmChangeListener<RealmResults<PostalInfo>> changeListener = new RealmChangeListener<RealmResults<PostalInfo>>() {
            @Override
            public void onChange(RealmResults<PostalInfo> pi) {
                if (!pi.isValid() && pi.isEmpty()) {
                    Log.e(TAG, "-----------------No Postal Code Found-----------------------");
                } else {
                    adapter.setPostalInfos(pi);
                    adapter.notifyDataSetChanged();
                }
                postalInfos.removeChangeListener(this);
                realmChangeListeners.remove(this);
                progressBar.setVisibility(View.GONE);
            }
        };

        realmChangeListeners.add(changeListener);
        postalInfos.addChangeListener(changeListener);
    }


//    private void createFile(String folderName, String fileName) {
//        String parentfolderName = "foodciti";
//        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + parentfolderName + File.separator + folderName);
//        if (!folder.exists())
//            folder.mkdirs();
//
//        File file = new File(Environment.getExternalStorageDirectory() + File.separator + parentfolderName + File.separator + folderName + File.separator + fileName);
//        if (!file.exists()) {
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Log.e(TAG, "----------------------keyEvent---------------------");
        switch (keyCode) {
            case ExtendedKeyBoard.KEYCODE_DONE:
                Log.e(TAG, "-------------------keycode: " + keyCode);
                saveUserData("ENTRY");
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

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        EditText editText = null;
        InputConnection ic = null;
        if (v instanceof EditText) {
            editText = (EditText) v;
            ic = editText.onCreateInputConnection(new EditorInfo());
        }

        if (hasFocus && ic != null) {
            primaryKeyboard.setInputConnection(ic);
            numPad.setInputConnection(ic);
        }
    }


    private void getStatusOrderList(final String orderId, int orderStatus) {
        if (InternetConnection.checkConnection(NewCustomerInfoActivity.this)) {
            //  if (!TextUtils.isEmpty(SessionManager.get(RestaurentMainActivity.this).getFoodTruckId())) {
            Observable<GenericResponse<List<Order>>> results = RetroClient.getApiService()
                    .getStatusOrderList(SessionManager.get(NewCustomerInfoActivity.this).getFoodTruckId(), orderStatus);
            new CompositeDisposable().add(results.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GenericResponse<List<Order>>>() {
                        @Override
                        public void accept(GenericResponse<List<Order>> response) throws Exception {
                            Log.e("ORDER_RESPONSE", "" + response.getStatus());
                            if (response.getStatus().equals(Consts.IS_SUCCESS)) {
                                List<Order> orderList = response.getData();
                                Log.e("ODR_LIST", "" + orderList);
                                if (orderList != null) {
                                    Log.e("LIST_ORDER", "" + orderList.size());
                                    for (Order order : orderList) {
                                        Log.e("OD_EQUAL", "" + order.getOrderId() + ", " + orderId);
                                        if (order.getOrderId().equals(orderId)) {
                                            showOrderInfo(order);
                                        }
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.e("ERROR", "" + throwable.toString());
                        }
                    }));

        } else {
            Toast.makeText(NewCustomerInfoActivity.this, R.string.string_internet_connection_warning, Toast.LENGTH_LONG).show();
        }
    }

    private void showOrderInfo(Order order) {
        Log.e("ORDER_DIALOG", "" + order.getFoodTruckId());
        // playSoundFile();
        OrderInfo orderInfo = OrderInfo.newInstance(order);
        orderInfo.setItemListener(this);
        orderInfo.setCancelable(false);
        orderInfo.show(getSupportFragmentManager(), null);
    }

    @Override
    public void changeOrderStatus(String orderId, int orderStatus) {

    }

    @Override
    public void cancelOrder(String orderId) {

    }

    @Override
    public void itemClick(Order order) {
//        timerChecker = true;
        OrderInfo orderInfo = OrderInfo.newInstance(order);
        orderInfo.setItemListener(this);
        orderInfo.show(getSupportFragmentManager(), null);
    }


    @Override
    public void itemClick(OrderedItem orderedItem) {
        if (orderedItem.getSubItemList().size() > 0) {
            ItemSubDetails itemSubDetails = ItemSubDetails.newInstance((ArrayList<SubItem>) orderedItem.getSubItemList());
            itemSubDetails.show(getSupportFragmentManager(), null);
        }
    }

    @Override
    public void onOrderAccepted(String orderId) {

    }

    @Override
    public void onOrderForward(String orderId) {

    }

    @Override
    public void onOrderCancel(String orderId) {

    }

}
