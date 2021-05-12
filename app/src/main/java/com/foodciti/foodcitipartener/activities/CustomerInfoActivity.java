/*
package com.foodciti.foodcitipartener.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.text.Editable;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.AddressAdapter;
import com.foodciti.foodcitipartener.adapters.SpinnerAdapter;
import com.foodciti.foodcitipartener.gson.Item;
import com.foodciti.foodcitipartener.gson.PostalData;
import com.foodciti.foodcitipartener.gson.UserData;
import com.foodciti.foodcitipartener.models.RemarksStatusBean;
import com.foodciti.foodcitipartener.realm_entities.Addon;
import com.foodciti.foodcitipartener.realm_entities.CustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.MenuCategory;
import com.foodciti.foodcitipartener.realm_entities.MenuItem;
import com.foodciti.foodcitipartener.realm_entities.PostalInfo;
import com.foodciti.foodcitipartener.realm_entities.RemarkType;
import com.foodciti.foodcitipartener.services.CallerIDService;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.DataInitializers;
import com.foodciti.foodcitipartener.utils.StringHelper;
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
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.fabric.sdk.android.Fabric;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class CustomerInfoActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, */
/*CustomerListAdapter.PostalItemListener,*//*
 AddressAdapter.AddressAdapterCAllback {

    private static final String TAG = "CustomerInfoActivity";

    private Realm realm;

    private AddressAdapter adapter;
    private List<PostalInfo> postalInfos = new ArrayList<>();

    private EditText postCodeET, telephoneNoET, houseNoET, cityET, remarksET, nameET;
    private Spinner remarkStatusSpinner;

    // string variable to set which edittext is being focused
    String where_click = "postcode";

    private CustomerInfo uniqueUserFromPhoneSearch;
    private RealmResults<PostalInfo> dynamicPostalInfoFromSearch;
    private PostalInfo uniquePostalInfoFromSearch;


    String[] remarksType = {"Normal", "Payment Issue", "Wrong OrderDetails", "Got Gift", "Other"};
    int[] remarksColor = {R.color.menuList, R.color.addItemColor, R.color.pink, R.color.colorAccent, R.color.colorVividTangerine};
    int MENU_SCREEN_REQUEST = 101;

    private CustomerInfo infoBean = null;
    private CallerIDService mBoundService;
    private boolean mIsBound, isResumed;
    private LocalBroadcastManager localBroadcastManager;
//    private CallerIDProcessor callerIDProcessor;

    private BroadcastReceiver telephoneBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            */
/*byte[] buffer=intent.getByteArrayExtra(CallerIDService.RECEIVED_BUFFER);
            int size=intent.getIntExtra(CallerIDService.RECEIVED_BUFFER_SIZE,0);
            String in_tel=callerIDProcessor.processBuffer(buffer, size);
            if(in_tel!=null)
                telephoneNoET.setText(in_tel);
*//*

            String telephone = intent.getStringExtra(CallerIDService.RECEIVED_PHONE_NO);
            telephoneNoET.setText(telephone);
        }
    };

    private BroadcastReceiver resetBuffers = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            callerIDProcessor.resetBuffer();
            Toast.makeText(context, "Buffer reset", Toast.LENGTH_SHORT).show();
        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has
            // been established, giving us the service object we can use
            // to interact with the service.  Because we have bound to a
            // explicit service that we know is running in our own
            // process, we can cast its IBinder to a concrete class and
            // directly access it.
            mBoundService = ((CallerIDService.LocalBinder) service).getService();

            if (isResumed) {
                if (mBoundService.getLastTelephone() != null && !mBoundService.getLastTelephone().trim().isEmpty())
                    telephoneNoET.setText(mBoundService.getLastTelephone().trim());
                isResumed = false;
                mBoundService.setLastTelephone("");
            }

            */
