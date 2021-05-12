package com.foodciti.foodcitipartener.activities;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.adapters.DriverListAdapter;
import com.foodciti.foodcitipartener.adapters.ManageOrdersAdapter;
import com.foodciti.foodcitipartener.dialogs.AssignOrderToDriverDialog;
import com.foodciti.foodcitipartener.dialogs.OrderHistoryDetailsDialog;
import com.foodciti.foodcitipartener.realm_entities.Driver;
import com.foodciti.foodcitipartener.realm_entities.Purchase;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.Preferences;
import com.foodciti.foodcitipartener.utils.PrintHelper;
import com.foodciti.foodcitipartener.utils.RealmManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.foodciti.foodcitipartener.utils.AppConfig.MyPREFERENCES;

public class ManageOrdersActivity extends AppCompatActivity implements ManageOrdersAdapter.Callback, DriverListAdapter.OnClickListener
        , PrintHelper.PrintHelperCallback {
    private static final String TAG = "ManageOrdersActivity";

    private Realm realm;
    private EditText startDate, endDate;
    private Button search, exportCsv;
    private RadioGroup reportTypeRG;
    private RadioButton dailyReportRB, weeklyReportRB, monthlyReportRB;
    private Date mDate1, mDate2;
    final Calendar myCalendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat;

//    private RealmResults<Order> orderRealmResultsAsync = null;
    private RealmResults<Purchase> orderRealmResultsAsync = null;
    private RealmResults<Driver> driverRealmResultsAsync = null;
    private Purchase currentOrder = null;
//    private RealmResults<Order>

    private RecyclerView ordersRV, driverViewRV;
//    private ManageOrdersAdapter manageOrdersAdapter;
    private ManageOrdersAdapter manageOrdersAdapter;
    private DriverListAdapter driverListAdapter;
    protected SharedPreferences sharedPreferences;
    protected SharedPreferences.Editor editor;

    final String myFormat = "MM/dd/yy"; //In which you need put here
    final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

    final DatePickerDialog.OnDateSetListener dateSetListener1 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(java.util.Calendar.YEAR, year);
            myCalendar.set(java.util.Calendar.MONTH, monthOfYear);
            myCalendar.set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth);
            myCalendar.set(java.util.Calendar.HOUR_OF_DAY, 23);
            myCalendar.set(java.util.Calendar.MINUTE, 59);
            mDate1 = myCalendar.getTime();
            updateLabel(startDate);
        }

    };
    final DatePickerDialog.OnDateSetListener dateSetListener2 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(java.util.Calendar.YEAR, year);
            myCalendar.set(java.util.Calendar.MONTH, monthOfYear);
            myCalendar.set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth);
            myCalendar.set(java.util.Calendar.HOUR_OF_DAY, 23);
            myCalendar.set(java.util.Calendar.MINUTE, 59);
            mDate2 = myCalendar.getTime();
            updateLabel(endDate);
        }

    };

    private void updateLabel(EditText dateField) {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateField.setText(sdf.format(myCalendar.getTime()));
        if (!startDate.getText().toString().trim().isEmpty())
            search.setEnabled(true);
        /*if (sales_between_date.isChecked()) {
            if(!date1.getText().toString().trim().isEmpty() && !date2.getText().toString().trim().isEmpty())
                go.setEnabled(true);
        }*/
    }

    private void removeChangeListenersOnAsyncResults() {
        if (orderRealmResultsAsync != null)
            orderRealmResultsAsync.removeAllChangeListeners();
        if (driverRealmResultsAsync != null)
            driverRealmResultsAsync.removeAllChangeListeners();
    }

    @Override
    protected void onPause() {
        removeChangeListenersOnAsyncResults();
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_orders);
        realm = RealmManager.getLocalInstance();
        ordersRV = findViewById(R.id.ordersRV);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
        ordersRV.setLayoutManager(lm);
        sharedPreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        editor = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
        manageOrdersAdapter = new ManageOrdersAdapter(this, null,sharedPreferences.getLong(Preferences.LAST_EOD_TIME,1));
        ordersRV.setAdapter(manageOrdersAdapter);

        driverViewRV = findViewById(R.id.driverViewRV);
        RecyclerView.LayoutManager driverViewLM = new LinearLayoutManager(this);
        driverViewRV.setLayoutManager(driverViewLM);
        /*orderRealmResultsAsync = realm.where(Order.class).findAllAsync();
        orderRealmResultsAsync.addChangeListener(rs -> {
            manageOrdersAdapter.setOrders(rs);
            manageOrdersAdapter.notifyDataSetChanged();
        });*/

        findViewById(R.id.close).setOnClickListener(v -> {
            onBackPressed();
        });

        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
