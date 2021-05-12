package com.foodciti.foodcitipartener.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.foodciti.foodcitipartener.R;

import java.util.List;

public class OptionPopupAdapter extends BaseAdapter {
    private List<String> itemsList;

    public OptionPopupAdapter(List<String> itemsList) {
        this.itemsList = itemsList;
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String item = itemsList.get(position);
        holder.text1.setText(item);

        return convertView;
    }

    class ViewHolder {
        TextView text1;

        public ViewHolder(View itemView) {
            text1 = itemView.findViewById(R.id.companyname);
        }
    }
}