/*Toast.makeText(CustomerInfoActivity.this.getApplicationContext(),
                   "service connected",
                    Toast.LENGTH_SHORT).show();*//*

        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has
            // been unexpectedly disconnected -- that is, its process
            // crashed. Because it is running in our same process, we
            // should never see this happen.
            mBoundService = null;
            Toast.makeText(CustomerInfoActivity.this.getApplicationContext(),
                    "service disconnected",
                    Toast.LENGTH_SHORT).show();
            Log.e(TAG, "--------------------service disconnected");
        }

        @Override
        public void onBindingDied(ComponentName name) {
            Toast.makeText(CustomerInfoActivity.this.getApplicationContext(), "Binding died", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "--------------------BInding Died");
        }
    };

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation
        // that we know will be running in our own process (and thus
        // won't be supporting component replacement by other
        // applications).
        bindService(new Intent(CustomerInfoActivity.this, CallerIDService.class),
                mConnection,
                Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }


    // method to remove charater from edittext
    private void removeCharacter(EditText editText) {
        if (editText.getText().toString().length() > 0) {
            String value = editText.getText().toString();

            StringBuilder sb = new StringBuilder(value);
            int i = editText.getSelectionStart();
            if (i > 0) {
                sb = sb.deleteCharAt(editText.getSelectionStart() - 1);
                editText.setText(sb);

                editText.setSelection(i - 1);
            }
        }
    }

    private void setValue(String s) {
        s = s.toUpperCase();
        if (where_click.equalsIgnoreCase("name")) {
            if (s.equalsIgnoreCase("clear")) {
                removeCharacter(nameET);
            } else if (s.equalsIgnoreCase("TAB")) {
                nameET.performClick();
            } else if (s.equalsIgnoreCase("CL")) {
                nameET.setText("");
            } else {
                nameET.getText().insert(nameET.getSelectionStart(), s);
            }
        } else if (where_click.equalsIgnoreCase("postcode")) {
            if (s.equalsIgnoreCase("clear")) {
                removeCharacter(postCodeET);
            } else if (s.equalsIgnoreCase("TAB")) {
                telephoneNoET.performClick();
            } else if (s.equalsIgnoreCase("CL")) {
                postCodeET.setText("");
            } else {
                postCodeET.getText().insert(postCodeET.getSelectionStart(), s);
            }
        } else if (where_click.equalsIgnoreCase("telephone")) {
            if (s.equalsIgnoreCase("clear")) {
                removeCharacter(telephoneNoET);
            } else if (s.equalsIgnoreCase("TAB")) {
                houseNoET.performClick();
            } else if (s.equalsIgnoreCase("CL")) {
                telephoneNoET.setText("");
            } else {
                telephoneNoET.getText().insert(telephoneNoET.getSelectionStart(), s);
            }
        } else if (where_click.equalsIgnoreCase("house number")) {
            if (s.equalsIgnoreCase("clear")) {
                removeCharacter(houseNoET);
            } else if (s.equalsIgnoreCase("TAB")) {
                cityET.performClick();
            } else if (s.equalsIgnoreCase("CL")) {
                houseNoET.setText("");
            } else {
                houseNoET.getText().insert(houseNoET.getSelectionStart(), s);
            }
        } else if (where_click.equalsIgnoreCase("city")) {
            if (s.equalsIgnoreCase("clear")) {
                removeCharacter(cityET);
            } else if (s.equalsIgnoreCase("TAB")) {
                remarksET.performClick();
            } else if (s.equalsIgnoreCase("CL")) {
                cityET.setText("");
            } else {
                cityET.getText().insert(cityET.getSelectionStart(), s);
            }
        } else if (where_click.equalsIgnoreCase("remarks")) {
            if (s.equalsIgnoreCase("clear")) {
                removeCharacter(remarksET);
            } else if (s.equalsIgnoreCase("TAB")) {
                remarksET.performClick();
            } else if (s.equalsIgnoreCase("CL")) {
                remarksET.setText("");
            } else {
                remarksET.getText().insert(remarksET.getSelectionStart(), s);
            }
        }
    }

    private <E extends RealmObject> Object[] createBeanIfNotExists(Class<E> aClass, String primarykeyField, String primaryKey) {
        AtomicReference<E> beanRef = new AtomicReference<>();
        E bean = realm.where(aClass).equalTo(primarykeyField, primaryKey).findFirst();
        beanRef.set(bean);
        final Object[] results = new Object[2];
        results[1] = new Boolean(false);
        if (bean == null) {
            Log.e(TAG, "-----------------creating new bean of type: " + aClass.getSimpleName());
            if (!realm.isInTransaction()) {
                realm.executeTransaction(r -> {
                    Number maxId = r.where(aClass).max("id");
                    long nextId = (maxId == null) ? 0 : maxId.longValue() + 1;
                    beanRef.set(r.createObject(aClass, nextId));
                    results[1] = new Boolean(true);
                });
            } else {
                Number maxId = realm.where(aClass).max("id");
                long nextId = (maxId == null) ? 0 : maxId.longValue() + 1;
                beanRef.set(realm.createObject(aClass, nextId));
                results[1] = new Boolean(true);
            }
        } else
            Log.e(TAG, "-----------------bean exists of type: " + aClass.getSimpleName());
        results[0] = beanRef.get();
        return results;
    }

    */
