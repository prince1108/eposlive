package com.foodciti.foodcitipartener.parser;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by ravish on 07-03-2017.
 */

public class CallHttp {
    public static String TAG = "Logger===";

    public String httpPostRequest(String url, HashMap<String, String> hashMap) {
        String POST_PARAMS = createQueryStringForParameters(hashMap);
        Log.i(TAG, "POST Request Code :: " + url + POST_PARAMS);
        String mResponse = null;
        URL obj = null;
        HttpURLConnection con = null;
        try {
            obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            // For POST only - BEGIN
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(POST_PARAMS.getBytes());
            os.flush();
            os.close();
            // For POST only - END
            int responseCode = con.getResponseCode();
            Log.i(TAG, "POST Response Code :: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) { //success
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                // print result
                Log.i(TAG, response.toString());
                mResponse = response.toString();
            } else {
                mResponse = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mResponse;
    }



    public String createQueryStringForParameters(Map<String, String> parameters) {
        final char PARAMETER_DELIMITER = '&';
        final char PARAMETER_EQUALS_CHAR = '=';
        StringBuilder parametersAsQueryString = new StringBuilder();
        if (parameters != null) {
            boolean firstParameter = true;
            String s = "";
            for (String parameterName : parameters.keySet()) {
                if (!firstParameter) {
                    parametersAsQueryString.append(PARAMETER_DELIMITER);
                }

                try {
                    s = URLEncoder.encode(parameters.get(parameterName));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                parametersAsQueryString.append(parameterName)
                        .append(PARAMETER_EQUALS_CHAR)
                        .append(s);
                firstParameter = false;
            }
        }
        return parametersAsQueryString.toString();
    }
}
