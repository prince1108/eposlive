package com.foodciti.foodcitipartener.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.AbstractMenuCategoryAdapter;
import com.foodciti.foodcitipartener.adapters.AbstractMenuItemAdapter;
import com.foodciti.foodcitipartener.adapters.AddonAdapter;
import com.foodciti.foodcitipartener.adapters.CartItemAdapter;
import com.foodciti.foodcitipartener.adapters.MenuCategoryAdapter;
import com.foodciti.foodcitipartener.adapters.MenuCategoryAdapterForTable;
import com.foodciti.foodcitipartener.adapters.MenuItemAdapterForTable;
import com.foodciti.foodcitipartener.adapters.NewMenuItemAdapter;
import com.foodciti.foodcitipartener.realm_entities.Addon;
import com.foodciti.foodcitipartener.realm_entities.CartItem;
import com.foodciti.foodcitipartener.realm_entities.CustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.MenuCategory;
import com.foodciti.foodcitipartener.realm_entities.MenuItem;
import com.foodciti.foodcitipartener.realm_entities.OrderAddon;
import com.foodciti.foodcitipartener.realm_entities.OrderCustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.OrderMenuCategory;
import com.foodciti.foodcitipartener.realm_entities.OrderMenuItem;
import com.foodciti.foodcitipartener.realm_entities.OrderPostalInfo;
import com.foodciti.foodcitipartener.realm_entities.PostalInfo;
import com.foodciti.foodcitipartener.realm_entities.Purchase;
import com.foodciti.foodcitipartener.realm_entities.PurchaseEntry;
import com.foodciti.foodcitipartener.realm_entities.RemarkType;
import com.foodciti.foodcitipartener.realm_entities.Table;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.RealmManager;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class EditOrder extends AppCompatActivity implements AbstractMenuCategoryAdapter.Callback, AbstractMenuItemAdapter.Callback,
        AddonAdapter.AddonClicklistener, CartItemAdapter.CartItemClickListener {

    private static final String TAG = "EditOrder";
    private RecyclerView categoryRV, menuItemRV, commonItemRV, addonRV, noaddonRV, cartRV;
    private AbstractMenuCategoryAdapter menuCategoryAdapter;
    private AbstractMenuItemAdapter newMenuItemAdapter, commonItemAdapter;
    private AddonAdapter addonAdapter, noAddonAdapter;
    private CartItemAdapter cartItemAdapter;
    private Realm realm;
    private List<MenuCategory> menuCategories;
    private List<MenuItem> menuItems, commonItems;
    private List<Addon> addons, noAddons;
    private String orderType;
    private Purchase order;

    private TextView total;
    private View close;

    private Handler handler;

    private RealmResults<CartItem> cartItems;

    private Spinner paymentModeSpinner;
    private Table table;

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Log.e(TAG, "----------OnGlobalLayoutListener-------------");
            if (!menuCategories.isEmpty())
                categoryRV.getChildAt(0).performClick();
            categoryRV.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
        }
    };

    /*private List<MenuItem> restoreTempItems() {
        realm.executeTransaction(r->{
            RealmResults<OrderMenuItem> orderMenuItems = realm.where(OrderMenuItem.class).equalTo("type", Constants.ITEM_TYPE_TEMP).findAll();
            for(OrderMenuItem orderMenuItem: orderMenuItems) {

                Number maxCartId = r.where(CartItem.class).max("id");
                long nextCartId = (maxCartId == null) ? 1 : maxCartId.longValue() + 1;
                CartItem cartItem = r.createObject(CartItem.class, nextCartId);
                List<Addon> addons = new ArrayList<>();
                for(OrderAddon orderAddon: orderTuple.getOrderAddons()) {
                    Addon addon = r.where(Addon.class).equalTo("name", orderAddon.name).findFirst();
                    if(addon!=null)
                        addons.add(addon);
                }
                cartItem.addons.addAll(addons);
                cartItem.count = orderTuple.getCount();

                Number maxId = realm.where(MenuItem.class).max("id");
                long nextId = (maxId==null)? 1: maxId.longValue()+1;
                MenuItem menuItem = realm.createObject(MenuItem.class, nextId);
            }
        });


    }*/
    private void restoreCart() {
        realm.executeTransaction(r -> {
            Log.e(TAG, "----orderTuple size: " + order.getPurchaseEntries().size());
            for (PurchaseEntry orderTuple : order.getPurchaseEntries()) {
                Log.e(TAG, "------------orderTuple: " + orderTuple.getOrderMenuItem().name);
                Number maxCartId = r.where(CartItem.class).max("id");
                long nextCartId = (maxCartId == null) ? 1 : maxCartId.longValue() + 1;
                CartItem cartItem = r.createObject(CartItem.class, nextCartId);
                List<Addon> addons = new ArrayList<>();
                for(OrderAddon orderAddon: orderTuple.getOrderAddons()) {
                    Addon addon = r.where(Addon.class).equalTo("name", orderAddon.name).findFirst();
                    if(addon!=null)
                        addons.add(addon);
                }
                cartItem.addons.addAll(addons);
                cartItem.count = orderTuple.getCount();

                MenuItem menuItem=null;
                if(orderTuple.getOrderMenuItem().type.equals(Constants.ITEM_TYPE_TEMP)) {
                    OrderMenuItem orderMenuItem = orderTuple.getOrderMenuItem();
                    Number maxId = realm.where(MenuItem.class).max("id");
                    long nextId = (maxId==null)? 1: maxId.longValue()+1;
                    menuItem = realm.createObject(MenuItem.class, nextId);
                    menuItem.type=orderMenuItem.type;
                    menuItem.name=orderMenuItem.name;
                    menuItem.collectionPrice=orderMenuItem.collectionPrice;
                    menuItem.price=orderMenuItem.price;
                    menuItem.deliveryPrice=orderMenuItem.deliveryPrice;

                }else {
                    menuItem = r.where(MenuItem.class).equalTo("name", orderTuple.getOrderMenuItem().name).findFirst();
                }
                if(menuItem!=null) {
                    cartItem.menuItem = menuItem;
                    cartItem.comment = orderTuple.getAdditionalNote();
                    cartItem.menuItemIndex = menuItems.indexOf(menuItem);
                    if (orderType.equals(Constants.TYPE_COLLECTION))
                        cartItem.price = menuItem.collectionPrice;
                    else if (orderType.equals(Constants.TYPE_DELIVERY))
                        cartItem.price = menuItem.deliveryPrice;
                    else if (orderType.equals(Constants.TYPE_TABLE)) {
                        cartItem.price = menuItem.collectionPrice;
                    }
                    cartItem.isRestored = true;
                }
                if(table!=null)
                table.cartItems.add(cartItem);
            }
        });


/*        RealmResults<MenuItem> menuItems = realm.where(MenuItem.class).sort("id", Sort.ASCENDING).findAll();
        for (MenuItem mi : menuItems) {
            List<CartItem> cartItemList = new ArrayList<>();
            for (CartItem cartItem : cartItems) {
                if (cartItem.menuItem != null) {
                    if (cartItem.menuItem.id == mi.id)
                        cartItemList.add(cartItem);
                }
            }
            menuItemListMap.put(mi, cartItemList);
        }*/

        cartItemAdapter.notifyDataSetChanged();
        total.setText(String.format("%.2f", getTotal()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        realm = RealmManager.getLocalInstance();
    }

    @Override
    protected void onDestroy() {
//        RealmManager.closeRealmFor(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        realm.executeTransaction(r -> {
//            order.getPurchaseEntries().clear();
            RealmResults<PurchaseEntry> orderTuples = r.where(PurchaseEntry.class).equalTo("purchases.id", order.getId()).findAll();
            Log.e(TAG, "----------total orderTuples: " + orderTuples.size());
            Log.e(TAG, "-------customerOrderId: " + order.getId());
            orderTuples.deleteAllFromRealm();
            for (CartItem cartItem : cartItemAdapter.getCartItems()) {
                Log.e(TAG, "---------saving: " + cartItem.menuItem.name);
                Number maxOrderTuple = r.where(PurchaseEntry.class).max("id");
                long nextOrderTupleId = (maxOrderTuple == null) ? 1 : maxOrderTuple.longValue() + 1;
                PurchaseEntry orderTuple = r.createObject(PurchaseEntry.class, nextOrderTupleId);
                orderTuple.getOrderAddons().addAll(toOrderAddons(cartItem.addons));
                orderTuple.setOrderMenuItem(toOrderMenuItem(cartItem.menuItem));
                orderTuple.setCount(cartItem.count);
//                orderTuple.setCustomerOrderId(order.getId());
                orderTuple.setAdditionalNote(cartItem.comment);
                order.getPurchaseEntries().add(orderTuple);
            }
            order.setTotal(getTotal());
            order.setPaymentMode(paymentModeSpinner.getSelectedItem().toString());
        });

        clearCart();
        clearTempItems();
    }

    private void setSpinner(String paymentMode) {
//        List<RemarksStatusBean> remarksStatusList = new ArrayList<>();
        RealmResults<RemarkType> remarkTypes = realm.where(RemarkType.class).findAll();

       /* for (int i = 0; i < remarksType.length; i++) {
            remarksStatusList.add(new RemarksStatusBean(remarksType[i], remarksColor[i]));
        }*/

       String[] strings = {Constants.PAYMENT_TYPE_CARD, Constants.PAYMENT_TYPE_CASH};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, strings);

        paymentModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                remarksET.setBackgroundColor(remarkTypes.get(position).getColor());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        paymentModeSpinner.setAdapter(spinnerAdapter);
        if(paymentMode.equals(strings[0]))
            paymentModeSpinner.setSelection(0);
        else
            paymentModeSpinner.setSelection(1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order);
        handler = new Handler(Looper.getMainLooper());
        initViews();
        realm = Realm.getDefaultInstance();
        menuCategories = new ArrayList<>();
        menuItems = new ArrayList<>();
        addons = new ArrayList<>();
        noAddons = new ArrayList<>();
        commonItems = new ArrayList<>();
        menuCategories.addAll(realm.where(MenuCategory.class).notEqualTo("name", Constants.MENUCATEGORY_COMMON).findAll());


        Log.e(TAG, "---------menucategories--------: " + menuCategories);

        long orderId = getIntent().getLongExtra("ORDER_ID", -1);
        if (orderId != -1)
            order = realm.where(Purchase.class).equalTo("id", orderId).findFirst();
        orderType = order.getOrderType();
        long tableId = order.getTableId();
        if(tableId!=-1) {
            table = realm.where(Table.class).equalTo("id", tableId).findFirst();
        }

        Log.d(TAG, "----------------ORDER TYPE: "+orderType);
        if(orderType.equals(Constants.TYPE_TABLE)) {
            menuCategoryAdapter = new MenuCategoryAdapterForTable(this, table, menuCategories);
            menuCategoryAdapter.setRestored(true);
            categoryRV.setAdapter(menuCategoryAdapter);
            categoryRV.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);

            newMenuItemAdapter = new MenuItemAdapterForTable(this, table, menuItems, Constants.ITEM_TYPE_MENU, orderType);
            newMenuItemAdapter.setRestored(true);
            menuItemRV.setAdapter(newMenuItemAdapter);

            commonItems.addAll(realm.where(MenuItem.class).equalTo("name", Constants.MENUCATEGORY_COMMON).findAll());
            commonItemAdapter = new MenuItemAdapterForTable(this, table, commonItems, Constants.ITEM_TYPE_COMMON, orderType);
            commonItemAdapter.setRestored(true);
            commonItemRV.setAdapter(commonItemAdapter);

        } else {
            menuCategoryAdapter = new MenuCategoryAdapter(this, menuCategories);
            menuCategoryAdapter.setRestored(true);
            categoryRV.setAdapter(menuCategoryAdapter);
            categoryRV.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);

            newMenuItemAdapter = new NewMenuItemAdapter(this, menuItems, Constants.ITEM_TYPE_MENU, orderType);
            newMenuItemAdapter.setRestored(true);

            menuItemRV.setAdapter(newMenuItemAdapter);

            commonItems.addAll(realm.where(MenuItem.class).equalTo("name", Constants.MENUCATEGORY_COMMON).findAll());
            commonItemAdapter = new NewMenuItemAdapter(this, commonItems, Constants.ITEM_TYPE_COMMON, orderType);
            commonItemAdapter.setRestored(true);
            commonItemRV.setAdapter(commonItemAdapter);
        }


        addonAdapter = new AddonAdapter(this, addons, false);
        addonRV.setAdapter(addonAdapter);

        noAddonAdapter = new AddonAdapter(this, noAddons, true);
        noaddonRV.setAdapter(noAddonAdapter);

        cartItems = realm.where(CartItem.class).equalTo("isRestored", true).findAllAsync();
        cartItems.addChangeListener(rs -> {
            List<CartItem> cartItems = new ArrayList<>(rs);
            cartItemAdapter = new CartItemAdapter(this, cartItems);
            cartRV.setAdapter(cartItemAdapter);
        });

        restoreCart();

        setSpinner(order.getPaymentMode());

    }

    private void initViews() {
        categoryRV = findViewById(R.id.categoryRV);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        categoryRV.setLayoutManager(layoutManager);
        categoryRV.setHasFixedSize(true);

        menuItemRV = findViewById(R.id.menuItemRV);
        RecyclerView.LayoutManager menuItemLM = new GridLayoutManager(this, 3);
        menuItemRV.setLayoutManager(menuItemLM);
        menuItemRV.setHasFixedSize(true);

        commonItemRV = findViewById(R.id.commonItemRV);
        RecyclerView.LayoutManager commonItemLM = new GridLayoutManager(this, 3);
        commonItemRV.setLayoutManager(commonItemLM);
        commonItemRV.setHasFixedSize(true);

        addonRV = findViewById(R.id.addonRV);
        RecyclerView.LayoutManager addonLM = new GridLayoutManager(this, 3);
        addonRV.setLayoutManager(addonLM);
        addonRV.setHasFixedSize(true);

        noaddonRV = findViewById(R.id.noaddonRV);
        RecyclerView.LayoutManager noAddonLM = new GridLayoutManager(this, 3);
        noaddonRV.setLayoutManager(noAddonLM);
        noaddonRV.setHasFixedSize(true);

        cartRV = findViewById(R.id.cartRV);
        RecyclerView.LayoutManager cartItemLM = new LinearLayoutManager(this);
        cartRV.setLayoutManager(cartItemLM);
        cartRV.setHasFixedSize(true);

        total = findViewById(R.id.right_panel_total);
        close = findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        paymentModeSpinner = findViewById(R.id.payment_mode);
    }

    private void showSelectedAddons(final CartItem cartItem) {
        for (Addon a : addons) {
            Log.e(TAG, "addon name: " + a.name + ", is contained: " + addons.contains(a));
            if (cartItem.addons.contains(a))
                a.selected = true;
            else
                a.selected = false;

        }
        addonAdapter.notifyDataSetChanged();
        for (Addon a : noAddons) {
            if (cartItem.addons.contains(a))
                a.selected = true;
            else
                a.selected = false;
        }
        noAddonAdapter.notifyDataSetChanged();
    }

    private void clearCart() {
        realm.executeTransaction(r -> {
//                r.delete(CartItem.class);
            cartItems.deleteAllFromRealm();
            cartItemAdapter.notifyDataSetChanged();
        });

    }

    private void clearTempItems() {
        realm.executeTransaction(r->{
            RealmResults<MenuItem> menuItems = r.where(MenuItem.class).equalTo("type", Constants.ITEM_TYPE_TEMP).findAll();
            menuItems.deleteAllFromRealm();
        });
    }

    private void clearAddons() {
        for (Addon addon : addons)
            addon.selected = false;
        addonAdapter.notifyDataSetChanged();

        for (Addon addon : noAddons)
            addon.selected = false;
        noAddonAdapter.notifyDataSetChanged();
    }

    private void clearMenuItem(MenuItem menuItem) {
        if (menuItem.flavours.size() == 0) {
            return;
        }
        for (MenuItem item : menuItem.flavours)
            clearMenuItem(item);
    }

    private void clearCommonItems() {
        for (MenuItem commonItem : realm.where(MenuItem.class).equalTo("type", Constants.ITEM_TYPE_COMMON).findAll())
            clearCommonItems(commonItem);
        commonItemAdapter.notifyDataSetChanged();
    }

    private void clearCommonItems(MenuItem menuItem) {
        if (menuItem.flavours.size() == 0) {
            return;
        }
        for (MenuItem item : menuItem.flavours)
            clearCommonItems(item);
    }

    @Override
    public void onMenuCatClick(int position, MenuCategory category) {
        menuItems.clear();
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
                if (!m.type.equals(Constants.ITEM_TYPE_COMMON))
                    menuItems.add(m);
            }
            newMenuItemAdapter.notifyDataSetChanged();
