package com.foodciti.foodcitipartener.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.callbacks.GridItemTouchHelperCallback;
import com.foodciti.foodcitipartener.realm_entities.MenuItem;
import com.foodciti.foodcitipartener.utils.AppConfig;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.Realm;
import io.realm.RealmChangeListener;

public abstract class AbstractMenuItemAdapter extends RecyclerView.Adapter<AbstractMenuItemAdapter.ItemHolder> implements GridItemTouchHelperCallback.ActionCompletionContract {
    protected final String TAG = this.getClass().getSimpleName();
    protected Activity context;
    protected List<MenuItem> items;
    protected String itemType;
    protected static String orderType;
    public int defaultColor = 0;
    private Callback callback;
    public AbstractMenuItemAdapter parent = null;
    protected SharedPreferences sharedPreferences;
    private boolean disabled = false;
    protected int selectionIndex = -1;
    protected final Set<RealmChangeListener<MenuItem>> changeListenerHashSet = new HashSet<>(); // keeping strong reference to prevent early garbage collection

    protected Realm realm;

    public void setRealm(Realm realm) {
        this.realm = realm;
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public void setItems(List<MenuItem> it) {
        this.items = it;
        notifyDataSetChanged();
    }

    public int getSelectionIndex() {
        return selectionIndex;
    }

    public static void setOrderType(String ot) {
        orderType = ot;
    }

    public MenuItem getSelectedItem() {
        if (selectionIndex == -1)
            return null;
        return items.get(selectionIndex);
    }

    public void clearItems() {
        if (items != null) {
            realm.executeTransaction(r -> {
                items.clear();
            });
        }
    }

    public int indexOf(MenuItem m) {
        return items.indexOf(m);
    }


    protected boolean isRestored=false;

    public void setRestored(boolean restored) {
        isRestored = restored;
    }


    public AbstractMenuItemAdapter(Activity context, List<MenuItem> items, String itemType, String orderType) {
        this.context = context;
        realm = RealmManager.getLocalInstance();
        this.items = items;
        this.itemType = itemType;
        this.orderType = orderType;
        Log.d(TAG, "----------------orderType: " + orderType);
        if (!(context instanceof Callback))
            throw new RuntimeException("context must implement AbstractMenuItemAdapter.Callback");
        callback = (Callback) context;
        sharedPreferences = context.getSharedPreferences(AppConfig.MyPREFERENCES, Context.MODE_PRIVATE);
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_category_items, parent, false);
        return new ItemHolder(view);
    }

    protected abstract void setCounter(TextView counterTV, long menuItemId);

    @Override
    public int getItemCount() {
        try {
            if (items == null)
                return 0;
            return items.size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        MenuItem item1 = null, item2 = null;
        AtomicReference<MenuItem> menuRef1 = new AtomicReference<>();
        AtomicReference<MenuItem> menuRef2 = new AtomicReference<>();
        realm.executeTransaction(r -> {
            try {
                if (oldPosition < newPosition) {
                    for (int i = oldPosition; i < newPosition; i++) {
                        menuRef1.set(items.get(i));
                        menuRef2.set(items.get(i + 1));
                        swap(menuRef1.get(), menuRef2.get());
                        Collections.swap(items, i, i + 1);
                    }
                } else {
                    for (int i = oldPosition; i > newPosition; i--) {
                        menuRef1.set(items.get(i));
                        menuRef2.set(items.get(i - 1));
                        swap(menuRef1.get(), menuRef2.get());
                        Collections.swap(items, i, i - 1);
                    }
                }
                notifyItemMoved(oldPosition, newPosition);
                callback.onRearrange(items.get(oldPosition), items.get(newPosition));
            } catch (Exception e) {
                Log.e(TAG, e + "");
                e.printStackTrace();
            }
        });
    }

    private void swap(MenuItem i, MenuItem j) {
        int temp = i.itemPosition;
        i.itemPosition = j.itemPosition;
        j.itemPosition = temp;
    }

    @Override
    public void onViewSwiped(int position) {

    }


    protected abstract void showFlavoursDialog(List<MenuItem> flavours, int position);

    public class ItemHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemPrice, counter;

        public ItemHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemPrice = itemView.findViewById(R.id.item_price);
            counter = itemView.findViewById(R.id.counter);
            itemView.setOnClickListener(v -> {
                if (disabled) {
                    return;
                }
                try {
                    final int index = getAdapterPosition();
                    final MenuItem menuItem = items.get(index);
                    setSelection(index);
                    if (menuItem.flavours != null && menuItem.flavours.size() > 0) {
                        List<MenuItem> flavours = new ArrayList<>(menuItem.flavours);
                        Collections.sort(flavours, new Comparator<MenuItem>() {
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
                        showFlavoursDialog(flavours, index);
                    } else {
                        callback.onMenuItemClick(AbstractMenuItemAdapter.this, index, menuItem, itemType);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void clearSelection() {
        for (MenuItem m : items)
            m.isSelected = false;
    }

    public void setSelection(int index) {
        if(index==-1)
            return;
        clearSelection();
        selectionIndex = index;
        items.get(index).isSelected = true;
        /*if (parent != null)
            parent.notifyDataSetChanged();
        notifyDataSetChanged();*/
    }

    private Drawable getGradientDrawable(CardView cardView) {
        GradientDrawable gradient = new GradientDrawable();
        gradient.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        int color = cardView.getCardBackgroundColor().getDefaultColor();
        int gradientStart = color - 6250335;
        int gradientEnd = color + 1842204;
        gradient.setColors(new int[]{gradientStart, gradientEnd});
        return gradient;
    }


    public interface Callback {
        void onMenuItemClick(AbstractMenuItemAdapter adapter, int position, MenuItem menuItem, String itemType);

        void onRearrange(MenuItem item1, MenuItem item2);
    }

}
