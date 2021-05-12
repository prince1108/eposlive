package com.foodciti.foodcitipartener.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.models.SplitBill;
import com.foodciti.foodcitipartener.realm_entities.Addon;
import com.foodciti.foodcitipartener.realm_entities.CartItem;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class SplitBillAdapter extends RecyclerView.Adapter<SplitBillAdapter.SplitBillHolder> {
    private static final String TAG = "SplitBillAdapter";
    private Activity context;
    private List<SplitBill> splitBills;
    private boolean dragEnabled = false;

    private Callback callback;

    public SplitBillAdapter(Activity context, List<SplitBill> splitBills) {
        this.context = context;
        this.splitBills = splitBills;
    }

    public void setDragEnabled(boolean dragEnabled) {
        this.dragEnabled = dragEnabled;
    }

    public List<SplitBill> getSplitBills() {
        return splitBills;
    }

    public void setSplitBills(List<SplitBill> splitBills) {
        this.splitBills = splitBills;
    }

    @NonNull
    @Override
    public SplitBillHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.split_bill_layout, parent, false);
        return new SplitBillHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SplitBillHolder holder, int position) {
        SplitBill splitBill = splitBills.get(position);
        Log.e(TAG, "---------------splitbill: " + splitBill);
        holder.itemSAdapter.setCartItems(splitBill.getCartItemList());
        if (splitBills.size() > 1) {
            holder.itemSAdapter.setDragEnabled(true);
        } else {
            holder.itemSAdapter.setDragEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        if (splitBills == null)
            return 0;
        return splitBills.size();
    }

    private double getTotal(List<CartItem> cartItems) {
        double total = 0;
        for (CartItem c : cartItems) {
            total += c.price * c.count;
            for (Addon a : c.addons)
                total += a.price;
        }
        return total;
    }

    public class SplitBillHolder extends RecyclerView.ViewHolder {
        RecyclerView cartRV;
        TableCheckoutItemSAdapter itemSAdapter;
        Button printBill, checkout, scroller;
        TextView total;

        public SplitBillHolder(View itemView) {
            super(itemView);
            cartRV = itemView.findViewById(R.id.cartRV);
            LinearLayoutManager lm = new LinearLayoutManager(context);
            cartRV.setLayoutManager(lm);
            cartRV.setHasFixedSize(true);
            itemSAdapter = new TableCheckoutItemSAdapter(context, null);
            cartRV.setAdapter(itemSAdapter);
            cartRV.setOnDragListener(itemSAdapter.getDragInstance());
            itemSAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    try {
                        total.setText(String.format("%.2f", getTotal(itemSAdapter.getCartItems())));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

/*//            TableCheckoutItemSAdapter itemSAdapter=(TableCheckoutItemSAdapter)cartRV.getAdapter();
            cartRV.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Log.e(TAG, "---------------------------global layout changed------------------------------------");
                    try {
                        total.setText(getTotal(itemSAdapter.getCartItems()) + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });*/

            printBill = itemView.findViewById(R.id.right_panel_printBill);
            checkout = itemView.findViewById(R.id.right_panel_checkout);
            total = itemView.findViewById(R.id.right_panel_total);
           /* scroller=itemView.findViewById(R.id.scroller);
            scroller.setOnClickListener(v->{
                int lastPos=lm.findLastVisibleItemPosition();
                if(lastPos+1<itemSAdapter.getItemCount())
                    cartRV.scrollToPosition(lastPos);
            });*/

            printBill.setOnClickListener(v -> {
                if (callback == null) {
                    Snackbar snackbar = Snackbar.make(itemView.getRootView(), "No Callback attached", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Dismiss", view -> {
                        snackbar.dismiss();
                    });
                    snackbar.show();
                    return;
                }

                List<CartItem> cartItems = ((TableCheckoutItemSAdapter) cartRV.getAdapter()).getCartItems();
                callback.onPrintBill(cartItems);
            });

            checkout.setOnClickListener(v -> {
                TableCheckoutItemSAdapter adapter = (TableCheckoutItemSAdapter) cartRV.getAdapter();
                List<CartItem> cartItems = adapter.getCartItems();
                callback.onCheckout(cartItems, getAdapterPosition());
                /*adapter.getCartItems().removeAll(cartItems);
                adapter.notifyDataSetChanged();

                int pos=getAdapterPosition();
                splitBills.remove(pos);
                notifyItemRemoved(pos);*/
            });
        }
    }

    public void addCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onPrintBill(List<CartItem> cartItems);

        void onCheckout(List<CartItem> cartItems, int splitBillIndex);
    }
}
