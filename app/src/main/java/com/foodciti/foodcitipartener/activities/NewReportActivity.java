package com.foodciti.foodcitipartener.activities;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.fragments.CustomerReportsFragment;
import com.foodciti.foodcitipartener.fragments.DriverReportFragment;
import com.foodciti.foodcitipartener.fragments.NewSalesReportFragment2;
import com.foodciti.foodcitipartener.fragments.SalesReportFragment;
import com.foodciti.foodcitipartener.utils.PrintHelper;
import com.google.android.material.tabs.TabLayout;



public class NewReportActivity extends AppCompatActivity implements SalesReportFragment.OnFragmentInteractionListener, PrintHelper.PrintHelperCallback,NewSalesReportFragment2.OnEodRun {
    private static final String TAG = "NewReportActivity";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabsAdapter pagerAdapter;


    @Override
    protected void onDestroy() {
//        RealmManager.closeRealmFor(this);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_new_report);
        initViews();
    }

    private void initViews() {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        pagerAdapter = new TabsAdapter(getSupportFragmentManager(), 3);
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onDataReceived(byte[] buffer, int size) {
        Log.e(TAG, "-----printData: " + new String(buffer));
    }

    @Override
    public void onEodRun() {
        initViews();
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
                    NewSalesReportFragment2 reportFragment = NewSalesReportFragment2.newInstance();
                    return reportFragment;
                /*case 1:
                    AboutFragment about = new AboutFragment();
                    return about;
                case 2:
                    ContactFragment contact = new ContactFragment();
                    return contact;*/
                case 1:
                    DriverReportFragment driverReportFragment = DriverReportFragment.newInstance();
                    return driverReportFragment;
                case 2:
                    CustomerReportsFragment customerReportsFragment = CustomerReportsFragment.newInstance();
                    return customerReportsFragment;
                default:
                    return null;
            }
        }
    }
}
