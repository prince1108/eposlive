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
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.TableCheckoutItemSAdapter;
import com.foodciti.foodcitipartener.compound_views.CounterBox;
import com.foodciti.foodcitipartener.db.ExceptionLogger;
import com.foodciti.foodcitipartener.realm_entities.Addon;
import com.foodciti.foodcitipartener.realm_entities.CartItem;
import com.foodciti.foodcitipartener.realm_entities.CustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.Exlogger;
import com.foodciti.foodcitipartener.realm_entities.MenuCategory;
import com.foodciti.foodcitipartener.realm_entities.MenuItem;
import com.foodciti.foodcitipartener.realm_entities.Purchase;
import com.foodciti.foodcitipartener.realm_entities.Table;
import com.foodciti.foodcitipartener.realm_entities.Vendor;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.Preferences;
import com.foodciti.foodcitipartener.utils.PrintUtils;
import com.foodciti.foodcitipartener.utils.RealmManager;
import com.foodciti.foodcitipartener.utils.StringHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.Sort;

import static com.foodciti.foodcitipartener.utils.AppConfig.MyPREFERENCES;

public class TableCheckoutDialog2 extends DialogFragment {
    private static final String TAG = "TableCheckoutDialog";
    private static final String ARG_TABLE_ID = "table_id";
    private static final String ARG_PAYMENT_TYPE = "payment_type";
    private CounterBox splitCounter;
    private LocalBroadcastManager localBroadcastManager;
    private Realm realm;
    private TableCheckoutItemSAdapter splitBillAdapter, masterListAdapter;
    private SharedPreferences shared;
    private long tableId = -1;
    private String paymentType;
    ExceptionLogger logger;
    private BroadcastReceiver idArrayBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int index = intent.getIntExtra("SPLIT_BILL_INDEX", -1);
            if (index != -1) {
                /*splitBillAdapter.getSplitBills().remove(index);
                splitBillAdapter.notifyItemRemoved(index);*/
//                splitCounter.setCount(splitCounter.getCount() - 1);
                if (index == 0)
                    dismiss();
            }
        }
    };

    private TableCheckoutDialog2() {
    }

    public static TableCheckoutDialog2 getInstance(long tableId) {
        TableCheckoutDialog2 tableCheckoutDialog = new TableCheckoutDialog2();
        Bundle bundle = new Bundle();
        bundle.putLong(ARG_TABLE_ID, tableId);
        tableCheckoutDialog.setArguments(bundle);
        tableCheckoutDialog.setCancelable(true);
        return tableCheckoutDialog;
    }

    public static TableCheckoutDialog2 getInstance(long tableId, String paymentType) {
        TableCheckoutDialog2 tableCheckoutDialog = new TableCheckoutDialog2();
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
            if (getArguments().containsKey(ARG_PAYMENT_TYPE))
                paymentType = getArguments().getString(ARG_PAYMENT_TYPE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        logger=new ExceptionLogger(getActivity());
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
        View view = inflater.inflate(R.layout.tablecheckout2_dialog_layout, container, false);
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
        splitBillRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        splitBillRV.setHasFixedSize(true);
        splitBillAdapter = new TableCheckoutItemSAdapter(getActivity(), null);
        splitBillRV.setAdapter(splitBillAdapter);
        splitBillRV.setOnDragListener(splitBillAdapter.getDragInstance());
//        splitBillAdapter.addCallback(this);

        RecyclerView masterListRV = view.findViewById(R.id.masterListRV);
        masterListRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        masterListRV.setHasFixedSize(true);
        masterListAdapter = new TableCheckoutItemSAdapter(getActivity(), null);
        masterListRV.setAdapter(masterListAdapter);
        masterListRV.setOnDragListener(masterListAdapter.getDragInstance());

        Table table = realm.where(Table.class).equalTo("id", tableId).findFirst();
        masterListAdapter.setCartItems(new ArrayList<>(table.cartItems));

        /*LinearLayout splitBillLayout=view.findViewById(R.id.splitBillLayout);
        View splitView = LayoutInflater.from(getActivity()).inflate(R.layout.split_bill_layout, splitBillLayout, false);
        splitBillLayout.addView(splitView);
        createSplitView(splitView, new ArrayList<>(table.cartItems));

        Log.e(TAG, "-------------------SPLITLAYOUT CHILD COUNT: "+splitBillLayout.getChildCount());*/

        /*List<SplitBill> splitBils = new ArrayList<>();
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
*/
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

        View rightPanel = view.findViewById(R.id.right_panel);
        Button lPrintBill = view.findViewById(R.id.left_panel_printBill);
        Button lCheckout = view.findViewById(R.id.left_panel_checkout);
        Button rPrintBill = view.findViewById(R.id.right_panel_printBill);
        Button rCheckout = view.findViewById(R.id.right_panel_checkout);
        ToggleButton split = view.findViewById(R.id.split);

        lPrintBill.setOnClickListener(v -> {
            onPrintBill(masterListAdapter.getCartItems());
        });

        lCheckout.setOnClickListener(v -> {
            onCheckout(masterListAdapter.getCartItems(), 0);
        });

        rPrintBill.setOnClickListener(v -> {
            onPrintBill(splitBillAdapter.getCartItems());
        });

        rCheckout.setOnClickListener(v -> {
            onCheckout(splitBillAdapter.getCartItems(), 1);
            splitBillAdapter.getCartItems().clear();
            splitBillAdapter.notifyDataSetChanged();
            split.setChecked(false);
        });


        split.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                rightPanel.setVisibility(View.VISIBLE);
                masterListAdapter.setDragEnabled(true);
                splitBillAdapter.setDragEnabled(true);
            } else {
                rightPanel.setVisibility(View.GONE);
                masterListAdapter.setDragEnabled(true);
                splitBillAdapter.setDragEnabled(true);
            }
        });
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
        printForTable(cartItems, 0, total, total, 0, 0, "", Constants.TYPE_TABLE, "", tableId, tax, false);
    }


    public void onCheckout(List<CartItem> cartItems, int splitBillIndex) {
//        double total=computeTotal(cartItems);
        Log.e(TAG, "-----------------cart Size: " + cartItems.size());

        long[] idArray = new long[cartItems.size()];
        int i = 0;
        for (CartItem c : cartItems) {
            idArray[i++] = c.id;
        }

        PoundDialog poundDialog = PoundDialog.getInstance(idArray, splitBillIndex);
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

    public boolean printForTable(List<CartItem> cartItems, long order_id, double total, double sub_total, double discount, double extra, String payment_type,
                                 String orderType, String special_note, long tableId, double serviceCharges, boolean paid) {

        final Realm realm = Realm.getDefaultInstance();
        Vendor vendorInfo = realm.where(Vendor.class).findFirst();
        if(vendorInfo==null) {
            Toast.makeText(getActivity(), "Vendor Info is NULL", Toast.LENGTH_SHORT).show();
            return false;
        }
        final String provider = vendorInfo.getTitle();
        final String vendor = vendorInfo.getName();
        final String tel_no = vendorInfo.getTel_no();
        final String pin = vendorInfo.getPin();
        final String loc = vendorInfo.getAddress();
        final String vatNo = vendorInfo.getVatNo() == null ? "" : vendorInfo.getVatNo();
        final String companyNo = vendorInfo.getCompanyNo()==null? "" : vendorInfo.getCompanyNo();

        final String POUND = "\u00A3";
        boolean returnValue = true;

        final Table table = realm.where(Table.class).equalTo("id", tableId).findFirst();
        final CustomerInfo customerInfo = table.getCustomerInfo();

        final byte[] dividerLine = PrintUtils.getBytes("\n-----------------------\n");
        final byte[] dividerLine2 = PrintUtils.getBytes("-----------------------");
        byte[] cursorPosition = null;
        byte[] alignment = null;
        byte[] size = null;
        byte[] bold = null;
        byte[] text = null;
        byte[] tabs = null;
        try {

            final List<Byte> bytes = new LinkedList<>();
            alignment = PrintUtils.setAlignment('0');
            PrintUtils.copyBytesToList(bytes, alignment);

            byte[] internationalCharcters = PrintUtils.setInternationalCharcters('3');
            PrintUtils.copyBytesToList(bytes, internationalCharcters);

            alignment = PrintUtils.setAlignment('1');
            bold = PrintUtils.setBold(true);
            size = PrintUtils.setWH('4');
            text = PrintUtils.getBytes(table.getName() + "\n");
            PrintUtils.copyBytesToList(bytes, alignment, bold, size, text);

            text = PrintUtils.getBytes(provider);
            PrintUtils.copyBytesToList(bytes, text);

            size = PrintUtils.setWH('2');
            bold = PrintUtils.setBold(false);
            text = dividerLine;
            PrintUtils.copyBytesToList(bytes, size, bold, text);

            /*bold = PrintUtils.setBold(true);
            text = PrintUtils.getBytes(vendor.replace(",","\n") + "\n");
            PrintUtils.copyBytesToList(bytes, bold, text);*/

            bold = PrintUtils.setBold(false);
            String location = loc.replace(",", "\n");
            text = PrintUtils.getBytes(location);
            PrintUtils.copyBytesToList(bytes, bold, text);

            text = PrintUtils.getBytes(pin);
            PrintUtils.copyBytesToList(bytes, text, PrintUtils.getLF(1));

            text = PrintUtils.getBytes("Tel:" + tel_no);
            PrintUtils.copyBytesToList(bytes, text, PrintUtils.getLF(1));

            if(!vatNo.isEmpty()) {
                text = PrintUtils.getBytes("VAT No:" + vatNo);
                PrintUtils.copyBytesToList(bytes, text, PrintUtils.getLF(1));
            }
            if(!companyNo.isEmpty()) {
                text = PrintUtils.getBytes("Company No:" + companyNo);
                PrintUtils.copyBytesToList(bytes, text, PrintUtils.getLF(1));
            }

            text = dividerLine;
            PrintUtils.copyBytesToList(bytes, text);


            if (special_note != null && !special_note.trim().isEmpty()) {
                text = PrintUtils.getBytes(special_note);
                PrintUtils.copyBytesToList(bytes, text);

                text = dividerLine;
                PrintUtils.copyBytesToList(bytes, text);

            }

            alignment = PrintUtils.setAlignment('0');
            size = PrintUtils.setWH('5');
            bold = PrintUtils.setBold(false);
            cursorPosition = PrintUtils.setCursorPosition(390);
            tabs = PrintUtils.getTabs(1);
            text = PrintUtils.getBytes("GBP\n");
            PrintUtils.copyBytesToList(bytes, alignment, size, bold, cursorPosition, tabs, text);

            cursorPosition = PrintUtils.setCursorPosition(0);
            PrintUtils.copyBytesToList(bytes, cursorPosition);

            //    ---------------------------------------- From realm ------------------------------------------

            final List<CartItem> cartItemList = new ArrayList<>(cartItems);
            Collections.sort(cartItemList, (o1, o2) -> {
                MenuItem mi1 = o1.menuItem;
                MenuItem mi2 = o2.menuItem;
                MenuCategory mc1 = null;
                MenuCategory mc2 = null;
                if (mi1 != null)
                    mc1 = mi1.menuCategory;
                if (mi2 != null)
                    mc2 = mi2.menuCategory;
                int printOrder1 = Integer.MAX_VALUE;
                int printOrder2 = Integer.MAX_VALUE;
                if (mc1 != null)
                    printOrder1 = mc1.getPrintOrder();
                if (mc2 != null)
                    printOrder2 = mc2.getPrintOrder();

                if (printOrder1 < printOrder2)
                    return -1;
                else if (printOrder1 > printOrder2)
                    return 1;
                else
                    return 0;

            });

            for (int i = 0; i < cartItemList.size(); i++) {
                CartItem a = cartItemList.get(i);
                StringBuilder addonStrBuilder = new StringBuilder();
                alignment = PrintUtils.setAlignment('0');
                bold = PrintUtils.setBold(true);
                size = PrintUtils.setWH('0');
                PrintUtils.copyBytesToList(bytes, alignment, bold, size);

                if (cartItems.size() != 0) {
                    List<Addon> addons = a.addons;
                    for (Addon addon : addons) {
                        String included = (addon.isNoAddon == false) ? "+" : "-";
                        addonStrBuilder.append("  ").append(included).append(addon.name.trim() + "\n");
                    }
                    if (!a.comment.trim().isEmpty())
                        addonStrBuilder.append("  *").append(a.comment.trim()).append("\n");
                    text = PrintUtils.getBytes(" " + a.count + " " + a.menuItem.name);
                    PrintUtils.copyBytesToList(bytes, text);
                } else {
                    text = PrintUtils.getBytes(" " + a.menuItem.name);
                    PrintUtils.copyBytesToList(bytes, text);
                }

                cursorPosition = PrintUtils.setCursorPosition(390);
                PrintUtils.copyBytesToList(bytes, cursorPosition);


                if (cartItems.size() != 0) {
                    tabs = PrintUtils.getTabs(1);
                    double price = a.menuItem.collectionPrice;
                    text = PrintUtils.getBytes(String.format("%.2f", (a.count) * price) + "\n");
                    PrintUtils.copyBytesToList(bytes, tabs, text);
                } else {
                    tabs = PrintUtils.getTabs(1);
                    text = PrintUtils.getBytes("\n");
                    PrintUtils.copyBytesToList(bytes, tabs, text);
                }
                size = PrintUtils.setWH('0');
                cursorPosition = PrintUtils.setCursorPosition(0);
                text = PrintUtils.getBytes(addonStrBuilder.toString());
                PrintUtils.copyBytesToList(bytes, size, cursorPosition, text);

                alignment = PrintUtils.setAlignment('1');
                size = PrintUtils.setWH('2');
                text = dividerLine2;
                PrintUtils.copyBytesToList(bytes, alignment, size, text, PrintUtils.getLF(1));
            }

            alignment = PrintUtils.setAlignment('0');
            bold = PrintUtils.setBold(true);
            size = PrintUtils.setWH('5');
            text = PrintUtils.getBytes("Subtotal: ");
            PrintUtils.copyBytesToList(bytes, alignment, bold, size, text);

            cursorPosition = PrintUtils.setCursorPosition(390);
            tabs = PrintUtils.getTabs(1);
            text = PrintUtils.getBytes(String.format(" %.2f", sub_total));
            PrintUtils.copyBytesToList(bytes, cursorPosition, tabs, text, PrintUtils.getLF(1));


            if (discount > 0) {
                cursorPosition = PrintUtils.setCursorPosition(0);
                text = PrintUtils.getBytes("Discount: ");
                PrintUtils.copyBytesToList(bytes, cursorPosition, text);

                cursorPosition = PrintUtils.setCursorPosition(390);
                tabs = PrintUtils.getTabs(1);
                text = PrintUtils.getBytes("-" + String.format("%.2f", discount));
                PrintUtils.copyBytesToList(bytes, cursorPosition, tabs, text);
                PrintUtils.copyBytesToList(bytes, PrintUtils.getLF(1));
            }

            if (extra > 0) {
                cursorPosition = PrintUtils.setCursorPosition(0);
                text = PrintUtils.getBytes("Extra: ");
                PrintUtils.copyBytesToList(bytes, cursorPosition, text);

                cursorPosition = PrintUtils.setCursorPosition(390);
                tabs = PrintUtils.getTabs(1);
                text = PrintUtils.getBytes(String.format("%.2f", extra));
                PrintUtils.copyBytesToList(bytes, cursorPosition, tabs, text);
                PrintUtils.copyBytesToList(bytes, PrintUtils.getLF(1));
            }

            if (serviceCharges > 0) {
                cursorPosition = PrintUtils.setCursorPosition(0);
                text = PrintUtils.getBytes("Service Charge: ");
                PrintUtils.copyBytesToList(bytes, cursorPosition, text);

                cursorPosition = PrintUtils.setCursorPosition(390);
                tabs = PrintUtils.getTabs(1);
                text = PrintUtils.getBytes(String.format("%.2f", serviceCharges));
                PrintUtils.copyBytesToList(bytes, cursorPosition, tabs, text);
                PrintUtils.copyBytesToList(bytes, PrintUtils.getLF(1));
            }

            cursorPosition = PrintUtils.setCursorPosition(0);
            alignment = PrintUtils.setAlignment('1');
            size = PrintUtils.setWH('2');
            bold = PrintUtils.setBold(false);
            text = dividerLine2;
            PrintUtils.copyBytesToList(bytes, cursorPosition, alignment, bold, size, text, PrintUtils.getLF(1));

            alignment = PrintUtils.setAlignment('0');
            bold = PrintUtils.setBold(true);
            text = PrintUtils.getBytes("Total Price:  ");
            PrintUtils.copyBytesToList(bytes, alignment, bold, text);

            cursorPosition = PrintUtils.setCursorPosition(390);
            text = PrintUtils.getBytes(String.format("%.2f", total));
            PrintUtils.copyBytesToList(bytes, cursorPosition, text, PrintUtils.getLF(1));

            cursorPosition = PrintUtils.setCursorPosition(0);
            alignment = PrintUtils.setAlignment('1');
            bold = PrintUtils.setBold(false);
            text = dividerLine2;
            PrintUtils.copyBytesToList(bytes, cursorPosition, alignment, bold, text, PrintUtils.getLF(1));


            String paidStatus = (paid == true) ? "PAID" : "NOT PAID";
            size = PrintUtils.setWH('4');
            bold = PrintUtils.setBold(true);
            text = PrintUtils.getBytes(payment_type + " " + paidStatus + "\n");
            PrintUtils.copyBytesToList(bytes, size, bold, text);

            size = PrintUtils.setWH('1');
            bold = PrintUtils.setBold(false);
            String time = new SimpleDateFormat("dd-MM-yyyy", Locale.UK).format(new Date());
//            String time = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.UK).format(new Date());
            text = PrintUtils.getBytes(time);
            PrintUtils.copyBytesToList(bytes, size, bold, text);

            String customerPhone = "";
            String customerHouseNo = "";
            String CustomerAddress = "";
            String CustomerPostalCode = "";
            String customerName = "";
            StringBuilder addressString = new StringBuilder();

            if (customerInfo != null) {
                customerName = (customerInfo.getName() == null || customerInfo.getName().isEmpty()) ? "" : customerInfo.getName().trim();
                if (!customerName.isEmpty())
                    addressString.append(customerName);

                customerHouseNo = (customerInfo.getHouse_no() == null) ? "" : customerInfo.getHouse_no().trim();
                if (!customerHouseNo.isEmpty()) {
                    if (addressString.length() != 0)
                        addressString.append(",").append(customerHouseNo);
                    else
                        addressString.append(customerHouseNo);
                }
                if (customerInfo.getPostalInfo() != null) {
                    CustomerAddress = (customerInfo.getPostalInfo().getAddress() == null) ? "" : customerInfo.getPostalInfo().getAddress().trim();
                    if (!CustomerAddress.isEmpty()) {
                        if (addressString.length() != 0) {
                            addressString.append(" ").append(CustomerAddress);
                        } else
                            addressString.append(CustomerAddress);
                    }
                    CustomerPostalCode = (customerInfo.getPostalInfo().getA_PostCode() == null) ? "" : customerInfo.getPostalInfo().getA_PostCode().trim();
                    if (!CustomerPostalCode.isEmpty()) {
                        if (addressString.length() != 0)
                            addressString.append(",").append(CustomerPostalCode);
                        else
                            addressString.append(CustomerAddress);
                    }
                }

                customerPhone = (customerInfo.getPhone() == null) ? "" : customerInfo.getPhone();
                if (!customerPhone.isEmpty()) {
                    if (addressString.length() != 0)
                        addressString.append(",").append(customerPhone);
                    else
                        addressString.append(customerPhone);
                }

                Number totalOrders = realm.where(Purchase.class).equalTo("orderCustomerInfo.phone", customerInfo.getPhone()).count();
                int totalOrderCount = (totalOrders == null) ? 0 : totalOrders.intValue();
                if (totalOrderCount == 1) {
                    addressString.append(",,").append("New Customer");
                } else {
                    addressString.append(",,").append("Total Orders: ").append(totalOrderCount);
                }
            }

            if (!addressString.toString().trim().isEmpty()) {
                size = PrintUtils.setWH('2');
                alignment = PrintUtils.setAlignment('1');
                bold = PrintUtils.setBold(false);
                text = dividerLine;
                PrintUtils.copyBytesToList(bytes, size, alignment, bold, text);


                // added later

                StringBuilder stringBuilder = new StringBuilder();
                String details = StringHelper.capitalizeEachWordAfterComma(addressString.toString());
                String str = details.replace(",", "\n");
              /*  String[] tokens = StringHelper.capitalizeEachWordAfterComma(addressString.toString()).split(",");
                List<String> stringList = Arrays.asList(tokens);
                Iterator<String> stringIterator = stringList.iterator();
                while (stringIterator.hasNext()) {
                    final String str = stringIterator.next();
                    if (stringIterator.hasNext())
                        stringBuilder.append(str.trim() + "\n");
                    else
                        stringBuilder.append(str.trim());
                }*/

                alignment = PrintUtils.setAlignment('0');
                size = PrintUtils.setWH('4');
                text = PrintUtils.getBytes("Customer Details:\n");
                PrintUtils.copyBytesToList(bytes, alignment, size, text, PrintUtils.getBytes(str));
            }

            alignment = PrintUtils.setAlignment('1');
            size = PrintUtils.setWH('2');
            text = dividerLine;
            PrintUtils.copyBytesToList(bytes, alignment, size, text);

            size = PrintUtils.setWH('4');
            bold = PrintUtils.setBold(true);
            text = PrintUtils.getBytes("www.foodciti.co.uk");
            PrintUtils.copyBytesToList(bytes, size, bold, text, PrintUtils.getLF(2));

            byte[] paperCut = PrintUtils.cutPaper();
            PrintUtils.copyBytesToList(bytes, paperCut);

            byte[] data = PrintUtils.toPrimitiveBytes(bytes);

            Intent intent = new Intent(PrintUtils.ACTION_PRINT_REQUEST);
            intent.putExtra(PrintUtils.PRINT_DATA, data);
            localBroadcastManager.sendBroadcast(intent);

        } catch (Exception ex) {
            returnValue = false;
            Exlogger exlogger = new Exlogger();
            exlogger.setErrorType("Print Error");
            exlogger.setErrorMessage(ex.getMessage());
            exlogger.setScreenName("TableCheckoutDialog2->>printForTable() function");
            logger.addException(exlogger);
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            realm.close();
        }
        return returnValue;
    }
}

