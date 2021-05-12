package com.foodciti.foodcitipartener.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.response.SubItem;
import com.foodciti.foodcitipartener.utils.Consts;

import java.util.List;


/**
 * Created by quflip1 on 28-11-2017.
 */

public class ItemSubAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private List<SubItem> subItemList;


    //Type of data in recycler view
    private OnItemClickListener itemClickListener;


    public interface OnItemClickListener {
        void onItemClick(SubItem item);
    }

    public ItemSubAdapter(Context context, List<SubItem> subItemList, OnItemClickListener itemClickListener) {
        this.context = context;
        this.itemClickListener = itemClickListener;
        this.subItemList = subItemList;

    }

    @Override
    public int getItemCount() {
        return (null != subItemList ? subItemList.size() : 0);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,
                                 final int position) {

        SubItem subItem = subItemList.get(position);


        ((GenericViewHolder) holder).itemText.setText(String.valueOf(subItem.getItemName()));
        ((GenericViewHolder) holder).itemQuantity.setText(String.valueOf(subItem.getOrderedQuantity()));

        //set fontstyle
        ((GenericViewHolder) holder).itemText.setTypeface(Typeface.createFromAsset(context.getAssets(), Consts.COMFORTAA_BOLD));
        ((GenericViewHolder) holder).itemQuantity.setTypeface(Typeface.createFromAsset(context.getAssets(), Consts.COMFORTAA_BOLD));


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            ViewGroup viewGroup, int viewType) {

        //inflate the item according to item type
        View itemView;
        //inflate your layout and pass it to view holder
        itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_order_item, viewGroup, false);
        return new GenericViewHolder(itemView);
    }


    public class GenericViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout orderLayout;
        public TextView itemText, itemQuantity;

        public GenericViewHolder(View itemView) {
            super(itemView);
            orderLayout = itemView.findViewById(R.id.orderMain);
            itemText = itemView.findViewById(R.id.itemText);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
        }
    }

}
