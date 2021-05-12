package com.foodciti.foodcitipartener.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.activities.BasicMenuActivity;
import com.foodciti.foodcitipartener.activities.EditOrder;
import com.foodciti.foodcitipartener.activities.ManageOrdersActivity;
import com.foodciti.foodcitipartener.activities.VendorInfoActivity;
import com.foodciti.foodcitipartener.dialogs.CustomAlertDialog;
import com.foodciti.foodcitipartener.dialogs.PasswordDialog;
import com.foodciti.foodcitipartener.realm_entities.OrderCustomerInfo;
import com.foodciti.foodcitipartener.realm_entities.OrderPostalInfo;
import com.foodciti.foodcitipartener.realm_entities.Purchase;
import com.foodciti.foodcitipartener.realm_entities.Vendor;
import com.foodciti.foodcitipartener.utils.Constants;
import com.foodciti.foodcitipartener.utils.Preferences;
import com.foodciti.foodcitipartener.utils.RealmManager;
import com.foodciti.foodcitipartener.utils.StringHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

import static android.content.Context.MODE_PRIVATE;
import static com.foodciti.foodcitipartener.utils.AppConfig.MyPREFERENCES;

public class ManageOrdersAdapter extends RecyclerView.Adapter<ManageOrdersAdapter.OrderHolder> {
    private static final String TAG = "ManageOrdersAdapter";
    private Context context;
    private List<Purchase> purchases;
    private Calendar calendar;
    private Callback callback;
    private SimpleDateFormat dateFormat;
    private Realm realm;
    private static int currentIndex = -1;
    private long eodTIme;
    private MyItemSelectionListener listener;

    private class MyItemSelectionListener implements AdapterView.OnItemSelectedListener {
        private int adapterPosition;
        public MyItemSelectionListener(int adapterPosition) {
            this.adapterPosition=adapterPosition;
        }
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            final Purchase purchase = purchases.get(adapterPosition);
            if(position==0)
                callback.onClickStatusSpinner(purchase.getId(), adapterPosition, true);
            else
                callback.onClickStatusSpinner(purchase.getId(), adapterPosition,false);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    public Purchase getCurrentlySelection() {
        if (currentIndex == -1)
            return null;
        return purchases.get(currentIndex);
    }

    private void setCurrentSelection(int index) {
        int previous = currentIndex;
        if (index == previous)
            return;
        currentIndex = index;
        notifyItemChanged(currentIndex);

        if (previous != -1)
            notifyItemChanged(previous);
    }

    public ManageOrdersAdapter(Context context, List<Purchase> purchases, long eodTIme) {
        this.context = context;
        this.purchases = purchases;
        calendar = Calendar.getInstance();
        if (!(context instanceof Callback))
            throw new RuntimeException("context must implement ManageOrdersAdapter.Callback");
        callback = (Callback) context;
        dateFormat = new SimpleDateFormat(context.getString(R.string.current_date_format));
        this.realm = RealmManager.getLocalInstance();
        this.eodTIme=eodTIme;
    }

    public List<Purchase> getPurchases() {
        return purchases;
    }

