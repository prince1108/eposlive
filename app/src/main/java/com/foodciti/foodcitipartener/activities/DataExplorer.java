package com.foodciti.foodcitipartener.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.util.Set;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class DataExplorer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_explorer);

        DynamicRealm realm = DynamicRealm.getInstance(Realm.getDefaultConfiguration());
        realm.executeTransaction(r->{
            RealmSchema realmSchema = r.getSchema();
            if(realmSchema.contains("Vendor"))
                realmSchema.remove("Vendor");
            Set<RealmObjectSchema> realmObjectSchemaSet = realmSchema.getAll();
            for(RealmObjectSchema objectSchema: realmObjectSchemaSet) {
                Log.d("DataExplorer", "---------------------------"+objectSchema.getClassName());
            }
        });

        realm.close();
    }
}
