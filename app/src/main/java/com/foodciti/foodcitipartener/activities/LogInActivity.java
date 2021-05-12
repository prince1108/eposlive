//package com.foodciti.foodcitipartener.activities;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.text.method.HideReturnsTransformationMethod;
//import android.text.method.PasswordTransformationMethod;
//import android.util.Log;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.EditText;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.foodciti.foodcitipartener.R;
//import com.foodciti.foodcitipartener.response.GenericResponse;
//import com.foodciti.foodcitipartener.response.RestaurentUser;
//import com.foodciti.foodcitipartener.rest.RetroClient;
//import com.foodciti.foodcitipartener.utils.Consts;
//import com.foodciti.foodcitipartener.utils.InternetConnection;
//import com.foodciti.foodcitipartener.utils.SessionManager;
//
//import io.reactivex.Observable;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.disposables.CompositeDisposable;
//import io.reactivex.functions.Consumer;
//import io.reactivex.schedulers.Schedulers;
//
///**
// * Created by quflip1 on 08-02-2017.
// */
//
//public class LogInActivity extends AppCompatActivity {
//    private SessionManager sessionManager;
//    private static final String TAG = "LogInActivity";
//    private EditText userName, password;
//    private Button Login;
//    private TextView forgotPassword, createAccount, bottomText;
//    private ProgressBar progressBar;
//    CheckBox showPassword;
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());
//        setContentView(R.layout.activity_login_pro);
//        userName = findViewById(R.id.userName);
//        password = findViewById(R.id.password);
//        Login = findViewById(R.id.loginButton);
//        forgotPassword = findViewById(R.id.forgotPassword);
//        createAccount = findViewById(R.id.createAccount);
//        bottomText = findViewById(R.id.bottomText);
//        progressBar = findViewById(R.id.progressBar1);
//        showPassword= findViewById(R.id.showPassword);
//        userName.setText("holburykebab@foodciti.co.uk");
//        password.setText("pass@123");
//        closeSoftKeyBoard();
////session
//        sessionManager = new SessionManager(this);
//
//        if (sessionManager.isLoggedIn()) {
//            //    Intent intent = new Intent(LogInActivity.this, RestaurentMainActivity.class);
//            Intent intent = new Intent(LogInActivity.this, ResturantMainActivityNewPro.class);
//            startActivity(intent);
//            finish();
//        }
//        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    // show password
//                    Toast.makeText(LogInActivity.this, "tttt", Toast.LENGTH_SHORT).show();
//                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                } else {
//                    // hide password
//                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                }}
//        });
//
//        Login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                View focusView;
//                if (TextUtils.isEmpty(userName.getText().toString())) {
//                    focusView = userName;
//                    focusView.requestFocus();
//                    userName.setError("Please Enter The Username");
//                } else if (TextUtils.isEmpty(password.getText().toString())) {
//                    focusView = password;
//                    focusView.requestFocus();
//                    password.setError("Please Enter The Password");
//                } else {
//                    getUserInfo(userName.getText().toString(), password.getText().toString());
//                }
//            }
//        });
//    }
//
//    private void getUserInfo(String userName, String password) {
//        Log.e("USER_OUT", "" + userName + ", " + password);
//        if (InternetConnection.checkConnection(LogInActivity.this)) {
//            Log.e("USER", "" + userName + ", " + password);
//            Observable<GenericResponse<RestaurentUser>> results = RetroClient.getApiService()
//                    .getUserInfo(userName, password);
//            showProgressBar();
//            new CompositeDisposable().add(results.subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Consumer<GenericResponse<RestaurentUser>>() {
//                        @Override
//                        public void accept(GenericResponse<RestaurentUser> response) throws Exception {
//                            Log.e("RESPONSE_LOGIN", "" + response.getStatus());
//                            if (response.getStatus().equals(Consts.IS_SUCCESS)) {
//                                sessionManager.setLogin(true);
//                                SessionManager.get(LogInActivity.this).setUserId(response.getData().getFoodTruckUserId());
//                                Toast.makeText(LogInActivity.this, "Login Successfully !", Toast.LENGTH_LONG).show();
//                                getFoodTruckConnected();
//                            } else {
//                                hideProgressBar();
//                                //  Toast.makeText(LogInActivity.this, response.getMessage(), Toast.LENGTH_LONG).show();
//                            }
//
//                            SessionManager.get(LogInActivity.this).setUserName(response.
//                                    getData().getEmailId());
//                            SessionManager.get(LogInActivity.this).setUserToken(response.getData().getUserToken());
//                        }
//                    }, new Consumer<Throwable>() {
//                        @Override
//                        public void accept(Throwable e) throws Exception {
//                            Log.e(TAG, "GET_INFO_ERROR : " + e.toString());
//                            hideProgressBar();
//                            // Toast.makeText(LogInActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    }));
//        } else {
//            Toast.makeText(LogInActivity.this, R.string.string_internet_connection_warning, Toast.LENGTH_LONG).show();
//
//        }
//    }
//
//
////    private void testInfo() {
////        if (InternetConnection.checkConnection(LogInActivity.this)) {
////            Observable<ResponseBody> results = RetroClient.getApiService()
////                    .testInfo("Basic NTcyODQ4MDc0NzEzNTIwOToxMjhiNTNkMS1iMTc2LTQxOTEtYWJmYS1hN2RhYjIzYmFjMzk=", "application/x-www-form-urlencoded",
////                            "John Q. Test", "Visa", "4747474747474747", 12, 25, 999, "123 A. Street",
////                            "Orem", "UT", "84058", "USA", "4355906", "TpB4VbN4Z/JXqJlLczGNib9qnO8T4ZAiWBAvbEiRVpu6LdwdxKqbgqvKXBIcUDVX2sIZXglUD1vdfyXXCxEJSBoEVO3AeqNcG6xe/23tpjiF2to8wduOYMN+6FdX59PcdvvALyVy+Zfitn+gMurHaioQwsdkF/d21mLcyRTlPWGTuU0IyT4pxxKGszGT0IBdRUJkm5BmtiXzEJ2qD+7pPqwfaHM6sdCqJEa6Tqg+Hvq7KdorW8egNF6aKfw+8AcLtSm0fqU/STWqKxhVjJvUAtAEA0n/DTrufoRWYoz8Oee9P2G7GHbLN1DO3hq6wqpMC6LCFEi6wT6GKHLd8P0YVhmhfN4VmA2woMb7RpeRZInGMKrbP7a6ldEBr22o7nKeMfAjqw5vanhyvKekzOhqlF9Nbu0d7Ekb+xwmoPuD/RGa4Jzy1G8bfG5VLw88CJ3TIFDuRjTs9pimec7oFzLfRBUFS9V3PocvfaHVNqzjOxeN/lJ/s3Ic0v9Zb5ayMFp3/GVUe/wmR9MClMXvCDtr6oHQ3dehXWgKbjaYw0cMS5s=");
//////            showProgressBar();
////            new CompositeDisposable().add(results.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
////                    .subscribe(new Consumer<ResponseBody>() {
////                        @Override
////                        public void accept(ResponseBody response) throws Exception {
////                            try {
////                                org.jsoup.nodes.Document doc = Jsoup.parse(response.string());
////                                String value = doc.getElementById("ResponseCipher").val();
////                                Log.d(TAG, "onNext: " + value);
////                                //    Toast.makeText(LogInActivity.this, value, Toast.LENGTH_LONG).show();
////
////                            } catch (IOException e) {
////                                e.printStackTrace();
////                            }
////                        }
////                    }, new Consumer<Throwable>() {
////                        @Override
////                        public void accept(Throwable throwable) throws Exception
////                        {
////                            //                            hideProgressBar();
////                            //  Toast.makeText(LogInActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
////                        }
////                    }));
////        } else {
////            Toast.makeText(LogInActivity.this, R.string.string_internet_connection_warning, Toast.LENGTH_LONG).show();
////
////        }
////    }
//
//
//    private void getFoodTruckConnected() {
//        if (InternetConnection.checkConnection(LogInActivity.this)) {
//
//            Observable<GenericResponse<String>> results = RetroClient.getApiService()
//                    .getConnectedFoodTruckId(SessionManager.get(LogInActivity.this).getUserId());
//            new CompositeDisposable().add(results.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Consumer<GenericResponse<String>>() {
//                        @Override
//                        public void accept(GenericResponse<String> response) throws Exception {
//                            hideProgressBar();
//                            if (response.getStatus().equals(Consts.IS_SUCCESS)) {
//                                //Launch main activity
//                                // Toast.makeText(LogInActivity.this,response.getData(), Toast.LENGTH_LONG).show();
//                                SessionManager.get(LogInActivity.this).setFoodTruckId(response.getData());
//                                Intent intent = new Intent(LogInActivity.this, ResturantMainActivityNewPro.class);
//                                //  Intent intent = new Intent(LogInActivity.this, ResturantMainActivityPro.class);
//                                startActivity(intent);
//                                finish();
//                            } else {
//                                // SessionManager.get(LogInActivity.this).clearFoodTruck();
//                                //   Toast.makeText(LogInActivity.this, response.getMessage(), Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    }, new Consumer<Throwable>() {
//                        @Override
//                        public void accept(Throwable throwable) throws Exception {
//                            hideProgressBar();
//                        }
//                    }));
//        } else {
//            Toast.makeText(LogInActivity.this, R.string.string_internet_connection_warning, Toast.LENGTH_LONG).show();
//
//        }
//    }
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }
//
//    private void showProgressBar() {
//        progressBar.setVisibility(View.VISIBLE);
//    }
//
//    private void hideProgressBar() {
//        progressBar.setVisibility(View.GONE);
//    }
//
//
//    private void closeSoftKeyBoard() {
//        getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
//        );
//    }
//}
