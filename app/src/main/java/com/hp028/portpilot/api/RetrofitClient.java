package com.hp028.portpilot.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private final static String BASE_URL = "http://192.168.0.102:8080/";

    public static RetrofitService getApiService() {
        return getClient().create(RetrofitService.class);
    }

    public static Retrofit getClient() {
        Gson gson = new GsonBuilder().setLenient().create();
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}
