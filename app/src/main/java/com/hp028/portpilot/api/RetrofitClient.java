package com.hp028.portpilot.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hp028.portpilot.TokenManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private final static String BASE_URL = "http://3.36.205.92:8080/";
    private static Retrofit retrofit = null;
    private static TokenManager tokenManager;

    public static RetrofitService getApiService(Context context) {
        if (retrofit == null) {
            initializeRetrofit(context);
        }
        return retrofit.create(RetrofitService.class);
    }

    private static void initializeRetrofit(Context context) {
        tokenManager = TokenManager.getInstance(context);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(50, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new AuthIntercepter(tokenManager))
                .build();

        Gson gson = new GsonBuilder().setLenient().create();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}