/*private CustomerInfo insertUserInfoForNonDeliveryOrders() {
        AtomicReference<CustomerInfo> beanRef = new AtomicReference<>();
        realm.executeTransaction(r -> {
            CustomerInfo bean = null;
            final String phone = telephoneNoET.getText().toString().trim();
            final String name = nameET.getText().toString().trim();
            final String postCode = postCodeET.getText().toString().trim();
            final String houseNo = noteET.getText().toString().trim();
            final String city = cityET.getText().toString().trim();
            if (!phone.isEmpty()) {
                Object[] results = createBeanIfNotExists(CustomerInfo.class, "phone", phone);
                bean = (CustomerInfo) results[0];
                Boolean justCreated = (Boolean) results[1];
                if (justCreated.booleanValue())
                    bean.setPhone(phone);
            } else {
                Number maxId = r.where(CustomerInfo.class).max("id");
                long nextId = (maxId == null) ? 0 : maxId.longValue() + 1;
                bean = r.createObject(CustomerInfo.class, nextId);
            }
            beanRef.set(bean);
            if (!name.isEmpty())
                bean.setName(name);
            if (!postCode.isEmpty()) {
                Object[] results = createBeanIfNotExists(PostalInfo.class, "A_PostCode", postCode);
                PostalInfo postalInfo = (PostalInfo) results[0];
                Boolean justCreated = (Boolean) results[1];
                if (justCreated)
                    postalInfo.setA_PostCode(postCode);
                if (!city.isEmpty())
                    postalInfo.setAddress(city);
                bean.setPostalInfo(postalInfo);
            }
            if (!houseNo.isEmpty())
                bean.setHouse_no(houseNo);
        });

        return beanRef.get();
    }*//*


    private CustomerInfo insertUserInfoForNonDeliveryOrders() {
        AtomicReference<CustomerInfo> customerInfoAtomicReference=new AtomicReference<>(null);
        realm.executeTransaction(r->{
            CustomerInfo bean = null;
            final String phone = telephoneNoET.getText().toString().trim();
            final String name = nameET.getText().toString().trim();
            final String postCode = postCodeET.getText().toString().trim();
            final String houseNo = houseNoET.getText().toString().trim();
            final String city = cityET.getText().toString().trim();

            if(phone.isEmpty() && name.isEmpty() && postCode.isEmpty() && houseNo.isEmpty() && city.isEmpty()) {
                bean=r.where(CustomerInfo.class)
                        .isNull("postalInfo").and()
                        .isEmpty("name").and()
                        .isEmpty("phone").and()
                        .isEmpty("house_no").findFirst();
                if(bean==null) {
                    Number maxId=r.where(CustomerInfo.class).max("id");
                    long nextId=(maxId==null)?1:maxId.longValue()+1;
                    bean=r.createObject(CustomerInfo.class, nextId);
                }
                customerInfoAtomicReference.set(bean);
            } else {
                if (!phone.isEmpty()) {
                    Object[] results = createBeanIfNotExists(CustomerInfo.class, "phone", phone);
                    bean = (CustomerInfo) results[0];
                    Boolean justCreated = (Boolean) results[1];
                    if (justCreated.booleanValue())
                        bean.setPhone(phone);
                } else {
                    Number maxId = r.where(CustomerInfo.class).max("id");
                    long nextId = (maxId == null) ? 0 : maxId.longValue() + 1;
                    bean = r.createObject(CustomerInfo.class, nextId);
                }
                customerInfoAtomicReference.set(bean);
                if (!name.isEmpty())
                    bean.setName(name);
                if (!postCode.isEmpty()) {
                    Object[] results = createBeanIfNotExists(PostalInfo.class, "A_PostCode", postCode);
                    PostalInfo postalInfo = (PostalInfo) results[0];
                    Boolean justCreated = (Boolean) results[1];
                    if (justCreated)
                        postalInfo.setA_PostCode(postCode);
                    if (!city.isEmpty())
                        postalInfo.setAddress(city);
                    bean.setPostalInfo(postalInfo);
                }
                if (!houseNo.isEmpty())
                    bean.setHouse_no(houseNo);
            }
        });
        return customerInfoAtomicReference.get();
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
                RemarksStatusBean remarksStatusBean = (RemarksStatusBean) remarkStatusSpinner.getSelectedItem();
                bean.get().setRemarkStatus(remarksStatusBean.getRemarksType());
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
            RemarksStatusBean remarksStatusBean = (RemarksStatusBean) remarkStatusSpinner.getSelectedItem();
            bean.get().setRemarkStatus(remarksStatusBean.getRemarksType());
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

                        infoBean = insertUserInfo();
                        if (from.equalsIgnoreCase("DELIVERY")) {
//                            Intent intent = new Intent(this, MenuActivity.class);
                            Intent intent = new Intent(this, BasicMenuActivity.class);
                            intent.putExtra("USER_DATA_ID", infoBean.getId());
                            intent.putExtra("ORDER_TYPE", Constants.TYPE_DELIVERY);
//                            startActivityForResult(intent, MENU_SCREEN_REQUEST);
                            startActivity(intent);
//                            finish();
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

        clearFields();
        Toast.makeText(this, "Data Saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvDelivery:
            case R.id.tvDeliveryIcon:
            case R.id.delivery_btn:
                saveUserData("DELIVERY");
                break;

            case R.id.collection_btn:
            case R.id.tvCollection:
            case R.id.collection_icon: {
                */
