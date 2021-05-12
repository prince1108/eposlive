package com.foodciti.foodcitipartener.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.callbacks.GridItemTouchHelperCallback;
import com.foodciti.foodcitipartener.adapters.callbacks.MyTouchListener;
import com.foodciti.foodcitipartener.compound_views.CounterBox;
import com.foodciti.foodcitipartener.realm_entities.Addon;
import com.foodciti.foodcitipartener.realm_entities.MenuItem;
import com.foodciti.foodcitipartener.utils.AppConfig;
import com.foodciti.foodcitipartener.utils.CommonMethods;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.Preferences;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.util.Collections;
import java.util.List;

import io.realm.Realm;


public class AddonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements GridItemTouchHelperCallback.ActionCompletionContract {
    private static final String TAG = "AddonAdapter";
    private Activity context;
    private List<Addon> addons;
    private AddonClicklistener clicklistener;
    private static final int VIEW_ITEM = 0;
    private static final int VIEW_NO_ADDON = 1;
    private static final int VIEW_ADD_ITEM = 2;
    private int defaultColor = -1;

    private PopupWindow optionPopup;
    private boolean noAddon;
    private SharedPreferences sharedPreferences;
    private boolean disabled = false;
    private Realm realm;

    public AddonAdapter(Activity context, List<Addon> addons, boolean noAddon) {
        this.context = context;
        this.addons = addons;
        defaultColor = ContextCompat.getColor(context, R.color.addon_default_color);
        this.noAddon = noAddon;
        if (!(context instanceof AddonClicklistener))
            throw new RuntimeException("context must implement AddonAdapter.AddonClicklistener");
        clicklistener = (AddonClicklistener) context;
        sharedPreferences = context.getSharedPreferences(AppConfig.MyPREFERENCES, Context.MODE_PRIVATE);
        realm = RealmManager.getLocalInstance();
    }

    public List<Addon> getAddons() {
        return addons;
    }

    public void setAddons(List<Addon> addons) {
        this.addons = addons;
    }

    public void clearAddons() {
        if (addons != null)
            this.addons.clear();
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder vh = null;
        View view = null;
        switch (viewType) {
            case VIEW_ITEM:
                view = inflater.inflate(R.layout.subitems, parent, false);
                vh = new AddonHolder(view);
                break;
            case VIEW_NO_ADDON:
                view = inflater.inflate(R.layout.no_addon, parent, false);
                vh = new NoAddonHolder(view);
                break;
            case VIEW_ADD_ITEM:
                view = inflater.inflate(R.layout.add_item, parent, false);
                vh = new AddAddonViewHolder(view);
                break;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case VIEW_ITEM:
                AddonHolder addonHolder = (AddonHolder) holder;
                processAddonHolder(addonHolder, position);
                break;
            case VIEW_NO_ADDON:
                NoAddonHolder noAddonHolder = (NoAddonHolder) holder;
                processNoAddonHolder(noAddonHolder, position);
                break;
        }
    }


//    private void showFlavoursDialog(List<Addon> flavours, int position) {
//        Addon menuItem = realm.where(Addon.class).equalTo("id", flavours.get(position).parent.id).findFirstAsync();
//        final Dialog dialog = new Dialog(context);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.menu_subcat);
//        TextView title = dialog.findViewById(R.id.title);
//        title.setText("Select Option");
//
//        View close = dialog.findViewById(R.id.close);
//        close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        RecyclerView recyclerView = dialog.findViewById(R.id.editFlavoursRV);
//        recyclerView.setLayoutManager(new GridLayoutManager(context, 4));
//        NewMenuItemAdapter menuItemAdapter = new NewMenuItemAdapter(context, flavours, itemType, orderType);
//        menuItemAdapter.parent = this;
//        GridItemTouchHelperCallback itemTouchHelperCallback1 = new GridItemTouchHelperCallback(menuItemAdapter);
//        ItemTouchHelper touchHelper = new ItemTouchHelper(itemTouchHelperCallback1);
//        touchHelper.attachToRecyclerView(recyclerView);
//        recyclerView.setAdapter(menuItemAdapter);
//        recyclerView.addOnItemTouchListener(new MyTouchListener(context,
//                recyclerView,
//                new MyTouchListener.OnTouchActionListener() {
//                    @Override
//                    public void onLeftSwipe(View view, int position) {
//                    }
//
//                    @Override
//                    public void onRightSwipe(View view, int position) {
//                    }
//
//                    @Override
//                    public void onClick(View view, int position) {
//                        count++;
//                        if(menuItem.closeCounter==count)
//                        {
//                            count=0;
//                            dialog.dismiss();
//                        }
//                    }
//                }));
//        dialog.show();
//    }


