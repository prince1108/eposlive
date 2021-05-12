package com.foodciti.foodcitipartener.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.foodciti.foodcitipartener.R;

import static com.foodciti.foodcitipartener.utils.AppConfig.MyPREFERENCES;

public class SystemSettings extends AppCompatActivity {

    RadioGroup rg1,rg2,rg3,rg4,rg5;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_settings);
        ImageView close;
        close = findViewById(R.id.close);

        rg1 = findViewById(R.id.cashnotpaidradio);
        rg2 = findViewById(R.id.cashpaidradio);
        rg3 = findViewById(R.id.cardnotpaidradio);
        rg4 = findViewById(R.id.cardpaidradio);
        rg5 = findViewById(R.id.itemmenuRadio);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        int cashnotpaid = sharedPreferences.getInt("cashnotpaid",R.id.cashnottrue);
        int cashpaid = sharedPreferences.getInt("cashpaid",R.id.cashtrue);
        int cardnotpaid = sharedPreferences.getInt("cardnotpaid",R.id.cardnottrue);
        int cardpaid = sharedPreferences.getInt("cardpaid",R.id.cardtrue);
        int itemSet = sharedPreferences.getInt("ItemSetting",3);
        rg1.check(cashnotpaid);
        rg2.check(cashpaid);
        rg3.check(cardnotpaid);
        rg4.check(cardpaid);
        if(itemSet==1)
        rg5.check(R.id.itemmenuRadio1);
        if(itemSet==2)
            rg5.check(R.id.itemmenuRadio2);
        if(itemSet==3)
            rg5.check(R.id.itemmenuRadio3);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putInt("cashnotpaid",rg1.getCheckedRadioButtonId());
                editor.putInt("cashpaid",rg2.getCheckedRadioButtonId());
                editor.putInt("cardnotpaid",rg3.getCheckedRadioButtonId());
                editor.putInt("cardpaid",rg4.getCheckedRadioButtonId());
                editor.putInt("ItemSetting",rg5.getCheckedRadioButtonId());
                if(rg1.getCheckedRadioButtonId() == R.id.cashnottrue){
                    editor.putBoolean("cashnotpaid_val",true);
                }
                else{
                    editor.putBoolean("cashnotpaid_val",false);
                }
                if(rg2.getCheckedRadioButtonId() == R.id.cashtrue){
                    editor.putBoolean("cashpaid_val",true);
                }
                else{
                    editor.putBoolean("cashpaid_val",false);
                }
                if(rg3.getCheckedRadioButtonId() == R.id.cardnottrue){
                    editor.putBoolean("cardnotpaid_val",true);
                }
                else{
                    editor.putBoolean("cardnotpaid_val",false);
                }
                if(rg4.getCheckedRadioButtonId() == R.id.cardtrue){
                    editor.putBoolean("cardpaid_val",true);
                }
                else{
                    editor.putBoolean("cardpaid_val",false);
                }
                if(rg5.getCheckedRadioButtonId() == R.id.itemmenuRadio1){
                    editor.putInt("ItemSetting",1);
                }else if(rg5.getCheckedRadioButtonId() == R.id.itemmenuRadio2){
                    editor.putInt("ItemSetting",2);
                }
                else{
                    editor.putInt("ItemSetting",3);
                }

                editor.commit();
                onBackPressed();
            }
        });
    }
}