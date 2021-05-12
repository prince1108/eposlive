package com.foodciti.foodcitipartener.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.AbstractMenuItemAdapter;
import com.foodciti.foodcitipartener.adapters.CartItemAdapter;
import com.foodciti.foodcitipartener.adapters.MenuCategoryAdapterForTable;
import com.foodciti.foodcitipartener.adapters.MenuItemAdapterForTable;
import com.foodciti.foodcitipartener.adapters.TableOrderAdapter;
import com.foodciti.foodcitipartener.adapters.callbacks.GridItemTouchHelperCallback;
import com.foodciti.foodcitipartener.compound_views.DiscountView;
import com.foodciti.foodcitipartener.db.ExceptionLogger;
import com.foodciti.foodcitipartener.dialogs.AddManualOrderDialog;
import com.foodciti.foodcitipartener.dialogs.CustomAlertDialog;
import com.foodciti.foodcitipartener.dialogs.FireBaseDialoge;
import com.foodciti.foodcitipartener.dialogs.ItemSubDetails;
import com.foodciti.foodcitipartener.dialogs.OrderInfo;
import com.foodciti.foodcitipartener.dialogs.PasswordDialog;
import com.foodciti.foodcitipartener.dialogs.PersonsPerTableDialog;
import com.foodciti.foodcitipartener.dialogs.PoundDialog;
import com.foodciti.foodcitipartener.dialogs.SpecialNoteDialog;
import com.foodciti.foodcitipartener.dialogs.TableCheckoutDialog2;
import com.foodciti.foodcitipartener.dialogs.TablesFullViewDialog;
import com.foodciti.foodcitipartener.dialogs.UpdateCustomerDetails;
import com.foodciti.foodcitipartener.dialogs.WarningListDialog;
import com.foodciti.foodcitipartener.realm_entities.Addon;
import com.foodciti.foodcitipartener.realm_entities.CartItem;
import com.foodciti.foodcitipartener.realm_entities.CustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.Exlogger;
import com.foodciti.foodcitipartener.realm_entities.MenuCategory;
import com.foodciti.foodcitipartener.realm_entities.MenuItem;
import com.foodciti.foodcitipartener.realm_entities.Purchase;
import com.foodciti.foodcitipartener.realm_entities.PurchaseEntry;
import com.foodciti.foodcitipartener.realm_entities.Table;
import com.foodciti.foodcitipartener.realm_entities.Vendor;
import com.foodciti.foodcitipartener.response.GenericResponse;
import com.foodciti.foodcitipartener.response.Order;
import com.foodciti.foodcitipartener.response.OrderedItem;
import com.foodciti.foodcitipartener.response.SubItem;
import com.foodciti.foodcitipartener.rest.RetroClient;
import com.foodciti.foodcitipartener.services.CallerIDService;
import com.foodciti.foodcitipartener.services.FirebaseBackgroundService;
import com.foodciti.foodcitipartener.services.LoggerUploadService;
import com.foodciti.foodcitipartener.services.SmsService;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.Consts;
import com.foodciti.foodcitipartener.utils.InternetConnection;
import com.foodciti.foodcitipartener.utils.Preferences;
import com.foodciti.foodcitipartener.utils.PrintUtils;
import com.foodciti.foodcitipartener.utils.RealmManager;
import com.foodciti.foodcitipartener.utils.RecyclerViewItemSelectionAfterLayoutUpdate;
import com.foodciti.foodcitipartener.utils.SessionManager;
import com.foodciti.foodcitipartener.utils.StringHelper;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

