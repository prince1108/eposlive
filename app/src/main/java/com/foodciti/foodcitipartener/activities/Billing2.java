package com.foodciti.foodcitipartener.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.fragments.CollectionBillingFragment;
import com.foodciti.foodcitipartener.fragments.DeliveryBillingFragment;
import com.foodciti.foodcitipartener.fragments.TableBillingFragment;
import com.google.android.material.tabs.TabLayout;

public class Billing2 extends AppCompatActivity {
    private static final String TAG = "Billing";
    private TabLayout tabLayout;
    private TabsAdapter pagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing2);
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
                    CollectionBillingFragment collectionBilling = CollectionBillingFragment.newInstance(null, null);
                    return collectionBilling;
                case 1:
                    DeliveryBillingFragment deliveryBilling = DeliveryBillingFragment.newInstance(null, null);
                    return deliveryBilling;
                case 2:
                    TableBillingFragment tableBilling = TableBillingFragment.newInstance(null, null);
                    return tableBilling;
                default:
                    return null;
            }
        }
    }
}
