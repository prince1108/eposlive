package com.foodciti.foodcitipartener.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.foodciti.foodcitipartener.R;
import com.foodciti.foodcitipartener.db.ExceptionLogger;
import com.foodciti.foodcitipartener.dialogs.ItemSubDetails;
import com.foodciti.foodcitipartener.dialogs.OrderInfo;
import com.foodciti.foodcitipartener.realm_entities.Exlogger;
import com.foodciti.foodcitipartener.response.GenericResponse;
import com.foodciti.foodcitipartener.response.OrderTotal;
import com.foodciti.foodcitipartener.response.OrderedItem;
import com.foodciti.foodcitipartener.response.SubItem;
import com.foodciti.foodcitipartener.rest.RetroClient;
import com.foodciti.foodcitipartener.utils.Application;
import com.foodciti.foodcitipartener.utils.Consts;
import com.foodciti.foodcitipartener.utils.InternetConnection;
import com.foodciti.foodcitipartener.utils.PrintUtils;
import com.foodciti.foodcitipartener.utils.RequestQueueSingleton;
import com.foodciti.foodcitipartener.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android_serialport_api.SerialPort;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.foodciti.foodcitipartener.dialogs.OrderInfo.mediaPlayer;


public class OrderTotalActivity extends AppCompatActivity implements OrderInfo.OnClickListener {

    private TextView orderTotal, collectionOrderNumber, collectionOrderTotal,
            deliveryOrderNumber, deliveryOrderTotal, cardOrderNumber, cardOrderTotal,
            cashOrderNumber, cashOrderTotal, orderNumberText, paypalNumberText, paypalOrderTotal, worldpayNumberText, worldpayOrderTotal;

