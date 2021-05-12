package com.foodciti.foodcitipartener.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.realm_entities.Driver;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class DriverDetailsListAdapter extends RecyclerView.Adapter<DriverDetailsListAdapter.DriverHolder> {
    private static final String TAG = "DriverListAdapter";
    private Activity context;
    private SimpleDateFormat simpleDateFormat;
    private RealmResults<Driver> drivers;
    private OnClickListener clickListener;
    private Realm realm;

    public DriverDetailsListAdapter(Activity context, RealmResults<Driver> drivers) {
        this.context = context;
        simpleDateFormat = new SimpleDateFormat(context.getString(R.string.current_date_format));
        this.drivers = drivers;
        if (!(context instanceof OnClickListener))
            throw new IllegalStateException("context must implement DriverDetailsListAdapter.OnClickListener");
        clickListener = (OnClickListener) context;
        this.realm = RealmManager.getLocalInstance();
    }

    @NonNull
    @Override
    public DriverHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.driver_details_layout, parent, false);
        return new DriverHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverHolder holder, int position) {
        Driver driver = drivers.get(position);
        holder.driverName.setText(driver.getDriver_name());
        holder.cardView.setCardBackgroundColor(driver.getColor());
        holder.registrationDate.setText(simpleDateFormat.format(new Date(driver.getRegistrationDate())));
        holder.vehicleNo.setText(driver.getDriver_vehicle_no());
        holder.enabled.setChecked(driver.isEnabled());
        Log.e(TAG, "--------driver enabled?? " + driver.isEnabled());
        Log.e(TAG, "-------background color: " + driver.getColor());
        Log.e(TAG, "-------hex color: " + String.format("#%06X", (0xFFFFFF & driver.getColor())));
    }

    @Override
    public int getItemCount() {
        return drivers.size();
    }

    public class DriverHolder extends RecyclerView.ViewHolder {
        TextView driverName, vehicleNo, registrationDate;
        ImageView edit, delete;
        CheckBox enabled;
        CardView cardView;

        public DriverHolder(View itemView) {
            super(itemView);
            driverName = itemView.findViewById(R.id.driver_name);
            vehicleNo = itemView.findViewById(R.id.vehicle_no);
            registrationDate = itemView.findViewById(R.id.date_registered);
            enabled = itemView.findViewById(R.id.driver_enabled);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
            cardView = (CardView) itemView;
            /*cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onClick(drivers.get(getAdapterPosition()).getId());
                }
            });*/

            enabled.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "----------checkbox clicked-------------");
                    final int pos = getAdapterPosition();
                    final Driver driver = drivers.get(pos);
                    if (enabled.isChecked()) {
                        Log.e(TAG, "----------checkbox is checked-------------");
                        /*realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                driver.setEnabled(false);
                            }
                        });*/
                        realm.executeTransaction(realm -> {
                            driver.setEnabled(true);
                        });
                    } else {
                        Log.e(TAG, "----------checkbox not checked-------------");
                        realm.executeTransaction(realm -> {
                            driver.setEnabled(false);
                        });
                    }
                    /*notifyItemChanged(pos);*/
                }
            });
            delete.setOnClickListener(v -> {
                final int pos = getAdapterPosition();
                final Driver driver = drivers.get(pos);
                realm.executeTransaction(realm1 -> {
                    driver.deleteFromRealm();
                });
            });
        }
    }

    public interface OnClickListener {
        void onClick(long driverId);
    }
}
