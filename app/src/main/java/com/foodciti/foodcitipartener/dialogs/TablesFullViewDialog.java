package com.foodciti.foodcitipartener.dialogs;

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

import io.realm.Realm;
import io.realm.RealmResults;

public class TablesFullViewDialog extends DialogFragment {
    private static final String TAG = "TablesFullViewDialog";

    private RecyclerView warningRV;
    private Spinner spinner;
    private String type = "All";

    private RecyclerView allTablesRV;

    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    private TablesFullViewDialog(){}

    public static TablesFullViewDialog newInstance() {
        TablesFullViewDialog tablesFullViewDialog = new TablesFullViewDialog();
        tablesFullViewDialog.setCancelable(true);
        return tablesFullViewDialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_all_tables, container,
                false);

        Realm realm = RealmManager.getLocalInstance();

        allTablesRV = rootView.findViewById(R.id.allTablesRV);
        allTablesRV.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        RealmResults<Table> tables = realm.where(Table.class).findAll();
        AllTablesAdapter allTablesAdapter = new AllTablesAdapter(getActivity(), tables);
        allTablesAdapter.setCallback((position, tableId) -> {
            if (callback != null)
                callback.onSelect(TablesFullViewDialog.this, position, tableId);
        });
        allTablesRV.setAdapter(allTablesAdapter);

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
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.7);

            dialog.getWindow().setLayout(width, height);
        }
    }

    public interface Callback {
        void onSelect(TablesFullViewDialog dialog, int position, long tableId);
    }
}
