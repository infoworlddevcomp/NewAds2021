package com.newAds2021.adsmodels;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {


    @GET("exec?")
    Call<AdsDetails> getAds(@Query("sheetid") String sheetid);


    @GET("exec")
    Call<IHAdsData> getIHAds();


}
