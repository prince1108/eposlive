package com.foodciti.foodcitipartener.utils;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatSpinner;

public class OrderSpinner extends AppCompatSpinner
{
    public OrderSpinner(Context context) {
        super(context);
    }

    public OrderSpinner(Context context, int mode) {
        super(context, mode);
    }

    public OrderSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
