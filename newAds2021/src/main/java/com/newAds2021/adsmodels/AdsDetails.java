package com.newAds2021.adsmodels;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdsDetails {

    @SerializedName("adsData")
    @Expose
    private ArrayList<AdsData> adsData = null;
    @SerializedName("IhAdsDetail")
    @Expose
    private ArrayList<IhAdsDetail> ihAdsDetail = null;
    @SerializedName("fbadsData")
    @Expose
    private ArrayList<AdsDataFB> FBadsData = null;

    @SerializedName("success")
    @Expose
    private Integer success;

    public ArrayList<AdsData> getAdsData() {
        return adsData;
    }

    public ArrayList<IhAdsDetail> getIhAdsDetail() {
        return ihAdsDetail;
    }

    public void setAdsData(ArrayList<AdsData> adsData) {
        this.adsData = adsData;
    }

    public void setIhAdsDetail(ArrayList<IhAdsDetail> ihAdsDetail) {
        this.ihAdsDetail = ihAdsDetail;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }


    public ArrayList<AdsDataFB> getFBAdsData() {
        return FBadsData;
    }

    public void setFBAdsData(ArrayList<AdsDataFB> FBadsData) {
        this.FBadsData = FBadsData;
    }










}