/*if (!TextUtils.isEmpty(postCodeET.getText().toString())
                        && !TextUtils.isEmpty(telephoneNoET.getText().toString())
                        && !TextUtils.isEmpty(noteET.getText().toString())
                        && !TextUtils.isEmpty(cityET.getText().toString())) {
                    infoBean = insertUserInfo();
                } else if (!TextUtils.isEmpty(nameET.getText().toString())) {
                    infoBean = insertUserInfoForNonDeliveryOrders();
                }*//*

                infoBean = insertUserInfoForNonDeliveryOrders();
//                Intent intent = new Intent(this, MenuActivity.class);
//                Intent intent = new Intent(this, MenuActivity.class);
                Intent intent = new Intent(this, BasicMenuActivity.class);
                if (infoBean != null) {
                    intent.putExtra("USER_DATA_ID", infoBean.getId());
                }
                intent.putExtra("ORDER_TYPE", Constants.TYPE_COLLECTION);
                startActivityForResult(intent, MENU_SCREEN_REQUEST);
            }
            break;

            case R.id.tvTable:
            case R.id.table_icon:
            case R.id.table_btn: {
                */
/*if (!TextUtils.isEmpty(postCodeET.getText().toString())
                        && !TextUtils.isEmpty(telephoneNoET.getText().toString())
                        && !TextUtils.isEmpty(noteET.getText().toString())
                        && !TextUtils.isEmpty(cityET.getText().toString())) {
                    infoBean = insertUserInfo();
                } else if (!TextUtils.isEmpty(nameET.getText().toString())) {
                    infoBean = insertUserInfoForNonDeliveryOrders();
                }*//*

 */
