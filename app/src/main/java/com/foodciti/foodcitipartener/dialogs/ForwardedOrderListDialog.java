package com.foodciti.foodcitipartener.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.ForwardedOrdersAdapter;
import com.foodciti.foodcitipartener.response.GenericResponse;
import com.foodciti.foodcitipartener.response.Order;
import com.foodciti.foodcitipartener.rest.RetroClient;
import com.foodciti.foodcitipartener.utils.Consts;
import com.foodciti.foodcitipartener.utils.InternetConnection;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class ForwardedOrderListDialog extends DialogFragment implements View.OnClickListener, ForwardedOrdersAdapter.ForwardOrderClickListener {
    public static final String TAG = "ForwardedOrderListDialog";

    private Context context;
    private RecyclerView forwardedOrdersRV;
    private ForwardedOrdersAdapter adapter;

    private List<Order> forwardedOrderList = new ArrayList<>();
    private String forwardedRestuarantID;

    public static ForwardedOrderListDialog getInstance(Bundle args)
    {
        ForwardedOrderListDialog dialog = new ForwardedOrderListDialog();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if(args != null) {
            forwardedRestuarantID = args.getString("FORWARDED_RESTAURANT_ID");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.forwarded_dialog_view, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if(window != null) {
                window.setGravity(Gravity.BOTTOM);
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(null);
            }
        }
    }

    private void setDialogHeightWidth()
    {
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if(window != null) {
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                int height = display.getHeight()/2;// ((display.getHeight()*30)/100)
                Log.e("WIDTH_HEIGHT",""+height);
                if(forwardedOrderList != null && forwardedOrderList.size() >= 4){
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height);
                }
                else {
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                window.setBackgroundDrawable(null);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getContext();

        forwardedOrdersRV = view.findViewById(R.id.recycler_view);

        view.findViewById(R.id.close_btn).setOnClickListener(this);

        adapter = new ForwardedOrdersAdapter(getContext(), this);
        forwardedOrdersRV.setAdapter(adapter);

        getForwardOrdersList();
    }


    @Override
    public void onResume() {
        super.onResume();
        firebaseListener();
    }

    private void firebaseListener()
    {
        Log.e("DATA_SNAP_FORWARD",""+forwardedRestuarantID);
        //isNetworkAvailable();
        //get reference to the orders node
        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();
        DatabaseReference mFirebaseDatabase = mFirebaseInstance.getReference();
        mFirebaseDatabase.child(forwardedRestuarantID)
                .addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.e("DATA_SNAP_FORWARD",""+dataSnapshot.getChildren());
                        getForwardOrdersList();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("ONDATA_CANCEL", "onCancelled: new ");
                    }
                });
    }



    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.close_btn:
                dismiss();
                break;
        }
    }


    public void getForwardOrdersList()
    {
        boolean isNetConnected = InternetConnection.checkConnection(context);
        if (isNetConnected)
        {
            Observable<GenericResponse<List<Order>>> results = RetroClient.getApiService()
                    .getForwardedOrderStatus(forwardedRestuarantID);
            new CompositeDisposable().add(results.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GenericResponse<List<Order>>>() {
                        @Override
                        public void accept(GenericResponse<List<Order>> response) throws Exception {
                            forwardedOrderList.clear();
                            Log.e("RESPONSE", "" + response.getStatus());
                            if (response.getStatus().equals("200")) {
                                Log.e("COOKING_SIZE",""+response.getData().size());
                                if (response.getData().size() > 0) {
                                    forwardedOrderList.addAll(response.getData());
                                    adapter.addAllData(forwardedOrderList);

                                    setDialogHeightWidth();
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception
                        {
                            Log.e("NETWORK_ERROR",""+throwable.toString());
                        }
                    }));
        }
    }

    @Override
    public void onForwardedItemClick(Order item) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Consts.ORDER_INFO, item);
        bundle.putBoolean("dialogstatus",true);
        bundle.putString("IS_FROM","FORWARD");

        OrderInfo orderInfo = OrderInfo.newInstance(bundle);
        orderInfo.show(getChildFragmentManager(), OrderInfo.TAG);
    }
}