//        selectedCategory = category;

            addons.clear();
            noAddons.clear();
            for (Addon addon : category.addons) {
                if (addon.isNoAddon)
                    noAddons.add(addon);
                else
                    addons.add(addon);
            }

            Log.e(TAG, "Order type: " + orderType);

            addonAdapter.notifyDataSetChanged();
            noAddonAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onMenuCatLongClick(int position, MenuCategory category) {

    }

    private void resetAddons(MenuCategory menuCategory) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (Addon addon : addons) {
                    addon.selected = false;
                }
                addonAdapter.notifyDataSetChanged();
                for (Addon addon : noAddons) {
                    addon.selected = false;
                }
                noAddonAdapter.notifyDataSetChanged();
            }
        });
    }

    private double getTotal() {
        Log.d(TAG, "-----------------orderType: "+orderType);
        /*double total = 0;
        for (MenuCategory menuCategory : menuCategories) {
            for (MenuItem item : menuCategory.menuItems) {
                if (item.type.equals(Constants.ITEM_TYPE_COMMON))
                    continue;
                total += getItemTotal(item, 0);
                Log.e(TAG, "---------------total: " + total);
            }
        }
        for (MenuItem commonItem : realm.where(MenuItem.class).equalTo("type", Constants.ITEM_TYPE_COMMON).findAll())
            total += getItemTotal(commonItem, 0);

        return total;*/
        double total = 0;

//        RealmQuery<CartItem> cartItemRealmQuery = realm.where(CartItem.class).and().isEmpty("tables");
        RealmQuery<CartItem> cartItemRealmQuery = realm.where(CartItem.class);
        if(orderType.equals(Constants.TYPE_TABLE)) {
            Table table = realm.where(Table.class).equalTo("id", order.getTableId()).findFirst();
            if(table!=null) {
                List<CartItem> cartItems = table.cartItems.where().equalTo("isRestored", true).findAll();
                for (CartItem cartItem : cartItems) {
                    total += (cartItem.price * cartItem.count);
                    for (Addon addon : cartItem.addons) {
                        total += addon.price;
                    }
                }
                Log.e(TAG, "-----------------------------getItemTotal (addeditem size): " + cartItems.size());
            }
        } else {
            cartItemRealmQuery.and().isEmpty("tables").and().equalTo("isRestored", true);
            List<CartItem> cartItems = cartItemRealmQuery.findAll();
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

    /*private double getItemTotal(MenuItem menuItem, double total) {
        if (menuItem.flavours.isEmpty()) {
            List<CartItem> cartItems = (menuItemListMap.get(menuItem) == null) ? new ArrayList<CartItem>() : menuItemListMap.get(menuItem);
            for (CartItem cartItem : cartItems) {
//                total += (cartItem.price);
                total += (cartItem.price * cartItem.count);
                for (Addon addon : cartItem.addons) {
                    total += addon.price;
                }
            }
            Log.e(TAG, "-----------------------------getItemTotal (addeditem size): " + cartItems.size());
            return total;
        }
        for (MenuItem item : menuItem.flavours)
            total += getItemTotal(item, 0);
        return total;
    }*/

    @Override
    public void onMenuItemClick(AbstractMenuItemAdapter adapter, int position, MenuItem menuItem, String itemType) {
        MenuCategory menuCategory = menuItem.menuCategory;
        resetAddons(menuCategory);
        realm.executeTransaction(r -> {

            CartItem cartItem = null;
            /*if(orderType.equals(Constants.TYPE_TABLE)) {
                cartItem = r.where(CartItem.class).equalTo("menuItem.name", menuItem.name).and().isEmpty("addons").and()
                        .isNotEmpty("tables").findFirst();
            }
            else {
                cartItem = r.where(CartItem.class).equalTo("menuItem.name", menuItem.name).and().isEmpty("addons").and()
                        .isEmpty("tables").findFirst();
            }*/

            if(table==null) {
                cartItem = r.where(CartItem.class).equalTo("menuItem.name", menuItem.name).and().isEmpty("addons").and()
                        .equalTo("isRestored", true).findFirst();
            } else {
                cartItem = table.cartItems.where().equalTo("menuItem.name", menuItem.name).and()
                        .isEmpty("addons").findFirst();
            }

            if (cartItem == null) {
                Log.e(TAG, "------------item not present in cart--------------");
                Number maxId = r.where(CartItem.class).max("id");
                long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
                cartItem = r.createObject(CartItem.class, nextId);
                cartItem.menuItem = menuItem;
                cartItem.name = menuItem.name;
                cartItem.price = (orderType.equals(Constants.TYPE_COLLECTION)) ? menuItem.collectionPrice : menuItem.deliveryPrice;
                cartItem.categoryIndex = menuCategoryAdapter.getSelectedCategoryIndex();
                cartItem.menuItemIndex = newMenuItemAdapter.getSelectionIndex();
                cartItem.count = 1;
                cartItem.isRestored = true;
                if(table!=null) {
                    table.cartItems.add(cartItem);
                }

                cartItemAdapter.notifyItemInserted(cartItemAdapter.getItemCount() - 1);
                cartItemAdapter.setSelection(cartItemAdapter.getItemCount() - 1);
                cartRV.scrollToPosition(cartItemAdapter.getItemCount() - 1);
            } else {
                Log.e(TAG, "------------item present in cart--------------");
                cartItem.count++;
                int index = cartItemAdapter.indexOf(cartItem);
                cartItemAdapter.notifyItemChanged(index);
                cartItemAdapter.setSelection(cartItemAdapter.getItemCount() - 1);
                cartRV.scrollToPosition(cartItemAdapter.getItemCount() - 1);
            }

            int categoryIndex = menuCategories.indexOf(menuItem.menuCategory);
            Log.e(TAG, "----------------------category Index: " + categoryIndex);
            adapter.notifyItemChanged(position);
            AbstractMenuItemAdapter newMenuItemAdapter = adapter;
            while (newMenuItemAdapter.parent != null) {
                newMenuItemAdapter = newMenuItemAdapter.parent;
                newMenuItemAdapter.notifyDataSetChanged();
            }
            menuCategoryAdapter.notifyItemChanged(menuCategoryAdapter.getSelectedCategoryIndex());

            String currentTotal = String.format("%.2f", getTotal());
            total.setText(currentTotal);
        });
    }

    @Override
    public void onRearrange(MenuItem item1, MenuItem item2) {

    }

    @Override
    public void onClickAddon(Addon addon) {
        Log.e(TAG, "-----------------addon: " + addon);
        realm.executeTransaction(r -> {
            CartItem cartItem = cartItemAdapter.getSelectedCartItem();
            if (cartItem == null || cartItemAdapter.isEmpty()) {
                addon.selected = false;
                Snackbar snackbar = Snackbar.make(EditOrder.this.getWindow().getDecorView(), "Select a CartItem on the extreme right first", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Dismiss", v -> {
                    snackbar.dismiss();
                });
                snackbar.show();
                return;
            }
            if (!cartItem.menuItem.menuCategory.addons.contains(addon)) {
                addon.selected = false;
                Snackbar snackbar = Snackbar.make(EditOrder.this.getWindow().getDecorView(), "Addon invalid for this cartItem", Snackbar.LENGTH_INDEFINITE);
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
                lastItem.categoryIndex = menuCategoryAdapter.getSelectedCategoryIndex();
                lastItem.menuItemIndex = newMenuItemAdapter.getSelectionIndex();
                lastItem.count = 1;
                lastItem.addons.add(addon);
                lastItem.isRestored = true;
                cartItemAdapter.notifyItemInserted(cartItemAdapter.getItemCount() - 1);
                cartItem.count--;
                cartItemAdapter.notifyItemChanged(cartItemAdapter.indexOf(cartItem));
                selectAddedCartItem(cartItemAdapter.getItemCount() - 1);

            } else {
                if (addon.selected)
                    cartItem.addons.add(addon);
                else
                    cartItem.addons.remove(addon);
                cartItemAdapter.notifyItemChanged(cartItemAdapter.indexOf(cartItem));
            }
            String currentTotal = String.format("%.2f", getTotal());
            total.setText(currentTotal);
        });
    }

    private void selectAddedCartItem(int position) {
        handler.post(() -> {
            if (!cartRV.hasPendingAdapterUpdates()) {
                Log.e(TAG, "---------added item index: " + position);

                // This will call last item by calling "performClick()" of view.
                cartRV.getChildAt(position).performClick();
            } else {
                selectAddedCartItem(position);
            }
        });
    }

    @Override
    public void onCartItemClick(CartItem cartItem) {
        MenuCategory menuCategory = cartItem.menuItem.menuCategory;
        if (menuCategory == null || menuCategory.getName().equals(Constants.MENUCATEGORY_COMMON)) {
            Log.e(TAG, "-------------menucategory is null---------------");
            commonItemAdapter.setSelection(commonItems.indexOf(cartItem.menuItem));
            return;
        }
        Log.e(TAG, "-------------menucategory index: " + menuCategories.indexOf(menuCategory));
        int indexOfMenuCategory = menuCategories.indexOf(menuCategory);
        if (indexOfMenuCategory >= 0)
            categoryRV.getChildAt(indexOfMenuCategory).performClick();
        realm.executeTransaction(r -> {
            showSelectedAddons(cartItem);
        });
    }

    @Override
    public void onCartItemLongClick(CartItem cartItem) {

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
    public void onClickSpecialNote(long cartItemId, int position) {

    }

    private void updateCounters(MenuItem menuItem) {
        if (menuItem.type.equals(Constants.ITEM_TYPE_FLAVOUR)) {
            MenuItem parent = menuItem;
            while (parent.parent != null) {
                parent = parent.parent;
            }
            if (parent.type.equals(Constants.ITEM_TYPE_COMMON))
                commonItemAdapter.notifyItemChanged(commonItems.indexOf(parent));
            else
                newMenuItemAdapter.notifyItemChanged(menuItems.indexOf(parent));
        } else if (menuItem.type.equals(Constants.ITEM_TYPE_COMMON)) {
            commonItemAdapter.notifyItemChanged(commonItems.indexOf(menuItem));
        } else
            newMenuItemAdapter.notifyItemChanged(menuItems.indexOf(menuItem));
        menuCategoryAdapter.notifyItemChanged(menuCategories.indexOf(menuItem.menuCategory));
        addonAdapter.notifyDataSetChanged();
        String currentTotal = String.format("%.2f", getTotal());
        total.setText(currentTotal);
    }

    private OrderCustomerInfo toOrderCustomerInfo(CustomerInfo customerInfo) {
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

    private OrderPostalInfo toOrderPostalInfo(PostalInfo postalInfo) {
        if(postalInfo==null)
            return null;
        Number maxId= realm.where(OrderPostalInfo.class).max("id");
        long nextId = (maxId==null)? 1: maxId.longValue()+1;
        OrderPostalInfo orderPostalInfo = realm.createObject(OrderPostalInfo.class, nextId);
        orderPostalInfo.setA_PostCode(postalInfo.getA_PostCode());
        orderPostalInfo.setAddress(postalInfo.getAddress());
        return orderPostalInfo;
    }

    private List<OrderAddon> toOrderAddons(List<Addon> addons) {
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

    private OrderMenuItem toOrderMenuItem(MenuItem menuItem) {
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


    private OrderMenuCategory toOrderMenuCategory(MenuCategory menuCategory) {
        if(menuCategory==null)
            return null;
        Number maxOrderMenuCategory = realm.where(OrderMenuCategory.class).max("id");
        long nextId = (maxOrderMenuCategory==null)? 1: maxOrderMenuCategory.longValue()+1;
        OrderMenuCategory orderMenuCategory = realm.createObject(OrderMenuCategory.class, nextId);
        orderMenuCategory.setName(menuCategory.getName());
        orderMenuCategory.orderAddons.addAll(toOrderAddons(menuCategory.addons));
        orderMenuCategory.orderAddons.addAll(toOrderAddons(menuCategory.noAddons));
        return  orderMenuCategory;
    }

}
