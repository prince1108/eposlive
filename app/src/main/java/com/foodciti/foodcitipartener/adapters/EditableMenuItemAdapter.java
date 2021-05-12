package com.foodciti.foodcitipartener.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.dialogs.EditFlavourDialog;
import com.foodciti.foodcitipartener.realm_entities.MenuItem;
import com.foodciti.foodcitipartener.utils.AppConfig;
import com.foodciti.foodcitipartener.utils.CommonMethods;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.Preferences;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class EditableMenuItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "EditableMenuItemAdapter";
    private PopupWindow optionPopup;
    private Activity activity;
    private List<MenuItem> items;
    private static final int VIEW_ITEM = 0;
    private static final int VIEW_ADD_ITEM = 1;
    private int defaultColor;

    private Realm realm;

    private Callback callback;
    private final String itemType;
    private String orderType;
    private SharedPreferences sharedPreferences;
    private MenuItem parent;

    public List<MenuItem> getItems() {
        return items;
    }

    public void setItems(List<MenuItem> items) {
        this.items = items;
    }

    public EditableMenuItemAdapter(Activity activity, List<MenuItem> menuItems, String itemType, String orderType) {
        this.activity = activity;
        this.items = menuItems;
        this.itemType = itemType;
        this.realm = RealmManager.getLocalInstance();
        this.defaultColor = ContextCompat.getColor(activity, R.color.menu_unselected);
        if (!(activity instanceof Callback))
            throw new RuntimeException("Activity must implement EditableMenuItemAdapter.Clicklistener");
        callback = (Callback) activity;
        this.orderType = orderType;
        sharedPreferences = activity.getSharedPreferences(AppConfig.MyPREFERENCES, Context.MODE_PRIVATE);
    }

    public EditableMenuItemAdapter(Activity activity, List<MenuItem> menuItems, MenuItem parent, String orderType) {
        this.activity = activity;
        this.items = menuItems;
        this.itemType = Constants.ITEM_TYPE_FLAVOUR;
        this.parent = parent;
        this.realm = RealmManager.getLocalInstance();
        this.defaultColor = ContextCompat.getColor(activity, R.color.menu_unselected);
        if (!(activity instanceof Callback))
            throw new RuntimeException("Activity must implement EditableMenuItemAdapter.Clicklistener");
        callback = (Callback) activity;
        this.orderType = orderType;
        sharedPreferences = activity.getSharedPreferences(AppConfig.MyPREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == items.size())
            return VIEW_ADD_ITEM;
        return VIEW_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder vh = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_ITEM:
                view = inflater.inflate(R.layout.fragment_category_items, parent, false);
                vh = new EditableItemHolder(view);
                break;

            case VIEW_ADD_ITEM:
                view = inflater.inflate(R.layout.add_item, parent, false);
                vh = new AddItemViewHolder(view);
                break;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_ITEM:
                EditableItemHolder itemHolder = (EditableItemHolder) holder;
                processItemHolder(itemHolder, position);
                break;
        }
    }

    private void processItemHolder(EditableItemHolder holder, int position) {
        MenuItem item = items.get(position);
        holder.itemName.setText(item.name);
        String price = "";
        if (orderType.equals(Constants.TYPE_DELIVERY))
            price = String.format("%.2f", item.deliveryPrice);
        else
            price = String.format("%.2f", item.collectionPrice);
        if (item.flavours.size() > 0)
            holder.itemPrice.setText(R.string.option_hint);
        else {
            holder.itemPrice.setText(activity.getString(R.string.pound_symbol) + " " + price);
        }
        if (item.color == -1) {
            realm.executeTransaction(r -> {
                item.color = defaultColor;
            });
        }
//        holder.cardView.setCardBackgroundColor(item.color);
        CommonMethods.setGradientDrawable(activity, holder.itemView, item.color);

        int size = sharedPreferences.getInt(Preferences.MENU_ICON_SIZE, Constants.ICON_SMALL);
        switch (size) {
            case Constants.ICON_SMALL:
                holder.itemName.setTextSize(TypedValue.COMPLEX_UNIT_SP, activity.getResources().getDimension(R.dimen.icon_small));
                break;
            case Constants.ICON_MEDIUM:
                holder.itemName.setTextSize(TypedValue.COMPLEX_UNIT_SP, activity.getResources().getDimension(R.dimen.icon_medium));
                break;
            case Constants.ICON_LARGE:
                holder.itemName.setTextSize(TypedValue.COMPLEX_UNIT_SP, activity.getResources().getDimension(R.dimen.icon_large));
                break;
        }

    }

    @Override
    public int getItemCount() {
        return items.size() + 1;
    }

    public class EditableItemHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemPrice, counter;

        public EditableItemHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemPrice = itemView.findViewById(R.id.item_price);
            counter = itemView.findViewById(R.id.counter);

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                List<String> strings = new ArrayList<>();
                strings.add("Edit");
                strings.add("Delete");
                strings.add("Edit Flavours");
                optionPopup = optionPopupWindow(v, strings, position);
                optionPopup.showAsDropDown(v, 0, 0);
                return true;
            });
        }
    }

    public class AddItemViewHolder extends RecyclerView.ViewHolder {
        TextView add;

        public AddItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(v -> {
                callback.addMenuItem(EditableMenuItemAdapter.this, itemType, parent);
            });
        }
    }

    private PopupWindow optionPopupWindow(View parent, final List<String> optionList, final int itemPosition) {

        // initialize a pop up window type
        optionPopup = new PopupWindow(activity);

        OptionPopupAdapter adapter = new OptionPopupAdapter(optionList);
        // the drop down list is a list view
        ListView optionListView = new ListView(activity);
        optionListView.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));

        // set our adapter and pass our pop up window contents
        optionListView.setAdapter(adapter);

        // set on item selected
        optionListView.setOnItemClickListener((parent1, view, position, id) -> {
            String item = optionList.get(position);
            if (item.equalsIgnoreCase("Edit")) {
                callback.onMenuItemEdit(EditableMenuItemAdapter.this, itemPosition, items.get(itemPosition), itemType);
            } else if (item.equalsIgnoreCase("delete")) {
                callback.deleteMenu(EditableMenuItemAdapter.this, items.get(itemPosition), itemPosition);
            } else if (item.equalsIgnoreCase("Edit Flavours")) {
                EditFlavourDialog editFlavourDialog = EditFlavourDialog.newInstance(items.get(itemPosition).id, orderType);
                FragmentManager fragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
                editFlavourDialog.show(fragmentManager, null);
            }
            dismissPopup();
        });

        // some other visual settings for popup window
        Log.e("WIDTH_POPUP", "" + parent.getMeasuredWidth());
        optionPopup.setFocusable(true);
        optionPopup.setWidth(parent.getMeasuredWidth());
        optionPopup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        // set the list view as pop up window content
        optionPopup.setContentView(optionListView);

        return optionPopup;
    }

    private void dismissPopup() {
        if (optionPopup != null)
            optionPopup.dismiss();
    }

    public interface Callback {
        void onMenuItemEdit(EditableMenuItemAdapter menuItemAdapter, int position, MenuItem menuItem, String itemType);

        void addMenuItem(EditableMenuItemAdapter menuItemAdapter, String itemType, MenuItem parent);

        void deleteMenu(EditableMenuItemAdapter editableMenuItemAdapter, MenuItem menuItem, int position);
    }
}
