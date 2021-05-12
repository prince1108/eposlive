package com.foodciti.foodcitipartener.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

;
import com.foodciti.foodcitipartener.adapters.AbstractMenuCategoryAdapter;
import com.foodciti.foodcitipartener.adapters.AbstractMenuItemAdapter;
import com.foodciti.foodcitipartener.adapters.AddonAdapter;
import com.foodciti.foodcitipartener.adapters.CartItemAdapter;
import com.foodciti.foodcitipartener.adapters.MenuCategoryAdapter;
import com.foodciti.foodcitipartener.adapters.NewMenuItemAdapter;
import com.foodciti.foodcitipartener.adapters.callbacks.GridItemTouchHelperCallback;
import com.foodciti.foodcitipartener.dialogs.AddCustomerCommentDialog;
import com.foodciti.foodcitipartener.observables.ObservableNumber;
import com.foodciti.foodcitipartener.realm_entities.Addon;
import com.foodciti.foodcitipartener.realm_entities.CartItem;
import com.foodciti.foodcitipartener.realm_entities.CustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.MenuCategory;
import com.foodciti.foodcitipartener.realm_entities.MenuItem;
import com.foodciti.foodcitipartener.realm_entities.Order;
import com.foodciti.foodcitipartener.realm_entities.OrderAddon;
import com.foodciti.foodcitipartener.realm_entities.OrderCustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.OrderMenuCategory;
import com.foodciti.foodcitipartener.realm_entities.OrderMenuItem;
import com.foodciti.foodcitipartener.realm_entities.OrderPostalInfo;
import com.foodciti.foodcitipartener.realm_entities.OrderTuple;
import com.foodciti.foodcitipartener.realm_entities.PostalInfo;
import com.foodciti.foodcitipartener.realm_entities.Purchase;
import com.foodciti.foodcitipartener.realm_entities.PurchaseEntry;
import com.foodciti.foodcitipartener.services.SmsService;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.Preferences;
import com.foodciti.foodcitipartener.utils.RealmManager;
import com.foodciti.foodcitipartener.utils.SmsSender;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.foodciti.foodcitipartener.utils.AppConfig.MyPREFERENCES;

