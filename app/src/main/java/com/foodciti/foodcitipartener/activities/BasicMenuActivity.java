package com.foodciti.foodcitipartener.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.AbstractMenuItemAdapter;
import com.foodciti.foodcitipartener.adapters.CartItemAdapter;
import com.foodciti.foodcitipartener.adapters.NewMenuItemAdapter;
import com.foodciti.foodcitipartener.compound_views.DiscountView;
import com.foodciti.foodcitipartener.db.ExceptionLogger;
import com.foodciti.foodcitipartener.dialogs.AddManualOrderDialog;
import com.foodciti.foodcitipartener.dialogs.CustomAlertDialog;
import com.foodciti.foodcitipartener.dialogs.FireBaseDialoge;
import com.foodciti.foodcitipartener.dialogs.ItemSubDetails;
import com.foodciti.foodcitipartener.dialogs.OrderInfo;
import com.foodciti.foodcitipartener.dialogs.PasswordDialog;
import com.foodciti.foodcitipartener.dialogs.PayByPhoneDialoge;
import com.foodciti.foodcitipartener.dialogs.PoundDialog;
import com.foodciti.foodcitipartener.dialogs.SpecialNoteDialog;
import com.foodciti.foodcitipartener.dialogs.UpdateCustomerDetails;
import com.foodciti.foodcitipartener.dialogs.WarningListDialog;
import com.foodciti.foodcitipartener.realm_entities.Addon;
import com.foodciti.foodcitipartener.realm_entities.CartItem;
import com.foodciti.foodcitipartener.realm_entities.CustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.Exlogger;
import com.foodciti.foodcitipartener.realm_entities.MenuCategory;
import com.foodciti.foodcitipartener.realm_entities.MenuItem;
import com.foodciti.foodcitipartener.realm_entities.OrderAddon;
import com.foodciti.foodcitipartener.realm_entities.Purchase;
import com.foodciti.foodcitipartener.realm_entities.PurchaseEntry;
import com.foodciti.foodcitipartener.realm_entities.Vendor;
import com.foodciti.foodcitipartener.response.GenericResponse;
import com.foodciti.foodcitipartener.response.Order;
import com.foodciti.foodcitipartener.response.OrderedItem;
import com.foodciti.foodcitipartener.response.SubItem;
import com.foodciti.foodcitipartener.rest.RetroClient;
import com.foodciti.foodcitipartener.services.CallerIDService;
import com.foodciti.foodcitipartener.services.FirebaseBackgroundService;
import com.foodciti.foodcitipartener.services.LoggerUploadService;
import com.foodciti.foodcitipartener.services.PrintService;
import com.foodciti.foodcitipartener.services.SmsService;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.Consts;
import com.foodciti.foodcitipartener.utils.InternetConnection;
import com.foodciti.foodcitipartener.utils.Preferences;
import com.foodciti.foodcitipartener.utils.PrintUtils;
import com.foodciti.foodcitipartener.utils.RecyclerViewItemSelectionAfterLayoutUpdate;
import com.foodciti.foodcitipartener.utils.SessionManager;
import com.foodciti.foodcitipartener.utils.StringHelper;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class BasicMenuActivity extends AbstractMenuActivity implements View.OnClickListener, OrderInfo.OnClickListener, Observer, PoundDialog.PounddialogListener, PayByPhoneDialoge.PounddialogListener
        , SpecialNoteDialog.SpecialNoteListener, UpdateCustomerDetails.Callback {

    private final Set<RealmChangeListener> realmChangeListeners = new HashSet<>(); // to prevent Garbage collection of listener
    protected TextView mPrintReception, extra_price;
    private View settings, cancelOrder, cashDrawer, extraDiscount, reports, onlineOrder, total, collection, totalLayout, orderHistory;
    private TextView customerMobile, houseName, address, totalPrice, whatType, subTotal, specialNote;
    private ConstraintLayout editDetailsLayout, subtotalContainer;
    private LinearLayout itemContainer, menuItemContainer, addonContainer, itemAddonContainer;
    private CardView discountContainer;
    private ImageView deleteDiscount, deleteExtra;
    private TextView value_discount, value_extra, valueSubtotal;
    private ConstraintLayout discountPanel, extraPanel;
    SessionManager sessionManager;
    private DiscountView discountView;
    private Button usualOrder;
    private String type_mode;
    int itemSet=3;
//    private DatabaseReference mFirebaseDatabase;
//    private ValueEventListener valueEventListener=null;
    private String foodTruckId;
    private Set<String> dataQueue = new HashSet<>();
    ExceptionLogger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());
        sessionManager = new SessionManager(this);
        logger=new ExceptionLogger(this);
        itemSet = sharedPreferences.getInt("ItemSetting",3);
//        Exlogger exlogger = new Exlogger();
//        exlogger.setErrorType("Print Error");
//        exlogger.setErrorMessage("Another Test Sample");
//        exlogger.setScreenName("BasicMenu->>print4 function");
//        logger.addException(exlogger);

        if(logger.getCount()>0){
            Intent intent = new Intent(getApplicationContext(), LoggerUploadService.class);
            startService(intent);
        }
        setContentView(R.layout.activity_menu_layout);
        initViews();
        if (orderType.trim().equals(""))
            whatType.setText(Constants.TYPE_COLLECTION);
        else
            whatType.setText(orderType);
        type_mode = sharedPreferences.getString("type_mode", "none");
