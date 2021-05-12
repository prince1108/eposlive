package com.foodciti.foodcitipartener.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.realm_entities.CustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.OrderPostalInfo;
import com.foodciti.foodcitipartener.realm_entities.PostalInfo;

import java.util.List;

import io.realm.Realm;

public class CustomerInfoAdapter extends RecyclerView.Adapter<CustomerInfoAdapter.CustomerInfoHolder> {
    private static final String TAG = "CustomerInfoAdapter";
    private Context context;
    private List<CustomerInfo> customerInfos;
    private Realm realm;

    public CustomerInfoAdapter(Context context, List<CustomerInfo> customerInfos, Realm realm) {
        this.context = context;
        this.customerInfos = customerInfos;
        this.realm = realm;
    }

    @NonNull
    @Override
    public CustomerInfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userinfo_layout, parent, false);
        return new CustomerInfoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerInfoHolder holder, int position) {
        final CustomerInfo customerInfo = customerInfos.get(position);
        String name = (customerInfo.getName().trim().isEmpty())? "NA":customerInfo.getName();

        StringBuilder userInfoBuilder = new StringBuilder();
        if (customerInfo != null) {
            Log.e(TAG, "-------------------userinfo not null--------------------");
            PostalInfo postalInfo = customerInfo.getPostalInfo();
            if (!customerInfo.getHouse_no().trim().isEmpty())
                userInfoBuilder.append(customerInfo.getHouse_no()).append(", ");
            if (postalInfo != null) {
                if(!postalInfo.getA_PostCode().trim().isEmpty())
                    userInfoBuilder.append(postalInfo.getA_PostCode()).append(", ");
                if(!postalInfo.getAddress().trim().isEmpty())
                    userInfoBuilder.append(postalInfo.getAddress());
            }
        } else
            Log.e(TAG, "-------------------userinfo is null--------------------");

        String address = (userInfoBuilder.toString().trim().isEmpty())? "NA": userInfoBuilder.toString();

        holder.phone.setText(customerInfo.getPhone().trim());
        holder.name.setText(name);
        holder.address.setText(address);
    }

    @Override
    public int getItemCount() {
        return customerInfos.size();
    }

    public class CustomerInfoHolder extends RecyclerView.ViewHolder {
        TextView phone, name, address;
        ImageView delete;

        public CustomerInfoHolder(View itemView) {
            super(itemView);
            phone = itemView.findViewById(R.id.phone);
            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            delete = itemView.findViewById(R.id.delete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    final CustomerInfo customerInfo = customerInfos.get(position);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete Record");
                    builder.setMessage("Are you sure you want to delete this record?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            realm.executeTransaction(r -> {
                                customerInfo.deleteFromRealm();
                                notifyItemRemoved(position);
                            });
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }
            });
        }
    }
}
