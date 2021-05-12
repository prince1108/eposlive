package com.foodciti.foodcitipartener.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.dialogs.ConnectDialoge;
import com.foodciti.foodcitipartener.dialogs.CreateAddonDialoge;
import com.foodciti.foodcitipartener.dialogs.CustomAlertDialog;
import com.foodciti.foodcitipartener.dialogs.CustomFileDialog;
import com.foodciti.foodcitipartener.dialogs.FireBaseDialoge;
import com.foodciti.foodcitipartener.dialogs.PrinterDialoge;
import com.foodciti.foodcitipartener.utils.MenuBackupHelper;
import com.foodciti.foodcitipartener.utils.RealmBackupHelper;
import com.foodciti.foodcitipartener.utils.RealmManager;
import com.foodciti.foodcitipartener.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.reactivestreams.Subscription;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class SettingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "SettingsActivity";
    ListView listView;
    String whatUser = "";
    CheckBox all;
    ArrayList<String> selected_category = new ArrayList<>();
    private View upload, download;
    private RealmBackupHelper realmBackupHelper;
    private void upload() {
        if (realmBackupHelper != null) {
            boolean success = realmBackupHelper.backup();
            if (success) {
                Toast.makeText(this, "Backup Successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Backup Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void download() {
        if (realmBackupHelper != null)
            realmBackupHelper.restore();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        RealmManager.getRealmRefCount();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        try {
            realmBackupHelper = new RealmBackupHelper(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        findViewById(R.id.edit_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, EditMenu.class);
                intent.putExtra("ORDER_TYPE", getIntent().getStringExtra("ORDER_TYPE"));
                startActivity(intent);
            }
        });

        findViewById(R.id.systemsettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, SystemSettings.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.printersettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrinterDialoge dialoge = PrinterDialoge.newInstance();
                dialoge.show(getSupportFragmentManager(), "");
            }
        });

        findViewById(R.id.connectsettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectDialoge dialoge = ConnectDialoge.newInstance();
                dialoge.show(getSupportFragmentManager(), "");

            }
        });

        findViewById(R.id.addoncreat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAddonDialoge dialoge = CreateAddonDialoge.newInstance();
                dialoge.show(getSupportFragmentManager(), "");
            }
        });

        listView = findViewById(R.id.list_item);

        download = findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "----------------starting download------------------");
                CustomAlertDialog customAlertDialog = CustomAlertDialog.getInstance();
                customAlertDialog.setTitle("Restore Data");
                customAlertDialog.setMessage("Are you sure you want to restore data?");
                customAlertDialog.setPositiveButton("Yes", dialog -> {
                    Log.d(TAG, "restore: reached");
                    download();
                    dialog.dismiss();
                });
                customAlertDialog.setNegativeButton("No", dialog -> {
                    dialog.dismiss();
                });
                customAlertDialog.show(getSupportFragmentManager(), null);
            }
        });
        upload = findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "----------------starting upload------------------");
                CustomAlertDialog customAlertDialog = CustomAlertDialog.getInstance();
                customAlertDialog.setTitle("Backup Data");
                customAlertDialog.setMessage("Are you sure you want to backup data?");
                customAlertDialog.setPositiveButton("Yes", dialog -> {
                    upload();
                    dialog.dismiss();
                });
                customAlertDialog.setNegativeButton("No", dialog -> {
                    dialog.dismiss();
                });
                customAlertDialog.show(getSupportFragmentManager(), null);
            }
        });

//        dbHelper = new DBHelper(this);
        all = findViewById(R.id.all);
        all.setOnCheckedChangeListener(this);
        all.setChecked(false);
        // driverList


       /* listView.setAdapter(showDriverListAdapter);
        showDriverListAdapter.notifyDataSetChanged();*/


        findViewById(R.id.driver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*
                if(databaseHandler.getDriverCount()>0){
                    driverList=new ArrayList<>();
                    driverList=databaseHandler.getDriverData();
                    listView.setAdapter(showDriverListAdapter);
                    showDriverListAdapter.notifyDataSetChanged();
                }*/

            }
        });

/*        findViewById(R.id.print_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // startActivity(new Intent(SettingActivity.this,MenuManagementActivity.class));
                // finish();
//                allMenuData();
//                getMenuitemsData(SettingActivity.this);
                startActivity(new Intent(SettingActivity.this, EditPrintingOrder.class));
            }
        });*/

        findViewById(R.id.cancel_layou).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
