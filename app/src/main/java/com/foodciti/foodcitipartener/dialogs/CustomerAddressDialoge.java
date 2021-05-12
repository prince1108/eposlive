package com.foodciti.foodcitipartener.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.AddressAdapter;
import com.foodciti.foodcitipartener.adapters.AddressPostalCodeAdapter;
import com.foodciti.foodcitipartener.adapters.AllTablesAdapter;
import com.foodciti.foodcitipartener.realm_entities.PostalInfo;
import com.foodciti.foodcitipartener.realm_entities.Table;
import com.foodciti.foodcitipartener.utils.RealmManager;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import android.app.Dialog;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.Window;
        import android.widget.Spinner;

        import androidx.fragment.app.DialogFragment;
        import androidx.recyclerview.widget.GridLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import com.foodciti.foodcitipartener.R;
        import com.foodciti.foodcitipartener.adapters.AllTablesAdapter;
        import com.foodciti.foodcitipartener.realm_entities.Table;
        import com.foodciti.foodcitipartener.utils.RealmManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import io.realm.Realm;
        import io.realm.RealmResults;

public class CustomerAddressDialoge extends DialogFragment {
    private static final String TAG = "TablesFullViewDialog";

    private Realm realm;
    private RecyclerView allTablesRV;

    private Callback callback;
    public void setCallback(Callback callback) {
        this.callback = callback;
    }
    private String postCodeStr="";
    private CustomerAddressDialoge(){}

    public static CustomerAddressDialoge newInstance() {
        CustomerAddressDialoge tablesFullViewDialog = new CustomerAddressDialoge();
        tablesFullViewDialog.setCancelable(true);
        return tablesFullViewDialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_all_tables, container,
                false);
        realm = Realm.getDefaultInstance();
        allTablesRV = rootView.findViewById(R.id.allTablesRV);
        RealmResults<PostalInfo> dynamicSearchResults = realm.where(PostalInfo.class).contains("A_PostCode", postCodeStr.trim()).findAll();
        allTablesRV.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        AddressPostalCodeAdapter allTablesAdapter = new AddressPostalCodeAdapter(getActivity(),dynamicSearchResults);
        allTablesAdapter.setCallback((houseNo) -> {
            if (callback != null)
                callback.onSelect(CustomerAddressDialoge.this,houseNo);
        });
        allTablesRV.setAdapter(allTablesAdapter);

        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (getArguments() != null) {
            postCodeStr = getArguments().getString("PostCode");
        }
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.7);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.7);

            dialog.getWindow().setLayout(width, height);
        }
    }

    public interface Callback {
        void onSelect(CustomerAddressDialoge dialog, String address);
    }
}

