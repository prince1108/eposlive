package com.foodciti.foodcitipartener.rest;

import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by quflip1 on 08-02-2017.
 */

public class RetroClient {

//   public static final String ROOT_URL = "https://foodcitiunsecureapi.foodciti.co.uk/";
   public static final String ROOT_URL = "https://foodcitisecureapi.foodciti.co.uk/";
//   public static final String ROOT_URL = "http://35.178.91.227:3000/";
    private static Retrofit restAdapter;
    private static ApiService apiService;

    static {
        setupRestClient();
    }

    private static void setupRestClient()
    {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        okHttpClient.addInterceptor(logging);
        okHttpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Accept", "application/json").build();
                Log.e("Request-----", "" +request.toString());
                return chain.proceed(request);
            }
        });
        restAdapter = new Retrofit.Builder()
                .baseUrl(ROOT_URL)
                .client(okHttpClient.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }


    public static ApiService getApiService() {
        if (apiService == null) {
            apiService = restAdapter.create(ApiService.class);
        }
        return apiService;
    }

}
