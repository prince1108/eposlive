package com.foodciti.foodcitipartener.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.realm_entities.MenuItem;
import com.foodciti.foodcitipartener.utils.CommonMethods;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.StringHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.realm.Realm;


public class MenuSubCatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "MenuSubCatAdapter";
    private Context context;
    private List<MenuItem> menuItemFlavours;
    private MenuItem menuItem;

    private static final int VIEW_FLAVOUR = 0;
    private static final int VIEW_ADD = 1;

    private EditText name, price;
    private Button add;
    private ClickListener clickListener;
    private Realm realm;
    private PopupWindow optionPopup;
    private int defaultColor;
    private String orderType;
    private CommonMethods commonMethods;

    public void addClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public MenuSubCatAdapter(Context context, List<MenuItem> itemFlavours, final MenuItem menuItem, String orderType, Realm realm) {
        this.realm = realm;
        this.context = context;
        this.menuItemFlavours = itemFlavours;
        this.menuItem = menuItem;
        defaultColor = ContextCompat.getColor(context, R.color.dark_brown);
        this.orderType = orderType;
        commonMethods = new CommonMethods(context);
    }


    @Override
    public int getItemViewType(int position) {
        if (position == menuItemFlavours.size())
            return VIEW_ADD;
        return VIEW_FLAVOUR;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_FLAVOUR:
                view = inflater.inflate(R.layout.fragment_category_items, parent, false);
                viewHolder = new SubCatHolder(view);
                break;
            case VIEW_ADD:
                view = inflater.inflate(R.layout.add_item, parent, false);
                viewHolder = new AddItemViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_FLAVOUR:
                SubCatHolder subCatHolder = (SubCatHolder) holder;
                processSubCatHolder(subCatHolder, position);
                break;

        }
    }

    private void processSubCatHolder(SubCatHolder subCatHolder, int position) {
        MenuItem menuItem = menuItemFlavours.get(position);

        Log.e(TAG, "---------------------orderType: " + orderType);
        Log.e(TAG, "----------------deliveryPrice: " + menuItem.deliveryPrice);
        Log.e(TAG, "----------------collectionPrice: " + menuItem.collectionPrice);
        subCatHolder.name.setText(menuItem.name);
        if (orderType.equals(Constants.TYPE_DELIVERY))
            subCatHolder.price.setText(menuItem.deliveryPrice + "");
        else
            subCatHolder.price.setText(menuItem.collectionPrice + "");
        if (menuItem.color == -1)
            subCatHolder.itemView.setBackgroundColor(defaultColor);
        else
            subCatHolder.itemView.setBackgroundColor(menuItem.color);
    }

    @Override
    public int getItemCount() {
        return menuItemFlavours.size() + 1;
    }

    public class SubCatHolder extends RecyclerView.ViewHolder {
        TextView name, price;
//        CardView cardView;

        public SubCatHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_name);
            price = itemView.findViewById(R.id.item_price);
//            cardView = (CardView) itemView;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* MenuItem flavours=menuItemFlavours.get(getAdapterPosition());
                    menuItem.name=flavours.name;
                    menuItem.price=flavours.price;
                    clickListener.dismiss();*/
                }
            });

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

   /* private Map<String, Integer> setupColorOptions() {
        Map<String, Integer> colorMap = new HashMap<>();
        String[] colors = context.getResources().getStringArray(R.array.color_string);
        for (String color : colors) {
            String name = color.split("\\|")[0];
            String hex = color.split("\\|")[1];
            colorMap.put(name, Color.parseColor(hex));
        }
        return colorMap;
    }*/

    public class AddItemViewHolder extends RecyclerView.ViewHolder {
        TextView add;

        public AddItemViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Map<String, Integer> colorMap = commonMethods.getColorMap();
                    final Dialog addSubCat = new Dialog(context);
                    addSubCat.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    addSubCat.setContentView(R.layout.add_menu_subcat);
                    addSubCat.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addSubCat.dismiss();
                        }
                    });

                    final EditText deliveryPrice;
                    deliveryPrice = addSubCat.findViewById(R.id.delivery_price);
                    final Spinner color_spinner = addSubCat.findViewById(R.id.color_spinner);
                    ArrayAdapter<String> integerArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, new ArrayList<>(colorMap.keySet()));
                    integerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    color_spinner.setAdapter(integerArrayAdapter);
                    color_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Integer color = colorMap.get(parent.getItemAtPosition(position));
                            Toast.makeText(parent.getContext(), "Selected: " + parent.getItemAtPosition(position), Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    final CheckBox checkbox;
                    checkbox = addSubCat.findViewById(R.id.checkbox);
//                    final StringBuilder deliveryPrice = new StringBuilder();

                    name = addSubCat.findViewById(R.id.name);
                    price = addSubCat.findViewById(R.id.price);
                    add = addSubCat.findViewById(R.id.add);
                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /*if (checkbox.isChecked()) {
                                deliveryPrice.delete(0, deliveryPrice.length());
                                deliveryPrice.append(price.getText().toString());
                            } else {
                                deliveryPrice.delete(0, deliveryPrice.length());
                                deliveryPrice.append(deliveryPrice.getText().toString());
                            }*/
                            realm.executeTransaction(r -> {
                                Number maxId = realm.where(MenuItem.class).max("id");
                                // If there are no rows, currentId is null, so the next id must be 1
                                // If currentId is not null, increment it by 1
                                long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
                                // User object created with the new Primary key

                                MenuItem item = realm.createObject(MenuItem.class, nextId);
                                item.name = StringHelper.capitalizeEachWord(name.getText().toString().trim());
                                double price_ = price.getText().toString().trim().equals("") ? 0 : Double.parseDouble(price.getText().toString());
                                item.collectionPrice = price_;
                                item.parent = menuItem;
                                menuItem.flavours.add(item);
                                item.menuCategory = menuItem.menuCategory;
                                item.type = Constants.ITEM_TYPE_FLAVOUR;
                                item.last_updated = new Date().getTime();
                                if (deliveryPrice.getVisibility() == View.VISIBLE) {
                                    double price = deliveryPrice.getText().toString().trim().equals("") ? 0 : Double.parseDouble(deliveryPrice.getText().toString().trim());
                                    item.deliveryPrice = price;
                                } else
                                    item.deliveryPrice = price_;
                                item.color = colorMap.get(color_spinner.getSelectedItem());
                                item.itemPosition = menuItem.flavours.size() - 1;
                                Log.e(TAG, "----------------------- item parent: " + item.parent);
                                Log.e(TAG, "----------------------- item menuCategory: " + item.menuCategory);
                            });


                            addSubCat.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    notifyDataSetChanged();
                                }
                            });
                            addSubCat.dismiss();
                        }
                    });


                    checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                deliveryPrice.setVisibility(View.GONE);