//                finish();
            }
        });


        /*findViewById(R.id.download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClickAddress(View view) {
                //  allMenuData();
            }
        });*/

        findViewById(R.id.sendMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//            Intent intent=new Intent(SettingActivity.this,CustomerMessageList.class);
                Intent intent = new Intent(SettingActivity.this, SendMessageActivity.class);
                startActivity(intent);
            }
        });

       /* findViewById(R.id.download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClickAddress(View view) {
//                startActivity(new Intent(SettingActivity.this,RearrangeCategoryActivity.class));
                finish();
            }
        });*/

        /*findViewById(R.id.postal_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  Toast.makeText(SettingActivity.this,"Go All user",Toast.LENGTH_SHORT).show();
                allUserData();
            }
        });*/

        findViewById(R.id.billing).setOnClickListener(v -> {
            startActivity(new Intent(SettingActivity.this, Billing2.class));
        });

        findViewById(R.id.show_last_month).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                Toast.makeText(SettingActivity.this, date, Toast.LENGTH_LONG).show();

            }
        });

        /*findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClickAddress(View view) {
               *//* startActivity(new Intent(SettingActivity.this,RearrangeCategoryActivity.class));
                finish();*//*
            }
        });*/

        findViewById(R.id.Number_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                findViewById(R.id.orderhistory_layout).setVisibility(View.VISIBLE);

                whatUser = "allSeventy";
/*                if(databaseHandler.getUserListCount() > 0){
                    customer_list = new ArrayList<>();
                    customer_list = databaseHandler.getUserDetail();
                    customer_list_seven=new ArrayList<>();

                    for(int i=0;i<customer_list.size();i++){
                        String mobile=customer_list.get(i).getUser_telephone();
                        UserDetailsGetSet userDetailsGetSet=new UserDetailsGetSet();
                        userDetailsGetSet.setUser_register_date_time(customer_list.get(i).getUser_register_date_time());
                        userDetailsGetSet.setUser_name(customer_list.get(i).getUser_name());
                        userDetailsGetSet.setUser_address_id(customer_list.get(i).getUser_address_id());
                        userDetailsGetSet.setUser_house_name(customer_list.get(i).getUser_house_name());
                        userDetailsGetSet.setUser_telephone(customer_list.get(i).getUser_telephone());
                        userDetailsGetSet.setUser_postal_code(customer_list.get(i).getUser_postal_code());
                        if(mobile.startsWith("07")){
                            customer_list_seven.add(userDetailsGetSet);
                        }
                    }


                    listView.setAdapter(showCustomerListAdapter);
                    showCustomerListAdapter.notifyDataSetChanged();
                }*/

            }
        });

        findViewById(R.id.addDriver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this, ManageDriversActivity.class));
            }
        });

        findViewById(R.id.manage_users).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, ManageUsersActivity.class));
            }
        });

      /*  findViewById(R.id.loadMenu).setOnClickListener(v -> {
            CustomFileDialog customFileDialog = CustomFileDialog.newInstance();
            customFileDialog.setCallback((fileDialog, file) -> {
                Log.e(TAG, "--------------selected file: " + file);
                fileDialog.dismiss();
                menuBackupHelper = new MenuBackupHelper(this);
                menuBackupHelper.setParentFolder(file.getPath());
                menuBackupHelper.restore();
            });
            customFileDialog.show(getSupportFragmentManager(), null);
        });*/

        findViewById(R.id.edit_colors).setOnClickListener(v -> {
            startActivity(new Intent(SettingActivity.this, ManageColorsActivity.class));
        });

        findViewById(R.id.edit_postalinfo).setOnClickListener(v -> {
            startActivity(new Intent(SettingActivity.this, ManagePostalInfo.class));
        });

        findViewById(R.id.edit_vendorinfo).setOnClickListener(v -> {
            Intent i = new Intent(SettingActivity.this, VendorInfoActivity.class);
            i.putExtra(VendorInfoActivity.INTENT_ARG_EDITMODE, true);
            startActivity(i);
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

    }


    class ShowDriverListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
//            return driverList.size();
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            LayoutInflater layoutInflater = getLayoutInflater();
            final View progressView = layoutInflater.inflate(R.layout.driver_list_view, null);
//            final DriverGetSet driverGetSet=driverList.get(i);

            /*Random random = new Random();

            int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));

            progressView.setBackgroundColor(color);*/

//            progressView.setBackgroundColor(Integer.valueOf(driverGetSet.getColor()));
            TextView driver_name = progressView.findViewById(R.id.driver_name);
//            driver_name.setText(driverGetSet.getDriver_name());

            progressView.findViewById(R.id.delete_driver).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*ad=new AlertDialog.Builder(SettingActivity.this)
                            .setTitle("Alert?")
                            .setMessage("Are you sure want to delete "+driverGetSet.getDriver_name()+" from Driver list ?")
                            .setPositiveButton("Yes",

                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClickAddress(DialogInterface dialog, int which) {
                                       driverList.remove(i);
                                       notifyDataSetChanged();
                                       databaseHandler.deleteDriver(driverGetSet);
                                        }
                                    })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClickAddress(DialogInterface dialog, int which) {
                                    //Toast.makeText(MainActivity.this, "Phone switch off ho rha hai..!", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                            }).create();

                    ad.show();*/

                }
            });

            return progressView;
        }
    }

    class ShowCustomerListAdapter extends BaseAdapter {

        @Override
        public int getCount() {

            /*if(whatUser.equalsIgnoreCase("all")){
                return customer_list.size();
            }else if(whatUser.equalsIgnoreCase("allSeventy")){
                return customer_list_seven.size();
            }
            return customer_list_seven.size();*/
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater layoutInflater = getLayoutInflater();
            final View progressView = layoutInflater.inflate(R.layout.customer_item_list, null);


           /* UserDetailsGetSet userDetailsGetSet = null;

            if(whatUser.equalsIgnoreCase("all")){
                userDetailsGetSet=customer_list.get(i);
            }else if(whatUser.equalsIgnoreCase("allSeventy")){
                userDetailsGetSet=customer_list_seven.get(i);
            }*/


            TextView customer_name = progressView.findViewById(R.id.customer_name);
            final TextView customer_mobile = progressView.findViewById(R.id.customer_mobile);
            //  TextView msg_send = progressView.findViewById(R.id.msg_send);

            CheckBox checkBox = progressView.findViewById(R.id.checkbox);
            // if(!userDetailsGetSet.getUser_postal_code().equalsIgnoreCase("")){
            //   customer_name.setVisibility(View.VISIBLE);
//                customer_name.setText(userDetailsGetSet.getUser_house_name()+" , "+userDetailsGetSet.getUser_postal_code());

            //
            //  }
//            customer_mobile.setText(userDetailsGetSet.getUser_telephone());

//            final UserDetailsGetSet finalUserDetailsGetSet = userDetailsGetSet;
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    // artCatGetSet=int_search_result_list.get(position);
                  /*  if (isChecked) {
                        if (!selected_category.contains(finalUserDetailsGetSet.getUser_telephone())) {
                            selected_category.add(finalUserDetailsGetSet.getUser_telephone());
                        }
                    } else {
                        selected_category.remove(finalUserDetailsGetSet.getUser_telephone());
                    }*/
                }
            });

            /*if (selected_category.contains(finalUserDetailsGetSet.getUser_telephone())) {
                checkBox.setChecked(true);

            } else {
                checkBox.setChecked(false);
            }*/


            findViewById(R.id.orderhistory_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String numbers = "";
                    if (selected_category.size() > 0) {
                        for (int i = 0; i < selected_category.size(); i++) {
                            if (i == 0) {
                                numbers = selected_category.get(i);
                            } else {
                                numbers = numbers + "," + selected_category.get(i);
                            }
                        }
                       /* OfferMessageDialog addition = OfferMessageDialog.getInstance(numbers);
                        addition.setCancelable(false);
                        addition.show(getSupportFragmentManager(), null);*/
                    }

                }
            });

            return progressView;
        }
    }

    private void allUserData() {
      /*  if (InternetConnection.checkConnection(SettingActivity.this)) {
            String url = AppConfig.Url + "getPostalData";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                                 Toast.makeText(SettingActivity.this, response, Toast.LENGTH_SHORT).show();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                parseJSONAllUserData1(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

            //Adding the string request to the queue
            if(dash_requests==null){
                dash_requests=Volley.newRequestQueue(SettingActivity.this);
                dash_requests.getCache().clear();
                DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 16 * 1024 * 1024);
                dash_requests = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
                dash_requests.start();
                dash_requests.add(stringRequest);
            }else{
                dash_requests.getCache().clear();
                dash_requests.add(stringRequest);
            }

        }*/

    }

    private void parseJSONAllUserData1(JSONObject jsonSuggest) throws JSONException {

        if (jsonSuggest.getString("status").equalsIgnoreCase("1")) {
            JSONArray jsonArray = jsonSuggest.getJSONArray("details");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
              /*  if(databaseHandler.checkPostalCode(jsonObject.getString("postal_id"))==0){

                    databaseHandler.addPostalCodeServer(new PostalCodeGetSet(
                            jsonObject.getString("postal_id"),
                            jsonObject.getString("postal_code"),
                            jsonObject.getString("postal_address"),
                            jsonObject.getString("postal_lane"),
                            "0"));
                }*/
            }

            allCustomerUserData();
        }

        // startActivity(new Intent(SettingActivity.this, MainActivity.class));


    }


    private void allMenuData() {
        /*if (InternetConnection.checkConnection(SettingActivity.this)) {
            String url = AppConfig.Url + "getItemsDataFromServer";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //     Toast.makeText(DashBoardActivity.this,response.toString(),Toast.LENGTH_SHORT).show();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                parseJSONAllUserData(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

            //Adding the string request to the queue
            if(dash_requests==null){
                dash_requests=Volley.newRequestQueue(SettingActivity.this);
                dash_requests.getCache().clear();
                DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 16 * 1024 * 1024);
                dash_requests = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
                dash_requests.start();
                dash_requests.add(stringRequest);
            }else{
                dash_requests.getCache().clear();
                dash_requests.add(stringRequest);
            }

        }*/

    }

    private void parseJSONAllUserData(JSONObject jsonSuggest) throws JSONException {
/*
        if(jsonSuggest.getString("status").equalsIgnoreCase("1"))
        {
            JSONArray jsonArray = jsonSuggest.getJSONArray("category");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(dbHelper.checkCatIdExist(jsonObject.getString("cat_id"))==0){

                    Category category = new Category();
                    category.setCategoryId(jsonObject.getString("cat_id"));
                    category.setCategoryName(jsonObject.getString("cat_name"));
                    category.setCategoryFoodCitiId(jsonObject.getString("cat_foodciti_id"));
                    dbHelper.insertServerCategory(category);
                }

            }
            JSONArray jsonArray2 = jsonSuggest.getJSONArray("items_Addon");
            for (int i = 0; i < jsonArray2.length(); i++) {
                JSONObject jsonObject = jsonArray2.getJSONObject(i);
                if(dbHelper.checkAddonIdExist(jsonObject.getString("subitemId"))==0){



                    SubItems subCategory = new SubItems();
                    subCategory.setSubitemId(Integer.parseInt(jsonObject.getString("subitemId")));
                    subCategory.setSubitemName(jsonObject.getString("subitemName"));
                    subCategory.setSubitemPrice(jsonObject.getString("subitemPrice"));
                    //subCategory.setSubitemColor(Integer.parseInt(jsonObject.getString("subitemColor")));
                   // subCategory.setSubitemColor(Integer.parseInt(jsonObject.getString("subitemColor")));
                    subCategory.setSubitemColor(1);

                    //subCategory.setProductCatId(catId);
                    dbHelper.insertServerAddon(subCategory);
                }


            }

            JSONArray jsonArray3 = jsonSuggest.getJSONArray("item_NoAddon");
            for (int i = 0; i < jsonArray3.length(); i++) {
                JSONObject jsonObject = jsonArray3.getJSONObject(i);
                if(dbHelper.checkNoAddonIdExist(jsonObject.getString("id"))==0){



                    AdditionalItems additionalItems = new AdditionalItems();
                    //additionalItems.setAddditionId(jsonObject.getString("id"));
                    additionalItems.setAdditionalitemname(jsonObject.getString("noaddon_item_name"));
                    additionalItems.setAdditionaloitemPrice(jsonObject.getString("noaddon_item_price"));

                    //subCategory.setProductCatId(catId);
                    dbHelper.insertNoAddon(additionalItems);
                }


            }

            getMenuitemsData(this);

//            allCustomerUserData();
        }*/

        Toast.makeText(SettingActivity.this, "Menu Downloaded", Toast.LENGTH_LONG).show();


    }

    public void getMenuitemsData(Context context) {
        /*if (InternetConnection.checkConnection(context)) {
            String url = "https://devapi.foodciti.co.uk/restaurents/info";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //     Toast.makeText(DashBoardActivity.this,response.toString(),Toast.LENGTH_SHORT).show();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                parseJSONAllMenuItemsData(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

            //Adding the string request to the queue
            if(dash_requests==null){
                dash_requests=Volley.newRequestQueue(context);
                dash_requests.getCache().clear();
                DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 16 * 1024 * 1024);
                dash_requests = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
                dash_requests.start();
                dash_requests.add(stringRequest);
            }else{
                dash_requests.getCache().clear();
                dash_requests.add(stringRequest);
            }

        }*/
    }

    private void parseJSONAllMenuItemsData(JSONObject jsonSuggest) throws JSONException {
  /*      if(jsonSuggest.getString("status").equalsIgnoreCase("200")){
            JSONArray jsonArray = jsonSuggest.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                if (i == 6) {


                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    JSONArray jsonArray1 = jsonObject.getJSONArray("item_list");

                    for (int j = 0; j < jsonArray1.length(); j++) {

                        JSONObject jsonObject1 = jsonArray1.getJSONObject(j);

                        String value=jsonObject1.getString("item_category_id");
                        CategoryGet categoryGet=new CategoryGet();
                        categoryGet = dbHelper.getCategoriesSingleId(value);

                        String value1=categoryGet.getId();
                        SubCategory subCategory = new SubCategory();
                        subCategory.setSubCategoryName(jsonObject1.getString("item_name"));
                        subCategory.setPrice(jsonObject1.getString("item_price"));
                        subCategory.setSubCategoryId(categoryGet.getId());
                        dbHelper.insertSubCategory(subCategory);

                    }

                }

                startActivity(new Intent(SettingActivity.this,MainActivity.class));
            }}*/
    }

    private void allCustomerUserData() {
 /*       if (InternetConnection.checkConnection(SettingActivity.this)) {
            String url = AppConfig.Url + "getCustomerData";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //     Toast.makeText(DashBoardActivity.this,response.toString(),Toast.LENGTH_SHORT).show();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                parseJSONAllCustomerUserData(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

            //Adding the string request to the queue
            if(dash_requests==null){
                dash_requests=Volley.newRequestQueue(SettingActivity.this);
                dash_requests.getCache().clear();
                DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 16 * 1024 * 1024);
                dash_requests = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
                dash_requests.start();
                dash_requests.add(stringRequest);
            }else{
                dash_requests.getCache().clear();
                dash_requests.add(stringRequest);
            }

        }*/

    }

    private void parseJSONAllCustomerUserData(JSONObject jsonSuggest) throws JSONException {

/*        if (jsonSuggest.getString("status").equalsIgnoreCase("1")) {

            JSONArray jsonArray = jsonSuggest.getJSONArray("details");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                    if (databaseHandler.checkMobileNumber(jsonObject.getString("user_telephone")) == 0) {

                            String postalid=jsonObject.getString("user_postal_code");
                            PostalCodeGetSet postalCodeGetSet=databaseHandler.getSinglePostalCodebyId(postalid);

                            databaseHandler.addUserDetail(new UserDetailsGetSet(
                                    jsonObject.getString("user_postal_code"),
                                    jsonObject.getString("user_telephone"),postalCodeGetSet.getPostal_lane(),

                                    jsonObject.getString("user_house_name"),postalCodeGetSet.getPostal_address(),
                                    jsonObject.getString("user_register_date_time"),
                                    "0",
                                    "NORMAL",
                                    jsonObject.getString("remark_status")));
                }




            }
            startActivity(new Intent(SettingActivity.this, MainActivity.class));


        }*/




     /*   private void uploadUserData ( final String user_postal_code, final String user_telephone,
        final String user_house_name, final String user_address_id){
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

            final String url = AppConfig.Url + "addUserData";
            StringRequest stringRequest1 = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Toast.makeText(MerchantOfferList.this,response.toString(),Toast.LENGTH_SHORT).show();
                            //If we are getting success from server
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                //  parseMerchantsLocality(jsonArray);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want

                            // pd.dismiss();
                            // Toast.makeText(MerchantOfferList.this,error.toString(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(RearrangeCategoryActivity.this, "Server Error.. Try Again", Toast.LENGTH_SHORT).show();
                            //  Snackbar.make(parent_layout, "Server Error.. Try Again", Snackbar.LENGTH_SHORT).show();

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    //Adding parameters to request

                    params.put("q", "addUserData");
                    params.put("userPostalCode", user_postal_code);  //for google and fb it will be email
                    params.put("userTelephone", user_telephone);
                    params.put("userName", user_house_name);
                    params.put("userHouse", user_address_id);

                    //returning parameter
                    return params;
                }
            };
            stringRequest1.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            //Adding the string request to the queue
            if (dataRequest == null) {
                dataRequest = Volley.newRequestQueue(this);
                dataRequest.getCache().clear();
                DiskBasedCache cache = new DiskBasedCache(getCacheDir(), 16 * 1024 * 1024);
                dataRequest = new RequestQueue(cache, new BasicNetwork(new HurlStack()));
                dataRequest.start();
                stringRequest1.setShouldCache(false);
                dataRequest.add(stringRequest1);

            } else {
                dataRequest.getCache().clear();
                stringRequest1.setShouldCache(false);
                dataRequest.add(stringRequest1);


            }

        }
    */
    }


}
