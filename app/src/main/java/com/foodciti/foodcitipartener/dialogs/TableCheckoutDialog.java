package com.foodciti.foodcitipartener.dialogs;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.SplitBillAdapter;
import com.foodciti.foodcitipartener.adapters.TableCheckoutItemSAdapter;
import com.foodciti.foodcitipartener.compound_views.CounterBox;
import com.foodciti.foodcitipartener.models.SplitBill;
import com.foodciti.foodcitipartener.realm_entities.Addon;
import com.foodciti.foodcitipartener.realm_entities.CartItem;
import com.foodciti.foodcitipartener.realm_entities.Table;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.Preferences;
import com.foodciti.foodcitipartener.utils.PrintHelper;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

import static com.foodciti.foodcitipartener.utils.AppConfig.MyPREFERENCES;

public class TableCheckoutDialog extends DialogFragment implements SplitBillAdapter.Callback {
    private static final String TAG = "TableCheckoutDialog";
    private CounterBox splitCounter;
    private LocalBroadcastManager localBroadcastManager;
    private Realm realm;
    private SplitBillAdapter splitBillAdapter;
    private SharedPreferences shared;

    private static final String ARG_TABLE_ID = "table_id";
    private static final String ARG_PAYMENT_TYPE = "payment_type";

    private long tableId=-1;
    private String paymentType;

