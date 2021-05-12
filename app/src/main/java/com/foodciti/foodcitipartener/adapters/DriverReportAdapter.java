package com.foodciti.foodcitipartener.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.realm_entities.Driver;
import com.foodciti.foodcitipartener.realm_entities.OrderCustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.OrderPostalInfo;
import com.foodciti.foodcitipartener.realm_entities.Purchase;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.RealmManager;
import com.foodciti.foodcitipartener.utils.StringHelper;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class DriverReportAdapter extends RecyclerView.Adapter<DriverReportAdapter.ReportHolder> {
    private static final String TAG = "DriverReportAdapter";
    private Realm realm;

    private Activity context;
    private List<Driver> drivers;
    private Date start, end;
    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public DriverReportAdapter(Activity context, List<Driver> drivers) {
        this.context = context;
        this.drivers = drivers;
        this.realm = RealmManager.getLocalInstance();
    }

    public DriverReportAdapter(Activity context, List<Driver> drivers, Date start, Date end) {
        this.context = context;
        this.drivers = drivers;
        realm = RealmManager.getLocalInstance();
        this.start = start;
        this.end = end;
    }

    public List<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    @NonNull
    @Override
    public ReportHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.driver_report_view, parent, false);
        return new ReportHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportHolder holder, int position) {
        final Driver driver = drivers.get(position);
        holder.driver.setText(StringHelper.capitalizeEachWord(driver.getDriver_name()));
        holder.vehicleNo.setText(driver.getDriver_vehicle_no().toUpperCase());
        RealmQuery<Purchase> orderRealmQuery = realm.where(Purchase.class).equalTo("driver.id", driver.getId());
        RealmQuery<Purchase> orderRealmQueryForCard = realm.where(Purchase.class).equalTo("driver.id", driver.getId())
                .and().equalTo("paymentMode", Constants.PAYMENT_TYPE_CARD);
        RealmQuery<Purchase> orderRealmQueryForCash = realm.where(Purchase.class).equalTo("driver.id", driver.getId())
                .and().equalTo("paymentMode", Constants.PAYMENT_TYPE_CASH);
        if (start != null && end != null) {
            orderRealmQuery.and().between("timestamp", start.getTime(), end.getTime());
            orderRealmQueryForCard.and().between("timestamp", start.getTime(), end.getTime());
            orderRealmQueryForCash.and().between("timestamp", start.getTime(), end.getTime());
        }
        double revenue = orderRealmQuery.sum("total").doubleValue();
        double totalCard = orderRealmQueryForCard.sum("total").doubleValue();
        double totalCash = orderRealmQueryForCash.sum("total").doubleValue();
        String revenueString = String.format("%.2f", revenue);
        String totalCardString = String.format("%.2f", totalCard);
        String totalCashString = String.format("%.2f", totalCash);
        holder.total.setText(StringHelper.capitalizeEachWord(revenueString));
        holder.totalCard.setText(StringHelper.capitalizeEachWord(totalCardString));
        holder.totalCash.setText(StringHelper.capitalizeEachWord(totalCashString));
        final long totalDeliveries = orderRealmQuery.count();
        holder.totalDeliveries.setText(totalDeliveries + "");
        RealmResults<Purchase> orders = orderRealmQuery.findAll();
        if (orders != null) {
            Number maxId = orders.max("id");
            if (maxId == null)
                return;
            OrderCustomerInfo customerInfo = orderRealmQuery.findAll().where().equalTo("id", orders.max("id").longValue()).findFirst().getOrderCustomerInfo();

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
            holder.lastDelivery.setText(userInfoBuilder);
        }
    }

    @Override
    public int getItemCount() {
        if (drivers == null)
            return 0;
        return drivers.size();
    }

    public class ReportHolder extends RecyclerView.ViewHolder {
        TextView driver, vehicleNo, total, totalCash, totalCard, totalDeliveries, lastDelivery;

        public ReportHolder(View view) {
            super(view);
            driver = view.findViewById(R.id.driver);
            vehicleNo = view.findViewById(R.id.vehicle_no);
            total = view.findViewById(R.id.right_panel_total);
            totalCash = view.findViewById(R.id.total_cash);
            totalCard = view.findViewById(R.id.total_card);
            totalDeliveries = view.findViewById(R.id.total_deliveries);
            lastDelivery = view.findViewById(R.id.lastdelivery);

            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
            view.setOnClickListener(v -> {
                final int position = getAdapterPosition();
                /*final Driver driver=drivers.get(position);
                DriverSalesDetailsDialog driverSalesDetailsDialog=DriverSalesDetailsDialog.newInstance(driver.getId(), start, end);
                driverSalesDetailsDialog.show(fragmentManager, null);*/
                if (callback != null)
                    callback.onClick(position, start, end);
            });
        }
    }

    public interface Callback {
        void onClick(int position, Date start, Date end);
    }
}
