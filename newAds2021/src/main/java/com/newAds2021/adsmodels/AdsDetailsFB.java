package com.newAds2021.adsmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AdsDetailsFB {

    @SerializedName("adsData")
    @Expose
    private ArrayList<AdsDataFB> adsData = null;
    @SerializedName("success")
    @Expose
    private Integer success;

    public ArrayList<AdsDataFB> getAdsData() {
        return adsData;
    }

    public void setAdsData(ArrayList<AdsData> AdsDataFB) {
        this.adsData = adsData;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }
}