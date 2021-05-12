package com.foodciti.foodcitipartener.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.AddressAdapter;
import com.foodciti.foodcitipartener.dialogs.CustomAlertDialog;
import com.foodciti.foodcitipartener.fragments.CustomerReportsFragment;
import com.foodciti.foodcitipartener.fragments.DriverReportFragment;
import com.foodciti.foodcitipartener.fragments.EditPostCode;
import com.foodciti.foodcitipartener.fragments.NewSalesReportFragment2;
import com.foodciti.foodcitipartener.fragments.SelectPostcodes;
import com.foodciti.foodcitipartener.gson.PostalData;
import com.foodciti.foodcitipartener.realm_entities.PostalInfo;
import com.foodciti.foodcitipartener.utils.DataInitializers;
import com.foodciti.foodcitipartener.utils.RealmManager;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class ManagePostalInfo extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabsAdapter pagerAdapter;
    private ArrayList<PostalData> postalData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_manage_postal_info);
        initViews();
        postalData = initPostalCodes();
    }

    private void initViews() {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        pagerAdapter = new TabsAdapter(getSupportFragmentManager(), 2);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        findViewById(R.id.close).setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private ArrayList<PostalData> initPostalCodes() {
        InputStream ins = getResources().openRawResource(
                getResources().getIdentifier("generic_postcode",
                        "raw", getPackageName()));
        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(new InputStreamReader(ins));

        final Type POSTALINFO_TYPE = new TypeToken<ArrayList<PostalData>>() {
        }.getType();
        ArrayList<PostalData> postalInfos = gson.fromJson(jsonReader, POSTALINFO_TYPE);
//        DataInitializers.initPostalInfo(this, postalInfos);
        return postalInfos;
    }

    public class TabsAdapter extends FragmentPagerAdapter {
        int mNumOfTabs;

        public TabsAdapter(FragmentManager fm, int NoofTabs) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.mNumOfTabs = NoofTabs;
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
//                    SalesReportFragment reportFragment = SalesReportFragment.newInstance();
                    Log.d("MPA", postalData.toString());
                    SelectPostcodes selectPostcodes = SelectPostcodes.newInstance(postalData);
                    return selectPostcodes;
                /*case 1:
                    AboutFragment about = new AboutFragment();
                    return about;
                case 2:
                    ContactFragment contact = new ContactFragment();
                    return contact;*/
                case 1:
                    EditPostCode editPostCode = EditPostCode.newInstance();
                    return editPostCode;
                default:
                    return null;
            }
        }
    }
}
