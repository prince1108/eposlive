package com.foodciti.foodcitipartener.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.activities.NewCustomerInfoActivity;
import com.foodciti.foodcitipartener.keyboards.ExtendedKeyBoard;
import com.foodciti.foodcitipartener.realm_entities.CustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.PostalInfo;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.RealmManager;
import com.foodciti.foodcitipartener.utils.StringHelper;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.content.Context.MODE_PRIVATE;
import static com.foodciti.foodcitipartener.utils.AppConfig.MyPREFERENCES;


public class UpdateCustomerDetails extends DialogFragment implements View.OnKeyListener, View.OnFocusChangeListener, View.OnClickListener {
    private static final String TAG = "UpdateCustomerDetails";
    private TextView update_btn, title;
    private Realm myRealm;
    private EditText nameET, houseNoET, addressET, specialNotesET;
    private Callback mListener;
    private AutoCompleteTextView telephoneET, postcodeET;
    private RadioGroup radioGroupOrderType;
    private ImageView closebtn;
    private boolean calleridstatus = false;
    private SharedPreferences.Editor editor;
    private CustomerInfo customerInfo;

    private SharedPreferences shared;
    private ExtendedKeyBoard keyboard;
    private InputMethodManager imm;

    private static final String ARG_USER_ID = "userId";
    private static final String ARG_ORDER_TYPE = "order_type";
    private static final String ARG_VERIFY = "verify";
    private static final String ARG_NOTE = "note";

    private long userId;
    private String orderType, note;

    private RealmResults<PostalInfo> postalInfos;
    private RealmResults<CustomerInfo> customerInfos;

    private ArrayAdapter<String> postcodeArrayAdapter, telephoneArrayAdapter;

    /*public static UpdateCustomerDetails getInstance(boolean verify) {
        UpdateCustomerDetails addition = new UpdateCustomerDetails();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ARG_VERIFY, verify);
        addition.setArguments(bundle);
        return addition;
    }*/

    private UpdateCustomerDetails(){}

    public static UpdateCustomerDetails getInstance(long userId, String orderType) {
        UpdateCustomerDetails updateCustomerDetails = new UpdateCustomerDetails();
        Bundle bundle = new Bundle();
        bundle.putLong(ARG_USER_ID, userId);
        bundle.putString(ARG_ORDER_TYPE, orderType);
        updateCustomerDetails.setArguments(bundle);
        updateCustomerDetails.setCancelable(true);
        return updateCustomerDetails;
    }

    public static UpdateCustomerDetails getInstance(long userId, String orderType, String note) {
        UpdateCustomerDetails updateCustomerDetails = new UpdateCustomerDetails();
        Bundle bundle = new Bundle();
        bundle.putLong(ARG_USER_ID, userId);
        bundle.putString(ARG_ORDER_TYPE, orderType);
        bundle.putString(ARG_NOTE, note);
        updateCustomerDetails.setArguments(bundle);
        updateCustomerDetails.setCancelable(true);
        return updateCustomerDetails;
    }

    private void removeChangeListenersOnAsyncResults() {
        if (postalInfos != null)
            postalInfos.removeAllChangeListeners();
        if (customerInfos != null)
            customerInfos.removeAllChangeListeners();
    }

