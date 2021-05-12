package com.foodciti.foodcitipartener.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.activities.ResturantMainActivityNewPro;
import com.foodciti.foodcitipartener.compound_views.CounterBox;
import com.foodciti.foodcitipartener.response.GenericResponse;
import com.foodciti.foodcitipartener.response.RestaurentUser;
import com.foodciti.foodcitipartener.response.order_response_bean.PrinterSettingData;
import com.foodciti.foodcitipartener.rest.RetroClient;
import com.foodciti.foodcitipartener.utils.Consts;
import com.foodciti.foodcitipartener.utils.InternetConnection;
import com.foodciti.foodcitipartener.utils.Preferences;
import com.foodciti.foodcitipartener.utils.SessionManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;
import static com.foodciti.foodcitipartener.utils.AppConfig.MyPREFERENCES;

public class PrinterDialoge extends DialogFragment {
    private SessionManager sessionManager;
    private static final String TAG = "PrinterDialoge";
    private Button submitBtn;
    ImageView close;
    private ProgressBar progressBar;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private CounterBox printCopyCounterColCash, printCopyCounterColCard, printCopyCounterColOnline;
    private CounterBox printCopyDeliveryCash, printCopyDeliveryCard, printCopyDeliveryOnline;

    private PrinterDialoge() {
    }

    public static PrinterDialoge newInstance(String baseUrl) {
        PrinterDialoge addonEditDialog = new PrinterDialoge();
        addonEditDialog.setCancelable(true);

        return addonEditDialog;
    }

    public static PrinterDialoge newInstance() {
        PrinterDialoge itemEditDialog = new PrinterDialoge();
        return itemEditDialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();
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
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.printer_setting_dialoge, container, false);
        printCopyCounterColCash = view.findViewById(R.id.printCopyCounterColCash);
        printCopyCounterColCash.setLowerLimit(0);
        printCopyCounterColCard = view.findViewById(R.id.printCopyCounterColCard);
        printCopyCounterColCard.setLowerLimit(0);
        printCopyCounterColOnline = view.findViewById(R.id.printCopyCounterColOnline);
        printCopyCounterColOnline.setLowerLimit(0);
        printCopyDeliveryCash = view.findViewById(R.id.printCopyDeliveryCash);
        printCopyDeliveryCash.setLowerLimit(0);
        printCopyDeliveryCard = view.findViewById(R.id.printCopyDeliveryCard);
        printCopyDeliveryCard.setLowerLimit(0);
        printCopyDeliveryOnline = view.findViewById(R.id.printCopyDeliveryOnline);
        printCopyDeliveryOnline.setLowerLimit(0);
        submitBtn = view.findViewById(R.id.submitButton);
        progressBar = view.findViewById(R.id.progressBar1);

