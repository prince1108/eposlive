package com.foodciti.foodcitipartener.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.OrderhistoryDetailsAdapter;
import com.foodciti.foodcitipartener.db.ExceptionLogger;
import com.foodciti.foodcitipartener.realm_entities.Addon;
import com.foodciti.foodcitipartener.realm_entities.Exlogger;
import com.foodciti.foodcitipartener.realm_entities.OrderAddon;
import com.foodciti.foodcitipartener.realm_entities.OrderCustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.OrderMenuCategory;
import com.foodciti.foodcitipartener.realm_entities.OrderMenuItem;
import com.foodciti.foodcitipartener.realm_entities.OrderTuple;
import com.foodciti.foodcitipartener.realm_entities.Purchase;
import com.foodciti.foodcitipartener.realm_entities.PurchaseEntry;
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

import static android.content.Context.MODE_PRIVATE;
import static com.foodciti.foodcitipartener.utils.AppConfig.MyPREFERENCES;

public class OrderHistoryDetailsDialog extends DialogFragment {
    private static final String TAG = "OrderHistoryDetails";
    private static final String ARG_ORDER_ID = "order_id";
    private Realm realm;
    private RecyclerView orderDetailsRV;
    private OrderhistoryDetailsAdapter orderhistoryDetailsAdapter;
    private ImageView closebtn;

    private TextView dateTime, orderNo, subTotal, adjustment, deliveryCharges, serviceCharges,orderNumberLabel;

    private SimpleDateFormat simpleDateFormat;
    private String orderType;
    ExceptionLogger logger;
    private long purchaseId;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private OrderHistoryDetailsDialog() {
    }

    public static OrderHistoryDetailsDialog getInstance(long orderid) {
        OrderHistoryDetailsDialog historyDetailsDialog = new OrderHistoryDetailsDialog();
        Bundle bundle = new Bundle();
        bundle.putLong(ARG_ORDER_ID, orderid);
        historyDetailsDialog.setArguments(bundle);
        historyDetailsDialog.setCancelable(true);
        return historyDetailsDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            purchaseId = getArguments().getLong(ARG_ORDER_ID);
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.orderhistory_details, container, false);
        sharedPreferences = getActivity().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        logger=new ExceptionLogger(getActivity());
        realm = RealmManager.getLocalInstance();
        simpleDateFormat = new SimpleDateFormat(getString(R.string.current_date_format));
        orderDetailsRV = rootView.findViewById(R.id.orderDetailsRV);
        RecyclerView.LayoutManager linearLM = new LinearLayoutManager(getActivity());
        orderDetailsRV.setLayoutManager(linearLM);
        orderDetailsRV.setHasFixedSize(true);

        Purchase order = realm.where(Purchase.class).equalTo("id", purchaseId).findFirst();
        orderType = order.getOrderType();
        orderhistoryDetailsAdapter = new OrderhistoryDetailsAdapter(getActivity(), order);
        orderDetailsRV.setAdapter(orderhistoryDetailsAdapter);

        dateTime = rootView.findViewById(R.id.date_time);
        orderNo = rootView.findViewById(R.id.order_number);
        orderNumberLabel= rootView.findViewById(R.id.orderNumberLabel);
        dateTime.setText(simpleDateFormat.format(new Date(order.getTimestamp())));
        boolean isChecked = sharedPreferences.getBoolean(Preferences.VENDOR_ORDER_COUNTER, false);
        if (isChecked) {
            orderNumberLabel.setVisibility(View.VISIBLE);
            orderNo.setVisibility(View.VISIBLE);
            orderNo.setText(order.getId() + "");
        } else {
            orderNumberLabel.setVisibility(View.GONE);
            orderNo.setVisibility(View.GONE);
        }

        subTotal = rootView.findViewById(R.id.subtotal);
        adjustment = rootView.findViewById(R.id.adjustment);
        deliveryCharges = rootView.findViewById(R.id.delivery_charges);
        serviceCharges = rootView.findViewById(R.id.service_charges);