    private void setAutocompleteForPostcode() {
        postalInfos = myRealm.where(PostalInfo.class).findAllAsync();
        postalInfos.addChangeListener(p -> {
            String[] postcodes = new String[p.size()];
            for (int i = 0; i < postcodes.length; i++) {
                postcodes[i] = p.get(i).getA_PostCode();
            }
            postcodeArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, postcodes);
            postcodeET.setAdapter(postcodeArrayAdapter);
        });
    }

    private void setAutocompleteForPhone() {
        customerInfos = myRealm.where(CustomerInfo.class).findAllAsync();
        customerInfos.addChangeListener(p -> {
            String[] telephones = new String[p.size()];
            for (int i = 0; i < telephones.length; i++) {
                telephones[i] = p.get(i).getPhone();
            }
            telephoneArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, telephones);
            telephoneET.setAdapter(telephoneArrayAdapter);
        });
    }

    private CustomerInfo createNewUserInfo() {
        Number maxId = myRealm.where(CustomerInfo.class).max("id");
        long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
        CustomerInfo blankCustomerInfo = myRealm.createObject(CustomerInfo.class, nextId);
        return blankCustomerInfo;
    }

    /*private <E extends RealmObject> Object[] createBeanIfNotExists(Class<E> aClass, String primarykeyField, String primaryKey) {
        AtomicReference<E> beanRef = new AtomicReference<>();
        E bean = myRealm.where(aClass).equalTo(primarykeyField, primaryKey).findFirst();
        beanRef.set(bean);
        final Object[] results = new Object[2];
        results[1] = new Boolean(false);
        if (bean == null) {
            Log.e(TAG, "-----------------creating new bean of type: " + aClass.getSimpleName());
            if (!myRealm.isInTransaction()) {
                myRealm.executeTransaction(r -> {
                    Number maxId = r.where(aClass).max("id");
                    long nextId = (maxId == null) ? 0 : maxId.longValue() + 1;
                    beanRef.set(r.createObject(aClass, nextId));
                    results[1] = new Boolean(true);
                });
            } else {
                Number maxId = myRealm.where(aClass).max("id");
                long nextId = (maxId == null) ? 0 : maxId.longValue() + 1;
                beanRef.set(myRealm.createObject(aClass, nextId));
                results[1] = new Boolean(true);
            }
        } else
            Log.e(TAG, "-----------------bean exists of type: " + aClass.getSimpleName());
        results[0] = beanRef.get();
        return results;
    }*/

    private CustomerInfo insertUserInfoForNonDeliveryOrders() {
        AtomicReference<CustomerInfo> customerInfoAtomicReference = new AtomicReference<>(null);
        myRealm.executeTransaction(r -> {
            CustomerInfo bean = null;
            final String phone = telephoneET.getText().toString().trim();
            final String name = nameET.getText().toString().trim();
            final String postCode = postcodeET.getText().toString().trim();
            final String houseNo = houseNoET.getText().toString().trim();
            final String city = addressET.getText().toString().trim();

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
                customerInfoAtomicReference.set(bean);
            } else {
                if (!phone.isEmpty()) {
                    Object[] results = NewCustomerInfoActivity.createBeanIfNotExists(CustomerInfo.class, "phone", phone, myRealm);
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
                    Object[] results = NewCustomerInfoActivity.createBeanIfNotExists(PostalInfo.class, "A_PostCode", postCode, myRealm);
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getLong(ARG_USER_ID);
            orderType = getArguments().getString(ARG_ORDER_TYPE);
            note = getArguments().getString(ARG_NOTE);
        }
    }

    @Override
    public void onDestroyView() {
        removeChangeListenersOnAsyncResults();
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.update_customer_info, container,
                false);
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        shared = getActivity().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        myRealm = RealmManager.getLocalInstance();
        editor = getActivity().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        final String formattedDate = df.format(date);

        editor = getActivity().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();

        title = rootView.findViewById(R.id.title);
        Log.e(TAG, "---------------------------userid: " + userId);
        customerInfo = myRealm.where(CustomerInfo.class).equalTo("id", userId).findFirst();
        String customerPhone = "";
        String customerName = "";
        String houseNo = "";
        String CustomerAddress = "";
        String CustomerPostalCode = "";
        if (customerInfo != null) {
            title.setText("Update Customer Details");
            customerPhone = (customerInfo.getPhone() == null) ? "" : customerInfo.getPhone().trim();
            customerName = (customerInfo.getName() == null) ? "" : customerInfo.getName().trim();
            houseNo = (customerInfo.getHouse_no() == null) ? "" : customerInfo.getHouse_no().trim();
            CustomerAddress = "";
            CustomerPostalCode = "";
            StringBuilder addressString = new StringBuilder();

            if (customerInfo.getPostalInfo() != null) {
                String address = customerInfo.getPostalInfo().getAddress();
                if (address != null || !address.trim().isEmpty())
                    addressString.append(StringHelper.capitalizeEachWordAfterComma(address));
                CustomerAddress = addressString.toString();
                CustomerPostalCode = customerInfo.getPostalInfo().getA_PostCode().trim();
            }

        } else {
            Toast.makeText(getActivity(), "User not found in Database", Toast.LENGTH_SHORT).show();
            title.setText("Add Customer Details");
        }

        keyboard = rootView.findViewById(R.id.keyBoard);

        radioGroupOrderType = rootView.findViewById(R.id.radiogroup_ordertype);
        RadioButton collectionRB = rootView.findViewById(R.id.collection);
        RadioButton deliveryRB = rootView.findViewById(R.id.delivery);
        radioGroupOrderType.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.collection:
                    orderType = Constants.TYPE_COLLECTION;
                    break;
                case R.id.delivery:
                    orderType = Constants.TYPE_DELIVERY;
                    break;
            }
        });

        if (orderType != null && orderType.equals(Constants.TYPE_TABLE) || orderType == null)
            radioGroupOrderType.setVisibility(View.GONE);
        else {
            if (orderType.equals(Constants.TYPE_DELIVERY))
                deliveryRB.setChecked(true);
            else
                collectionRB.setChecked(true);
        }

        telephoneET = rootView.findViewById(R.id.telephone);
        telephoneET.setShowSoftInputOnFocus(false);
        telephoneET.setOnFocusChangeListener(this);
        telephoneET.setOnKeyListener(this);
        telephoneET.setOnClickListener(this);
        telephoneET.setThreshold(1);

        postcodeET = rootView.findViewById(R.id.postcode);
        postcodeET.setShowSoftInputOnFocus(false);
        postcodeET.setOnFocusChangeListener(this);
        postcodeET.setOnKeyListener(this);
        postcodeET.setOnClickListener(this);
        postcodeET.setThreshold(1);

        nameET = rootView.findViewById(R.id.name);
        nameET.setShowSoftInputOnFocus(false);
        nameET.setTextIsSelectable(true);
        nameET.setOnFocusChangeListener(this);
        nameET.setOnKeyListener(this);
        nameET.setOnClickListener(this);

        houseNoET = rootView.findViewById(R.id.houseNo);
        houseNoET.setShowSoftInputOnFocus(false);
        houseNoET.setTextIsSelectable(true);
        houseNoET.setOnFocusChangeListener(this);
        houseNoET.setOnKeyListener(this);
        houseNoET.setOnClickListener(this);

        addressET = rootView.findViewById(R.id.customerAddress);
        addressET.setShowSoftInputOnFocus(false);
        addressET.setTextIsSelectable(true);
        addressET.setOnFocusChangeListener(this);
        addressET.setOnKeyListener(this);
        addressET.setOnClickListener(this);

        specialNotesET = rootView.findViewById(R.id.special_note);
        specialNotesET.setText(note);
        specialNotesET.setShowSoftInputOnFocus(false);
        specialNotesET.setTextIsSelectable(true);
        specialNotesET.setOnFocusChangeListener(this);
        specialNotesET.setOnKeyListener(this);

        try {
            setAutocompleteForPhone();
            setAutocompleteForPostcode();
        } catch (Exception e) {
            e.printStackTrace();
        }

        telephoneET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                customerInfo = myRealm.where(CustomerInfo.class).equalTo("phone", s.toString()).findFirst();
                if (customerInfo != null) {
                    houseNoET.setText(StringHelper.capitalizeEachWord(customerInfo.getHouse_no()));
                    PostalInfo postalInfo = customerInfo.getPostalInfo();
                    addressET.setText("");
                    if (postalInfo != null) {
                        postcodeET.setText(postalInfo.getA_PostCode());
                        postcodeET.dismissDropDown();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        postcodeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (!str.equals(str.toUpperCase())) {
                    str = str.toUpperCase();
                    postcodeET.setText(str);
                    postcodeET.setSelection(postcodeET.length()); //fix reverse texting
                }
                PostalInfo postalInfos = myRealm.where(PostalInfo.class).equalTo("A_PostCode", str).findFirst();
                addressET.setText("");
                if (postalInfos != null) {
                    Log.e(TAG, "-------------address: " + postalInfos.getAddress());
                    String[] tokens = StringHelper.capitalizeEachWordAfterComma(postalInfos.getAddress().trim()).split(",");
                    List<String> stringList = Arrays.asList(tokens);
                    Iterator<String> stringIterator = stringList.iterator();
                    while (stringIterator.hasNext()) {
                        final String st = stringIterator.next();
                        if (stringIterator.hasNext())
                            addressET.append(st.trim() + ",\n");
                        else
                            addressET.append(st.trim());
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                CustomerAddressDialoge tablesFullViewDialog = CustomerAddressDialoge.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("PostCode",s.toString());
                tablesFullViewDialog.setArguments(bundle);
                tablesFullViewDialog.setCallback((dialog, houseNo) -> {
                    houseNoET.setText(houseNo);
                    dialog.dismiss();
                });
                tablesFullViewDialog.show(getChildFragmentManager(), null);
            }
        });

        nameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (!str.equals(str.toUpperCase())) {
                    str = str.toUpperCase();
                    nameET.setText(str);
                    nameET.setSelection(nameET.length()); //fix reverse texting
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
                String str = s.toString();
                if (!str.equals(str.toUpperCase())) {
                    str = str.toUpperCase();
                    houseNoET.setText(str);
                    houseNoET.setSelection(houseNoET.length()); //fix reverse texting
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addressET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (!str.equals(str.toUpperCase())) {
                    str = str.toUpperCase();
                    addressET.setText(str);
                    addressET.setSelection(addressET.length()); //fix reverse texting
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        specialNotesET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (!str.equals(str.toUpperCase())) {
                    str = str.toUpperCase();
                    specialNotesET.setText(str);
                    specialNotesET.setSelection(specialNotesET.length()); //fix reverse texting
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (customerPhone.equalsIgnoreCase("")) {

        } else {
            telephoneET.setText(customerPhone);
        }
        if (customerName.equalsIgnoreCase("")) {

        } else {
            nameET.setText(customerName);
        }
        if (houseNo.equalsIgnoreCase("")) {

        } else {
            houseNoET.setText(houseNo);
        }
        if (CustomerAddress.equalsIgnoreCase("")) {

        } else {
            addressET.setText(CustomerAddress);
        }
        if (CustomerPostalCode.equalsIgnoreCase("")) {

        } else {
            postcodeET.setText(CustomerPostalCode);
        }


        closebtn = rootView.findViewById(R.id.close);
        closebtn.setOnClickListener(v -> {
            dismiss();
        });


        calleridstatus = true;

        telephoneET.requestFocus();
        return rootView;
    }

    private void save() {
        if (orderType != null && orderType.equals(Constants.TYPE_DELIVERY)) {
            if (!addressET.getText().toString().isEmpty() && !houseNoET.getText().toString().isEmpty() && !postcodeET.getText().toString().isEmpty() && !telephoneET.getText().toString().isEmpty()) {
                myRealm.executeTransaction(r -> {
                    if (customerInfo == null)
                        customerInfo = createNewUserInfo();
                    customerInfo.setPhone(telephoneET.getText().toString().trim());
                    customerInfo.setName(nameET.getText().toString().trim());
                    customerInfo.setHouse_no(houseNoET.getText().toString().trim());
                    PostalInfo postalInfo = r.where(PostalInfo.class).equalTo("A_PostCode", postcodeET.getText().toString().trim()).findFirst();
                    if (postalInfo == null) {
                        Number maxId = r.where(PostalInfo.class).max("id");
                        long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
                        postalInfo = r.createObject(PostalInfo.class, nextId);
                        postalInfo.setA_PostCode(postcodeET.getText().toString().trim());
                        postalInfo.setAddress(addressET.getText().toString().trim());
                    } else {
//                                postalInfo.setA_PostCode(postcodeET.getText().toString().trim());
                        postalInfo.setAddress(addressET.getText().toString().trim());
                    }
                    customerInfo.setPostalInfo(postalInfo);
                    mListener.onUpdateCustomerDetails(customerInfo.getId(), orderType, specialNotesET.getText().toString().trim());
                });

                dismiss();
            } else {
                Toast toast = Toast.makeText(getActivity(), "All fields are mandatory", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        } else {
            CustomerInfo customerInfo = insertUserInfoForNonDeliveryOrders();
            mListener.onUpdateCustomerDetails(customerInfo.getId(), orderType, specialNotesET.getText().toString().trim());
            dismiss();
        }
    }


    private void EditTextInstaSetter() {
        postcodeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

//                getPostalCodeAddress(charSequence);
// }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void getPostalCodeAddress(CharSequence charSequence) {
        String url = "http://maps.googleapis.com/maps/api/geocode/json?address=" + charSequence + "&sensor=true";
        LayoutInflater layoutInflater = getLayoutInflater();

       /* StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            addressET.setText("");
                            JSONObject jsonObject= new JSONObject(response);
                            if(jsonObject.getString("status").equalsIgnoreCase("OK")){
                                JSONArray jsonArray=jsonObject.getJSONArray("results");

                                JSONObject jsonObject1=jsonArray.getJSONObject(0);
                                String name=jsonObject1.getString("formatted_address");

                               *//* String[] list_countries={name};

                               // String[] countries=getResources().getStringArray(R.array.list_countries);
                                ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,list_countries);
                                postcodeET.setAdapter(adapter);*//*

                                Toast.makeText(getActivity(), " "+name, Toast.LENGTH_SHORT).show();
                                addressET.setText(name);

                            }else{
// postcodeET.setText("");
                                Toast.makeText(getActivity(), "No Result Found", Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        if(backQueue==null){
            if(this!=null){
                backQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
                backQueue.getCache().clear();
                DiskBasedCache cache = new DiskBasedCache(getActivity().getCacheDir(), 16 * 1024 * 1024);
                backQueue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
                backQueue.start();
                stringRequest.setShouldCache(false);
                backQueue.add(stringRequest);
            }

        }else{
            if(this!=null){
                backQueue.getCache().clear();
                stringRequest.setShouldCache(false);
                backQueue.add(stringRequest);
            }
        }*/
    }


  /*  private void getPostalCodeAddress(){
        String url ="http://maps.googleapis.com/maps/api/geocode/json?address="+value+"&sensor=true";
        LayoutInflater layoutInflater = getLayoutInflater();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject= new JSONObject(response);
                            if(jsonObject.getString("status").equalsIgnoreCase("OK")){
                                JSONArray jsonArray=jsonObject.getJSONArray("results");

                                JSONObject jsonObject1=jsonArray.getJSONObject(0);
                                String name=jsonObject1.getString("formatted_address");

                                //     Toast.makeText(getActivity(), " "+name, Toast.LENGTH_SHORT).show();

                                addressET.setText(name);
                            }else{
                                postcodeET.setText("");
                                Toast.makeText(getActivity(), "No Result Found", Toast.LENGTH_SHORT).show();
                            }
                          //  Toast.makeText(getActivity(), "Server response", Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       // Toast.makeText(getActivity(), "Server not response", Toast.LENGTH_SHORT).show();
                    }
                });

        if(backQueue==null){
            if(this!=null){
                backQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
                backQueue.getCache().clear();
                DiskBasedCache cache = new DiskBasedCache(getActivity().getCacheDir(), 16 * 1024 * 1024);
                backQueue = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
                backQueue.start();
                stringRequest.setShouldCache(false);
                backQueue.add(stringRequest);
            }

        }else{
            if(this!=null){
                backQueue.getCache().clear();
                stringRequest.setShouldCache(false);
                backQueue.add(stringRequest);
            }
        }
    }*/


    @Override
    public void onDestroy() {
        super.onDestroy();
        telephoneET.dismissDropDown();
        postcodeET.dismissDropDown();
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
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.99);
            dialog.getWindow()
                    .setLayout(width, height);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Callback) {
            mListener = (Callback) context;
        } /*else {
            throw new RuntimeException(context.toString()
                    + " must implement UpdateCustomerDetails.Callback");
        }*/
    }

    public int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    public void setmListener(Callback mListener) {
        this.mListener = mListener;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Log.e(TAG, "-------------------keycode: " + keyCode);
        switch (keyCode) {
            case ExtendedKeyBoard.KEYCODE_DONE:
                save();
                return true;

            case KeyEvent.KEYCODE_CLEAR:
                if (v instanceof EditText) {
                    EditText et = (EditText) v;
                    et.setText("");
                    return true;
                }
        }
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        Log.e(TAG, "------------------focus changed-------------------");
//        hideKeyBoard(v);
        EditText editText = null;
        InputConnection ic = null;
        if (v instanceof EditText) {
            editText = (EditText) v;
            ic = editText.onCreateInputConnection(new EditorInfo());
        }

        if (hasFocus && ic != null) {
            keyboard.setInputConnection(ic);
        }
    }

    private void hideKeyBoard(View v) {
        final InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocusedView = getActivity().getCurrentFocus();
        if (currentFocusedView != null)
            im.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onClick(View v) {
       /* if(v instanceof EditText)
            hideKeyBoard(v);*/
    }

    public interface Callback {
        void onUpdateCustomerDetails(long userId, String orderType, String note);
    }
}
