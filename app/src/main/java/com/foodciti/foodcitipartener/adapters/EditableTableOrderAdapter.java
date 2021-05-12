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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.realm_entities.Table;
import com.foodciti.foodcitipartener.utils.CommonMethods;

import java.util.ArrayList;
import java.util.List;

public class EditableTableOrderAdapter extends RecyclerView.Adapter<EditableTableOrderAdapter.TableHolder> {
    private static final String TAG = "TableOrderAdapter";
    private Context context;
    private List<Table> tables;
    private static final int VIEW_TABLE = 0;
    private static final int VIEW_ADD_TABLE = 1;

    private Callback callback;

    public EditableTableOrderAdapter(Context context, List<Table> tables) {
        this.context = context;
        this.tables = tables;
        if (!(context instanceof Callback))
            throw new IllegalStateException("context must implement EditableTableOrderAdapter.Callback");
        callback = (Callback) context;
    }

    public List<Table> getTables() {
        return tables;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == tables.size())
            return VIEW_ADD_TABLE;
        return VIEW_TABLE;
    }

    @NonNull
    @Override
    public TableHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == VIEW_ADD_TABLE)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_add_table, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.table_layout, parent, false);
        return new TableHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull TableHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TABLE) {
            final Table table = tables.get(position);
            holder.table.setText(table.getName());
            /*if (table.getColor() != -1)
                holder.cardView.setCardBackgroundColor(table.getColor());*/
//            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.teal));
            int color = ContextCompat.getColor(context, R.color.teal);
            CommonMethods.setGradientDrawable(context, holder.itemView, color);
        }
    }

    @Override
    public int getItemCount() {
        return tables.size() + 1;
    }

    public class TableHolder extends RecyclerView.ViewHolder {
        TextView table;
        //        CardView cardView;
        int viewType;

        public TableHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            if (viewType == VIEW_TABLE) {
//                cardView = (CardView) itemView;
                table = itemView.findViewById(R.id.total_order_type_table);
                itemView.setOnLongClickListener(v -> {
                    int position = getAdapterPosition();
                    List<String> strings = new ArrayList<>();
                    strings.add("Edit");
                    strings.add("Delete");
                    PopupWindow optionPopup = optionPopupWindow(v, strings, position);
                    optionPopup.showAsDropDown(v, 0, 0);
                    return true;
                });
            } else {
                itemView.setOnClickListener(v -> {
                    Log.e(TAG, "----------------------Add table clicked-----------------------------");
//                    final Table table=tables.get(getAdapterPosition());
                    callback.onAddTable();
                });
            }
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
                if (item.equalsIgnoreCase("Edit")) {
                    callback.onLongClickTable(itemPosition, tables.get(itemPosition));
                } else if (item.equalsIgnoreCase("delete")) {
                    callback.onDelete(itemPosition, tables.get(itemPosition));
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

    public interface Callback {
        public void onAddTable();

        public void onLongClickTable(int position, Table table);

        public void onDelete(int position, Table table);
    }
}
