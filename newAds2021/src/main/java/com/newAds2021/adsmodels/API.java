package com.newAds2021.adsmodels;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class API {

    private static <T> T builder(Class<T> endpoint) {

        return new Retrofit.Builder()
                .baseUrl("https://script.google.com/macros/s/AKfycbwv0jR9Yr8qjwz0lxraaRmAry03Hocay5a9KPJtrzJWjpQNSRrQMYWQRzqSK0WOX42r/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(endpoint);


    }

    public static ApiInterface apiInterface() {
        return builder(ApiInterface.class);
    }

}
