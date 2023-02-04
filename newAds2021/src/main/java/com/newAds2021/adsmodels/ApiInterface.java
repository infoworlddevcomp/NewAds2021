package com.newAds2021.adsmodels;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {


    @GET("exec?")
    Call<AdsDetails> getAds(@Query("id") String id);

    @GET("exec")
    Call<AdsDetailsFB> getAdsFB();

    @GET("exec")
    Call<IHAdsData> getIHAds();

    @GET("exec?")
    Call<ResponseDetails> getAppData(@Query("id") String id, @Query("sheet") String sheet);


}