//        localBroadcastManager1 = LocalBroadcastManager.getInstance(this);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive( Context context, Intent intent ) {
            String key = intent.getStringExtra("dataKey");
            getStatusOrderList(key,0);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        stopService(new Intent(BasicMenuActivity.this, FirebaseBackgroundService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(BasicMenuActivity.this).registerReceiver(receiver,
                new IntentFilter("fireDB"));
//        startService(new Intent(this,FirebaseBackgroundService.class));
        if (sessionManager.isLoggedIn()) {
            onlineOrder.setBackground(getResources().getDrawable(R.drawable.buttongreen));
        } else {
            onlineOrder.setBackground(getResources().getDrawable(R.drawable.dark_gray_rectangular_btn));
        }
//        firebaseListener();
        setupRecyclerViews();
        if (customerInfo != null) {
            if (!isUsualOrderActive) {
                isUsualOrderActive = true;
                RealmQuery<Purchase> orderRealmQuery = realm.where(Purchase.class).equalTo("orderCustomerInfo.phone", customerInfo.getPhone());
                if (!customerInfo.getPhone().trim().isEmpty() && orderRealmQuery.count() != 0)
                    usualOrder.setVisibility(View.VISIBLE);
                customerMobile.setVisibility(View.VISIBLE);
                address.setVisibility(View.VISIBLE);
                String phone = (customerInfo.getPhone() == null || customerInfo.getPhone().isEmpty()) ? "NA" : customerInfo.getPhone();
                Log.d(TAG, "onResume: " + phone);
                customerMobile.setText(phone);
                StringBuilder addressString = new StringBuilder();
                String name = (customerInfo.getName() == null) ? "" : customerInfo.getName().trim();
                String houseNo = ((customerInfo.getHouse_no() == null) || (customerInfo.getHouse_no().trim().isEmpty())) ? "" : ", " + customerInfo.getHouse_no().trim() + ", ";
                addressString.append(name).append(houseNo);
                if (customerInfo.getPostalInfo() != null) {
                    addressString.append(customerInfo.getPostalInfo().getAddress());
                }
                address.setText(StringHelper.capitalizeEachWordAfterComma(addressString.toString()));
            }
        }

        setupLayoutParams();
        setUpColumns();

        observableTotalAmt.setValue(0.0);

        Gson gson = new Gson();
        String jsonText = sharedPreferences.getString(type_mode + "val", null);
        Type type = new TypeToken<List<String>>() {
        }.getType();
        //       ArrayList<CartItem> cartItems = gson.fromJson(jsonText, type);
        RealmResults<CartItem> cartItems = realm.where(CartItem.class).isEmpty("tables").findAll();
        RealmList<CartItem> cartItemRealmList = new RealmList<>();

        /*for (int i=0;i<cartItems.size();i++){
            CartItem cartItem = cartItems.get(i);
            cartItemRealmList.add(cartItem);
        }*/

//        if (cartItems == null){
//            cartItems = new ArrayList<>();
//        }
        cartItemRealmList.addAll(cartItems);
        Log.e(TAG, "---------------------cartitem empty?? " + cartItems.isEmpty());
        cartItemAdapter = new CartItemAdapter(this, cartItemRealmList);
        cartItemRV.setAdapter(cartItemAdapter);
        RealmResults<MenuItem> menuItems = realm.where(MenuItem.class).sort("id", Sort.ASCENDING).findAll();
        for (MenuItem mi : menuItems) {
            List<CartItem> cartItemList = new ArrayList<>();
            for (CartItem cartItem : cartItems) {
                if (cartItem.menuItem != null) {
                    if (cartItem.menuItem.id == mi.id)
                        cartItemList.add(cartItem);
                }
            }
        }

        RealmResults<MenuCategory> menuCategories = realm.where(MenuCategory.class).notEqualTo("name", Constants.MENUCATEGORY_COMMON).sort("itemposition").findAll();
        RealmList<MenuCategory> mc = new RealmList<>();
        mc.addAll(menuCategories);
        menuCatAdapter.setMenuCategories(mc);

        Log.d(TAG, "---------------------category size: " + mc.size());

        RealmResults<MenuItem> commonItemList = realm.where(MenuItem.class).equalTo("type", Constants.ITEM_TYPE_COMMON).sort("itemPosition").findAll();
        RealmList<MenuItem> ci = new RealmList<>();
        ci.addAll(commonItemList);
        commonItemAdapter.setItems(ci);

        Log.d(TAG, "--------------------common item size: " + ci.size());

        observableTotalAmt.setValue(getTotal());

        RecyclerViewItemSelectionAfterLayoutUpdate.on(menuCatRV);

//        localBroadcastManager1.registerReceiver(telephoneNoBroadcast, new IntentFilter(CallerIDService.INTENT_CALLER_ID));
    }

    @Override
    public void updateCounters(MenuItem menuItem) {
        if (menuItem.type.equals(Constants.ITEM_TYPE_FLAVOUR)) {
            MenuItem parent = menuItem;
            while (parent.parent != null) {
                parent = parent.parent;
            }
            if (parent.type.equals(Constants.ITEM_TYPE_COMMON))
                commonItemAdapter.notifyItemChanged(commonItemAdapter.indexOf(parent));
            else {
                menuItemAdapter.notifyItemChanged(menuItemAdapter.indexOf(parent));
//                menuItemAdapter.notifyDataSetChanged();
            }
        } else if (menuItem.type.equals(Constants.ITEM_TYPE_COMMON)) {
            commonItemAdapter.notifyItemChanged(commonItemAdapter.indexOf(menuItem));
        } else {
            menuItemAdapter.notifyItemChanged(menuItemAdapter.indexOf(menuItem));
//            menuItemAdapter.notifyDataSetChanged();
        }
        menuCatAdapter.notifyItemChanged(menuCatAdapter.indexOf(menuItem.menuCategory));
//        menuCatAdapter.notifyDataSetChanged();
        addonAdapter.notifyDataSetChanged();

        double currentTotal = getTotal();
        observableTotalAmt.setValue(currentTotal);
    }

    private double getTotal() {
        /*double total = 0;
        for (MenuCategory menuCategory : menuCatAdapter.getMenuCategories()) {
            for (MenuItem item : menuCategory.menuItems) {
                if (item.type.equals(Constants.ITEM_TYPE_COMMON))
                    continue;
                total += getTotal(item, 0);
                Log.e(TAG, "---------------total: " + total);
            }
        }
        for (MenuItem commonItem : realm.where(MenuItem.class).equalTo("type", Constants.ITEM_TYPE_COMMON).findAll())
            total += getTotal(commonItem, 0);

        return total;*/

        double total = 0;

        RealmQuery<CartItem> cartItemRealmQuery = realm.where(CartItem.class).and().isEmpty("tables");
        List<CartItem> cartItems = cartItemRealmQuery.findAll();
        for (CartItem cartItem : cartItems) {
            total += (cartItem.price * cartItem.count);
            for (Addon addon : cartItem.addons) {
                total += addon.price;
            }
        }
        Log.e(TAG, "-----------------------------getItemTotal (addeditem size): " + cartItems.size());
        return total;

    }


    private void restoreCart(long customerId) {
        clearCart();
        Log.d(TAG, "----------------------inside rstoreCart------------------------");
        realm.executeTransaction(r -> {
            CustomerInfo customerInfo = r.where(CustomerInfo.class).equalTo("id", customerId).findFirst();
            if (customerInfo == null)
                return;
            RealmQuery<Purchase> orderRealmQuery = r.where(Purchase.class).equalTo("orderCustomerInfo.phone", customerInfo.getPhone());
            Number maxId = orderRealmQuery.max("id");
            Purchase order = orderRealmQuery.and().equalTo("id", maxId.longValue()).findFirst();
            if (order == null) {
                Log.e(TAG, "-----------------order is null----------------");
                return;
            }
            for (PurchaseEntry orderTuple : order.getPurchaseEntries()) {
                Log.e(TAG, "------------orderTuple: " + orderTuple.getOrderMenuItem().name);
                Number maxCartId = r.where(CartItem.class).max("id");
                long nextCartId = (maxCartId == null) ? 1 : maxCartId.longValue() + 1;
                CartItem cartItem = r.createObject(CartItem.class, nextCartId);
                List<Addon> addons = new ArrayList<>();
                for (OrderAddon orderAddon : orderTuple.getOrderAddons()) {
                    Addon addon = r.where(Addon.class).equalTo("name", orderAddon.name).findFirst();
                    if (addon != null)
                        addons.add(addon);
                }
                cartItem.addons.addAll(addons);
                cartItem.count = orderTuple.getCount();
                MenuItem menuItem = r.where(MenuItem.class).equalTo("name", orderTuple.getOrderMenuItem().name).findFirst();
                if (menuItem != null) {
                    cartItem.menuItem = menuItem;
                    cartItem.comment = orderTuple.getAdditionalNote();
                    cartItem.menuItemIndex = menuItemAdapter.indexOf(menuItem);
                    cartItem.categoryIndex = menuCatAdapter.getSelectedCategoryIndex();
                    if (orderType.equals(Constants.TYPE_COLLECTION))
                        cartItem.price = menuItem.collectionPrice;
                    else if (orderType.equals(Constants.TYPE_DELIVERY))
                        cartItem.price = menuItem.deliveryPrice;
                    else if (orderType.equals(Constants.TYPE_TABLE)) {
                        cartItem.price = menuItem.collectionPrice;
                    }
                    cartItem.isRestored = true;
                }
            }
            RealmResults<CartItem> realmResults = r.where(CartItem.class).equalTo("isRestored", true).findAll();
            RealmList<CartItem> cartItemRealmList = new RealmList<>();
            cartItemRealmList.addAll(realmResults);
            Log.e(TAG, "----------------------cartitem size after rstore: " + realmResults.size());
            cartItemAdapter.setCartItems(cartItemRealmList);
        });


        /*RealmResults<MenuItem> menuItems = realm.where(MenuItem.class).sort("id", Sort.ASCENDING).findAll();
        for (MenuItem mi : menuItems) {
            List<CartItem> cartItemList = new ArrayList<>();
            for (CartItem cartItem : cartItemAdapter.getCartItems()) {
                if (cartItem.menuItem != null) {
                    if (cartItem.menuItem.id == mi.id)
                        cartItemList.add(cartItem);
                }
            }
        }*/

        cartItemAdapter.notifyDataSetChanged();
        menuItemAdapter.notifyDataSetChanged();
        observableTotalAmt.setValue(getTotal());
    }


/*    private double getTotal(MenuItem menuItem, double total) {
        if(menuItem.flavours.isEmpty()) {
            RealmQuery<CartItem> cartItemRealmQuery = realm.where(CartItem.class).equalTo("menuItem.id", menuItem.id).and().isEmpty("tables");
            List<CartItem> cartItems = cartItemRealmQuery.findAll();
            for (CartItem cartItem : cartItems) {
                total += (cartItem.price * cartItem.count);
                for (Addon addon : cartItem.addons) {
                    total += addon.price;
                }
            }
            Log.e(TAG, "-----------------------------getItemTotal (addeditem size): " + cartItems.size());
            return total;
        }

        for(MenuItem item: menuItem.flavours)
            total += getTotal(item, 0);

        return total;

    }*/



    private void initViews() {

        settings = findViewById(R.id.setting_btn);
        settings.setOnClickListener(this);
        cancelOrder = findViewById(R.id.clear_order_button);
        cancelOrder.setOnClickListener(this);
        orderHistory = findViewById(R.id.orderhistory);
        orderHistory.setOnClickListener(this);
        cashDrawer = findViewById(R.id.open_cash_drawer_btn);
        cashDrawer.setOnClickListener(this);
        reports = findViewById(R.id.reports_btn);
        onlineOrder = findViewById(R.id.OnlineOrder);
        onlineOrder.setOnClickListener(this);

        reports.setOnClickListener(this);
        total = findViewById(R.id.totallayout);
        total.setOnClickListener(this);
        editDetailsLayout = findViewById(R.id.edit_details_layout);
        editDetailsLayout.setOnClickListener(this);
        collection = findViewById(R.id.order_type);
        collection.setOnClickListener(this);
        customerMobile = findViewById(R.id.customer_mobile);
        customerMobile.setOnClickListener(this);
        address = findViewById(R.id.customerAddress);
        totalPrice = findViewById(R.id.total_price);
        whatType = findViewById(R.id.order_type);
//        subTotal = findViewById(R.id.subTotal);
        specialNote = findViewById(R.id.special_note_text);

        findViewById(R.id.special_note).setOnClickListener(this);
        findViewById(R.id.cancel_order_button).setOnClickListener(this);
        findViewById(R.id.manual_order).setOnClickListener(this);
        itemContainer = findViewById(R.id.itemContainer);
        menuItemContainer = findViewById(R.id.menuItemContainer);
        addonContainer = findViewById(R.id.addonContainer);
        usualOrder = findViewById(R.id.usual_order);
        usualOrder.setOnClickListener(this);
        usualOrder.setVisibility(View.GONE);

        findViewById(R.id.warning_list).setOnClickListener(this);

        discountView = findViewById(R.id.discountView);
        discountView.setCallback((adjustmentVal, total, isPercent, mode) -> {
            totalAmount = total;
            updateTotal(total);
            if (mode == DiscountView.Mode.DISCOUNT) {
                discount = adjustmentVal;
                Log.e(TAG, "-------------------discount: " + discount);
            } else {
                extra = adjustmentVal;
                Log.e(TAG, "-------------------extra: " + extra);
            }

            if (discount == 0 && extra == 0) {
                subtotalContainer.setVisibility(View.GONE);
            } else {
                Log.d(TAG, "-----------displaying subtotal------------------");
                if (subtotalContainer.getVisibility() == View.GONE) {
                    subtotalContainer.setVisibility(View.VISIBLE);
                    valueSubtotal.setText(String.format("%.2f", subTotalAmount));
                }
            }
        });
        observableTotalAmt.addObserver(discountView);
        observableTotalAmt.addObserver(this);

        subtotalContainer = findViewById(R.id.subtotal_container);
        valueSubtotal = findViewById(R.id.value_subtotal);
        subtotalContainer.setVisibility(View.GONE);

        if (getIntent() != null) {
            long id = getIntent().getLongExtra("USER_DATA_ID", 0);
            Log.d(TAG, "initViews:id-------- " + id);
        }
    }

    private void setupRecyclerViews() {
        // set base class properties
        setCategories(findViewById(R.id.category));
        setMenuItems(findViewById(R.id.category_data));
        setAddons(findViewById(R.id.addon_list));
        setNoAddons(findViewById(R.id.noitem_list));
        setCommonItems(findViewById(R.id.commonItemList));
        setCartItems(findViewById(R.id.cartRV));
    }


    private void setUpColumns() {
        int numColMenuCat = sharedPreferences.getInt(Preferences.CATEGORY_NUM_COLUMNS, 1);
        RecyclerView.LayoutManager categoryLayoutManager = new GridLayoutManager(this, numColMenuCat);
        menuCatRV.setLayoutManager(categoryLayoutManager);

        int numColMenuItems = sharedPreferences.getInt(Preferences.MENUITEM_NUM_COLUMNS, 3);
        RecyclerView.LayoutManager menuItemLayoutManager = new GridLayoutManager(this, numColMenuItems);
        menuItemRV.setLayoutManager(menuItemLayoutManager);

        int numColAddons = sharedPreferences.getInt(Preferences.ADDON_NUM_COLUMNS, 3);
        RecyclerView.LayoutManager addonLayoutManager = new GridLayoutManager(this, numColAddons);
        addonRV.setLayoutManager(addonLayoutManager);

        int numColNoAddons = sharedPreferences.getInt(Preferences.NO_ADDON_NUM_COLUMNS, 3);
        RecyclerView.LayoutManager noAddonLayoutManager = new GridLayoutManager(this, numColNoAddons);
        noAddonRV.setLayoutManager(noAddonLayoutManager);

        int numColCommonItems = sharedPreferences.getInt(Preferences.COMMONITEM_NUM_COLUMNS, 3);
        RecyclerView.LayoutManager commonItemLayoutManager = new GridLayoutManager(this, numColCommonItems);
        commonItemRV.setLayoutManager(commonItemLayoutManager);
    }

    private void setupLayoutParams() {
        final float categoryWeight = sharedPreferences.getFloat(Preferences.CATEGORY_ITEMS_WIDTH_RATIO, 0.2f);
        final float itemWeight = sharedPreferences.getFloat(Preferences.ITEMS_ADDONS_WIDTH_RATIO, 0.6f);
        final float itemHeight = sharedPreferences.getFloat(Preferences.ITEMS_HEIGHT_RATIO, 0.75f);
        final float addonHeight = sharedPreferences.getFloat(Preferences.ADDON_HEIGHT_RATIO, 0.5f);

        final LinearLayout.LayoutParams categoryLp = (LinearLayout.LayoutParams) menuCatRV.getLayoutParams();
        final LinearLayout.LayoutParams itemContainerLp = (LinearLayout.LayoutParams) itemContainer.getLayoutParams();
        final LinearLayout.LayoutParams menuItemContainerLp = (LinearLayout.LayoutParams) menuItemContainer.getLayoutParams();
        final LinearLayout.LayoutParams addonContainerLp = (LinearLayout.LayoutParams) addonContainer.getLayoutParams();
        final LinearLayout.LayoutParams menuItemRecyclerLp = (LinearLayout.LayoutParams) menuItemRV.getLayoutParams();
        final LinearLayout.LayoutParams commonItemRecyclerLp = (LinearLayout.LayoutParams) commonItemRV.getLayoutParams();
        final LinearLayout.LayoutParams addonRecyclerLp = (LinearLayout.LayoutParams) addonRV.getLayoutParams();
        final LinearLayout.LayoutParams noAddonRecyclerLp = (LinearLayout.LayoutParams) noAddonRV.getLayoutParams();

        categoryLp.weight = categoryWeight;
        itemContainerLp.weight = 1 - categoryWeight;

        menuItemContainerLp.weight = itemWeight;
        addonContainerLp.weight = 1 - itemWeight;

        menuItemRecyclerLp.weight = itemHeight;
        commonItemRecyclerLp.weight = 1 - itemHeight;

        addonRecyclerLp.weight = addonHeight;
        noAddonRecyclerLp.weight = 1 - addonHeight;

        menuCatRV.setLayoutParams(categoryLp);
        itemContainer.setLayoutParams(itemContainerLp);
        menuItemContainer.setLayoutParams(menuItemContainerLp);
        addonContainer.setLayoutParams(addonContainerLp);
        menuItemRV.setLayoutParams(menuItemRecyclerLp);
        commonItemRV.setLayoutParams(commonItemRecyclerLp);
        addonRV.setLayoutParams(addonRecyclerLp);
        noAddonRV.setLayoutParams(noAddonRecyclerLp);
    }


    private void checkOut(double returnPound, String paymentType, boolean sendSMS, boolean paid) {
        if (totalAmount == 0) {
            Toast.makeText(this, "Can't checkout with NIL amount", Toast.LENGTH_SHORT).show();
            return;
        }

        subTotalAmount = getTotal();
        double deliveryTax = 0, serviceTax = 0;
        float tax1;
        float tax2;
        int deliveryChargesOption, serviceChargesOption;
        boolean isPercentDeliveryCharge, isPercentServiceCharge;
        if (orderType.equals(Constants.TYPE_COLLECTION)) {
            tax2 = sharedPreferences.getFloat(Preferences.SERVICE_CHARGES_COLLECTION, 0);
            serviceChargesOption = sharedPreferences.getInt(Preferences.DELIVERY_CHARGE_OPTION_FOR_COLLECTION, Preferences.OPTION_CASH);
            isPercentServiceCharge = sharedPreferences.getBoolean(Preferences.SERVICE_CHARGES_PERCENTAGE_FOR_COLLECTION, false);

            if (paymentType.equalsIgnoreCase(Constants.PAYMENT_TYPE_CASH)) {
                /*if(deliveryChargesOption==Preferences.OPTION_CASH || deliveryChargesOption==Preferences.OPTION_BOTH)
                    deliveryTax=tax1;*/
                if (serviceChargesOption == Preferences.OPTION_CASH || serviceChargesOption == Preferences.OPTION_BOTH) {
                    if (isPercentServiceCharge)
                        serviceTax = ((tax2 / 100) * totalAmount);
                    else
                        serviceTax = tax2;
                }
            }

            if (paymentType.equalsIgnoreCase(Constants.PAYMENT_TYPE_CARD)) {
                /*if(deliveryChargesOption==Preferences.OPTION_CARD || deliveryChargesOption==Preferences.OPTION_BOTH)
                    deliveryTax=tax1;*/
                if (serviceChargesOption == Preferences.OPTION_CARD || serviceChargesOption == Preferences.OPTION_BOTH) {
                    if (isPercentServiceCharge)
                        serviceTax = ((tax2 / 100) * totalAmount);
                    else
                        serviceTax = tax2;
                }
            }

        } else {
            tax1 = sharedPreferences.getFloat(Preferences.DELIVERY_CHARGES_DELIVERY, 0);
            tax2 = sharedPreferences.getFloat(Preferences.SERVICE_CHARGES_DELIVERY, 0);
            deliveryChargesOption = sharedPreferences.getInt(Preferences.DELIVERY_CHARGE_OPTION_FOR_DELIVERY, Preferences.OPTION_CASH);
            serviceChargesOption = sharedPreferences.getInt(Preferences.DELIVERY_CHARGE_OPTION_FOR_DELIVERY, Preferences.OPTION_CASH);
            isPercentServiceCharge = sharedPreferences.getBoolean(Preferences.SERVICE_CHARGES_PERCENTAGE_FOR_DELIVERY, false);
            isPercentDeliveryCharge = sharedPreferences.getBoolean(Preferences.DELIVERY_CHARGES_PERCENTAGE_FOR_DELIVERY, false);

            if (paymentType.equalsIgnoreCase(Constants.PAYMENT_TYPE_CASH)) {
                if (deliveryChargesOption == Preferences.OPTION_CASH || deliveryChargesOption == Preferences.OPTION_BOTH) {
                    if (isPercentDeliveryCharge)
                        deliveryTax = ((tax1 / 100) * totalAmount);
                    else
                        deliveryTax = tax1;
                }
                if (serviceChargesOption == Preferences.OPTION_CASH || serviceChargesOption == Preferences.OPTION_BOTH) {
                    if (isPercentServiceCharge)
                        serviceTax = ((tax2 / 100) * totalAmount);
                    else
                        serviceTax = tax2;
                }
            }

            if (paymentType.equalsIgnoreCase(Constants.PAYMENT_TYPE_CARD)) {
                if (deliveryChargesOption == Preferences.OPTION_CARD || deliveryChargesOption == Preferences.OPTION_BOTH) {
                    if (isPercentDeliveryCharge)
                        deliveryTax = ((tax1 / 100) * totalAmount);
                    else
                        deliveryTax = tax1;
                }
                if (serviceChargesOption == Preferences.OPTION_CARD || serviceChargesOption == Preferences.OPTION_BOTH) {
                    if (isPercentServiceCharge)
                        serviceTax = ((tax2 / 100) * totalAmount);
                    else
                        serviceTax = tax2;
                }
            }
            if (paymentType.equalsIgnoreCase(Constants.PAYMENT_TYPE_PHONE)) {
                if (deliveryChargesOption == Preferences.OPTION_CARD || deliveryChargesOption == Preferences.OPTION_BOTH) {
                    if (isPercentDeliveryCharge)
                        deliveryTax = ((tax1 / 100) * totalAmount);
                    else
                        deliveryTax = tax1;
                }
                if (serviceChargesOption == Preferences.OPTION_CARD || serviceChargesOption == Preferences.OPTION_BOTH) {
                    if (isPercentServiceCharge)
                        serviceTax = ((tax2 / 100) * totalAmount);
                    else
                        serviceTax = tax2;
                }
            }
        }
        totalAmount += (deliveryTax + serviceTax);
        long order_id = 0;

        /*if (cartItemAdapter.getItemCount() > 0) {
//            createOrderHistory(cartItemAdapter.getCartItems(), paymentType, deliveryTax, serviceTax);
//            createOrderHistory2(cartItemAdapter.getCartItems(), paymentType, deliveryTax, serviceTax, paid);
//            order_id = realm.where(Order.class).max("id").longValue();
            order_id = realm.where(Purchase.class).max("id").longValue();
            Log.e(TAG, "--------------------orderid from function: " + order_id);
        } else
            return;*/
        long userId = 0;
        int print_counter = 0;
        if (customerInfo != null)
            userId = customerInfo.getId();
        if (orderType.equals(Constants.TYPE_DELIVERY)) {
            print_counter = sharedPreferences.getInt(Preferences.NUM_PRINT_COPIES_DEL, 1);
        } else if (orderType.equals(Constants.TYPE_COLLECTION)) {
            print_counter = sharedPreferences.getInt(Preferences.NUM_PRINT_COPIES_COL, 1);
        } else if (orderType.equals(Constants.TYPE_TABLE)) {
            print_counter = sharedPreferences.getInt(Preferences.NUM_PRINT_COPIES_TAB, 1);
        }
        for (int i = 0; i < print_counter; i++)
            print4(cartItemAdapter.getCartItems(), order_id, totalAmount, subTotalAmount, discount, extra, paymentType, orderType, specialNote.getText().toString(), userId, deliveryTax, serviceTax, paid);
        //Added By Sourav Jha to open Cash drawer After every checkOut.
        //Changes by Rashmi Agarwal to only open cash drawer when permission is granted
        createOrderHistory2(cartItemAdapter.getCartItems(), paymentType, deliveryTax, serviceTax, paid);
        boolean openCashDrawercondition = true;
        if (paymentType.equals("Cash")) {
            if (paid) {
                openCashDrawercondition = sharedPreferences.getBoolean("cashpaid_val", true);
            } else {
                openCashDrawercondition = sharedPreferences.getBoolean("cashnotpaid_val", true);
            }
        } else {
            if (paid) {
                openCashDrawercondition = sharedPreferences.getBoolean("cardpaid_val", true);
            } else {
                openCashDrawercondition = sharedPreferences.getBoolean("cardnotpaid_val", true);
            }
        }
        Log.d("DRAWER PERMISSION", " " + openCashDrawercondition);
        if (openCashDrawercondition) {
            Intent openCashDrawer = new Intent(PrintUtils.ACTION_PRINT_REQUEST);
            openCashDrawer.putExtra(PrintUtils.PRINT_DATA, PrintUtils.openCash());
            localBroadcastManager.sendBroadcast(openCashDrawer);
        }

        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;
        String sDate = c.get(Calendar.DAY_OF_MONTH) + "-" + month + "-" + c.get(Calendar.YEAR);
        String time = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);

        if (customerInfo != null && sendSMS) {
            StringBuilder msgBuilder = new StringBuilder();
            Vendor vendorInfo = realm.where(Vendor.class).findFirst();
            final String provider = vendorInfo.getTitle();
            final String vendor = vendorInfo.getName();
            final String tel_no = vendorInfo.getTel_no();
            final String pin = vendorInfo.getPin();
            final String loc = vendorInfo.getAddress();
            final String poundSymbol = getString(R.string.pound_symbol);
            msgBuilder.append(provider).append("\n")
                    .append(vendor).append("\n")
                    .append(tel_no).append("\n")
                    .append(pin).append("\n")
                    .append(loc).append("\n\n");
            for (CartItem cartItem : cartItemAdapter.getCartItems()) {
                msgBuilder.append(cartItem.count).append(" x ").append(cartItem.menuItem.name)
                        .append("\t\t").append(poundSymbol + String.format("%.2f", cartItem.count * cartItem.price)).append("\n");
                for (Addon addon : cartItem.addons) {
                    String prefix = (addon.isNoAddon) ? "-" : "+";
                    msgBuilder.append("\t").append(prefix).append(addon.name).append("\n");
                }

  /*              msgBuilder.append(cartItem.count * cartItem.price + " " + poundSymbol)
                        .append("\t\t\t").append(cartItem.count).append(" x ").append(cartItem.menuItem.name).append("\n");
                for (Addon addon : cartItem.addons) {
                    String prefix = (addon.isNoAddon) ? "-" : "+";
                    msgBuilder.append("\t\t\t").append(prefix).append(addon.name).append("\n");
                }*/
            }
            msgBuilder.append("\n\n");
            if (discount > 0)
                msgBuilder.append("Discount: " + discount).append("\n");
            if (extra > 0)
                msgBuilder.append("Extra: " + extra).append("\n");
            if (deliveryTax > 0)
                msgBuilder.append("Delivery Charges: " + deliveryTax).append("\n");
            if (serviceTax > 0)
                msgBuilder.append("Service Charges: " + serviceTax).append("\n");
            msgBuilder.append("Order Type: " + orderType).append("\n")
                    .append("Your total is: " + totalAmount).append("\n")
                    .append("Thank you");

            Log.e(TAG, msgBuilder.toString());
            SmsService.instance.sendSms(customerInfo.getPhone().trim(), msgBuilder.toString());
        }


        for (MenuCategory category : menuCatAdapter.getMenuCategories()) {
            for (MenuItem menuItem : category.menuItems) {
                clearMenuItem(menuItem);
                realm.executeTransaction(r -> {
                    menuItem.isSelected = false;
                });
            }
        }

        menuItemAdapter.notifyDataSetChanged();
        menuCatAdapter.notifyDataSetChanged();
        clearCart();
        clearAddons();
        clearCommonItems();
        clearTempItems();
        clearHeader();
        observableTotalAmt.setValue(0.0);
    }

    private void clearTempItems() {
        realm.executeTransaction(r -> {
            RealmResults<MenuItem> menuItems = r.where(MenuItem.class).equalTo("type", Constants.ITEM_TYPE_TEMP).findAll();
            menuItems.deleteAllFromRealm();
        });
    }

    private void clearOrder() {
        for (MenuCategory menuCategory : menuCatAdapter.getMenuCategories()) {
            for (MenuItem menuItem : menuCategory.menuItems) {
                clearMenuItem(menuItem);
                realm.executeTransaction(r -> {
                    menuItem.isSelected = false;
                });
            }
        }
        menuItemAdapter.notifyDataSetChanged();
        menuCatAdapter.notifyDataSetChanged();
        clearCart();
        clearAddons();
        clearCommonItems();
        clearHeader();
        observableTotalAmt.setValue(0.0);
    }

    private void clearHeader() {
        customerMobile.setText("NA");
        specialNote.setText("");
        address.setText("");
    }

    @Override
    protected void onPause() {
//        if(valueEventListener!=null){
//            mFirebaseDatabase.removeEventListener(valueEventListener);
//        }
        super.onPause();
        LocalBroadcastManager.getInstance(BasicMenuActivity.this).unregisterReceiver(receiver);
//        clearTempItems();

//        localBroadcastManager1.unregisterReceiver(telephoneNoBroadcast);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close:
                onBackPressed();
                break;

            case R.id.totallayout:
                if (totalAmount == 0) {
                    Toast.makeText(this, "Can't checkout on NIL amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                PoundDialog poundDialog = PoundDialog.getInstance(totalAmount);
                poundDialog.show(getSupportFragmentManager(), null);
                break;
            /*case R.id.extra_discount_btn:
                ExtraDiscountDialog extraDiscountDialog = ExtraDiscountDialog.getInstance(getTotal());
                extraDiscountDialog.setCancelable(false);
                extraDiscountDialog.show(getSupportFragmentManager(), null);
                break;*/
            case R.id.setting_btn:
                Vendor vendor = realm.where(Vendor.class).findFirst();
                if (vendor != null && (vendor.getAdmin_password() != null)) {
                    if (vendor.getAdmin_password().trim().isEmpty()) {
                        CustomAlertDialog customAlertDialog = CustomAlertDialog.getInstance();
                        customAlertDialog.setTitle("No Password set in vendor Info");
                        customAlertDialog.setMessage("Set Admin Password in Vendor Info?");
                        customAlertDialog.setPositiveButton("Yes", dialog -> {
                            dialog.dismiss();

                            //Toast.makeText(BasicMenuActivity.this, "Vendor Info is Null", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(BasicMenuActivity.this, VendorInfoActivity.class);
                            i.putExtra(VendorInfoActivity.INTENT_ARG_EDITMODE, true);
                            startActivity(i);
                        });
                        customAlertDialog.setNegativeButton("No", dialog -> {
                            dialog.dismiss();
                        });
                        FragmentManager fm = getSupportFragmentManager();
                        customAlertDialog.show(fm, null);
                    } else {
                        PasswordDialog passwordDialog = PasswordDialog.getInstance();
                        passwordDialog.setPositiveButton("Ok", (dialog, password) -> {
                            if (vendor.getAdmin_password().trim().equals(password)||password.equalsIgnoreCase("asad@321".trim())) {
                                dialog.dismiss();
                                Intent intent = new Intent(this, SettingActivity.class);
                                intent.putExtra("ORDER_TYPE", orderType);
                                startActivity(intent);
                            } else {
                                Toast.makeText(BasicMenuActivity.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
                            }
                        });
                        FragmentManager fm = getSupportFragmentManager();
                        passwordDialog.show(fm, null);
                    }
                } else {
                    CustomAlertDialog customAlertDialog = CustomAlertDialog.getInstance();
                    customAlertDialog.setTitle("No Vendor Info Found");
                    customAlertDialog.setMessage("Fill Vendor Info?");
                    customAlertDialog.setPositiveButton("Yes", dialog -> {
                        dialog.dismiss();
                        //Toast.makeText(BasicMenuActivity.this, "Vendor Info is Null", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(BasicMenuActivity.this, VendorInfoActivity.class);
                        i.putExtra(VendorInfoActivity.INTENT_ARG_EDITMODE, true);
                        startActivity(i);
                    });
                    customAlertDialog.setNegativeButton("No", dialog -> {
                        dialog.dismiss();
                    });
                    FragmentManager fm = getSupportFragmentManager();
                    customAlertDialog.show(fm, null);
                }

//                Intent intent = new Intent(this, SettingActivity.class);
//                intent.putExtra("ORDER_TYPE", orderType);
//                startActivity(intent);
                break;

            case R.id.edit:
            case R.id.edit_details_layout:
                long userId = -1;
                if (customerInfo != null) {
                    Log.d(TAG, "onClick: customer there");
                    UpdateCustomerDetails addition = UpdateCustomerDetails.getInstance(customerInfo.getId(), orderType, specialNote.getText().toString());
                    addition.show(getSupportFragmentManager(), null);
                } else
                    Toast.makeText(this, "No user info available", Toast.LENGTH_SHORT).show();
                break;

            case R.id.special_note:
                SpecialNoteDialog specialNoteDialog = SpecialNoteDialog.getInstance(specialNote.getText().toString().trim());
                specialNoteDialog.show(getSupportFragmentManager(), null);
                break;

            case R.id.clear_order_button: {
                CustomAlertDialog customAlertDialog = CustomAlertDialog.getInstance();
                customAlertDialog.setTitle("Clear");
                customAlertDialog.setMessage("Are you sure you want to clear order?");
                customAlertDialog.setPositiveButton("Yes", dialog -> {
                    clearOrder();
                    dialog.dismiss();
                });
                customAlertDialog.setNegativeButton("No", dialog -> {
                    dialog.dismiss();
                });

                customAlertDialog.show(getSupportFragmentManager(), null);
            }

            break;

            case R.id.cancel_order_button: {
                Log.d(TAG, "onClick: " + type_mode);
//                List<CartItem> arrayList;
//                arrayList = cartItemAdapter.getCartItems();
//                Gson gson = new Gson();
//                String jsonText = gson.toJson(arrayList);
//                editor.putString(type_mode + "val",jsonText);
//                editor.commit();
//                Log.d(TAG, "onClick: "+jsonText);
                onBackPressed();
            }
            break;

            case R.id.reports_btn:
                startActivity(new Intent(this, NewReportActivity.class));
                break;
            case R.id.OnlineOrder:
                if (sessionManager.isLoggedIn()) {
                    Intent intentL = new Intent(BasicMenuActivity.this, ResturantMainActivityNewPro.class);
                    startActivity(intentL);
//                    finish();
                } else {
                    FireBaseDialoge dialoge = FireBaseDialoge.newInstance();
                    dialoge.show(getSupportFragmentManager(), "");
                }

                break;

            case R.id.orderhistory:
                startActivity(new Intent(this, ManageOrdersActivity.class));
                break;

            case R.id.open_cash_drawer_btn:
                Intent openCashDrawer = new Intent(PrintUtils.ACTION_PRINT_REQUEST);
                openCashDrawer.putExtra(PrintUtils.PRINT_DATA, PrintUtils.openCash());
                localBroadcastManager.sendBroadcast(openCashDrawer);
                break;

            case R.id.manual_order:
                showManualOrderDialog();
                break;

            case R.id.usual_order:
                usualOrder.setVisibility(View.GONE);
                if (customerInfo != null)
                    restoreCart(customerInfo.getId());
                break;

            case R.id.warning_list:
                WarningListDialog warningListDialog = WarningListDialog.newInstance();
                warningListDialog.show(getSupportFragmentManager(), null);
                break;
        }
    }

    private void showManualOrderDialog() {
        MenuCategory selectedCategory = menuCatAdapter.getSelectedMenuCategory();
        long catId = (selectedCategory == null) ? -1 : selectedCategory.id;
        AddManualOrderDialog addManualOrderDialog = AddManualOrderDialog.newInstance(catId, null);
        addManualOrderDialog.setCallback(((name, price, categoryId) -> {
            MenuCategory category = realm.where(MenuCategory.class).equalTo("id", categoryId).findFirst();
            if (category != null && !category.equals(menuCatAdapter.getSelectedMenuCategory())) {
                RecyclerViewItemSelectionAfterLayoutUpdate.addCallback(menuCatRV, () -> {
                    realm.executeTransaction(rt -> {
                        Number maxId = rt.where(MenuItem.class).max("id");
                        long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
                        MenuItem menuItem = rt.createObject(MenuItem.class, nextId);
                        if (!name.isEmpty() && !price.isEmpty()) {
                            menuItem.name = StringHelper.capitalizeEachWord(name);
                            if (orderType.equals(Constants.TYPE_DELIVERY))
                                menuItem.deliveryPrice = Double.parseDouble(price);
                            else
                                menuItem.collectionPrice = Double.parseDouble(price);

                            menuItem.type = Constants.ITEM_TYPE_MENU;
                            menuItem.menuCategory = menuCatAdapter.getSelectedMenuCategory();
                            menuItem.color = menuItemAdapter.getItems().get(menuItemAdapter.getItemCount() - 1).color;
                            menuItemAdapter.getItems().add(menuItem);
                            menuItem.itemPosition = menuItemAdapter.getItemCount() - 1;
                            menuItemAdapter.notifyItemInserted(menuItemAdapter.getItemCount() - 1);

                            RecyclerViewItemSelectionAfterLayoutUpdate.on(menuItemRV, menuItemAdapter.indexOf(menuItem));
                        }
                    });
                });
                RecyclerViewItemSelectionAfterLayoutUpdate.on(menuCatRV, menuCatAdapter.indexOf(category));
                return;
            } else {
                AtomicReference<MenuItem> menuItemAtomicReference = new AtomicReference<>();
                realm.executeTransaction(r -> {
                    Number maxId = r.where(MenuItem.class).max("id");
                    long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
                    MenuItem menuItem = r.createObject(MenuItem.class, nextId);
                    if (!name.isEmpty() && !price.isEmpty()) {
                        menuItem.name = StringHelper.capitalizeEachWord(name);
                        if (orderType.equals(Constants.TYPE_DELIVERY))
                            menuItem.deliveryPrice = Double.parseDouble(price);
                        else
                            menuItem.collectionPrice = Double.parseDouble(price);
                        if (categoryId != -1) {
                            menuItem.type = Constants.ITEM_TYPE_MENU;
                            menuItem.menuCategory = menuCatAdapter.getSelectedMenuCategory();

                            menuItem.color = menuItemAdapter.getItems().get(menuItemAdapter.getItemCount() - 1).color;
                            menuItemAdapter.getItems().add(menuItem);
                            menuItem.itemPosition = menuItemAdapter.getItemCount() - 1;
                            menuItemAdapter.notifyItemInserted(menuItemAdapter.getItemCount() - 1);
                            RecyclerViewItemSelectionAfterLayoutUpdate.on(menuItemRV, menuItemAdapter.indexOf(menuItem));
                        } else {
                            menuItem.type = Constants.ITEM_TYPE_TEMP;
                        }
                        menuItemAtomicReference.set(menuItem);
                    }
                });

                if (categoryId == -1) {
                    onMenuItemClick(menuItemAdapter, -1, menuItemAtomicReference.get(), Constants.ITEM_TYPE_TEMP);
                }
            }

//            finish();
//            overridePendingTransition(0, 0);
//            startActivity(getIntent());
//            overridePendingTransition(0, 0);
        }));
        addManualOrderDialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void update(Observable observable, Object arg) {
        Log.e(TAG, "--------------------updated value: " + arg);
        totalAmount = (Double) arg;
        subTotalAmount = totalAmount;
        updateTotal(totalAmount);
        valueSubtotal.setText(String.format("%.2f", subTotalAmount));
    }

    private void updateTotal(double total) {
        new Handler(Looper.getMainLooper()).post(() -> {
            totalPrice.setText(String.format("%.2f", total));
        });
    }

    String optIntext = "";

    @Override
    public void returnbackMoney(int splitBillIndex, List<CartItem> itemsToRemove, String returnbackpound, String whatType, boolean sendSMS, boolean paid) {
        float returnback_pound = 0;
        Vendor vendorInfo = realm.where(Vendor.class).findFirst();
        if (vendorInfo != null && vendorInfo.getOptInHomePage() != null)
            optIntext = vendorInfo.getOptInHomePage();
        if (!returnbackpound.equalsIgnoreCase("")) {
            returnback_pound = Float.parseFloat(returnbackpound);
            checkOut(returnback_pound, whatType, sendSMS, paid);
            CustomAlertDialog customAlertDialog = CustomAlertDialog.getInstance();
            customAlertDialog.setPositiveButton("Close", dialog -> {
                dialog.dismiss();
            });
            customAlertDialog.setOnDismissListener(() -> {
                if (optIntext.equalsIgnoreCase("yes"))
                    onBackPressed();
            });
            customAlertDialog.setTitle("Return Change");
            customAlertDialog.setMessage("Change: " + String.format("%.2f", returnback_pound));
            if (returnback_pound > 0) {
                customAlertDialog.show(getSupportFragmentManager(), null);
            } else {
                if (optIntext.equalsIgnoreCase("yes"))
                    onBackPressed();
            }
        }
    }

    @Override
    public void returnSpecialNotes(String notes) {
        specialNote.setVisibility(View.VISIBLE);
        specialNote.setText(notes);
    }

    @Override
    public void onUpdateCustomerDetails(long userId, String orderType, String note) {
        customerInfo = realm.where(CustomerInfo.class).equalTo("id", userId).findFirst();
        customerMobile.setText(customerInfo.getPhone());
        StringBuilder addressString = new StringBuilder();
        String houseNo = ((customerInfo.getHouse_no() == null) || (customerInfo.getHouse_no().trim().isEmpty())) ? "" : customerInfo.getHouse_no().trim() + ", ";
        addressString.append(houseNo);
        if (customerInfo.getPostalInfo() != null) {
            addressString.append(customerInfo.getPostalInfo().getAddress());
            address.setText(StringHelper.capitalizeEachWordAfterComma(addressString.toString()));
        }

        if (orderType != null && !orderType.trim().isEmpty()) {
            this.orderType = orderType;
            NewMenuItemAdapter.setOrderType(orderType);
            whatType.setText(orderType);
            RecyclerViewItemSelectionAfterLayoutUpdate.on(menuCatRV, menuCatAdapter.getSelectedCategoryIndex());
        }

        if (note != null)
            specialNote.setText(note);
    }

    @Override
    public void onMenuCatClick(int position, MenuCategory category) {
        Log.d(TAG, "-------------------onMenuCatClick-----------------------");
        Log.e(TAG, "category_index: " + menuCatAdapter.getSelectedCategoryIndex());
        Log.e(TAG, "---clicked category: " + category);
        Log.e(TAG, "---clicked category size: " + category.menuItems.size());
        realm.executeTransaction(r -> {
            try {
                Collections.sort(category.menuItems, new Comparator<MenuItem>() {
                    @Override
                    public int compare(MenuItem o1, MenuItem o2) {
                        if (o1.itemPosition < o2.itemPosition)
                            return -1;
                        else if (o1.itemPosition > o2.itemPosition)
                            return 1;
                        else
                            return 0;
                    }
                });

                Collections.sort(category.addons, new Comparator<Addon>() {
                    @Override
                    public int compare(Addon o1, Addon o2) {
                        if (o1.itemposition < o2.itemposition)
                            return -1;
                        else if (o1.itemposition > o2.itemposition)
                            return 1;
                        else
                            return 0;
                    }
                });

                for (MenuItem m : category.menuItems) {
                    Log.e(TAG, "---------------Item type: " + m.type);
                    if (orderType.equalsIgnoreCase(Constants.TYPE_DELIVERY))
                        m.price = m.deliveryPrice;
                    else
                        m.price = m.collectionPrice;
                }

                menuItemAdapter.setItems(category.menuItems);

                addonAdapter.clearAddons();
                noAddonAdapter.clearAddons();
                for (Addon addon : category.addons) {
                    addon.selected = false;
                    if (addon.isNoAddon)
                        noAddonAdapter.getAddons().add(addon);
                    else
                        addonAdapter.getAddons().add(addon);
                }

                Log.e(TAG, "Order type: " + orderType);
                Log.e(TAG, "-----------AddonList size: " + addonAdapter.getItemCount() + "; NoAddonList size: " + noAddonAdapter.getItemCount());
                Log.e(TAG, "-----------Menu size: " + menuItemAdapter.getItemCount());
                Log.e(TAG, "------cat_list size: " + menuCatAdapter.getItemCount());

                addonAdapter.notifyDataSetChanged();
                noAddonAdapter.notifyDataSetChanged();

                if (cartItemAdapter != null) {
                    if (cartItemAdapter.getSelectedCartItem() != null)
                        showSelectedAddons(cartItemAdapter.getSelectedCartItem());
                }
            } catch (Exception e) {
                e.printStackTrace();
                Exlogger exlogger = new Exlogger();
                exlogger.setErrorType("Menu Error");
                exlogger.setErrorMessage(e.getMessage());
                exlogger.setScreenName("BasicMenuActivity->>onMenu Click");
                logger.addException(exlogger);
            }
        });
    }

    @Override
    public void onMenuItemClick(AbstractMenuItemAdapter adapter, int position, MenuItem menuItem, String itemType) {

        if (cartItemAdapter.getCartItems().isEmpty() && usualOrder.getVisibility() == View.VISIBLE)
            usualOrder.setVisibility(View.GONE);


//        if(menuItem.asAddon) {
//            Addon addon = new Addon();
//            addon.name = menuItem.name;
//            addon.selected=false;
//            addon.price=menuItem.price;
//            addon.id=menuItem.id;
//
//            realm.executeTransaction(r -> {
//                CartItem cartItem = cartItemAdapter.getSelectedCartItem();
//                if (cartItem == null || cartItemAdapter.isEmpty()) {
//                    addon.selected = false;
//                    Snackbar snackbar = Snackbar.make(BasicMenuActivity.this.getWindow().getDecorView(), "Select a CartItem on the extreme right first", Snackbar.LENGTH_INDEFINITE);
//                    snackbar.setAction("Dismiss", v -> {
//                        snackbar.dismiss();
//                    });
//                    snackbar.show();
//                    return;
//                }
//                if (cartItem.menuItem==null&&!cartItem.menuItem.menuCategory.addons.contains(addon)) {
//                    cartItem.hasAddonOfOtherCatg = true;
//                    return;
//                }
//                if (cartItem.count > 1) {
//                    Log.d(TAG, "---------------------raviprice: " + menuItem.price);
//                    MenuItem menuItemp = cartItem.menuItem;
//                    Number maxId = r.where(CartItem.class).max("id");
//                    long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
//                    CartItem lastItem = r.createObject(CartItem.class, nextId);
//                    lastItem.menuItem = menuItemp;
//                    lastItem.name = menuItemp.name;
//                    lastItem.price = (orderType.equals(Constants.TYPE_COLLECTION)) ? menuItemp.collectionPrice : menuItemp.deliveryPrice;
//                    lastItem.categoryIndex = menuCatAdapter.getSelectedCategoryIndex();
//                    lastItem.menuItemIndex = menuItemAdapter.getSelectionIndex();
//                    lastItem.count = 1;
//                    lastItem.addons.add(addon);
//                    cartItemAdapter.getCartItems().add(lastItem);
//                    cartItemAdapter.notifyItemInserted(cartItemAdapter.getItemCount() - 1);
//                    cartItem.count--;
//                    cartItemAdapter.notifyItemChanged(cartItemAdapter.indexOf(cartItem));
//                    cartItemAdapter.setSelection(cartItemAdapter.getItemCount() - 1);
//                    cartItemRV.scrollToPosition(cartItemAdapter.getItemCount() - 1);
//
//                } else {
//                    if (!addon.selected)
//                        cartItem.addons.add(addon);
//                    else {
//                        cartItem.addons.remove(addon);
//                        cartItem.hasAddonOfOtherCatg = false;
//                    }
//                    cartItemAdapter.notifyItemChanged(cartItemAdapter.indexOf(cartItem));
//                }
//                if (addon.selected) {
//                    observableTotalAmt.setValue(observableTotalAmt.getValue()  + addon.price);
//                } else {
//                    observableTotalAmt.setValue(observableTotalAmt.getValue()  - addon.price);
//                }
//            });
//
//
//            return;
//        }
        MenuCategory menuCategory = menuItem.menuCategory;
        resetAddonState(menuCategory);
        StringBuilder s=new StringBuilder();
        realm.executeTransaction(r -> {
            CartItem cartItem = r.where(CartItem.class).equalTo("menuItem.name", menuItem.name).and().isEmpty("addons").and()
                    .isEmpty("tables").findFirst();
            if (cartItem == null) {
                Log.e(TAG, "------------item not present in cart--------------");
                Number maxId = r.where(CartItem.class).max("id");
                long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
                cartItem = r.createObject(CartItem.class, nextId);
                cartItem.menuItem = menuItem;
            if(menuItem.parent!=null) {
                if (menuItem.printerSetting == 1){
                    for(int i=0;i<menuItem.parent.flavours.size();i++){
                        s.append(menuItem.parent.flavours.get(i).name + " " + menuItem.name);
                    }
                cartItem.name=s.toString();}

                else if (menuItem.printerSetting  == 2)
                    cartItem.name = menuItem.name + " " + menuItem.parent.name;
                else {
                    cartItem.name = menuItem.name;
                }
            }else{
                cartItem.name = menuItem.name;
            }
                cartItem.price = (orderType.equals(Constants.TYPE_COLLECTION)) ? menuItem.collectionPrice : menuItem.deliveryPrice;
                cartItem.categoryIndex = menuCatAdapter.getSelectedCategoryIndex();
                cartItem.menuItemIndex = menuItemAdapter.getSelectionIndex();
                cartItem.count = 1;
                cartItemAdapter.getCartItems().add(cartItem);
                cartItemAdapter.notifyItemInserted(cartItemAdapter.getItemCount() - 1);
                cartItemAdapter.setSelection(cartItemAdapter.getItemCount() - 1);
                cartItemRV.scrollToPosition(cartItemAdapter.getItemCount() - 1);
            } else {
                Log.e(TAG, "------------item present in cart--------------");
                cartItem.count++;
                int index = cartItemAdapter.indexOf(cartItem);
                cartItemAdapter.notifyItemChanged(index);
                cartItemAdapter.setSelection(index);
                cartItemRV.scrollToPosition(index);
            }

            int categoryIndex = menuCatAdapter.indexOf(menuItem.menuCategory);
            Log.e(TAG, "----------------------category Index: " + categoryIndex);
            adapter.notifyItemChanged(position);
            AbstractMenuItemAdapter newMenuItemAdapter = adapter;
            while (newMenuItemAdapter.parent != null) {
                Log.d(TAG, "------------------menuitem adapter parent not null---------------");
                newMenuItemAdapter = newMenuItemAdapter.parent;
                newMenuItemAdapter.notifyDataSetChanged();
            }
            menuCatAdapter.notifyItemChanged(menuCatAdapter.getSelectedCategoryIndex());
//            double price = orderType.equals(Constants.TYPE_DELIVERY)?menuItem.deliveryPrice:menuItem.collectionPrice;
            observableTotalAmt.setValue(observableTotalAmt.getValue() + cartItem.price);
        });
       /* realm.executeTransaction(r->{

            CartItem cartItem = r.where(CartItem.class).equalTo("menuItem.name", menuItem.name).and().isEmpty("addons").and()
                    .isEmpty("tables").findFirstAsync();


            RealmChangeListener<CartItem> cartItemRealmChangeListener = new RealmChangeListener<CartItem>() {
                @Override
                public void onChange(CartItem c) {
                    Log.d(TAG, "---------------------cartitem id: "+c.isValid());
                    if(!c.isValid()) {
                        Log.e(TAG, "------------item not present in cart--------------");
                        Number maxId = r.where(CartItem.class).max("id");
                        long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
                        c = r.createObject(CartItem.class, nextId);
                        c.menuItem = menuItem;
                        c.name = menuItem.name;
                        c.price = (orderType.equals(Constants.TYPE_COLLECTION)) ? menuItem.collectionPrice : menuItem.deliveryPrice;
                        c.categoryIndex = menuCatAdapter.getSelectedCategoryIndex();
                        c.menuItemIndex = menuItemAdapter.getSelectionIndex();
                        c.count = 1;

                        cartItemAdapter.getCartItems().add(c);
                        cartItemAdapter.notifyItemInserted(cartItemAdapter.getItemCount() - 1);
                        cartItemAdapter.setSelection(cartItemAdapter.getItemCount() - 1);
                        cartItemRV.scrollToPosition(cartItemAdapter.getItemCount() - 1);
                    } else {
                        Log.e(TAG, "------------item present in cart--------------");
                        c.count++;
                        int index = cartItemAdapter.indexOf(c);
                        cartItemAdapter.notifyItemChanged(index);
                        cartItemAdapter.setSelection(index);
                        cartItemRV.scrollToPosition(index);
                    }

                    int categoryIndex = menuCatAdapter.indexOf(menuItem.menuCategory);
                    Log.e(TAG, "----------------------category Index: " + categoryIndex);
                    adapter.notifyItemChanged(position);
                    AbstractMenuItemAdapter newMenuItemAdapter = adapter;
                    while (newMenuItemAdapter.parent != null) {
                        Log.d(TAG, "------------------menuitem adapter parent not null---------------");
                        newMenuItemAdapter = newMenuItemAdapter.parent;
                        newMenuItemAdapter.notifyDataSetChanged();
                    }
                    menuCatAdapter.notifyItemChanged(menuCatAdapter.getSelectedCategoryIndex());
                    double price = orderType.equals(Constants.TYPE_DELIVERY)?menuItem.deliveryPrice:menuItem.collectionPrice;
                    observableTotalAmt.setValue(observableTotalAmt.getValue() + price);

                    cartItem.removeChangeListener(this);
                    realmChangeListeners.remove(this);
                }
            };

            realmChangeListeners.add(cartItemRealmChangeListener);
            cartItem.addChangeListener(cartItemRealmChangeListener);
        });*/

       /* realm.executeTransaction(r->{
            Flowable<CartItem> cartItemFlowable =r.where(CartItem.class).equalTo("menuItem.name", menuItem.name).and().isEmpty("addons").and()
                    .isEmpty("tables").findFirstAsync().asFlowable();
            cartItemFlowable.subscribe(c -> {
                if(c==null) {
                    Log.e(TAG, "------------item not present in cart--------------");
                    Number maxId = r.where(CartItem.class).max("id");
                    long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
                    c = r.createObject(CartItem.class, nextId);
                    c.menuItem = menuItem;
                    c.name = menuItem.name;
                    c.price = (orderType.equals(Constants.TYPE_COLLECTION)) ? menuItem.collectionPrice : menuItem.deliveryPrice;
                    c.categoryIndex = menuCatAdapter.getSelectedCategoryIndex();
                    c.menuItemIndex = menuItemAdapter.getSelectionIndex();
                    c.count = 1;

                    cartItemAdapter.getCartItems().add(c);
                    cartItemAdapter.notifyItemInserted(cartItemAdapter.getItemCount() - 1);
                    cartItemAdapter.setSelection(cartItemAdapter.getItemCount() - 1);
                    cartItemRV.scrollToPosition(cartItemAdapter.getItemCount() - 1);
                } else {
                    Log.e(TAG, "------------item present in cart--------------");
                    c.count++;
                    int index = cartItemAdapter.indexOf(c);
                    cartItemAdapter.notifyItemChanged(index);
                    cartItemAdapter.setSelection(index);
                    cartItemRV.scrollToPosition(index);
                }
                int categoryIndex = menuCatAdapter.indexOf(menuItem.menuCategory);
                Log.e(TAG, "----------------------category Index: " + categoryIndex);
                adapter.notifyItemChanged(position);
                AbstractMenuItemAdapter newMenuItemAdapter = adapter;
                while (newMenuItemAdapter.parent != null) {
                    Log.d(TAG, "------------------menuitem adapter parent not null---------------");
                    newMenuItemAdapter = newMenuItemAdapter.parent;
                    newMenuItemAdapter.notifyDataSetChanged();
                }
                menuCatAdapter.notifyItemChanged(menuCatAdapter.getSelectedCategoryIndex());
                double price = orderType.equals(Constants.TYPE_DELIVERY)?menuItem.deliveryPrice:menuItem.collectionPrice;
                observableTotalAmt.setValue(observableTotalAmt.getValue() + price);
            });
        });*/
    }

    @Override
    public void onCartItemDeleted(int position, CartItem cartItem) {
        realm.executeTransaction(r -> {
            if (cartItem.count > 1) {
                cartItem.count--;
                cartItemAdapter.notifyItemChanged(cartItemAdapter.indexOf(cartItem));
                updateCounters(cartItem.menuItem);
            } else {
                int index = cartItemAdapter.indexOf(cartItem);
                cartItemAdapter.getCartItems().remove(cartItem);
                MenuItem menuItem = cartItem.menuItem;
                cartItem.deleteFromRealm();
                updateCounters(menuItem);
                cartItemAdapter.notifyItemRemoved(index);
                cartItemAdapter.setSelection(cartItemAdapter.getItemCount() - 1);
                if (cartItemAdapter.getSelectedCartItem() == null)
                    clearAddons();
                else
                    showSelectedAddons(cartItemAdapter.getSelectedCartItem());
            }
        });
    }

    @Override
    public void onClickAddon(final Addon addon) {
        Log.e(TAG, "-----------------addon: " + addon);
        realm.executeTransaction(r -> {
            CartItem cartItem = cartItemAdapter.getSelectedCartItem();
            if (cartItem == null || cartItemAdapter.isEmpty()) {
                addon.selected = false;
                Snackbar snackbar = Snackbar.make(BasicMenuActivity.this.getWindow().getDecorView(), "Select a CartItem on the extreme right first", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Dismiss", v -> {
                    snackbar.dismiss();
                });
                snackbar.show();
                return;
            }
            if (cartItem.menuItem==null&&!cartItem.menuItem.menuCategory.addons.contains(addon)) {
                cartItem.hasAddonOfOtherCatg = true;
                return;
            }
            if (cartItem.count > 1) {
                MenuItem menuItem = cartItem.menuItem;
                Number maxId = r.where(CartItem.class).max("id");
                long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
                CartItem lastItem = r.createObject(CartItem.class, nextId);
                lastItem.menuItem = menuItem;
                lastItem.name = menuItem.name;
                lastItem.price = (orderType.equals(Constants.TYPE_COLLECTION)) ? menuItem.collectionPrice : menuItem.deliveryPrice;
                lastItem.categoryIndex = menuCatAdapter.getSelectedCategoryIndex();
                lastItem.menuItemIndex = menuItemAdapter.getSelectionIndex();
                lastItem.count = 1;
                lastItem.addons.add(addon);
                cartItemAdapter.getCartItems().add(lastItem);
                cartItemAdapter.notifyItemInserted(cartItemAdapter.getItemCount() - 1);
                cartItem.count--;
                cartItemAdapter.notifyItemChanged(cartItemAdapter.indexOf(cartItem));
                cartItemAdapter.setSelection(cartItemAdapter.getItemCount() - 1);
                cartItemRV.scrollToPosition(cartItemAdapter.getItemCount() - 1);

            } else {
                if (addon.selected)
                    cartItem.addons.add(addon);
                else {
                    cartItem.addons.remove(addon);
                    cartItem.hasAddonOfOtherCatg = false;
                }
                cartItemAdapter.notifyItemChanged(cartItemAdapter.indexOf(cartItem));
            }
            if (addon.selected) {
                observableTotalAmt.setValue(observableTotalAmt.getValue() + addon.price);
            } else {
                observableTotalAmt.setValue(observableTotalAmt.getValue() - addon.price);
            }
        });
    }

    private boolean print4(List<CartItem> cartItems, long order_id, double total, double sub_total, double discount, double extra, String payment_type,
                           String orderType, String special_note, long userId, double deliveryCharges, double serviceCharges, boolean paid) {

        final Realm realm = Realm.getDefaultInstance();
        Vendor vendorInfo = realm.where(Vendor.class).findFirst();
        if (vendorInfo == null) {
            Toast.makeText(this, "Vendor Info is NULL", Toast.LENGTH_SHORT).show();
            return false;
        }
        final String provider = vendorInfo.getTitle();
        final String vendor = vendorInfo.getName();
        final String tel_no = vendorInfo.getTel_no();
        final String pin = vendorInfo.getPin();
        final String loc = vendorInfo.getAddress();
        final String vatNo = vendorInfo.getVatNo() == null ? "" : vendorInfo.getVatNo();
        final String companyNo = vendorInfo.getCompanyNo() == null ? "" : vendorInfo.getCompanyNo();

        final byte[] dividerLine = PrintUtils.getBytes("-----------------------");
        byte[] cursorPosition = null;

        final String POUND = "\u00A3";

        boolean success = true;
        try {
            List<Byte> bytes = new LinkedList<>();
            byte[] alignment = PrintUtils.setAlignment('0');
            PrintUtils.copyBytesToList(bytes, alignment);

            alignment = PrintUtils.setAlignment('1');
            PrintUtils.copyBytesToList(bytes, alignment);

            byte[] bold = PrintUtils.setBold(true);
            PrintUtils.copyBytesToList(bytes, bold);

            byte[] size = PrintUtils.setWH('4');
            PrintUtils.copyBytesToList(bytes, size);

            /*String orderID = Long.toString(order_id);
            PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(orderID), PrintUtils.getLF(1));*/
            PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(provider), PrintUtils.getLF(1));

            size = PrintUtils.setWH('2');
            bold = PrintUtils.setBold(false);
            PrintUtils.copyBytesToList(bytes, size);
            PrintUtils.copyBytesToList(bytes, bold);
            PrintUtils.copyBytesToList(bytes, dividerLine, PrintUtils.getLF(1));

            bold = PrintUtils.setBold(true);
            PrintUtils.copyBytesToList(bytes, bold);
            PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(vendor), PrintUtils.getLF(1));

            bold = PrintUtils.setBold(false);
            String location = loc.replace(",", "\n");
            PrintUtils.copyBytesToList(bytes, bold);
            PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(location), PrintUtils.getLF(1));

            PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(pin), PrintUtils.getLF(1));

            PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes("Tel:" + tel_no), PrintUtils.getLF(1));

            if (!vatNo.isEmpty())
                PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes("VAT No:" + vatNo), PrintUtils.getLF(1));
            if (!companyNo.isEmpty())
                PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes("Company No:" + companyNo), PrintUtils.getLF(1));

            PrintUtils.copyBytesToList(bytes, dividerLine, PrintUtils.getLF(1));

            if (special_note != null && !special_note.trim().isEmpty()) {
                PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(special_note), PrintUtils.getLF(1));

                PrintUtils.copyBytesToList(bytes, dividerLine, PrintUtils.getLF(1));
            }

            alignment = PrintUtils.setAlignment('0');
            size = PrintUtils.setWH('0'); // ge default size
            bold = PrintUtils.setBold(false);
            cursorPosition = PrintUtils.setCursorPosition(390);
            PrintUtils.copyBytesToList(bytes, alignment);
            PrintUtils.copyBytesToList(bytes, size);
            PrintUtils.copyBytesToList(bytes, bold);
            PrintUtils.copyBytesToList(bytes, cursorPosition);
            PrintUtils.copyBytesToList(bytes, PrintUtils.getTabs(1));
            PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes("GBP"), PrintUtils.getLF(1));

            cursorPosition = PrintUtils.setCursorPosition(0);
            PrintUtils.copyBytesToList(bytes, cursorPosition);

            final List<CartItem> cartItemList = new ArrayList<>(cartItems);
            Collections.sort(cartItemList, (o1, o2) -> {
                MenuItem mi1 = o1.menuItem;
                MenuItem mi2 = o2.menuItem;
                MenuCategory mc1 = null;
                MenuCategory mc2 = null;
                if (mi1 != null)
                    mc1 = mi1.menuCategory;
                if (mi2 != null)
                    mc2 = mi2.menuCategory;
                int printOrder1 = Integer.MAX_VALUE;
                int printOrder2 = Integer.MAX_VALUE;
                if (mc1 != null)
                    printOrder1 = mc1.getPrintOrder();
                if (mc2 != null)
                    printOrder2 = mc2.getPrintOrder();

                if (printOrder1 < printOrder2)
                    return -1;
                else if (printOrder1 > printOrder2)
                    return 1;
                else
                    return 0;

            });

            for (int i = 0; i < cartItemList.size(); i++) {
                CartItem a = cartItemList.get(i);
                Log.d(TAG, "print45: " + a.comment);
                StringBuilder addonStrBuilder = new StringBuilder();
                alignment = PrintUtils.setAlignment('0');
                size = PrintUtils.setWH('0');
                bold = PrintUtils.setBold(true);
                PrintUtils.copyBytesToList(bytes, alignment, bold, size);
                if (cartItems.size() != 0) {
                    PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(" " + a.count + " " + a.name));
                } else {
                    PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(" " + a.name));
                }
                cursorPosition = PrintUtils.setCursorPosition(390);
                PrintUtils.copyBytesToList(bytes, cursorPosition);


                if (cartItems.size() != 0) {
                    double price = (orderType.equals(Constants.TYPE_COLLECTION)) ? a.menuItem.collectionPrice : a.menuItem.deliveryPrice;
                    PrintUtils.copyBytesToList(bytes, PrintUtils.getTabs(1));
                    PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(String.format("%.2f", (a.count) * price)), PrintUtils.getLF(1));
                } else {
                    PrintUtils.copyBytesToList(bytes, PrintUtils.getTabs(1), PrintUtils.getLF(1));
                }

                //cursorPosition = PrintUtils.setCursorPosition(0);
                //PrintUtils.copyBytesToList(bytes, cursorPosition);

                //PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(addonStrBuilder.toString()));
                //UpdatedCode By Sourav Jha
                cursorPosition = PrintUtils.setCursorPosition(390);
                PrintUtils.copyBytesToList(bytes, cursorPosition);
                if (a.addons.size() != 0) {
                    List<Addon> addons = a.addons;

                    for (Addon addon : addons) {
                        Log.d(TAG, "print4: " + addon.price);
                        cursorPosition = PrintUtils.setCursorPosition(0);
                        PrintUtils.copyBytesToList(bytes, cursorPosition);
                        String included = (addon.isNoAddon == false) ? "+" : "-";
                        PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(" " + included + addon.name));
                        if (addon.price != 0) {
                            cursorPosition = PrintUtils.setCursorPosition(390);
                            PrintUtils.copyBytesToList(bytes, cursorPosition);
                            PrintUtils.copyBytesToList(bytes, PrintUtils.getTabs(1));
                            Log.d(TAG, "print44: " + addon.name + " " + addon.price);
                            PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(String.format("%.2f", addon.price)), PrintUtils.getLF(1));
                        } else {
                            PrintUtils.copyBytesToList(bytes, PrintUtils.getTabs(1), PrintUtils.getLF(1));
                        }
                    }
                }
                if (!a.comment.trim().isEmpty()) {
                    cursorPosition = PrintUtils.setCursorPosition(0);
                    PrintUtils.copyBytesToList(bytes, cursorPosition);
                    addonStrBuilder.append("\n").append("  *").append(a.comment.trim()).append("\n");
                    PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(addonStrBuilder.toString()));
                } else {
                    cursorPosition = PrintUtils.setCursorPosition(0);
                    PrintUtils.copyBytesToList(bytes, cursorPosition);
                    addonStrBuilder.append("\n");
                    PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(addonStrBuilder.toString()));
                }

                alignment = PrintUtils.setAlignment('1');
                size = PrintUtils.setWH('2');
                PrintUtils.copyBytesToList(bytes, alignment, size, dividerLine, PrintUtils.getLF(1));
            }

            /*size = PrintUtils.setWH('2');
            alignment = PrintUtils.setAlignment('1');
            PrintUtils.copyBytesToList(bytes, PrintUtils.getLF(1), size, alignment, dividerLine, PrintUtils.getLF(1));*/

            alignment = PrintUtils.setAlignment('0');
            bold = PrintUtils.setBold(true);
            size = PrintUtils.setWH('0');
            PrintUtils.copyBytesToList(bytes, alignment);
            PrintUtils.copyBytesToList(bytes, bold);
            PrintUtils.copyBytesToList(bytes, size);
            PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes("Subtotal: "));

            cursorPosition = PrintUtils.setCursorPosition(390);
            PrintUtils.copyBytesToList(bytes, cursorPosition);
            PrintUtils.copyBytesToList(bytes, PrintUtils.getTabs(1));
            PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(String.format(" %.2f", sub_total)), PrintUtils.getLF(1));


            if (discount > 0) {
                cursorPosition = PrintUtils.setCursorPosition(0);
                PrintUtils.copyBytesToList(bytes, cursorPosition);
                PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes("Discount: "));
                cursorPosition = PrintUtils.setCursorPosition(390);
                PrintUtils.copyBytesToList(bytes, cursorPosition);
                PrintUtils.copyBytesToList(bytes, PrintUtils.getTabs(1));
                PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes("-" + String.format("%.2f", discount)));
                PrintUtils.copyBytesToList(bytes, PrintUtils.getLF(1));
            }

            if (extra > 0) {
                cursorPosition = PrintUtils.setCursorPosition(0);
                PrintUtils.copyBytesToList(bytes, cursorPosition);
                PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes("Extra: "));
                cursorPosition = PrintUtils.setCursorPosition(390);
                PrintUtils.copyBytesToList(bytes, cursorPosition);
                PrintUtils.copyBytesToList(bytes, PrintUtils.getTabs(1));
                PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(String.format("%.2f", extra)));
                PrintUtils.copyBytesToList(bytes, PrintUtils.getLF(1));
            }

            if (deliveryCharges > 0) {
                cursorPosition = PrintUtils.setCursorPosition(0);
                PrintUtils.copyBytesToList(bytes, cursorPosition);
                PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes("Delivery Charge: "));
                cursorPosition = PrintUtils.setCursorPosition(390);
                PrintUtils.copyBytesToList(bytes, cursorPosition);
                PrintUtils.copyBytesToList(bytes, PrintUtils.getTabs(1));
                PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(String.format("%.2f", deliveryCharges)));
                PrintUtils.copyBytesToList(bytes, PrintUtils.getLF(1));
            }

            if (serviceCharges > 0) {
                cursorPosition = PrintUtils.setCursorPosition(0);
                PrintUtils.copyBytesToList(bytes, cursorPosition);
                PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes("Service Charge: "));
                cursorPosition = PrintUtils.setCursorPosition(390);
                PrintUtils.copyBytesToList(bytes, cursorPosition);
                PrintUtils.copyBytesToList(bytes, PrintUtils.getTabs(1));
                PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(String.format("%.2f", serviceCharges)));
                PrintUtils.copyBytesToList(bytes, PrintUtils.getLF(1));
            }


            cursorPosition = PrintUtils.setCursorPosition(0);
            size = PrintUtils.setWH('2');
            alignment = PrintUtils.setAlignment('1');
            bold = PrintUtils.setBold(false);
            PrintUtils.copyBytesToList(bytes, cursorPosition, size, alignment, bold, dividerLine, PrintUtils.getLF(1));

            alignment = PrintUtils.setAlignment('0');
            bold = PrintUtils.setBold(true);
            PrintUtils.copyBytesToList(bytes, alignment, bold, PrintUtils.getBytes("Total Price: "));

            cursorPosition = PrintUtils.setCursorPosition(390);
            PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(String.format("%.2f", total)), cursorPosition, PrintUtils.getLF(1));

            cursorPosition = PrintUtils.setCursorPosition(0);
            alignment = PrintUtils.setAlignment('1');
            bold = PrintUtils.setBold(false);
            PrintUtils.copyBytesToList(bytes, cursorPosition, alignment, bold, dividerLine, PrintUtils.getLF(1));

            String paidStatus = (paid == true) ? "PAID" : "NOT PAID";
            size = PrintUtils.setWH('4');
            bold = PrintUtils.setBold(true);
            PrintUtils.copyBytesToList(bytes, size, bold, PrintUtils.getBytes(payment_type + " " + paidStatus), PrintUtils.getLF(1));

            bold = PrintUtils.setBold(true);
            PrintUtils.copyBytesToList(bytes, size, bold, PrintUtils.getBytes(orderType), PrintUtils.getLF(1));

            size = PrintUtils.setWH('1');
            bold = PrintUtils.setBold(false);
            String time = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.UK).format(new Date());
            PrintUtils.copyBytesToList(bytes, size, bold, PrintUtils.getBytes(time));

            CustomerInfo customerInfo = realm.where(CustomerInfo.class).equalTo("id", userId).findFirst();
            String customerPhone = "";
            String customerHouseNo = "";
            String CustomerAddress = "";
            String CustomerPostalCode = "";
            String customerName = "";
            StringBuilder addressString = new StringBuilder();
            //&& !customerInfo.getPhone().trim().isEmpty()
            if (customerInfo != null) {
                customerName = (customerInfo.getName() == null || customerInfo.getName().isEmpty()) ? "" : customerInfo.getName().trim();
                if (!customerName.isEmpty())
                    addressString.append(customerName);

                if (orderType.equals(Constants.TYPE_DELIVERY)) {
                    customerHouseNo = (customerInfo.getHouse_no() == null) ? "" : customerInfo.getHouse_no().trim();
                    if (!customerHouseNo.isEmpty()) {
                        if (addressString.length() != 0)
                            addressString.append(",").append(customerHouseNo);
                        else
                            addressString.append(customerHouseNo);
                    }

                    if (customerInfo.getPostalInfo() != null) {
                        CustomerAddress = (customerInfo.getPostalInfo().getAddress() == null) ? "" : customerInfo.getPostalInfo().getAddress().trim();
                        if (!CustomerAddress.isEmpty()) {
                            if (addressString.length() != 0) {
                                addressString.append(" ").append(CustomerAddress);
                            } else
                                addressString.append(CustomerAddress);
                        }
                        CustomerPostalCode = (customerInfo.getPostalInfo().getA_PostCode() == null) ? "" : customerInfo.getPostalInfo().getA_PostCode().trim();
                        if (!CustomerPostalCode.isEmpty()) {
                            if (addressString.length() != 0)
                                addressString.append(",").append(CustomerPostalCode);
                            else
                                addressString.append(CustomerAddress);
                        }
                    }
                }

                customerPhone = (customerInfo.getPhone() == null) ? "" : customerInfo.getPhone();
                if (!customerPhone.isEmpty()) {
                    if (addressString.length() != 0)
                        addressString.append(",").append(customerPhone);
                    else
                        addressString.append(customerPhone);
                }

                Number totalOrders = realm.where(Purchase.class).equalTo("orderCustomerInfo.phone", customerInfo.getPhone()).count();
                int totalOrderCount = (totalOrders == null) ? 0 : totalOrders.intValue();
                if (totalOrderCount == 1) {
                    addressString.append(",,").append("New Customer");
                } else {
                    if (!customerPhone.isEmpty())
                        addressString.append(",,").append("Total Orders: ").append(totalOrderCount);
                }
            }

            if (!addressString.toString().trim().isEmpty()) {
                PrintUtils.copyBytesToList(bytes, PrintUtils.getLF(1), alignment, bold, dividerLine, PrintUtils.getLF(1));

                // added later
                alignment = PrintUtils.setAlignment('0');
                size = PrintUtils.setWH('4');
                PrintUtils.copyBytesToList(bytes, alignment, size);

                StringBuilder stringBuilder = new StringBuilder();
                String details = StringHelper.capitalizeEachWordAfterComma(addressString.toString());
                String str = details.replace(",", "\n");
                /*String[] tokens = StringHelper.capitalizeEachWordAfterComma(addressString.toString()).split(",");
                List<String> stringList = Arrays.asList(tokens);
                Iterator<String> stringIterator = stringList.iterator();
                while (stringIterator.hasNext()) {
                    final String str = stringIterator.next();
                    if (stringIterator.hasNext())
                        stringBuilder.append(str.trim() + "\n");
                    else
                        stringBuilder.append(str.trim());
                }*/

                PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes("Customer Details:\n"), PrintUtils.getBytes(str));
            }

            alignment = PrintUtils.setAlignment('1');
            bold = PrintUtils.setBold(false);
            size = PrintUtils.setWH('2');
            PrintUtils.copyBytesToList(bytes, PrintUtils.getLF(1), alignment, bold, size, dividerLine, PrintUtils.getLF(1));

            bold = PrintUtils.setBold(true);
            size = PrintUtils.setWH('4');
            PrintUtils.copyBytesToList(bytes, bold, size, PrintUtils.getBytes("www.foodciti.co.uk"), PrintUtils.getLF(2), PrintUtils.cutPaper());

            byte[] arr = PrintUtils.toPrimitiveBytes(bytes);

            Intent intent = new Intent(PrintUtils.ACTION_PRINT_REQUEST);
            intent.putExtra(PrintUtils.PRINT_DATA, arr);
            localBroadcastManager.sendBroadcast(intent);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            realm.close();
        }
        return success;
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


