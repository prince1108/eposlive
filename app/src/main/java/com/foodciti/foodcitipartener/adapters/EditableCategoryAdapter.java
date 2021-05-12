package com.foodciti.foodcitipartener.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.realm_entities.MenuCategory;
import com.foodciti.foodcitipartener.utils.AppConfig;
import com.foodciti.foodcitipartener.utils.CommonMethods;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.Preferences;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class EditableCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "PrintOrderAdapter";
    private Context context;
    private MenuCategoryListener menuCategoryListener;
    private List<MenuCategory> menuCategories;
    public int categoryIndex = -1;
    private Realm realm;
    private PopupWindow optionPopup;
    private MenuCategory currentSelection = null;

    private static final int VIEW_ITEM = 0;
    private static final int VIEW_ADD_ITEM = 1;

    private SharedPreferences sharedPreferences;

    public MenuCategory getCurrentSelection() {
        return currentSelection;
    }

    public List<MenuCategory> getMenuCategories() {
        return menuCategories;
    }

    public void setMenuCategories(List<MenuCategory> menuCategories) {
        this.menuCategories = menuCategories;
    }

    public EditableCategoryAdapter(Context context, List<MenuCategory> categories) {
        this.context = context;
        this.menuCategories = categories;
        if (!(context instanceof MenuCategoryListener))
            throw new IllegalStateException("context must implement MenuCategoryListener");
        menuCategoryListener = (MenuCategoryListener) context;
        this.realm = RealmManager.getLocalInstance();
        sharedPreferences = context.getSharedPreferences(AppConfig.MyPREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == menuCategories.size())
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
                view = inflater.inflate(R.layout.category, parent, false);
                vh = new EditableCategoryHolder(view);
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
                EditableCategoryHolder itemHolder = (EditableCategoryHolder) holder;
                processItemHolder(itemHolder, position);
                break;
        }
    }

    private void processItemHolder(EditableCategoryHolder holder, int position) {
        MenuCategory category = menuCategories.get(position);
        holder.name.setText(category.getName());
        if (category.color != -1) {
//            holder.cardView.setCardBackgroundColor(category.color);
            CommonMethods.setGradientDrawable(context, holder.itemView, category.color);
        } else {
//            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_unselected));
            int color = ContextCompat.getColor(context, R.color.category_unselected);
            CommonMethods.setGradientDrawable(context, holder.itemView, color);
        }

        if (categoryIndex == position) {
//            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorVividTangerine));
            int color = ContextCompat.getColor(context, R.color.colorVividTangerine);
            CommonMethods.setGradientDrawable(context, holder.itemView, color);
        } else {
            if (category.color != -1) {
//                holder.cardView.setCardBackgroundColor(category.color);
                CommonMethods.setGradientDrawable(context, holder.itemView, category.color);
            } else {
//                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.category_unselected));
                int color = ContextCompat.getColor(context, R.color.category_unselected);
                CommonMethods.setGradientDrawable(context, holder.itemView, color);
            }
        }
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
    public int getItemCount() {
        return menuCategories.size() + 1;
    }

    public class AddItemViewHolder extends RecyclerView.ViewHolder {
        TextView add;

        public AddItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menuCategoryListener.addCategory();
                }
            });
        }
    }

    public void setSelection(int index) {
        currentSelection = menuCategories.get(index);
        categoryIndex = index;
        notifyDataSetChanged();
    }

    public class EditableCategoryHolder extends RecyclerView.ViewHolder {
        TextView name, counter;
//        CardView cardView;

        public EditableCategoryHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.category_name);
            counter = itemView.findViewById(R.id.counter);
//            cardView = (CardView) itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int previousIndex = categoryIndex;
                    final int position = getAdapterPosition();

                    /*realm.executeTransaction(r->{
                        menuCategories.get(categoryIndex).setSelected(true);
                        notifyItemChanged(categoryIndex);
                        if (previousIndex != -1) {
                            menuCategories.get(previousIndex).setSelected(false);
                            notifyItemChanged(previousIndex);
                        }

                        MenuCategory category = menuCategories.get(getAdapterPosition());
                        menuCategoryListener.onMenuCatClick(getAdapterPosition(), category);
                    });*/
                    MenuCategory category = menuCategories.get(position);
                    setSelection(position);
                    menuCategoryListener.onMenuCatClick(position, category);
                }

            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    /*MenuCategory category=menuCategories.get(getAdapterPosition());
                    menuCategoryListener.onMenuCatLongClick(getAdapterPosition(),category);*/
                    final int position = getAdapterPosition();
                    final List<String> strings = new ArrayList<>();
                    strings.add("Edit");
                    strings.add("Delete");
                    strings.add("Edit Print Order");
                    optionPopup = optionPopupWindow(v, strings, position);
                    optionPopup.showAsDropDown(v, 0, 0);
                    return true;
                }
            });
        }
    }

    private PopupWindow optionPopupWindow(View parent, final List<String> optionList, final int itemPosition) {

        // initialize a pop up window type
        final PopupWindow optionPopup = new PopupWindow(context);

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
                /*final Dialog subCatDialog = new Dialog(context);
                subCatDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                subCatDialog.setContentView(R.layout.menu_subcat);
                final RecyclerView menuSubCatRecycler = subCatDialog.findViewById(R.id.menu_subcat);
                TextView title = subCatDialog.findViewById(R.id.title);
                title.setText("Edit Flavours");
                subCatDialog.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        subCatDialog.dismiss();
                    }
                });*/
                String item = optionList.get(position);
                if (item.equalsIgnoreCase("Edit")) {
                    menuCategoryListener.onMenuCatLongClick(itemPosition, menuCategories.get(itemPosition));
                } else if (item.equalsIgnoreCase("delete")) {
//                    menuItemListener.deleteMenu(items.get(itemPosition), itemPosition);
                    menuCategoryListener.deleteCategory(itemPosition);
                } else if (item.equalsIgnoreCase("Edit Print Order"))
                    menuCategoryListener.onClickEditPrintOrder();
                optionPopup.dismiss();
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


    public interface MenuCategoryListener {
        void onMenuCatClick(int position, MenuCategory category);

        void onMenuCatLongClick(int position, MenuCategory category);

        void addCategory();

        void deleteCategory(int position);

        void onClickEditPrintOrder();
    }
}