    public void setPurchases(List<Purchase> purchases) {
        this.purchases = purchases;
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manageable_order_layout, parent, false);
        return new OrderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHolder holder, int position) {
        final Purchase purchase = purchases.get(position);
        final String type = purchase.getOrderType();
        final long orderNo = purchase.getId();
        final long orderTime = purchase.getTimestamp();
        final long deliveryTime = purchase.getDeliveryTime();
        final String paymentStatus = purchase.getPaymentMode();
        final long elapsedTime = calendar.getTimeInMillis() - orderTime;
        final double seconds = (elapsedTime / 1000);
        final double minutes = (elapsedTime / 60000);
        final double hours = (elapsedTime / 3600000);
        final double days = (elapsedTime / (3600000 * 24));
        //Added By Sourav on 10th June2020
        if(eodTIme!=1){
            if(purchase.getOrderTimeStamp().getTime()<eodTIme){
                holder.edit.setEnabled(false);
                holder.delete.setEnabled(false);
                holder.tvDriver.setEnabled(false);
                holder.tvDriver.setBackground(ContextCompat.getDrawable(context,R.drawable.light_grey_rectangular_btn));
                holder.edit.setColorFilter(Color.argb(150,200,200,200));
                holder.delete.setColorFilter(Color.argb(150,200,200,200));

            }
        }
        String timeElapsed = "";
        if (days > 0)
            timeElapsed = days + " day(s)";
        else if (hours > 0)
            timeElapsed = hours + " hour(s)";
        else if (minutes > 0)
            timeElapsed = minutes + " min(s)";
        else
            timeElapsed = seconds + " sec";

        final String status = purchase.isPaid() ? "PAID" : "NOT PAID";
        if(purchase.isPaid()) {
            holder.setStatus(0);
        } else {
            holder.setStatus(1);
        }
        final double total = purchase.getTotal();
        OrderCustomerInfo customerInfo = purchase.getOrderCustomerInfo();

//        Table table = null;
        String tableName=null;
        if (type.equalsIgnoreCase(Constants.TYPE_TABLE)) {
            tableName = purchase.getTableName();
            holder.type.setText(tableName);
        } else {
            holder.type.setText(type);
        }
        holder.orderNo.setText(orderNo + "");
        holder.date.setText(dateFormat.format(new Date(orderTime)));

        if (!purchase.getOrderType().equals(Constants.TYPE_TABLE)) {
            holder.tvDriver.setVisibility(View.VISIBLE);
            holder.tvNA.setVisibility(View.GONE);
            if (purchase.getDriver() != null) {
                holder.tvDriver.setBackgroundResource(R.color.transparent);
                holder.tvDriver.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
                holder.tvDriver.setText(purchase.getDriver().getDriver_name());
                if (purchase.getDriver().getColor() != -1)
                    holder.cardView.setCardBackgroundColor(purchase.getDriver().getColor());
            } else {
                holder.tvDriver.setTextColor(ContextCompat.getColor(context, R.color.white));
                holder.tvDriver.setText("Choose Driver");
                holder.tvDriver.setBackgroundResource(R.drawable.dark_gray_rectangular_btn);
                holder.cardView.setCardBackgroundColor(Color.WHITE);
            }
        } else {
            holder.tvDriver.setVisibility(View.GONE);
            holder.tvNA.setVisibility(View.VISIBLE);
        }
        holder.paymentStatus.setText(paymentStatus);
        holder.elapsedTime.setText(timeElapsed);
        holder.total.setText(String.format("%.2f", total));

        StringBuilder userInfoBuilder = new StringBuilder();
        if (customerInfo != null) {
            Log.e(TAG, "-------------------userinfo not null--------------------");
            OrderPostalInfo postalInfo = customerInfo.getOrderPostalInfo();
            if (!customerInfo.getName().trim().isEmpty())
                userInfoBuilder.append(customerInfo.getName()).append(", ");
            if (!customerInfo.getPhone().trim().isEmpty())
                userInfoBuilder.append(customerInfo.getPhone()).append(", ");
            if (!customerInfo.getHouse_no().trim().isEmpty())
                userInfoBuilder.append(customerInfo.getHouse_no()).append(", ");
            if (postalInfo != null) {
                userInfoBuilder.append(postalInfo.getA_PostCode()).append(", ");
                userInfoBuilder.append(postalInfo.getAddress());
            }
        } else
            Log.e(TAG, "-------------------userinfo is null--------------------");
        holder.customer.setText(StringHelper.capitalizeEachWordAfterComma(userInfoBuilder.toString()));

        /*if(position==currentIndex) {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.dark_gray_gradientStart));
        }
        else {
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.transparent));
        }*/
    }

    @Override
    public int getItemCount() {
        if (purchases == null)
            return 0;
        return purchases.size();
    }

