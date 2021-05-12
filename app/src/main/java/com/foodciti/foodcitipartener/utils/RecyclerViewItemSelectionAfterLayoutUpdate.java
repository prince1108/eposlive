package com.foodciti.foodcitipartener.utils;

import android.view.View;
import android.view.ViewTreeObserver;

import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;

public class RecyclerViewItemSelectionAfterLayoutUpdate {
    private static final Map<RecyclerView, Callback> CALLBACK_MAP = new HashMap();

    public static void addCallback(RecyclerView recyclerView, Callback callback) {
        CALLBACK_MAP.put(recyclerView, callback);
    }

    public static void on(RecyclerView rv, int position) {
        rv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (rv.getAdapter().getItemCount() > 0) {
                    RecyclerView.ViewHolder viewHolder = rv.findViewHolderForAdapterPosition(position);
                    if(viewHolder!=null) {
                        viewHolder.itemView.performClick();
                    }
                    Callback rvCallback = CALLBACK_MAP.get(rv);
                    if (rvCallback != null)
                        rvCallback.onFinish();
                }
                rv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                CALLBACK_MAP.remove(rv);
            }
        });
    }

    public static void on(RecyclerView rv) {
        rv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (rv.getAdapter().getItemCount() > 0) {
                    RecyclerView.ViewHolder viewHolder = rv.findViewHolderForAdapterPosition(0);
                    if(viewHolder!=null)
                        viewHolder.itemView.performClick();
                    Callback rvCallback = CALLBACK_MAP.get(rv);
                    if (rvCallback != null)
                        rvCallback.onFinish();
                }
                rv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                CALLBACK_MAP.remove(rv);
            }
        });
    }

    public interface Callback {
        void onFinish();
    }
}