package com.foodciti.foodcitipartener.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.keyboards.ExtendedKeyBoard;
import com.foodciti.foodcitipartener.keyboards.NumPad;
import com.foodciti.foodcitipartener.parser.CallHttp;
import com.foodciti.foodcitipartener.realm_entities.CartItem;
import com.foodciti.foodcitipartener.realm_entities.Vendor;
import com.foodciti.foodcitipartener.utils.AppConfig;
import com.foodciti.foodcitipartener.utils.CheckConnection;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.DecimalDigitsInputFilter;
import com.foodciti.foodcitipartener.utils.Preferences;
import com.foodciti.foodcitipartener.utils.RealmManager;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;

import static android.content.Context.MODE_PRIVATE;
import static com.foodciti.foodcitipartener.utils.AppConfig.MyPREFERENCES;

public class PayByPhoneDialoge extends DialogFragment implements View.OnFocusChangeListener, View.OnKeyListener {
    private static final String TAG = "PayByPhoneDialoge";
    private static final String ARG_TOTAL_PRICE = "total_price";
    private static final String ARG_CART_ITEMS = "cart_items";
    private static final String ARG_SPLITBILL_INDEX = "splitbill_index";
    private String baseUrl = "";
    HashMap<String, String> mParams;
    ProgressBar progress;
    Button payBtn;
    EditText cardNoTxt, edtCardHoNameTxt, expiryTxt, cvvTxt;
    TextView totalAmountTxt;
    private ExtendedKeyBoard keyBoard;
    private NumPad numPad;
    private LinearLayout formlayout;
    private String totalAmount = "";
    String Type = "";
    String restroID = "";
    private PounddialogListener mListener;
    private int splitBillIndex = -1;
    private Vendor vendor;
    private Realm realm;
    private List<CartItem> cartItems;

    //    private long[] cartItemPKs;
    private PayByPhoneDialoge() {
    }

    public static PayByPhoneDialoge newInstance(List<CartItem> mCartItems, int splitBillIndex, String amountValue) {
        PayByPhoneDialoge addonEditDialog = new PayByPhoneDialoge();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TOTAL_PRICE, amountValue);
        bundle.putSerializable(ARG_CART_ITEMS, (Serializable) mCartItems);
        bundle.putInt(ARG_SPLITBILL_INDEX, splitBillIndex);
        addonEditDialog.setArguments(bundle);
        addonEditDialog.setCancelable(true);

        return addonEditDialog;
    }

    public static PayByPhoneDialoge newInstance() {
        PayByPhoneDialoge itemEditDialog = new PayByPhoneDialoge();
        return itemEditDialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            if (arguments.containsKey(ARG_TOTAL_PRICE))
                totalAmount = arguments.getString(ARG_TOTAL_PRICE);
            if (arguments.containsKey(ARG_CART_ITEMS))
                cartItems = (ArrayList<CartItem>) arguments.getSerializable(ARG_CART_ITEMS);
//                cartItemPKs = arguments.getLongArray(ARG_CART_ITEMS);
            if (arguments.containsKey(ARG_SPLITBILL_INDEX))
                splitBillIndex = arguments.getInt(ARG_SPLITBILL_INDEX);
            realm = RealmManager.getLocalInstance();
            vendor = realm.where(Vendor.class).findFirst();
            if (vendor != null) {
                restroID=vendor.getRestroID();
            }
        }
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
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 1);
            dialog.getWindow().setLayout(width, height);
        }
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

    public interface PounddialogListener {
        void returnbackMoney(int splitBillIndex, List<CartItem> itemsToRemove, String returnbackpound, String whatType, boolean sendSMS, boolean paid);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialoge_payby_phone, container, false);

