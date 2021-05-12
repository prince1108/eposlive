package com.foodciti.foodcitipartener.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.realm_entities.CustomerInfo;
import com.foodciti.foodcitipartener.utils.StringHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllCustomerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "AllCustomerListAdapter";
    private Context context;
    private List<CustomerInfo> customerInfoList;
    private final Map<String, CustomerInfo> stringCustomerInfoMap=new HashMap<>();

    public Map<String, CustomerInfo> getStringCustomerInfoMap() {
        return stringCustomerInfoMap;
    }

    public List<CustomerInfo> getCustomerInfoList() {
        return customerInfoList;
    }

    public void setCustomerInfoList(List<CustomerInfo> customerInfoList) {
        this.customerInfoList = customerInfoList;
    }

    public AllCustomerListAdapter(Context context, @NonNull List<CustomerInfo> customerInfos) {
        this.context = context;
        this.customerInfoList = customerInfos;
    }

    public void setSelectAll(boolean allChecked) {
        if(customerInfoList!=null) {
            for (CustomerInfo customerInfo : customerInfoList) {
                customerInfo.setSelected(allChecked);
                stringCustomerInfoMap.put(customerInfo.getPhone(), customerInfo);
            }
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_item_list, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_message_customer_layout, parent, false);
        return new CustomerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CustomerInfo customerInfo = customerInfoList.get(position);
        CustomerHolder customerHolder = (CustomerHolder) holder;
        StringBuilder customerAddress = new StringBuilder();
        String houseNo = ((customerInfo.getHouse_no() == null) || customerInfo.getHouse_no().trim().isEmpty()) ? "" : customerInfo.getHouse_no().trim() + ", ";
        customerAddress.append(houseNo);
        if (customerInfo.getPostalInfo() != null) {
            String postCode = ((customerInfo.getPostalInfo().getA_PostCode() == null) || customerInfo.getPostalInfo().getA_PostCode().isEmpty()) ? "" : customerInfo.getPostalInfo().getA_PostCode().trim();
            String address = ((customerInfo.getPostalInfo().getAddress() == null) || customerInfo.getPostalInfo().getAddress().isEmpty()) ? "" : customerInfo.getPostalInfo().getAddress().trim() + ", ";
            customerAddress.append(address).append(postCode);
        }
        if(customerAddress.toString().trim().isEmpty()) {
            customerHolder.customer_name.setText("NA");
        } else {
            customerHolder.customer_name.setText(StringHelper.capitalizeEachWordAfterComma(customerAddress.toString()));
        }
        String phone = (customerInfo.getPhone() == null) ? "NA" : customerInfo.getPhone().trim();
        customerHolder.customer_mobile.setText(phone);
        String delvStatus = (customerInfo.getMsgDeliveryStatus() == null) ? "" : customerInfo.getMsgDeliveryStatus().trim();
        customerHolder.delvStatus.setText(delvStatus);
        customerHolder.checkBox.setChecked(customerInfo.isSelected());
        customerHolder.delvStatus.setText(customerInfo.getMsgDeliveryStatus());
    }

    @Override
    public int getItemCount() {
        if(customerInfoList==null)
            return 0;
        return customerInfoList.size();
    }

    public class CustomerHolder extends RecyclerView.ViewHolder {
        TextView customer_name, customer_mobile, delvStatus;
        CheckBox checkBox;

        public CustomerHolder(View itemView) {
            super(itemView);
            customer_name = itemView.findViewById(R.id.customer_name);
            customer_mobile = itemView.findViewById(R.id.customer_mobile);
            delvStatus = itemView.findViewById(R.id.statusTxt);
            checkBox = itemView.findViewById(R.id.checkbox);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    customerInfoList.get(getAdapterPosition()).setSelected(isChecked);
                    stringCustomerInfoMap.put(customerInfoList.get(getAdapterPosition()).getPhone(), customerInfoList.get(getAdapterPosition()));
                }
            });
        }
    }
}
