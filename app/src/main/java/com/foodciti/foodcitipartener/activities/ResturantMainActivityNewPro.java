package com.foodciti.foodcitipartener.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.OrderAdapter;
import com.foodciti.foodcitipartener.dialogs.ConfirmationDialog;
import com.foodciti.foodcitipartener.dialogs.FoodcitiAlertDialog;
import com.foodciti.foodcitipartener.dialogs.ForwardedOrderListDialog;
import com.foodciti.foodcitipartener.dialogs.ItemSubDetails;
import com.foodciti.foodcitipartener.dialogs.OrderInfo;
import com.foodciti.foodcitipartener.response.GenericResponse;
import com.foodciti.foodcitipartener.response.Order;
import com.foodciti.foodcitipartener.response.OrderedItem;
import com.foodciti.foodcitipartener.response.SubItem;
import com.foodciti.foodcitipartener.rest.RetroClient;
import com.foodciti.foodcitipartener.services.FirebaseBackgroundService;
import com.foodciti.foodcitipartener.utils.Consts;
import com.foodciti.foodcitipartener.utils.InternetConnection;
import com.foodciti.foodcitipartener.utils.SessionManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.snapshot.ChildKey;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.foodciti.foodcitipartener.utils.AppConfig.MyPREFERENCES;


public class ResturantMainActivityNewPro extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OrderInfo.OnClickListener,
        OrderAdapter.OnItemClickListener, View.OnClickListener {
    private String TAG = "ResturantMainActivityNewPro";
    private Context context;
    private DrawerLayout drawer;
    private TextView cookingOrders;
    private CardView forwardedListBtn;

    private boolean isNewClicked = false, isCookingClicked = true, isCompleteClicked = false,
            isCancelClicked = false;

    public static boolean ready_btn = false, cancel_btn = false;

    private TextView wifi_signal, online_status;
    private MenuItem nav_camara, menuItem;

    private SharedPreferences sharedpreferences;
    //    public static final String mypreference = "mypref";
//    public static final String Name = "nameKey";
    public static final String printSizeSavedKey = "printSizeKey";

    private ProgressBar progressLayout;
    private Set<String> dataQueue = new HashSet<>();
    private ArrayList<String> newOrdersIdsList = new ArrayList<>();

    private OrderAdapter orderAdapter;
    private List<Order> orderList = new ArrayList<>();

    private RecyclerView recyclerView;
    private String foodTruckId;

    private ForwardedOrderListDialog forwardedOrderDialog;
    private FoodcitiAlertDialog internetAlertDialog;
    //    private Timer timer;
    private boolean responded = false;
    //    private int internetLostCount = 0;
//    private boolean isPaused = false;
    private TextView statusTxt;
//    private ValueEventListener valueEventListener = null;
//    private DatabaseReference mFirebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resturant_main_new_pro);
//        Fabric.with(this, new Crashlytics());
        initializeViews();

//        if (sharedpreferences.contains(Name)) {
//            nav_camara.setTitle("Print Setting ( " + sharedpreferences.getString(Name, "") + " )");
//        }

        if (TextUtils.isEmpty(foodTruckId)) {
            getFoodTruckConnected();
        }

        orderAdapter = new OrderAdapter(this, orderList, this);
        recyclerView.setAdapter(orderAdapter);
        cookingOrders.performClick();
//        firebaseListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(ResturantMainActivityNewPro.this).registerReceiver(receiver,
                new IntentFilter("fireDB"));
