package com.foodciti.foodcitipartener.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.foodciti.foodcitipartener.response.GenericResponse;
import com.foodciti.foodcitipartener.response.RestaurentUser;
import com.foodciti.foodcitipartener.rest.RetroClient;
import com.foodciti.foodcitipartener.utils.Consts;
import com.foodciti.foodcitipartener.utils.InternetConnection;
import com.foodciti.foodcitipartener.utils.SessionManager;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FireBaseDialoge extends DialogFragment {
    private SessionManager sessionManager;
    private static final String TAG = "LogInActivity";
    private EditText userName, password;
    private Button Login;
    private TextView forgotPassword, createAccount, bottomText;
    ImageView close;
    CheckBox showPassword;
    private ProgressBar progressBar;
    private FireBaseDialoge() {
    }

    public static FireBaseDialoge newInstance(String baseUrl) {
        FireBaseDialoge addonEditDialog = new FireBaseDialoge();
//        Bundle args = new Bundle();
//        args.putString(ARG_BASE_URL, baseUrl);
//        addonEditDialog.setArguments(args);
        addonEditDialog.setCancelable(true);

        return addonEditDialog;
    }

    public static FireBaseDialoge newInstance() {
        FireBaseDialoge itemEditDialog = new FireBaseDialoge();
        return itemEditDialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        View view = inflater.inflate(R.layout.activity_login_pro, container, false);
        userName =view.findViewById(R.id.userName);
        password =view.findViewById(R.id.password);
        Login =view.findViewById(R.id.loginButton);
        forgotPassword =view.findViewById(R.id.forgotPassword);
        createAccount =view.findViewById(R.id.createAccount);
        bottomText =view.findViewById(R.id.bottomText);
        progressBar =view.findViewById(R.id.progressBar1);
        showPassword= view.findViewById(R.id.showPassword);
        close=view.findViewById(R.id.close);
//        userName.setText("holburykebab@foodciti.co.uk");
//        password.setText("pass@123");
        closeSoftKeyBoard();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
//session
        sessionManager = new SessionManager(getActivity());

        if (sessionManager.isLoggedIn()) {
            //    Intent intent = new Intent(LogInActivity.this, RestaurentMainActivity.class);
            Intent intent = new Intent(getActivity(), ResturantMainActivityNewPro.class);
            startActivity(intent);
            getActivity().finish();
        }
        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // show password
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    // hide password
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }}
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View focusView;
                if (TextUtils.isEmpty(userName.getText().toString())) {
                    focusView = userName;
                    focusView.requestFocus();
                    userName.setError("Please Enter The Username");
                } else if (TextUtils.isEmpty(password.getText().toString())) {
                    focusView = password;
                    focusView.requestFocus();
                    password.setError("Please Enter The Password");
                } else {
                    getUserInfo(userName.getText().toString(), password.getText().toString());
                }
            }
        });
        return view;
    }

    private void getUserInfo(String userName, String password) {
        Log.e("USER_OUT", "" + userName + ", " + password);
        if (InternetConnection.checkConnection(getActivity())) {
            Log.e("USER", "" + userName + ", " + password);
            Observable<GenericResponse<RestaurentUser>> results = RetroClient.getApiService()
                    .getUserInfo(userName, password);
            showProgressBar();
            new CompositeDisposable().add(results.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GenericResponse<RestaurentUser>>() {
                        @Override
                        public void accept(GenericResponse<RestaurentUser> response) throws Exception {
                            Log.e("RESPONSE_LOGIN", "" + response.getData());
                            if (response.getStatus().equals(Consts.IS_SUCCESS)) {
                                sessionManager.setLogin(true);
                                SessionManager.get(getActivity()).setUserId(response.getData().getFoodTruckUserId());
                                Toast.makeText(getActivity(), "Login Successfully !"+response.getData().getFoodTruckUserId(), Toast.LENGTH_LONG).show();
                                getFoodTruckConnected();
                            } else {
                                hideProgressBar();
                                //  Toast.makeText(LogInActivity.this, response.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            SessionManager.get(getActivity()).setUserName(response.
                                    getData().getEmailId());
                            SessionManager.get(getActivity()).setUserToken(response.getData().getUserToken());
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable e) throws Exception {
                            Log.e(TAG, "GET_INFO_ERROR : " + e.toString());
                            hideProgressBar();
                            // Toast.makeText(LogInActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }));
        } else {
            Toast.makeText(getActivity(), R.string.string_internet_connection_warning, Toast.LENGTH_LONG).show();

        }
    }

    private void getFoodTruckConnected() {
        if (InternetConnection.checkConnection(getActivity())) {
            Observable<GenericResponse<String>> results = RetroClient.getApiService()
                    .getConnectedFoodTruckId(SessionManager.get(getActivity()).getUserId());
            new CompositeDisposable().add(results.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GenericResponse<String>>() {
                        @Override
                        public void accept(GenericResponse<String> response) throws Exception {
                            hideProgressBar();
                            if (response.getStatus().equals(Consts.IS_SUCCESS)) {
                                //Launch main activity
                                // Toast.makeText(LogInActivity.this,response.getData(), Toast.LENGTH_LONG).show();
                                SessionManager.get(getActivity()).setFoodTruckId(response.getData());
                                Intent intent = new Intent(getActivity(), ResturantMainActivityNewPro.class);
                                //  Intent intent = new Intent(LogInActivity.this, ResturantMainActivityPro.class);
                                startActivity(intent);
                                dismiss();
                            } else {
                                // SessionManager.get(LogInActivity.this).clearFoodTruck();
                                //   Toast.makeText(LogInActivity.this, response.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
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
}