//                    deliveryPrice=price.getText().toString();
                            } else {
                                deliveryPrice.setVisibility(View.VISIBLE);
//                                deliveryPrice.append(delivery_charges.getText().toString().trim());
                            }
                        }
                    });
                    checkbox.setChecked(true);
                    addSubCat.show();
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
                final Dialog subCatDialog = new Dialog(context);
                subCatDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                subCatDialog.setContentView(R.layout.menu_subcat);
                final RecyclerView menuSubCatRecycler = subCatDialog.findViewById(R.id.editFlavoursRV);
                TextView title = subCatDialog.findViewById(R.id.title);
                title.setText("Edit Flavours");
                subCatDialog.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        subCatDialog.dismiss();
                    }
                });
                String item = optionList.get(position);
                if (item.equalsIgnoreCase("Edit")) {
                    callMenuItemEditDialog(itemPosition, menuItemFlavours.get(itemPosition));
                    optionPopup.dismiss();
                }
//                    menuItemListener.onMenuItemEdit(itemPosition, items.get(itemPosition));
                else if (item.equalsIgnoreCase("delete")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(false);
                    builder.setTitle("Delete Item");
                    builder.setMessage("Are you sure you want to delete this item?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            realm.executeTransaction(r -> {
                                MenuItem flavour = menuItemFlavours.get(itemPosition);
                                menuItem.flavours.remove(flavour);
                                menuItemFlavours.remove(flavour);
                                menuItem.last_updated = new Date().getTime();
                                flavour.deleteFromRealm();
                            });
                            optionPopup.dismiss();
                            notifyItemRemoved(itemPosition);
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
                }
//                    menuItemListener.deleteMenu(items.get(itemPosition), itemPosition);
                else if (item.equalsIgnoreCase("Edit Flavours")) {
                    /*MenuSubCatAdapter menuSubCatAdapter=new MenuSubCatAdapter(context, items.get(itemPosition).flavours, items.get(itemPosition));
                    menuSubCatAdapter.addClickListener(MenuItemAdapter.this);
                    menuSubCatRecycler.setAdapter(menuSubCatAdapter);
                    subCatDialog.show();*/
                    MenuSubCatAdapter menuSubCatAdapter = new MenuSubCatAdapter(context, menuItemFlavours.get(itemPosition).flavours, menuItemFlavours.get(itemPosition), orderType, realm);
                    menuSubCatRecycler.setAdapter(menuSubCatAdapter);
                    subCatDialog.show();
                    subCatDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            notifyItemChanged(itemPosition);
                        }
                    });
                }
            }
        });

        // some other visual settings for popup window
        Log.e("WIDTH_POPUP", "" + parent.getMeasuredWidth());
        optionPopup.setFocusable(true);
        optionPopup.setWidth(parent.getMeasuredWidth());
        // falvourPopup.setBackgroundDrawable(getResources().getDrawable(R.drawable.white));
        optionPopup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        // set the list view as pop up window content
        optionPopup.setContentView(optionListView);

        return optionPopup;
    }

    public interface ClickListener {
        void dismiss();
    }

    private void callMenuItemEditDialog(final int position, final MenuItem menuItem) {
        final Dialog modifyMenuItem = new Dialog(context);
        modifyMenuItem.requestWindowFeature(Window.FEATURE_NO_TITLE);
        modifyMenuItem.setContentView(R.layout.add_menuitem_dialog);
        modifyMenuItem.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyMenuItem.dismiss();
            }
        });
        final EditText item_name, price, delivery_charges;
        final Spinner color_spinner;
        final CheckBox checkbox;
        final Button add_item;
        item_name = modifyMenuItem.findViewById(R.id.item_name);
        price = modifyMenuItem.findViewById(R.id.price);
        delivery_charges = modifyMenuItem.findViewById(R.id.delivery_price);
        color_spinner = modifyMenuItem.findViewById(R.id.color_spinner);
        checkbox = modifyMenuItem.findViewById(R.id.checkbox);
        add_item = modifyMenuItem.findViewById(R.id.add_item);
