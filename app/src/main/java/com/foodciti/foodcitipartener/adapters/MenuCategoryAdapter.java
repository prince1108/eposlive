package com.foodciti.foodcitipartener.adapters;

import android.app.Activity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.realm_entities.CartItem;
import com.foodciti.foodcitipartener.realm_entities.MenuCategory;
import com.foodciti.foodcitipartener.realm_entities.MenuItem;
import com.foodciti.foodcitipartener.utils.CommonMethods;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.Preferences;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;

public class MenuCategoryAdapter extends AbstractMenuCategoryAdapter {

    public MenuCategoryAdapter(Activity context, List<MenuCategory> menuCategories) {
        super(context, menuCategories);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder holder, int position) {
        MenuCategory category = menuCategories.get(position);
        holder.name.setText(category.getName());
        if (category.color != -1) {
            CommonMethods.setGradientDrawable(context, holder.itemView, category.color);
        } else {
            int color = ContextCompat.getColor(context, R.color.category_unselected);
            CommonMethods.setGradientDrawable(context, holder.itemView, color);
        }

        if (position == selectedCategoryIndex) {
            int color = ContextCompat.getColor(context, R.color.colorVividTangerine);
            CommonMethods.setGradientDrawable(context, holder.itemView, color);
        } else {
            if (category.color != -1) {
                CommonMethods.setGradientDrawable(context, holder.itemView, category.color);
            } else {
                int color = ContextCompat.getColor(context, R.color.category_unselected);
                CommonMethods.setGradientDrawable(context, holder.itemView, color);
            }
        }

        setCounter(holder.counter, category.id);

        int size = sharedPreferences.getInt(Preferences.MENU_ICON_SIZE, Constants.ICON_SMALL);
        switch (size) {
            case Constants.ICON_SMALL:
                holder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, context.getResources().getDimension(R.dimen.icon_small));
                break;
            case Constants.ICON_MEDIUM:
                holder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, context.getResources().getDimension(R.dimen.icon_medium));
                break;
            case Constants.ICON_LARGE:
                holder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, context.getResources().getDimension(R.dimen.icon_large));
                break;
        }
    }

    @Override
    protected void setCounter(TextView countTV, long categoryId) {
        if(realm.isClosed())
            realm = RealmManager.getLocalInstance();
        MenuCategory category = realm.where(MenuCategory.class).equalTo("id", categoryId).findFirstAsync();
        RealmChangeListener<MenuCategory> realmChangeListener = new RealmChangeListener<MenuCategory>() {
            @Override
            public void onChange(MenuCategory menuCategory) {
//                int totalItems = getTotalItems(menuCategory, realm);
                int totalItems = getTotalItems2(menuCategory, realm);
                if (totalItems > 0) {
                    countTV.setVisibility(View.VISIBLE);
                    countTV.setText(Integer.toString(totalItems));
                } else {
                    countTV.setVisibility(View.GONE);
                }
                category.removeChangeListener(this);
                changeListenerHashSet.remove(this);
            }
        };
        changeListenerHashSet.add(realmChangeListener);
        category.addChangeListener(realmChangeListener);
    }

    private int getTotalItems(MenuCategory category, Realm realm) {
        int count = 0;
        for (MenuItem m : category.menuItems) {
            if (m.type.equals(Constants.ITEM_TYPE_COMMON))
                continue;
            count += getItemSize(m, 0, realm);
        }
        return count;
    }

    private int getItemSize(MenuItem menuItem, int size, Realm realm) {
        if (menuItem.flavours.isEmpty()) {
            Number sizeNum = null;
            sizeNum = realm.where(CartItem.class).equalTo("menuItem.id", menuItem.id).and().isEmpty("tables").and().equalTo("isRestored", isRestored).sum("count");
            return sizeNum == null ? 0 : sizeNum.intValue();
        }

        for (MenuItem item : menuItem.flavours)
            size += getItemSize(item, size, realm);

        return size;
    }

    private int getTotalItems2(MenuCategory category, Realm realm) {
        List<MenuItem> menuItemList = category.menuItems.where().notEqualTo("type", Constants.ITEM_TYPE_COMMON).findAll();
        List<Long> longList = new ArrayList<>(menuItemList.size());
        for(MenuItem menuItem: menuItemList) {
            getItemIds(menuItem, longList);
        }
        Long[] arr = longList.toArray(new Long[longList.size()]);
        return getItemSize2(arr, realm);
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
        sizeNum = realm.where(CartItem.class).in("menuItem.id", itemIds).and()
                .isEmpty("tables").and().equalTo("isRestored", isRestored)
                .sum("count");

        return sizeNum == null ? 0 : sizeNum.intValue();
    }
}
