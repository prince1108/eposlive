package com.foodciti.foodcitipartener.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.activities.ResturantMainActivityNewPro;
import com.foodciti.foodcitipartener.response.GenericResponse;
import com.foodciti.foodcitipartener.rest.RetroClient;
import com.foodciti.foodcitipartener.utils.InternetConnection;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class CookingTime extends DialogFragment {


    private TextView close;
    private ProgressBar progressBar;
    private OnClickListener listener;
    TextView order_type;

    LinearLayout one_5,three_0,four_5,six_0,seven_5,nine_0,one_05,one_20,one_35;

    public interface OnClickListener {
        void print();
    }
    public void setItemListener(OnClickListener listener) {
        this.listener = listener;
    }

    public static CookingTime getInstance(String orderid, String order_type) {
        CookingTime cookingTime = new CookingTime();
        Bundle bundle = new Bundle();
        bundle.putString("orderid", orderid);
        bundle.putString("ordertype", order_type);

        cookingTime.setArguments(bundle);
        return cookingTime;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String orderType=getArguments().getString("ordertype");

        Log.e("ORDER_TYPE",""+orderType);
        View rootView;
        if(orderType.equalsIgnoreCase("Pick-up")){
            rootView = inflater.inflate(R.layout.cooking_time_pickup, container,
                    false);
        }else{
            rootView = inflater.inflate(R.layout.cooking_time_delivery, container,
                    false);
        }

        progressBar = rootView.findViewById(R.id.progressBar1);
        close =  rootView.findViewById(R.id.close_dialog);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        order_type=rootView.findViewById(R.id.order_type);



       order_type.setText(orderType);

        one_5= rootView.findViewById(R.id.fifteen);
        one_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCookingTimes(getArguments().getString("orderid"),"15");
//                dismiss();
//                navigateToMainActivity();
            }
        });
        three_0= rootView.findViewById(R.id.three_0);
        three_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCookingTimes(getArguments().getString("orderid"),"30");
//                dismiss();
//                navigateToMainActivity();
            }
        });
        four_5= rootView.findViewById(R.id.four_5);
        four_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCookingTimes(getArguments().getString("orderid"),"45");
//                dismiss();
//                navigateToMainActivity();
            }
        });
        six_0= rootView.findViewById(R.id.six_0);
        six_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCookingTimes(getArguments().getString("orderid"),"60");
//                dismiss();
//                navigateToMainActivity();
            }
        });
        seven_5= rootView.findViewById(R.id.seven_5);
        seven_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCookingTimes(getArguments().getString("orderid"),"75");
//                dismiss();
//                navigateToMainActivity();
            }
        });
        nine_0= rootView.findViewById(R.id.nine_0);
        nine_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCookingTimes(getArguments().getString("orderid"),"90");
//                dismiss();
//                navigateToMainActivity();
            }
        });
        one_05= rootView.findViewById(R.id.one_05);
        one_05.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCookingTimes(getArguments().getString("orderid"),"105");
//                dismiss();
//                navigateToMainActivity();

            }
        });
        one_20= rootView.findViewById(R.id.one_20);
        one_20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCookingTimes(getArguments().getString("orderid"),"120");
//                dismiss();
//                navigateToMainActivity();
            }
        });
        one_35= rootView.findViewById(R.id.one_35);
        one_35.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCookingTimes(getArguments().getString("orderid"),"135");
//                dismiss();
//                navigateToMainActivity();
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
                    .setLayout((int) (getScreenWidth(getActivity()) * .65), (int) (getScreenWidth(getActivity()) * .5));
        }
    }

    public int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }



    private void setCookingTimes(String orderid, String ordertAt) {

      //  Toast.makeText(getActivity(), data.toString(), Toast.LENGTH_LONG).show();
        if (InternetConnection.checkConnection(getActivity())) {
            //If the internet is working then only make the request
            Observable<GenericResponse> results = RetroClient.getApiService().
                    giveCookingTime(orderid,ordertAt);
            showProgressBar();
            new CompositeDisposable().add(results.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GenericResponse>() {
                        @Override
                        public void accept(GenericResponse genericResponse) throws Exception {
                            Log.e("TIME_RESPONSE",""+genericResponse.getStatus()+", "+genericResponse.getMessage());
                            hideProgressBar();
                            listener.print();

                            dismissAllowingStateLoss();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable e) throws Exception {
                            hideProgressBar();
                            Log.e("TIME_ERROR",""+e.toString());
                        }
                    }));
        } else {
            Toast.makeText(getActivity(), R.string.string_internet_connection_warning, Toast.LENGTH_LONG).show();

        }
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void navigateToMainActivity()
    {
        Log.e("NAVIGATION"," TO MAIN ACTIVITY");
        Intent intent=new Intent(getContext(), ResturantMainActivityNewPro.class);
        startActivity(intent);
    }
}
