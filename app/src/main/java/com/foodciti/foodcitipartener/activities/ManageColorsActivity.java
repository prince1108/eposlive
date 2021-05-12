package com.foodciti.foodcitipartener.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.ManageColorAdapter;
import com.foodciti.foodcitipartener.dialogs.ColorDialog;
import com.foodciti.foodcitipartener.realm_entities.ItemColor;
import com.foodciti.foodcitipartener.utils.RealmManager;

import io.realm.Realm;
import io.realm.RealmResults;

public class ManageColorsActivity extends AppCompatActivity {
    private static final String TAG = "ManageColorsActivity";
    private RecyclerView colorRV;
    private View addColor, close;
    private ManageColorAdapter manageColorAdapter = null;
    private RealmResults<ItemColor> itemColorRealmResultsAsync = null;
    private Realm realm;

    @Override
    protected void onResume() {
        super.onResume();
//        realm = RealmManager.getRealmFor(getApplicationContext());
    }

    @Override
    protected void onPause() {
        if (itemColorRealmResultsAsync != null)
            itemColorRealmResultsAsync.removeAllChangeListeners();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
//        RealmManager.closeRealmFor(this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_colors);
        realm = RealmManager.getLocalInstance();
        initViews();
        setColors();
    }

    private void setColors() {
        itemColorRealmResultsAsync = realm.where(ItemColor.class).findAllAsync();
        itemColorRealmResultsAsync.addChangeListener(results -> {
            manageColorAdapter = new ManageColorAdapter(this, results);
            colorRV.setAdapter(manageColorAdapter);
        });
    }

    private void initViews() {
        colorRV = findViewById(R.id.colorRV);
        colorRV.setLayoutManager(new LinearLayoutManager(this));

        addColor = findViewById(R.id.addColor);
        addColor.setOnClickListener(v -> {
            ColorDialog colorDialog = ColorDialog.newInstance();
            colorDialog.setCallback(dialog -> {
                manageColorAdapter.notifyItemInserted(manageColorAdapter.getItemCount() - 1);
                dialog.dismiss();
            });

            colorDialog.show(getSupportFragmentManager(), null);
        });

        close = findViewById(R.id.close);
        close.setOnClickListener(v -> {
            onBackPressed();
        });

    }
}
