package com.newAds2021.adsmodels;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class API {

    private static <T> T builder(Class<T> endpoint) {
        return new Retrofit.Builder()
                .baseUrl("https://script.google.com/macros/s/AKfycbyFbu6ZzVDmj_b3WJBDCwplJ1H7hko6jIUqj15hKGx6XX37P5bz-rU1Nj5GmnB_q6fF/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(endpoint);
    }

    public static ApiInterface apiInterface() {
        return builder(ApiInterface.class);
    }

}
