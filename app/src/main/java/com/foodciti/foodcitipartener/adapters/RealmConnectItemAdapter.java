package com.foodciti.foodcitipartener.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.realm_entities.Addon;
import com.foodciti.foodcitipartener.realm_entities.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class RealmConnectItemAdapter extends ArrayAdapter {

    private List<MenuItem> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
    }

    public RealmConnectItemAdapter(List<MenuItem> data, Context context) {
        super(context, R.layout.connect_item_row, data);
        this.dataSet = data;
        this.mContext = context;
    }


    @Override
    public int getCount() {
        return dataSet.size();
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.connect_item_row, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            result = convertView;
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        viewHolder.txtName.setText(dataSet.get(position).name);



        return result;
    }
}