//        orderNo = findViewById(R.id.orderNo);

        reportTypeRG = findViewById(R.id.charges_type_group);
        dailyReportRB = findViewById(R.id.daily);
        reportTypeRG.setOnCheckedChangeListener((group, checkedId) -> {
            getSales(checkedId);
        });

        search = findViewById(R.id.search);

        search.setOnClickListener(v -> {
            ordersRV.setAdapter(null);
//            RealmQuery<Order> realmQuery = realm.where(Order.class);
            RealmQuery<Purchase> realmQuery = realm.where(Purchase.class);
            if (mDate1 != null) {

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(mDate1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 1);
                Date start = calendar.getTime();

                if (mDate2 == null)
                    mDate2 = calendar.getTime();
                calendar.setTime(mDate2);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                Date end = calendar.getTime();

                realmQuery.between("timestamp", start.getTime(), end.getTime());
            }

            /*Long ordernum = null;
            try {
                ordernum = Long.parseLong(orderNo.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (ordernum != null)
                realmQuery.and().equalTo("id", ordernum.longValue());*/

            orderRealmResultsAsync = realmQuery.findAllAsync();
            orderRealmResultsAsync.addChangeListener(rs -> {
                ordersRV.setAdapter(new ManageOrdersAdapter(ManageOrdersActivity.this, rs,sharedPreferences.getLong(Preferences.LAST_EOD_TIME,1)));
//                RecyclerViewItemSelectionAfterLayoutUpdate.on(ordersRV);
            });
        });

        startDate.setOnClickListener(v -> {
            new DatePickerDialog(ManageOrdersActivity.this, dateSetListener1, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        endDate.setOnClickListener(v -> {
            new DatePickerDialog(ManageOrdersActivity.this, dateSetListener2, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

//        dailyReportRB.setChecked(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reportTypeRG.clearCheck();
        dailyReportRB.setChecked(true);
    }

    @Override
    public void onClickDetails(long id) {
        OrderHistoryDetailsDialog addition = OrderHistoryDetailsDialog.getInstance(id);
        addition.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onLongPress(long id) {
        Purchase order = realm.where(Purchase.class).equalTo("id", id).findFirst();
        if (order.getOrderType().equals(Constants.TYPE_TABLE)) {
            Toast.makeText(this, "Can't assign driver to order type Table", Toast.LENGTH_SHORT).show();
            return;
        }
        AssignOrderToDriverDialog assignOrderToDriverDialog = AssignOrderToDriverDialog.getInstance(id);
        assignOrderToDriverDialog.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onClickDelivererdBy(long orderId) {
        Log.e(TAG, "------------current orderId: " + orderId);
        currentOrder = realm.where(Purchase.class).equalTo("id", orderId).findFirst();
        driverRealmResultsAsync = realm.where(Driver.class).equalTo("isAvailable", true).and().equalTo("enabled", true).findAllAsync();
        driverRealmResultsAsync.addChangeListener(rs -> {
            Log.e(TAG, "------result count: " + rs.size());
            driverListAdapter = new DriverListAdapter(ManageOrdersActivity.this, rs, currentOrder);
            driverViewRV.setAdapter(driverListAdapter);
        });
    }

    @Override
    public void onClickStatusSpinner(long purchaseId, int itemPosition, boolean paid) {
        Log.d(TAG, "---------------------onClickStatusSpinner---------------------------");
        Purchase purchase = realm.where(Purchase.class).equalTo("id", purchaseId).findFirst();
        if(purchase!=null) {
            realm.executeTransaction(r->{
                purchase.setPaid(paid);
//                manageOrdersAdapter.notifyItemChanged(itemPosition);
            });
        }
    }

    @Override
    public void onDataReceived(byte[] buffer, int size) {
        runOnUiThread(() -> {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < size; i++) {
                String s = Integer.toHexString((int) buffer[i]);//String.valueOf(((char)buffer[i]));
                sb.append(s + ' ');
            }
            Log.e(TAG, "---------onDataReceived: " + sb);
        });
    }

    private void getSales(int checkedId) {
        switch (checkedId) {
            case R.id.daily:
                getSalesDaily();
                break;
            case R.id.weekly:
                getSalesWeekly();
                break;
            case R.id.monthly:
                getSalesMonthly();
                break;
        }
    }

    private void resetFields() {
       /* time.setText("NA");
        total.setText("NA");
        discount.setText("NA");
        delivery_charges.setText("NA");
        service_charges.setText("NA");
        total_order_type_collection.setText("NA");
        total_order_type_delivery.setText("NA");
        total_order_type_table.setText("NA");*/
    }

    private void getSalesDaily() {
        Calendar calendar = Calendar.getInstance();
//        mDate1 = calendar.getTime();
        long eodstart = sharedPreferences.getLong(Preferences.LAST_EOD_TIME, 1);
        mDate1 = new Date(eodstart);
        Log.d(TAG, "getSalesDaily: " + mDate1);
        mDate2 = calendar.getTime();

        final String formattedDate = sdf.format(mDate1);
        startDate.setText(formattedDate);
        endDate.setText(formattedDate);

        filter(mDate1, mDate2);
    }

    private void getSalesWeekly() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMinimum(Calendar.DAY_OF_WEEK));
        mDate1 = calendar.getTime();

        calendar.set(Calendar.DAY_OF_WEEK, calendar.getActualMaximum(Calendar.DAY_OF_WEEK));
        mDate2 = calendar.getTime();

        final String formattedDate1 = sdf.format(mDate1);
        startDate.setText(formattedDate1);

        final String formattedDate2 = sdf.format(mDate2);
        endDate.setText(formattedDate2);

        filter(mDate1, mDate2);
    }

    private void getSalesMonthly() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        mDate1 = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        mDate2 = calendar.getTime();

        final String formattedDate1 = sdf.format(mDate1);
        startDate.setText(formattedDate1);

        final String formattedDate2 = sdf.format(mDate2);
        endDate.setText(formattedDate2);

        filter(mDate1, mDate2);
    }

    private void filter(Date start, Date end) {
        resetFields();
        manageOrdersAdapter.setPurchases(null);
        manageOrdersAdapter.notifyDataSetChanged();
        RealmQuery<Purchase> realmQuery = realm.where(Purchase.class);
        RealmQuery<Purchase> realmQueryForCollection = realm.where(Purchase.class).equalTo("orderType", Constants.TYPE_COLLECTION);
        RealmQuery<Purchase> realmQueryForDelivery = realm.where(Purchase.class).equalTo("orderType", Constants.TYPE_DELIVERY);
        RealmQuery<Purchase> realmQueryForTable = realm.where(Purchase.class).equalTo("orderType", Constants.TYPE_TABLE);

        if (start != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(start);
//            calendar.set(Calendar.HOUR_OF_DAY, 23);
//            calendar.set(Calendar.MINUTE, 59);
//            calendar.set(Calendar.SECOND, 59);
            Date startDateMin = calendar.getTime();

            if (end == null) {
                Log.e(TAG, "-------------mDate2 is NULL");
                end = calendar.getTime();
            }
            calendar.setTime(end);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            Date endDateMax = calendar.getTime();

            Log.d(TAG, "filter: " +startDateMin);
            realmQuery.between("timestamp", startDateMin.getTime(), endDateMax.getTime());
            realmQueryForCollection.between("timestamp", startDateMin.getTime(), endDateMax.getTime());
            realmQueryForDelivery.between("timestamp", startDateMin.getTime(), endDateMax.getTime());
            realmQueryForTable.between("timestamp", startDateMin.getTime(), endDateMax.getTime());

            /*StringBuilder salesDateSB = new StringBuilder();
            salesDateSB.append(" (").append(simpleDateFormat.format(start));
            if (!start.equals(end)) {
                salesDateSB.append(" to " + simpleDateFormat.format(end));
            }
            salesDateSB.append(")");
            time.setText(salesDateSB);*/
        }

            /*Long ordernum = null;
            try {
                ordernum = Long.parseLong(orderNo.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (ordernum != null)
                realmQuery.and().equalTo("id", ordernum.longValue());*/

       /* double totalAmount = realmQuery.sum("total").doubleValue();
        String formattedTotal = String.format("%.2f", totalAmount);
        total.setText(poundSym + " " + formattedTotal);

        double discountAmount = realmQuery.sum("discount").doubleValue();
        final String formattedDiscount = String.format("%.2f", discountAmount);
        discount.setText(formattedDiscount);

        double deliveryCharges = realmQuery.sum("deliveryCharges").doubleValue();
        final String formattedDeliveryCharges = String.format("%.2f", deliveryCharges);
        delivery_charges.setText(formattedDeliveryCharges);

        double serviceCharges = realmQuery.sum("serviceCharges").doubleValue();
        final String formattedServiceCharges = String.format("%.2f", serviceCharges);
        service_charges.setText(formattedServiceCharges);

        long totalCollectionOrders=realmQueryForCollection.count();
        long totalDeliveryOrders=realmQueryForDelivery.count();
        long totalTableOrders=realmQueryForTable.count();

        total_order_type_collection.setText(totalCollectionOrders+"");
        total_order_type_delivery.setText(totalDeliveryOrders+"");
        total_order_type_table.setText(totalTableOrders+"");*/

        Log.e(TAG, "-------------getting results async-------------");
        orderRealmResultsAsync = realmQuery.sort("timestamp", Sort.DESCENDING).findAllAsync();
        orderRealmResultsAsync.addChangeListener(rs -> {
            Log.e(TAG, "-----------------size: " + rs.size());
//            salesReportRV.setAdapter(new SalesOrderAdapter(getActivity(), rs));
            manageOrdersAdapter.setPurchases(rs);
            manageOrdersAdapter.notifyDataSetChanged();
//            RecyclerViewItemSelectionAfterLayoutUpdate.on(ordersRV);
        });
    }

    @Override
    public void onClick(long driverId) {
        Log.e(TAG, "------------Driver Id: " + driverId);
        final Driver newDriver = realm.where(Driver.class).equalTo("id", driverId).findFirst();
        realm.executeTransaction(realm -> {
            if (currentOrder != null) {
                Driver previousDriver = currentOrder.getDriver();
                currentOrder.setDriver(newDriver);
                driverListAdapter.notifyItemChanged(driverListAdapter.getDrivers().indexOf(newDriver));
                if (previousDriver != null) {
                    if (previousDriver.equals(newDriver)) {
                        currentOrder.setDriver(null);
                    }
                    driverListAdapter.notifyItemChanged(driverListAdapter.getDrivers().indexOf(previousDriver));
                }


                manageOrdersAdapter.notifyItemChanged(manageOrdersAdapter.getPurchases().indexOf(currentOrder));
            }
        });
    }
}
