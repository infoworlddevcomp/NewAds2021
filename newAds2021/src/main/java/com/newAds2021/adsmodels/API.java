package com.newAds2021.adsmodels;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class API {

    private static <T> T builder(Class<T> endpoint) {
        return new Retrofit.Builder()
                .baseUrl("https://script.google.com/macros/s/AKfycbwvZygN-GZ38A5THM1-_gCjMvrQRkxE6eI0D7fJrtG_fP4gUcO7qyO-eaMKrNxeiqRQ/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(endpoint);
    }

    public static ApiInterface apiInterface() {
        return builder(ApiInterface.class);
    }

}
