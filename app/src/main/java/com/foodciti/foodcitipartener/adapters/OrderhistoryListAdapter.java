package com.foodciti.foodcitipartener.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.activities.EditOrder;
import com.foodciti.foodcitipartener.realm_entities.CustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.Driver;
import com.foodciti.foodcitipartener.realm_entities.Order;
import com.foodciti.foodcitipartener.realm_entities.PostalInfo;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class OrderhistoryListAdapter extends RecyclerView.Adapter<OrderhistoryListAdapter.HistoryHolder> {
    private static final String TAG = "OrderhistoryListAdapter";
    private Context context;
    private List<Order> orders;
    private static final int HEADER = 0, LIST = 1;
    private SimpleDateFormat currentDateFormat;
    private OnItemClickListener itemClickListener;
    private Realm realm;

    public OrderhistoryListAdapter(Context context, RealmResults<Order> orders) {
        this.context = context;
        this.orders = new ArrayList<>(orders);
        currentDateFormat = new SimpleDateFormat(context.getString(R.string.current_date_format));
        if (!(context instanceof OnItemClickListener))
            throw new IllegalStateException("context must implement OnItemClickListener");
        itemClickListener = (OnItemClickListener) context;
        this.realm = RealmManager.getLocalInstance();
    }

    public OrderhistoryListAdapter(Context context, RealmResults<Order> orders, OnItemClickListener itemClickListener, Realm realm) {
        this.context = context;
        this.orders = new ArrayList<>(orders);
        currentDateFormat = new SimpleDateFormat(context.getString(R.string.current_date_format));
        this.itemClickListener = itemClickListener;
        this.realm = realm;
    }

    public List<Order> getOrders() {
        return orders;
    }

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderhistory_header_layout, parent, false);
                return new HistoryHolder(view, viewType);
            }
            case LIST: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderhistory_layout, parent, false);
                return new HistoryHolder(view, viewType);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryHolder holder, int position) {

        /*switch (holder.getItemViewType()){
         *//* case HEADER: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderhistory_header_layout, parent, false);
                return new HistoryHolder(view, viewType);
            }*//*
            case LIST: {

            }
        }*/
        Order order = orders.get(position);
        holder.s_no.setText(order.getId() + "");
        holder.date.setText(currentDateFormat.format(new Date(order.getTimestamp())));
        boolean userInfoAvailable = false;
        if (order.getCustomerInfo() != null) {
            userInfoAvailable = true;
            CustomerInfo customerInfo = order.getCustomerInfo();
            StringBuilder userInfoBuilder = new StringBuilder();
            userInfoBuilder.append(customerInfo.getPhone());
            if (!customerInfo.getName().trim().isEmpty())
                userInfoBuilder.append(", ").append(customerInfo.getName().trim());
            if (customerInfo.getHouse_no() != null && !customerInfo.getHouse_no().trim().isEmpty())
                userInfoBuilder.append(", ").append(customerInfo.getHouse_no().trim());
            if (customerInfo.getPostalInfo() != null) {
                PostalInfo postalInfo = customerInfo.getPostalInfo();
                if (!postalInfo.getAddress().trim().isEmpty())
                    userInfoBuilder.append(", ").append(postalInfo.getAddress().trim());
                userInfoBuilder.append(", ").append(postalInfo.getA_PostCode());
            }

            holder.userInfo.setText(userInfoBuilder.toString());
           /* holder.message.setVisibility(View.VISIBLE);
            holder.msgNA.setVisibility(View.GONE);*/
        } else {
            holder.userInfo.setText("NA");
            /*holder.message.setVisibility(View.GONE);
            holder.msgNA.setVisibility(View.VISIBLE);*/
        }
        holder.orderType.setText(order.getOrderType());
        holder.paymentMode.setText(order.getPaymentMode());
        holder.total.setText(context.getString(R.string.pound_symbol) + " " + order.getTotal());

        Driver deliveredBy = order.getDriver();
        if (deliveredBy != null) {
            holder.deliveredBy.setText(deliveredBy.getDriver_name().trim());
            holder.cardView.setCardBackgroundColor(deliveredBy.getColor());
            invertComponentColors(holder, false, userInfoAvailable);
            if (order.getOrderType().equals(Constants.TYPE_DELIVERY))
                holder.setDelivered.setVisibility(View.VISIBLE);
            else
                holder.setDelivered.setVisibility(View.INVISIBLE);
        } else {
            holder.deliveredBy.setText("NA");
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white));
            invertComponentColors(holder, true, userInfoAvailable);
        }
    }

    private void invertComponentColors(HistoryHolder holder, boolean invert, boolean userInfoAvailable) {
        if (invert) {
            holder.s_no.setTextColor(Color.BLACK);
            holder.date.setTextColor(Color.BLACK);
            holder.userInfo.setTextColor(Color.BLACK);
            holder.total.setTextColor(Color.BLACK);
            holder.orderType.setTextColor(Color.BLACK);
            holder.paymentMode.setTextColor(Color.BLACK);
            holder.deliveredBy.setTextColor(Color.BLACK);
            if (userInfoAvailable) {
                holder.message.setVisibility(View.VISIBLE);
                holder.msgNA.setVisibility(View.INVISIBLE);
                holder.message.setBackgroundResource(R.drawable.ic_message_green_24dp);
            } else {
                holder.message.setVisibility(View.INVISIBLE);
                holder.msgNA.setVisibility(View.VISIBLE);
                holder.msgNA.setTextColor(Color.BLACK);
            }
            holder.edit.setBackgroundResource(R.drawable.ic_edit_black_60dp);
            holder.cancel.setBackgroundResource(R.drawable.ic_cancel_red_24dp);
            holder.orderDetails.setBackgroundResource(R.drawable.ic_info_red_24dp);
        } else {
            holder.s_no.setTextColor(Color.WHITE);
            holder.date.setTextColor(Color.WHITE);
            holder.userInfo.setTextColor(Color.WHITE);
            holder.total.setTextColor(Color.WHITE);
            holder.orderType.setTextColor(Color.WHITE);
            holder.paymentMode.setTextColor(Color.WHITE);
            holder.deliveredBy.setTextColor(Color.WHITE);
            if (userInfoAvailable) {
                holder.message.setVisibility(View.VISIBLE);
                holder.msgNA.setVisibility(View.INVISIBLE);
                holder.message.setBackgroundResource(R.drawable.ic_message_white_24dp);
            } else {
                holder.message.setVisibility(View.INVISIBLE);
                holder.msgNA.setVisibility(View.VISIBLE);
                holder.msgNA.setTextColor(Color.WHITE);
            }
            holder.edit.setBackgroundResource(R.drawable.ic_edit_white_24dp);
            holder.cancel.setBackgroundResource(R.drawable.ic_cancel_white_24dp);
            holder.orderDetails.setBackgroundResource(R.drawable.ic_info_white_24dp);
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class HistoryHolder extends RecyclerView.ViewHolder {
        TextView s_no, date, userInfo, total, orderType, paymentMode, deliveredBy, msgNA;
        ImageView message, edit, cancel, orderDetails, setDelivered;
        CardView cardView;
        int viewType;

        public HistoryHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;

            if (viewType == HEADER) {

            } else if (viewType == LIST) {
                cardView = (CardView) itemView;
                s_no = itemView.findViewById(R.id.s_no);
                date = itemView.findViewById(R.id.date);
                userInfo = itemView.findViewById(R.id.userinfo);
                total = itemView.findViewById(R.id.right_panel_total);
                orderType = itemView.findViewById(R.id.order_type);
                paymentMode = itemView.findViewById(R.id.payment_mode);
                deliveredBy = itemView.findViewById(R.id.delivered_by);
                message = itemView.findViewById(R.id.message);
                edit = itemView.findViewById(R.id.edit);
                cancel = itemView.findViewById(R.id.cancel);
                orderDetails = itemView.findViewById(R.id.order_details);
                msgNA = itemView.findViewById(R.id.msgNA);
                setDelivered = itemView.findViewById(R.id.set_delivered);

                message.setOnClickListener(v -> {
                    final int position = getAdapterPosition();
                    final Order order = orders.get(position);
                    if (order.getCustomerInfo() != null) {
                        if (order.getCustomerInfo().getPhone().equalsIgnoreCase("")) {
                            // Toast.makeText(context,"N")
                            Toast.makeText(context, "Not a valid mobile No.", Toast.LENGTH_LONG).show();
                        } else {
                            itemClickListener.messageUser(order.getCustomerInfo().getPhone().trim());
                        }
                    } else {
                        Toast.makeText(context, "No Customer Info and phone record found", Toast.LENGTH_LONG).show();
                    }
                });

                edit.setOnClickListener(v -> {
                    final int position = getAdapterPosition();
                    final Order order = orders.get(position);
                    Intent intent = new Intent(context, EditOrder.class);
                    intent.putExtra("ORDER_ID", order.getId());
                    context.startActivity(intent);
                });

                cancel.setOnClickListener(v -> {
                    final int position = getAdapterPosition();
                    final Order order = orders.get(position);
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

                orderDetails.setOnClickListener(v -> {
                    final int position = getAdapterPosition();
                    final Order order = orders.get(position);
                    itemClickListener.clickedItem(order.getId());
                });

                itemView.setOnClickListener(v -> {
                    final int position = getAdapterPosition();
                    final Order order = orders.get(position);
                    itemClickListener.showDriverList(order.getId());
                });

                setDelivered.setOnClickListener(v -> {
                    final int position = getAdapterPosition();
                    final Order order = orders.get(position);
                    realm.executeTransaction(realm -> {
                        order.setDelivered(true);
                        orders.remove(order);
                        notifyItemRemoved(position);
                        itemClickListener.onOrderDelivered(order.getId());
                    });
                });
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
       /* if(position==0)
            return HEADER;*/
        return LIST;
    }

    public interface OnItemClickListener {
        void clickedItem(long orderid);

        void showDriverList(long orderId);

        void messageUser(String phoneNo);

        void onOrderDelivered(long orderId);
    }
}