        if (order.getDiscount() > 0) {
            String st = String.format("%.2f", order.getSubTotal());
            String dscnt = String.format("%.2f", order.getDiscount());

            subTotal.setVisibility(View.VISIBLE);
            subTotal.setText("Sub Total:\t\t" + st);

            adjustment.setVisibility(View.VISIBLE);
            adjustment.setText("Discount:\t\t" + dscnt);
        }

        if (order.getExtra() > 0) {
            String st = String.format("%.2f", order.getSubTotal());
            String extr = String.format("%.2f", order.getExtra());

            subTotal.setVisibility(View.VISIBLE);
            subTotal.setText("Sub Total:\t\t" + st);

            adjustment.setVisibility(View.VISIBLE);
            adjustment.setText("Extra:\t\t" + extr);
        }

        if (order.getDeliveryCharges() > 0) {
            String st = String.format("%.2f", order.getSubTotal());
            String extr = String.format("%.2f", order.getDeliveryCharges());

            subTotal.setVisibility(View.VISIBLE);
            subTotal.setText("Sub Total:\t\t" + st);

            deliveryCharges.setVisibility(View.VISIBLE);
            deliveryCharges.setText("Delivery Charges:\t\t" + extr);
        }

        if (order.getServiceCharges() > 0) {
            String st = String.format("%.2f", order.getSubTotal());
            String extr = String.format("%.2f", order.getServiceCharges());

            subTotal.setVisibility(View.VISIBLE);
            subTotal.setText("Sub Total:\t\t" + st);

            serviceCharges.setVisibility(View.VISIBLE);
            serviceCharges.setText("Service Charges:\t\t" + extr);
        }


        closebtn = rootView.findViewById(R.id.close);
        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

//        final double total=computeTotal(order.getOrderTuples());
        final double total = order.getTotal();

