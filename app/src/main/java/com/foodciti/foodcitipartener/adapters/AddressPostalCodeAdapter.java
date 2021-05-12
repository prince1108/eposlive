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
import com.foodciti.foodcitipartener.realm_entities.PostalInfo;
import com.foodciti.foodcitipartener.realm_entities.Table;
import com.foodciti.foodcitipartener.utils.CommonMethods;

import java.util.List;


public class AddressPostalCodeAdapter extends RecyclerView.Adapter<AddressPostalCodeAdapter.TableHolder> {
    private static final String TAG = "AllTablesAdapter";
    private Activity activity;
    private List<PostalInfo> tableList;

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public AddressPostalCodeAdapter(Activity activity, List<PostalInfo> tableList) {
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
        tableHolder.table.setText(tableList.get(position).getHouseno());
        int color = ContextCompat.getColor(activity, android.R.color.holo_green_dark);
        CommonMethods.setGradientDrawable(activity, holder.itemView, color);
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
                String address = tableList.get(position).getHouseno();
                if (callback != null)
                    callback.onSelect(address);
            });
        }
    }

    public interface Callback {
        void onSelect(String address);
    }
}

