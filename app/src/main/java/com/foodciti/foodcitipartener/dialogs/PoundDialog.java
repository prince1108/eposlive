package com.foodciti.foodcitipartener.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.realm_entities.Addon;
import com.foodciti.foodcitipartener.realm_entities.CartItem;
import com.foodciti.foodcitipartener.realm_entities.Vendor;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.Preferences;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.util.List;

import io.realm.Realm;

import static com.foodciti.foodcitipartener.utils.AppConfig.MyPREFERENCES;


public class PoundDialog extends DialogFragment {
    private static final String TAG = "PoundDialog";
    private View closebtn, cross;
    private RelativeLayout fivepound, tenpound, fifteenpound, twentypound, twentyfivepound, thirtypound, thirtyfivepound, fourtypound, fiftypound;
    private double totalptice = 0.0;
    private double returnpound = 0.0;
    private PounddialogListener mListener;

    TextView no_1, no_2, no_3, no_4, no_5, no_6, no_7, no_8, no_9, no_0, go, point, cash;
    View tenPound, twentyPound, thirtyPound, fortyPound, fiftyPound, sixtyPound;
    View cashNotPaid, cardNotPaid, cashPaid, cardPaid, sendPaymentByPhone;
    EditText noteTotal;
    String Type = "";
    String isPoint = "no";
    private CheckBox checkboxSendSMS;
    private SharedPreferences sharedPreferences;
    private List<CartItem> cartItems;
    private Realm realm;
    private Vendor vendor;
    private int splitBillIndex = -1;

    private static final String ARG_TOTAL_PRICE = "total_price";
    private static final String ARG_CART_ITEMS = "cart_items";
    private static final String ARG_SPLITBILL_INDEX = "splitbill_index";
    private static final String ARG_CLOSE_ON_CHECKOUT = "close_on_checkout";

    private double totalPrice = 0;
    private long[] cartItemPKs;
    private boolean closeOnCheckout;

    private PoundDialog() {
    }

    public static PoundDialog getInstance(double totalprice) {
        PoundDialog poundDialog = new PoundDialog();
        Bundle bundle = new Bundle();
        bundle.putDouble(ARG_TOTAL_PRICE, totalprice);
        poundDialog.setArguments(bundle);
        poundDialog.setCancelable(true);
        return poundDialog;
    }

    public static PoundDialog getInstance(long[] cartItems, int splitBillIndex) {
        PoundDialog poundDialog = new PoundDialog();
        Bundle bundle = new Bundle();
        bundle.putLongArray(ARG_CART_ITEMS, cartItems);
        bundle.putInt(ARG_SPLITBILL_INDEX, splitBillIndex);
        poundDialog.setArguments(bundle);
        poundDialog.setCancelable(true);
        return poundDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            if (arguments.containsKey(ARG_TOTAL_PRICE))
                totalPrice = arguments.getDouble(ARG_TOTAL_PRICE);
            if (arguments.containsKey(ARG_CART_ITEMS))
                cartItemPKs = arguments.getLongArray(ARG_CART_ITEMS);
            if (arguments.containsKey(ARG_SPLITBILL_INDEX))
                splitBillIndex = arguments.getInt(ARG_SPLITBILL_INDEX);
            if (arguments.containsKey(ARG_CLOSE_ON_CHECKOUT))
                closeOnCheckout = arguments.getBoolean(ARG_CLOSE_ON_CHECKOUT);
        }
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

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        Log.e(TAG, "---------------dialog onDismiss called------------------");
        super.onDismiss(dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_payment_layout, container,
                false);

        realm = RealmManager.getLocalInstance();
        Bundle bundle = getArguments();
        if (bundle.containsKey(ARG_SPLITBILL_INDEX) && bundle.containsKey(ARG_CART_ITEMS)) {
            splitBillIndex = getArguments().getInt(ARG_SPLITBILL_INDEX);
            Log.e(TAG, "-------------Psplit bill index: " + cartItemPKs);
            long[] array = getArguments().getLongArray(ARG_CART_ITEMS);
            Long[] ids = new Long[array.length];
            for (int i = 0; i < array.length; i++)
                ids[i] = array[i];
            cartItems = realm.where(CartItem.class).in("id", ids).findAll();
        }
        sharedPreferences = rootView.getContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        closebtn = rootView.findViewById(R.id.close);
        realm = RealmManager.getLocalInstance();
        vendor = realm.where(Vendor.class).findFirst();

        closebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        if (getArguments() != null) {
            if (cartItems != null)
                totalptice = computeTotal(cartItems);
            else
                totalptice = getArguments().getDouble(ARG_TOTAL_PRICE);
        }

