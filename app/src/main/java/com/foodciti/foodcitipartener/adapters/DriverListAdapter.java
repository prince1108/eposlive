package com.foodciti.foodcitipartener.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.realm_entities.Driver;
import com.foodciti.foodcitipartener.realm_entities.Order;
import com.foodciti.foodcitipartener.realm_entities.Purchase;

import java.util.List;

public class DriverListAdapter extends RecyclerView.Adapter<DriverListAdapter.DriverHolder> {
    private static final String TAG = "DriverListAdapter";
    private Context context;
    private List<Driver> drivers;
    private Purchase order;
    private OnClickListener clickListener;

    public DriverListAdapter(Context context, List<Driver> drivers) {
        this.context = context;
        this.drivers = drivers;
        if (!(context instanceof OnClickListener))
            throw new IllegalStateException("context must implement DriverListAdapter.OnClickListener");
        clickListener = (OnClickListener) context;
    }

    public DriverListAdapter(Context context, List<Driver> drivers, Purchase order) {
        this.context = context;
        this.order = order;
        this.drivers = drivers;
        if (!(context instanceof OnClickListener))
            throw new IllegalStateException("context must implement DriverListAdapter.OnClickListener");
        clickListener = (OnClickListener) context;
    }

    public DriverListAdapter(Context context, List<Driver> drivers, Purchase order, OnClickListener onClickListener) {
        this.context = context;
        this.order = order;
        this.drivers = drivers;
        clickListener = onClickListener;
    }

    public List<Driver> getDrivers() {
        return drivers;
    }

    @NonNull
    @Override
    public DriverHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.driver_show_view, parent, false);
        return new DriverHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverHolder holder, int position) {
        Driver driver = drivers.get(position);
        holder.driver.setText(driver.getDriver_name());
        holder.cardView.setCardBackgroundColor(driver.getColor());
        Log.e(TAG, "-------background color: " + driver.getColor());
        holder.vehicleNo.setText(driver.getDriver_vehicle_no().trim());
        if (order != null) {
            if (order.getDriver() != null && order.getDriver().equals(driver))
                holder.selected.setVisibility(View.VISIBLE);
            else
                holder.selected.setVisibility(View.INVISIBLE);
        } else
            holder.selected.setVisibility(View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return drivers.size();
    }

    public class DriverHolder extends RecyclerView.ViewHolder {
        TextView driver, vehicleNo;
        ImageView driverPhoto, selected;
        CardView cardView;

        public DriverHolder(View itemView) {
            super(itemView);
            driver = itemView.findViewById(R.id.driver);
            vehicleNo = itemView.findViewById(R.id.vehicle_no);
            selected = itemView.findViewById(R.id.selected);
            driverPhoto = itemView.findViewById(R.id.driver_photo);
            cardView = (CardView) itemView;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(drivers.get(getAdapterPosition()).getId());
                }
            });
        }
    }

    public interface OnClickListener {
        void onClick(long driverId);
    }
}
