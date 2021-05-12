package com.foodciti.foodcitipartener.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.callbacks.GridItemTouchHelperCallback;
import com.foodciti.foodcitipartener.realm_entities.CartItem;
import com.foodciti.foodcitipartener.realm_entities.MenuItem;
import com.foodciti.foodcitipartener.realm_entities.Table;
import com.foodciti.foodcitipartener.utils.CommonMethods;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.Preferences;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;

public class MenuItemAdapterForTable extends AbstractMenuItemAdapter {
    private Table table;
    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public MenuItemAdapterForTable(Activity context, Table table, List<MenuItem> items, String itemType, String orderType) {
        super(context, items, itemType, orderType);
        this.table = table;
    }

    @Override
    protected void setCounter(TextView counterTV, long menuItemId) {
        MenuItem menuItem = realm.where(MenuItem.class).equalTo("id", menuItemId).findFirstAsync();
        RealmChangeListener<MenuItem> realmChangeListener = new RealmChangeListener<MenuItem>() {
            @Override
            public void onChange(MenuItem item) {
//                int totalItems = getItemSize(item, 0, realm);
                List<Long> longList = getItemIds(item, new ArrayList<>());
                Long[] arr = longList.toArray(new Long[longList.size()]);
                int totalItems = getItemSize2(arr, realm);
                if (totalItems > 0) {
                    counterTV.setVisibility(View.VISIBLE);
                    counterTV.setText(totalItems + "");
                } else {
                    counterTV.setVisibility(View.GONE);
                }
                menuItem.removeChangeListener(this);
                changeListenerHashSet.remove(this);
            }
        };

        changeListenerHashSet.add(realmChangeListener);
        menuItem.addChangeListener(realmChangeListener);
    }

    @Override
    protected void showFlavoursDialog(List<MenuItem> flavours, int position) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.menu_subcat);
        TextView title = dialog.findViewById(R.id.title);
        title.setText("Select Option");
        View close = dialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        RecyclerView recyclerView = dialog.findViewById(R.id.editFlavoursRV);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 4));
        MenuItemAdapterForTable menuItemAdapter = new MenuItemAdapterForTable(context, table, flavours, itemType, orderType);
        menuItemAdapter.setTable(table);
        menuItemAdapter.parent = this;
        GridItemTouchHelperCallback itemTouchHelperCallback1 = new GridItemTouchHelperCallback(menuItemAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(itemTouchHelperCallback1);
        touchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(menuItemAdapter);
        dialog.show();
    }

    private int getItemSize(MenuItem menuItem, int size, Realm realm) {
        if (menuItem.flavours.isEmpty()) {
            RealmQuery<CartItem> cartItemRealmQuery = realm.where(CartItem.class).equalTo("menuItem.id", menuItem.id);
            Number sizeNum = null;
            if (table == null) {
                sizeNum = null;
            } else {
                sizeNum = cartItemRealmQuery.and()
                        .isNotEmpty("tables").and().equalTo("tables.id", table.getId()).and().equalTo("isRestored", isRestored)
                        .sum("count");
            }
            return sizeNum == null ? 0 : sizeNum.intValue();
        }

        for (MenuItem item : menuItem.flavours)
            size += getItemSize(item, size, realm);

        return size;
    }

    private List<Long> getItemIds(MenuItem menuItem, List<Long> longList) {
        if(menuItem.flavours.isEmpty()) {
            longList.add(menuItem.id);
            return longList;
        }
        for (MenuItem item: menuItem.flavours)
            getItemIds(item, longList);
        return longList;
    }
    private int getItemSize2(Long[] itemIds, Realm realm) {
        Number sizeNum;
        if(table==null) {
            sizeNum = null;
        } else {
            sizeNum = realm.where(CartItem.class).in("menuItem.id", itemIds).and()
                    .isNotEmpty("tables").and().equalTo("tables.id", table.getId()).and().equalTo("isRestored", isRestored)
                    .sum("count");
        }
        return sizeNum == null ? 0 : sizeNum.intValue();
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        try {
            final MenuItem item = items.get(position);
            holder.itemName.setText(item.name);
            String price = "";
            price = String.format("%.2f", item.collectionPrice);
            if (item.flavours.size() > 0)
                holder.itemPrice.setText(R.string.option_hint);
            else {
                holder.itemPrice.setText(context.getString(R.string.pound_symbol) + " " + price);
            }
            if (item.color == -1) {
                realm.executeTransaction(r -> {
                    item.color = defaultColor;
                });
            }
//            holder.itemView.setBackgroundColor(item.color);
            CommonMethods.setGradientDrawable(context, holder.itemView, item.color);

            setCounter(holder.counter, item.id);


            int iconsize = sharedPreferences.getInt(Preferences.MENU_ICON_SIZE, Constants.ICON_SMALL);
            switch (iconsize) {
                case Constants.ICON_SMALL:
                    holder.itemName.setTextSize(TypedValue.COMPLEX_UNIT_SP, context.getResources().getDimension(R.dimen.icon_small));
                    break;
                case Constants.ICON_MEDIUM:
                    holder.itemName.setTextSize(TypedValue.COMPLEX_UNIT_SP, context.getResources().getDimension(R.dimen.icon_medium));
                    break;
                case Constants.ICON_LARGE:
                    holder.itemName.setTextSize(TypedValue.COMPLEX_UNIT_SP, context.getResources().getDimension(R.dimen.icon_large));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
