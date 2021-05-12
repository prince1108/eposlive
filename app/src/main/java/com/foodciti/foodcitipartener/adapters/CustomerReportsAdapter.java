package com.foodciti.foodcitipartener.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.dialogs.CustomAlertDialog;
import com.foodciti.foodcitipartener.dialogs.UpdateCustomerDetails;
import com.foodciti.foodcitipartener.realm_entities.CustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.Order;
import com.foodciti.foodcitipartener.realm_entities.PostalInfo;
import com.foodciti.foodcitipartener.realm_entities.Purchase;
import com.foodciti.foodcitipartener.utils.RealmManager;
import com.foodciti.foodcitipartener.utils.StringHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;

public class CustomerReportsAdapter extends RecyclerView.Adapter<CustomerReportsAdapter.ReportViewHolder> {
    private static final String TAG = "CustomerReportsAdapter";
    private Context context;
    private RealmList<CustomerInfo> customerInfoList;
    private Realm realm;
    private SimpleDateFormat simpleDateFormat;

    public CustomerReportsAdapter(Context context, RealmList<CustomerInfo> customerInfoList) {
        this.context = context;
        this.customerInfoList = customerInfoList;
        this.realm = RealmManager.getLocalInstance();
        simpleDateFormat = new SimpleDateFormat(context.getString(R.string.current_date_format));
    }

    public RealmList<CustomerInfo> getCustomerInfoList() {
        return customerInfoList;
    }

    public void setCustomerInfoList(RealmList<CustomerInfo> customerInfoList) {
        this.customerInfoList = customerInfoList;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.customer_report_view, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        final CustomerInfo customerInfo = customerInfoList.get(position);
        RealmQuery<Purchase> orderRealmQuery = realm.where(Purchase.class).equalTo("orderCustomerInfo.phone", customerInfo.getPhone());
        if (orderRealmQuery.count() == 0)
            return;
        final String name = (customerInfo.getName().trim().isEmpty()) ? "NA" : customerInfo.getName().trim();
        final StringBuilder stringBuilder = new StringBuilder();
        if (!customerInfo.getHouse_no().trim().isEmpty()) {
            stringBuilder.append(customerInfo.getHouse_no());
        }
        if (customerInfo.getPostalInfo() != null) {
            final PostalInfo postalInfo = customerInfo.getPostalInfo();
            if (postalInfo.getAddress() != null)
                stringBuilder.append(", ").append(postalInfo.getAddress());
            if (postalInfo.getA_PostCode() != null)
                stringBuilder.append(", ").append(postalInfo.getA_PostCode());
        }
        holder.name.setText(StringHelper.capitalizeEachWord(name));
        holder.address.setText(StringHelper.capitalizeEachWordAfterComma(stringBuilder.toString()));
        if (!customerInfo.getPhone().trim().isEmpty())
            holder.phone.setText(customerInfo.getPhone());

        final long totalOrders = orderRealmQuery.count();
        holder.total_orders.setText(totalOrders + "");

        Number lastOrder = orderRealmQuery.max("id");
        long lastOrderId = -1;
        if (lastOrder != null) {
            lastOrderId = lastOrder.longValue();
            long timeStamp = orderRealmQuery.and().equalTo("id", lastOrderId).findFirst().getTimestamp();
            holder.last_order_time.setText(simpleDateFormat.format(new Date(timeStamp)));
        }
    }

    @Override
    public int getItemCount() {
        if (customerInfoList == null)
            return 0;
        return customerInfoList.size();
    }

    public class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView name, address, phone, total_orders, last_order_time;
        ImageView edit, delete;
        CardView cardView;

        public ReportViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            phone = itemView.findViewById(R.id.phone);
            total_orders = itemView.findViewById(R.id.total_orders);
            last_order_time = itemView.findViewById(R.id.last_order_time);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);

            AppCompatActivity appCompatActivity = (AppCompatActivity) context;

            edit.setOnClickListener(v -> {
                final int position = getAdapterPosition();
                final CustomerInfo customerInfo = customerInfoList.get(position);
                UpdateCustomerDetails updateCustomerDetails = UpdateCustomerDetails.getInstance(customerInfo.getId(), null);
                updateCustomerDetails.setmListener((id, orderType, note) -> {
                    notifyItemChanged(position);
                });
                updateCustomerDetails.show(appCompatActivity.getSupportFragmentManager(), null);
            });
            delete.setOnClickListener(v -> {
                CustomAlertDialog customAlertDialog = CustomAlertDialog.getInstance();
                customAlertDialog.setTitle("Delete Customer");
                customAlertDialog.setMessage("Are you sure you want to remove this customer?");
                customAlertDialog.setPositiveButton("Yes", dialog -> {
                    final int position = getAdapterPosition();
                    final CustomerInfo customerInfo = customerInfoList.get(position);
                    realm.executeTransaction(r -> {
                        customerInfo.deleteFromRealm();
                        notifyItemRemoved(position);
                    });
                    dialog.dismiss();
                });
                customAlertDialog.setNegativeButton("No", dialog -> {
                    dialog.dismiss();
                });
                customAlertDialog.show(appCompatActivity.getSupportFragmentManager(), null);
            });
        }
    }
}
