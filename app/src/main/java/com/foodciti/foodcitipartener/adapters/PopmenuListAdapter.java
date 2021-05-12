package com.foodciti.foodcitipartener.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.realm_entities.MenuItem;

import java.util.List;

public class PopmenuListAdapter extends BaseAdapter {
    private List<MenuItem> itemsList;

    public PopmenuListAdapter(List<MenuItem> items) {
        itemsList = items;
    }

    @Override
    public int getCount() {
        return itemsList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.extra_item_choices_view, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MenuItem item = itemsList.get(position);
        holder.flavourName.setText(item.name);
        String flavPrice = "£" + item.price;
        holder.flavourPrice.setText(flavPrice);

        return convertView;
    }

    class ViewHolder {
        TextView flavourName, flavourPrice;

        public ViewHolder(View itemView) {
            flavourName = itemView.findViewById(R.id.item_name);
            flavourPrice = itemView.findViewById(R.id.item_price);
        }
    }
}