//        startService(new Intent(this,FirebaseBackgroundService.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(ResturantMainActivityNewPro.this).unregisterReceiver(receiver);
    }

    // Method to initialize views required for user interaction
    private void initializeViews() {
        context = this;
        foodTruckId = SessionManager.get(this).getFoodTruckId();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        wifi_signal = findViewById(R.id.wifi_signal);
        online_status = findViewById(R.id.online_status);
        statusTxt = findViewById(R.id.foodcitilable);
        progressLayout = findViewById(R.id.progressLayout);
        cookingOrders = findViewById(R.id.cookingOrderss);
        recyclerView = findViewById(R.id.recycler_view);
        forwardedListBtn = findViewById(R.id.forwarded_order_btn);

        // find MenuItem you want to change
        nav_camara = navigationView.getMenu().findItem(R.id.nav_print_setting);

        // set new title to the MenuItem
        sharedpreferences = getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);


        // setting up click listeners for the required views
        cookingOrders.setOnClickListener(this);
        forwardedListBtn.setOnClickListener(this);
        findViewById(R.id.navigation_icon).setOnClickListener(this);
        findViewById(R.id.allOrders).setOnClickListener(this);
        findViewById(R.id.pendingOrders).setOnClickListener(this);
        findViewById(R.id.completeOrders).setOnClickListener(this);
        findViewById(R.id.cancelOrderss).setOnClickListener(this);
        findViewById(R.id.forward_icon).setOnClickListener(this);
        findViewById(R.id.backBtn).setOnClickListener(this);

    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private void getFoodTruckConnected() {
        if (InternetConnection.checkConnection(context)) {
            // showProgressBar();
            Observable<GenericResponse<String>> results = RetroClient.getApiService()
                    .getConnectedFoodTruckId(SessionManager.get(context).getUserId());
            new CompositeDisposable().add(results.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GenericResponse<String>>() {
                        @Override
                        public void accept(GenericResponse<String> response) throws Exception {
                            //  hideProgressBar();
                            Log.e("FOOD_ID", "" + response.getStatus() + ", " + response.getData());
                            if (response.getStatus().equals(Consts.IS_SUCCESS)) {
                                foodTruckId = response.getData();
                                SessionManager.get(context).setFoodTruckId(response.getData());
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {

                        }
                    }));
        } else {
            Toast.makeText(context, R.string.string_internet_connection_warning, Toast.LENGTH_LONG).show();
        }
    }

//    private void firebaseListener() {
//        //isNetworkAvailable();
//        //get reference to the orders node
//        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
//        valueEventListener = new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                Log.e("ORDER_DATA_SNAP", "" + dataSnapshot);
//                for (DataSnapshot child : dataSnapshot.getChildren()) {
//                    final String orderkey = child.getKey();
//                    String order_status = "";
//                    try {
//                        HashMap<String, String> data = (HashMap<String, String>) child.getValue();
//                        order_status = data.get("order_status");
//                        Log.e("Ravi-ORDER_KEY", "" + orderkey + ", " + order_status);
//                        if(order_status.equalsIgnoreCase("0")) {
//                            Log.e("Asad--",  order_status);
//                            dataQueue.add(orderkey);
//                            sendDataKey(orderkey);
//                        }
//                    } catch (Exception e) {
//                        Log.e("FDB_EXP", "" + e.toString());
//                    }
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d(TAG, "onCancelled: new ");
//            }
//        };
//
//        mFirebaseDatabase.child(SessionManager.get(this).getFoodTruckId()).addValueEventListener(valueEventListener);
//    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive( Context context, Intent intent ) {
            String key = intent.getStringExtra("dataKey");
            getStatusOrderList(key,0);
        }
    };

    private void isFowardedOrderEmpty() {
        Log.e("IS_FORWARDED", "" + foodTruckId);
        if (foodTruckId.equalsIgnoreCase("5afdab5cc887646c028f5b57")) {
            getForwardOrdersList("5b066eab600aff6e20406c6c");
        } else if (foodTruckId.equalsIgnoreCase("5b066eab600aff6e20406c6c")) {
            getForwardOrdersList("5afdab5cc887646c028f5b57");
        }
    }


    private void getStatusOrderList(final String orderId, int orderStatus) {
        if (InternetConnection.checkConnection(context)) {
            Observable<GenericResponse<List<Order>>> results = RetroClient.getApiService()
                    .getStatusOrderList(SessionManager.get(context).getFoodTruckId(), orderStatus);
            new CompositeDisposable().add(results.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GenericResponse<List<Order>>>() {
                        @Override
                        public void accept(GenericResponse<List<Order>> response) throws Exception {
                            if (response.getStatus().equals(Consts.IS_SUCCESS)) {
                                List<Order> orderList = response.getData();
                                Log.e("STATUS_3", "" + orderList.size() + ", " + newOrdersIdsList.size());
                                for (Order order : orderList) {
                                    Log.e("STATUS_4", "" + order.getOrderId());
                                    if (order.getOrderId().equalsIgnoreCase(orderId)) {
                                        showOrderInfo(order);
                                        newOrdersIdsList.add(orderId);
//                                        if (newOrdersIdsList.size() == 0) {
//                                        } else {
//                                            newOrdersIdsList.add(orderId);
//                                        }
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
            Toast.makeText(context, R.string.string_internet_connection_warning, Toast.LENGTH_LONG).show();

        }
    }


    private void getStatusOrderList(final String orderId) {
        int status=Integer.parseInt(orderId);
        if (InternetConnection.checkConnection(context)) {
            Observable<GenericResponse<List<Order>>> results = RetroClient.getApiService()
                    .getStatusOrderList(SessionManager.get(context).getFoodTruckId(), status);
            new CompositeDisposable().add(results.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GenericResponse<List<Order>>>() {
                        @Override
                        public void accept(GenericResponse<List<Order>> response) throws Exception {
                            if (response.getStatus().equals(Consts.IS_SUCCESS)) {
                                List<Order> orderList = response.getData();
                                Log.e("STATUS_5", "" + orderList.size());
                                for (Order order : orderList) {
                                    Log.e("STATUS_6", "" + order.getOrderId());
                                    if (order.getOrderId().equalsIgnoreCase(orderId)) {
                                        showOrderInfo(order);
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
            Toast.makeText(context, R.string.string_internet_connection_warning, Toast.LENGTH_LONG).show();

        }
    }


    private void showOrderInfo(Order order) {
        // playSoundFile();
        OrderInfo orderInfo = OrderInfo.newInstance(order);
        orderInfo.setItemListener(this);
        orderInfo.setCancelable(false);
        orderInfo.show(getSupportFragmentManager(), null);
    }


    public void getOrdersList(int orderStatus) {
        if (InternetConnection.checkConnection(this)) {
            if (!TextUtils.isEmpty(foodTruckId)) {
                //   emptyLayout.setVisibility(View.INVISIBLE);
                String token = SessionManager.get(this).getUserToken();
                Observable<GenericResponse<List<Order>>> results = RetroClient.getApiService()
                        .getOrdersList(foodTruckId, orderStatus);
                showProgressBar();
                orderList.clear();
                orderAdapter.notifyDataSetChanged();
                new CompositeDisposable().add(results.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<GenericResponse<List<Order>>>() {
                            @Override
                            public void accept(GenericResponse<List<Order>> response) throws Exception {
                                hideProgressBar();
                                orderList.clear();
                                Log.e("RESPONSE", "" + response.getStatus());
                                if (response.getStatus().equals("200")) {
                                    Log.e("COOKING_SIZE", "" + response.getData().size());
                                    if (response.getData().size() > 0) {
                                        orderList.addAll(response.getData());
                                        orderAdapter.notifyDataSetChanged();
                                        //   emptyLayout.setVisibility(View.INVISIBLE);
                                    } else {
                                        //      emptyLayout.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e("NETWORK_ERROR", "" + throwable.toString());
                                hideProgressBar();
                            }
                        }));
            } else {
//            Snackbar.make(view, R.string.string_internet_connection_warning, Snackbar.LENGTH_INDEFINITE).show();

            }
        }

    }


    private void getOrders() {
        if (InternetConnection.checkConnection(context)) {
            if (!TextUtils.isEmpty(SessionManager.get(this).getFoodTruckId())) {
                String token = SessionManager.get(this).getUserToken();

                Log.e("Token", "" + token + "\n" + foodTruckId);
                Observable<GenericResponse<List<Order>>> results = RetroClient.getApiService()
                        .getOrderList(token, foodTruckId);
                showProgressBar();
                orderList.clear();
                orderAdapter.notifyDataSetChanged();
                new CompositeDisposable().add(results.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<GenericResponse<List<Order>>>() {
                            @Override
                            public void accept(GenericResponse<List<Order>> response) throws Exception {
                                Log.e("ORDERS_ALL", "" + response);
                                hideProgressBar();
                                if (response.getData().size() > 0) {
                                    orderList.addAll(response.getData());
                                    orderAdapter.notifyDataSetChanged();
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e("ORDERS_ALL_EXCEPTION", "" + throwable.toString());
                                hideProgressBar();
                            }
                        }));
            }
        }
    }

    private void showProgressBar() {
        progressLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    private void hideProgressBar() {
        progressLayout.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }


    @Override
    public void changeOrderStatus(final String orderId, final int orderStatus) {
        if (orderStatus == 3) {
            if (!isCancelClicked) {
                showConfirmationDialog(orderId, orderStatus);
            }
        } else {
//            hitChangeOrderStatusApi(orderId, orderStatus);
        }
    }


    private void hitChangeOrderStatusApi(final String orderId, final int orderStatus) {
        if (isCookingClicked || isCancelClicked || isCompleteClicked) {
            orderAdapter.removeOrder(orderId, orderStatus - 1);
        } else {
            orderAdapter.updateOrder(orderId, orderStatus);
        }

        String token = SessionManager.get(this).getUserToken();

        if (InternetConnection.checkConnection(this)) {
            Observable<GenericResponse<Order>> results = RetroClient.getApiService()
                    .updateOrder("Bearer " + token, orderId, orderStatus);
            if (orderStatus == 1) {
                showProgressBar();
            }
            new CompositeDisposable().add(results.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GenericResponse<Order>>() {
                        @Override
                        public void accept(GenericResponse<Order> response) throws Exception {
                            if (orderStatus == 1) {
                                hideProgressBar();
                                if (response.getStatus().equals(Consts.IS_SUCCESS)) {
                                    orderAdapter.updateOrder(orderId, orderStatus);
                                }
                            }
                            if (!response.getStatus().equals(Consts.IS_SUCCESS)) {
                                if (!isNewClicked && !isCookingClicked && !isCancelClicked && !isCompleteClicked) {
                                    orderAdapter.updateOrder(orderId, orderStatus - 1);
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            orderAdapter.updateOrder(orderId, orderStatus - 1);
                            if (orderStatus == 1) {
                                hideProgressBar();
                            }
                        }
                    }));
        } else {
            Toast.makeText(this, R.string.string_internet_connection_warning, Toast.LENGTH_LONG).show();

        }
    }


    @Override
    public void cancelOrder(String orderId) {
        orderAdapter.updateOrder(orderId, 3);
        if (InternetConnection.checkConnection(this)) {
            String token = SessionManager.get(this).getUserToken();
            Observable<GenericResponse> results = RetroClient.getApiService()
                    .cancelOrder("Bearer " + token, orderId);
            new CompositeDisposable().add(results.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GenericResponse>() {
                        @Override
                        public void accept(GenericResponse response) throws Exception {

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    }));
        }
    }


    @Override
    public void itemClick(Order order) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Consts.ORDER_INFO, order);
        bundle.putBoolean("dialogstatus", true);
        bundle.putString("IS_FROM", "MAIN");

        OrderInfo orderInfo = OrderInfo.newInstance(bundle);
        orderInfo.setItemListener(this);
        orderInfo.show(getSupportFragmentManager(), OrderInfo.TAG);
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
        newOrdersIdsList.remove(orderId);
        cookingOrders.performClick();
//        getNextOrder();
    }

    @Override
    public void onOrderForward(String orderId) {
        dataQueue.remove(orderId);
        cookingOrders.performClick();

        isFowardedOrderEmpty();
    }

    @Override
    public void onOrderCancel(String orderId) {
        newOrdersIdsList.remove(orderId);
//        getNextOrder();
    }

    private void getNextOrder() {
        if (newOrdersIdsList.size() > 0) {
            getStatusOrderList(newOrdersIdsList.get(0));
        }
    }


//    @Override
//    protected void onPostResume() {
//        super.onPostResume();
//        isPaused = false;
//        Log.e("ON_RESUME", "activity resumed");
//
////        updateInternetStatus(networkHandler);
////        firebaseListener();
//    }


//    @Override
//    protected void onPause() {
//        Log.e("ON_PAUSE", "activity paused");
//        isPaused = true;
////        stopInternetCheckThread();
////        if(valueEventListener!=null){
////            mFirebaseDatabase.removeEventListener(valueEventListener);
////        }
//        super.onPause();
//        LocalBroadcastManager.getInstance(ResturantMainActivityNewPro.this).unregisterReceiver(receiver);
//    }

    private void sendDataKey(String key) {
        Intent intent = new Intent("fireDB");
        intent.putExtra("dataKey", key);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }



    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action
//            startActivity(new Intent(this, MenuActivity.class));
        } else if (id == R.id.nav_profile) {

//            startActivity(new Intent(context, ProfileActivity.class));
        } else if (id == R.id.nav_offline) {
//                showDialog("");
//            startActivity(new Intent(context, ProfileActivity.class));
        } else if (id == R.id.nav_print_setting) {

            LayoutInflater layoutInflater = getLayoutInflater();
            final View progressView = layoutInflater.inflate(R.layout.dialoge_no_printing
                    , null);

            final android.app.AlertDialog ad = new android.app.AlertDialog.Builder(context)
                    .setView(progressView)
                    .create();
            ad.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            ad.show();
            ad.setCancelable(true);

//            final TextView value = progressView.findViewById(R.id.no_value);
            String printSizeValue = "";

            progressView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ad.dismiss();
                }
            });

//            if (sharedpreferences.contains(Name)) {
//                value.setText(sharedpreferences.getString(Name, ""));
//            }
            if (sharedpreferences.contains(printSizeSavedKey)) {
                printSizeValue = sharedpreferences.getString(printSizeSavedKey, "");
            }

            // get selected radio button from radioGroup
            final RadioGroup rgPrintTextSize = progressView.findViewById(R.id.print_size_rg);
            RadioButton regularTxt = progressView.findViewById(R.id.regular_size);
            RadioButton mediumTxt = progressView.findViewById(R.id.medium_size);
            RadioButton largeTxt = progressView.findViewById(R.id.large_size);

            if (printSizeValue.equalsIgnoreCase(getString(R.string.regular))) {
                regularTxt.setChecked(true);
            }
            if (printSizeValue.equalsIgnoreCase(getString(R.string.medium))) {
                mediumTxt.setChecked(true);
            }
            if (printSizeValue.equalsIgnoreCase(getString(R.string.large))) {
                largeTxt.setChecked(true);
            }

            progressView.findViewById(R.id.submiit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    View focusView;
//                    String Value = value.getText().toString();
                    // find the radiobutton by returned id
                    int selectedId = rgPrintTextSize.getCheckedRadioButtonId();
                    RadioButton selectedRadio = progressView.findViewById(selectedId);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
//                        editor.putString(Name, Value);
                    editor.putString(printSizeSavedKey, selectedRadio.getText().toString());

                    nav_camara.setTitle("Print Setting ( " + 1 + " )");
                    editor.apply();
                    ad.dismiss();
//                    if (value.getText().toString().equals("")) {
//
//                        focusView = value;
//                        focusView.requestFocus();
//                        value.setError("Please Enter Valid Value");
//                    } else {
//                        SharedPreferences.Editor editor = sharedpreferences.edit();
////                        editor.putString(Name, Value);
//                        editor.putString(printSizeSavedKey, selectedRadio.getText().toString());
//
//                        nav_camara.setTitle("Print Setting ( " + Value + " )");
//                        editor.apply();
//                        ad.dismiss();
//                    }

                }
            });


        } else if (id == R.id.nav_logout) {

            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to logout?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SessionManager.clearpreferences(context);
                            finish();
//                            Intent intent = new Intent(context, LogInActivity.class);
//                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();

        } else if (id == R.id.end_report) {
            Intent intent = new Intent(context, OrderTotalActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(context, SettingActivity.class);
            startActivity(intent);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.navigation_icon:
                drawer.openDrawer(GravityCompat.START);
                break;

            case R.id.allOrders:
                isNewClicked = false;
                isCookingClicked = false;
                isCompleteClicked = false;
                isCancelClicked = false;
                getOrders();
                break;

            case R.id.cookingOrderss:
                isNewClicked = false;
                isCookingClicked = true;
                isCompleteClicked = false;
                isCancelClicked = false;
                getOrdersList(1);
                break;

            case R.id.pendingOrders:
                isNewClicked = false;
                isCookingClicked = true;
                isCompleteClicked = false;
                isCancelClicked = false;
                getOrdersList(0);
                break;

            case R.id.completeOrders:
                isNewClicked = false;
                isCookingClicked = false;
                isCompleteClicked = true;
                isCancelClicked = false;
                ready_btn = true;
                getOrdersList(2);
                break;

            case R.id.cancelOrderss:
                isNewClicked = false;
                isCookingClicked = false;
                isCompleteClicked = false;
                isCancelClicked = true;
                cancel_btn = true;
                getOrdersList(3);
                break;
            case R.id.backBtn:
                onBackPressed();
                break;
            case R.id.forward_icon:
            case R.id.forwarded_order_btn:
                Bundle args = new Bundle();
                if (foodTruckId.equalsIgnoreCase("5afdab5cc887646c028f5b57")) {
                    args.putString("FORWARDED_RESTAURANT_ID", "5b066eab600aff6e20406c6c");
                } else if (foodTruckId.equalsIgnoreCase("5b066eab600aff6e20406c6c")) {
                    args.putString("FORWARDED_RESTAURANT_ID", "5afdab5cc887646c028f5b57");
                }
                forwardedOrderDialog = ForwardedOrderListDialog.getInstance(args);
                forwardedOrderDialog.show(getSupportFragmentManager(), ForwardedOrderListDialog.TAG);
                break;
        }
    }


    public void getForwardOrdersList(String restaurantId) {
        Log.e("IS_FORWARDED_RES_ID", "" + restaurantId);
        boolean isNetConnected = InternetConnection.checkConnection(context);
        if (isNetConnected) {
            Observable<GenericResponse<List<Order>>> results = RetroClient.getApiService()
                    .getForwardedOrderStatus(restaurantId);
            new CompositeDisposable().add(results.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GenericResponse<List<Order>>>() {
                        @Override
                        public void accept(GenericResponse<List<Order>> response) throws Exception {
                            Log.e("RESPONSE", "" + response.getStatus());
                            if (response.getStatus().equals("200")) {
                                Log.e("COOKING_SIZE", "" + response.getData().size());
                                if (response.getData() != null && response.getData().size() > 0) {
                                    forwardedListBtn.setVisibility(View.VISIBLE);
                                } else {
                                    forwardedListBtn.setVisibility(View.GONE);
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.e("NETWORK_ERROR", "" + throwable.toString());
                        }
                    }));
        }
    }


    public boolean isOnline() {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);
            sock.connect(sockaddr, timeoutMs);
            sock.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }


//    private void updateInternetStatus(final Handler handler) {
//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//
//            @Override
//            public void run() {
//                try {
//                    responded = isOnline();
//                }catch (Exception e){
//                    Log.d("NETWORK_EXP",""+e.toString());
//                }
//                finally {
//                    if(responded){
//                        internetLostCount = 0;
//                        handler.sendEmptyMessage(1);
//                    }else {
//                        internetLostCount++;
//                        handler.sendEmptyMessage(0);
//                    }
//                }
//
//            }
//
//        },0,1000);//Update text every second
//    }


    private Handler networkHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what != 1) { // code if not connected
                online_status.setBackground(getResources().getDrawable(R.drawable.red_offline));
                wifi_signal.setBackground(getResources().getDrawable(R.drawable.no_wifi));

//                if(internetLostCount == 60){
//                    if(!isPaused) {
//                        showInternetAlertDialog();
//                    }
//                }
            } else { // code if connected
                online_status.setBackground(getResources().getDrawable(R.drawable.green_online));
                wifi_signal.setBackground(getResources().getDrawable(R.drawable.wifi_connected));

//                dismissInternetAlertDialog();
            }
        }
    };

    private void stopInternetCheckThread() {
//        if(timer != null){
//            timer.cancel();
//            timer = null;
//        }
    }


    private void showInternetAlertDialog() {
        Bundle args = new Bundle();
        args.putString("title", "No Internet Connection!");
        args.putString("message", "Please check your internet connection.");

        if (internetAlertDialog == null) {
            internetAlertDialog = FoodcitiAlertDialog.getInstance(args);
            internetAlertDialog.setListener(new FoodcitiAlertDialog.AlertDialogListener() {
                @Override
                public void onOkClicked() {
                    if (responded) {
                        dismissInternetAlertDialog();
//                        internetLostCount = 0;
                        internetAlertDialog = null;
                    }
                    /*else {
                        startActivity(new Intent(
                                Settings.ACTION_SETTINGS));
                    }*/
                }
            });
            if (getSupportFragmentManager() != null) {
                internetAlertDialog.show(getSupportFragmentManager(), FoodcitiAlertDialog.TAG);
            }

        }
    }

    private void dismissInternetAlertDialog() {
        if (internetAlertDialog != null) {
            internetAlertDialog.dismissAllowingStateLoss();
            internetAlertDialog = null;
        }
    }

    private void showConfirmationDialog(final String orderId, final int orderStatus) {
        Bundle confirmargs = new Bundle();
        confirmargs.putString("title", "Are You Sure?");
        confirmargs.putString("message", "Do you want to cancel this order?");

        final ConfirmationDialog confirmationDialog = ConfirmationDialog.getInstance(confirmargs);
        confirmationDialog.setListener(new ConfirmationDialog.ConfirmationDialogListener() {
            @Override
            public void onOkClicked() {
                hitChangeOrderStatusApi(orderId, orderStatus);
            }

            @Override
            public void onNoClicked() {
                orderAdapter.notifyDataSetChanged();
            }
        });
        confirmationDialog.show(getSupportFragmentManager(), ConfirmationDialog.TAG);
    }


    private void changeRestaurantStatus(final String status) {
        showProgressBar();
        if (InternetConnection.checkConnection(context)) {
//            Log.e("LOGIN_ID",""+SessionManager.get(context).getUserId());
            Observable<GenericResponse<String>> results = RetroClient.getApiService()
                    .openCloseRestaurent(SessionManager.get(context).getFoodTruckId(), status);
            new CompositeDisposable().add(results.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GenericResponse<String>>() {
                        @Override
                        public void accept(GenericResponse<String> response) {
                            hideProgressBar();
                            Log.e("Status Result", "" + response.getStatus());
                            if (response.getStatus().equals(Consts.IS_SUCCESS)) {
                                //Launch main activity
                                if (menuItem != null) {
                                    if (status.equalsIgnoreCase("1")) {
                                        statusTxt.setText("Foodciti is now Online");
                                        menuItem.setTitle("Go offline");
                                    } else {
                                        statusTxt.setText("Foodciti is now Offline");
                                        menuItem.setTitle("Go online");
                                    }
                                }
                                SessionManager.get(context).setRestaurantStatus(status);
                                Toast.makeText(context, "" + response.getMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, "" + response.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            hideProgressBar();
                        }
                    }));
        } else {
            Toast.makeText(context, R.string.string_internet_connection_warning, Toast.LENGTH_LONG).show();

        }
    }


    public void showDialog(String msg) {
        final AlertDialog.Builder dialog1 = new AlertDialog.Builder(ResturantMainActivityNewPro.this);
        dialog1.setCancelable(false);
        dialog1.setCustomTitle(getLayoutInflater().inflate(R.layout.switch_button_dialoge, null));
        dialog1.create().show();
//        new AlertDialog.Builder(this)
//                .setMessage("You are offline")
//                .setCancelable(false)
//                .setPositiveButton("Go Online", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        changeRestaurantStatus("1");
//                    }
//                })
//                .setNegativeButton("Go Offline", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        changeRestaurantStatus("2");
//                    }
//                })
//                .show();
    }
}