    private void processNoAddonHolder(NoAddonHolder holder, int position) {
        Addon addon = addons.get(position);
        if (addon.color == -1) {
            realm.executeTransaction(r -> {
                addon.color = defaultColor;
            });
        }
//        holder.itemView.setBackgroundColor(addon.color);
        CommonMethods.setGradientDrawable(context, holder.itemView, addon.color);
        holder.name.setText(addon.name);
        if (addon.selected)
            holder.selected.setVisibility(View.VISIBLE);
        else
            holder.selected.setVisibility(View.GONE);

        int iconsize = sharedPreferences.getInt(Preferences.MENU_ICON_SIZE, Constants.ICON_SMALL);
        switch (iconsize) {
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

    private void processAddonHolder(AddonHolder holder, int position) {
        Addon addon = addons.get(position);
        if (addon.color == -1) {
            realm.executeTransaction(r -> {
                addon.color = defaultColor;
            });
        }
//        holder.itemView.setBackgroundColor(addon.color);
        CommonMethods.setGradientDrawable(context, holder.itemView, addon.color);
        holder.name.setText(addon.name);
        if (addon.selected)
            holder.selected.setVisibility(View.VISIBLE);
        else
            holder.selected.setVisibility(View.GONE);

        int iconsize = sharedPreferences.getInt(Preferences.MENU_ICON_SIZE, Constants.ICON_SMALL);
        switch (iconsize) {
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

        if (addon.price > 0) {
            final String price = String.format("%.2f", addon.price);
            holder.price.setText(context.getString(R.string.pound_symbol) + " " + price);
        } else
            holder.price.setText("");
    }

    @Override
    public int getItemCount() {
//        return addons.size()+1;
        return addons.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (addons.get(position).isNoAddon)
            return VIEW_NO_ADDON;
        return VIEW_ITEM;
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        Log.e(TAG, "OLDPOSITION: " + oldPosition + ", NEWPOSITION: " + newPosition);
        Addon item1 = null, item2 = null;
        try {
            if (oldPosition < newPosition) {
                for (int i = oldPosition; i < newPosition; i++) {
                    item1 = addons.get(i);
                    item2 = addons.get(i + 1);
                    swap(item1, item2);
                    Collections.swap(addons, i, i + 1);
                }
            } else {
                for (int i = oldPosition; i > newPosition; i--) {
                    item1 = addons.get(i);
                    item2 = addons.get(i - 1);
                    swap(item1, item2);
                    Collections.swap(addons, i, i - 1);
                }
            }
            notifyItemMoved(oldPosition, newPosition);
        } catch (Exception e) {
            Log.e(TAG, e + "");
            e.printStackTrace();
        }
    }

    private void swap(Addon i, Addon j) {
        realm.executeTransaction(r -> {
            int temp = i.itemposition;
            i.itemposition = j.itemposition;
            j.itemposition = temp;
        });
    }

    @Override
    public void onViewSwiped(int position) {

    }

    public class AddAddonViewHolder extends RecyclerView.ViewHolder {
        TextView add;

        public AddAddonViewHolder(View itemView) {
            super(itemView);
            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clicklistener.addNewAddon(noAddon);
                }
            });*/
        }
    }

    public class AddonHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        ImageView selected;
//        CardView cardView;

        public AddonHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.sub_item_name);
            price = itemView.findViewById(R.id.price);
            selected = itemView.findViewById(R.id.selected);
//            cardView = (CardView) itemView;
            itemView.setOnClickListener(v -> {
                if (disabled)
                    return;
                final Addon addon = addons.get(getAdapterPosition());
                realm.executeTransaction(r -> {
                    if (addon.selected)
                        addon.selected = false;
                    else
                        addon.selected = true;
                    notifyItemChanged(getAdapterPosition());
                });
                clicklistener.onClickAddon(addon);
            });

            /*cardView.setOnLongClickListener(v -> {
                return true;
            });*/
        }
    }

    public class NoAddonHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        ImageView selected;
//        CardView cardView;

        public NoAddonHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.sub_item_name);
            price = itemView.findViewById(R.id.price);
            selected = itemView.findViewById(R.id.selected);
//            cardView = (CardView) itemView;
            itemView.setOnClickListener(v -> {
                if (disabled)
                    return;
                final Addon addon = addons.get(getAdapterPosition());
                realm.executeTransaction(r -> {
                    if (addon.selected)
                        addon.selected = false;
                    else
                        addon.selected = true;
                    notifyItemChanged(getAdapterPosition());
                });
                clicklistener.onClickAddon(addon);
            });

            /*cardView.setOnLongClickListener(v -> {
                return true;
            });*/
        }
    }

    public interface AddonClicklistener {
        void onClickAddon(Addon addon);

       /* void onLongClickAddon(Addon addon, int position, boolean noAddon);

        void addNewAddon(boolean noAddon);*/
    }
}
