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
import com.foodciti.foodcitipartener.utils.RealmManager;
import com.foodciti.foodcitipartener.utils.StringHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

public class SalesOrderAdapter extends RecyclerView.Adapter<SalesOrderAdapter.OrderHolder> {
    private static final String TAG = "SalesOrderAdapter";
    private Activity context;
    private List<Purchase> orders;
    private Calendar calendar;
    private Callback callback;
    private SimpleDateFormat dateFormat, dateFormat2;
    private Realm realm;

    public SalesOrderAdapter(Activity context, List<Purchase> orders) {
        this.context = context;
        this.orders = orders;
        calendar = Calendar.getInstance();
        /*if (!(context instanceof Callback))
            throw new RuntimeException("context must implement SalesOrderAdapter.Callback");
        callback = (Callback) context;*/
        dateFormat = new SimpleDateFormat(context.getString(R.string.current_date_format));
        dateFormat2 = new SimpleDateFormat("dd");
        this.realm = RealmManager.getLocalInstance();
    }

    public List<Purchase> getOrders() {
        return orders;
    }

    public void setOrders(List<Purchase> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sales_report_view, parent, false);
        return new OrderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHolder holder, int position) {
        final Purchase order = orders.get(position);
        final String type = order.getOrderType();
        final long orderNo = order.getId();
        final long orderTime = order.getTimestamp();
        final long deliveryTime = order.getDeliveryTime();
        final String paymentStatus = order.getPaymentMode();
        final long elapsedTime = calendar.getTimeInMillis() - orderTime;
        final double seconds = (elapsedTime / 1000);
        final double minutes = (elapsedTime / 60000);
        final double hours = (elapsedTime / 3600000);
        final double days = (elapsedTime / (3600000 * 24));

        String timeElapsed = "";
        if (days > 0)
            timeElapsed = days + " day(s)";
        else if (hours > 0)
            timeElapsed = hours + " hour(s)";
        else if (minutes > 0)
            timeElapsed = minutes + " min(s)";
        else
            timeElapsed = seconds + " sec";

        final String status = order.isPaid() ? "Completed" : "Pending";
        final double total = order.getTotal();
        OrderCustomerInfo customerInfo = order.getOrderCustomerInfo();

//        Table table = null;
        String tableName=null;
        if (type.equalsIgnoreCase(Constants.TYPE_TABLE)) {
            tableName = order.getTableName();
            holder.type.setText(tableName);
        } else {
            holder.type.setText(type);
        }
        holder.orderNo.setText(orderNo + "");
        holder.date.setText(dateFormat.format(new Date(orderTime)));
        holder.delivery.setText("");
        holder.paymentStatus.setText(paymentStatus);
        holder.elapsedTime.setText(timeElapsed);
        holder.status.setText(status);
        holder.total.setText(String.format("%.2f", total));

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
        holder.customer.setText(StringHelper.capitalizeEachWordAfterComma(userInfoBuilder.toString()));

/*        Driver driver=order.getDriver();
        if(driver!=null) {
            if(driver.getColor()!=-1)
                holder.cardView.setCardBackgroundColor(driver.getColor());
        }*/
    }

    @Override
    public int getItemCount() {
        if (orders == null)
            return 0;
        return orders.size();
    }

    public class OrderHolder extends RecyclerView.ViewHolder {
        TextView type, orderNo, date, delivery, paymentStatus, elapsedTime, status, total, customer;
        CardView cardView;

        public OrderHolder(View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.type);
            orderNo = itemView.findViewById(R.id.orderno);
            date = itemView.findViewById(R.id.date);
            delivery = itemView.findViewById(R.id.delivered_by);
            paymentStatus = itemView.findViewById(R.id.payment_mode);
            elapsedTime = itemView.findViewById(R.id.elapsedtime);
            status = itemView.findViewById(R.id.status);
            total = itemView.findViewById(R.id.right_panel_total);
            customer = itemView.findViewById(R.id.customer);
            cardView = (CardView) itemView;
           /* edit = itemView.findViewById(R.id.edit);
            edit.setOnClickListener(v -> {
                final int position = getAdapterPosition();
                final Order order = orders.get(position);
                Intent intent = new Intent(context, EditOrder.class);
                intent.putExtra("ORDER_ID", order.getId());
                context.startActivity(intent);
            });

            delete = itemView.findViewById(R.id.delete);
            delete.setOnClickListener(v -> {
                final int position = getAdapterPosition();
                final Order order = orders.get(position);

                CustomAlertDialog alertDialog = CustomAlertDialog.getInstance();
                alertDialog.setTitle("Delete Order");
                alertDialog.setMessage("Are you sure you want to delete this order?");
                alertDialog.setPositiveButton("Yes", dialog -> {
                    realm.executeTransaction(r -> {
                        order.deleteFromRealm();
                    });
                    notifyItemChanged(position);
                    dialog.dismiss();
                });
                alertDialog.setNegativeButton("No", dialog -> {
                    dialog.dismiss();
                });

                FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                alertDialog.show(fm, null);
            });*/

            itemView.setOnClickListener(v -> {
                final int position = getAdapterPosition();
                final Purchase order = orders.get(position);
//                callback.onClickDetails(order.getId());
            });

            itemView.setOnLongClickListener(v -> {
                final int position = getAdapterPosition();
                final Purchase order = orders.get(position);
//                callback.onLongPress(order.getId());
                return true;
            });
        }
    }

    public interface Callback {
        public void onClickDetails(long id);

        public void onLongPress(long id);
    }
}