/*infoBean = insertUserInfoForNonDeliveryOrders();
//                Intent intent = new Intent(this, TableMenuActivity.class);
                Intent intent = new Intent(this, BasicTableMenuActivity.class);
                if (infoBean != null) {
                    intent.putExtra("USER_DATA_ID", infoBean.getId());
                }
                intent.putExtra("ORDER_TYPE", Constants.TYPE_TABLE);
                startActivity(intent);*//*

            }
            break;

            case R.id.name:
                nameET.requestFocus();
                where_click = "name";
                break;
            case R.id.postcode:
                postCodeET.requestFocus();
                where_click = "postcode";
                break;

            case R.id.telephone:
                telephoneNoET.requestFocus();
                where_click = "telephone";
                break;

            case R.id.noteET:
                houseNoET.requestFocus();
                where_click = "house number";
                break;

            case R.id.customerAddress:
                where_click = "city";
                cityET.requestFocus();
                break;

            case R.id.remark:
                */
/*remarksET.requestFocus();
                where_click = "remarks";*//*

                break;

            case R.id.one:
            case R.id.one1:
                setValue("1");
                break;

            case R.id.two:
            case R.id.two2:
                setValue("2");
                break;

            case R.id.three:
            case R.id.three3:
                setValue("3");
                break;

            case R.id.four:
            case R.id.four4:
                setValue("4");
                break;

            case R.id.five:
            case R.id.five5:
                setValue("5");
                break;

            case R.id.six:
            case R.id.six6:
                setValue("6");
                break;

            case R.id.seven:
            case R.id.seven7:
                setValue("7");
                break;

            case R.id.eight:
            case R.id.eight8:
                setValue("8");
                break;

            case R.id.nine:
            case R.id.nine9:
                setValue("9");
                break;

            case R.id.zero:
            case R.id.zero0:
                setValue("0");
                break;

            case R.id.back_space:
            case R.id.cancel:
                setValue("clear");
                break;

            case R.id.a:
                setValue("a");
                break;
            case R.id.b:
                setValue("b");
                break;
            case R.id.c:
                setValue("c");
                break;
            case R.id.d:
                setValue("d");
                break;
            case R.id.e:
                setValue("e");
                break;
            case R.id.f:
                setValue("f");
                break;
            case R.id.g:
                setValue("g");
                break;
            case R.id.h:
                setValue("h");
                break;
            case R.id.i:
                setValue("i");
                break;
            case R.id.j:
                setValue("j");
                break;
            case R.id.k:
                setValue("k");
                break;
            case R.id.l:
                setValue("l");
                break;
            case R.id.m:
                setValue("m");
                break;
            case R.id.n:
                setValue("n");
                break;
            case R.id.o:
                setValue("o");
                break;
            case R.id.p:
                setValue("p");
                break;
            case R.id.q:
                setValue("q");
                break;
            case R.id.r:
                setValue("r");
                break;
            case R.id.s:
                setValue("s");
                break;
            case R.id.t:
                setValue("t");
                break;
            case R.id.u:
                setValue("u");
                break;
            case R.id.v:
                setValue("v");
                break;
            case R.id.w:
                setValue("w");
                break;
            case R.id.x:
                setValue("x");
                break;
            case R.id.y:
                setValue("y");
                break;
            case R.id.z:
                setValue("z");
                break;
            case R.id.dot:
                setValue(".");
                break;
            case R.id.question_mark:
                setValue("?");
                break;
            case R.id.dash:
                setValue("-");
                break;
            case R.id.sp:
                setValue(" ");
                break;
            case R.id.pound_sign:
                setValue("£");
                break;
            case R.id.plus_sign:
                setValue("+");
                break;
            case R.id.tab:
            case R.id.tab1:
                setValue("TAB");
                break;
            case R.id.del:
                setValue("CL");
                break;

            case R.id.enter1:
                saveUserData("ENTRY");
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.e(TAG, "------------------touch event triggered------------------------");
            switch (v.getId()) {
                case R.id.name:
                    setCursor(nameET, event);
                    where_click = "name";
                    return true;

                case R.id.postcode:
                    setCursor(postCodeET, event);
                    where_click = "postcode";
                    return true;

                case R.id.telephone:
                    setCursor(telephoneNoET, event);
                    where_click = "telephone";
                    return true;

                case R.id.noteET:
                    setCursor(houseNoET, event);
                    where_click = "house number";
                    return true;

                case R.id.customerAddress:
                    where_click = "city";
                    setCursor(cityET, event);
                    return true;

                case R.id.remark:
                    setCursor(remarksET, event);
                    where_click = "remarks";
                    final Dialog dialog = new Dialog(this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.remarks_layout);
                    dialog.setCancelable(false);
                    View close = dialog.findViewById(R.id.close);
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    TextView title = dialog.findViewById(R.id.title);
                    title.setText("Remarks");
                    TextView remarks = dialog.findViewById(R.id.remarks);
                    remarks.setText(remarksET.getText().toString().trim());
                    if (!remarksET.getText().toString().trim().isEmpty())
                        dialog.show();
                    return true;
            }
        }
        return true;
    }

    public void hideKeyBoard(View v) {
        final InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void setCursor(EditText editText, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            hideKeyBoard(editText);
            editText.requestFocus();
            Layout layout = editText.getLayout();
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (layout != null) {
                int line = layout.getLineForVertical(y);
                int offset = layout.getOffsetForHorizontal(line, x);
                editText.setSelection(offset);
            }
        }
    }

    @Override
    public void onClickAddress(int position) {
        PostalInfo postalInfo = postalInfos.get(position);
        postCodeET.setText(postalInfo.getA_PostCode());
        cityET.setText(StringHelper.capitalizeEachWordAfterComma(postalInfo.getAddress().trim()));
        houseNoET.requestFocus();
    }

    private void initializeViews() {
        nameET = findViewById(R.id.name);
        postCodeET = findViewById(R.id.postcode);
        telephoneNoET = findViewById(R.id.telephone);
        houseNoET = findViewById(R.id.noteET);
        cityET = findViewById(R.id.customerAddress);
        remarksET = findViewById(R.id.remark);
        remarksET.setOnClickListener(this);
        RecyclerView customerDataRV = findViewById(R.id.suggestion_field);
        remarkStatusSpinner = findViewById(R.id.remark_status_spinner);

        // initializing adapter
//        adapter = new CustomerListAdapter(this, postalInfos);
        adapter = new AddressAdapter(this, postalInfos);
        customerDataRV.setAdapter(adapter);

        // click listener
        postCodeET.setOnClickListener(this);
        telephoneNoET.setOnClickListener(this);

        // touch listeners
        nameET.setOnTouchListener(this);
        postCodeET.setOnTouchListener(this);
        telephoneNoET.setOnTouchListener(this);
        houseNoET.setOnTouchListener(this);
        cityET.setOnTouchListener(this);
        remarksET.setOnTouchListener(this);

        // text change listener
        postCodeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (where_click.equalsIgnoreCase("postcode")) {
                    if (s.length() == 0)
                        cityET.setText("");
                    try {
                        dynamicPostalInfoFromSearch = realm.where(PostalInfo.class).contains("A_PostCode", s.toString().trim(), Case.INSENSITIVE).findAllAsync();
                        dynamicPostalInfoFromSearch.addChangeListener(new RealmChangeListener<RealmResults<PostalInfo>>() {
                            @Override
                            public void onChange(RealmResults<PostalInfo> postalInfoList) {
                                if (postalInfoList != null && postalInfoList.isValid()) {
                                    postalInfos.clear();
                                    postalInfos.addAll(postalInfoList);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });

                        uniquePostalInfoFromSearch = realm.where(PostalInfo.class).equalTo("A_PostCode", s.toString().trim()).findFirstAsync();
                        uniquePostalInfoFromSearch.addChangeListener(new RealmChangeListener<PostalInfo>() {
                            @Override
                            public void onChange(PostalInfo postalInfo) {
//                                Log.e(TAG, "----postalInfo: "+postalInfo);
                                if (postalInfo != null && postalInfo.isValid())
                                    cityET.setText(StringHelper.capitalizeEachWordAfterComma(postalInfo.getAddress().trim()));
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        telephoneNoET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    nameET.setText("");
                    postCodeET.setText("");
                    houseNoET.setText("");
                    cityET.setText("");
                    remarksET.setText("");
                    remarkStatusSpinner.setSelection(0);
                } else {
//                    realm.beginTransaction();
                    uniqueUserFromPhoneSearch = realm.where(CustomerInfo.class).equalTo("phone", s.toString()).findFirstAsync();
                    uniqueUserFromPhoneSearch.addChangeListener(new RealmChangeListener<CustomerInfo>() {
                        @Override
                        public void onChange(CustomerInfo customerInfo) {
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
                                    cityET.setText(StringHelper.capitalizeEachWordAfterComma(address));
                                    remarksET.setText(remarks);

                                    if (customerInfo.getRemarkStatus() != null) {
                                        int position = Arrays.asList(remarksType).indexOf(customerInfo.getRemarkStatus());
                                        remarkStatusSpinner.setSelection(position);
                                    }
                                    postCodeET.requestFocus();
                                } else {
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
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setSpinner() {
        */
