package com.foodciti.foodcitipartener.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.response.OrderedItem;
import com.foodciti.foodcitipartener.response.SubItem;
import com.foodciti.foodcitipartener.response.SubOptions;
import com.foodciti.foodcitipartener.utils.Consts;

import java.util.List;

/**
 * Created by quflip1 on 15-04-2017.
 */

public class OrderInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private List<OrderedItem> orderedItemList;


    //Type of data in recycler view
    private OnItemClickListener itemClickListener;


    public interface OnItemClickListener {

        void onDataReceived(byte[] buffer, int size);

        void itemClick(OrderedItem orderedItem);
    }

    public OrderInfoAdapter(Context context, List<OrderedItem> orderedItems, OnItemClickListener itemClickListener) {
        this.context = context;
        this.itemClickListener = itemClickListener;
        this.orderedItemList = orderedItems;

    }

    @Override
    public int getItemCount() {
        return (null != orderedItemList ? orderedItemList.size() : 0);
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


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,
                                 final int position) {

        if(orderedItemList!=null&&orderedItemList.size()>0){


        OrderedItem orderedItem = orderedItemList.get(position);

        if (orderedItem.getItemData().getItemName() != null){
            ((GenericViewHolder) holder).itemText.setText(String.valueOf(orderedItem.getItemData().getItemName()));
        }

        if (orderedItem.getQuantity() != null){
            ((GenericViewHolder) holder).itemQuantity.setText(orderedItem.getQuantity());
        }


        //set fontstyle
        ((GenericViewHolder) holder).itemText.setTypeface(Typeface.createFromAsset(context.getAssets(), Consts.COMFORTAA_BOLD));
        ((GenericViewHolder) holder).itemQuantity.setTypeface(Typeface.createFromAsset(context.getAssets(), Consts.COMFORTAA_BOLD));

       /* ((GenericViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.itemClick(orderedItemList.get(position));
            }
        });*/

        if (orderedItem.getSubItemList().size() > 0) {
            for (int i = 0; i < orderedItem.getSubItemList().size(); i++)
            {
                SubItem subItemObj = orderedItem.getSubItemList().get(i);
                List<SubOptions> subOptionItemsList = subItemObj.getSubOptions();
                TextView tv1 = new TextView(context);
                String subItemStr = subItemObj.getOrderedQuantity() + " X " + subItemObj.getItemName();
                if(subOptionItemsList != null && subOptionItemsList.size() > 0){
                    Log.e("SUBITEMS_LIST",""+subOptionItemsList.size());
                    for(int j=0; j<subOptionItemsList.size(); j++){
                        SubOptions subItems = subOptionItemsList.get(j);
                        subItemStr = subItemStr +"\n"+"     - "+subItems.getName();
                    }
                }
                tv1.setText(subItemStr);
                tv1.setTypeface(Typeface.createFromAsset(context.getAssets(), Consts.COMFORTAA_REGULAR));
                ((GenericViewHolder) holder).subItemLayout.addView(tv1);

            }
        } }
    }




    public class GenericViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout subItemLayout;
        public TextView itemText, itemQuantity;

        public GenericViewHolder(View itemView) {
            super(itemView);
            subItemLayout = itemView.findViewById(R.id.subItemName);
            itemText = itemView.findViewById(R.id.itemText);
            itemQuantity = itemView.findViewById(R.id.itemQuantity);
        }
    }

}