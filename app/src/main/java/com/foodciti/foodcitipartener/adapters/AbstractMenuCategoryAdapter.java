package com.foodciti.foodcitipartener.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.callbacks.GridItemTouchHelperCallback;
import com.foodciti.foodcitipartener.realm_entities.MenuCategory;
import com.foodciti.foodcitipartener.utils.AppConfig;
import com.foodciti.foodcitipartener.utils.Preferences;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.Realm;
import io.realm.RealmChangeListener;

public abstract class AbstractMenuCategoryAdapter extends RecyclerView.Adapter<AbstractMenuCategoryAdapter.CategoryHolder> implements GridItemTouchHelperCallback.ActionCompletionContract {
    protected final String TAG = this.getClass().getSimpleName();
    protected Activity context;
    private Callback callback;
    private PopupWindow popupWindow;
    protected List<MenuCategory> menuCategories;
    protected int selectedCategoryIndex = -1;
    protected SharedPreferences sharedPreferences;
    protected Realm realm;
    protected String orderType;
    private MenuCategory currentCategory=null;

    public MenuCategory getCurrentCategory() {
        return currentCategory;
    }

    protected final Set<RealmChangeListener<MenuCategory>> changeListenerHashSet = new HashSet<>(); // keeping strong reference to prevent early garbage collection

    public int getSelectedCategoryIndex() {
        return selectedCategoryIndex;
    }

    public MenuCategory getSelectedMenuCategory() {
        MenuCategory m = null;
        try {
            m = menuCategories.get(selectedCategoryIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }

    public void setMenuCategories(List<MenuCategory> menuCategories) {
        this.menuCategories = menuCategories;
    }

    protected boolean isRestored=false;

    public void setRestored(boolean restored) {
        isRestored = restored;
    }


    public AbstractMenuCategoryAdapter(Activity context, List<MenuCategory> menuCategories) {
        this.context = context;
        this.menuCategories = menuCategories;
        if (!(context instanceof Callback))
            throw new IllegalStateException("context must implement AbstractMenuCategoryAdapter.Callback");
        this.callback = (Callback) context;
        sharedPreferences = context.getSharedPreferences(AppConfig.MyPREFERENCES, Context.MODE_PRIVATE);
        realm = RealmManager.getLocalInstance();
        orderType = sharedPreferences.getString(Preferences.ORDER_TYPE,"");
    }

    public List<MenuCategory> getMenuCategories() {
        return menuCategories;
    }

    public int indexOf(MenuCategory menuCategory) {
        return menuCategories.indexOf(menuCategory);
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category, parent, false);
        return new CategoryHolder(view);
    }

/*    @Override
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

    }*/

/*    protected void setCounter(TextView textView, long categoryId) {
        if(realm.isClosed())
            realm = RealmManager.getLocalInstance();
        MenuCategory category = realm.where(MenuCategory.class).equalTo("id", categoryId).findFirstAsync();
        RealmChangeListener<MenuCategory> realmChangeListener = new RealmChangeListener<MenuCategory>() {
            @Override
            public void onChange(MenuCategory menuCategory) {
                int totalItems = getTotalItems(menuCategory, realm);
                if (totalItems > 0) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(Integer.toString(totalItems));
                } else {
                    textView.setVisibility(View.GONE);
                }
                Log.d(TAG, "-------------removing listener----------------");
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
            if(orderType.equals(Constants.TYPE_TABLE)) {
                sizeNum = realm.where(CartItem.class).equalTo("menuItem.id", menuItem.id).and()
                        .isNotEmpty("tables").sum("count");
            } else {
                sizeNum = realm.where(CartItem.class).equalTo("menuItem.id", menuItem.id).and()
                        .isEmpty("tables").sum("count");
            }
            return sizeNum == null ? 0 : sizeNum.intValue();
        }

        for (MenuItem item : menuItem.flavours)
            size += getItemSize(item, size, realm);

        return size;
    }*/

protected abstract void setCounter(TextView countTV, long categoryId);

    @Override
    public int getItemCount() {
        if (menuCategories == null)
            return 0;
        return menuCategories.size();
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        Log.e(TAG, "OLDPOSITION: " + oldPosition + ", NEWPOSITION: " + newPosition);
        MenuCategory item1 = null, item2 = null;
        AtomicReference<MenuCategory> mcRef1 = new AtomicReference<>();
        AtomicReference<MenuCategory> mcRef2 = new AtomicReference<>();
        Realm realm = RealmManager.getLocalInstance();
        realm.executeTransaction(r -> {
            try {
                if (oldPosition < newPosition) {
                    for (int i = oldPosition; i < newPosition; i++) {
                        mcRef1.set(menuCategories.get(i));
                        mcRef2.set(menuCategories.get(i + 1));
                        swap(mcRef1.get(), mcRef2.get());
                        Collections.swap(menuCategories, i, i + 1);
                    }
                } else {
                    for (int i = oldPosition; i > newPosition; i--) {
                        mcRef1.set(menuCategories.get(i));
                        mcRef2.set(menuCategories.get(i - 1));
                        swap(mcRef1.get(), mcRef2.get());
                        Collections.swap(menuCategories, i, i - 1);
                    }
                }
                notifyItemMoved(oldPosition, newPosition);
            } catch (Exception e) {
                Log.e(TAG, e + "");
                e.printStackTrace();
            }
        });
    }


    private void swap(MenuCategory i, MenuCategory j) {
        int temp = i.getItemposition();
        i.setItemposition(j.getItemposition());
        j.setItemposition(temp);
    }

    @Override
    public void onViewSwiped(int position) {

    }

    private void clearSelection() {
        for (MenuCategory m : menuCategories)
            m.setSelected(false);
    }

    public void setSelection(int index) {
//        clearSelection();
        selectedCategoryIndex = index;
        currentCategory = menuCategories.get(index);
        menuCategories.get(index).setSelected(true);
//        notifyDataSetChanged();
        notifyItemChanged(index);
    }

    public class CategoryHolder extends RecyclerView.ViewHolder {
        TextView name, counter;

        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.category_name);
            counter = itemView.findViewById(R.id.counter);
            itemView.setOnClickListener(v -> {
                int previousIndex = selectedCategoryIndex;
                final int pos = getAdapterPosition();
                selectedCategoryIndex = getAdapterPosition();
                notifyItemChanged(previousIndex);
                notifyItemChanged(selectedCategoryIndex);
                Log.e(TAG, "Cat Adapter Pos: " + pos);

                MenuCategory category = menuCategories.get(pos);
                callback.onMenuCatClick(pos, category);
            });
        }
    }

    public interface Callback {
        void onMenuCatClick(int position, MenuCategory category);

        void onMenuCatLongClick(int position, MenuCategory category);
    }
}
