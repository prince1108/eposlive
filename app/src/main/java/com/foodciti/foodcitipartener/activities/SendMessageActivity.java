package com.foodciti.foodcitipartener.activities;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.AllCustomerListAdapter;
import com.foodciti.foodcitipartener.dialogs.OfferMessageDialog;
import com.foodciti.foodcitipartener.realm_entities.CustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.OrderCustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.PostalInfo;
import com.foodciti.foodcitipartener.realm_entities.Purchase;
import com.foodciti.foodcitipartener.services.SmsService;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import io.reactivex.Flowable;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class SendMessageActivity extends AppCompatActivity implements OfferMessageDialog.smsSend/*, SmsSender.MessageStatusListener*/ {
    private static final String TAG = "SendMessageActivity";
    private AllCustomerListAdapter allCustomerListAdapter;
    private RecyclerView customerRecycler;
    private Button sendMessage;
//    private RadioGroup options;
    private CheckBox checkAllCB;
    private View close;
    private Realm realm;
    private TextView totalRecords;
    private ProgressBar progressBar;
    /* private BroadcastReceiver sendBroadcastReceiver = new SentReceiver();
     private BroadcastReceiver deliveryBroadcastReceiver = new DeliverReceiver(this);*/
//    private SmsSender smsSender;

    private EditText startDateET, endDateET, startsWithNoET, orderCountET;
    private Date mDate1, mDate2;
    final Calendar myCalendar = Calendar.getInstance();

    final DatePickerDialog.OnDateSetListener dateSetListener1 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            myCalendar.set(Calendar.HOUR_OF_DAY, 23);
            myCalendar.set(Calendar.MINUTE, 59);
            mDate1 = myCalendar.getTime();
            updateLabel(startDateET);
        }
    };

    final DatePickerDialog.OnDateSetListener dateSetListener2 = (view, year, monthOfYear, dayOfMonth) -> {
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        myCalendar.set(Calendar.HOUR_OF_DAY, 23);
        myCalendar.set(Calendar.MINUTE, 59);
        mDate2 = myCalendar.getTime();
        updateLabel(endDateET);
//        filterDateWise(mDate1, mDate2);
    };

    private void updateLabel(EditText dateField) {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateField.setText(sdf.format(myCalendar.getTime()));
    }

    private void filter(Date start, Date end, String phoneStartsWith, int orderCount) {
        allCustomerListAdapter.setCustomerInfoList(null);
        allCustomerListAdapter.notifyDataSetChanged();
        Log.d(TAG, "-----------------phone starts with: "+phoneStartsWith);
        AtomicReference<RealmResults<CustomerInfo>> reference = new AtomicReference<>();
        AtomicReference<Boolean[]> filterAppliedRef = new AtomicReference<>(new Boolean[3]);
        RealmQuery<Purchase> customerInfoRealmQuery = realm.where(Purchase.class);
        if(start!=null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(start);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 1);
            Date startDateMin = calendar.getTime();
            if (end == null) {
                Log.e(TAG, "-------------mDate2 is NULL");
                end = calendar.getTime();
            }
            calendar.setTime(end);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            Date endDateMax = calendar.getTime();
            customerInfoRealmQuery.between("timestamp", startDateMin.getTime(), endDateMax.getTime());

            customerInfoRealmQuery.findAll().asFlowable().flatMap(purchaseRealmResults -> {
                List<String> phoneList = new ArrayList<>();
                for(Purchase o: purchaseRealmResults) {
                    if(o.getOrderCustomerInfo()!=null) {
                        OrderCustomerInfo orderCustomerInfo = o.getOrderCustomerInfo();
                        if(orderCustomerInfo.getPhone()!=null && !orderCustomerInfo.getPhone().trim().isEmpty())
                            phoneList.add(o.getOrderCustomerInfo().getPhone());
                    }
                }
                return Flowable.just(phoneList);
            }).subscribe(phoneList -> {
                if(!phoneList.isEmpty()) {
                    String[] arr = phoneList.toArray(new String[phoneList.size()]);
                    RealmResults<CustomerInfo> realmResults = RealmManager.getLocalInstance().where(CustomerInfo.class).in("phone", arr).findAll();
                    Log.d(TAG, "-----------------------results empty: "+realmResults.isEmpty());
                    reference.set(realmResults);
                }
            });
        }

        if(orderCount > 0) {
            customerInfoRealmQuery.findAll().asFlowable().flatMap(purchaseRealmResults -> {
                Map<String, Integer> customerInfoIntegerMap = new HashMap<>();
                for(Purchase p: purchaseRealmResults) {
                    if(p.getOrderCustomerInfo()!=null) {
                        OrderCustomerInfo orderCustomerInfo = p.getOrderCustomerInfo();
                        if(orderCustomerInfo.getPhone()!=null && !orderCustomerInfo.getPhone().trim().isEmpty()) {
                            Integer cnt = customerInfoIntegerMap.get(orderCustomerInfo.getPhone());
                            int i = (cnt == null) ? 0 : cnt.intValue();
                            i++;
                            customerInfoIntegerMap.put(orderCustomerInfo.getPhone(), i);
                        }
                    }
                }
                Log.d(TAG, "----------map: "+customerInfoIntegerMap);
                return Flowable.just(customerInfoIntegerMap);
            }).flatMap(map -> {
                List<String> phoneList = new ArrayList<>();
                if(!map.isEmpty()) {
                    Iterator<String> infoIterator = map.keySet().iterator();
                    while (infoIterator.hasNext()) {
                        String phone = infoIterator.next();
                        if(map.get(phone).intValue() >= orderCount) {
                            phoneList.add(phone);
                        }
                    }
                }
                return Flowable.just(phoneList);
            }).subscribe(list -> {
                Log.d(TAG, list.toString());
                String[] arr = list.toArray(new String[list.size()]);
                RealmResults<CustomerInfo> previousResults = reference.get();
                RealmResults<CustomerInfo> customerInfos = null;
                if(previousResults==null) {
                    Log.d(TAG, "-------------------previous results NULL");
                    customerInfos = realm.where(CustomerInfo.class).in("phone", arr).findAll();
                } else {
                    customerInfos = previousResults.where().in("phone", arr).findAll();
                }
                reference.set(customerInfos);
            });
        }

        if(!phoneStartsWith.trim().isEmpty()) {
            RealmResults<CustomerInfo> previousResults = reference.get();
            RealmResults<CustomerInfo> customerInfos=null;
            if(previousResults==null) {
                Log.d(TAG, "---------------previous results NULL--------------------");
                customerInfos = realm.where(CustomerInfo.class).beginsWith("phone", phoneStartsWith.trim()).findAll();
            } else {
                Log.d(TAG, "---------------previous results NOT  NULL--------------------: "+previousResults.toString());
                customerInfos = previousResults.where().beginsWith("phone", phoneStartsWith.trim()).findAll();
            }
            if(customerInfos!=null)
            reference.set(customerInfos);
        }

        if(reference.get()!=null) {
            RealmResults<CustomerInfo> realmResults = reference.get();
            allCustomerListAdapter.setCustomerInfoList(new ArrayList<>(realmResults));
            allCustomerListAdapter.notifyDataSetChanged();
            totalRecords.setText("Total Records : " + realmResults.size() + "/" + realm.where(CustomerInfo.class).count());
        }
    }

    private void filterDateWise(Date start, Date end) {
        RealmQuery<Purchase> customerInfoRealmQuery = realm.where(Purchase.class);
        if(start!=null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(start);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 1);
            Date startDateMin = calendar.getTime();
            if (end == null) {
                Log.e(TAG, "-------------mDate2 is NULL");
                end = calendar.getTime();
            }
            calendar.setTime(end);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            Date endDateMax = calendar.getTime();
            customerInfoRealmQuery.between("timestamp", startDateMin.getTime(), endDateMax.getTime());
        }

        RealmResults<Purchase> purchases = customerInfoRealmQuery.findAll();
        purchases.asFlowable().flatMap(purchaseRealmResults -> {
            List<String> phoneList = new ArrayList<>();
            for(Purchase o: purchaseRealmResults) {
                if(o.getOrderCustomerInfo()!=null) {
                    OrderCustomerInfo orderCustomerInfo = o.getOrderCustomerInfo();
                    if(orderCustomerInfo.getPhone()!=null && !orderCustomerInfo.getPhone().trim().isEmpty())
                        phoneList.add(o.getOrderCustomerInfo().getPhone());
                }
            }
            return Flowable.just(phoneList);
        }).subscribe(phoneList -> {
            if(!phoneList.isEmpty()) {
                String[] arr = phoneList.toArray(new String[phoneList.size()]);
                RealmResults<CustomerInfo> customerInfos = RealmManager.getLocalInstance().where(CustomerInfo.class).in("phone", arr).findAll();
                allCustomerListAdapter.setCustomerInfoList(customerInfos);
                allCustomerListAdapter.notifyDataSetChanged();
            }
        });
    }

    private void numberFilter(String phone) {
        RealmQuery<CustomerInfo> realmQuery = realm.where(CustomerInfo.class);
        RealmResults<CustomerInfo> customerInfos = realmQuery.beginsWith("phone", phone).findAll();
        allCustomerListAdapter.setCustomerInfoList(customerInfos);
        allCustomerListAdapter.notifyDataSetChanged();
    }

    private void lastOrderFilter(int count) {
        RealmQuery<Purchase> realmQuery = realm.where(Purchase.class);
        realmQuery.findAll().asFlowable().flatMap(purchaseRealmResults -> {
            Map<String, Integer> customerInfoIntegerMap = new HashMap<>();
            for(Purchase p: purchaseRealmResults) {
                if(p.getOrderCustomerInfo()!=null) {
                    OrderCustomerInfo orderCustomerInfo = p.getOrderCustomerInfo();
                    if(orderCustomerInfo.getPhone()!=null && !orderCustomerInfo.getPhone().trim().isEmpty()) {
                        Integer cnt = customerInfoIntegerMap.get(orderCustomerInfo.getPhone());
                        int i = (cnt == null) ? 0 : cnt.intValue();
                        i++;
                        customerInfoIntegerMap.put(orderCustomerInfo.getPhone(), i);
                    }
                }
            }
            Log.d(TAG, "----------map: "+customerInfoIntegerMap);
            return Flowable.just(customerInfoIntegerMap);
        }).flatMap(map -> {
            List<String> phoneList = new ArrayList<>();
                if(!map.isEmpty()) {
                    Iterator<String> infoIterator = map.keySet().iterator();
                    while (infoIterator.hasNext()) {
                        String phone = infoIterator.next();
                        if(map.get(phone).intValue() >= count) {
                            phoneList.add(phone);
                        }
                    }
                }
                return Flowable.just(phoneList);
        }).subscribe(list -> {
            String[] arr = list.toArray(new String[list.size()]);
            RealmResults<CustomerInfo> customerInfos = RealmManager.getLocalInstance().where(CustomerInfo.class).in("phone", arr).findAll();
            allCustomerListAdapter.setCustomerInfoList(customerInfos);
            allCustomerListAdapter.notifyDataSetChanged();
        });
    }
    private RealmResults<CustomerInfo> customerInfoRealmResultsAsync = null;

    private LocalBroadcastManager localBroadcastManager;


    private SmsService mBoundService;

    // code for creating bound service starts; a bound service has the same lifecycle as the calling activity
    private boolean mIsBound;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has
            // been established, giving us the service object we can use
            // to interact with the service.  Because we have bound to a
            // explicit service that we know is running in our own
            // process, we can cast its IBinder to a concrete class and
            // directly access it.
            mBoundService = ((SmsService.LocalBinder)service).getService();

            // Tell the user about this for our demo.
            Toast.makeText(SendMessageActivity.this,
                    "SMS Service connected",
                    Toast.LENGTH_SHORT).show();
            Log.d("MainActivity", "----------------------------service connected--------------------------------");
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has
            // been unexpectedly disconnected -- that is, its process
            // crashed. Because it is running in our same process, we
            // should never see this happen.
            mBoundService = null;
            Toast.makeText(SendMessageActivity.this,
                    "SMS Service disconnected",
                    Toast.LENGTH_SHORT).show();
        }
    };

    private BroadcastReceiver smsDeliveredBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String number = intent.getStringExtra("c_no");
            Toast.makeText(SendMessageActivity.this, "DELIVERY Broadcast : " + number, Toast.LENGTH_LONG).show();
            Log.e("MSG_DEL", "" + number);
            realm.executeTransaction(r -> {
//                CustomerInfo customerInfo = realm.where(CustomerInfo.class).equalTo("phone", number).findFirst();
                CustomerInfo customerInfo = allCustomerListAdapter.getStringCustomerInfoMap().get(number);
                customerInfo.setMsgDeliveryStatus("Delivered");
                allCustomerListAdapter.notifyItemChanged(allCustomerListAdapter.getCustomerInfoList().indexOf(customerInfo));
            });
        }
    };

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation
        // that we know will be running in our own process (and thus
        // won't be supporting component replacement by other
        // applications).
        bindService(new Intent(SendMessageActivity.this, SmsService.class),
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

    // code to create bound service ends


    @Override
    protected void onPause() {
        if (customerInfoRealmResultsAsync != null)
            customerInfoRealmResultsAsync.removeAllChangeListeners();
        localBroadcastManager.unregisterReceiver(smsDeliveredBroadcast);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        localBroadcastManager.registerReceiver(smsDeliveredBroadcast, new IntentFilter(SmsService.SMS_DELIVERED_ACTION));
        showAll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        doUnbindService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
//        doBindService();
        mBoundService = SmsService.instance;

        realm = RealmManager.getLocalInstance();
//        smsSender = new SmsSender(this);
        initViews();
        allCustomerListAdapter = new AllCustomerListAdapter(this, null);
        customerRecycler.setAdapter(allCustomerListAdapter);

/*        options.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.show_all:
                        showAll();
                        break;
                    case R.id.show_last_month:
                        showLastMonth();
                        break;
                    case R.id.Number_start:
                        showNumberStart();
                        break;
                    case R.id.last_five:
//                        showLastFive();
                        break;
                }
            }
        });*/

        /*RadioButton rb1 = findViewById(R.id.show_all);
        rb1.setChecked(true);
        RadioButton rb2 = findViewById(R.id.show_last_month);
        RadioButton rb3 = findViewById(R.id.Number_start);
        RadioButton rb4 = findViewById(R.id.last_five);*/
    }

    private void initViews() {
        customerRecycler = findViewById(R.id.customerRecycler);
//        options = findViewById(R.id.options);
        close = findViewById(R.id.close);
        close.setOnClickListener(v -> {
            onBackPressed();
        });
        sendMessage = findViewById(R.id.sendMessage);
        totalRecords = findViewById(R.id.totalRecords);
        progressBar = findViewById(R.id.progress_bar);

        sendMessage.setOnClickListener(v -> {
            Toast.makeText(mBoundService, "List size: "+allCustomerListAdapter.getItemCount(), Toast.LENGTH_SHORT).show();
            StringBuilder numbers = new StringBuilder();
            int count = 0;
            for (CustomerInfo info : allCustomerListAdapter.getCustomerInfoList()) {
                if (info.isSelected())
                    count++;
            }
            Log.d(TAG, "-------------------------count: "+count);
            if (count > 0) {
                Iterator<CustomerInfo> userInfoIterator = allCustomerListAdapter.getCustomerInfoList().iterator();
                while (userInfoIterator.hasNext()) {
                    CustomerInfo customerInfo = userInfoIterator.next();
                    if (!customerInfo.isSelected())
                        continue;
                    if (userInfoIterator.hasNext())
                        numbers.append(customerInfo.getPhone().trim()).append(";");
                    else
                        numbers.append(customerInfo.getPhone().trim());
                }
                Log.d(TAG, "-------------------numbers: "+numbers);

                OfferMessageDialog addition = OfferMessageDialog.getInstance(numbers.toString(), SendMessageActivity.this);
                addition.show(getSupportFragmentManager(), null);
            } else {
                Toast.makeText(SendMessageActivity.this, "No Number Selected !", Toast.LENGTH_LONG);
            }
        });

        checkAllCB = findViewById(R.id.header_check);
        checkAllCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d(TAG,"-------------select all: "+isChecked);
            allCustomerListAdapter.setSelectAll(isChecked);
        });