/*List<RemarksStatusBean> remarksStatusList = new ArrayList<>();

        for (int i = 0; i < remarksType.length; i++) {
            remarksStatusList.add(new RemarksStatusBean(remarksType[i], remarksColor[i]));
        }*//*

        RealmResults<RemarkType> remarkTypes=realm.where(RemarkType.class).findAll();
        SpinnerAdapter adapter = new SpinnerAdapter(CustomerInfoActivity.this, remarkTypes);

        remarkStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GradientDrawable bgShape = (GradientDrawable) remarksET.getBackground();
                bgShape.setColor(ContextCompat.getColor(CustomerInfoActivity.this, remarksColor[position]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        remarkStatusSpinner.setAdapter(adapter);
        remarkStatusSpinner.setSelection(0);
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "--------------------------onDestroy called----------------");
        System.out.println("onDestroy.");
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        nameET.setText("");
        remarksET.setText("");
        telephoneNoET.setText("");
        houseNoET.setText("");
        postCodeET.setText("");
        postCodeET.performClick();
        cityET.setText("");
        infoBean = null;
        // TODO defined in service
//        startread();

        localBroadcastManager.registerReceiver(telephoneBroadcast, new IntentFilter(CallerIDService.INTENT_CALLER_ID));
//        localBroadcastManager.registerReceiver(resetBuffers, new IntentFilter(CallerIDService.INTENT_RESET_BUFFER));
        doBindService();
        isResumed = true;
        */
