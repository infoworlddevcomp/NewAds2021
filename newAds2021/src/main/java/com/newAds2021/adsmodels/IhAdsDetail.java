package com.newAds2021.adsmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IhAdsDetail {

    @SerializedName("ihads_id")
    @Expose
    private String ihads_id;
    @SerializedName("showad")
    @Expose
    private Boolean showad;
    @SerializedName("openin")
    @Expose
    private String openin;
    @SerializedName("applink")
    @Expose
    private String applink;
    @SerializedName("showreview")
    @Expose
    private Boolean showreview;
    @SerializedName("reviewcount")
    @Expose
    private String reviewcount;
    @SerializedName("showrating")
    @Expose
    private Boolean showrating;
    @SerializedName("showdouble")
    @Expose
    private Boolean showdouble;
    @SerializedName("ratingcount")
    @Expose
    private String ratingcount;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("subtitle")
    @Expose
    private String subtitle;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("extratext")
    @Expose
    private String extratext;
    @SerializedName("buttontext")
    @Expose
    private String buttontext;
    @SerializedName("bigimage")
    @Expose
    private String bigimage;
    @SerializedName("desc_title")
    @Expose
    private String desc_title;
    @SerializedName("desc_text")
    @Expose
    private String desc_text;


    public IhAdsDetail(String ihads_id, Boolean showad, String openin, String applink, Boolean showreview, String reviewcount, Boolean showrating, Boolean showdouble, String ratingcount, String title, String subtitle, String icon, String extratext, String buttontext,String bigimage, String desc_title, String desc_text) {
        this.ihads_id = ihads_id;
        this.showad = showad;
        this.openin = openin;
        this.applink = applink;
        this.showreview = showreview;
        this.reviewcount = reviewcount;
        this.showrating = showrating;
        this.showdouble = showdouble;
        this.ratingcount = ratingcount;
        this.title = title;
        this.subtitle = subtitle;
        this.icon = icon;
        this.extratext = extratext;
        this.buttontext = buttontext;
        this.bigimage = bigimage;
        this.desc_title = desc_title;
        this.desc_text = desc_text;
    }

    public Boolean getShowdouble() {
        return showdouble;
    }

    public void setShowdouble(Boolean showdouble) {
        this.showdouble = showdouble;
    }

    public String getIhads_id() {
        return ihads_id;
    }

    public void setIhads_id(String ihads_id) {
        this.ihads_id = ihads_id;
    }

    public Boolean getShowad() {
        return showad;
    }

    public void setShowad(Boolean showad) {
        this.showad = showad;
    }

    public String getOpenin() {
        return openin;
    }

    public void setOpenin(String openin) {
        this.openin = openin;
    }

    public String getApplink() {
        return applink;
    }

    public void setApplink(String applink) {
        this.applink = applink;
    }

    public Boolean getShowreview() {
        return showreview;
    }

    public void setShowreview(Boolean showreview) {
        this.showreview = showreview;
    }

    public String getReviewcount() {
        return reviewcount;
    }

    public void setReviewcount(String reviewcount) {
        this.reviewcount = reviewcount;
    }

    public Boolean getShowrating() {
        return showrating;
    }

    public void setShowrating(Boolean showrating) {
        this.showrating = showrating;
    }

    public String getRatingcount() {
        return ratingcount;
    }

    public void setRatingcount(String ratingcount) {
        this.ratingcount = ratingcount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getExtratext() {
        return extratext;
    }

    public void setExtratext(String extratext) {
        this.extratext = extratext;
    }

    public String getButtontext() {
        return buttontext;
    }

    public void setButtontext(String buttontext) {
        this.buttontext = buttontext;
    }

    public String getBigimage() {
        return bigimage;
    }

    public void setBigimage(String bigimage) {
        this.bigimage = bigimage;
    }

    public String getDesc_title() {
        return desc_title;
    }

    public void setDesc_title(String desc_title) {
        this.desc_title = desc_title;
    }

    public String getDesc_text() {
        return desc_text;
    }

    public void setDesc_text(String desc_text) {
        this.desc_text = desc_text;
    }
}