    private BroadcastReceiver idArrayBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int index = intent.getIntExtra("SPLIT_BILL_INDEX", -1);
            if (index != -1) {
                splitBillAdapter.getSplitBills().remove(index);
                splitBillAdapter.notifyItemRemoved(index);
                splitCounter.setCount(splitCounter.getCount() - 1);
                if (index == 0)
                    dismiss();
            }
        }
    };

    private TableCheckoutDialog(){}

    public static TableCheckoutDialog getInstance(long tableId) {
        TableCheckoutDialog tableCheckoutDialog = new TableCheckoutDialog();
        Bundle bundle = new Bundle();
        bundle.putLong(ARG_TABLE_ID, tableId);
        tableCheckoutDialog.setArguments(bundle);
        tableCheckoutDialog.setCancelable(true);
        return tableCheckoutDialog;
    }

    public static TableCheckoutDialog getInstance(long tableId, String paymentType) {
        TableCheckoutDialog tableCheckoutDialog = new TableCheckoutDialog();
        Bundle bundle = new Bundle();
        bundle.putLong(ARG_TABLE_ID, tableId);
        bundle.putString(ARG_PAYMENT_TYPE, paymentType);
        tableCheckoutDialog.setArguments(bundle);
        tableCheckoutDialog.setCancelable(true);
        return tableCheckoutDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tableId = getArguments().getLong(ARG_TABLE_ID);
            if(getArguments().containsKey(ARG_PAYMENT_TYPE))
                paymentType = getArguments().getString(ARG_PAYMENT_TYPE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        localBroadcastManager.registerReceiver(idArrayBroadcastReceiver, new IntentFilter("splitbill_update"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        localBroadcastManager.unregisterReceiver(idArrayBroadcastReceiver);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tablecheckout_dialog_layout, container, false);
        return view;
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        realm = RealmManager.getLocalInstance();
        shared = getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        View close = view.findViewById(R.id.close);
        close.setOnClickListener(v -> {
            dismiss();
        });


        RecyclerView splitBillRV = view.findViewById(R.id.splitBillRV);
        RecyclerView.LayoutManager lm = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        splitBillRV.setLayoutManager(lm);
        splitBillRV.setHasFixedSize(true);
        splitBillAdapter = new SplitBillAdapter(getActivity(), null);
        splitBillRV.setAdapter(splitBillAdapter);
        splitBillAdapter.addCallback(this);

        Table table = realm.where(Table.class).equalTo("id", tableId).findFirst();
        SplitBill sb1 = new SplitBill();
        sb1.getCartItemList().addAll(table.cartItems);

        /*LinearLayout splitBillLayout=view.findViewById(R.id.splitBillLayout);
        View splitView = LayoutInflater.from(getActivity()).inflate(R.layout.split_bill_layout, splitBillLayout, false);
        splitBillLayout.addView(splitView);
        createSplitView(splitView, new ArrayList<>(table.cartItems));

        Log.e(TAG, "-------------------SPLITLAYOUT CHILD COUNT: "+splitBillLayout.getChildCount());*/

        List<SplitBill> splitBils = new ArrayList<>();
        splitBils.add(sb1);
        splitBillAdapter.setSplitBills(splitBils);
        splitBillAdapter.notifyDataSetChanged();

        splitCounter = view.findViewById(R.id.splitCounter);
        splitCounter.setLowerLimit(1);
        splitCounter.setUpperLimit(table.getTotalPersons());
        splitCounter.setCount(1);
        splitCounter.setOnCounterChangeListener(new CounterBox.CounterListener() {
            @Override
            public void onIncrement(int count) {
                RecyclerView.LayoutManager lm = new GridLayoutManager(getContext(), count) {
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                };
                splitBillRV.setLayoutManager(lm);
                SplitBill sb1 = new SplitBill();
                splitBillAdapter.getSplitBills().add(sb1);
//                splitBillAdapter.notifyItemInserted(splitBillAdapter.getItemCount()-1);
                splitBillAdapter.notifyDataSetChanged();
            }

            @Override
            public void onDecrement(int count) {
                if (count > 0) {
                    RecyclerView.LayoutManager lm = new GridLayoutManager(getContext(), count) {
                        @Override
                        public boolean canScrollVertically() {
                            return false;
                        }
                    };
                    splitBillRV.setLayoutManager(lm);
                }

                SplitBill sb = splitBillAdapter.getSplitBills().get(splitBillAdapter.getItemCount() - 1);
                splitBillAdapter.getSplitBills().get(0).getCartItemList().addAll(sb.getCartItemList());
                splitBillAdapter.getSplitBills().remove(sb);
                splitBillAdapter.notifyItemRemoved(splitBillAdapter.getItemCount());
                splitBillAdapter.notifyItemChanged(0);
                splitBillAdapter.notifyDataSetChanged();
            }
        });

     /*   splitCounter.setOnCounterChangeListener(new CounterBox.CounterListener() {
            @Override
            public void onIncrement(int count) {
                View splitView = LayoutInflater.from(getActivity()).inflate(R.layout.split_bill_layout, splitBillLayout, false);
                splitBillLayout.addView(splitView);
                createSplitView(splitView, new ArrayList<>());
                Log.e(TAG, "-------------------SPLITLAYOUT CHILD COUNT: "+splitBillLayout.getChildCount());
                final float weight=1.0f/(float)count;
                Log.e(TAG, "-----------------COMPUTED WEIGHT: "+weight);
                for(int i=0; i<splitBillLayout.getChildCount(); i++) {
                    LinearLayout.LayoutParams lp=(LinearLayout.LayoutParams)splitBillLayout.getChildAt(i).getLayoutParams();
                    lp.weight=weight;
                    splitBillLayout.getChildAt(i).setLayoutParams(lp);
                }

            }

            @Override
            public void onDecrement(int count) {
                splitBillLayout.removeViewAt(count-1);
            }
        });*/
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

    private void createSplitView(View itemView, List<CartItem> cartItems) {
        RecyclerView cartRV;
        TableCheckoutItemSAdapter itemSAdapter;
        Button printBill, checkout, scroller;
        TextView total;
        cartRV = itemView.findViewById(R.id.cartRV);
        printBill = itemView.findViewById(R.id.right_panel_printBill);
        checkout = itemView.findViewById(R.id.right_panel_checkout);
        total = itemView.findViewById(R.id.right_panel_total);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        cartRV.setLayoutManager(lm);
        cartRV.setHasFixedSize(true);
        itemSAdapter = new TableCheckoutItemSAdapter(getActivity(), cartItems);
        itemSAdapter.setDragEnabled(true);
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


        printBill.setOnClickListener(v -> {
            /*if (callback == null) {
                Snackbar snackbar = Snackbar.make(itemView.getRootView(), "No Callback attached", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Dismiss", view -> {
                    snackbar.dismiss();
                });
                snackbar.show();
                return;
            }
            List<CartItem> cartItems = ((TableCheckoutItemSAdapter) cartRV.getAdapter()).getCartItems();
            callback.onPrintBill(cartItems);*/
        });
        checkout.setOnClickListener(v -> {
            /*TableCheckoutItemSAdapter adapter = (TableCheckoutItemSAdapter) cartRV.getAdapter();
            List<CartItem> cartItems = adapter.getCartItems();
            callback.onCheckout(cartItems, getAdapterPosition());*/
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.9);

            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onPrintBill(List<CartItem> cartItems) {
        double serviceCharges = 0;
        float tax = shared.getFloat(Preferences.SERVICE_CHARGES_TABLE, 0);
        boolean serviceChargesOnCash = shared.getBoolean(Preferences.SERVICE_CHARGES_CASH_APPLICABLE, false);
        boolean serviceChargesOnCard = shared.getBoolean(Preferences.SERVICE_CHARGES_CARD_APPLICABLE, false);

        /*if(serviceChargesOnCard && getArguments().getString("PAYMENT_TYPE").equalsIgnoreCase(Constants.PAYMENT_TYPE_CARD))
            serviceCharges=tax;
        if(serviceChargesOnCash && getArguments().getString("PAYMENT_TYPE").equalsIgnoreCase(Constants.PAYMENT_TYPE_CASH))
            serviceCharges=tax;*/

        double total = computeTotal(cartItems) + tax;
        PrintHelper printHelper = new PrintHelper(getContext());
        printHelper.printForTable(cartItems, 0, total, total, 0, 0, "", Constants.TYPE_TABLE, "", tableId, tax);
    }

    @Override
    public void onCheckout(List<CartItem> cartItems, int splitBillIndex) {
//        double total=computeTotal(cartItems);
        Log.e(TAG, "-----------------cart Size: " + cartItems.size());

        long[] idArray = new long[cartItems.size()];
        int i = 0;
        for (CartItem c : cartItems) {
            idArray[i++] = c.id;
        }

        PoundDialog poundDialog = PoundDialog.getInstance(idArray, splitBillIndex);
        poundDialog.setCancelable(false);
        poundDialog.show(getFragmentManager(), null);
    }

    private double computeTotal(List<CartItem> cartItems) {
        double total = 0;
        for (CartItem c : cartItems) {
            total += (c.price) * c.count;
            for (Addon a : c.addons)
                total += a.price;
        }
        return total;
    }
}