//        checkAllCB.setChecked(true);

        startDateET = findViewById(R.id.startDateET);
        startDateET.setOnClickListener(v->{
            new DatePickerDialog(SendMessageActivity.this, dateSetListener1, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
        endDateET = findViewById(R.id.endDateET);
        endDateET.setOnClickListener(v->{
             new DatePickerDialog(SendMessageActivity.this, dateSetListener2, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
        startsWithNoET = findViewById(R.id.startsWithNoET);
        /*startsWithNoET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                numberFilter(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
        orderCountET = findViewById(R.id.orderCountET);
        /*orderCountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty())
                    lastOrderFilter(Integer.parseInt(s.toString().trim()));
                else {
                    allCustomerListAdapter.setCustomerInfoList(null);
                    allCustomerListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        findViewById(R.id.filter).setOnClickListener(v->{
            String orderCountStr = orderCountET.getText().toString().trim();
            int count = (orderCountStr.isEmpty())?0: Integer.parseInt(orderCountStr);
            filter(mDate1, mDate2, startsWithNoET.getText().toString(), count);
        });

        findViewById(R.id.clearFilter).setOnClickListener(v->{
            showAll();
        });
    }

    private void showAll() {
        progressBar.setVisibility(View.VISIBLE);
//        customerInfoList.clear();
        totalRecords.setText("");
        customerInfoRealmResultsAsync = realm.where(CustomerInfo.class).isNotEmpty("phone").sort("postalInfo.address", Sort.ASCENDING).findAllAsync();
        customerInfoRealmResultsAsync.addChangeListener(results -> {
            /*for (CustomerInfo u : results) {
                customerInfoList.add(u);
            }*/
            allCustomerListAdapter.setCustomerInfoList(new ArrayList<>(results));
            allCustomerListAdapter.notifyDataSetChanged();
            allCustomerListAdapter.setSelectAll(checkAllCB.isChecked());
            progressBar.setVisibility(View.GONE);
            totalRecords.setText("Total Records : " + results.size() + "/" + realm.where(CustomerInfo.class).count());
        });
    }

    private void showLastMonth() {
        progressBar.setVisibility(View.VISIBLE);
        totalRecords.setText("");
        allCustomerListAdapter.notifyDataSetChanged();
        Date referenceDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(referenceDate);
        c.add(Calendar.MONTH, -3);
        Log.e(TAG, "---------------reference data time: " + referenceDate.getTime());
        RealmQuery<CustomerInfo> realmQuery = realm.where(CustomerInfo.class).distinct("id");
        realmQuery.greaterThanOrEqualTo("user_visited_date_time", c.getTime().getTime());
        customerInfoRealmResultsAsync = realmQuery.sort("postalInfo.address", Sort.ASCENDING).findAllAsync();
        customerInfoRealmResultsAsync.addChangeListener(new RealmChangeListener<RealmResults<CustomerInfo>>() {
            @Override
            public void onChange(RealmResults<CustomerInfo> customerInfos) {
                Toast.makeText(SendMessageActivity.this, "transaction successful", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                /*for (CustomerInfo u : customerInfos) {
                    customerInfoList.add(u);
                }*/
                allCustomerListAdapter.setCustomerInfoList(new ArrayList<>(customerInfos));
                allCustomerListAdapter.notifyDataSetChanged();
                allCustomerListAdapter.setSelectAll(checkAllCB.isChecked());
                totalRecords.setText("Total Records : " + allCustomerListAdapter.getCustomerInfoList().size() + "/" + realm.where(CustomerInfo.class).count());
            }
        });
    }

    private void showNumberStart() {
        progressBar.setVisibility(View.VISIBLE);
        totalRecords.setText("");
        allCustomerListAdapter.notifyDataSetChanged();

        customerInfoRealmResultsAsync = realm.where(CustomerInfo.class).like("phone", "07*")
                .and().isNotNull("phone").sort("postalInfo.address", Sort.ASCENDING).findAllAsync();
        customerInfoRealmResultsAsync.addChangeListener(new RealmChangeListener<RealmResults<CustomerInfo>>() {
            @Override
            public void onChange(final RealmResults<CustomerInfo> customerInfos) {
                /*for (CustomerInfo u : customerInfos) {
                    customerInfoList.add(u);
                }*/
                allCustomerListAdapter.setCustomerInfoList(new ArrayList<>(customerInfos));
                allCustomerListAdapter.notifyDataSetChanged();
                allCustomerListAdapter.setSelectAll(checkAllCB.isChecked());
                progressBar.setVisibility(View.GONE);
                totalRecords.setText("Total Records : " + allCustomerListAdapter.getCustomerInfoList().size() + "/" + realm.where(CustomerInfo.class).count());
            }
        });
    }

/*    private void showLastFive() {
        progressBar.setVisibility(View.VISIBLE);
        customerInfoList.clear();
        totalRecords.setText("");
        allCustomerListAdapter.notifyDataSetChanged();
        try {
            RealmResults<OrderHistory> orderHistories = realm.where(OrderHistory.class).sort("id", Sort.DESCENDING).limit(5).findAllAsync()
                    .where().sort("customer.postalInfo.address", Sort.ASCENDING).findAllAsync();
            orderHistories.addChangeListener(new RealmChangeListener<RealmResults<OrderHistory>>() {
                @Override
                public void onChange(RealmResults<OrderHistory> orderHistories) {
                    for (OrderHistory o : orderHistories) {
                        CustomerInfoActivity u = o.getCustomer();
                        if (u != null && !customerInfoList.contains(u)) {
                            u.setSelected(true);
                            customerInfoList.add(u);
                        }
                    }
                    allCustomerListAdapter.notifyDataSetChanged();
                    totalRecords.setText("Total Records : " + customerInfoList.size() + "/" + realm.where(CustomerInfoActivity.class).count());
                    progressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }*/

    @Override
    public void sendMessage(long[] userId, String textMessage) {
        realm.executeTransaction(r -> {
            for (CustomerInfo u : allCustomerListAdapter.getCustomerInfoList()) {
                if (u.isSelected()) {
                    Log.e("SEND_MSG", "" + textMessage + "\n" + u.getPhone().trim());
//                    smsSender.sendSMS(u.getPhone().trim(), textMessage);
                    mBoundService.sendSms(u.getPhone().trim(), textMessage);
                    u.setMsgDeliveryStatus("Sending...");
                    allCustomerListAdapter.notifyItemChanged(allCustomerListAdapter.getCustomerInfoList().indexOf(u));
                }
            }
        });
    }

    /*@Override
    public void onMessageDelivered(String number) {
        Toast.makeText(this, "DELIVERY : " + number, Toast.LENGTH_LONG).show();
        Log.e("MSG_DEL", "" + number);
        realm.executeTransaction(r -> {
            CustomerInfo customerInfo = realm.where(CustomerInfo.class).equalTo("phone", number).findFirst();
            customerInfo.setMsgDeliveryStatus("Delivered");
            allCustomerListAdapter.notifyItemChanged(customerInfoList.indexOf(customerInfo));
        });

        try {
            smsSender.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }*/

/*    private void sendSMS(final String phoneNumber, String message)
    {
        IntentFilter sentIF = new IntentFilter(phoneNumber);

        IntentFilter delIF = new IntentFilter(phoneNumber);

        registerReceiver(sendBroadcastReceiver, sentIF);
        registerReceiver(deliveryBroadcastReceiver, delIF);

        Intent intent = new Intent(phoneNumber);
        Intent intentDel = new Intent(phoneNumber);
        intentDel.putExtra("c_no", phoneNumber);
        intent.putExtra("c_no", phoneNumber);
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);


        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                intentDel, PendingIntent.FLAG_CANCEL_CURRENT);
        try {
            SmsManager sms = SmsManager.getDefault();

            sms.sendTextMessage(phoneNumber, "", message, sentPI, deliveredPI);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("MainActivity.sendSMS EXCEPTION: " + ex.getMessage());
        }
    }*/
}