    public class OrderHolder extends RecyclerView.ViewHolder {
        TextView type, orderNo, date, deliveredBy, paymentStatus, elapsedTime, total, customer, tvNA, tvDriver;
        Spinner status;
        ImageView delete, edit;
        CardView cardView;
        ConstraintLayout mainLayout;

        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final int adapterPosition = getAdapterPosition();
                Log.d(TAG, "------------------ADapter Position: "+adapterPosition);
                if(adapterPosition==-1)
                    return;
                final Purchase purchase = purchases.get(adapterPosition);
                if(position==0)
                    callback.onClickStatusSpinner(purchase.getId(), adapterPosition, true);
                else
                    callback.onClickStatusSpinner(purchase.getId(), adapterPosition,false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        public OrderHolder(View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.type);
            orderNo = itemView.findViewById(R.id.orderno);
            date = itemView.findViewById(R.id.date);
            paymentStatus = itemView.findViewById(R.id.payment_mode);
            elapsedTime = itemView.findViewById(R.id.elapsedtime);
            status = itemView.findViewById(R.id.status);
            total = itemView.findViewById(R.id.right_panel_total);
            customer = itemView.findViewById(R.id.customer);
            tvNA = itemView.findViewById(R.id.tvNA);
            tvDriver = itemView.findViewById(R.id.tvDriver);
            cardView = (CardView) itemView;
            mainLayout = itemView.findViewById(R.id.main_layout);
            edit = itemView.findViewById(R.id.edit);

            edit.setOnClickListener(v -> {
                Vendor vendor = realm.where(Vendor.class).findFirst();
                if (vendor != null && vendor.getAdmin_password()!=null) {
                    if(vendor.getAdmin_password().trim().isEmpty()) {
                        CustomAlertDialog customAlertDialog = CustomAlertDialog.getInstance();
                        customAlertDialog.setTitle("No Password set in vendor Info");
                        customAlertDialog.setMessage("Set Admin Password in Vendor Info?");
                        customAlertDialog.setPositiveButton("Ok", dialog -> {
                            dialog.dismiss();
                        });
                        FragmentManager fm = ((ManageOrdersActivity)context).getSupportFragmentManager();
                        customAlertDialog.show(fm, null);
                    } else {
                        PasswordDialog passwordDialog = PasswordDialog.getInstance();
                        passwordDialog.setPositiveButton("Ok", (dialog, password) -> {

                            if (vendor.getAdmin_password().trim().equals(password)) {
                                dialog.dismiss();
                                final int position = getAdapterPosition();
                                final Purchase purchase = purchases.get(position);
                                Intent intent = new Intent(context, EditOrder.class);
                                intent.putExtra("ORDER_ID", purchase.getId());
                                context.startActivity(intent);
                            } else {
                                Toast.makeText(context, "Password doesn't match", Toast.LENGTH_SHORT).show();
                            }

                        });
                        FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                        passwordDialog.show(fm, null);
                    }
                } else {
                    Toast.makeText(context, "Vendor Info is Null", Toast.LENGTH_SHORT).show();
                }

            });

            delete = itemView.findViewById(R.id.delete);
            delete.setOnClickListener(v -> {
                Vendor vendor = realm.where(Vendor.class).findFirst();
                if(vendor!=null && vendor.getAdmin_password()!=null) {
                    if(vendor.getAdmin_password().trim().isEmpty()) {
                        CustomAlertDialog customAlertDialog = CustomAlertDialog.getInstance();
                        customAlertDialog.setTitle("No Password set in vendor Info");
                        customAlertDialog.setMessage("Set Admin Password in Vendor Info?");
                        customAlertDialog.setPositiveButton("Ok", dialog -> {
                            dialog.dismiss();
                        });
                        FragmentManager fm = ((ManageOrdersActivity)context).getSupportFragmentManager();
                        customAlertDialog.show(fm, null);
                    } else {
                        PasswordDialog passwordDialog = PasswordDialog.getInstance();
                        passwordDialog.setPositiveButton("Ok", (dialog, password) -> {

                            if (vendor.getAdmin_password().trim().equals(password)) {
                                dialog.dismiss();
                                final int position = getAdapterPosition();
                                Purchase purchase = purchases.get(0);
                                try {
                                    purchase = purchases.get(position);
                                }
                                catch (Exception e){

                                }

                                CustomAlertDialog alertDialog = CustomAlertDialog.getInstance();
                                alertDialog.setTitle("Delete Order");
                                alertDialog.setMessage("Are you sure you want to delete this order?");
                                Purchase finalPurchase = purchase;
                                alertDialog.setPositiveButton("Yes", d -> {
                                    realm.executeTransaction(r -> {
                                        finalPurchase.deleteFromRealm();
                                    });
                                    notifyItemChanged(position);
                                    d.dismiss();
                                });
                                alertDialog.setNegativeButton("No", d -> {
                                    d.dismiss();
                                });

                                FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                                alertDialog.show(fm, null);
                            } else {
                                Toast.makeText(context, "Password doesn't match", Toast.LENGTH_SHORT).show();
                            }

                        });
                        FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
                        passwordDialog.show(fm, null);
                    }
                } else {
                    Toast.makeText(context, "Vendor Info is Null", Toast.LENGTH_SHORT).show();
                }

            });

            mainLayout.setOnClickListener(v -> {
                final int position = getAdapterPosition();
                final Purchase purchase = purchases.get(position);
                setCurrentSelection(position);
                callback.onClickDetails(purchase.getId());
            });

            itemView.setOnLongClickListener(v -> {
                final int position = getAdapterPosition();
                final Purchase order = purchases.get(position);
                callback.onLongPress(order.getId());
                return true;
            });

            tvDriver.setOnClickListener(v -> {
                final int position = getAdapterPosition();
                final Purchase order = purchases.get(position);
                callback.onClickDelivererdBy(order.getId());
            });

            String[] strings = new String[]{"PAID", "NOT PAID"};
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, strings);
            status.setAdapter(arrayAdapter);
            status.setSelection(SpinnerAdapter.NO_SELECTION, true);
            status.setOnItemSelectedListener(listener);
        }

        public void setStatus(int position){
            if(listener != null){
                status.setOnItemSelectedListener(null);
                Log.d(TAG, "------------listener is not null----------------");
            } else {
                Log.d(TAG, "------------listener is null----------------");
            }
            status.setSelection(position, true);
            status.setOnItemSelectedListener(listener);
        }
    }

    public interface Callback {
        public void onClickDetails(long id);

        public void onLongPress(long id);

        void onClickDelivererdBy(long orderId);

        void onClickStatusSpinner(long purchaseId, int itemPos, boolean paid);
    }
}
