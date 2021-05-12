package com.foodciti.foodcitipartener.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.realm_entities.PostalInfo;
import com.foodciti.foodcitipartener.utils.StringHelper;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private static final String TAG = "AddressAdapter";
    private Context context;
    private List<PostalInfo> postalInfos;
    private AddressAdapterCAllback addressAdapterCAllback;

    public List<PostalInfo> getPostalInfos() {
        return postalInfos;
    }

    public void setPostalInfos(List<PostalInfo> postalInfos) {
        this.postalInfos = postalInfos;
    }

    public AddressAdapter(Context context, List<PostalInfo> postalInfos) {
        this.context = context;
        this.postalInfos = postalInfos;
        if(!(context instanceof AddressAdapterCAllback))
            throw new RuntimeException("context must implement AddressAdapter.AddressAdapterCAllback");
        addressAdapterCAllback = (AddressAdapterCAllback) context;
    }

    public AddressAdapter(Context context, List<PostalInfo> postalInfos, AddressAdapterCAllback adapterCAllback) {
        this.context = context;
        this.postalInfos = postalInfos;
        addressAdapterCAllback = adapterCAllback;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.customer_data_adapter_item, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        PostalInfo postalInfo = postalInfos.get(position);

        StringBuilder address = new StringBuilder();
        address.append(postalInfo.getAddress()).append(",").append(postalInfo.getA_PostCode());
        holder.addressTxt.setText(StringHelper.capitalizeEachWordAfterComma(address.toString()));

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorDarkGray));
        }
    }

    @Override
    public int getItemCount() {
        if(postalInfos == null)
            return 0;
        return postalInfos.size();
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView addressTxt;
        View delete;

        public AddressViewHolder(View itemView) {
            super(itemView);
            addressTxt = itemView.findViewById(R.id.addressTxt);
            delete = itemView.findViewById(R.id.remove_item);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addressAdapterCAllback.onClickAddress(getAdapterPosition());
                }
            });

            delete.setOnClickListener(v->{
                final int position = getAdapterPosition();
                final PostalInfo postalInfo = postalInfos.get(position);
                addressAdapterCAllback.onDeletePostalInfo(postalInfo.getId(), position);
            });
        }
    }

    public interface AddressAdapterCAllback {
        public void onClickAddress(int position);
        void onDeletePostalInfo(long id, int position);
    }
}
