package com.foodciti.foodcitipartener.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.response.Order;
import com.foodciti.foodcitipartener.utils.Consts;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ForwardedOrdersAdapter extends RecyclerView.Adapter<ForwardedOrdersAdapter.ViewHolder>
{
    private Context context;
    private List<Order> orderList;
    private ForwardOrderClickListener listener;

    public ForwardedOrdersAdapter(Context context, ForwardOrderClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup viewGroup, int viewType) {
        //inflate the item according to item type
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.forwarded_orders_adapter_item, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return (null != orderList ? orderList.size() : 0);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder,
                                 final int position) {

        final Order orderTest = orderList.get(position);
        holder.orderId.setText(orderTest.getUser().getAddress());
        holder.orderTotal.setText("£ " + round(orderTest.getOrderTotal(), 2));
        holder.orderTime.setText(getDate(orderTest.getOrderTime()));
        holder.orderPaid.setText(orderTest.getOrderPaid());
        holder.orderCurrentStatus.setText(orderTest.getUser().getPhNo());

        Log.e("ORDER_STATUS", "" + orderTest.getOrderStatus());
        if (orderTest.getOrderStatus() == 3) {
            holder.orderCard.setBackground(context.getResources().getDrawable(R.drawable.status_cancel_order));
            holder.orderStatus.setText("CANCELLED");
        } else if (orderTest.getOrderStatus() == 1) {
            holder.orderCard.setBackground(context.getResources().getDrawable(R.drawable.status_complete_order));
            holder.orderStatus.setText("ACCEPTED");
        } else if (orderTest.getOrderStatus() == 2) {
            holder.orderCard.setBackground(context.getResources().getDrawable(R.drawable.status_ready_order));
            holder.orderStatus.setText("COMPLETED");
        } else if(orderTest.getOrderStatus() == 0){
            holder.orderCard.setBackground(context.getResources().getDrawable(R.drawable.status_pending_order));
            holder.orderStatus.setText("PENDING");
        }


        if (orderTest.getOrderDelivery().equals(Consts.PICK_UP)) {
            holder.orderPickUp.setText("PICKUP");
        } else {
            holder.orderPickUp.setText("DELIVERY");
        }
        if (orderTest.getUser() != null) {
            holder.address.setText(orderTest.getUser().getUserName());
        }

    }


    public void addAllData(List<Order> data)
    {
        orderList = data;

        notifyDataSetChanged();
    }




    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private String getDate(String OurDate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = formatter.parse(OurDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm a", Locale.getDefault()); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            OurDate = dateFormatter.format(value);

            Log.d("OurDate", OurDate);
        } catch (Exception e) {
            OurDate = "00-00-0000 00:00";
        }
        return OurDate;
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView orderTotal, address, orderPickUp, orderId, orderTime,
                orderPaid, orderCurrentStatus, orderStatus;
        private LinearLayout orderCard;

        public ViewHolder(View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address);
            orderTime = itemView.findViewById(R.id.orderTime);
            orderPickUp = itemView.findViewById(R.id.orderPickUp);
            orderTotal = itemView.findViewById(R.id.orderTotal);
            orderPaid = itemView.findViewById(R.id.orderPaid);
            orderCard = itemView.findViewById(R.id.orderCard);
            orderId = itemView.findViewById(R.id.orderId);
            orderCurrentStatus = itemView.findViewById(R.id.orderCurrentStatus);
            orderStatus = itemView.findViewById(R.id.forwarded_order_status);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(listener != null)
            {
                listener.onForwardedItemClick(orderList.get(getAdapterPosition()));
            }
        }
    }


    public interface ForwardOrderClickListener{
        void onForwardedItemClick(Order item);
    }

}
