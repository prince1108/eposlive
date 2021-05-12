package com.foodciti.foodcitipartener.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.dialogs.EditRemarkDialog;
import com.foodciti.foodcitipartener.realm_entities.CustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.PostalInfo;
import com.foodciti.foodcitipartener.realm_entities.RemarkType;
import com.foodciti.foodcitipartener.utils.RealmManager;
import com.foodciti.foodcitipartener.utils.StringHelper;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;

public class WarningListAdapter extends RecyclerView.Adapter<WarningListAdapter.WarningViewHolder> {
    private static final String TAG = "WarningListAdapter";
    private List<CustomerInfo> customerInfos;
    private Activity context;
    private Realm realm;

    public WarningListAdapter(Activity context, List<CustomerInfo> customerInfos) {
        this.customerInfos = customerInfos;
        this.context = context;
        this.realm = RealmManager.getLocalInstance();
    }

    @NonNull
    @Override
    public WarningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_warninglist_layout, parent, false);
        return new WarningViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WarningViewHolder holder, int position) {
        RealmQuery<RemarkType> remarkTypeRealmQuery = realm.where(RemarkType.class);

        final CustomerInfo customerInfo = customerInfos.get(position);
        holder.warningId.setText(customerInfo.getId() + "");
        holder.phone.setText(customerInfo.getPhone());
        holder.type.setText(customerInfo.getRemarkStatus());
        holder.remarks.setText(customerInfo.getRemarks());

        final StringBuilder stringBuilder = new StringBuilder();
        if (!customerInfo.getName().trim().isEmpty()) {
            stringBuilder.append(StringHelper.capitalizeEachWord(customerInfo.getName().trim()));
        }
        if (!customerInfo.getHouse_no().trim().isEmpty()) {
            stringBuilder.append(", ").append(customerInfo.getHouse_no());
        }
        if (customerInfo.getPostalInfo() != null) {
            final PostalInfo postalInfo = customerInfo.getPostalInfo();
            if (postalInfo.getAddress() != null)
                stringBuilder.append(", \n").append(postalInfo.getAddress());
            if (postalInfo.getA_PostCode() != null)
                stringBuilder.append(", ").append(postalInfo.getA_PostCode());
        }

        holder.customer.setText(StringHelper.capitalizeEachWordAfterComma(stringBuilder.toString()));
        if (!customerInfo.getPhone().trim().isEmpty())
            holder.phone.setText(customerInfo.getPhone());

        RemarkType remarkType = remarkTypeRealmQuery.equalTo("type", customerInfo.getRemarkStatus()).findFirst();
        if (remarkType != null)
            holder.cardView.setCardBackgroundColor(remarkType.getColor());
        else
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
    }

    @Override
    public int getItemCount() {
        if (customerInfos == null)
            return 0;
        return customerInfos.size();
    }

    public class WarningViewHolder extends RecyclerView.ViewHolder {
        TextView warningId, phone, customer, type, remarks;
        CardView cardView;

        public WarningViewHolder(View itemView) {
            super(itemView);
            warningId = itemView.findViewById(R.id.warning_id);
            phone = itemView.findViewById(R.id.phone);
            customer = itemView.findViewById(R.id.customer);
            type = itemView.findViewById(R.id.type);
            remarks = itemView.findViewById(R.id.remarks);

            cardView = (CardView) itemView;

            itemView.setOnClickListener(v -> {
                final int position = getAdapterPosition();
                CustomerInfo customerInfo = customerInfos.get(position);
                EditRemarkDialog editRemarkDialog = EditRemarkDialog.newInstance(customerInfo.getId());
                editRemarkDialog.setTitle(customerInfo.getPhone());
                editRemarkDialog.setCallback(() -> {
                    notifyItemChanged(position);
                });

                AppCompatActivity appCompatActivity = (AppCompatActivity) context;
                editRemarkDialog.show(appCompatActivity.getSupportFragmentManager(), null);
            });
        }
    }
}
