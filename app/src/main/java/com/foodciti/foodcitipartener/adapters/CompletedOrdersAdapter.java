package com.foodciti.foodcitipartener.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.activities.EditOrder;
import com.foodciti.foodcitipartener.realm_entities.CustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.Order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

public class CompletedOrdersAdapter extends RecyclerView.Adapter<CompletedOrdersAdapter.OrderHolder> {
    private Context context;
    private List<Order> orderList;
    private SimpleDateFormat simpleDateFormat;
    private OnClickListener onClickListener;
    private Realm realm;

    public CompletedOrdersAdapter(Context context, List<Order> orders, OnClickListener onClickListener, Realm realm) {
        this.context = context;
        this.orderList = orders;
        simpleDateFormat = new SimpleDateFormat(context.getString(R.string.current_date_format));
        this.onClickListener = onClickListener;
        this.realm = realm;
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.completed_orders_layout, parent, false);
        return new OrderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHolder holder, int position) {
        Order order = orderList.get(position);
        holder.sNo.setText(order.getId() + "");
        holder.date.setText(simpleDateFormat.format(new Date(order.getTimestamp())));
        holder.paymentMode.setText(order.getPaymentMode());
        holder.orderType.setText(order.getOrderType());
        holder.total.setText(order.getTotal() + "");
        CustomerInfo customerInfo = order.getCustomerInfo();
        StringBuilder userinfoBuilder = new StringBuilder();
        if (customerInfo != null) {
            userinfoBuilder.append(customerInfo.getPhone().trim());
            if (customerInfo.getName() != null && !customerInfo.getName().trim().isEmpty())
                userinfoBuilder.append(", ").append(customerInfo.getName());
            holder.message.setVisibility(View.VISIBLE);
            holder.userInfo.setText(userinfoBuilder);
        } else {
            holder.message.setVisibility(View.INVISIBLE);
            holder.userInfo.setText("NA");
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class OrderHolder extends RecyclerView.ViewHolder {
        TextView sNo, userInfo, total, paymentMode, date, orderType;
        ImageView orderDetails, message, edit, delete;

        public OrderHolder(View itemView) {
            super(itemView);
            sNo = itemView.findViewById(R.id.s_no);
            date = itemView.findViewById(R.id.date);
            userInfo = itemView.findViewById(R.id.userinfo);
            orderDetails = itemView.findViewById(R.id.order_details);
            total = itemView.findViewById(R.id.right_panel_total);
            orderType = itemView.findViewById(R.id.order_type);
            paymentMode = itemView.findViewById(R.id.payment_mode);
            message = itemView.findViewById(R.id.message);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);

            orderDetails.setOnClickListener(v -> {
                Order order = orderList.get(getAdapterPosition());
                onClickListener.onClickShowDetails(order.getId());
            });

            edit.setOnClickListener(v -> {
                final int position = getAdapterPosition();
                final Order order = orderList.get(position);
                Intent intent = new Intent(context, EditOrder.class);
                intent.putExtra("ORDER_ID", order.getId());
                context.startActivity(intent);
            });

            delete.setOnClickListener(v -> {
                final int position = getAdapterPosition();
                final Order order = orderList.get(position);
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(false);
                builder.setMessage("Are you sure you want to cancel this order?");
                builder.setTitle("Cancel Order");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    realm.executeTransaction(r -> {
                        order.deleteFromRealm();
                    });
                    notifyItemChanged(position);
                    dialog.dismiss();
                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                });
                builder.create().show();
            });
        }
    }

    public interface OnClickListener {
        void onClickShowDetails(long orderId);
    }
}
