package com.foodciti.foodcitipartener.services;

/**
 * Created by ravish on 21-02-2020.
 */

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

import com.foodciti.foodcitipartener.db.ExceptionLogger;
import com.foodciti.foodcitipartener.realm_entities.Exlogger;
import com.foodciti.foodcitipartener.realm_entities.Vendor;
import com.foodciti.foodcitipartener.response.GenericResponse;
import com.foodciti.foodcitipartener.response.Order;
import com.foodciti.foodcitipartener.rest.RetroClient;
import com.foodciti.foodcitipartener.utils.Consts;
import com.foodciti.foodcitipartener.utils.InternetConnection;
import com.foodciti.foodcitipartener.utils.RealmManager;
import com.foodciti.foodcitipartener.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import retrofit2.http.Field;

public class LoggerUploadService extends IntentService {

    //    private Context myContext;
//    HashMap<String, String> postDataParams;
    ExceptionLogger logger;
    List<Exlogger> list;
    String mStatus = "";
    String messagge = "";
    private boolean isSuccess=false;
    String restaurantid="";
    public LoggerUploadService() {
        super("LoggerUploadService");
    }

    // will be called asynchronously by Android
    @Override
    protected void onHandleIntent(Intent intent) {
        restaurantid = SessionManager.get(getApplicationContext()).getFoodTruckId();
        logger = new ExceptionLogger(getApplicationContext());
        list = logger.getAllExceptions();
//        String response = null;
        if (list != null & list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                String errormessage = list.get(i).getErrorMessage();
                String errortype = list.get(i).getErrorType();
                String screenname = list.get(i).getScreenName();
                String deviceinfo = list.get(i).getDeviceInfo();
                String networkstatus = list.get(i).getNetworkStatus();
                String errordate = list.get(i).getDateTime();
                performPostCall(restaurantid, errormessage, errortype, screenname, deviceinfo, networkstatus, errordate);
            }
            logger.deleteAllEntries();

//            System.out.println("Response=" + response);
//            JSONObject obj = null;
//            try {
//                obj = new JSONObject(response);
//                mStatus = obj.getString("status");
//                messagge = obj.getString("message");
//            } catch (JSONException e) {
//                e.printStackTrace();
//                Exlogger exlogger = new Exlogger();
//                exlogger.setErrorType("Logger REquest");
//                exlogger.setErrorMessage(e.getMessage());
//                exlogger.setScreenName("LoggerTask");
//                logger.addException(exlogger);
//            }
        }

//        if (mStatus.equalsIgnoreCase("200")) {
//            logger.deleteAllEntries();
//        }
//        if(fromStr!=null&&fromStr.equalsIgnoreCase("Inbox")){
//            publishResults(messagge,mResult);
//        }

    }

//    private void publishResults(String message, String result) {
//        Intent intent = new Intent(NOTIFICATION);
//        intent.putExtra(MESSAGE, message);
//        intent.putExtra(RESULT, result);
//        sendBroadcast(intent);
//    }

    private String getCurrentTimeUsingCalendar() {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm aa");
        String formattedDate = dateFormat.format(date);
        return formattedDate;
    }

    public void performPostCall(String restaurantid, String errormessage, String errortype, String screenname, String deviceinfo, String networkstatus, String errordate) {

        if (InternetConnection.checkConnection(this)) {
            Observable<GenericResponse<String>> results = RetroClient.getApiService()
                    .postErrorLogs(restaurantid, errormessage, errortype, screenname, deviceinfo, networkstatus, errordate);
            new CompositeDisposable().add(results.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<GenericResponse<String>>() {
                        @Override
                        public void accept(GenericResponse<String> response) throws Exception {
                            if (response.getStatus().equals(Consts.IS_SUCCESS)) {
                                System.out.println("Logger---Res"+response);
                                isSuccess=true;
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            System.out.println("Logger---Error"+throwable);
                            isSuccess=false;
                        }
                    }));
        }


//        URL url;
//        String response = "";
//        try {
//            url = new URL("https://foodcitisecureapi.foodciti.co.uk/restaurents/apk/errorlogger");
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setReadTimeout(15000);
//            conn.setConnectTimeout(15000);
//            conn.setRequestProperty("Content-Type", "application/json");
//            conn.setRequestMethod("POST");
//            conn.setDoInput(true);
//            conn.setDoOutput(true);
//            OutputStream os = conn.getOutputStream();
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
//            writer.write(getPostDataString(postDataParams));
//            writer.flush();
//            writer.close();
//            os.close();
//            int responseCode = conn.getResponseCode();
//
//            if (responseCode == HttpsURLConnection.HTTP_OK) {
//                String line;
//                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                while ((line = br.readLine()) != null) {
//                    response += line;
//                }
//            } else {
//                String line;
//                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                while ((line = br.readLine()) != null) {
//                    response += line;
//                }
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
