package com.foodciti.foodcitipartener.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.realm_entities.ItemColor;

import java.util.List;

public class ColorItemAdapter extends RecyclerView.Adapter<ColorItemAdapter.ColorHolder> {
    private static final String TAG = "ColorItemAdapter";
    private Activity activity;
    private List<ItemColor> itemColorList;
    private Callback callback;

    private int currentelectionIndex = -1;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public ColorItemAdapter(Activity activity, List<ItemColor> itemColorList) {
        this.activity = activity;
        this.itemColorList = itemColorList;
    }

    public List<ItemColor> getItemColorList() {
        return itemColorList;
    }

    public void setItemColorList(List<ItemColor> itemColorList) {
        this.itemColorList = itemColorList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ColorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.view_coloritem_layout, parent, false);
        return new ColorHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorHolder holder, int position) {
        final ItemColor itemColor = itemColorList.get(position);
        if (itemColor.getName().trim().isEmpty())
            holder.colorName.setText(itemColor.getHexCode());
        else
            holder.colorName.setText(itemColor.getName());

        holder.colorFill.setBackgroundColor(Color.parseColor(itemColor.getHexCode()));

        if (currentelectionIndex == position)
            holder.checked.setVisibility(View.VISIBLE);
        else
            holder.checked.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        if (itemColorList == null)
            return 0;
        return itemColorList.size();
    }

    public class ColorHolder extends RecyclerView.ViewHolder {
        View colorFill;
        TextView colorName;
        View checked;

        public ColorHolder(View itemView) {
            super(itemView);
            colorFill = itemView.findViewById(R.id.colorFill);
            colorName = itemView.findViewById(R.id.colorName);
            checked = itemView.findViewById(R.id.selected);
            itemView.setOnClickListener(v -> {
                final int position = getAdapterPosition();
                currentelectionIndex = position;
                notifyDataSetChanged();
                final long colorId = itemColorList.get(position).getId();
                if (callback != null)
                    callback.onSelectColor(colorId);
            });
        }
    }

    public interface Callback {
        void onSelectColor(long id);
    }
}
