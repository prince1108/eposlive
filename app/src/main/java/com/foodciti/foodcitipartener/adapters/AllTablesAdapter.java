package com.foodciti.foodcitipartener.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.realm_entities.Table;
import com.foodciti.foodcitipartener.utils.CommonMethods;

import java.util.List;

public class AllTablesAdapter extends RecyclerView.Adapter<AllTablesAdapter.TableHolder> {
    private static final String TAG = "AllTablesAdapter";
    private Activity activity;
    private List<Table> tableList;

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public AllTablesAdapter(Activity activity, List<Table> tableList) {
        this.activity = activity;
        this.tableList = tableList;
    }

    @NonNull
    @Override
    public TableHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.table_layout2, parent, false);
        return new TableHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableHolder holder, int position) {
        TableHolder tableHolder = (TableHolder) holder;
        final Table table = tableList.get(position);
        tableHolder.table.setText(table.getName());
            /*if (table.getColor() != -1)
                tableHolder.cardView.setCardBackgroundColor(table.getColor());
            if(table.cartItems.size()>0)
                tableHolder.tableContainer.setBackground(ContextCompat.getDrawable(context, R.drawable.rectangular_selection_border));
            else
                tableHolder.tableContainer.setBackground(null);*/
        if (table.isAvailable()) {
//            tableHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(activity, android.R.color.holo_green_light));
            int color = ContextCompat.getColor(activity, android.R.color.holo_green_light);
            CommonMethods.setGradientDrawable(activity, holder.itemView, color);
            tableHolder.availability.setText("Available");
        } else if (!table.isDirty() && !table.isAvailable()) {
//            tableHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.addItemColor));
            int color = ContextCompat.getColor(activity, R.color.addItemColor);
            CommonMethods.setGradientDrawable(activity, holder.itemView, color);
            tableHolder.availability.setText("Busy");
            if (table.getTotalPersons() > 0)
                tableHolder.availability.append(": " + table.getTotalPersons() + " person(s)");
        } else if (table.isDirty()) {
//            tableHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.colorVividTangerine));
            int color = ContextCompat.getColor(activity, R.color.colorVividTangerine);
            CommonMethods.setGradientDrawable(activity, holder.itemView, color);
            tableHolder.availability.setText("Dirty");
        }
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }

    public class TableHolder extends RecyclerView.ViewHolder {
        TextView table, availability;
        //        CardView cardView;
        ConstraintLayout tableContainer;

        public TableHolder(View itemView) {
            super(itemView);
//            cardView = (CardView) itemView;
            table = itemView.findViewById(R.id.total_order_type_table);
            availability = itemView.findViewById(R.id.availability);

            itemView.setOnClickListener(v -> {
                final int position = getAdapterPosition();
                Table table = tableList.get(position);
                if (callback != null)
                    callback.onSelect(position, table.getId());
            });
        }
    }

    public interface Callback {
        void onSelect(int position, long tableId);
    }
}
