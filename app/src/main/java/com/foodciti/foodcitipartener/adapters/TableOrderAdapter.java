package com.foodciti.foodcitipartener.adapters;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.realm_entities.Table;
import com.foodciti.foodcitipartener.utils.CommonMethods;

import java.util.ArrayList;
import java.util.List;

public class TableOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "TableOrderAdapter";
    private List<Table> tableList;
    private Context context;
    private Clicklistener callback;
    private static int selectedTableIndex = -1;

    private final int VIEW_TABLE = 0;
    private final int VIEW_NULL = 1;


    public TableOrderAdapter(Context context, List<Table> tableList) {
        this.tableList = tableList;
        this.context = context;
        if (!(context instanceof Clicklistener))
            throw new RuntimeException("context must implement TableOrderAdapter.Clicklistener");
        callback = (Clicklistener) context;
    }

    public int getSelectedTableIndex() {
        return selectedTableIndex;
    }

    public Table getSelectedTable() {
        if (selectedTableIndex == -1)
            return null;
        return tableList.get(selectedTableIndex);
    }

    public static void clearSelection() {
        selectedTableIndex = -1;
    }

    @Override
    public int getItemViewType(int position) {
        if (tableList.size() == 0)
            return VIEW_NULL;
        return VIEW_TABLE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TABLE:
                view = layoutInflater.inflate(R.layout.table_layout, parent, false);
                return new TableHolder(view);
            case VIEW_NULL:
                view = layoutInflater.inflate(R.layout.null_view, parent, false);
                return new NullViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TABLE) {
            TableHolder tableHolder = (TableHolder) holder;
            final Table table = tableList.get(position);
            tableHolder.table.setText(table.getName());

            if (table.isAvailable()) {
//                tableHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_light));
                int color = ContextCompat.getColor(context, android.R.color.holo_green_light);
                CommonMethods.setGradientDrawable(context, holder.itemView, color);
                tableHolder.availability.setText("Available");
            } else if (!table.isDirty() && !table.isAvailable()) {
//                tableHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.addItemColor));
                int color = ContextCompat.getColor(context, R.color.addItemColor);
                CommonMethods.setGradientDrawable(context, holder.itemView, color);
                tableHolder.availability.setText("Busy");
                if (table.getTotalPersons() > 0)
                    tableHolder.availability.append(": " + table.getTotalPersons() + " person(s)");
            } else if (table.isDirty()) {
//                tableHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorVividTangerine));
                int color = ContextCompat.getColor(context, R.color.colorVividTangerine);
                CommonMethods.setGradientDrawable(context, holder.itemView, color);
                tableHolder.availability.setText("Dirty");
            }

        }
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }

    public class NullViewHolder extends RecyclerView.ViewHolder {
        TextView message;

        public NullViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class TableHolder extends RecyclerView.ViewHolder {
        TextView table, availability;
        //        CardView cardView;
        ConstraintLayout tableContainer;

        public TableHolder(@NonNull View itemView) {
            super(itemView);
//            cardView = (CardView) itemView;
            table = itemView.findViewById(R.id.total_order_type_table);
            availability = itemView.findViewById(R.id.availability);
            itemView.setOnClickListener(v -> {
                final int pos = getAdapterPosition();
                final Table table = tableList.get(pos);
                selectedTableIndex = pos;
                callback.onClickTable(table.getId());
                Log.e(TAG, "-------------------selectedIndex: " + selectedTableIndex);
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                final Table table = tableList.get(position);
                if (!table.isDirty() && !table.isAvailable()) {
                    selectedTableIndex = position;
                    List<String> strings = new ArrayList<>();
                    strings.add("Edit No Of Persons");
                    PopupWindow optionPopup = optionPopupWindow(v, strings, position);
                    optionPopup.showAsDropDown(v, 0, 0);
                    return true;
                }
                return false;
            });

            tableContainer = itemView.findViewById(R.id.tableContainer);
        }
    }

    private PopupWindow optionPopupWindow(View parent, final List<String> optionList, final int itemPosition) {

        // initialize a pop up window type
        PopupWindow optionPopup = new PopupWindow(context);

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
                if (item.equalsIgnoreCase("Edit No Of Persons")) {
                    callback.onLongClickTable(tableList.get(itemPosition).getId());
                }
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

    public interface Clicklistener {
        public void onClickTable(long tableId);

        public void onLongClickTable(long tableId);
    }
}