        TextView totalTV = rootView.findViewById(R.id.right_panel_total);
        totalTV.setText("Total:\t\t" + String.format("%.2f", total));
        rootView.findViewById(R.id.print).setOnClickListener(v -> {
            long customerId = (order.getOrderCustomerInfo() == null) ? -1 : order.getOrderCustomerInfo().getId();
            if (order.getOrderType().equals(Constants.TYPE_TABLE)) {
                printOrders(order.getPurchaseEntries(), order.getTableName(), order.getTotal(), order.getSubTotal(), order.getDiscount(), order.getExtra(), order.getPaymentMode(), orderType, "", customerId, order.getDeliveryCharges(), order.getServiceCharges(), order.isPaid());
            } else {
                printOrders(order.getPurchaseEntries(), null, order.getTotal(), order.getSubTotal(), order.getDiscount(), order.getExtra(), order.getPaymentMode(), orderType, "", customerId, order.getDeliveryCharges(), order.getServiceCharges(), order.isPaid());
            }
        });

        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow()
                    .setLayout((int) (getScreenWidth(getActivity()) * .5), (int) (getScreenWidth(getActivity()) * .4));
        }
    }

    private int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    private double computeTotal(List<OrderTuple> orderTuples) {
        double total = 0;
        for (OrderTuple o : orderTuples) {
            total += (o.getPrice()) * o.getCount();
            for (Addon a : o.getAddons())
                total += a.price;
        }
        return total;
    }

    private boolean printOrders(List<PurchaseEntry> purchaseEntries, String order_id, double total, double sub_total, double discount, double extra, String payment_type,
                                String orderType, String special_note, long userId, double deliveryCharges, double serviceCharges, boolean paid) {

        final Realm realm = Realm.getDefaultInstance();
        Vendor vendorInfo = realm.where(Vendor.class).findFirst();
        if (vendorInfo == null) {
            Toast.makeText(getActivity(), "Vendor Info is NULL", Toast.LENGTH_SHORT).show();
            return false;
        }
        final String provider = vendorInfo.getTitle();
        final String vendor = vendorInfo.getName();
        final String tel_no = vendorInfo.getTel_no();
        final String pin = vendorInfo.getPin();
        final String loc = vendorInfo.getAddress();
        final String vatNo = vendorInfo.getVatNo() == null ? "" : vendorInfo.getVatNo();
        final String companyNo = vendorInfo.getCompanyNo() == null ? "" : vendorInfo.getCompanyNo();

        final byte[] dividerLine = PrintUtils.getBytes("-----------------------");
        byte[] cursorPosition = null;

        final String POUND = "\u00A3";

        boolean success = true;
        try {
            List<Byte> bytes = new LinkedList<>();
            byte[] alignment = PrintUtils.setAlignment('0');
            PrintUtils.copyBytesToList(bytes, alignment);

            alignment = PrintUtils.setAlignment('1');
            PrintUtils.copyBytesToList(bytes, alignment);

            byte[] bold = PrintUtils.setBold(true);
            PrintUtils.copyBytesToList(bytes, bold);

            byte[] size = PrintUtils.setWH('4');
            PrintUtils.copyBytesToList(bytes, size);

            if (order_id != null && !order_id.trim().isEmpty()) {
//                String orderID = Long.toString(order_id);
                PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(order_id), PrintUtils.getLF(1));
            }
            PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(provider), PrintUtils.getLF(1));

            size = PrintUtils.setWH('2');
            bold = PrintUtils.setBold(false);
            PrintUtils.copyBytesToList(bytes, size);
            PrintUtils.copyBytesToList(bytes, bold);
            PrintUtils.copyBytesToList(bytes, dividerLine, PrintUtils.getLF(1));

            bold = PrintUtils.setBold(true);
            PrintUtils.copyBytesToList(bytes, bold);
            PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(vendor.replace(",", "\n")), PrintUtils.getLF(1));

            bold = PrintUtils.setBold(false);
            String location = loc.replace(",", "\n");
            PrintUtils.copyBytesToList(bytes, bold);
            PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(location), PrintUtils.getLF(1));

            PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(pin), PrintUtils.getLF(1));

            PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes("Tel:" + tel_no), PrintUtils.getLF(1));

            if (!vatNo.isEmpty())
                PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes("VAT No:" + vatNo), PrintUtils.getLF(1));
            if (!companyNo.isEmpty())
                PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes("Company No:" + companyNo), PrintUtils.getLF(1));

            PrintUtils.copyBytesToList(bytes, dividerLine, PrintUtils.getLF(1));

            if (special_note != null && !special_note.trim().isEmpty()) {
                PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(special_note), PrintUtils.getLF(1));

                PrintUtils.copyBytesToList(bytes, dividerLine, PrintUtils.getLF(1));
            }

            alignment = PrintUtils.setAlignment('0');
            size = PrintUtils.setWH('0'); // ge default size
            bold = PrintUtils.setBold(false);
            cursorPosition = PrintUtils.setCursorPosition(390);
            PrintUtils.copyBytesToList(bytes, alignment);
            PrintUtils.copyBytesToList(bytes, size);
            PrintUtils.copyBytesToList(bytes, bold);
            PrintUtils.copyBytesToList(bytes, cursorPosition);
            PrintUtils.copyBytesToList(bytes, PrintUtils.getTabs(1));
            PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes("GBP"), PrintUtils.getLF(1));

            cursorPosition = PrintUtils.setCursorPosition(0);
            PrintUtils.copyBytesToList(bytes, cursorPosition);

            final List<PurchaseEntry> cartItemList = new ArrayList<>(purchaseEntries);
            Collections.sort(cartItemList, (o1, o2) -> {
                OrderMenuItem mi1 = o1.getOrderMenuItem();
                OrderMenuItem mi2 = o2.getOrderMenuItem();
                OrderMenuCategory mc1 = null;
                OrderMenuCategory mc2 = null;
                if (mi1 != null) {
                    if (!mi1.orderMenuCategory.isEmpty())
                        mc1 = mi1.orderMenuCategory.first();
                }
                if (mi2 != null) {
                    if (!mi2.orderMenuCategory.isEmpty())
                        mc2 = mi2.orderMenuCategory.first();
                }
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
                PurchaseEntry a = cartItemList.get(i);
                StringBuilder addonStrBuilder = new StringBuilder();
                alignment = PrintUtils.setAlignment('0');
                size = PrintUtils.setWH('0');
                bold = PrintUtils.setBold(true);
                PrintUtils.copyBytesToList(bytes, alignment, bold, size);

                if (purchaseEntries.size() != 0) {
                    List<OrderAddon> addons = a.getOrderAddons();
                    for (OrderAddon addon : addons) {
                        String included = (addon.isNoAddon == false) ? "+" : "-";
                        addonStrBuilder.append("  ").append(included).append(addon.name.trim() + "\n");
                    }
                    if (!a.getAdditionalNote().trim().isEmpty())
                        addonStrBuilder.append("  *").append(a.getAdditionalNote().trim()).append("\n");
                    PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(" " + a.getCount() + " " + a.getOrderMenuItem().name));

                } else {
                    PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(" " + a.getOrderMenuItem().name));
                }

                cursorPosition = PrintUtils.setCursorPosition(390);
                PrintUtils.copyBytesToList(bytes, cursorPosition);


                if (purchaseEntries.size() != 0) {
                    double price = (orderType.equals(Constants.TYPE_COLLECTION)) ? a.getOrderMenuItem().collectionPrice : a.getOrderMenuItem().deliveryPrice;
                    PrintUtils.copyBytesToList(bytes, PrintUtils.getTabs(1));
                    PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(String.format("%.2f", (a.getCount()) * price)), PrintUtils.getLF(1));
                } else {
                    PrintUtils.copyBytesToList(bytes, PrintUtils.getTabs(1), PrintUtils.getLF(1));
                }

                cursorPosition = PrintUtils.setCursorPosition(0);
                PrintUtils.copyBytesToList(bytes, cursorPosition);

                PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(addonStrBuilder.toString()));

                alignment = PrintUtils.setAlignment('1');
                size = PrintUtils.setWH('2');
                PrintUtils.copyBytesToList(bytes, alignment, size, dividerLine, PrintUtils.getLF(1));
            }

            /*size = PrintUtils.setWH('2');
            alignment = PrintUtils.setAlignment('1');
            PrintUtils.copyBytesToList(bytes, PrintUtils.getLF(1), size, alignment, dividerLine, PrintUtils.getLF(1));*/

            alignment = PrintUtils.setAlignment('0');
            bold = PrintUtils.setBold(true);
            size = PrintUtils.setWH('0');
            PrintUtils.copyBytesToList(bytes, alignment);
            PrintUtils.copyBytesToList(bytes, bold);
            PrintUtils.copyBytesToList(bytes, size);
            PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes("Subtotal: "));

            cursorPosition = PrintUtils.setCursorPosition(390);
            PrintUtils.copyBytesToList(bytes, cursorPosition);
            PrintUtils.copyBytesToList(bytes, PrintUtils.getTabs(1));
            PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(String.format(" %.2f", sub_total)), PrintUtils.getLF(1));


            if (discount > 0) {
                cursorPosition = PrintUtils.setCursorPosition(0);
                PrintUtils.copyBytesToList(bytes, cursorPosition);
                PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes("Discount: "));
                cursorPosition = PrintUtils.setCursorPosition(390);
                PrintUtils.copyBytesToList(bytes, cursorPosition);
                PrintUtils.copyBytesToList(bytes, PrintUtils.getTabs(1));
                PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes("-" + String.format("%.2f", discount)));
                PrintUtils.copyBytesToList(bytes, PrintUtils.getLF(1));
            }

            if (extra > 0) {
                cursorPosition = PrintUtils.setCursorPosition(0);
                PrintUtils.copyBytesToList(bytes, cursorPosition);
                PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes("Extra: "));
                cursorPosition = PrintUtils.setCursorPosition(390);
                PrintUtils.copyBytesToList(bytes, cursorPosition);
                PrintUtils.copyBytesToList(bytes, PrintUtils.getTabs(1));
                PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(String.format("%.2f", extra)));
                PrintUtils.copyBytesToList(bytes, PrintUtils.getLF(1));
            }

            if (deliveryCharges > 0) {
                cursorPosition = PrintUtils.setCursorPosition(0);
                PrintUtils.copyBytesToList(bytes, cursorPosition);
                PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes("Delivery Charge: "));
                cursorPosition = PrintUtils.setCursorPosition(390);
                PrintUtils.copyBytesToList(bytes, cursorPosition);
                PrintUtils.copyBytesToList(bytes, PrintUtils.getTabs(1));
                PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(String.format("%.2f", deliveryCharges)));
                PrintUtils.copyBytesToList(bytes, PrintUtils.getLF(1));
            }

            if (serviceCharges > 0) {
                cursorPosition = PrintUtils.setCursorPosition(0);
                PrintUtils.copyBytesToList(bytes, cursorPosition);
                PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes("Service Charge: "));
                cursorPosition = PrintUtils.setCursorPosition(390);
                PrintUtils.copyBytesToList(bytes, cursorPosition);
                PrintUtils.copyBytesToList(bytes, PrintUtils.getTabs(1));
                PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(String.format("%.2f", serviceCharges)));
                PrintUtils.copyBytesToList(bytes, PrintUtils.getLF(1));
            }


            cursorPosition = PrintUtils.setCursorPosition(0);
            size = PrintUtils.setWH('2');
            alignment = PrintUtils.setAlignment('1');
            bold = PrintUtils.setBold(false);
            PrintUtils.copyBytesToList(bytes, cursorPosition, size, alignment, bold, dividerLine, PrintUtils.getLF(1));

            alignment = PrintUtils.setAlignment('0');
            bold = PrintUtils.setBold(true);
            PrintUtils.copyBytesToList(bytes, alignment, bold, PrintUtils.getBytes("Total Price: "));

            cursorPosition = PrintUtils.setCursorPosition(390);
            PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes(String.format("%.2f", total)), cursorPosition, PrintUtils.getLF(1));

            cursorPosition = PrintUtils.setCursorPosition(0);
            alignment = PrintUtils.setAlignment('1');
            bold = PrintUtils.setBold(false);
            PrintUtils.copyBytesToList(bytes, cursorPosition, alignment, bold, dividerLine, PrintUtils.getLF(1));

            String paidStatus = (paid == true) ? "PAID" : "NOT PAID";
            size = PrintUtils.setWH('4');
            bold = PrintUtils.setBold(true);
            PrintUtils.copyBytesToList(bytes, size, bold, PrintUtils.getBytes(payment_type + " " + paidStatus), PrintUtils.getLF(1));

            bold = PrintUtils.setBold(true);
            PrintUtils.copyBytesToList(bytes, size, bold, PrintUtils.getBytes(orderType), PrintUtils.getLF(1));

            size = PrintUtils.setWH('1');
            bold = PrintUtils.setBold(false);
            String time = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.UK).format(new Date());
            PrintUtils.copyBytesToList(bytes, size, bold, PrintUtils.getBytes(time));

            OrderCustomerInfo customerInfo = realm.where(OrderCustomerInfo.class).equalTo("id", userId).findFirst();
            String customerPhone = "";
            String customerHouseNo = "";
            String CustomerAddress = "";
            String CustomerPostalCode = "";
            String customerName = "";
            StringBuilder addressString = new StringBuilder();
            if (customerInfo != null && !customerInfo.getPhone().trim().isEmpty()) {
                customerName = (customerInfo.getName() == null || customerInfo.getName().isEmpty()) ? "" : customerInfo.getName().trim();
                if (!customerName.isEmpty())
                    addressString.append(customerName);

                if (orderType.equals(Constants.TYPE_DELIVERY)) {
                    customerHouseNo = (customerInfo.getHouse_no() == null) ? "" : customerInfo.getHouse_no().trim();
                    if (!customerHouseNo.isEmpty()) {
                        if (addressString.length() != 0)
                            addressString.append(",").append(customerHouseNo);
                        else
                            addressString.append(customerHouseNo);
                    }

                    if (customerInfo.getOrderPostalInfo() != null) {
                        CustomerAddress = (customerInfo.getOrderPostalInfo().getAddress() == null) ? "" : customerInfo.getOrderPostalInfo().getAddress().trim();
                        if (!CustomerAddress.isEmpty()) {
                            if (addressString.length() != 0) {
                                addressString.append(" ").append(CustomerAddress);
                            } else
                                addressString.append(CustomerAddress);
                        }
                        CustomerPostalCode = (customerInfo.getOrderPostalInfo().getA_PostCode() == null) ? "" : customerInfo.getOrderPostalInfo().getA_PostCode().trim();
                        if (!CustomerPostalCode.isEmpty()) {
                            if (addressString.length() != 0)
                                addressString.append(",").append(CustomerPostalCode);
                            else
                                addressString.append(CustomerAddress);
                        }
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
                PrintUtils.copyBytesToList(bytes, PrintUtils.getLF(1), alignment, bold, dividerLine, PrintUtils.getLF(1));

                // added later
                alignment = PrintUtils.setAlignment('0');
                size = PrintUtils.setWH('4');
                PrintUtils.copyBytesToList(bytes, alignment, size);

                StringBuilder stringBuilder = new StringBuilder();
                String details = StringHelper.capitalizeEachWordAfterComma(addressString.toString());
                String str = details.replace(",", "\n");
                /*String[] tokens = StringHelper.capitalizeEachWordAfterComma(addressString.toString()).split(",");
                List<String> stringList = Arrays.asList(tokens);
                Iterator<String> stringIterator = stringList.iterator();
                while (stringIterator.hasNext()) {
                    final String str = stringIterator.next();
                    if (stringIterator.hasNext())
                        stringBuilder.append(str.trim() + "\n");
                    else
                        stringBuilder.append(str.trim());
                }*/

                PrintUtils.copyBytesToList(bytes, PrintUtils.getBytes("Customer Details:\n"), PrintUtils.getBytes(str));
            }

            alignment = PrintUtils.setAlignment('1');
            bold = PrintUtils.setBold(false);
            size = PrintUtils.setWH('2');
            PrintUtils.copyBytesToList(bytes, PrintUtils.getLF(1), alignment, bold, size, dividerLine, PrintUtils.getLF(1));

            bold = PrintUtils.setBold(true);
            size = PrintUtils.setWH('4');
            PrintUtils.copyBytesToList(bytes, bold, size, PrintUtils.getBytes("www.foodciti.co.uk"), PrintUtils.getLF(2), PrintUtils.cutPaper());

            byte[] arr = PrintUtils.toPrimitiveBytes(bytes);


            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
            Intent intent = new Intent(PrintUtils.ACTION_PRINT_REQUEST);
            intent.putExtra(PrintUtils.PRINT_DATA, arr);
            localBroadcastManager.sendBroadcast(intent);


        } catch (Exception e) {
            Exlogger exlogger = new Exlogger();
            exlogger.setErrorType("Print Error");
            exlogger.setErrorMessage(e.getMessage());
            exlogger.setScreenName("OrderHistoryDetailsDialoge->>printOrders() function");
            logger.addException(exlogger);
            e.printStackTrace();
        } finally {
            realm.close();
        }
        return success;
    }
}