//        realm = RealmManager.getLocalInstance();
//        Bundle bundle = getArguments();
//        if (bundle.containsKey(ARG_SPLITBILL_INDEX) && bundle.containsKey(ARG_CART_ITEMS)) {
//            splitBillIndex = getArguments().getInt(ARG_SPLITBILL_INDEX);
//            Log.e(TAG, "-------------split bill index: " + splitBillIndex);
//            long[] array = cartItemPKs;
//            if(array!=null)
//            {
//                Long[] ids = new Long[array.length];
//                for (int i = 0; i < array.length; i++)
//                    ids[i] = array[i];
//                cartItems = realm.where(CartItem.class).in("id", ids).findAll();
//            }
//
//        }

        cardNoTxt = (EditText) view.findViewById(R.id.edtCardNoTxt);
        edtCardHoNameTxt = (EditText) view.findViewById(R.id.edtCardHoNameTxt);
        expiryTxt = (EditText) view.findViewById(R.id.expiryTxt);
        cvvTxt = (EditText) view.findViewById(R.id.cvvTxt);
        totalAmountTxt = (TextView) view.findViewById(R.id.totalAmountTxt);
        double updatedamnt=Double.parseDouble(totalAmount);
        totalAmountTxt.setText(String.format("%.2f", updatedamnt));
        totalAmountTxt.setShowSoftInputOnFocus(false);
        totalAmountTxt.setOnKeyListener(this);
        totalAmountTxt.setOnFocusChangeListener(this);

        cardNoTxt.setShowSoftInputOnFocus(false);
        cardNoTxt.setOnKeyListener(this);
        cardNoTxt.setOnFocusChangeListener(this);

        edtCardHoNameTxt.setShowSoftInputOnFocus(false);
        edtCardHoNameTxt.setOnKeyListener(this);
        edtCardHoNameTxt.setOnFocusChangeListener(this);

        expiryTxt.setShowSoftInputOnFocus(false);
        expiryTxt.setOnKeyListener(this);
        expiryTxt.setOnFocusChangeListener(this);

        cvvTxt.setShowSoftInputOnFocus(false);
        cvvTxt.setOnKeyListener(this);
        cvvTxt.setOnFocusChangeListener(this);

        progress = (ProgressBar) view.findViewById(R.id.progress);
        progress.setVisibility(View.GONE);
        payBtn = (Button) view.findViewById(R.id.payBtn);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int height = display.getHeight();
        formlayout = (LinearLayout) view.findViewById(R.id.formlayout);
        keyBoard = (ExtendedKeyBoard) view.findViewById(R.id.keyBoard);
        numPad = view.findViewById(R.id.numPad);
        int hight1 = (int) (height * 0.58);
        int hight2 = (int) (height * 0.42);
        keyBoard.getLayoutParams().height = hight2;
        formlayout.getLayoutParams().height = hight1;
        keyBoard.requestLayout();
        View closebtn = view.findViewById(R.id.close);
        closebtn.setOnClickListener(v -> {
            dismiss();
        });

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPaymentAPI();
            }
        });

        expiryTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = expiryTxt.getText().toString().trim().length();
                if (length >= 5) {
                    cvvTxt.requestFocus();
                }
                if (length == 2) {
                    String text = expiryTxt.getText().toString();
                    expiryTxt.setText(text + "/");
                    expiryTxt.requestFocus();
                    int position = expiryTxt.length();
                    Editable etext = expiryTxt.getText();
                    Selection.setSelection(etext, position);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cardNoTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = cardNoTxt.getText().toString().trim().length();
                if (length >= 16) {
                    edtCardHoNameTxt.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;

    }

    private void callPaymentAPI() {
        String expStr = expiryTxt.getText().toString();
        String cardNoStr = cardNoTxt.getText().toString();
        String nameStr = edtCardHoNameTxt.getText().toString();
        String cvvStr = cvvTxt.getText().toString();
        if (cardNoStr.equalsIgnoreCase("") || cardNoStr.length() < 16) {
            Toast.makeText(getActivity(), "Enter 16 digits", Toast.LENGTH_SHORT).show();
            return;
        } else if (nameStr.equalsIgnoreCase("")) {
            Toast.makeText(getActivity(), "Enter Card Holder Name", Toast.LENGTH_SHORT).show();
            return;
        } else if (expStr.equalsIgnoreCase("") || expStr.length() < 5) {
            Toast.makeText(getActivity(), "Enter Card Expiry", Toast.LENGTH_SHORT).show();
            return;
        } else if (cvvStr.equalsIgnoreCase("") || cvvStr.length() < 3) {
            Toast.makeText(getActivity(), "Enter Card CVV", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (restroID.equalsIgnoreCase("")||restroID==null) {
            Toast.makeText(getActivity(), "Restaurant id missing", Toast.LENGTH_SHORT).show();
            return;
        }

        String spl[]=expStr.split("/");
        String month=spl[0];
        String year=spl[1];

        mParams = new HashMap<>();
        mParams.put("expYear", "20"+year);
        mParams.put("expMonth", month);
        mParams.put("number", cardNoStr);
        mParams.put("restaurantid", restroID);
        mParams.put("cvn", cvvStr);
        mParams.put("cardHolderName", nameStr);
        mParams.put("totalamount", totalAmount);

        if (CheckConnection.isNetworkAvailable(getActivity())) {
            new SendPaymentDetailTask().execute();
        } else {
            Toast.makeText(getActivity(), "Ensure your internet connectivity!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        EditText editText = null;
        InputConnection ic = null;
        if (v instanceof EditText) {
            editText = (EditText) v;
            ic = editText.onCreateInputConnection(new EditorInfo());
        }

        if (hasFocus && ic != null) {
            keyBoard.setInputConnection(ic);
            numPad.setInputConnection(ic);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Log.e(TAG, "----------------------keyEvent---------------------");
        switch (keyCode) {
            case ExtendedKeyBoard.KEYCODE_DONE:
                Log.e(TAG, "-------------------keycode: " + keyCode);
                callPaymentAPI();
                return true;
            case KeyEvent.KEYCODE_CLEAR:
                Log.e(TAG, "-------------------keycode: " + keyCode);
                if (v instanceof EditText) {
                    EditText et = (EditText) v;
                    et.setText("");
                }
                return true;
        }
        return false;
    }

    public class SendPaymentDetailTask extends AsyncTask<Void, Void, Boolean> {
        String message = "";
        boolean ispaid = false;
        String transactionID = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            CallHttp callHttp = new CallHttp();
            String json = callHttp.httpPostRequest(AppConfig.TestPaymentUrl, mParams);
            if (json != null && json.length() > 0) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    message = jsonObject.getString("message");
                    ispaid = jsonObject.getBoolean("ispaid");
                    JSONObject dataOBJ = jsonObject.getJSONObject("data");
                    transactionID = dataOBJ.optString("transactionid");
                } catch (Exception e) {
                    ispaid = false;
                    e.printStackTrace();
                }

            }
            return ispaid;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            progress.setVisibility(View.GONE);
            if (success) {
                Type = Constants.PAYMENT_TYPE_PHONE;
//                if(transactionID!=null&&!transactionID.equalsIgnoreCase(""))
//                    Type=Type+"#"+transactionID;
                mListener.returnbackMoney(splitBillIndex, cartItems, "0.0", Type, true, true);
                dismiss();
            } else {
                AlertBox(message);
            }
        }

        @Override
        protected void onCancelled() {
        }
    }

    public void AlertBox(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Done",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
