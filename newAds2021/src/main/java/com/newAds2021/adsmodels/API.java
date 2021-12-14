package com.newAds2021.adsmodels;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class API {

    private static <T> T builder(Class<T> endpoint) {
        return new Retrofit.Builder()
                .baseUrl("https://script.google.com/macros/s/AKfycbxzrS2v7cCuHT-UOnLy5b0VgapcgiXIfzk76AruCtPBOW161yc0xLsWrscKjnOhJFnu/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(endpoint);
    }

    public static ApiInterface apiInterface() {
        return builder(ApiInterface.class);
    }

}