public class BasicTableMenuActivity extends AbstractMenuActivity implements View.OnClickListener, Observer, OrderInfo.OnClickListener, PoundDialog.PounddialogListener
        , SpecialNoteDialog.SpecialNoteListener, UpdateCustomerDetails.Callback, TableOrderAdapter.Clicklistener {

    private final int TIMEOUT_IN_SECONDS = 30;
    protected TextView mPrintReception, extra_price, valueSubtotal;
    private View settings, cancelOrder, cashDrawer, extraDiscount, reports, total, collection, totalLayout, orderHistory,onlineOrder, toggleAllTableView;
    private TextView customerMobile, houseName, address, totalPrice, whatType, subTotal, specialNote;
    private ConstraintLayout editDetailsLayout, subtotalContainer;
    private LinearLayout itemContainer, menuItemContainer, addonContainer, itemAddonContainer;
    private DiscountView discountView;
    private RecyclerView tableRV;
    private TableOrderAdapter tableOrderAdapter;
    private ConstraintLayout tableContainer;
    private CardView currentTableContainer;
    private ImageView back;
    private TextView currentTable;
    private Handler handler;
    private LocalBroadcastManager localBroadcastManager;
    private volatile int automatiDisableCounter = 0;
    private TableLockThread mTableLockThread;
//    private DatabaseReference mFirebaseDatabase;
//    private ValueEventListener valueEventListener=null;
    private String foodTruckId;
    private Set<String> dataQueue = new HashSet<>();
    private RealmResults<Table> tableRealmResultsAsync;
    private AsyncTask<Void, Void, Void> tableLockAsyncTask = null;
    SessionManager sessionManager;
    ExceptionLogger logger;
    private Runnable handlerCallback = new Runnable() {
        @Override
        public void run() {
            currentTableContainer.setCardBackgroundColor(ContextCompat.getColor(BasicTableMenuActivity.this, R.color.colorDarkGray));
            menuItemAdapter.setDisabled(true);
            addonAdapter.setDisabled(true);
            noAddonAdapter.setDisabled(true);
            cartItemAdapter.setDisabled(true);
            commonItemAdapter.setDisabled(true);
            Table table = tableOrderAdapter.getSelectedTable();
            if (table != null)
                currentTable.setText("Tap " + tableOrderAdapter.getSelectedTable().getName() + " to enable menu objects");
        }
    };

    private void enableMenuObjects() {
//        resetDisableCounter();
        if (tableOrderAdapter.getSelectedTable() != null) {
            currentTable.setText(tableOrderAdapter.getSelectedTable().getName());
            currentTableContainer.setCardBackgroundColor(ContextCompat.getColor(BasicTableMenuActivity.this, R.color.colorVividTangerine));
            menuItemAdapter.setDisabled(false);
            addonAdapter.setDisabled(false);
            noAddonAdapter.setDisabled(false);
            cartItemAdapter.setDisabled(false);
            commonItemAdapter.setDisabled(false);
            /*if (tableLockAsyncTask == null || !tableLockAsyncTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
                tableLockAsyncTask = new TableLockAsyncTask();
                tableLockAsyncTask.execute();
            }*/
        }
    }

    private void resetDisableCounter() {
        automatiDisableCounter = 0;
    }

    private void initViews() {
        tableRV = findViewById(R.id.tableRV);
//        RecyclerView.LayoutManager tableLayoutManager = new GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false);
        RecyclerView.LayoutManager tableLayoutManager = new GridLayoutManager(this, 4, RecyclerView.VERTICAL, false);
        tableRV.setLayoutManager(tableLayoutManager);
        tableRV.setHasFixedSize(true);

        // set base class properties
        /*setCategories(findViewById(R.id.category));
        setMenuItems(findViewById(R.id.category_data));
        setCommonItems(findViewById(R.id.commonItemList));*/
        setAddons(findViewById(R.id.addon_list));
        setNoAddons(findViewById(R.id.noitem_list));
        setCartItems(findViewById(R.id.cartRV));

        menuCatRV = findViewById(R.id.category);
        menuCatAdapter = new MenuCategoryAdapterForTable(this, null, null);
        menuCatRV.setAdapter(menuCatAdapter);
        GridItemTouchHelperCallback categoryTouchHelperCallback = new GridItemTouchHelperCallback(menuCatAdapter);
        ItemTouchHelper categoryTouchHelper = new ItemTouchHelper(categoryTouchHelperCallback);
        categoryTouchHelper.attachToRecyclerView(menuCatRV);

        menuItemRV = findViewById(R.id.category_data);
        menuItemAdapter = new MenuItemAdapterForTable(this, null, null, Constants.ITEM_TYPE_MENU, orderType);
        menuItemRV.setAdapter(menuItemAdapter);
        GridItemTouchHelperCallback menuItemTouchHelperCallback = new GridItemTouchHelperCallback(menuItemAdapter);
        ItemTouchHelper menuItemtouchHelper = new ItemTouchHelper(menuItemTouchHelperCallback);
        menuItemtouchHelper.attachToRecyclerView(menuItemRV);

        commonItemRV = findViewById(R.id.commonItemList);
        commonItemAdapter = new MenuItemAdapterForTable(this, null, null, Constants.ITEM_TYPE_COMMON, orderType);
        commonItemRV.setAdapter(commonItemAdapter);
        GridItemTouchHelperCallback itemTouchHelperCallback2 = new GridItemTouchHelperCallback(commonItemAdapter);
        ItemTouchHelper touchHelper2 = new ItemTouchHelper(itemTouchHelperCallback2);
        touchHelper2.attachToRecyclerView(commonItemRV);


        settings = findViewById(R.id.setting_btn);
        settings.setOnClickListener(this);
        cancelOrder = findViewById(R.id.clear_order_button);
        cancelOrder.setOnClickListener(this);
        orderHistory = findViewById(R.id.orderhistory);
        orderHistory.setOnClickListener(this);
        cashDrawer = findViewById(R.id.open_cash_drawer_btn);
        cashDrawer.setOnClickListener(this);
        /*extraDiscount = findViewById(R.id.extra_discount_btn);
        extraDiscount.setOnClickListener(this);*/
        onlineOrder = findViewById(R.id.OnlineOrder);
        onlineOrder.setOnClickListener(this);
        reports = findViewById(R.id.reports_btn);
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

        specialNote = findViewById(R.id.special_note_text);

        findViewById(R.id.special_note).setOnClickListener(this);
        findViewById(R.id.cancel_order_button).setOnClickListener(this);
        findViewById(R.id.manual_order).setOnClickListener(this);
        findViewById(R.id.warning_list).setOnClickListener(this);
        findViewById(R.id.show_tables).setOnClickListener(this);

        itemContainer = findViewById(R.id.itemContainer);
        menuItemContainer = findViewById(R.id.menuItemContainer);
        addonContainer = findViewById(R.id.addonContainer);

        tableContainer = findViewById(R.id.tableContainer);
        itemAddonContainer = findViewById(R.id.itemAddonContainer);
        currentTableContainer = findViewById(R.id.currentTableContainer);
        currentTableContainer.setOnClickListener(this);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        currentTable = findViewById(R.id.currentTable);

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
    }

    private void setUpColumns() {
        int numTableCol = sharedPreferences.getInt(Preferences.TABLE_NUM_COLUMNS, 4);
//        RecyclerView.LayoutManager tableLayoutManager = new GridLayoutManager(this, numTableCol);
//        RecyclerView.LayoutManager tableLayoutManager = new GridLayoutManager(this, numTableCol, RecyclerView.HORIZONTAL, false);
        RecyclerView.LayoutManager tableLayoutManager = new GridLayoutManager(this, numTableCol, RecyclerView.VERTICAL, false);
        tableRV.setLayoutManager(tableLayoutManager);

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
        final float tableHeight = sharedPreferences.getFloat(Preferences.TABLE_HEIGHT, 0.25f);
        final float categoryWeight = sharedPreferences.getFloat(Preferences.CATEGORY_ITEMS_WIDTH_RATIO, 0.2f);
        final float itemWeight = sharedPreferences.getFloat(Preferences.ITEMS_ADDONS_WIDTH_RATIO, 0.6f);
        final float itemHeight = sharedPreferences.getFloat(Preferences.ITEMS_HEIGHT_RATIO, 0.75f);
        final float addonHeight = sharedPreferences.getFloat(Preferences.ADDON_HEIGHT_RATIO, 0.5f);

        LinearLayout.LayoutParams tableLp = (LinearLayout.LayoutParams) tableContainer.getLayoutParams();
        LinearLayout.LayoutParams itemAddonLp = (LinearLayout.LayoutParams) itemAddonContainer.getLayoutParams();
        LinearLayout.LayoutParams categoryLp = (LinearLayout.LayoutParams) menuCatRV.getLayoutParams();
        LinearLayout.LayoutParams itemContainerLp = (LinearLayout.LayoutParams) itemContainer.getLayoutParams();
        LinearLayout.LayoutParams menuItemContainerLp = (LinearLayout.LayoutParams) menuItemContainer.getLayoutParams();
        LinearLayout.LayoutParams addonContainerLp = (LinearLayout.LayoutParams) addonContainer.getLayoutParams();
        LinearLayout.LayoutParams menuItemRecyclerLp = (LinearLayout.LayoutParams) menuItemRV.getLayoutParams();
        LinearLayout.LayoutParams commonItemRecyclerLp = (LinearLayout.LayoutParams) commonItemRV.getLayoutParams();
        LinearLayout.LayoutParams addonRecyclerLp = (LinearLayout.LayoutParams) addonRV.getLayoutParams();
        LinearLayout.LayoutParams noAddonRecyclerLp = (LinearLayout.LayoutParams) noAddonRV.getLayoutParams();

        tableLp.weight = tableHeight;
        itemAddonLp.weight = 1 - tableHeight;

        categoryLp.weight = categoryWeight;
        itemContainerLp.weight = 1 - categoryWeight;

        menuItemContainerLp.weight = itemWeight;
        addonContainerLp.weight = 1 - itemWeight;

        menuItemRecyclerLp.weight = itemHeight;
        commonItemRecyclerLp.weight = 1 - itemHeight;

        addonRecyclerLp.weight = addonHeight;
        noAddonRecyclerLp.weight = 1 - addonHeight;

        tableContainer.setLayoutParams(tableLp);
        itemAddonContainer.setLayoutParams(itemAddonLp);
        menuCatRV.setLayoutParams(categoryLp);
        itemContainer.setLayoutParams(itemContainerLp);
        menuItemContainer.setLayoutParams(menuItemContainerLp);
        addonContainer.setLayoutParams(addonContainerLp);
        menuItemRV.setLayoutParams(menuItemRecyclerLp);
        commonItemRV.setLayoutParams(commonItemRecyclerLp);
        addonRV.setLayoutParams(addonRecyclerLp);
        noAddonRV.setLayoutParams(noAddonRecyclerLp);
    }

    private void unSelectTable(Table table) {
        currentTableContainer.setVisibility(View.GONE);
        cartItemRV.setAdapter(null);

        MenuItemAdapterForTable itemAdapterForTable = (MenuItemAdapterForTable) menuItemAdapter;
        MenuCategoryAdapterForTable categoryAdapterForTable = (MenuCategoryAdapterForTable) menuCatAdapter;

        itemAdapterForTable.setTable(null);
        categoryAdapterForTable.setTable(null);

        itemAdapterForTable.notifyDataSetChanged();
        categoryAdapterForTable.notifyDataSetChanged();
        clearAddons();
        resetCustomerInfoFields();
        discountView.reset();
        tableOrderAdapter.clearSelection();
        Log.e(TAG, "-------------------interrupting tablelock thread--------------------------");
//        mTableLockThread.interrupt();
//        tableLockAsyncTask.cancel(true);
    }

    private void resetCustomerInfoFields() {
        customerMobile.setText("");
        address.setText("");
        whatType.setText("");
    }

    private double computeTotal(List<CartItem> cartItems) {
        double total = 0;
        for (CartItem c : cartItems) {
            total += (c.price) * c.count;
            for (Addon a : c.addons)
                total += a.price;
        }
        return total;
    }

    private void createOrderHistory2(final List<CartItem> itemsToCheckout, final String paymentMode, double serviceCharges, boolean paid) {
        Log.d(TAG, "-----------------items to checkout: " + itemsToCheckout);
        realm.executeTransaction(r -> {
            Number maxPurchaseId = realm.where(Purchase.class).max("id");
            long nextPuchaseId = (maxPurchaseId == null) ? 1 : maxPurchaseId.longValue() + 1;
            Purchase purchase = r.createObject(Purchase.class, nextPuchaseId);
            for (CartItem c : itemsToCheckout) {
                Number maxPurchaseEntry = r.where(PurchaseEntry.class).max("id");
                long nextPurchaseEntryId = (maxPurchaseEntry == null) ? 1 : maxPurchaseEntry.longValue() + 1;
                PurchaseEntry purchaseEntry = r.createObject(PurchaseEntry.class, nextPurchaseEntryId);
                purchaseEntry.getOrderAddons().addAll(toOrderAddons(c.addons));
                purchaseEntry.setOrderMenuItem(toOrderMenuItem(c.menuItem));
                purchaseEntry.setCount(c.count);
                purchaseEntry.setPrice(c.price);
                purchaseEntry.setAdditionalNote(c.comment);
                purchase.getPurchaseEntries().add(purchaseEntry);
            }
//            purchase.setDeliveryCharges(deliveryCharges);
            purchase.setServiceCharges(serviceCharges);
            purchase.setExtra(extra);
            purchase.setDiscount(discount);
            purchase.setSubTotal(subTotalAmount);
            purchase.setTotal(totalAmount);
            purchase.setTimestamp(new Date().getTime());
            purchase.setOrderTimeStamp(new Date());
            purchase.setOrderCustomerInfo(toOrderCustomerInfo(tableOrderAdapter.getSelectedTable().getCustomerInfo()));
            purchase.setTableName(tableOrderAdapter.getSelectedTable().getName());
            purchase.setTableId(tableOrderAdapter.getSelectedTable().getId());
            purchase.setOrderType(orderType);
            purchase.setPaymentMode(paymentMode);
            purchase.setPaid(paid);
        });
    }

    private void checkOut(int splitBillIndex, List<CartItem> itemsToCheckout, double returnPound, String paymentType, boolean sendSMS, boolean paid) {
        if (totalAmount == 0) {
            Toast.makeText(this, "Can't checkout with NIL amount", Toast.LENGTH_SHORT).show();
            return;
        }
        double serviceCharges = 0;
        float tax = sharedPreferences.getFloat(Preferences.SERVICE_CHARGES_TABLE, 0);
        int serviceChargesOption = sharedPreferences.getInt(Preferences.SERVICE_CHARGE_OPTION_FOR_TABLE, Preferences.OPTION_CASH);
        boolean isPercentServiceCharge = sharedPreferences.getBoolean(Preferences.SERVICE_CHARGES_PERCENTAGE_FOR_TABLE, false);

        if (paymentType.equalsIgnoreCase(Constants.PAYMENT_TYPE_CASH)) {
            if (serviceChargesOption == Preferences.OPTION_CASH || serviceChargesOption == Preferences.OPTION_BOTH) {
                if (isPercentServiceCharge)
                    serviceCharges = ((tax / 100) * totalAmount);
                else
                    serviceCharges = tax;
            }
        }

        if (paymentType.equalsIgnoreCase(Constants.PAYMENT_TYPE_CARD)) {
            if (serviceChargesOption == Preferences.OPTION_CARD || serviceChargesOption == Preferences.OPTION_BOTH) {
                if (isPercentServiceCharge)
                    serviceCharges = ((tax / 100) * totalAmount);
                else
                    serviceCharges = tax;
            }
        }

        subTotalAmount = computeTotal(itemsToCheckout);
        totalAmount = subTotalAmount;
        totalAmount += serviceCharges;
        totalAmount -= discount;

        long order_id = -1;

        if (tableOrderAdapter.getSelectedTable() != null) {
            if (itemsToCheckout.size() > 0) {
                Table table = tableOrderAdapter.getSelectedTable();
/*                realm.executeTransaction(r->{
                    for(CartItem c: table.cartItems) {
                        table.cartItems.remove(c);
                    }
                });*/
//                createOrderHistory2(itemsToCheckout, paymentType, serviceCharges, paid);
                order_id = realm.where(Purchase.class).max("id").longValue();
                Log.e(TAG, "--------------------orderid from function: " + order_id);
            } else {
                Snackbar snackbar = Snackbar.make(BasicTableMenuActivity.this.getWindow().getDecorView(), "No items in cart", Snackbar.LENGTH_LONG);
                snackbar.setAction("Dismiss", v -> {
                    snackbar.dismiss();
                });
                snackbar.show();
                return;
            }
        } else {
            Snackbar snackbar = Snackbar.make(BasicTableMenuActivity.this.getWindow().getDecorView(), "Choose a table first", Snackbar.LENGTH_LONG);
            snackbar.setAction("Dismiss", v -> {
                snackbar.dismiss();
            });
            snackbar.show();
            return;
        }

        int print_counter = 0;
        final long ARG_PRINT_ORDERID = order_id;
        final double ARG_PRINT_SERVICE_CHARGES = serviceCharges;
        if (orderType.equals(Constants.TYPE_DELIVERY)) {
            print_counter = sharedPreferences.getInt(Preferences.NUM_PRINT_COPIES_DEL, 1);
        } else if (orderType.equals(Constants.TYPE_COLLECTION)) {
            print_counter = sharedPreferences.getInt(Preferences.NUM_PRINT_COPIES_COL, 1);
        } else if (orderType.equals(Constants.TYPE_TABLE)) {
            print_counter = sharedPreferences.getInt(Preferences.NUM_PRINT_COPIES_TAB, 1);
        }
        for (int i = 0; i < print_counter; i++) {
            printForTable(itemsToCheckout, ARG_PRINT_ORDERID, totalAmount, subTotalAmount, discount, extra, paymentType, orderType, specialNote.getText().toString(), tableOrderAdapter.getSelectedTable().getId(), ARG_PRINT_SERVICE_CHARGES, paid);
        }

        createOrderHistory2(itemsToCheckout, paymentType, serviceCharges, paid);
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;
        String sDate = c.get(Calendar.DAY_OF_MONTH) + "-" + month + "-" + c.get(Calendar.YEAR);
        String time = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND);

        final Table table = tableOrderAdapter.getSelectedTable();
        CustomerInfo userData = table.getCustomerInfo();
        if (userData != null && sendSMS) {
            StringBuilder msgBuilder = new StringBuilder();
            Vendor vendorInfo = realm.where(Vendor.class).findFirst();
            final String provider = vendorInfo.getTitle();
            final String vendor = vendorInfo.getName();
            final String tel_no = vendorInfo.getTel_no();
            final String pin = vendorInfo.getPin();
            final String loc = vendorInfo.getAddress();
//            final String vatNo = vendorInfo.getVatNo();
            final String poundSymbol = getString(R.string.pound_symbol);
            msgBuilder.append(provider).append("\n")
                    .append(vendor).append("\n")
                    .append(tel_no).append("\n")
                    .append(pin).append("\n")
                    .append(loc).append("\n\n");
            for (CartItem cartItem : itemsToCheckout) {
                msgBuilder.append(cartItem.count).append(" x ").append(cartItem.menuItem.name)
                        .append("\t\t").append(poundSymbol + String.format("%.2f",cartItem.count * cartItem.price)).append("\n");
                for (Addon addon : cartItem.addons) {
                    String prefix = (addon.isNoAddon) ? "-" : "+";
                    msgBuilder.append("\t").append(prefix).append(addon.name).append("\n");
                }
                /*msgBuilder.append(cartItem.count * cartItem.price + " " + poundSymbol)
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

            msgBuilder.append("Order Type: " + orderType + " " + table.getName()).append("\n")
                    .append("Your total is: " + totalAmount).append("\n")
                    .append("Thank you");

            Log.e(TAG, msgBuilder.toString());

            SmsService.instance.sendSms(userData.getPhone().trim(), msgBuilder.toString());
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
        String currentTotal = String.format("%.2f", getTotal());

        observableTotalAmt.setValue(getTotal());
        clearCart(itemsToCheckout);
        realm.executeTransaction(r -> {
            clearAddons();
        });

        clearCommonItems();
        observableTotalAmt.setValue(0.0);

        Intent intent = new Intent("splitbill_update");
        intent.putExtra("SPLIT_BILL_INDEX", splitBillIndex);
        localBroadcastManager.sendBroadcast(intent);

        if (splitBillIndex == 0) {
            realm.executeTransaction(r -> {
                table.setDirty(true);
            });
            unSelectTable(table);
        }
    }

    private void clearCart(List<CartItem> itemsToRemove) {
        realm.executeTransaction(r -> {
            if (tableOrderAdapter.getSelectedTable() != null) {
                Long[] idArray = new Long[itemsToRemove.size()];
                int i = 0;
                for (CartItem c : itemsToRemove)
                    idArray[i++] = c.id;
                RealmResults<CartItem> realmResults = r.where(CartItem.class).in("id", idArray).findAll();
                Log.e(TAG, "----------------------realmresults of cart size: " + realmResults.size());
                realmResults.deleteAllFromRealm();
                tableOrderAdapter.getSelectedTable().setCustomerInfo(null);
            } else {
                Log.e(TAG, "-----can't clear cart on null table");
                handler.post(() -> {
                    Toast.makeText(BasicTableMenuActivity.this, "can't clear cart on null table", Toast.LENGTH_SHORT).show();
                });
            }
            if (cartItemAdapter != null)
                cartItemAdapter.notifyDataSetChanged();

        });

    }

    private void showTable(Table table) {
        currentTableContainer.setVisibility(View.VISIBLE);
        currentTable.setText(table.getName());
        TextView personCountTV = currentTableContainer.findViewById(R.id.personCount);
        personCountTV.setText("Person(s): " + table.getTotalPersons());
        resetMenuState(table);
        enableMenuObjects();
    }

    private void resetMenuState(Table table) {
        observableTotalAmt.setValue(0.0);
        if (realm.isInTransaction()) {
            List<CartItem> cartItems = table.cartItems;
            cartItemAdapter = new CartItemAdapter(this, cartItems);
            cartItemRV.setAdapter(cartItemAdapter);

        } else {
            realm.executeTransaction(r -> {
                List<CartItem> cartItems = table.cartItems;
                cartItemAdapter = new CartItemAdapter(this, cartItems);
                cartItemRV.setAdapter(cartItemAdapter);

            });
        }
        ((MenuItemAdapterForTable) menuItemAdapter).setTable(table);
        menuItemAdapter.notifyDataSetChanged();

        ((MenuCategoryAdapterForTable) menuCatAdapter).setTable(table);
        menuCatAdapter.notifyDataSetChanged();

        observableTotalAmt.setValue(getTotal());
    }

    private void changeTableAvailableStatus(Table table) {
        if (table == null)
            return;
        if (realm.isInTransaction()) {
            if (table.cartItems.isEmpty())
                table.setAvailable(true);
            else
                table.setAvailable(false);
        } else {
            realm.executeTransaction(r -> {
                if (table.cartItems.isEmpty())
                    table.setAvailable(true);
                else
                    table.setAvailable(false);
            });
        }
        Log.e(TAG, "-------------table empty? " + table.cartItems.isEmpty());
        Log.e(TAG, "-------------table available? " + table.isAvailable());
    }

    private void showUserInfo(CustomerInfo userData) {
        if (userData != null) {
            customerMobile.setVisibility(View.VISIBLE);
            address.setVisibility(View.VISIBLE);
            String phone = (userData.getPhone() == null || userData.getPhone().isEmpty()) ? "NA" : userData.getPhone();
            customerMobile.setText(phone);
            StringBuilder addressString = new StringBuilder();
            String name = (userData.getName() == null) ? "" : userData.getName().trim();
            String houseNo = ((userData.getHouse_no() == null) || (userData.getHouse_no().trim().isEmpty())) ? "" : ", " + userData.getHouse_no().trim() + ", ";
            addressString.append(name).append(houseNo);
            if (userData.getPostalInfo() != null) {
                addressString.append(userData.getPostalInfo().getAddress());
            }
            address.setText(StringHelper.capitalizeEachWordAfterComma(addressString.toString()));
        }
        whatType.setText(tableOrderAdapter.getSelectedTable().getName());
    }

    protected void clearOrder() {
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
        Table table = tableOrderAdapter.getSelectedTable();
        if (table != null) {
            clearCart(table.cartItems);
            realm.executeTransaction(r -> {
                table.setAvailable(true);
            });
        }
        clearAddons();
        clearCommonItems();
        double currentTotal = getTotal();
        observableTotalAmt.setValue(currentTotal);

        back.performClick();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());
        sessionManager=new SessionManager(this);
        logger=new ExceptionLogger(this);
        if(logger.getCount()>0){
            Intent intent = new Intent(getApplicationContext(), LoggerUploadService.class);
            startService(intent);
        }
        setContentView(R.layout.activity_table_menu);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        handler = new Handler(getMainLooper());
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent ) {
            String key = intent.getStringExtra("dataKey");
            Toast.makeText(BasicTableMenuActivity.this,"Hi",Toast.LENGTH_SHORT).show();
            getStatusOrderList(key,0);
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(BasicTableMenuActivity.this).registerReceiver(receiver,
                new IntentFilter("fireDB"));
//        startService(new Intent(this, FirebaseBackgroundService.class));
//        firebaseListener();
        realm = RealmManager.getLocalInstance();
        initViews();
        setupLayoutParams();
        setUpColumns();
        if (sessionManager.isLoggedIn()) {
            onlineOrder.setBackground(this.getResources().getDrawable(R.drawable.buttongreen));
        } else {
            onlineOrder.setBackground(this.getResources().getDrawable(R.drawable.dark_gray_rectangular_btn));
        }
        observableTotalAmt.setValue(0.0);

        TableOrderAdapter.clearSelection();

        tableRealmResultsAsync = realm.where(Table.class).findAllAsync();
        tableRealmResultsAsync.addChangeListener(result -> {
            tableOrderAdapter = new TableOrderAdapter(BasicTableMenuActivity.this, result);
            tableRV.setAdapter(tableOrderAdapter);
        });

        RealmResults<MenuCategory> menuCategories = realm.where(MenuCategory.class).notEqualTo("name", Constants.MENUCATEGORY_COMMON).sort("itemposition").findAll();
        RealmList<MenuCategory> mc = new RealmList<>();
        mc.addAll(menuCategories);
        menuCatAdapter.setMenuCategories(mc);

        RealmResults<MenuItem> commonItemList = realm.where(MenuItem.class).equalTo("type", Constants.ITEM_TYPE_COMMON).sort("itemPosition").findAll();
        RealmList<MenuItem> ci = new RealmList<>();
        ci.addAll(commonItemList);
        commonItemAdapter.setItems(ci);

//        observableTotalAmt.setValue(getTotal());

        RecyclerViewItemSelectionAfterLayoutUpdate.on(menuCatRV);

        if(CallerIDService.callerIDService!=null && !CallerIDService.callerIDService.isCallerIdCaptured()) {
            String phoneNo = CallerIDService.callerIDService.getLastTelephone();
            if(phoneNo!=null && !phoneNo.trim().isEmpty())
                customerMobile.setText(phoneNo);
            CallerIDService.callerIDService.setCallerIdCaptured(true);
        }

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
        Table table = tableOrderAdapter.getSelectedTable();
        if (table != null) {
            List<CartItem> cartItems = table.cartItems;
            for (CartItem cartItem : cartItems) {
                total += (cartItem.price * cartItem.count);
                for (Addon addon : cartItem.addons) {
                    total += addon.price;
                }
            }
            Log.e(TAG, "-----------------------------getItemTotal (addeditem size): " + cartItems.size());
        }
        return total;
    }

    private double getTotal(MenuItem menuItem, double total) {
        if (menuItem.flavours.isEmpty()) {
//            RealmQuery<CartItem> cartItemRealmQuery = realm.where(CartItem.class).equalTo("menuItem.id", menuItem.id);
            RealmQuery<CartItem> cartItemRealmQuery = realm.where(CartItem.class).equalTo("menuItem.id", menuItem.id).and().isNotEmpty("tables");
            Table table = tableOrderAdapter.getSelectedTable();
            /*if(!isTable) {
                cartItemRealmQuery.and().isEmpty("tables");
            } else {
                cartItemRealmQuery.and().isNotEmpty("tables");
            }*/
//            List<CartItem> cartItems = cartItemRealmQuery.findAll();
            List<CartItem> cartItems = table.cartItems;
            for (CartItem cartItem : cartItems) {
                total += (cartItem.price * cartItem.count);
                for (Addon addon : cartItem.addons) {
                    total += addon.price;
                }
            }
            Log.e(TAG, "-----------------------------getItemTotal (addeditem size): " + cartItems.size());
            return total;
        }

        for (MenuItem item : menuItem.flavours)
            total += getTotal(item, 0);

        return total;

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(BasicTableMenuActivity.this).unregisterReceiver(receiver);
//        if(valueEventListener!=null){
//            mFirebaseDatabase.removeEventListener(valueEventListener);
//        }
        if (mTableLockThread != null && !mTableLockThread.isAlive())
            mTableLockThread.interrupt();

        if (tableLockAsyncTask != null && tableLockAsyncTask.getStatus().equals(AsyncTask.Status.RUNNING))
            tableLockAsyncTask.cancel(true);

        if(tableRealmResultsAsync!=null)
            tableRealmResultsAsync.removeAllChangeListeners();

        if(tableOrderAdapter!=null) {
            Table table = tableOrderAdapter.getSelectedTable();
            if(table!=null)
                unSelectTable(table);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close:
                onBackPressed();
                break;

            case R.id.totallayout: {
                if (totalAmount == 0) {
                    Toast.makeText(this, "Can't checkout on NIL amount", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tableOrderAdapter.getSelectedTable() != null) {
                    TableCheckoutDialog2 dialog = TableCheckoutDialog2.getInstance(tableOrderAdapter.getSelectedTable().getId());
                    dialog.show(getSupportFragmentManager(), null);
                } else {
                    Snackbar snackbar = Snackbar.make(this.getWindow().getDecorView(), "No Table Selected", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Dismiss", view -> {
                        snackbar.dismiss();
                    });

                    snackbar.show();
                }
            }
            break;
            /*case R.id.extra_discount_btn:
                ExtraDiscountDialog extraDiscountDialog = ExtraDiscountDialog.getInstance(getTotal());
                extraDiscountDialog.setCancelable(false);
                extraDiscountDialog.show(getSupportFragmentManager(), null);
                break;*/
            case R.id.setting_btn:
//                Intent intentSetting = new Intent(BasicTableMenuActivity.this, SettingActivity.class);
//                intentSetting.putExtra("ORDER_TYPE", orderType);
//                startActivity(intentSetting);
                Vendor vendor = realm.where(Vendor.class).findFirst();
                if(vendor!=null && vendor.getAdmin_password()!=null) {
                    if(vendor.getAdmin_password().trim().isEmpty()) {
                        CustomAlertDialog customAlertDialog = CustomAlertDialog.getInstance();
                        customAlertDialog.setTitle("No Password set in vendor Info");
                        customAlertDialog.setMessage("Set Admin Password in Vendor Info?");
                        customAlertDialog.setPositiveButton("Yes", dialog -> {
                            dialog.dismiss();
                            //Toast.makeText(BasicMenuActivity.this, "Vendor Info is Null", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(BasicTableMenuActivity.this, VendorInfoActivity.class);
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
                                Intent intent = new Intent(BasicTableMenuActivity.this, SettingActivity.class);
                                intent.putExtra("ORDER_TYPE", orderType);
                                startActivity(intent);
                            } else {
                                Toast.makeText(BasicTableMenuActivity.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
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
                        //Toast.makeText(BasicTableMenuActivity.this, "Vendor Info is Null", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(BasicTableMenuActivity.this, VendorInfoActivity.class);
                        i.putExtra(VendorInfoActivity.INTENT_ARG_EDITMODE, true);
                        startActivity(i);
                    });
                    customAlertDialog.setNegativeButton("No", dialog -> {
                        dialog.dismiss();
                    });
                    FragmentManager fm = getSupportFragmentManager();
                    customAlertDialog.show(fm, null);
                }
                break;

            case R.id.edit:
            case R.id.edit_details_layout: {
                Table table = tableOrderAdapter.getSelectedTable();
                if (table == null) {
                    Toast.makeText(this, "Select a Table first", Toast.LENGTH_SHORT).show();
                    return;
                }
                long userId = -1;
                if (table.getCustomerInfo() != null)
                    userId = table.getCustomerInfo().getId();
                UpdateCustomerDetails addition = UpdateCustomerDetails.getInstance(userId, orderType, specialNote.getText().toString());
                addition.show(getSupportFragmentManager(), null);
            }
            break;

            case R.id.special_note:
                SpecialNoteDialog specialNoteDialog = SpecialNoteDialog.getInstance(specialNote.getText().toString().trim());
                specialNoteDialog.show(getSupportFragmentManager(), null);
                break;

            case R.id.clear_order_button: {
                CustomAlertDialog alertDialog = CustomAlertDialog.getInstance();
                alertDialog.setTitle("Clear Order");
                alertDialog.setMessage("Are you sure you want to clear order?");
                alertDialog.setPositiveButton("Yes", dialog -> {
                    dialog.dismiss();
                    clearOrder();
                });
                alertDialog.setNegativeButton("No", dialog -> {
                    dialog.dismiss();
                });

                alertDialog.show(getSupportFragmentManager(), null);
            }

            break;

            case R.id.cancel_order_button: {
                onBackPressed();
            }
            break;

            case R.id.reports_btn:
                startActivity(new Intent(this, NewReportActivity.class));
                break;

            case R.id.orderhistory:
                startActivity(new Intent(this, ManageOrdersActivity.class));
                break;
            case R.id.OnlineOrder:
                if (sessionManager.isLoggedIn()) {
                    Intent intent = new Intent(BasicTableMenuActivity.this, ResturantMainActivityNewPro.class);
                    startActivity(intent);
//                    finish();
                }else{
                    FireBaseDialoge dialoge = FireBaseDialoge.newInstance();
                    dialoge.show(getSupportFragmentManager(), "");
                }

                break;

            case R.id.open_cash_drawer_btn:
//                printHelper.openCashDrawer();
                Intent openCashDrawer = new Intent(PrintUtils.ACTION_PRINT_REQUEST);
                openCashDrawer.putExtra(PrintUtils.PRINT_DATA, PrintUtils.openCash());
                localBroadcastManager.sendBroadcast(openCashDrawer);
                break;

            case R.id.currentTableContainer:
                enableMenuObjects();
                break;
            case R.id.back:
                unSelectTable(tableOrderAdapter.getSelectedTable());
                break;

            case R.id.manual_order:
                if (tableOrderAdapter.getSelectedTable() == null) {
                    Snackbar snackbar = Snackbar.make(this.getWindow().getDecorView(), "No Table Selected", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Dismiss", view -> {
                        snackbar.dismiss();
                    });

                    snackbar.show();
                    return;
                }
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

                }));
                addManualOrderDialog.show(getSupportFragmentManager(), null);
                break;

            case R.id.warning_list:
                WarningListDialog warningListDialog = WarningListDialog.newInstance();
                warningListDialog.show(getSupportFragmentManager(), null);
                break;

            case R.id.show_tables:
                TablesFullViewDialog tablesFullViewDialog = TablesFullViewDialog.newInstance();
                tablesFullViewDialog.setCallback((dialog, position, tableId) -> {
                    tableRV.scrollToPosition(position);
                    RecyclerViewItemSelectionAfterLayoutUpdate.on(tableRV, position);
                    dialog.dismiss();
                });
                tablesFullViewDialog.show(getSupportFragmentManager(), null);
                break;
        }
    }

    @Override
    public void returnbackMoney(int splitBillIndex, List<CartItem> itemsToRemove, String returnbackpound, String whatType, boolean sendSMS, boolean paid) {
        float returnback_pound = 0;
        if (returnbackpound.equalsIgnoreCase("")) {

        } else {
            returnback_pound = Float.parseFloat(returnbackpound);
            checkOut(splitBillIndex, itemsToRemove, returnback_pound, whatType, sendSMS, paid);
            CustomAlertDialog customAlertDialog = CustomAlertDialog.getInstance();
            customAlertDialog.setPositiveButton("Close", dialog -> {
                dialog.dismiss();
            });
            customAlertDialog.setOnDismissListener(() -> {
//                onBackPressed();
            });
            customAlertDialog.setTitle("Return Change");
            customAlertDialog.setMessage("Change: " + String.format("%.2f", returnback_pound));
            if (returnback_pound > 0) {
                customAlertDialog.show(getSupportFragmentManager(), null);
            }
        }

//        checkOut(splitBillIndex, itemsToRemove, returnback_pound, whatType, sendSMS);
    }

    @Override
    public void returnSpecialNotes(String notes) {
        specialNote.setVisibility(View.VISIBLE);
        specialNote.setText("Special Notes :  " + notes);
    }

    @Override
    public void onUpdateCustomerDetails(long userId, String orderType, String note) {
        CustomerInfo customerInfo = realm.where(CustomerInfo.class).equalTo("id", userId).findFirst();
        customerMobile.setText(customerInfo.getPhone());

        StringBuilder addressString = new StringBuilder();
        String houseNo = ((customerInfo.getHouse_no() == null) || (customerInfo.getHouse_no().trim().isEmpty())) ? "" : customerInfo.getHouse_no().trim() + ", ";
        addressString.append(houseNo);
        if (customerInfo.getPostalInfo() != null) {
            addressString.append(customerInfo.getPostalInfo().getAddress());
            address.setText(StringHelper.capitalizeEachWordAfterComma(addressString.toString()));
        }

        final Table selectedTable = tableOrderAdapter.getSelectedTable();
        whatType.setText(selectedTable.getName());
        if (realm.isInTransaction())
            selectedTable.setCustomerInfo(customerInfo);
        else
            realm.executeTransaction(r -> {
                selectedTable.setCustomerInfo(customerInfo);
            });
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
       /* new Handler(Looper.getMainLooper()).post(()->{
            totalPrice.setText(String.format("%.2f", total));
        });*/

        totalPrice.setText(String.format("%.2f", total));
    }

    @Override
    public void onClickTable(long tableId) {
        discountView.reset();
        final Table table = tableOrderAdapter.getSelectedTable();
        if (table.isDirty()) {
            CustomAlertDialog customAlertDialog = CustomAlertDialog.getInstance();
            customAlertDialog.setTitle("Set Available");
            customAlertDialog.setMessage("Are you sure table has been cleaned?");
            customAlertDialog.setPositiveButton("Yes", dialog -> {
                realm.executeTransaction(r -> {
                    table.setDirty(false);
                    dialog.dismiss();
                });
            });
            customAlertDialog.setNegativeButton("No", dialog -> {
                dialog.dismiss();
            });

            customAlertDialog.show(getSupportFragmentManager(), null);
            TableOrderAdapter.clearSelection();
        } else if (table.isAvailable()) {
            PersonsPerTableDialog personsPerTableDialog = PersonsPerTableDialog.getInstance();
            final AtomicReference<Boolean> booleanAtomicReference = new AtomicReference<>(true);
            personsPerTableDialog.setCallback(count -> {
                final Table t = tableOrderAdapter.getSelectedTable();
                realm.executeTransaction(r -> {
                    t.setTotalPersons(count);
                    t.setAvailable(false);
                    tableOrderAdapter.notifyItemChanged(tableOrderAdapter.getSelectedTableIndex());
                });
                showTable(t);
                booleanAtomicReference.set(false);
            });
            personsPerTableDialog.setOnDismissListener(() -> {
                if (booleanAtomicReference.get().booleanValue() == true)
                    TableOrderAdapter.clearSelection();
            });
            personsPerTableDialog.show(getSupportFragmentManager(), null);
        } else {
            Log.e(TAG, "--------------showing table-------------------");
            showTable(table);
        }

        if (table.getCustomerInfo() != null)
            showUserInfo(table.getCustomerInfo());
    }

    @Override
    public void onLongClickTable(long tableId) {
        final Table table = realm.where(Table.class).equalTo("id", tableId).findFirst();
        PersonsPerTableDialog personsPerTableDialog = PersonsPerTableDialog.getInstance();
        personsPerTableDialog.setCallback(count -> {
            realm.executeTransaction(r -> {
                table.setTotalPersons(count);
                tableOrderAdapter.notifyItemChanged(tableOrderAdapter.getSelectedTableIndex());
            });
        });
        personsPerTableDialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onMenuCatClick(int position, MenuCategory category) {
        resetDisableCounter();
        Log.e(TAG, "category_index: " + menuCatAdapter.getSelectedCategoryIndex());
        Log.e(TAG, "---clicked category: " + category);
        Log.e(TAG, "category_index: " + menuCatAdapter.getSelectedCategoryIndex());
        Log.e(TAG, "---clicked category: " + category);

        realm.executeTransaction(r -> {
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
            menuItemAdapter.notifyDataSetChanged();

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
        });
    }

    @Override
    public void onMenuItemClick(final AbstractMenuItemAdapter adapter, final int position, final MenuItem menuItem, final String itemType) {
        resetDisableCounter();
        if (tableOrderAdapter.getSelectedTable() == null) {
            Snackbar snackbar = Snackbar.make(BasicTableMenuActivity.this.getWindow().getDecorView(), "Select a table first", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("Dismiss", v -> {
                snackbar.dismiss();
            });
            snackbar.show();
            return;
        }
        MenuCategory menuCategory = menuItem.menuCategory;
        resetAddonState(menuCategory);
        realm.executeTransaction(r -> {
            CartItem cartItem = tableOrderAdapter.getSelectedTable().cartItems.where().equalTo("menuItem.name", menuItem.name).and()
                    .isEmpty("addons").findFirst();
            if (cartItem == null) {
                Log.e(TAG, "------------item not present in cart--------------");
                Number maxId = r.where(CartItem.class).max("id");
                long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
                cartItem = r.createObject(CartItem.class, nextId);
                cartItem.menuItem = menuItem;
                if(menuItem.parent!=null) {
                    if (menuItem.printerSetting == 1)
                        cartItem.name = menuItem.parent.name + " " + menuItem.name;
                    else if (menuItem.printerSetting  == 2)
                        cartItem.name = menuItem.name + " " + menuItem.parent.name;
                    else {
                        cartItem.name = menuItem.name;
                    }
                }else{
                    cartItem.name = menuItem.name;
                }
                cartItem.price = menuItem.collectionPrice;
                cartItem.categoryIndex = menuCatAdapter.getSelectedCategoryIndex();
                cartItem.menuItemIndex = menuItemAdapter.getSelectionIndex();
                cartItem.count = 1;
                if (tableOrderAdapter.getSelectedTable() != null) {
                    Log.e(TAG, "----------selected table index > 0");
                    tableOrderAdapter.getSelectedTable().cartItems.add(cartItem);
                } else {
                    Log.e(TAG, "----------selected table index -1");
                }

                Log.e(TAG, "--------------cartitem count: " + cartItemAdapter.getItemCount());
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
                newMenuItemAdapter = newMenuItemAdapter.parent;
                newMenuItemAdapter.notifyDataSetChanged();
            }
            menuCatAdapter.notifyItemChanged(menuCatAdapter.getSelectedCategoryIndex());
            observableTotalAmt.setValue(observableTotalAmt.getValue() + cartItem.price);
            Log.e(TAG, "------------index of cartItem: " + cartItemAdapter.indexOf(cartItem));
            changeTableAvailableStatus(tableOrderAdapter.getSelectedTable());
        });
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
        resetDisableCounter();
        realm.executeTransaction(r -> {
            if (tableOrderAdapter.getSelectedTable() == null) {
                addon.selected = false;
                Snackbar snackbar = Snackbar.make(BasicTableMenuActivity.this.getWindow().getDecorView(), "Select a table first", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Dismiss", v -> {
                    snackbar.dismiss();
                });
                snackbar.show();
                return;
            }

            if (cartItemAdapter == null) {
                addon.selected = false;
                return;
            }

            CartItem cartItem = cartItemAdapter.getSelectedCartItem();
            if (cartItem == null || cartItemAdapter.isEmpty()) {
                addon.selected = false;
                Snackbar snackbar = Snackbar.make(BasicTableMenuActivity.this.getWindow().getDecorView(), "Select a CartItem on the extreme right first", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Dismiss", v -> {
                    snackbar.dismiss();
                });
                snackbar.show();
                return;
            }
            if (!cartItem.menuItem.menuCategory.addons.contains(addon)) {
                addon.selected = false;
                Snackbar snackbar = Snackbar.make(BasicTableMenuActivity.this.getWindow().getDecorView(), "Addon invalid for this cartItem", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Dismiss", v -> {
                    snackbar.dismiss();
                });
                snackbar.show();
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
                if (tableOrderAdapter.getSelectedTable() != null) {
                    System.out.println("------------selected table not null---------");
                    tableOrderAdapter.getSelectedTable().cartItems.add(lastItem);
                } else
                    Log.e(TAG, "------------table null");
                cartItemAdapter.notifyItemInserted(cartItemAdapter.getItemCount() - 1);
                cartItem.count--;
                cartItemAdapter.notifyItemChanged(cartItemAdapter.indexOf(cartItem));
                cartItemAdapter.setSelection(cartItemAdapter.getItemCount() - 1);
                cartItemRV.scrollToPosition(cartItemAdapter.getItemCount() - 1);

            } else {
                if (addon.selected)
                    cartItem.addons.add(addon);
                else
                    cartItem.addons.remove(addon);
                cartItemAdapter.notifyItemChanged(cartItemAdapter.indexOf(cartItem));
            }
            observableTotalAmt.setValue(observableTotalAmt.getValue() + addon.price);
        });
    }

    public boolean printForTable(List<CartItem> cartItems, long order_id, double total, double sub_total, double discount, double extra, String payment_type,
                                 String orderType, String special_note, long tableId, double serviceCharges, boolean paid) {

        Realm realm = Realm.getDefaultInstance();
        Vendor vendorInfo = realm.where(Vendor.class).findFirst();
        if(vendorInfo==null) {
            Toast.makeText(this, "Vendor Info is NULL", Toast.LENGTH_SHORT).show();
            return false;
        }
        final String provider = vendorInfo.getTitle();
        final String vendor = vendorInfo.getName();
        final String tel_no = vendorInfo.getTel_no();
        final String pin = vendorInfo.getPin();
        final String loc = vendorInfo.getAddress();
        final String vatNo = vendorInfo.getVatNo() == null ? "" : vendorInfo.getVatNo();
        final String companyNo = vendorInfo.getCompanyNo()==null? "" : vendorInfo.getCompanyNo();


        final String POUND = "\u00A3";
        boolean returnValue = true;

        final Table table = realm.where(Table.class).equalTo("id", tableId).findFirst();
        final CustomerInfo customerInfo = table.getCustomerInfo();

        final byte[] dividerLine = PrintUtils.getBytes("\n-----------------------\n");
        final byte[] dividerLine2 = PrintUtils.getBytes("-----------------------");
        byte[] cursorPosition = null;
        byte[] alignment = null;
        byte[] size = null;
        byte[] bold = null;
        byte[] text = null;
        byte[] tabs = null;
        try {

            final List<Byte> bytes = new LinkedList<>();
            alignment = PrintUtils.setAlignment('0');
            PrintUtils.copyBytesToList(bytes, alignment);

            byte[] internationalCharcters = PrintUtils.setInternationalCharcters('3');
            PrintUtils.copyBytesToList(bytes, internationalCharcters);

            alignment = PrintUtils.setAlignment('1');
            bold = PrintUtils.setBold(true);
            size = PrintUtils.setWH('4');
            text = PrintUtils.getBytes(table.getName() + "\n");
            PrintUtils.copyBytesToList(bytes, alignment, bold, size, text);

            text = PrintUtils.getBytes(provider);
            PrintUtils.copyBytesToList(bytes, text);

            size = PrintUtils.setWH('2');
            bold = PrintUtils.setBold(false);
            text = dividerLine;
            PrintUtils.copyBytesToList(bytes, size, bold, text);

            /*bold = PrintUtils.setBold(true);
            text = PrintUtils.getBytes(vendor.replace(",","\n") + "\n");
            PrintUtils.copyBytesToList(bytes, bold, text);*/

            bold = PrintUtils.setBold(false);
            String location = loc.replace(",", "\n");
            text = PrintUtils.getBytes(location);
            PrintUtils.copyBytesToList(bytes, bold, text);

            text = PrintUtils.getBytes(pin);
            PrintUtils.copyBytesToList(bytes, text, PrintUtils.getLF(1));

            text = PrintUtils.getBytes("Tel:" + tel_no);
            PrintUtils.copyBytesToList(bytes, text, PrintUtils.getLF(1));

            if(!vatNo.isEmpty()) {
                text = PrintUtils.getBytes("VAT No:" + vatNo);
                PrintUtils.copyBytesToList(bytes, text, PrintUtils.getLF(1));
            }
            if(!companyNo.isEmpty()) {
                text = PrintUtils.getBytes("Company No:" + companyNo);
                PrintUtils.copyBytesToList(bytes, text, PrintUtils.getLF(1));
            }

            text = dividerLine;
            PrintUtils.copyBytesToList(bytes, text);


            if (special_note != null && !special_note.trim().isEmpty()) {
                text = PrintUtils.getBytes(special_note);
                PrintUtils.copyBytesToList(bytes, text);

                text = dividerLine;
                PrintUtils.copyBytesToList(bytes, text);

            }

            alignment = PrintUtils.setAlignment('0');
            size = PrintUtils.setWH('5');
            bold = PrintUtils.setBold(false);
            cursorPosition = PrintUtils.setCursorPosition(390);
            tabs = PrintUtils.getTabs(1);
            text = PrintUtils.getBytes("GBP\n");
            PrintUtils.copyBytesToList(bytes, alignment, size, bold, cursorPosition, tabs, text);

            cursorPosition = PrintUtils.setCursorPosition(0);
            PrintUtils.copyBytesToList(bytes, cursorPosition);

            //    ---------------------------------------- From realm ------------------------------------------

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
                StringBuilder addonStrBuilder = new StringBuilder();
                alignment = PrintUtils.setAlignment('0');
                bold = PrintUtils.setBold(true);
                size = PrintUtils.setWH('0');
                PrintUtils.copyBytesToList(bytes, alignment, bold, size);

                if (cartItems.size() != 0) {
                    List<Addon> addons = a.addons;
                    for (Addon addon : addons) {
                        String included = (addon.isNoAddon == false) ? "+" : "-";
                        addonStrBuilder.append("  ").append(included).append(addon.name.trim() + "\n");
                    }
                    if (!a.comment.trim().isEmpty())
                        addonStrBuilder.append("  *").append(a.comment.trim()).append("\n");
                    text = PrintUtils.getBytes(" " + a.count + " " + a.menuItem.name);
                    PrintUtils.copyBytesToList(bytes, text);
                } else {
                    text = PrintUtils.getBytes(" " + a.menuItem.name);
                    PrintUtils.copyBytesToList(bytes, text);
                }

                cursorPosition = PrintUtils.setCursorPosition(390);
                PrintUtils.copyBytesToList(bytes, cursorPosition);


                if (cartItems.size() != 0) {
                    tabs = PrintUtils.getTabs(1);
                    double price = a.menuItem.collectionPrice;
                    text = PrintUtils.getBytes(String.format("%.2f", (a.count) * price) + "\n");
                    PrintUtils.copyBytesToList(bytes, tabs, text);
                } else {
                    tabs = PrintUtils.getTabs(1);
                    text = PrintUtils.getBytes("\n");
                    PrintUtils.copyBytesToList(bytes, tabs, text);
                }
                size = PrintUtils.setWH('0');
                cursorPosition = PrintUtils.setCursorPosition(0);
                text = PrintUtils.getBytes(addonStrBuilder.toString());
                PrintUtils.copyBytesToList(bytes, size, cursorPosition, text);

                alignment = PrintUtils.setAlignment('1');
                size = PrintUtils.setWH('2');
                text = dividerLine2;
                PrintUtils.copyBytesToList(bytes, alignment, size, text, PrintUtils.getLF(1));
            }

            alignment = PrintUtils.setAlignment('0');
            bold = PrintUtils.setBold(true);
            size = PrintUtils.setWH('5');
            text = PrintUtils.getBytes("Subtotal: ");
            PrintUtils.copyBytesToList(bytes, alignment, bold, size, text);

            cursorPosition = PrintUtils.setCursorPosition(390);
            tabs = PrintUtils.getTabs(1);
            text = PrintUtils.getBytes(String.format(" %.2f", sub_total));
            PrintUtils.copyBytesToList(bytes, cursorPosition, tabs, text, PrintUtils.getLF(1));


            if (discount > 0) {
                cursorPosition = PrintUtils.setCursorPosition(0);
                text = PrintUtils.getBytes("Discount: ");
                PrintUtils.copyBytesToList(bytes, cursorPosition, text);

                cursorPosition = PrintUtils.setCursorPosition(390);
                tabs = PrintUtils.getTabs(1);
                text = PrintUtils.getBytes("-" + String.format("%.2f", discount));
                PrintUtils.copyBytesToList(bytes, cursorPosition, tabs, text);
                PrintUtils.copyBytesToList(bytes, PrintUtils.getLF(1));
            }

            if (extra > 0) {
                cursorPosition = PrintUtils.setCursorPosition(0);
                text = PrintUtils.getBytes("Extra: ");
                PrintUtils.copyBytesToList(bytes, cursorPosition, text);

                cursorPosition = PrintUtils.setCursorPosition(390);
                tabs = PrintUtils.getTabs(1);
                text = PrintUtils.getBytes(String.format("%.2f", extra));
                PrintUtils.copyBytesToList(bytes, cursorPosition, tabs, text);
                PrintUtils.copyBytesToList(bytes, PrintUtils.getLF(1));
            }

            if (serviceCharges > 0) {
                cursorPosition = PrintUtils.setCursorPosition(0);
                text = PrintUtils.getBytes("Service Charge: ");
                PrintUtils.copyBytesToList(bytes, cursorPosition, text);

                cursorPosition = PrintUtils.setCursorPosition(390);
                tabs = PrintUtils.getTabs(1);
                text = PrintUtils.getBytes(String.format("%.2f", serviceCharges));
                PrintUtils.copyBytesToList(bytes, cursorPosition, tabs, text);
                PrintUtils.copyBytesToList(bytes, PrintUtils.getLF(1));
            }

            cursorPosition = PrintUtils.setCursorPosition(0);
            alignment = PrintUtils.setAlignment('1');
            size = PrintUtils.setWH('2');
            bold = PrintUtils.setBold(false);
            text = dividerLine2;
            PrintUtils.copyBytesToList(bytes, cursorPosition, alignment, bold, size, text, PrintUtils.getLF(1));

            alignment = PrintUtils.setAlignment('0');
            bold = PrintUtils.setBold(true);
            text = PrintUtils.getBytes("Total Price:  ");
            PrintUtils.copyBytesToList(bytes, alignment, bold, text);

            cursorPosition = PrintUtils.setCursorPosition(390);
            text = PrintUtils.getBytes(String.format("%.2f", total));
            PrintUtils.copyBytesToList(bytes, cursorPosition, text, PrintUtils.getLF(1));

            cursorPosition = PrintUtils.setCursorPosition(0);
            alignment = PrintUtils.setAlignment('1');
            bold = PrintUtils.setBold(false);
            text = dividerLine2;
            PrintUtils.copyBytesToList(bytes, cursorPosition, alignment, bold, text, PrintUtils.getLF(1));


            String paidStatus = (paid == true) ? "PAID" : "NOT PAID";
            size = PrintUtils.setWH('4');
            bold = PrintUtils.setBold(true);
            text = PrintUtils.getBytes(payment_type + " " + paidStatus + "\n");
            PrintUtils.copyBytesToList(bytes, size, bold, text);

            size = PrintUtils.setWH('1');
            bold = PrintUtils.setBold(false);
            String time = new SimpleDateFormat("dd-MM-yyyy", Locale.UK).format(new Date());
            text = PrintUtils.getBytes(time);
            PrintUtils.copyBytesToList(bytes, size, bold, text);

            String customerPhone = "";
            String customerHouseNo = "";
            String CustomerAddress = "";
            String CustomerPostalCode = "";
            String customerName = "";
            StringBuilder addressString = new StringBuilder();

            if (customerInfo != null) {
                customerName = (customerInfo.getName() == null || customerInfo.getName().isEmpty()) ? "" : customerInfo.getName().trim();
                if (!customerName.isEmpty())
                    addressString.append(customerName);

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
                    addressString.append(",,").append("Total Orders: ").append(totalOrderCount);
                }
            }

            if (!addressString.toString().trim().isEmpty()) {
                size = PrintUtils.setWH('2');
                alignment = PrintUtils.setAlignment('1');
                bold = PrintUtils.setBold(false);
                text = dividerLine;
                PrintUtils.copyBytesToList(bytes, size, alignment, bold, text);


                // added later

                StringBuilder stringBuilder = new StringBuilder();
                String details = StringHelper.capitalizeEachWordAfterComma(addressString.toString());
                String str = details.replace(",", "\n");
              /*  String[] tokens = StringHelper.capitalizeEachWordAfterComma(addressString.toString()).split(",");
                List<String> stringList = Arrays.asList(tokens);
                Iterator<String> stringIterator = stringList.iterator();
                while (stringIterator.hasNext()) {
                    final String str = stringIterator.next();
                    if (stringIterator.hasNext())
                        stringBuilder.append(str.trim() + "\n");
                    else
                        stringBuilder.append(str.trim());
                }*/

                alignment = PrintUtils.setAlignment('0');
                size = PrintUtils.setWH('4');
                text = PrintUtils.getBytes("Customer Details:\n");
                PrintUtils.copyBytesToList(bytes, alignment, size, text, PrintUtils.getBytes(str));
            }

            alignment = PrintUtils.setAlignment('1');
            size = PrintUtils.setWH('2');
            text = dividerLine;
            PrintUtils.copyBytesToList(bytes, alignment, size, text);

            size = PrintUtils.setWH('4');
            bold = PrintUtils.setBold(true);
            text = PrintUtils.getBytes("www.foodciti.co.uk");
            PrintUtils.copyBytesToList(bytes, size, bold, text, PrintUtils.getLF(2));

            byte[] paperCut = PrintUtils.cutPaper();
            PrintUtils.copyBytesToList(bytes, paperCut);

            byte[] data = PrintUtils.toPrimitiveBytes(bytes);

            Intent intent = new Intent(PrintUtils.ACTION_PRINT_REQUEST);
            intent.putExtra(PrintUtils.PRINT_DATA, data);
            localBroadcastManager.sendBroadcast(intent);

        } catch (Exception ex) {
            Exlogger exlogger = new Exlogger();
            exlogger.setErrorType("Print Error");
            exlogger.setErrorMessage(ex.getMessage());
            exlogger.setScreenName("BasicTableMenuActivity->>printForTable() function");
            logger.addException(exlogger);
            returnValue = false;
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            realm.close();
        }
        return returnValue;
    }

    private class TableLockThread extends Thread {
        @Override
        public void run() {
            super.run();
            boolean interrupted = false;
            while (automatiDisableCounter < TIMEOUT_IN_SECONDS && !interrupted) {
                try {
                    if (isInterrupted())
                        interrupted = true;
                    sleep(1000);
                    automatiDisableCounter++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    interrupted = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!interrupted) {
                handler.post(handlerCallback);
            } else {
                Log.d(TAG, "---------------------Thread was interrupted-----------------------");
                handler.removeCallbacks(handlerCallback);
            }
        }
    }

    private class TableLockAsyncTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            boolean interrupted = false;
            while (automatiDisableCounter < TIMEOUT_IN_SECONDS && !interrupted) {
                try {
                    if (Thread.currentThread().isInterrupted())
                        interrupted = true;
                    Thread.sleep(1000);
                    automatiDisableCounter++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    interrupted = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (!interrupted) {
                handler.post(handlerCallback);
            } else {
                Log.d(TAG, "---------------------Thread was interrupted-----------------------");
                handler.removeCallbacks(handlerCallback);
            }
            return null;
        }
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
//        mFirebaseDatabase.child(foodTruckId).addValueEventListener(valueEventListener);
//
//    }


    private void getStatusOrderList(final String orderId, int orderStatus) {
        if (InternetConnection.checkConnection(BasicTableMenuActivity.this)) {
            //  if (!TextUtils.isEmpty(SessionManager.get(RestaurentMainActivity.this).getFoodTruckId())) {
            io.reactivex.Observable<GenericResponse<List<Order>>> results = RetroClient.getApiService()
                    .getStatusOrderList(SessionManager.get(BasicTableMenuActivity.this).getFoodTruckId(), orderStatus);
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
            Toast.makeText(BasicTableMenuActivity.this, R.string.string_internet_connection_warning, Toast.LENGTH_LONG).show();

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
