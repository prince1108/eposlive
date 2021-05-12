package com.foodciti.foodcitipartener.adapters;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.response.Order;
import com.foodciti.foodcitipartener.utils.Consts;
import com.foodciti.foodcitipartener.utils.OrderSpinner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.foodciti.foodcitipartener.activities.ResturantMainActivityNewPro.cancel_btn;
import static com.foodciti.foodcitipartener.activities.ResturantMainActivityNewPro.ready_btn;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.GenericViewHolder> {
    private Context context;
    private List<Order> orderList;
    private String[] StatusNew = {"COOKING", "READY", "CANCELLED"};


    //Type of data in recycler view
    private OnItemClickListener itemClickListener;


    public interface OnItemClickListener {

        void changeOrderStatus(String orderId, int orderStatus);

        void cancelOrder(String orderId);

        void itemClick(Order order);
    }

    public OrderAdapter(Context context, List<Order> orderList, OnItemClickListener itemClickListener) {
        this.context = context;
        this.itemClickListener = itemClickListener;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderAdapter.GenericViewHolder onCreateViewHolder(
            @NonNull ViewGroup viewGroup, int viewType) {
        //inflate the item according to item type
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_order, viewGroup, false);
        return new GenericViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return (null != orderList ? orderList.size() : 0);
    }


    @Override
    public void onBindViewHolder(@NonNull final OrderAdapter.GenericViewHolder holder,
                                 final int position) {

        final Order orderTest = orderList.get(position);
        holder.orderNum.setText(String.valueOf(orderTest.getOrderNo()));
        holder.orderId.setText(orderTest.getUser().getAddress());
        holder.orderTotal.setText("£ " + round(orderTest.getOrderTotal(), 2));
        holder.orderTime.setText(getDate(orderTest.getOrderTime()));
        holder.orderPaid.setText(orderTest.getOrderPaid());
        holder.orderCurrentStatus.setText(orderTest.getUser().getPhNo());

        Log.e("ORDER_STATUS", "" + orderTest.getOrderStatus());
        if (orderTest.getOrderStatus() == 3) {
            holder.orderCard.setBackground(context.getResources().getDrawable(R.drawable.status_cancel_order));
        } else if (orderTest.getOrderStatus() == 1) {
            holder.orderCard.setBackground(context.getResources().getDrawable(R.drawable.status_complete_order));
        } else if (orderTest.getOrderStatus() == 2) {
            holder.orderCard.setBackground(context.getResources().getDrawable(R.drawable.status_ready_order));
        }

        if (orderTest.getOrderDelivery().equals(Consts.PICK_UP)) {
            holder.orderPickUp.setText("PICKUP");
        } else {
            holder.orderPickUp.setText("DELIVERY");
        }
        if (orderTest.getUser() != null) {
            holder.address.setText(orderTest.getUser().getUserName());
        }
        if (orderTest.getOrderStatus() == 1) {
            holder.spinner.setSelection(0);
        }
        if (orderTest.getOrderStatus() == 2 && orderTest.getOrderDelivery().equals(Consts.PICK_UP)) {
            holder.spinner.setSelection(1);
        }
        if (orderTest.getOrderStatus() == 2 && orderTest.getOrderDelivery().equals(Consts.DELIVERY)) {
            holder.spinner.setSelection(1);
        }
        if (orderTest.getOrderStatus() == 3) {
            holder.spinner.setSelection(2);
        }

        holder.orderCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClick(orderTest);
            }
        });

        if (orderTest.getOrderStatus() == 0 || orderTest.getOrderStatus() == 1) {
            holder.spinner.setEnabled(true);
        } else {
            holder.spinner.setEnabled(true);
        }

        if (orderTest.getForwardedRestaurantId() != null || !TextUtils.isEmpty(orderTest.getForwardedRestaurantId())) {
            holder.forwardedIcon.setVisibility(View.VISIBLE);
        } else {
            holder.forwardedIcon.setVisibility(View.INVISIBLE);
        }
    }

    private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {

            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder myShadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(data, myShadowBuilder, view, 0);
            return false;
        }
    };


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public void updateOrder(String orderId, int orderStatus) {
        for (int i = 0; i < orderList.size(); i++) {
            if (orderId.equals(orderList.get(i).getOrderId())) {
                orderList.get(i).setOrderStatus(orderStatus);
                notifyItemChanged(i);
            }
        }
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

    public void removeOrder(String orderId, int orderStatus) {
        for (int i = 0; i < orderList.size(); i++) {
            if (orderId.equals(orderList.get(i).getOrderId())) {
                orderList.get(i).setOrderStatus(orderStatus);
                orderList.remove(i);
                notifyItemRemoved(i);
            }
        }
    }


    public class GenericViewHolder extends RecyclerView.ViewHolder {
        private TextView orderTotal, address, orderPickUp, orderId, orderTime,
                orderPaid, orderCurrentStatus, orderNum;
        private LinearLayout orderCard;
        public OrderSpinner spinner;
        ImageView forwardedIcon;

        public GenericViewHolder(View itemView) {
            super(itemView);
            orderNum = itemView.findViewById(R.id.orderNumber);
            address = itemView.findViewById(R.id.address);
            orderTime = itemView.findViewById(R.id.orderTime);
            orderPickUp = itemView.findViewById(R.id.orderPickUp);
            orderTotal = itemView.findViewById(R.id.orderTotal);
            orderPaid = itemView.findViewById(R.id.orderPaid);
            orderCard = itemView.findViewById(R.id.orderCard);
            orderId = itemView.findViewById(R.id.orderId);
            spinner = itemView.findViewById(R.id.spinner);
            orderCurrentStatus = itemView.findViewById(R.id.orderCurrentStatus);
            forwardedIcon = itemView.findViewById(R.id.forwarded_icon);


            orderCard.setOnLongClickListener(onLongClickListener);
            // Setting a Custom Adapter to the Spinner
            spinner.setAdapter(new OrderStatusSpinnerAdapter(context, R.layout.spinner_bydefault_row, StatusNew, new OrderStatusSpinnerAdapter.SpinnerItemClickListener() {
                @Override
                public void onSpinnerItemClick(int position) {
                    final Order orderTest = orderList.get(getAdapterPosition());
                    spinner.onDetachedFromWindow();

                    if (position == 1) {
                        itemClickListener.changeOrderStatus(orderTest.getOrderId(), 2);
                    }
                    if (position == 2) {
                        itemClickListener.changeOrderStatus(orderTest.getOrderId(), 3);
//                        itemClickListener.cancelOrder(orderTest.getOrderId());
                    }

                    if (position == 0 && ready_btn) {
                        itemClickListener.changeOrderStatus(orderTest.getOrderId(), 1);
                        ready_btn = false;
                    }


                    if (position == 0 && cancel_btn) {
                        itemClickListener.changeOrderStatus(orderTest.getOrderId(), 1);
                        cancel_btn = false;
                    }


                }
            }));
            //set fontstyle
            orderTotal.setTypeface(Typeface.createFromAsset(context.getAssets(), Consts.COMFORTAA_REGULAR));
            orderTime.setTypeface(Typeface.createFromAsset(context.getAssets(), Consts.COMFORTAA_REGULAR));
            address.setTypeface(Typeface.createFromAsset(context.getAssets(), Consts.COMFORTAA_REGULAR));
            orderPickUp.setTypeface(Typeface.createFromAsset(context.getAssets(), Consts.COMFORTAA_REGULAR));
            orderId.setTypeface(Typeface.createFromAsset(context.getAssets(), Consts.COMFORTAA_REGULAR));
            orderPaid.setTypeface(Typeface.createFromAsset(context.getAssets(), Consts.COMFORTAA_REGULAR));
            orderCurrentStatus.setTypeface(Typeface.createFromAsset(context.getAssets(), Consts.COMFORTAA_BOLD));
        }
    }

}