        noteTotal = rootView.findViewById(R.id.noteTotal);
        noteTotal.setShowSoftInputOnFocus(false);
        no_1 = rootView.findViewById(R.id.no_1);
        no_2 = rootView.findViewById(R.id.no_2);
        no_3 = rootView.findViewById(R.id.no_3);
        no_4 = rootView.findViewById(R.id.no_4);
        no_5 = rootView.findViewById(R.id.no_5);
        no_6 = rootView.findViewById(R.id.no_6);
        no_7 = rootView.findViewById(R.id.no_7);
        no_8 = rootView.findViewById(R.id.no_8);
        no_9 = rootView.findViewById(R.id.no_9);
        no_9 = rootView.findViewById(R.id.no_9);
        no_0 = rootView.findViewById(R.id.no_0);
        point = rootView.findViewById(R.id.point);
        cross = rootView.findViewById(R.id.cross);
        tenPound = rootView.findViewById(R.id.ten_pounds);
        twentyPound = rootView.findViewById(R.id.twenty_pounds);
        thirtyPound = rootView.findViewById(R.id.thirty_pounds);
        fortyPound = rootView.findViewById(R.id.forty_pounds);
        fiftyPound = rootView.findViewById(R.id.fifty_pounds);
        sixtyPound = rootView.findViewById(R.id.sixty_pounds);
        go = rootView.findViewById(R.id.ok);
//        cash = rootView.findViewById(R.id.cash);
        sendPaymentByPhone = rootView.findViewById(R.id.sendPaymentByPhone);
        if (vendor != null && vendor.getRestroID() != null) {
            String restroID = vendor.getRestroID();
            if (restroID.equalsIgnoreCase("") || restroID == null) {
                sendPaymentByPhone.setVisibility(View.GONE);
            } else {
                sendPaymentByPhone.setVisibility(View.VISIBLE);
            }
        }else{
            sendPaymentByPhone.setVisibility(View.GONE);
        }
        cashNotPaid = rootView.findViewById(R.id.cash_not_paid);
        cashPaid = rootView.findViewById(R.id.cash_paid);
        cardNotPaid = rootView.findViewById(R.id.card_not_paid);
        cardPaid = rootView.findViewById(R.id.card_paid);