/*if(realm.isClosed())
        realm=Realm.getDefaultInstance();*//*

        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO defined in service
//        stopread();
        localBroadcastManager.unregisterReceiver(telephoneBroadcast);
//        localBroadcastManager.unregisterReceiver(resetBuffers);
        doUnbindService();
//        realm.close();
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_customer_info);
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        initializeViews();
        setSpinner();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
//        callerIDProcessor=new CallerIDProcessor(this);
        if (realm.where(PostalInfo.class).count() == 0)
            initPostalCodes();
        if (realm.where(CustomerInfo.class).count() == 0)
            initUserInfo();

        setPostalCodes();

        // TODO : uncomment for phone port listening
//        setPhonePortListnerVariables();
    }

    private void initDummyData() {
        if (realm.where(MenuCategory.class).count() == 0)
            initProductCategories();
        if (realm.where(MenuItem.class).count() == 0) {
            initMenuItems();
            initCommonItems();
            realm.beginTransaction();
            for (MenuCategory category : realm.where(MenuCategory.class).findAll()) {
                for (MenuItem item : realm.where(MenuItem.class).equalTo("type", Constants.ITEM_TYPE_COMMON).findAll()) {
//                    item.menuCategory=category;
                    category.menuItems.add(item);
                }
            }
            realm.commitTransaction();
        }
        if (realm.where(Addon.class).count() == 0) {
            initAddons();
            initNoAddons();
        }

        realm.beginTransaction();

        MenuCategory menuCategory = realm.where(MenuCategory.class).findFirst();
        if (menuCategory.addons.isEmpty()) {
            for (Addon a : realm.where(Addon.class).findAll()) {
                a.menuCategory = menuCategory;
                menuCategory.addons.add(a);
            }
        }
        realm.commitTransaction();
    }

    private void initPostalCodes() {
        InputStream ins = getResources().openRawResource(
                getResources().getIdentifier("generic_postcode",
                        "raw", getPackageName()));
        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(new InputStreamReader(ins));

        final Type POSTALINFO_TYPE = new TypeToken<List<PostalData>>() {
        }.getType();
        List<PostalData> postalInfos = gson.fromJson(jsonReader, POSTALINFO_TYPE);
        DataInitializers.initPostalInfo(postalInfos);
    }

    private void initUserInfo() {
        InputStream ins = getResources().openRawResource(
                getResources().getIdentifier("rectified_customers",
                        "raw", getPackageName()));

        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(new InputStreamReader(ins));

        final Type USERINFO_TYPE = new TypeToken<List<UserData>>() {
        }.getType();
        List<UserData> userInfos = gson.fromJson(jsonReader, USERINFO_TYPE);
        DataInitializers.initUserInfo(userInfos);
    }

    private void initProductCategories() {
        InputStream ins = getResources().openRawResource(
                getResources().getIdentifier("product_category",
                        "raw", getPackageName()));

        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(new InputStreamReader(ins));

        final Type PRODUCT_CATEGORY = new TypeToken<List<MenuCategory>>() {
        }.getType();
        List<MenuCategory> productCategories = gson.fromJson(jsonReader, PRODUCT_CATEGORY);
        DataInitializers.initProductCategory(productCategories);
    }

    private void initMenuItems() {
        InputStream ins = getResources().openRawResource(
                getResources().getIdentifier("items",
                        "raw", getPackageName()));

        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(new InputStreamReader(ins));

        final Type MENU_ITEMS = new TypeToken<List<Item>>() {
        }.getType();
        List<Item> items = gson.fromJson(jsonReader, MENU_ITEMS);
        DataInitializers.initMenuItems(items);
    }

    private void initAddons() {
        InputStream ins = getResources().openRawResource(
                getResources().getIdentifier("addons",
                        "raw", getPackageName()));

        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(new InputStreamReader(ins));

        final Type FLAVOUR = new TypeToken<List<Addon>>() {
        }.getType();
        List<Addon> addons = gson.fromJson(jsonReader, FLAVOUR);
        DataInitializers.initAddons(addons);
    }

    private void initNoAddons() {
        InputStream ins = getResources().openRawResource(
                getResources().getIdentifier("no_add_items",
                        "raw", getPackageName()));

        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(new InputStreamReader(ins));

        final Type FLAVOUR = new TypeToken<List<Addon>>() {
        }.getType();
        List<Addon> addons = gson.fromJson(jsonReader, FLAVOUR);
        DataInitializers.initNoAddons(addons);
    }

    private void initCommonItems() {
        InputStream ins = getResources().openRawResource(
                getResources().getIdentifier("common_items",
                        "raw", getPackageName()));

        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(new InputStreamReader(ins));

        final Type MENU_ITEMS = new TypeToken<List<Item>>() {
        }.getType();
        List<Item> items = gson.fromJson(jsonReader, MENU_ITEMS);
        DataInitializers.initCommonItems(items);
    }


    private void setPostalCodes() {
        postalInfos.clear();
        postalInfos.addAll(realm.where(PostalInfo.class).findAll());
        Log.e("SUGGESTION_DATA", postalInfos.toString());
    }


    private void createFile(String folderName, String fileName) {
        String parentfolderName = "foodciti";
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + parentfolderName + File.separator + folderName);
        if (!folder.exists())
            folder.mkdirs();

        File file = new File(Environment.getExternalStorageDirectory() + File.separator + parentfolderName + File.separator + folderName + File.separator + fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
*/
