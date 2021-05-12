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
import com.foodciti.foodcitipartener.realm_entities.AddonItemCategory;
import com.foodciti.foodcitipartener.realm_entities.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class ConnectAddonItemAdapter extends ArrayAdapter {

    private List<AddonItemCategory> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
    }

    public ConnectAddonItemAdapter(List<AddonItemCategory> data, Context context) {
        super(context, R.layout.addonitem_row, data);
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.addonitem_row, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            result = convertView;
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        viewHolder.txtName.setText(dataSet.get(position).getName());



        return result;
    }
}