        no_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteTotalValue = noteTotal.getText().toString();
                noteTotal.setText(noteTotalValue + "0");
                noteTotal.setSelection(noteTotal.getText().toString().length());


            }
        });

        no_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteTotalValue = noteTotal.getText().toString();
                noteTotal.setText(noteTotalValue + "1");
                noteTotal.setSelection(noteTotal.getText().toString().length());

            }
        });

        no_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteTotalValue = noteTotal.getText().toString();
                noteTotal.setText(noteTotalValue + "2");
                noteTotal.setSelection(noteTotal.getText().toString().length());
            }
        });

        no_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteTotalValue = noteTotal.getText().toString();
                noteTotal.setText(noteTotalValue + "3");
                noteTotal.setSelection(noteTotal.getText().toString().length());
            }
        });

        no_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteTotalValue = noteTotal.getText().toString();
                noteTotal.setText(noteTotalValue + "4");
                noteTotal.setSelection(noteTotal.getText().toString().length());
            }
        });

        no_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteTotalValue = noteTotal.getText().toString();
                noteTotal.setText(noteTotalValue + "5");
                noteTotal.setSelection(noteTotal.getText().toString().length());
            }
        });

        no_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteTotalValue = noteTotal.getText().toString();
                noteTotal.setText(noteTotalValue + "6");
                noteTotal.setSelection(noteTotal.getText().toString().length());
            }
        });

        no_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteTotalValue = noteTotal.getText().toString();
                noteTotal.setText(noteTotalValue + "7");
                noteTotal.setSelection(noteTotal.getText().toString().length());
            }
        });

        no_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteTotalValue = noteTotal.getText().toString();
                noteTotal.setText(noteTotalValue + "8");
                noteTotal.setSelection(noteTotal.getText().toString().length());
            }
        });

        no_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteTotalValue = noteTotal.getText().toString();
                noteTotal.setText(noteTotalValue + "9");
                noteTotal.setSelection(noteTotal.getText().toString().length());
            }
        });


        point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPoint.equalsIgnoreCase("no")) {
                    String noteTotalValue = noteTotal.getText().toString();
                    noteTotal.setText(noteTotalValue + ".");

                    isPoint = "yes";
                }
            }
        });

        tenPound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String noteTotalValue = "10";
                if (noteTotalValue.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Put Amount", Toast.LENGTH_LONG).show();
                } else if (Double.parseDouble(noteTotalValue) < totalptice) {

                    Toast.makeText(getContext(), "Not Enough Amount", Toast.LENGTH_LONG).show();

                } else {
                    Type = Constants.PAYMENT_TYPE_CASH;
                    returnpound = Double.parseDouble(noteTotalValue) - totalptice;
                    mListener.returnbackMoney(splitBillIndex, cartItems, String.valueOf(returnpound), Type, checkboxSendSMS.isChecked(), true);
                    dismiss();
                }
            }
        });

        twentyPound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String noteTotalValue = "20";
                if (noteTotalValue.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Put Amount", Toast.LENGTH_LONG).show();
                } else if (Double.parseDouble(noteTotalValue) < totalptice) {

                    Toast.makeText(getContext(), "Not Enough Amount", Toast.LENGTH_LONG).show();

                } else {
                    Type = Constants.PAYMENT_TYPE_CASH;
                    returnpound = Double.parseDouble(noteTotalValue) - totalptice;
                    mListener.returnbackMoney(splitBillIndex, cartItems, String.valueOf(returnpound), Type, checkboxSendSMS.isChecked(), true);
                    dismiss();
                }
            }
        });

        thirtyPound.setOnClickListener(v -> {
            String noteTotalValue = "30";
            if (noteTotalValue.equalsIgnoreCase("")) {
                Toast.makeText(getContext(), "Put Amount", Toast.LENGTH_LONG).show();
            } else if (Double.parseDouble(noteTotalValue) < totalptice) {

                Toast.makeText(getContext(), "Not Enough Amount", Toast.LENGTH_LONG).show();

            } else {
                Type = Constants.PAYMENT_TYPE_CASH;
                returnpound = Double.parseDouble(noteTotalValue) - totalptice;
                mListener.returnbackMoney(splitBillIndex, cartItems, String.valueOf(returnpound), Type, checkboxSendSMS.isChecked(), true);
                dismiss();
            }
        });

        fortyPound.setOnClickListener(v -> {
            String noteTotalValue = "40";
            if (noteTotalValue.equalsIgnoreCase("")) {
                Toast.makeText(getContext(), "Put Amount", Toast.LENGTH_LONG).show();
            } else if (Double.parseDouble(noteTotalValue) < totalptice) {

                Toast.makeText(getContext(), "Not Enough Amount", Toast.LENGTH_LONG).show();

            } else {
                Type = Constants.PAYMENT_TYPE_CASH;
                returnpound = Double.parseDouble(noteTotalValue) - totalptice;
                mListener.returnbackMoney(splitBillIndex, cartItems, String.valueOf(returnpound), Type, checkboxSendSMS.isChecked(), true);
                dismiss();
            }
        });

        fiftyPound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String noteTotalValue = "50";
                if (noteTotalValue.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Put Amount", Toast.LENGTH_LONG).show();
                } else if (Double.parseDouble(noteTotalValue) < totalptice) {

                    Toast.makeText(getContext(), "Not Enough Amount", Toast.LENGTH_LONG).show();

                } else {
                    Type = Constants.PAYMENT_TYPE_CASH;
                    returnpound = Double.parseDouble(noteTotalValue) - totalptice;
                    mListener.returnbackMoney(splitBillIndex, cartItems, String.valueOf(returnpound), Type, checkboxSendSMS.isChecked(), true);
                    dismiss();
                }
            }
        });

        sixtyPound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String noteTotalValue = "60";
                if (noteTotalValue.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Put Amount", Toast.LENGTH_LONG).show();
                } else if (Double.parseDouble(noteTotalValue) < totalptice) {

                    Toast.makeText(getContext(), "Not Enough Amount", Toast.LENGTH_LONG).show();

                } else {
                    Type = Constants.PAYMENT_TYPE_CASH;
                    returnpound = Double.parseDouble(noteTotalValue) - totalptice;
                    mListener.returnbackMoney(splitBillIndex, cartItems, String.valueOf(returnpound), Type, checkboxSendSMS.isChecked(), true);
                    dismiss();
                }
            }
        });


        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteTotal.setText("");
                isPoint = "no";
            }
        });

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteTotalValue = noteTotal.getText().toString();
                if (noteTotalValue.equalsIgnoreCase("")) {
                    Toast.makeText(getContext(), "Put Amount", Toast.LENGTH_LONG).show();
                } else if (Double.parseDouble(noteTotalValue) < totalptice) {

                    Toast.makeText(getContext(), "Not Enough Amount", Toast.LENGTH_LONG).show();

                } else {
                    Type = Constants.PAYMENT_TYPE_CASH;
                    returnpound = Double.parseDouble(noteTotalValue) - totalptice;
                    mListener.returnbackMoney(splitBillIndex, cartItems, String.valueOf(returnpound), Type, checkboxSendSMS.isChecked(), true);
                    dismiss();
                }

            }
        });

 /*       cash.setOnClickListener(v -> {
            if (cartItems != null)
                Log.e(TAG, "------------cart size: " + cartItems.size());

            String noteTotalValue = totalptice + "";
            if (noteTotalValue.equalsIgnoreCase("")) {
                Toast.makeText(getContext(), "Put Amount", Toast.LENGTH_LONG).show();
            } else if (Double.parseDouble(noteTotalValue) < totalptice) {

                Toast.makeText(getContext(), "Not Enough Amount", Toast.LENGTH_LONG).show();

            } else {
                Type = Constants.PAYMENT_TYPE_CASH;
                returnpound = Double.parseDouble(noteTotalValue) - totalptice;
                mListener.returnbackMoney(splitBillIndex, cartItems, String.valueOf(returnpound), Type, checkboxSendSMS.isChecked());
                dismiss();
            }
        });

        rootView.findViewById(R.id.pay_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Type = Constants.PAYMENT_TYPE_CARD;
                mListener.returnbackMoney(splitBillIndex, cartItems, "", Type, checkboxSendSMS.isChecked());
                dismiss();
            }
        });*/

        cashNotPaid.setOnClickListener(v -> {
            if (cartItems != null)
                Log.e(TAG, "------------cart size: " + cartItems.size());

           /* String noteTotalValue = (noteTotal.getText().toString().trim().isEmpty())?totalptice + "":noteTotal.getText().toString().trim();
            if (noteTotalValue.equalsIgnoreCase("")) {
                Toast.makeText(getContext(), "Put Amount", Toast.LENGTH_LONG).show();
            } else if (Double.parseDouble(noteTotalValue) < totalptice) {

                Toast.makeText(getContext(), "Not Enough Amount", Toast.LENGTH_LONG).show();

            } else {
                Type = Constants.PAYMENT_TYPE_CASH;
                returnpound = Double.parseDouble(noteTotalValue) - totalptice;
                mListener.returnbackMoney(splitBillIndex, cartItems, String.valueOf(returnpound), Type, checkboxSendSMS.isChecked(), false);
                dismiss();
            }*/
            Type = Constants.PAYMENT_TYPE_CASH;
            mListener.returnbackMoney(splitBillIndex, cartItems, "0.0", Type, checkboxSendSMS.isChecked(), false);
            dismiss();
        });

        cashPaid.setOnClickListener(v -> {
//            if (cartItems != null)
//                Log.e(TAG, "------------cart size: " + cartItems.size());

            String noteTotalValue = (noteTotal.getText().toString().trim().isEmpty()) ? totalptice + "" : noteTotal.getText().toString().trim();
//            if (noteTotalValue.equalsIgnoreCase("")) {
//                Toast.makeText(getContext(), "Put Amount", Toast.LENGTH_LONG).show();
//            } else
            if (Double.parseDouble(noteTotalValue) < totalptice) {
                Toast.makeText(getContext(), "Not Enough Amount", Toast.LENGTH_LONG).show();
            } else {
                Type = Constants.PAYMENT_TYPE_CASH;
                returnpound = Double.parseDouble(noteTotalValue) - totalptice;
                mListener.returnbackMoney(splitBillIndex, cartItems, String.valueOf(returnpound), Type, checkboxSendSMS.isChecked(), true);
                dismiss();
            }
        });

        cardNotPaid.setOnClickListener(v -> {
            Type = Constants.PAYMENT_TYPE_CARD;
            mListener.returnbackMoney(splitBillIndex, cartItems, "0.0", Type, checkboxSendSMS.isChecked(), false);
            dismiss();
        });

        cardPaid.setOnClickListener(v -> {
            Type = Constants.PAYMENT_TYPE_CARD;
            mListener.returnbackMoney(splitBillIndex, cartItems, "0.0", Type, checkboxSendSMS.isChecked(), true);
            dismiss();
        });

        sendPaymentByPhone.setOnClickListener(v -> {
            PayByPhoneDialoge dialoge = PayByPhoneDialoge.newInstance(cartItems, splitBillIndex, String.valueOf(totalptice));
            dialoge.show(getFragmentManager(), null);
            dismiss();
        });


        checkboxSendSMS = rootView.findViewById(R.id.checkboxSendSMS);
        checkboxSendSMS.setChecked(sharedPreferences.getBoolean(Preferences.SEND_TEXT_MESSAGE_AFTER_ORDER, false));

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
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.7);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);

            dialog.getWindow().setLayout(width, height);
        }
    }

    public int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }


    public interface PounddialogListener {
        void returnbackMoney(int splitBillIndex, List<CartItem> itemsToRemove, String returnbackpound, String whatType, boolean sendSMS, boolean paid);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PounddialogListener) {
            mListener = (PounddialogListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PounddialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
