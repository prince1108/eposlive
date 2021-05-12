package com.foodciti.foodcitipartener.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.DriverListAdapter;
import com.foodciti.foodcitipartener.realm_entities.Addon;
import com.foodciti.foodcitipartener.realm_entities.Driver;
import com.foodciti.foodcitipartener.realm_entities.Order;
import com.foodciti.foodcitipartener.realm_entities.OrderTuple;
import com.foodciti.foodcitipartener.realm_entities.Purchase;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.text.SimpleDateFormat;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class AssignOrderToDriverDialog extends DialogFragment {
    private static final String TAG = "AssignOrderToDriver";
    private Realm realm;
    private RecyclerView driversRV;
    private DriverListAdapter driverListAdapter;
    private ImageView closebtn;

    private TextView dateTime, orderNo, subTotal, adjustment;

    private SimpleDateFormat simpleDateFormat;
    private String orderType;

    private Callback callback;
    private RealmResults<Driver> driversRealmResultsAsync;

    private AssignOrderToDriverDialog(){}

    public static AssignOrderToDriverDialog getInstance(long orderid) {
        AssignOrderToDriverDialog assignOrderToDriverDialog = new AssignOrderToDriverDialog();
        Bundle bundle = new Bundle();
        bundle.putLong("ORDER_ID", orderid);
        assignOrderToDriverDialog.setArguments(bundle);
        assignOrderToDriverDialog.setCancelable(true);
        return assignOrderToDriverDialog;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onDestroyView() {
        if (driversRealmResultsAsync != null)
            driversRealmResultsAsync.removeAllChangeListeners();
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.driverlist_layout, container, false);
        realm = RealmManager.getLocalInstance();
        simpleDateFormat = new SimpleDateFormat(getString(R.string.current_date_format));
        driversRV = rootView.findViewById(R.id.driversRV);
        RecyclerView.LayoutManager linearLM = new LinearLayoutManager(getActivity());
        driversRV.setLayoutManager(linearLM);
        driversRV.setHasFixedSize(true);


        long orderId = getArguments().getLong("ORDER_ID");
        Purchase order = realm.where(Purchase.class).equalTo("id", orderId).findFirst();

        final DriverListAdapter.OnClickListener onClickListener = id -> {
            Log.e(TAG, "-----------driver clicked id: " + id);
            realm.executeTransaction(r -> {
                if (id != -1 && !order.getOrderType().equals(Constants.TYPE_TABLE)) {
                    order.setDriver(realm.where(Driver.class).equalTo("id", id).findFirst());
//                    callback.onClickDriver(id);
                }
            });

            dismiss();
        };

        driversRealmResultsAsync = realm.where(Driver.class).findAllAsync();
        final RealmList<Driver> driverRealmList = new RealmList<>();
        driversRealmResultsAsync.addChangeListener(results -> {
            driverRealmList.addAll(results);
            driverListAdapter = new DriverListAdapter(getActivity(), driverRealmList, order, onClickListener);
            driversRV.setAdapter(driverListAdapter);
        });
        orderType = order.getOrderType();

        closebtn = rootView.findViewById(R.id.close);
        closebtn.setOnClickListener(v -> {
            dismiss();
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

    /*@Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Callback)
            callback=(Callback)context;
        else
            throw new RuntimeException("context must implement AssignOrderToDriverDialog.Callback");
    }*/

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

    public interface Callback {
        void onClickDriver(long driverId);
    }
}
