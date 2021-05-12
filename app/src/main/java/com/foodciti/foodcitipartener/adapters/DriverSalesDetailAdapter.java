package com.foodciti.foodcitipartener.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.realm_entities.OrderCustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.OrderPostalInfo;
import com.foodciti.foodcitipartener.realm_entities.Purchase;
import com.foodciti.foodcitipartener.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DriverSalesDetailAdapter extends RecyclerView.Adapter<DriverSalesDetailAdapter.SalesDetailHolder> {
    private static final String TAG = "DriverSalesAdapter";
    private Activity context;
    private List<Purchase> orderRealmList;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    public DriverSalesDetailAdapter(Activity context, List<Purchase> orderRealmList) {
        this.context = context;
        this.orderRealmList = orderRealmList;
        dateFormat = new SimpleDateFormat(context.getString(R.string.current_date_format));
        calendar = Calendar.getInstance();
    }

    public List<Purchase> getOrderRealmList() {
        return orderRealmList;
    }

    public void setOrderRealmList(List<Purchase> orderRealmList) {
        this.orderRealmList = orderRealmList;
    }

    @NonNull
    @Override
    public SalesDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.driver_sales_detail_view, parent, false);
        return new SalesDetailHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalesDetailHolder holder, int position) {
        final Purchase order = orderRealmList.get(position);
        final String type = order.getOrderType();
        final long orderNo = order.getId();
        final long orderTime = order.getTimestamp();
        final long deliveryTime = order.getDeliveryTime();
        final String paymentMode = order.getPaymentMode();
        final long elapsedTime = calendar.getTimeInMillis() - orderTime;
        final String status = order.isPaid() ? "Completed" : "Pending";
        final double total = order.getTotal();
        OrderCustomerInfo customerInfo = order.getOrderCustomerInfo();

//        Table table = null;
        String tableName=null;
        if (type.equalsIgnoreCase(Constants.TYPE_TABLE)) {
            tableName = order.getTableName();
            holder.order_type.setText(tableName);
        } else {
            holder.order_type.setText(type);
        }
        holder.order_no.setText(orderNo + "");
        holder.date.setText(dateFormat.format(new Date(orderTime)));
        holder.payment_type.setText(paymentMode);
        final String amountStr = String.format("%.2f", total);
        holder.amount.setText(amountStr);

        StringBuilder userInfoBuilder = new StringBuilder();
        if (customerInfo != null) {
            Log.e(TAG, "-------------------userinfo not null--------------------");
            OrderPostalInfo postalInfo = customerInfo.getOrderPostalInfo();
            if (!customerInfo.getName().trim().isEmpty())
                userInfoBuilder.append(customerInfo.getName()).append(", ");
            if (!customerInfo.getPhone().trim().isEmpty())
                userInfoBuilder.append(customerInfo.getPhone()).append(", ");
            if (!customerInfo.getHouse_no().trim().isEmpty())
                userInfoBuilder.append(customerInfo.getHouse_no()).append(", ");
            if (postalInfo != null) {
                userInfoBuilder.append(postalInfo.getA_PostCode()).append(", ");
                userInfoBuilder.append(postalInfo.getAddress());
            }
        } else {
            Log.e(TAG, "-------------------userinfo is null--------------------");
            userInfoBuilder.append("NA");
        }
        holder.customer.setText(userInfoBuilder);

    }

    @Override
    public int getItemCount() {
        if (orderRealmList == null)
            return 0;
        return orderRealmList.size();
    }

    public class SalesDetailHolder extends RecyclerView.ViewHolder {
        TextView order_no, amount, customer, date, order_type, payment_type;
        CardView cardView;

        public SalesDetailHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            order_no = itemView.findViewById(R.id.order_no);
            amount = itemView.findViewById(R.id.amount);
            customer = itemView.findViewById(R.id.customer);
            date = itemView.findViewById(R.id.date);
            order_type = itemView.findViewById(R.id.order_type);
            payment_type = itemView.findViewById(R.id.payment_type);
        }
    }
}
