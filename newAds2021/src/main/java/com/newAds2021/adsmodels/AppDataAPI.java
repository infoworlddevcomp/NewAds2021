package com.newAds2021.adsmodels;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppDataAPI {

    private static <T> T builder(Class<T> endpoint) {

        return new Retrofit.Builder()
                .baseUrl("https://script.google.com/macros/s/AKfycbyadTgGZhPIajzDlpIhziPynirF7yKW9bL9U4Nite0zkZKOvHIaz5nSd0bg_WVzQNU/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(endpoint);


    }

    public static ApiInterface apiInterface() {
        return builder(ApiInterface.class);
    }

}