    private static final String REQ_TAG = "VACTIVITY";
    private String TAG = "OrderTotalActivity";
    private ProgressBar progressBar;
    private RequestQueue requestQueue;
    private String restaurantId;
    private LocalBroadcastManager localBroadcastManager;
    protected Application mApplication;
    protected SerialPort mSerialPort;
    protected OutputStream mOutputStream;
    protected InputStream mInputStream;
    protected ReadThread mReadThread;
    private TextView mPrintReception;
    ExceptionLogger logger;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_of_day_report);
        mApplication = (Application) getApplication();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        logger = new ExceptionLogger(this);
        restaurantId = SessionManager.get(this).getFoodTruckId();
        Log.e("ORDER_RESTAURANT_ID", "" + restaurantId);
        try {
            mSerialPort = mApplication.getPrintSerialPort();
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

            /* Create a receiving thread */
            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (SecurityException e) {
            //DisplayError(R.string.error_security);
        } catch (IOException e) {
            //DisplayError(R.string.error_unknown);
        } catch (InvalidParameterException e) {
            //	DisplayError(R.string.error_configuration);
        }

        progressBar = findViewById(R.id.progressBar1);
        orderTotal = findViewById(R.id.orderTotal);
        orderNumberText = findViewById(R.id.orderNumberText);
        collectionOrderNumber = findViewById(R.id.collectionOrderNumber);
        collectionOrderTotal = findViewById(R.id.collectionOrderTotal);
        deliveryOrderNumber = findViewById(R.id.deliveryOrderNumber);
        deliveryOrderTotal = findViewById(R.id.deliveryOrderTotal);
        cardOrderNumber = findViewById(R.id.cardOrderNumber);
        cardOrderTotal = findViewById(R.id.cardOrderTotal);
        cashOrderNumber = findViewById(R.id.cashOrderNumber);
        cashOrderTotal = findViewById(R.id.cashOrderTotal);
        paypalNumberText = findViewById(R.id.paypalOrderNumber);
        paypalOrderTotal = findViewById(R.id.paypalOrderTotal);
        worldpayNumberText = findViewById(R.id.worldpayOrderNumber);
        worldpayOrderTotal = findViewById(R.id.worldpayOrderTotal);


        requestQueue = RequestQueueSingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        showXReport();

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.printButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReports();
            }
        });

    }


    @Override
    public void itemClick(OrderedItem orderedItem) {
        if (orderedItem.getSubItemList().size() > 0) {
            ItemSubDetails itemSubDetails = ItemSubDetails.newInstance((ArrayList<SubItem>) orderedItem.getSubItemList());
            itemSubDetails.show(getSupportFragmentManager(), null);
        }
    }


    @Override
    public void onOrderAccepted(String orderId) {
        //  loadInitialFragment();
    }

    @Override
    public void onOrderForward(String orderId) {

    }

    @Override
    public void onOrderCancel(String orderId) {

    }


    private void showXReport() {
        showProgressBar();
//        String url = RetroClient.ROOT_URL + "restaurents/orders/total/details/" + restaurantId;
        String url = RetroClient.ROOT_URL + "restaurents/orders/totalCash/" + restaurantId;
        Log.e("URL", "" + url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideProgressBar();

                        try {
                            //  JSONObject jsonObject=new JSONObject(response);
                            Log.e("RESPO", "" + response.toString());

                            if (response.getString("status").equalsIgnoreCase("200")) {
                                JSONObject jsonObject1 = response.getJSONObject("data");
                                Log.e("DATA_OBJ", "" + jsonObject1);
                                JSONObject jsonObject2 = jsonObject1.has("card") ? jsonObject1.getJSONObject("card") : null;
                                if (jsonObject2 != null) {
                                    cardOrderNumber.setText(jsonObject2.getString("order_paid_count"));
                                    cardOrderTotal.setText("£ " + jsonObject2.getString("order_total_paid"));
                                }

                                JSONObject jsonObject3 = jsonObject1.has("cash") ? jsonObject1.getJSONObject("cash") : null;
                                if (jsonObject3 != null) {
                                    cashOrderNumber.setText(jsonObject3.getString("order_unpaid_count"));
                                    cashOrderTotal.setText("£ " + jsonObject3.getString("order_total_unpaid"));
                                }

                                JSONObject jsonObject4 = jsonObject1.has("pickUp") ? jsonObject1.getJSONObject("pickUp") : null;
                                if (jsonObject4 != null) {
                                    collectionOrderNumber.setText(jsonObject4.getString("order_pick_up_count"));
                                    collectionOrderTotal.setText("£ " + jsonObject4.getString("order_total_pick_up"));
                                }


                                JSONObject jsonObject5 = jsonObject1.has("delivery") ? jsonObject1.getJSONObject("delivery") : null;
                                if (jsonObject5 != null) {
                                    deliveryOrderNumber.setText(jsonObject5.getString("order_delivery_count"));
                                    deliveryOrderTotal.setText("£ " + jsonObject5.getString("order_total_delivery"));
                                }

                                JSONObject jsonObject6 = jsonObject1.has("total") ? jsonObject1.getJSONObject("total") : null;
                                if (jsonObject6 != null) {
                                    orderNumberText.setText(jsonObject6.getString("order_total_count"));
                                    orderTotal.setText("£ " + jsonObject6.getString("order_total"));
                                }

                                JSONObject jsonObject7 = jsonObject1.has("paypal") ? jsonObject1.getJSONObject("paypal") : null;
                                if (jsonObject7 != null) {
                                    paypalNumberText.setText(jsonObject7.getString("order_unpaid_count"));
                                    paypalOrderTotal.setText("£ " + jsonObject7.getString("order_total_unpaid"));
                                }

                                JSONObject jsonObject8 = jsonObject1.has("worldpay") ? jsonObject1.getJSONObject("worldpay") : null;
                                if (jsonObject8 != null) {
                                    worldpayNumberText.setText(jsonObject8.getString("order_unpaid_count"));
                                    worldpayOrderTotal.setText("£ " + jsonObject8.getString("order_total_unpaid"));
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "ERROR : " + e.toString());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                hideProgressBar();
                // serverResp.setText("Error getting response");
                Log.e(TAG, "Volley_Error : " + error.toString());
                Toast.makeText(OrderTotalActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        jsonObjectRequest.setTag(REQ_TAG);
        requestQueue.add(jsonObjectRequest);

    }


    private void showOrderTotalAndPrint() {
        if (InternetConnection.checkConnection(this)) {
            Log.e("SHOW_TOTAL", "show total");
            //If the internet is working then only make the request
            Observable<GenericResponse<OrderTotal>> results = RetroClient.getApiService().getTotalMatrix(restaurantId);

            new CompositeDisposable().add(results.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GenericResponse<OrderTotal>>() {
                        @Override
                        public void accept(GenericResponse<OrderTotal> response) throws Exception {
                            Log.e("SHOW_TOTAL_1", "" + response.getStatus());
                            if (response.getStatus().equals(Consts.IS_SUCCESS)) {
                                OrderTotal orderData = response.getData();
                                if (orderData != null) {
                                    if (orderData.getOrderTotalCash() != null) {
                                        cashOrderNumber.setText(orderData.getOrderTotalCash().getOrderUnpaidCount());
                                        cashOrderTotal.setText("£ " + String.format("%.2f", orderData.getOrderTotalCash().getOrderTotalUnpaid()));
                                    } else {
                                        cashOrderNumber.setText("0");
                                        cashOrderTotal.setText("£ " + "00.00");
                                    }
                                    if (orderData.getOrderTotalCard() != null) {
                                        cardOrderNumber.setText(orderData.getOrderTotalCard().getOrderPaidCount());
                                        cardOrderTotal.setText("£ " + String.format("%.2f", orderData.getOrderTotalCard().getOrderTotalPaid()));
                                    } else {
                                        cardOrderNumber.setText("0");
                                        cardOrderTotal.setText("£ " + "00.00");
                                    }
                                    if (orderData.getOrderTotalDelivery() != null) {
                                        deliveryOrderNumber.setText(orderData.getOrderTotalDelivery().getOrderDeliveryCount());
                                        deliveryOrderTotal.setText("£ " + String.format("%.2f", orderData.getOrderTotalDelivery().getOrderTotalDelivery()));
                                    } else {
                                        deliveryOrderNumber.setText("0");
                                        deliveryOrderTotal.setText("£ " + "00.00");
                                    }
                                    if (orderData.getOrderTotalPickUp() != null) {
                                        collectionOrderNumber.setText(orderData.getOrderTotalPickUp().getOrderPickUpCount());
                                        collectionOrderTotal.setText("£ " + String.format("%.2f", orderData.getOrderTotalPickUp().getOrderTotalPickUp()));
                                    } else {
                                        collectionOrderNumber.setText("0");
                                        collectionOrderTotal.setText("£ " + "00.00");
                                    }
                                    if (orderData.getOrderTotalOverAll() != null) {
                                        orderNumberText.setText(orderData.getOrderTotalOverAll().getOrderTotalCount());
                                        orderTotal.setText("£ " + String.format("%.2f", orderData.getOrderTotalOverAll().getOrserTotal()));

                                    } else {
                                        orderNumberText.setText("0");
                                        orderTotal.setText("£ " + "00.00");
                                    }

                                    if (orderData.getOrderTotalPaypal() != null) {
                                        paypalNumberText.setText(orderData.getOrderTotalPaypal().getOrderPaypalCount());
                                        paypalOrderTotal.setText("£ " + String.format("%.2f", orderData.getOrderTotalPaypal().getOrderTotalPaypal()));

                                    } else {
                                        paypalNumberText.setText("0");
                                        paypalOrderTotal.setText("£ " + "00.00");
                                    }

                                    if (orderData.getOrderTotalWorldPay() != null) {
                                        worldpayNumberText.setText(orderData.getOrderTotalWorldPay().getOrderUnpaidCount());
                                        worldpayOrderTotal.setText("£ " + String.format("%.2f", orderData.getOrderTotalWorldPay().getOrderTotalUnpaid()));

                                    } else {
                                        worldpayNumberText.setText("0");
                                        worldpayOrderTotal.setText("£ " + "00.00");
                                    }
                                }

                            }
                            setCookingTimes(restaurantId);
                            if (response!=null&&response.getData() != null)
                                printReport(response);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.e("SHOW_TOTAL_ERROR", "" + throwable.toString());
                        }
                    }));
        }
    }


    public void showReports() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to do the end of the day?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showOrderTotalAndPrint();

                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    //Printing fuctionallity
    protected class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[64];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    //  protected abstract void onDataReceived(final byte[] buffer, final int size);

    @Override
    public void onDestroy() {
        if (mReadThread != null) {
            mReadThread.interrupt();
        }

        if (mApplication != null) {
            mApplication.closeSerialPort();
        }

        mSerialPort = null;

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        super.onDestroy();
    }

    @Override
    protected void onStop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        super.onStop();
    }

    public void onDataReceived(final byte[] buffer, final int size) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (mPrintReception != null) {
                    //mPrintReception.append(new String(buffer, 0, size));
                    for (int i = 0; i < size; i++) {
                        String s = Integer.toHexString((int) buffer[i]);//String.valueOf(((char)buffer[i]));
                        mPrintReception.append(s + ' ');
                    }
                }
            }
        });
    }

    public static byte[] setAlignCenter(char paramChar) {
        byte[] arrayOfByte = new byte[3];
        arrayOfByte[0] = 0x1B;
        arrayOfByte[1] = 0x61;

        switch (paramChar) {
            case '1':
                arrayOfByte[2] = 0x01;
                break;
            case '2':
                arrayOfByte[2] = 0x02;
                break;
            default:
                arrayOfByte[2] = 0x00;
                break;
        }
        return arrayOfByte;
    }

    public static byte[] setWH(char paramChar) {
        byte[] arrayOfByte = new byte[3];
        arrayOfByte[0] = 0x1D;
        arrayOfByte[1] = 0x21;

        switch (paramChar) {
            case '1':
                arrayOfByte[2] = 0x0F;

                break;
            case '2':
                arrayOfByte[2] = 0x10;
                break;
            case '3':
                arrayOfByte[2] = 0x01;
                break;
            case '4':
                arrayOfByte[2] = 0x11;
                break;
            default:
                arrayOfByte[2] = 0x00;
                break;
        }

        return arrayOfByte;
    }

    public static byte[] setInternationalCharcters(char paramChar) {
        byte[] arrayOfByte = new byte[3];
        arrayOfByte[0] = 0x1B;
        arrayOfByte[1] = 0x52;
        //arrayOfByte.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), Consts.COMFORTAA_BOLD));
        switch (paramChar) {
            case '1':
                arrayOfByte[2] = 0x01;//France charactrer set
                break;
            case '2':
                arrayOfByte[2] = 0x02;//Germany charactrer set
                break;
            case '3':
                arrayOfByte[2] = 0x03;//UK charactrer set
                break;
            default:
                arrayOfByte[2] = 0x00;//USA charactrer set
                break;
        }

        return arrayOfByte;
    }

    public static byte[] getGbk(String paramString) {
        byte[] arrayOfByte = null;
        try {
            arrayOfByte = paramString.getBytes("GBK");
        } catch (Exception ex) {

        }
        return arrayOfByte;

    }

    public static byte[] setCusorPosition(int position) {
        byte[] returnText = new byte[4];
        returnText[0] = 0x1B;
        returnText[1] = 0x24;
        returnText[2] = (byte) (position % 256);
        returnText[3] = (byte) (position / 256);
        return returnText;
    }

    public static byte[] setBold(boolean paramBoolean) {
        byte[] arrayOfByte = new byte[3];
        arrayOfByte[0] = 0x1B;
        arrayOfByte[1] = 0x45;
        if (paramBoolean) {
            arrayOfByte[2] = 0x01;
        } else {
            arrayOfByte[2] = 0x00;
        }
        return arrayOfByte;
    }

    public static byte[] PrintBarcode(String paramString) {
        byte[] arrayOfByte = new byte[13 + paramString.length()];
        //设置条码高度
        arrayOfByte[0] = 0x1D;
        arrayOfByte[1] = 'h';
        arrayOfByte[2] = 0x60; //1到255

        //设置条码宽度
        arrayOfByte[3] = 0x1D;
        arrayOfByte[4] = 'w';
        arrayOfByte[5] = 2; //2到6

        //设置条码文字打印位置
        arrayOfByte[6] = 0x1D;
        arrayOfByte[7] = 'H';
        arrayOfByte[8] = 2; //0到3

        //打印39条码
        arrayOfByte[9] = 0x1D;
        arrayOfByte[10] = 'k';
        arrayOfByte[11] = 0x45;
        arrayOfByte[12] = ((byte) paramString.length());
        System.arraycopy(paramString.getBytes(), 0, arrayOfByte, 13, paramString.getBytes().length);
        return arrayOfByte;
    }

    public static byte[] CutPaper()   //切纸； GS V 66D 0D
    {
        byte[] arrayOfByte = new byte[]{0x1D, 0x56, 0x42, 0x00};
        return arrayOfByte;
    }

    boolean printReport(GenericResponse<OrderTotal> response) {

        final String POUND = "\u00A3";

        byte[] b = POUND.getBytes(Charset.forName("UTF-8"));

        boolean returnValue = true;
        try {
            int jNum = 0;

            byte[] printText22 = new byte[102400];

            byte[] oldText = setAlignCenter('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setInternationalCharcters('3');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('4');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk("FoodCiti");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk(SessionManager.get(this).getRestaurantName() + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            String location = SessionManager.get(this).getRestaurantLocation();
            int spacecount = commacount(location);
            //Toast.makeText(this, SessionManager.get(this).getRestaurantName() + "\n"+SessionManager.get(this).getRestaurantLocation(), Toast.LENGTH_LONG).show();
            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            if (spacecount >= 1) {
                oldText = getGbk(location.substring(0, location.indexOf(',')) + "\n" + location.substring(location.indexOf(',') + 1) + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            } else {
                oldText = getGbk(location + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }


            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk(SessionManager.get(this).getRestaurantPostalCode() + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Tel:" + SessionManager.get(this).getRestaurantPhonenumber());
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setAlignCenter('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('5');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(250);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Orders");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setCusorPosition(420);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('5');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("GBP" + "\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setAlignCenter('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            // Collection print text
            oldText = setWH('5');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Collection ");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('5');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(260);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            if (response.getData().getOrderTotalPickUp() != null) {
                oldText = getGbk(String.format(" %02d", response.getData().getOrderTotalPickUp().getOrderPickUpCount()));
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

            } else {
                oldText = getGbk("00");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }


            oldText = setWH('5');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(400);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            if (response.getData().getOrderTotalPickUp() != null) {
                oldText = getGbk(String.format("%.2f", response.getData().getOrderTotalPickUp().getOrderTotalPickUp()) + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

            } else {
                oldText = getGbk("00.00" + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }


            oldText = setAlignCenter('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            // Delivery print text
            oldText = setWH('5');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Delivery ");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(270);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            if (response.getData().getOrderTotalDelivery() != null) {

                oldText = getGbk(String.format("%02d", Integer.parseInt(response.getData().getOrderTotalDelivery().getOrderDeliveryCount())));
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

            } else {
                oldText = getGbk("00");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }


            oldText = setCusorPosition(400);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('5');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            if (response.getData().getOrderTotalDelivery() != null) {

                oldText = getGbk(String.format("%.2f", response.getData().getOrderTotalDelivery().getOrderTotalDelivery()) + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

            } else {
                oldText = getGbk("00.00" + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }


            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            // card print text
            if (response.getData().getOrderTotalCard() != null
                    && Integer.parseInt(response.getData().getOrderTotalCard().getOrderPaidCount()) > 0) {
                oldText = setWH('5');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = getGbk("Card ");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setCusorPosition(270);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('5');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                if (response.getData().getOrderTotalCard() != null) {

                    oldText = getGbk(String.format(" %02d", Integer.parseInt(response.getData().getOrderTotalCard().getOrderPaidCount())));
                    System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                    jNum += oldText.length;

                } /*else {

                oldText = getGbk("00");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

            }*/

                oldText = setCusorPosition(400);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                if (response.getData().getOrderTotalCard() != null) {

                    oldText = getGbk(String.format("%.2f", response.getData().getOrderTotalCard().getOrderTotalPaid()) + "\n");
                    System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                    jNum += oldText.length;

                } /*else {

                oldText = getGbk("00.00" + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

            }*/
            }


            // cash print text
            if (response.getData().getOrderTotalCash() != null
                    && Integer.parseInt(response.getData().getOrderTotalCash().getOrderUnpaidCount()) > 0) {
                oldText = setAlignCenter('0');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;


                oldText = setWH('5');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = getGbk("Cash ");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setCusorPosition(270);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                if (response.getData().getOrderTotalCash() != null) {

                    oldText = getGbk(String.format(" %02d", Integer.parseInt(response.getData().getOrderTotalCash().getOrderUnpaidCount())));
                    System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                    jNum += oldText.length;

                } /*else {
                oldText = getGbk("00");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }*/


                oldText = setCusorPosition(400);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('5');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                if (response.getData().getOrderTotalCash() != null) {

                    oldText = getGbk(String.format("%.2f", response.getData().getOrderTotalCash().getOrderTotalUnpaid()) + "\n");
                    System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                    jNum += oldText.length;

                } /*else {
                oldText = getGbk("00.00" + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }*/
            }


            // paypal print text
            if (response.getData().getOrderTotalPaypal() != null
                    && Integer.parseInt(response.getData().getOrderTotalPaypal().getOrderPaypalCount()) > 0) {
                oldText = getGbk("Paypal ");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setCusorPosition(270);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                if (response.getData().getOrderTotalPaypal() != null) {

                    oldText = getGbk(String.format(" %02d", Integer.parseInt(response.getData().getOrderTotalPaypal().getOrderPaypalCount())));
                    System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                    jNum += oldText.length;

                } /*else {
                oldText = getGbk("00");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }*/


                oldText = setCusorPosition(400);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('5');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                if (response.getData().getOrderTotalPaypal() != null) {

                    oldText = getGbk(String.format("%.2f", response.getData().getOrderTotalPaypal().getOrderTotalPaypal()) + "\n");
                    System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                    jNum += oldText.length;

                } /*else {
                oldText = getGbk("00.00" + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }*/
            }


            // worldpay print text
            if (response.getData().getOrderTotalWorldPay() != null
                    && Integer.parseInt(response.getData().getOrderTotalWorldPay().getOrderUnpaidCount()) > 0) {
                oldText = getGbk("WorldPay ");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setCusorPosition(270);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                if (response.getData().getOrderTotalWorldPay() != null) {

                    oldText = getGbk(String.format(" %02d", Integer.parseInt(response.getData().getOrderTotalWorldPay().getOrderUnpaidCount())));
                    System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                    jNum += oldText.length;

                } /*else {
                oldText = getGbk("00");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }*/


                oldText = setCusorPosition(400);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setWH('5');
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                oldText = setBold(true);
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

                if (response.getData().getOrderTotalWorldPay() != null) {

                    oldText = getGbk(String.format("%.2f", response.getData().getOrderTotalWorldPay().getOrderTotalUnpaid()) + "\n");
                    System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                    jNum += oldText.length;

                } /*else {
                oldText = getGbk("00.00" + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }*/
            }


            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("Total : ");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setAlignCenter('0');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setCusorPosition(270);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            if (response.getData().getOrderTotalOverAll() != null) {

                oldText = getGbk(String.format("%02d", Integer.parseInt(response.getData().getOrderTotalOverAll().getOrderTotalCount())));
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

            } else {
                oldText = getGbk("00");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }


            oldText = setCusorPosition(400);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            if (response.getData().getOrderTotalOverAll() != null) {

                oldText = getGbk(String.format("%.2f", response.getData().getOrderTotalOverAll().getOrserTotal()) + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;

            } else {
                oldText = getGbk("00.00" + "\n");
                System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
                jNum += oldText.length;
            }


            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("-----------------------\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            String strTmp2 = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.UK).format(new Date());
            oldText = getGbk(strTmp2);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('2');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(false);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n-----------------------\n\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setAlignCenter('1');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = setBold(true);
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = setWH('4');
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;


            oldText = getGbk("www.foodciti.co.uk");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            oldText = getGbk("\n\n");
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

           /* String s = new String(printText22);
            Toast.makeText(this, s, Toast.LENGTH_LONG).show();*/

            oldText = CutPaper();
            System.arraycopy(oldText, 0, printText22, jNum, oldText.length);
            jNum += oldText.length;

            Intent intent = new Intent(PrintUtils.ACTION_PRINT_REQUEST);
            intent.putExtra(PrintUtils.PRINT_DATA, printText22);
            localBroadcastManager.sendBroadcast(intent);
//            mOutputStream.write(printText22);

        } catch (Exception ex) {
            returnValue = false;
            Exlogger exlogger = new Exlogger();
            exlogger.setErrorType("Print Error");
            exlogger.setErrorMessage(ex.getMessage());
            exlogger.setScreenName("OrderTotalActivity->>printReport() function");
            logger.addException(exlogger);
            //  Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        return returnValue;
    }

    private int commacount(String s) {
        int i;
        int c = 0;
        for (i = 0, c = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == ',')
                c++;
        }
        return c;
    }


    private void setCookingTimes(String restaurantid) {
        if (InternetConnection.checkConnection(OrderTotalActivity.this)) {
            //If the internet is working then only make the request
            Observable<GenericResponse> results = RetroClient.getApiService().
                    orderHistory(restaurantid);
            showProgressBar();
            new CompositeDisposable().add(results.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GenericResponse>() {
                        @Override
                        public void accept(GenericResponse response) throws Exception {
                            hideProgressBar();
                            if (response.getStatus().equals(Consts.IS_SUCCESS)) {
//                                Intent intent = new Intent(OrderTotalActivity.this, ResturantMainActivityNewPro.class);
//                                startActivity(intent);
                                finish();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            hideProgressBar();
                            //   Toast.makeText(OrderTotalActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }));
        } else {
            Toast.makeText(OrderTotalActivity.this, R.string.string_internet_connection_warning, Toast.LENGTH_LONG).show();

        }
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }


}
