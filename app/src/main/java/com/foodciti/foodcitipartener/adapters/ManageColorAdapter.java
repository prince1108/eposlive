package com.foodciti.foodcitipartener.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.dialogs.CustomAlertDialog;
import com.foodciti.foodcitipartener.realm_entities.ItemColor;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.util.List;

import io.realm.Realm;

public class ManageColorAdapter extends RecyclerView.Adapter<ManageColorAdapter.ColorHolder> {
    private static final String TAG = "ManageColorAdapter";
    private Activity activity;
    private List<ItemColor> itemColorList;

    public ManageColorAdapter(Activity activity, List<ItemColor> itemColorList) {
        this.activity = activity;
        this.itemColorList = itemColorList;
    }

    @NonNull
    @Override
    public ColorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.view_manage_color_layout, parent, false);
        return new ColorHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorHolder holder, int position) {
        final ItemColor itemColor = itemColorList.get(position);
        holder.sNo.setText(itemColor.getId() + "");
        holder.name.setText((itemColor.getName().trim().isEmpty() ? "NA" : itemColor.getName()));
        holder.hexCode.setText(itemColor.getHexCode());
        holder.itemView.setBackgroundColor(Color.parseColor(itemColor.getHexCode()));
    }

    @Override
    public int getItemCount() {
        if (itemColorList == null)
            return 0;
        return itemColorList.size();
    }

    public class ColorHolder extends RecyclerView.ViewHolder {
        TextView sNo, name, hexCode;
        View edit, delete;

        public ColorHolder(View itemView) {
            super(itemView);
            sNo = itemView.findViewById(R.id.s_no);
            name = itemView.findViewById(R.id.name);
            hexCode = itemView.findViewById(R.id.hex_code);
//            edit=itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
            delete.setOnClickListener(v -> {
                final int position = getAdapterPosition();
                final ItemColor itemColor = itemColorList.get(position);
                Realm realm = RealmManager.getLocalInstance();
                CustomAlertDialog customAlertDialog = CustomAlertDialog.getInstance();
                customAlertDialog.setTitle("Delete Color");
                customAlertDialog.setMessage("Are you sure you want to delete this color?");
                customAlertDialog.setPositiveButton("Yes", dialog -> {
                    realm.executeTransaction(r -> {
                        itemColor.deleteFromRealm();
                        notifyItemRemoved(position);
                    });
                    dialog.dismiss();
                });

                customAlertDialog.setNegativeButton("No", dialog -> {
                    dialog.dismiss();
                });

                customAlertDialog.show(((AppCompatActivity) activity).getSupportFragmentManager(), null);
            });
        }
    }
}