public abstract class AbstractMenuActivity extends AppCompatActivity implements AbstractMenuCategoryAdapter.Callback
        , AbstractMenuItemAdapter.Callback, CartItemAdapter.CartItemClickListener, SmsSender.MessageStatusListener
        , AddonAdapter.AddonClicklistener {
    protected final String TAG = this.getClass().getSimpleName();
    protected RecyclerView menuCatRV, menuItemRV, addonRV, commonItemRV, noAddonRV, cartItemRV;
    protected AbstractMenuCategoryAdapter menuCatAdapter;
    protected AbstractMenuItemAdapter menuItemAdapter, commonItemAdapter;
    protected CartItemAdapter cartItemAdapter;
    protected AddonAdapter addonAdapter, noAddonAdapter;
    protected double totalAmount, subTotalAmount, discount, extra;
    protected SmsSender smsSender;
    protected CustomerInfo customerInfo;
    protected String orderType;
    protected ObservableNumber<Double> observableTotalAmt = new ObservableNumber<>();

    protected SharedPreferences sharedPreferences;
    protected SharedPreferences.Editor editor;

    protected Realm realm;

    protected boolean isUsualOrderActive = false;
    private long userId;

    private RealmResults<MenuCategory> menuCategoryRealmResultsAsync = null;

    protected LocalBroadcastManager localBroadcastManager;

    private BroadcastReceiver smsDeliveredBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String number = intent.getStringExtra("c_no");
            Toast.makeText(AbstractMenuActivity.this, "DELIVERY Broadcast : " + number, Toast.LENGTH_LONG).show();
            Log.e("MSG_DEL", "" + number);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        editor = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
        userId = getIntent().getLongExtra("USER_DATA_ID", -1);
        orderType = getIntent().getStringExtra("ORDER_TYPE");
        editor.putString(Preferences.ORDER_TYPE, orderType);
        editor.commit();

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "------------------onPause called-----------------------");
        /*boolean restoredCartDestroyed = false;
        if (isUsualOrderActive)
            restoredCartDestroyed = destroyRestoredCart();*/
        super.onPause();
        localBroadcastManager.unregisterReceiver(smsDeliveredBroadcast);
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "------------------onResume called-----------------------");
        Log.d(TAG, "--------------orderType: "+orderType);
        smsSender = new SmsSender(this);
        observableTotalAmt.setValue(0.0);

        realm = RealmManager.getLocalInstance();
        customerInfo = null;
        if (userId != -1)
            customerInfo = realm.where(CustomerInfo.class).equalTo("id", userId).findFirst();

        localBroadcastManager.registerReceiver(smsDeliveredBroadcast, new IntentFilter(SmsService.SMS_DELIVERED_ACTION));

        super.onResume();
    }

    protected void setCategories(RecyclerView rv) {
        menuCatRV = rv;
        menuCatAdapter = new MenuCategoryAdapter(this, null);
        menuCatRV.setAdapter(menuCatAdapter);
        GridItemTouchHelperCallback categoryTouchHelperCallback = new GridItemTouchHelperCallback(menuCatAdapter);
        ItemTouchHelper categoryTouchHelper = new ItemTouchHelper(categoryTouchHelperCallback);
        categoryTouchHelper.attachToRecyclerView(menuCatRV);
    }

    protected void setMenuItems(RecyclerView rv) {
        menuItemRV = rv;
        menuItemAdapter = new NewMenuItemAdapter(this, null, Constants.ITEM_TYPE_MENU, orderType);
        menuItemRV.setAdapter(menuItemAdapter);
        GridItemTouchHelperCallback menuItemTouchHelperCallback = new GridItemTouchHelperCallback(menuItemAdapter);
        ItemTouchHelper menuItemtouchHelper = new ItemTouchHelper(menuItemTouchHelperCallback);
        menuItemtouchHelper.attachToRecyclerView(menuItemRV);
    }

    protected void setCommonItems(RecyclerView rv) {
        commonItemRV = rv;
        commonItemAdapter = new NewMenuItemAdapter(this, null, Constants.ITEM_TYPE_COMMON, orderType);
        commonItemRV.setAdapter(commonItemAdapter);
        GridItemTouchHelperCallback itemTouchHelperCallback2 = new GridItemTouchHelperCallback(commonItemAdapter);
        ItemTouchHelper touchHelper2 = new ItemTouchHelper(itemTouchHelperCallback2);
        touchHelper2.attachToRecyclerView(commonItemRV);
    }

    protected void setAddons(RecyclerView rv) {
        addonRV = rv;
        addonAdapter = new AddonAdapter(this, new ArrayList<>(), false);
        addonRV.setAdapter(addonAdapter);
        GridItemTouchHelperCallback itemTouchHelperCallback3 = new GridItemTouchHelperCallback(addonAdapter);
        ItemTouchHelper touchHelper3 = new ItemTouchHelper(itemTouchHelperCallback3);
        touchHelper3.attachToRecyclerView(addonRV);
    }

    protected void setNoAddons(RecyclerView rv) {
        noAddonRV = rv;
        noAddonAdapter = new AddonAdapter(this, new ArrayList<>(), true);
        noAddonRV.setAdapter(noAddonAdapter);
        GridItemTouchHelperCallback itemTouchHelperCallback4 = new GridItemTouchHelperCallback(noAddonAdapter);
        ItemTouchHelper touchHelper4 = new ItemTouchHelper(itemTouchHelperCallback4);
        touchHelper4.attachToRecyclerView(noAddonRV);
    }

    protected void setCartItems(RecyclerView rv) {
        cartItemRV = rv;
        cartItemRV.setLayoutManager(new LinearLayoutManager(this));
        cartItemRV.setHasFixedSize(true);

/*        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final CartItem cartItem = cartItemAdapter.getItemAt(position);
                AddCustomerCommentDialog addCustomerCommentDialog = AddCustomerCommentDialog.newInstance(cartItem.comment);
                addCustomerCommentDialog.setCallback(comment -> {
                    realm.executeTransaction(r -> {
                        cartItem.comment = comment;
                    });
                });
                addCustomerCommentDialog.setOnDismissListener(()->{
                    cartItemAdapter.notifyItemChanged(position);
                });
                addCustomerCommentDialog.show(getSupportFragmentManager(), null);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(cartItemRV);*/
    }


    protected void showSelectedAddons(final CartItem cartItem) {
        for (Addon a : addonAdapter.getAddons()) {
            a.selected = cartItem.addons.contains(a);

        }
        addonAdapter.notifyDataSetChanged();
        for (Addon a : noAddonAdapter.getAddons()) {
            a.selected = cartItem.addons.contains(a);
        }
        noAddonAdapter.notifyDataSetChanged();
    }

    private void addonStateTransaction() {
        for (Addon addon : addonAdapter.getAddons()) {
            addon.selected = false;
        }
        addonAdapter.notifyDataSetChanged();
        for (Addon addon : noAddonAdapter.getAddons()) {
            addon.selected = false;
        }
        noAddonAdapter.notifyDataSetChanged();
    }
    protected void resetAddonState(MenuCategory menuCategory) {
        if(realm.isInTransaction()) {
            addonStateTransaction();
        } else {
            realm.executeTransaction(r -> {
                addonStateTransaction();
            });
        }
    }


    protected void clearAddons() {
        if (realm.isInTransaction()) {
            for (Addon addon : addonAdapter.getAddons())
                addon.selected = false;
            addonAdapter.notifyDataSetChanged();

            for (Addon addon : noAddonAdapter.getAddons())
                addon.selected = false;
            noAddonAdapter.notifyDataSetChanged();
        } else {
            realm.executeTransaction(r -> {
                for (Addon addon : addonAdapter.getAddons())
                    addon.selected = false;
                addonAdapter.notifyDataSetChanged();

                for (Addon addon : noAddonAdapter.getAddons())
                    addon.selected = false;
                noAddonAdapter.notifyDataSetChanged();
            });
        }
    }

    protected void clearMenuItem(MenuItem menuItem) {
        if (menuItem.flavours.size() == 0) {
            return;
        }
        for (MenuItem item : menuItem.flavours)
            clearMenuItem(item);
    }

    protected void clearCommonItems() {
        for (MenuItem commonItem : realm.where(MenuItem.class).equalTo("type", Constants.ITEM_TYPE_COMMON).findAll())
            clearCommonItems(commonItem);
        commonItemAdapter.notifyDataSetChanged();
    }

    protected void clearCommonItems(MenuItem menuItem) {
        if (menuItem.flavours.size() == 0) {
            return;
        }
        for (MenuItem item : menuItem.flavours)
            clearCommonItems(item);
    }


    protected void clearCart() {
        realm.executeTransaction(r -> {
            cartItemAdapter.getCartItems().clear();
            cartItemAdapter.notifyDataSetChanged();
            RealmResults<CartItem> realmResults = r.where(CartItem.class).isEmpty("tables").findAll();
            realmResults.deleteAllFromRealm();
            menuCatAdapter.notifyDataSetChanged();
        });
    }

    protected void createOrderHistory(final List<CartItem> cartItems, final String paymentMode, double deliveryCharges, double serviceCharges) {
        Log.e(TAG, "----------------------------discount: " + discount);
        realm.executeTransaction(r -> {
            Number maxCustomerOrderId = realm.where(Order.class).max("id");
            long nextCustomerOrderId = (maxCustomerOrderId == null) ? 1 : maxCustomerOrderId.longValue() + 1;
            Order order = realm.createObject(Order.class, nextCustomerOrderId);
            for (CartItem c : cartItems) {
                Number maxOrderTuple = realm.where(OrderTuple.class).max("id");
                long nextOrderTupleId = (maxOrderTuple == null) ? 1 : maxOrderTuple.longValue() + 1;
                OrderTuple orderTuple = realm.createObject(OrderTuple.class, nextOrderTupleId);
                orderTuple.getAddons().addAll(c.addons);
                orderTuple.setMenuItem(c.menuItem);
                orderTuple.setCount(c.count);
                orderTuple.setCustomerOrderId(nextCustomerOrderId);
                orderTuple.setPrice(c.price);
                orderTuple.setAdditionalNote(c.comment);
                order.getOrderTuples().add(orderTuple);
            }
            order.setDeliveryCharges(deliveryCharges);
            order.setServiceCharges(serviceCharges);
            order.setExtra(extra);
            order.setDiscount(discount);
            order.setSubTotal(subTotalAmount);
            order.setTotal(totalAmount);
            order.setTimestamp(new Date().getTime());
            order.setCustomerInfo(customerInfo);
            order.setOrderType(orderType);
            order.setPaymentMode(paymentMode);
        });
    }

    protected void createOrderHistory2(List<CartItem> cartItems, String paymentMode, double deliveryCharges, double serviceCharges, boolean paid) {
        realm.executeTransaction(r->{
            Number maxPurchaseId = realm.where(Purchase.class).max("id");
            long nextPuchaseId = (maxPurchaseId==null)? 1: maxPurchaseId.longValue()+1;
            Purchase purchase = r.createObject(Purchase.class, nextPuchaseId);
            for(CartItem c: cartItems) {
                Number maxPurchaseEntry = r.where(PurchaseEntry.class).max("id");
                long nextPurchaseEntryId = (maxPurchaseEntry==null)? 1: maxPurchaseEntry.longValue()+1;
                PurchaseEntry purchaseEntry = r.createObject(PurchaseEntry.class, nextPurchaseEntryId);
                purchaseEntry.getOrderAddons().addAll(toOrderAddons(c.addons));
                purchaseEntry.setOrderMenuItem(toOrderMenuItem(c.menuItem));
                purchaseEntry.setCount(c.count);
                purchaseEntry.setPrice(c.price);
                purchaseEntry.setAdditionalNote(c.comment);
                purchase.getPurchaseEntries().add(purchaseEntry);
            }
            purchase.setDeliveryCharges(deliveryCharges);
            purchase.setServiceCharges(serviceCharges);
            purchase.setExtra(extra);
            purchase.setDiscount(discount);
            purchase.setSubTotal(subTotalAmount);
            purchase.setTotal(totalAmount);
            purchase.setTimestamp(new Date().getTime());
            purchase.setOrderTimeStamp(new Date());
            purchase.setOrderCustomerInfo(toOrderCustomerInfo(customerInfo));
            purchase.setOrderType(orderType);
            purchase.setPaymentMode(paymentMode);
            purchase.setPaid(paid);
        });
    }

    protected OrderCustomerInfo toOrderCustomerInfo(CustomerInfo customerInfo) {
        if(customerInfo==null)
            return null;

        Number maxId= realm.where(OrderCustomerInfo.class).max("id");
        long nextId = (maxId==null)? 1: maxId.longValue()+1;
        OrderCustomerInfo orderCustomerInfo = realm.createObject(OrderCustomerInfo.class, nextId);
        orderCustomerInfo.setName(customerInfo.getName());
        orderCustomerInfo.setHouse_no(customerInfo.getHouse_no());
        orderCustomerInfo.setPhone(customerInfo.getPhone());
        orderCustomerInfo.setOrderPostalInfo(toOrderPostalInfo(customerInfo.postalInfo));
        orderCustomerInfo.setRemarks(customerInfo.getRemarks());
        orderCustomerInfo.setRemarkStatus(customerInfo.getRemarkStatus());
        return orderCustomerInfo;
    }

    protected OrderPostalInfo toOrderPostalInfo(PostalInfo postalInfo) {
        if(postalInfo==null)
            return null;
        Number maxId= realm.where(OrderPostalInfo.class).max("id");
        long nextId = (maxId==null)? 1: maxId.longValue()+1;
        OrderPostalInfo orderPostalInfo = realm.createObject(OrderPostalInfo.class, nextId);
        orderPostalInfo.setA_PostCode(postalInfo.getA_PostCode());
        orderPostalInfo.setAddress(postalInfo.getAddress());
        return orderPostalInfo;
    }

    protected List<OrderAddon> toOrderAddons(List<Addon> addons) {
        List<OrderAddon> orderAddons = new ArrayList<>();
        for(Addon addon: addons) {
            Number maxOrderAddon = realm.where(OrderAddon.class).max("id");
            long nextOrderAddonId = (maxOrderAddon == null) ? 1 : maxOrderAddon.longValue() + 1;
            OrderAddon orderAddon = realm.createObject(OrderAddon.class, nextOrderAddonId);
            orderAddon.color = addon.color;
            orderAddon.isNoAddon = addon.isNoAddon;
            orderAddon.name = addon.name;
            orderAddon.price = addon.price;
            orderAddons.add(orderAddon);
        }
        return orderAddons;
    }

    protected OrderMenuItem toOrderMenuItem(MenuItem menuItem) {
        Number maxOrderMenuItem = realm.where(OrderMenuItem.class).max("id");
        long nextOrderMenuiItemId = (maxOrderMenuItem==null)? 1: maxOrderMenuItem.longValue()+1;
        OrderMenuItem orderMenuItem = realm.createObject(OrderMenuItem.class, nextOrderMenuiItemId);
        orderMenuItem.collectionPrice = menuItem.collectionPrice;
        orderMenuItem.deliveryPrice = menuItem.deliveryPrice;
        orderMenuItem.name = menuItem.name;
        orderMenuItem.price = menuItem.price;
        orderMenuItem.type = menuItem.type;
        OrderMenuCategory orderMenuCategory = toOrderMenuCategory(menuItem.menuCategory);
        if(orderMenuCategory!=null)
            orderMenuCategory.orderMenuItems.add(orderMenuItem);
        return orderMenuItem;
    }


    protected OrderMenuCategory toOrderMenuCategory(MenuCategory menuCategory) {
        if(menuCategory==null)
            return null;
        Number maxOrderMenuCategory = realm.where(OrderMenuCategory.class).max("id");
        long nextId = (maxOrderMenuCategory==null)? 1: maxOrderMenuCategory.longValue()+1;
        OrderMenuCategory orderMenuCategory = realm.createObject(OrderMenuCategory.class, nextId);
        orderMenuCategory.setName(menuCategory.getName());
        orderMenuCategory.setPrintOrder(menuCategory.getPrintOrder());
        orderMenuCategory.orderAddons.addAll(toOrderAddons(menuCategory.addons));
        orderMenuCategory.orderAddons.addAll(toOrderAddons(menuCategory.noAddons));
        return  orderMenuCategory;
    }


    protected String currentDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy \n HH:mm:ss");
        String currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }

  public abstract void updateCounters(MenuItem menuItem);
