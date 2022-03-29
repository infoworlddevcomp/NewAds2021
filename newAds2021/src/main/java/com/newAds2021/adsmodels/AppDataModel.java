package com.newAds2021.adsmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppDataModel {

    @SerializedName("show_movies")
    @Expose
    private String showMovies;
    @SerializedName("show_telegram_blog")
    @Expose
    private boolean showTelegramBlog;
    @SerializedName("showwatchcricket")
    @Expose
    private String showWatchCricket;
    @SerializedName("show_channel")
    @Expose
    private String showChannel;
    @SerializedName("show_first_activity")
    @Expose
    private boolean showFirstActivity;
    @SerializedName("show_two_activity")
    @Expose
    private boolean showTwoActivity;
    @SerializedName("show_three_activity")
    @Expose
    private boolean showThreeActivity;
    @SerializedName("show_four_activity")
    @Expose
    private boolean showFourActivity;
    @SerializedName("create_channel")
    @Expose
    private String CreateChannel;
    @SerializedName("live_score")
    @Expose
    private String LiveScore;
    @SerializedName("ipl_cricket")
    @Expose
    private boolean IplCricket;
    @SerializedName("show_get_password")
    @Expose
    private boolean showGetPassword;
    @SerializedName("app_password")
    @Expose
    private String AppPassword;
    @SerializedName("show_blog_movies")
    @Expose
    private String ShowBlogMovies;
    @SerializedName("show_mymovie")
    @Expose
    private String ShowMYMovies;
    @SerializedName("qureka_url1")
    @Expose
    private String QurekaUrl1;
    @SerializedName("qureka_url2")
    @Expose
    private String QurekaUrl2;
    @SerializedName("qureka_url3")
    @Expose
    private String QurekaUrl3;
    @SerializedName("show_max_banner")
    @Expose
    private boolean showMaxBanner;
    @SerializedName("show_max_inter")
    @Expose
    private boolean showMaxInter;
    @SerializedName("max_banner_id")
    @Expose
    private String maxBannerId;
    @SerializedName("max_inter_id")
    @Expose
    private String maxInterId;
    @SerializedName("show_max_native")
    @Expose
    private boolean showMaxNative;
    @SerializedName("max_native_id")
    @Expose
    private String maxNativeId;
    @SerializedName("show_max_small_native")
    @Expose
    private boolean showMaxSmallNative;
    @SerializedName("max_small_native_id")
    @Expose
    private String maxSmallNativeId;


    public String getQurekaUrl1() {
        return QurekaUrl1;
    }

    public void setQurekaUrl1(String qurekaUrl1) {
        QurekaUrl1 = qurekaUrl1;
    }

    public String getQurekaUrl2() {
        return QurekaUrl2;
    }

    public void setQurekaUrl2(String qurekaUrl2) {
        QurekaUrl2 = qurekaUrl2;
    }

    public String getQurekaUrl3() {
        return QurekaUrl3;
    }

    public void setQurekaUrl3(String qurekaUrl3) {
        QurekaUrl3 = qurekaUrl3;
    }

    public boolean isShowMaxBanner() {
        return showMaxBanner;
    }

    public void setShowMaxBanner(boolean showMaxBanner) {
        this.showMaxBanner = showMaxBanner;
    }

    public boolean isShowMaxInter() {
        return showMaxInter;
    }

    public void setShowMaxInter(boolean showMaxInter) {
        this.showMaxInter = showMaxInter;
    }

    public String getMaxBannerId() {
        return maxBannerId;
    }

    public void setMaxBannerId(String maxBannerId) {
        this.maxBannerId = maxBannerId;
    }

    public String getMaxInterId() {
        return maxInterId;
    }

    public void setMaxInterId(String maxInterId) {
        this.maxInterId = maxInterId;
    }

    public boolean isShowMaxNative() {
        return showMaxNative;
    }

    public void setShowMaxNative(boolean showMaxNative) {
        this.showMaxNative = showMaxNative;
    }

    public String getMaxNativeId() {
        return maxNativeId;
    }

    public void setMaxNativeId(String maxNativeId) {
        this.maxNativeId = maxNativeId;
    }

    public boolean isShowMaxSmallNative() {
        return showMaxSmallNative;
    }

    public void setShowMaxSmallNative(boolean showMaxSmallNative) {
        this.showMaxSmallNative = showMaxSmallNative;
    }

    public String getMaxSmallNativeId() {
        return maxSmallNativeId;
    }

    public void setMaxSmallNativeId(String maxSmallNativeId) {
        this.maxSmallNativeId = maxSmallNativeId;
    }

    public String getShowMYMovies() {
        return ShowMYMovies;
    }

    public void setShowMYMovies(String showMYMovies) {
        ShowMYMovies = showMYMovies;
    }

    public String getShowBlogMovies() {
        return ShowBlogMovies;
    }

    public void setShowBlogMovies(String showBlogMovies) {
        ShowBlogMovies = showBlogMovies;
    }

    public String getAppPassword() {
        return AppPassword;
    }

    public void setAppPassword(String appPassword) {
        AppPassword = appPassword;
    }

    public String getShowMovies() {
        return showMovies;
    }

    public void setShowMovies(String showMovies) {
        this.showMovies = showMovies;
    }

    public boolean isShowTelegramBlog() {
        return showTelegramBlog;
    }

    public void setShowTelegramBlog(boolean showTelegramBlog) {
        this.showTelegramBlog = showTelegramBlog;
    }

    public String getShowWatchCricket() {
        return showWatchCricket;
    }

    public void setShowWatchCricket(String showWatchCricket) {
        this.showWatchCricket = showWatchCricket;
    }

    public String getShowChannel() {
        return showChannel;
    }

    public void setShowChannel(String showChannel) {
        this.showChannel = showChannel;
    }

    public boolean isShowFirstActivity() {
        return showFirstActivity;
    }

    public void setShowFirstActivity(boolean showFirstActivity) {
        this.showFirstActivity = showFirstActivity;
    }

    public boolean isShowTwoActivity() {
        return showTwoActivity;
    }

    public void setShowTwoActivity(boolean showTwoActivity) {
        this.showTwoActivity = showTwoActivity;
    }

    public boolean isShowThreeActivity() {
        return showThreeActivity;
    }

    public void setShowThreeActivity(boolean showThreeActivity) {
        this.showThreeActivity = showThreeActivity;
    }

    public boolean isShowFourActivity() {
        return showFourActivity;
    }

    public void setShowFourActivity(boolean showFourActivity) {
        this.showFourActivity = showFourActivity;
    }

    public String getCreateChannel() {
        return CreateChannel;
    }

    public void setCreateChannel(String createChannel) {
        CreateChannel = createChannel;
    }

    public String getLiveScore() {
        return LiveScore;
    }

    public void setLiveScore(String liveScore) {
        LiveScore = liveScore;
    }

    public boolean isIplCricket() {
        return IplCricket;
    }

    public void setIplCricket(boolean iplCricket) {
        IplCricket = iplCricket;
    }

    public boolean isShowGetPassword() {
        return showGetPassword;
    }

    public void setShowGetPassword(boolean showGetPassword) {
        this.showGetPassword = showGetPassword;
    }
}