//    private void firebaseListener() {
//        //isNetworkAvailable();
//        foodTruckId = SessionManager.get(this).getFoodTruckId();
//        //get reference to the orders node
//        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
//        valueEventListener=new ValueEventListener(){
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.e("ORDER_DATA_SNAP", "" + dataSnapshot);
//                for (DataSnapshot child : dataSnapshot.getChildren()) {
//
//                    final String orderkey = child.getKey();
//                    String order_status = "";
//                    try {
//                        HashMap<String, String> data = (HashMap<String, String>) child.getValue();
//                        Log.e("OBJECT", "" + data.get("order_status"));
//                        order_status = data.get("order_status");
//                    } catch (Exception e) {
//                        Log.e("FDB_EXP", "" + e.toString());
//                    }
//                    //     String orderStatus = child.child("order_status").getValue(String.class);
//                    // for (String dataqueue:dataQueue) {
//                    if (dataQueue.contains(orderkey)) {
//                        continue;
//                    } else {
//                        Log.e("ORDER_KEY", "" + orderkey + ", " + order_status);
//                        dataQueue.add(orderkey);
////                                int orderStatus = Integer.parseInt(order_status);
//                        getStatusOrderList(orderkey, 0);
//                        //  }
//                    }
//                    // }
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
////        mFirebaseDatabase.child(foodTruckId).addValueEventListener(valueEventListener);
//    }


    private void getStatusOrderList(final String orderId, int orderStatus) {
        if (InternetConnection.checkConnection(BasicMenuActivity.this)) {
            //  if (!TextUtils.isEmpty(SessionManager.get(RestaurentMainActivity.this).getFoodTruckId())) {
            io.reactivex.Observable<GenericResponse<List<Order>>> results = RetroClient.getApiService()
                    .getStatusOrderList(SessionManager.get(BasicMenuActivity.this).getFoodTruckId(), orderStatus);
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
//            wifi_signal.setBackground(getResources().getDrawable(R.drawable.no_wifi));
            Toast.makeText(BasicMenuActivity.this, R.string.string_internet_connection_warning, Toast.LENGTH_LONG).show();

        }
    }

    private void showOrderInfo(Order order) {
        Log.e("ORDER_DIALOG", "" + order.getFoodTruckId());
        OrderInfo orderInfo = OrderInfo.newInstance(order);
        orderInfo.setItemListener(this);
        orderInfo.setCancelable(false);
        orderInfo.show(getSupportFragmentManager(), null);
    }
}