        close = view.findViewById(R.id.close);
        closeSoftKeyBoard();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
//session
        sessionManager = new SessionManager(getActivity());
//        sessionManager.setUserId("603e7c64efe47c5e23c07387");
        if (sessionManager.isLoggedIn()) {
            getPrinterSetting();
        } else {
            Toast.makeText(getActivity(), "Please login with eposonline.", Toast.LENGTH_LONG).show();
        }


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String collectionCash = printCopyCounterColCash.getCount() + "";
                String collectionCard = printCopyCounterColCard.getCount() + "";
                String collectionOnline = printCopyCounterColOnline.getCount() + "";
                String deliveryCash = printCopyDeliveryCash.getCount() + "";
                String deliveryCard = printCopyDeliveryCard.getCount() + "";
                String deliveryOnline = printCopyDeliveryOnline.getCount() + "";
                savePrinterSettings(collectionCash, collectionCard, collectionOnline, deliveryCash, deliveryCard, deliveryOnline);
            }
        });

        printCopyCounterColOnline.setOnCounterChangeListener(new CounterBox.CounterListener() {
            @Override
            public void onIncrement(int count) {
                editor.putInt(Preferences.NUM_PRINT_COPIES_COL, count);
                editor.apply();
            }

            @Override
            public void onDecrement(int count) {
                editor.putInt(Preferences.NUM_PRINT_COPIES_COL, count);
                editor.apply();
            }
        });

        printCopyCounterColOnline.setCount(sharedPreferences.getInt(Preferences.NUM_PRINT_COPIES_COL, 0));

        printCopyCounterColCash.setOnCounterChangeListener(new CounterBox.CounterListener() {
            @Override
            public void onIncrement(int count) {
            }

            @Override
            public void onDecrement(int count) {
            }
        });
        printCopyCounterColCard.setOnCounterChangeListener(new CounterBox.CounterListener() {
            @Override
            public void onIncrement(int count) {
            }

            @Override
            public void onDecrement(int count) {
            }
        });

        printCopyDeliveryOnline.setOnCounterChangeListener(new CounterBox.CounterListener() {
            @Override
            public void onIncrement(int count) {
                editor.putInt(Preferences.NUM_PRINT_COPIES_DEL, count);
                editor.apply();
            }

            @Override
            public void onDecrement(int count) {
                editor.putInt(Preferences.NUM_PRINT_COPIES_DEL, count);
                editor.apply();
            }
        });

        printCopyDeliveryOnline.setCount(sharedPreferences.getInt(Preferences.NUM_PRINT_COPIES_DEL, 0));

        printCopyDeliveryCash.setOnCounterChangeListener(new CounterBox.CounterListener() {
            @Override
            public void onIncrement(int count) {
            }

            @Override
            public void onDecrement(int count) {
            }
        });
        printCopyDeliveryCard.setOnCounterChangeListener(new CounterBox.CounterListener() {
            @Override
            public void onIncrement(int count) {
            }

            @Override
            public void onDecrement(int count) {
            }
        });
        return view;
    }

    private void getPrinterSetting() {
        if (InternetConnection.checkConnection(getActivity())) {
            Observable<GenericResponse<PrinterSettingData>> results = RetroClient.getApiService()
                    .getPrinterSetting(SessionManager.get(getActivity()).getFoodTruckId());
            new CompositeDisposable().add(results.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GenericResponse<PrinterSettingData>>() {
                        @Override
                        public void accept(GenericResponse<PrinterSettingData> response) throws Exception {
                            hideProgressBar();
                            if (response.getStatus().equals(Consts.IS_SUCCESS)) {
//                                Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_LONG).show();
                                //Launch main activity
                                PrinterSettingData data = response.getData();
                                printCopyCounterColCash.setCount(Integer.parseInt(data.getCollectionCash()));
                                printCopyCounterColCard.setCount(Integer.parseInt(data.getCollectionCard()));
                                printCopyCounterColOnline.setCount(Integer.parseInt(data.getCollectionOnline()));
                                printCopyDeliveryCash.setCount(Integer.parseInt(data.getDeliveryCash()));
                                printCopyDeliveryCard.setCount(Integer.parseInt(data.getDeliveryCard()));
                                printCopyDeliveryOnline.setCount(Integer.parseInt(data.getDeliveryOnline()));

                                editor.putInt(Preferences.NUM_PRINT_COPIES_COL, Integer.parseInt(data.getCollectionOnline()));
                                editor.putInt(Preferences.NUM_PRINT_COPIES_DEL, Integer.parseInt(data.getDeliveryOnline()));
                                editor.apply();

                            } else {
                                Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            System.out.println(TAG + "" + throwable.getMessage());
                            hideProgressBar();
                        }
                    }));
        } else {
            Toast.makeText(getActivity(), R.string.string_internet_connection_warning, Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }


    private void closeSoftKeyBoard() {
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }


    public void savePrinterSettings(String collectionCash, String collectionCard, String collectionOnline, String deliveryCash, String deliveryCard, String deliveryOnline) {
        if (InternetConnection.checkConnection(getActivity())) {
            Observable<GenericResponse<String>> results = RetroClient.getApiService()
                    .postPrinterSettings(SessionManager.get(getActivity()).getFoodTruckId(), collectionCash, collectionCard, collectionOnline, deliveryCash, deliveryCard, deliveryOnline);
            new CompositeDisposable().add(results.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GenericResponse<String>>() {
                        @Override
                        public void accept(GenericResponse<String> response) throws Exception {
                            if (response.getStatus().equals(Consts.IS_SUCCESS)) {
                                Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_LONG).show();
                                editor.putInt(Preferences.NUM_PRINT_COPIES_COL, Integer.parseInt(collectionOnline));
                                editor.putInt(Preferences.NUM_PRINT_COPIES_DEL, Integer.parseInt(deliveryOnline));
                                editor.apply();
                                dismiss();
                            } else {
                                Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                        }
                    }));
        }
    }
}