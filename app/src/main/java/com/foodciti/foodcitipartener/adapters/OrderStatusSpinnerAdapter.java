package com.foodciti.foodcitipartener.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.utils.Consts;


/**
 * Created by quflip1 on 25-01-2018.
 */

public class OrderStatusSpinnerAdapter extends ArrayAdapter {
    private Context context;
    private String[] Status;
    private SpinnerItemClickListener listener;

    public OrderStatusSpinnerAdapter(Context context, int textViewResourceId,
                                     String[] objects, SpinnerItemClickListener listener) {
        super(context, textViewResourceId, objects);
        this.context = context;
        Status = objects;
        this.listener = listener;
    }

    public View getByDefaultView(int position, View convertView,
                                 ViewGroup parent) {

        // Inflating the layout for the custom Spinner
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_default_order_status, parent, false);

        // Declaring and Typecasting the textview in the inflated layout
        TextView tvLanguage = layout.findViewById(R.id.itemStatus);
        ImageView arrow = layout.findViewById(R.id.arrow);
        RelativeLayout background= layout.findViewById(R.id.orderManagmentSpinner);

        if (position == 1 || position == 2){
            //arrow.setVisibility(View.GONE);
           arrow.setVisibility(View.VISIBLE);
            if (position == 2){
                background.setBackgroundColor(ContextCompat.getColor(context, R.color.colorOlivine));
            }else {
                background.setBackgroundColor(ContextCompat.getColor(context, R.color.colorCinnabar));
            }

        }else {
            arrow.setVisibility(View.VISIBLE);
        }
        // Setting the text using the array
        tvLanguage.setText(Status[position]);
        tvLanguage.setTypeface(Typeface.createFromAsset(context.getAssets(), Consts.COMFORTAA_BOLD));
        return layout;
    }

    public View getDropView(final int position, View convertView, ViewGroup parent) {

        // Inflating the layout for the custom Spinner

        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_drop_down_order_status, parent, false);

        // Declaring and Typecasting the textview in the inflated layout
        TextView tvLanguage = layout.findViewById(R.id.itemStatus);

        // Setting the text using the array
        tvLanguage.setText(Status[position]);
        //set fontstyle
        tvLanguage.setTypeface(Typeface.createFromAsset(context.getAssets(), Consts.COMFORTAA_BOLD));

        tvLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSpinnerItemClick(position);

            }
        });

        return layout;
    }

    // It gets a View that displays in the drop down popup the data at the specified position
    @Override
    public View getDropDownView(int position, View convertView,
                                @NonNull ViewGroup parent) {
        return getDropView(position, convertView, parent);
    }

    // It gets a View that displays the data at the specified position
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getByDefaultView(position, convertView, parent);
    }

    public interface SpinnerItemClickListener
    {
        void onSpinnerItemClick(int position);
    }
}
