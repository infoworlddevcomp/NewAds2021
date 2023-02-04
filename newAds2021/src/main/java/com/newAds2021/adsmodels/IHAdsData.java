package com.newAds2021.adsmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class IHAdsData {

    @SerializedName("IhAdsDetail")
    @Expose
    private ArrayList<IhAdsDetail> ihAdsDetail = null;
    @SerializedName("success")
    @Expose
    private Integer success;

    public ArrayList<IhAdsDetail> getIhAdsDetail() {
        return ihAdsDetail;
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
}