//  public abstract double getTotal();

    private void updateTotal(double total) {
        totalAmount = total;

    }

    /*protected double getTotal(boolean isTable) {
        double total = 0;
        for (MenuCategory menuCategory : menuCatAdapter.getMenuCategories()) {
            for (MenuItem item : menuCategory.menuItems) {
                if (item.type.equals(Constants.ITEM_TYPE_COMMON))
                    continue;
                total += getItemTotal(item, 0, isTable);
                Log.e(TAG, "---------------total: " + total);
            }
        }
        for (MenuItem commonItem : realm.where(MenuItem.class).equalTo("type", Constants.ITEM_TYPE_COMMON).findAll())
            total += getItemTotal(commonItem, 0, isTable);

        return total;
    }

    protected double getItemTotal(MenuItem menuItem, double total, boolean isTable) {
        if(menuItem.flavours.isEmpty()) {
            RealmQuery<CartItem> cartItemRealmQuery = realm.where(CartItem.class).equalTo("menuItem.id", menuItem.id);
            if(!isTable) {
                cartItemRealmQuery.and().isEmpty("tables");
            } else {
                cartItemRealmQuery.and().isNotEmpty("tables");
            }
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
            total += getItemTotal(item, 0, isTable);

        return total;

    }


    protected void restoreCart(long customerId, boolean isTable) {
        clearCart();
        Log.d(TAG, "----------------------inside rstoreCart------------------------");
        realm.executeTransaction(r -> {
            RealmQuery<Order> orderRealmQuery = r.where(Order.class);
            Order order = orderRealmQuery.equalTo("customerInfo.id", customerId).and().equalTo("id", orderRealmQuery.max("id").longValue()).findFirst();
            if (order == null) {
                Log.e(TAG, "-----------------order is null----------------");
                return;
            }
            for (OrderTuple orderTuple : order.getOrderTuples()) {
                Log.e(TAG, "------------orderTuple: " + orderTuple.getMenuItem().name);
                Number maxCartId = r.where(CartItem.class).max("id");
                long nextCartId = (maxCartId == null) ? 1 : maxCartId.longValue() + 1;
                CartItem cartItem = r.createObject(CartItem.class, nextCartId);
                cartItem.addons.addAll(orderTuple.getAddons());
                cartItem.count = orderTuple.getCount();
                cartItem.menuItem = orderTuple.getMenuItem();
                cartItem.comment = orderTuple.getAdditionalNote();
                cartItem.menuItemIndex = menuItemAdapter.indexOf(orderTuple.getMenuItem());
                cartItem.categoryIndex = menuCatAdapter.getSelectedCategoryIndex();
                if (orderType.equals(Constants.TYPE_COLLECTION))
                    cartItem.price = orderTuple.getMenuItem().collectionPrice;
                else if (orderType.equals(Constants.TYPE_DELIVERY))
                    cartItem.price = orderTuple.getMenuItem().deliveryPrice;
                else if (orderType.equals(Constants.TYPE_TABLE)) {
                    cartItem.price = orderTuple.getMenuItem().collectionPrice;
                }
                cartItem.isRestored = true;
            }
            RealmResults<CartItem> realmResults = r.where(CartItem.class).equalTo("isRestored", true).findAll();
            RealmList<CartItem> cartItemRealmList = new RealmList<>();
            cartItemRealmList.addAll(realmResults);
            Log.e(TAG, "----------------------cartitem size after rstore: " + realmResults.size());
            cartItemAdapter.setCartItems(cartItemRealmList);
        });


        RealmResults<MenuItem> menuItems = realm.where(MenuItem.class).sort("id", Sort.ASCENDING).findAll();
        for (MenuItem mi : menuItems) {
            List<CartItem> cartItemList = new ArrayList<>();
            for (CartItem cartItem : cartItemAdapter.getCartItems()) {
                if (cartItem.menuItem != null) {
                    if (cartItem.menuItem.id == mi.id)
                        cartItemList.add(cartItem);
                }
            }
//            menuItemListMap.put(mi, cartItemList);
        }

        cartItemAdapter.notifyDataSetChanged();
        menuItemAdapter.notifyDataSetChanged();
        observableTotalAmt.setValue(getTotal(isTable));
    }

*/

    @Override
    public void onMenuCatLongClick(int position, MenuCategory category) {

    }


    @Override
    public void onRearrange(MenuItem item1, MenuItem item2) {

    }

    @Override
    public void onCartItemClick(CartItem cartItem) {
        MenuCategory menuCategory = cartItem.menuItem.menuCategory;
        if (menuCategory == null || menuCategory.getName().equals(Constants.MENUCATEGORY_COMMON)) {
            Log.e(TAG, "-------------menucategory is null---------------");
            commonItemAdapter.setSelection(commonItemAdapter.indexOf(cartItem.menuItem));
            return;
        }
        Log.e(TAG, "-------------menucategory index: " + menuCatAdapter.indexOf(menuCategory));
        int indexOfMenuCategory = menuCatAdapter.indexOf(menuCategory);
        if (indexOfMenuCategory >= 0) {
            menuCatAdapter.setSelection(indexOfMenuCategory);
            onMenuCatClick(indexOfMenuCategory, menuCatAdapter.getSelectedMenuCategory());
        }

        realm.executeTransaction(r -> {
            showSelectedAddons(cartItem);
        });
    }

    @Override
    public void onCartItemLongClick(CartItem cartItem) {

    }

    @Override
    public void onClickSpecialNote(long cartItemId, int position) {
        final CartItem cartItem = cartItemAdapter.getItemAt(position);
        AddCustomerCommentDialog addCustomerCommentDialog = AddCustomerCommentDialog.newInstance(cartItem.comment);
        addCustomerCommentDialog.setCallback(comment -> {
            realm.executeTransaction(r -> {
                cartItem.comment = comment;
            });
        });
        addCustomerCommentDialog.setOnDismissListener(()->{
            cartItemAdapter.notifyItemChanged(position);
        });
        addCustomerCommentDialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onMessageDelivered(String number) {
        Toast.makeText(this, "SMS to " + number + " is delivered", Toast.LENGTH_SHORT).show();
        try {
            smsSender.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    protected boolean destroyRestoredCart() {
        AtomicReference<Boolean> booleanAtomicReference = new AtomicReference<>(false);
        realm.executeTransaction(r -> {
            cartItemAdapter.getCartItems().clear();
            cartItemAdapter.notifyDataSetChanged();
            RealmResults<CartItem> realmResults = r.where(CartItem.class).equalTo("isRestored", true).findAll();
            realmResults.deleteAllFromRealm();
            menuCatAdapter.notifyDataSetChanged();
            booleanAtomicReference.set(true);
        });
        return booleanAtomicReference.get();
    }
}