//        final StringBuilder deliveryPrice = new StringBuilder();
        if (position == -1) {
            modifyMenuItem.setTitle("Add Menu-Item");
        } else {
            modifyMenuItem.setTitle("Update Menu-Item");
            add_item.setText("Update");
            item_name.setText(menuItem.name);
            price.setText(String.format("%.2f", menuItem.price));
        }
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    delivery_charges.setVisibility(View.GONE);
//                    deliveryPrice=price.getText().toString();
                } else {
                    delivery_charges.setVisibility(View.VISIBLE);
//                    deliveryPrice.append(delivery_charges.getText().toString().trim());
                }
            }
        });
        checkbox.setChecked(true);

        final Map<String, Integer> colorMap = commonMethods.getColorMap();


        ArrayAdapter<String> integerArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, new ArrayList<>(colorMap.keySet()));
        integerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        color_spinner.setAdapter(integerArrayAdapter);
        color_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Integer color=(Integer)parent.getItemAtPosition(position);
                Integer color = colorMap.get(parent.getItemAtPosition(position));
                Toast.makeText(parent.getContext(), "Selected: " + color, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if (checkbox.isChecked()) {
                    deliveryPrice.delete(0, deliveryPrice.length());
                    deliveryPrice.append(price.getText().toString());
                } else {
                    deliveryPrice.delete(0, deliveryPrice.length());
                    deliveryPrice.append(delivery_charges.getText().toString());
                }*/

                realm.executeTransaction(r -> {

                    if (menuItem == null) {

                        Number maxId = realm.where(MenuItem.class).max("id");
                        // If there are no rows, currentId is null, so the next id must be 1
                        // If currentId is not null, increment it by 1
                        long nextId = (maxId == null) ? 1 : maxId.longValue() + 1;
                        // User object created with the new Primary key

                        MenuItem item = realm.createObject(MenuItem.class, nextId);
                        item.name = item_name.getText().toString();
                        item.collectionPrice = Double.parseDouble(price.getText().toString());
                        if (delivery_charges.getVisibility() == View.VISIBLE) {
                            item.deliveryPrice = (delivery_charges.getText().toString().trim().isEmpty()) ? 0 : Double.parseDouble(delivery_charges.toString().trim());
                        } else
                            item.deliveryPrice = item.collectionPrice;
                        item.color = colorMap.get(color_spinner.getSelectedItem());
                        item.menuCategory = menuItem.menuCategory;
                        item.parent = menuItem;
                        item.type = Constants.ITEM_TYPE_FLAVOUR;
                        menuItemFlavours.add(item);
                        item.itemPosition = menuItemFlavours.size() - 1;
                        item.last_updated = new Date().getTime();
                        MenuSubCatAdapter.this.menuItem.flavours.add(item);
                        notifyItemInserted(menuItemFlavours.size() - 1);
                        Log.e(TAG, "------------------------- added subcat: " + item);
                    } else {
                        menuItem.name = item_name.getText().toString();
                        menuItem.collectionPrice = (Double.parseDouble(price.getText().toString()));
                        Log.e(TAG, "----------------modified name: " + item_name.getText() + "; modified price: " + price.getText());
                        if (delivery_charges.getVisibility() == View.VISIBLE) {
                            menuItem.deliveryPrice = (delivery_charges.getText().toString().trim().isEmpty()) ? 0 : Double.parseDouble(delivery_charges.getText().toString().trim());
                        } else
                            menuItem.deliveryPrice = menuItem.collectionPrice;
                        menuItem.color = colorMap.get(color_spinner.getSelectedItem());
                        menuItem.last_updated = new Date().getTime();
                        int pos = menuItemFlavours.indexOf(menuItem);
                        Log.e(TAG, "----------------POS: " + pos + "; position: " + position);
                        notifyItemChanged(position);
                    }
                    modifyMenuItem.dismiss();
                });
            }
        });
        modifyMenuItem.show();
    }

}
