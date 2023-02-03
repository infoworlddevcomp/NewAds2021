package com.newAds2021.adsmodels;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("exec")
    Call<AdsDetails> getAds();

    @GET("exec")
    Call<IHAdsData> getIHAds();


}
