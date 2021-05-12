package com.foodciti.foodcitipartener.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.realm_entities.Addon;
import com.foodciti.foodcitipartener.realm_entities.Order;
import com.foodciti.foodcitipartener.realm_entities.OrderAddon;
import com.foodciti.foodcitipartener.realm_entities.OrderTuple;
import com.foodciti.foodcitipartener.realm_entities.Purchase;
import com.foodciti.foodcitipartener.realm_entities.PurchaseEntry;
import com.foodciti.foodcitipartener.utils.Constants;

import java.util.List;

public class OrderhistoryDetailsAdapter extends RecyclerView.Adapter<OrderhistoryDetailsAdapter.HistoryDetailHolder> {
    private static final String TAG = "OrderhistoryDetailsAdapter";
    private Context context;
    private List<PurchaseEntry> orderTuples;
    private Purchase order;

    public OrderhistoryDetailsAdapter(Context context, Purchase order) {
        this.context = context;
        this.order = order;
        orderTuples = order.getPurchaseEntries();
    }

    @NonNull
    @Override
    public HistoryDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderhistory_detail_listview, parent, false);
        return new HistoryDetailHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryDetailHolder holder, int position) {
        PurchaseEntry o = orderTuples.get(position);
        holder.qty.setText(o.getCount() + "");
        holder.item.setText(o.getOrderMenuItem().name);
        StringBuilder str = new StringBuilder();
        double addonPrice = 0;
        for (OrderAddon addon : o.getOrderAddons()) {
            String prefix = (addon.isNoAddon) ? "-" : "+";
            str.append(prefix).append(addon.name).append(" ("+addon.price+")").append("\n");
            addonPrice += addon.price;
        }
        holder.addons.setText(str);
        String poundSymbol = context.getString(R.string.pound_symbol);
        if (order.getOrderType().equals(Constants.TYPE_COLLECTION)) {
            String price = String.format("%.2f", (o.getCount() * o.getOrderMenuItem().collectionPrice)+addonPrice);
            holder.price.setText(poundSymbol + " " + price);
        }
        else {
            String price = String.format("%.2f", (o.getCount() * o.getOrderMenuItem().deliveryPrice)+addonPrice);
            holder.price.setText(poundSymbol + " " + price);
        }

        holder.additionalNote.setText(o.getAdditionalNote());
    }

    @Override
    public int getItemCount() {
        return orderTuples.size();
    }

    public class HistoryDetailHolder extends RecyclerView.ViewHolder {
        TextView qty, item, addons, price, additionalNote;

        public HistoryDetailHolder(View itemView) {
            super(itemView);
            qty = itemView.findViewById(R.id.qty);
            item = itemView.findViewById(R.id.item);
            addons = itemView.findViewById(R.id.addons);
            price = itemView.findViewById(R.id.price);
            additionalNote = itemView.findViewById(R.id.additional_note);
        }
    }
}
