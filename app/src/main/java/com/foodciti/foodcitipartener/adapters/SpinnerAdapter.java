package com.foodciti.foodcitipartener.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.realm_entities.RemarkType;

import java.util.List;

public class SpinnerAdapter extends BaseAdapter {
    private Context context;
    private List<RemarkType> dataList;
    private SpinnerItemListener listener;

    public SpinnerAdapter(Context context, List<RemarkType> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    public int indexOf(String type) {
        int index = -1;
        for (RemarkType rt : dataList) {
            if (rt.getType().equals(type.trim())) {
                index = dataList.indexOf(rt);
                break;
            }
        }
        return index;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.e("GET_VIEW", "" + position + ", " + convertView);
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.remarks_spinner_view, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        RemarkType item = dataList.get(position);
        holder.item.setText(item.getType());

        GradientDrawable bgShape = (GradientDrawable) holder.item.getBackground();
        bgShape.setColor(item.getColor());
//        holder.item.setBackgroundColor(ContextCompat.getColor(context, item.getRemarksColor()));

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // Inflating the layout for the custom Spinner
        DropDownViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.remarks_spinner_dropdown_view, parent, false);
            holder = new DropDownViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (DropDownViewHolder) convertView.getTag();
        }

        RemarkType item = dataList.get(position);
        holder.dropdownItem.setText(item.getType());
        holder.dropdownItem.setBackgroundColor(item.getColor());

        return convertView;
    }

    class ViewHolder {
        TextView item;

        public ViewHolder(View view) {
            item = view.findViewById(R.id.spinner_text);
        }
    }

    class DropDownViewHolder {
        TextView dropdownItem;

        public DropDownViewHolder(View view) {
            dropdownItem = view.findViewById(R.id.spinner_dropdown_text);
        }
    }

    public interface SpinnerItemListener {
        void onItemClick(int position);
    }
}
