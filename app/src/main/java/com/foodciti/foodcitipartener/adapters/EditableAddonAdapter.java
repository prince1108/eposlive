package com.foodciti.foodcitipartener.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.dialogs.EditAddonFlaourDialoge;
import com.foodciti.foodcitipartener.dialogs.EditFlavourDialog;
import com.foodciti.foodcitipartener.realm_entities.Addon;
import com.foodciti.foodcitipartener.realm_entities.MenuCategory;
import com.foodciti.foodcitipartener.utils.AppConfig;
import com.foodciti.foodcitipartener.utils.CommonMethods;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.Preferences;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class EditableAddonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "EditableAddonAdapter";
    private Context context;
    private List<Addon> addons;
    private AddonClicklistener clicklistener;
    private static final int VIEW_ITEM = 0;
    private static final int VIEW_NO_ADDON = 1;
    private static final int VIEW_ADD_ITEM = 2;
    private int defaultColor = -1;
    private Realm realm;
    private PopupWindow optionPopup;
    private boolean noAddon;
    String orderType;
    private SharedPreferences sharedPreferences;
    public EditableAddonAdapter(Context context, List<Addon> addons, boolean noAddon,String mOrderType) {
        this.context = context;
        this.addons = addons;
        this.orderType=mOrderType;
        defaultColor = ContextCompat.getColor(context, R.color.addon_default_color);
        this.realm = RealmManager.getLocalInstance();
        this.noAddon = noAddon;
        if (!(context instanceof AddonClicklistener))
            throw new IllegalStateException("context must implement AddonClicklistener");
        clicklistener = (AddonClicklistener) context;
        sharedPreferences = context.getSharedPreferences(AppConfig.MyPREFERENCES, Context.MODE_PRIVATE);
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

    @Override
    public int getItemCount() {
        return addons.size() + 1;
    }

    private void processNoAddonHolder(NoAddonHolder holder, int position) {
        Addon addon = addons.get(position);
        if (addon.color == -1) {
            realm.executeTransaction(r -> {
                addon.color = defaultColor;
            });
        }
//        holder.cardView.setCardBackgroundColor(addon.color);
        CommonMethods.setGradientDrawable(context, holder.itemView, addon.color);
        holder.name.setText(addon.name);
        if (addon.selected)
            holder.selected.setVisibility(View.VISIBLE);
        else
            holder.selected.setVisibility(View.GONE);

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

    private void processAddonHolder(AddonHolder holder, int position) {
        Addon addon = addons.get(position);
        if (addon.color == -1) {
            realm.executeTransaction(r -> {
                addon.color = defaultColor;
            });
        }
//        holder.cardView.setCardBackgroundColor(addon.color);
        CommonMethods.setGradientDrawable(context, holder.itemView, addon.color);
        holder.name.setText(addon.name);
        if (addon.selected)
            holder.selected.setVisibility(View.VISIBLE);
        else
            holder.selected.setVisibility(View.GONE);

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

        if (addon.price > 0) {
            final String price = String.format("%.2f", addon.price);
            holder.price.setText(context.getString(R.string.pound_symbol) + " " + price);
        } else
            holder.price.setText("");
    }


    @Override
    public int getItemViewType(int position) {
        if (position == addons.size())
            return VIEW_ADD_ITEM;
        else if (addons.get(position).price < 0)
            return VIEW_NO_ADDON;
        return VIEW_ITEM;
    }

    public class AddAddonViewHolder extends RecyclerView.ViewHolder {
        TextView add;

        public AddAddonViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clicklistener.addNewAddon(noAddon);
                }
            });
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

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    List<String> strings = new ArrayList<>();
                    strings.add("Edit");
                    strings.add("Delete");
                    strings.add("Edit Flavours");
                    optionPopup = optionPopupWindow(v, strings, position);
                    optionPopup.showAsDropDown(v, 0, 0);
                    return true;
                }
            });
        }
    }

    public class NoAddonHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView selected;
//        CardView cardView;

        public NoAddonHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.sub_item_name);
            selected = itemView.findViewById(R.id.selected);
//            cardView = (CardView) itemView;

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    List<String> strings = new ArrayList<>();
                    strings.add("Edit");
                    strings.add("Delete");
                    optionPopup = optionPopupWindow(v, strings, position);
                    optionPopup.showAsDropDown(v, 0, 0);
                    return true;
                }
            });
        }
    }

    private PopupWindow optionPopupWindow(View parent, final List<String> optionList, final int itemPosition) {

        // initialize a pop up window type
        optionPopup = new PopupWindow(context);

        OptionPopupAdapter adapter = new OptionPopupAdapter(optionList);
        // the drop down list is a list view
        ListView optionListView = new ListView(context);
        optionListView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

        // set our adapter and pass our pop up window contents
        optionListView.setAdapter(adapter);

        // set on item selected
        optionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = optionList.get(position);
                if (item.equalsIgnoreCase("Edit")) {
                    clicklistener.onLongClickAddon(addons.get(itemPosition), itemPosition, noAddon);
                    optionPopup.dismiss();
                } else if(item.equalsIgnoreCase("Edit Flavours")){//set by ravi
//                    clicklistener.onLongClickAddon(addons.get(itemPosition).parent, itemPosition, noAddon);
//                    optionPopup.dismiss();
                    EditAddonFlaourDialoge editFlavourDialog = EditAddonFlaourDialoge.newInstance(addons.get(itemPosition).id, orderType);
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    editFlavourDialog.show(fragmentManager, null);
                }
                else if (item.equalsIgnoreCase("delete")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(false);
                    builder.setTitle("Delete Item");
                    builder.setMessage("Are you sure you want to delete this item?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            realm.executeTransaction(r -> {
                                Addon addon = addons.get(itemPosition);
                                MenuCategory menuCategory = addon.menuCategory;
                                menuCategory.addons.remove(addon);
                                addons.remove(addon);
                                addon.deleteFromRealm();
                                notifyItemRemoved(itemPosition);
                            });
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.create().show();

                    optionPopup.dismiss();
                }
            }
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


    public interface AddonClicklistener {
        void onLongClickAddon(Addon addon, int position, boolean noAddon);

        void addNewAddon(boolean noAddon);
    }
}
