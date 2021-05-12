package com.foodciti.foodcitipartener.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.realm_entities.PostalInfo;

import java.util.ArrayList;
import java.util.List;

public class PincodeArrayAdapter extends ArrayAdapter<PostalInfo> {
    // Your sent context
    private Context context;
    // Your custom values for the spinner (PostalInfo)
    private List<PostalInfo> values, tempItems, suggestions;
    ;
    private int resource;

    public PincodeArrayAdapter(@NonNull Context context, int resource, List<PostalInfo> values) {
        super(context, resource);
        this.context = context;
        this.values = values;
        this.resource = resource;
        tempItems = new ArrayList<>(values); // this makes the difference.
        suggestions = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Nullable
    @Override
    public PostalInfo getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        try {
            if (convertView == null) {
                // inflate the layout
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(resource, parent, false);
            }
            TextView postcode = convertView.findViewById(R.id.postcode);
            postcode.setText(values.get(position).getA_PostCode());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }


    /*@Override
    public Filter getFilter() {
        return nameFilter;
    }

    *//**
     * Custom Filter implementation for custom suggestions we provide.
     *//*
    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((PostalInfo) resultValue).getA_PostCode();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                values.clear();
                for (PostalInfo postalInfo : tempItems) {
                    if (postalInfo.getA_PostCode().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(postalInfo);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<PostalInfo> filterList = (ArrayList<PostalInfo>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (PostalInfo postalInfo : filterList) {
                    add(postalInfo);
                    notifyDataSetChanged();
                }
            }
        }
    };*/

}
