package com.newAds2021.adutils;

import static com.newAds2021.adsmodels.ConstantAds.ad_bg_drawable;
import static com.newAds2021.adsmodels.ConstantAds.dismisProgress;
import static com.newAds2021.adsmodels.ConstantAds.showProgress;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.newAds2021.Interfaces.InhouseBannerListener;
import com.newAds2021.Interfaces.InhouseInterstitialListener;
import com.newAds2021.Interfaces.InhouseNativeListener;
import com.newAds2021.Interfaces.OnRewardAdClosedListener;
import com.newAds2021.NetworkListner.NetworkStateReceiver;
import com.newAds2021.R;
import com.newAds2021.adsmodels.API;
import com.newAds2021.adsmodels.AdsDataFB;
import com.newAds2021.adsmodels.AdsDetails;
import com.newAds2021.adsmodels.AdsDetailsFB;
import com.newAds2021.adsmodels.AdsPrefernce;
import com.newAds2021.adsmodels.AdsData;
import com.newAds2021.adsmodels.AppsDetails;
import com.newAds2021.adsmodels.FBAPI;
import com.newAds2021.adsmodels.IhAdsDetail;
import com.newAds2021.adsmodels.ConstantAds;
import com.newAds2021.nativeadtemplate.TemplateView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import retrofit2.Call;
import retrofit2.Response;

//"AKfycbwWa0oIwNsZ4b7b-aIGi61iyJ98XFCy2kbfXNC-ZhiIkHtlHu2R88r-gzHc7eigJykh7A/exec"
public class BaseAdsClass extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    private NetworkStateReceiver networkStateReceiver;
    public static boolean isvalidInstall = false;


    public static NativeAd nativeAd1Beta = null;
    public static NativeAd nativeAd2Beta = null;
    public static NativeAd nativeAd3Beta = null;

    //inhouse
    public static boolean isLoaded_ADS, isLoaded_IH, isServiceDialogShown = false;
    public static int currentInter = 0;
    public static int currentBanner = 0;
    public static int currentNative = 0;
    public static boolean isFirstIHInter = true;
    public static boolean isFirstIHBanner = true;
    public static boolean isFirstIHNative = true;
    ArrayList<IhAdsDetail> ihAdsDetails;
    static ArrayList<IhAdsDetail> finalIHAds;
    ArrayList<AdsDataFB> adsDetailsArrayListFB;
    static ArrayList<AppsDetails> moreAppsArrayList;

    public static int interNo = 1;
    public static int bannerNo = 1;
    public static int nativeNo = 1;
    public static int rewardNo = 1;


    Dialog serviceDialog;
    public static int currentAD = 1;
    ArrayList<AdsData> adsDetailsArrayList;
    public static InterstitialAd mInterstitialAd1, mInterstitialAd2, mInterstitialAd3 = null;
    public static com.facebook.ads.InterstitialAd interstitialAd1, interstitialAd2, interstitialAd3 = null;
    public static AppOpenAd appOpenAd1, appOpenAd2, appOpenAd3 = null;

    AdRequest adRequest = new AdRequest.Builder().build();
    AdsPrefernce adsPrefernce;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adsPrefernce = new AdsPrefernce(this);
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.serviceDialog = new Dialog(this);

        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        isvalidInstall = verifyInstallerId(this);


        withDelay(2000, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                if (!isLoaded_ADS) {
                    getAdsx();
                }

                return null;
            }
        });
        AppAdDialog();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

    }

    public void getAdsx() {

        API.apiInterface().getAds(ConstantAds.adUrlId).enqueue(new retrofit2.Callback<AdsDetails>() {
            @Override
            public void onResponse(@NonNull Call<AdsDetails> call, @NonNull Response<AdsDetails> response) {
                AdsDetails adsDetails = response.body();
                adsDetailsArrayList = new ArrayList<>();
                ihAdsDetails = new ArrayList<>();
                adsDetailsArrayListFB = new ArrayList<>();
                try {
                    if (adsDetails.getAdsData() != null) {
                        adsDetailsArrayList = adsDetails.getAdsData();
                        ihAdsDetails = adsDetails.getIhAdsDetail();
                        adsDetailsArrayListFB = adsDetails.getFBAdsData();

                        AdsData ads = adsDetailsArrayList.get(0);

                        adsPrefernce = new AdsPrefernce(BaseAdsClass.this);
                        if (adsDetailsArrayList != null && adsDetailsArrayList.size() > 0) {
                            adsPrefernce.setAdsDefaults(ads.getShowAds(), ads.getAdsCount(), ads.getShowLoading(), ads.getAllowAccess(), ads.getAppAdDialogCount(),
                                    ads.getgBanner1(), ads.getgBanner2(), ads.getgBanner3(),
                                    ads.getgInter1(), ads.getgInter2(), ads.getgInter3(), ads.getgAppopen1(), ads.getgAppopen2(), ads.getgAppopen3(),
                                    ads.getgNative1(), ads.getgNative2(), ads.getgNative3(), ads.getgRewarded1(), ads.getgRewarded2(), ads.getgRewarded3(),
                                    ads.getgRewardinter1(), ads.getgRewardinter2(), ads.getgRewardinter3(), ads.getShowGbanner1(), ads.getShowGbanner2(),
                                    ads.getShowGbanner3(), ads.getShowGInter1(), ads.getShowGInter2(), ads.getShowGInter3(), ads.getShowGappopen1(),
                                    ads.getShowGappopen2(), ads.getShowGappopen3(), ads.getShowGnative1(), ads.getShowGnative2(), ads.getShowGnative3(),
                                    ads.getShowGrewarded1(), ads.getShowGrewarded2(), ads.getShowGrewarded3(), ads.getShowGrewardinter1(), ads.getShowGrewardinter2(),
                                    ads.getShowGrewardinter3(), ads.getExtraPara1(), ads.getExtraPara2(), ads.getExtraPara3(), ads.getExtraPara4(),

                                    ads.getIsUpdate(), ads.getIsAds(), ads.getIsNotification(),

                                    ads.getAdDialogTitle(), ads.getAdAppName(), ads.getAdAppShortDesc(), ads.getAdMessage(), ads.getAdAppUrl(), ads.getAdIconUrl(),
                                    ads.getAdBannerUrl(), ads.getAdButtontext(), ads.getAdShowCancel(),

                                    ads.getNotDialogTitle(), ads.getNotMessage(), ads.getNotShowCancel(),

                                    ads.getUpdateDialogTitle(), ads.getUpdateTitle(), ads.getUpdateAppUrl(), ads.getUpdateMessage(), ads.getUpdateVersionName(),
                                    ads.getUpdateShowCancel()
                            );
                            isLoaded_ADS = true;

                            currentAD = adsPrefernce.adCount();
                            if (ConstantAds.PRELOAD_INTERSTITIAL) {
                                loadInterstitialAds(BaseAdsClass.this);
                                loadInterstitialAdsFB(BaseAdsClass.this);
                            }
                            if (!adsPrefernce.showRewardInter3()) {
                                loadNativeAdBeta();
                            }
                            if (ConstantAds.PRELOAD_REWARD) {
                                loadRewardedAds();
                            }
                            if (ConstantAds.PRELOAD_APPOPEN) {
                                loadAppOpenAds(BaseAdsClass.this);
                            }

                        }


                        if (ihAdsDetails != null && ihAdsDetails.size() > 0) {
                            moreAppsArrayList = new ArrayList<>();
                            finalIHAds = new ArrayList<>();
                            moreAppsArrayList.clear();
                            for (int i = 0; i < ihAdsDetails.size(); i++) {
                                if (ihAdsDetails.get(i).getShowad()) {
                                    if (ihAdsDetails.get(i).getOpenin().equals("playstore")) {
                                        if (!isAppInstalled(getAppIdFromAppLink(ihAdsDetails.get(i).getApplink()))) {
                                            finalIHAds.add(new IhAdsDetail(ihAdsDetails.get(i).getIhads_id(),
                                                    ihAdsDetails.get(i).getShowad(),
                                                    ihAdsDetails.get(i).getOpenin(),
                                                    ihAdsDetails.get(i).getApplink(),
                                                    ihAdsDetails.get(i).getShowreview(),
                                                    ihAdsDetails.get(i).getReviewcount(),
                                                    ihAdsDetails.get(i).getShowrating(),
                                                    ihAdsDetails.get(i).getShowdouble(),
                                                    ihAdsDetails.get(i).getRatingcount(),
                                                    ihAdsDetails.get(i).getTitle(),
                                                    ihAdsDetails.get(i).getSubtitle(),
                                                    ihAdsDetails.get(i).getIcon(),
                                                    ihAdsDetails.get(i).getExtratext(),
                                                    ihAdsDetails.get(i).getButtontext(),
                                                    ihAdsDetails.get(i).getBigimage(),
                                                    ihAdsDetails.get(i).getDesc_title(),
                                                    ihAdsDetails.get(i).getDesc_text()));
                                            moreAppsArrayList.add(new AppsDetails(ihAdsDetails.get(i).getIhads_id(),
                                                    ihAdsDetails.get(i).getIcon(),
                                                    ihAdsDetails.get(i).getTitle(),
                                                    ihAdsDetails.get(i).getApplink(),
                                                    ihAdsDetails.get(i).getShowad(),
                                                    "",
                                                    ihAdsDetails.get(i).getOpenin(),
                                                    ihAdsDetails.get(i).getButtontext()));
                                        }
                                    } else {
                                        finalIHAds.add(new IhAdsDetail(ihAdsDetails.get(i).getIhads_id(),
                                                ihAdsDetails.get(i).getShowad(),
                                                ihAdsDetails.get(i).getOpenin(),
                                                ihAdsDetails.get(i).getApplink(),
                                                ihAdsDetails.get(i).getShowreview(),
                                                ihAdsDetails.get(i).getReviewcount(),
                                                ihAdsDetails.get(i).getShowrating(),
                                                ihAdsDetails.get(i).getShowdouble(),
                                                ihAdsDetails.get(i).getRatingcount(),
                                                ihAdsDetails.get(i).getTitle(),
                                                ihAdsDetails.get(i).getSubtitle(),
                                                ihAdsDetails.get(i).getIcon(),
                                                ihAdsDetails.get(i).getExtratext(),
                                                ihAdsDetails.get(i).getButtontext(),
                                                ihAdsDetails.get(i).getBigimage(),
                                                ihAdsDetails.get(i).getDesc_title(),
                                                ihAdsDetails.get(i).getDesc_text()));
                                        moreAppsArrayList.add(new AppsDetails(ihAdsDetails.get(i).getIhads_id(),
                                                ihAdsDetails.get(i).getIcon(),
                                                ihAdsDetails.get(i).getTitle(),
                                                ihAdsDetails.get(i).getApplink(),
                                                ihAdsDetails.get(i).getShowad(),
                                                "",
                                                ihAdsDetails.get(i).getOpenin(),
                                                ihAdsDetails.get(i).getButtontext()));

                                    }
                                }
                            }
                            adsPrefernce.setInHouseAdDetails(finalIHAds);
                            adsPrefernce.setMoreAppsDetails(moreAppsArrayList);
                            isLoaded_IH = true;

                        }


                        if (adsDetailsArrayListFB != null && adsDetailsArrayListFB.size() > 0) {
                            adsPrefernce.setAdsDefaultsFB(ads.getShowAds(), ads.getAdsCount(), ads.getShowLoading(), ads.getAllowAccess(), ads.getAppAdDialogCount(),
                                    ads.getgBanner1(), ads.getgBanner2(), ads.getgBanner3(),
                                    ads.getgInter1(), ads.getgInter2(), ads.getgInter3(), ads.getgAppopen1(), ads.getgAppopen2(), ads.getgAppopen3(),
                                    ads.getgNative1(), ads.getgNative2(), ads.getgNative3(), ads.getgRewarded1(), ads.getgRewarded2(), ads.getgRewarded3(),
                                    ads.getgRewardinter1(), ads.getgRewardinter2(), ads.getgRewardinter3(), ads.getShowGbanner1(), ads.getShowGbanner2(),
                                    ads.getShowGbanner3(), ads.getShowGInter1(), ads.getShowGInter2(), ads.getShowGInter3(), ads.getShowGappopen1(),
                                    ads.getShowGappopen2(), ads.getShowGappopen3(), ads.getShowGnative1(), ads.getShowGnative2(), ads.getShowGnative3(),
                                    ads.getShowGrewarded1(), ads.getShowGrewarded2(), ads.getShowGrewarded3(), ads.getShowGrewardinter1(), ads.getShowGrewardinter2(),
                                    ads.getShowGrewardinter3(), ads.getExtraPara1(), ads.getExtraPara2(), ads.getExtraPara3(), ads.getExtraPara4()
                            );
                            isLoaded_ADS = true;

                            currentAD = adsPrefernce.adCount();

                            if (ConstantAds.PRELOAD_INTERSTITIAL) {
                                loadInterstitialAds(BaseAdsClass.this);
                                loadInterstitialAdsFB(BaseAdsClass.this);
                            }
                            if (!adsPrefernce.showRewardInter3()) {
                                loadNativeAdBeta();
                            }
                            if (ConstantAds.PRELOAD_REWARD) {
                                loadRewardedAds();
                            }
                            if (ConstantAds.PRELOAD_APPOPEN) {
                                loadAppOpenAds(BaseAdsClass.this);
                            }

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }


            }

            @Override
            public void onFailure(@NonNull Call<AdsDetails> call, @NonNull Throwable t) {

            }
        });
    }


    boolean verifyInstallerId(Context context) {
        List<String> validInstallers = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));
        final String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());
        return installer != null && validInstallers.contains(installer);
    }

    public void validateInstall(Callable<Void> callable) {
        if (!adsPrefernce.allowAccess()) {
            if (!isvalidInstall) {
                try {
                    callable.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    int getCurrentInterAd(int totalAds) {
        if (!isFirstIHInter) {
            if (currentInter + 1 >= totalAds) {
                currentInter = 0;
            } else {
                currentInter = currentInter + 1;
            }
        } else {
            currentInter = 0;
            isFirstIHInter = false;

        }
        return currentInter;
    }

    int getCurrentBannerAd(int totalAds) {
        if (!isFirstIHBanner) {
            if (currentBanner + 1 >= totalAds) {
                currentBanner = 0;
            } else {
                currentBanner = currentBanner + 1;
            }
        } else {
            currentBanner = 0;
            isFirstIHBanner = false;

        }
        return currentBanner;

    }

    int getCurrentNativeAd(int totalAds) {
        if (!isFirstIHNative) {
            if (currentNative + 1 >= totalAds) {
                currentNative = 0;
            } else {
                currentNative = currentNative + 1;
            }
        } else {
            currentNative = 0;
            isFirstIHNative = false;

        }
        return currentNative;

    }


    public void showInhouseInterAd(Activity context,InhouseInterstitialListener inhouseInterstitialListener) {
        try {
            if (adsPrefernce.isInHouseAdLoaded()) {
                // get Interstitial Data
                ArrayList<IhAdsDetail> savedInterAdDetails = adsPrefernce.getInHouseAds();
                if (savedInterAdDetails.size() != 0) {
                    // ad to show from position
                    int current = getCurrentInterAd(savedInterAdDetails.size());


                    final Dialog interDialog = new Dialog(context);
                    interDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    interDialog.setContentView(R.layout.ad_interstitial);
                    interDialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
                    interDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    Objects.requireNonNull(interDialog.getWindow()).getAttributes().windowAnimations = R.style.InterstitialAdAnimation;
                    interDialog.setCancelable(false);

                    ImageView iv_close_ad = interDialog.findViewById(R.id.iv_close_ad);
                    LinearLayout lay_close_ad = interDialog.findViewById(R.id.lay_close_ad);
                    ImageView iv_ad_icon = interDialog.findViewById(R.id.iv_ad_icon);
                    RatingBar iv_inter_star_rating = interDialog.findViewById(R.id.iv_inter_star_rating);
                    TextView tv_inter_ad_title = interDialog.findViewById(R.id.tv_inter_ad_title);
                    TextView tv_inter_ad_subtitle = interDialog.findViewById(R.id.tv_inter_ad_subtitle);
                    TextView tv_inter_review_count = interDialog.findViewById(R.id.tv_inter_review_count);

                    ImageView iv_inter_main_banner = interDialog.findViewById(R.id.iv_inter_main_banner);

                    TextView tv_inter_ad_desc = interDialog.findViewById(R.id.tv_inter_ad_desc);
                    TextView tv_inter_ad_sub_desc = interDialog.findViewById(R.id.tv_inter_ad_sub_desc);

                    ImageView iv_inter_info = interDialog.findViewById(R.id.iv_inter_info);

                    TextView tv_install_btn_inter = interDialog.findViewById(R.id.tv_install_btn_inter);

                    // set Interstitial Data
                    IhAdsDetail interAd = savedInterAdDetails.get(current);

                    if (!context.isFinishing() && !context.isDestroyed()) {
                        // icon
                        Glide.with(context).load(interAd.getIcon()).into(iv_ad_icon);
                        // banner
                        Glide.with(context).load(interAd.getBigimage()).into(iv_inter_main_banner);
                    }
                    // title
                    tv_inter_ad_title.setText(interAd.getTitle());
                    // subtitle
                    tv_inter_ad_subtitle.setText(interAd.getSubtitle());
                    // install button Text
                    tv_install_btn_inter.setText(interAd.getButtontext());

                    // show rating or not and set rating image
                    if (interAd.getShowrating()) {
                        iv_inter_star_rating.setVisibility(View.VISIBLE);
                        iv_inter_star_rating.setRating(Float.parseFloat(interAd.getRatingcount()));
                    } else {
                        iv_inter_star_rating.setVisibility(View.GONE);
                    }

                    // show reviews or not and set review count
                    if (interAd.getShowreview()) {
                        tv_inter_review_count.setVisibility(View.VISIBLE);
                        tv_inter_review_count.setText("  ( " + interAd.getReviewcount() + " )");
                    } else {
                        tv_inter_review_count.setVisibility(View.GONE);
                    }

                    // description title
                    tv_inter_ad_desc.setText(interAd.getDesc_title());

                    // description text
                    tv_inter_ad_sub_desc.setText(interAd.getDesc_text());


                    withDelay(1000, new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            lay_close_ad.setVisibility(View.VISIBLE);
                            return null;
                        }
                    });

                    lay_close_ad.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            interDialog.dismiss();
                            inhouseInterstitialListener.onAdDismissed();
                        }
                    });

                    iv_inter_info.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showAdsPrivacyDialog();
                        }
                    });

                    tv_install_btn_inter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // open link
                            if (interAd.getOpenin().equals("playstore")) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(interAd.getApplink())));
                            } else {
                                Uri uri = Uri.parse(interAd.getApplink()); // missing 'http://' will cause crashed
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        }
                    });


                    if (interAd.getOpenin().equals("playstore")) {
                        if (!isAppInstalled(getAppIdFromAppLink(interAd.getApplink())) && !context.isFinishing() && !context.isDestroyed()) {
                            interDialog.show();
                            inhouseInterstitialListener.onAdShown();
                        } else {
                            inhouseInterstitialListener.onAdDismissed();
                        }
                    } else {
                        if (!context.isFinishing() && !context.isDestroyed()) {
                            interDialog.show();
                            inhouseInterstitialListener.onAdShown();
                        } else {
                            inhouseInterstitialListener.onAdDismissed();
                        }

                    }
                } else {
                    inhouseInterstitialListener.onAdDismissed();
                }
            } else {
                inhouseInterstitialListener.onAdDismissed();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            inhouseInterstitialListener.onAdDismissed();
        }
    }

    public void showInhouseBannerAd(InhouseBannerListener inhouseBannerListener) {
        try {
            if (adsPrefernce.isInHouseAdLoaded()) {
                ArrayList<IhAdsDetail> savedIhAdsDetails = adsPrefernce.getInHouseAds();
                if (savedIhAdsDetails.size() != 0) {
                    // ad to show from position
                    int current = getCurrentBannerAd(savedIhAdsDetails.size());

                    ImageView iv_banner_info = findViewById(R.id.iv_banner_info);
                    ImageView iv_close_ad_banner = findViewById(R.id.iv_close_ad_banner);
                    ImageView iv_ad_icon_banner = findViewById(R.id.iv_ad_icon_banner);

                    TextView tv_banner_ad_title = findViewById(R.id.tv_banner_ad_title);
                    TextView tv_banner_ad_subtitle = findViewById(R.id.tv_banner_ad_subtitle);

                    RatingBar iv_banner_star_rating = findViewById(R.id.iv_banner_star_rating);
                    TextView tv_banner_review_count = findViewById(R.id.tv_banner_review_count);

                    TextView tv_install_btn_banner = findViewById(R.id.tv_install_btn_banner);
                    TextView tv_banner_extra_text = findViewById(R.id.tv_banner_extra_text);

                    RelativeLayout lay_first = findViewById(R.id.lay_first);
                    RelativeLayout lay_second = findViewById(R.id.lay_second);
                    RelativeLayout lay_banner_ad = findViewById(R.id.lay_banner_ad);

                    lay_banner_ad.setVisibility(View.VISIBLE);


                    // set Banner Data
                    IhAdsDetail bannerAd = savedIhAdsDetails.get(current);


                    //icon
                    if (!this.isFinishing() || !this.isDestroyed()) {
                        Glide.with(this).load(bannerAd.getIcon()).into(iv_ad_icon_banner);
                    }

                    // title
                    tv_banner_ad_title.setText(bannerAd.getTitle());
                    // subtitle
                    tv_banner_ad_subtitle.setText(bannerAd.getSubtitle());
                    // install button Text
                    tv_install_btn_banner.setText(bannerAd.getButtontext());

                    // show rating or not and set rating image
                    if (bannerAd.getShowrating()) {
                        iv_banner_star_rating.setVisibility(View.VISIBLE);
                        iv_banner_star_rating.setRating(Float.parseFloat(bannerAd.getRatingcount()));
                    } else {
                        iv_banner_star_rating.setVisibility(View.GONE);
                    }

                    // show reviews or not and set review count
                    if (bannerAd.getShowreview()) {
                        tv_banner_review_count.setVisibility(View.VISIBLE);
                        tv_banner_review_count.setText("  ( " + bannerAd.getReviewcount() + " )");
                    } else {
                        tv_banner_review_count.setVisibility(View.GONE);
                    }

                    // extra text
                    tv_banner_extra_text.setText(bannerAd.getExtratext());

                    // check if double layout
                    if (bannerAd.getShowdouble()) {
                        Handler handler = new Handler();
                        Runnable run = new Runnable() {
                            @Override
                            public void run() {
                                if (lay_first.getVisibility() == View.VISIBLE) {
                                    lay_first.setVisibility(View.GONE);
                                    lay_second.setVisibility(View.VISIBLE);
                                } else {
                                    lay_first.setVisibility(View.VISIBLE);
                                    lay_second.setVisibility(View.GONE);
                                }
                                handler.postDelayed(this, 3000);
                            }
                        };

                        handler.post(run);

                    } else {
                        lay_first.setVisibility(View.VISIBLE);
                        lay_second.setVisibility(View.GONE);
                    }

                    // set selected
                    tv_banner_ad_title.setSelected(true);
                    tv_banner_ad_subtitle.setSelected(true);
                    tv_banner_extra_text.setSelected(true);


                    iv_banner_info.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showAdsPrivacyDialog();
                        }
                    });

                    tv_install_btn_banner.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // open link
                            if (bannerAd.getOpenin().equals("playstore")) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(bannerAd.getApplink())));
                            } else {
                                Uri uri = Uri.parse(bannerAd.getApplink()); // missing 'http://' will cause crashed
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        }
                    });
                    inhouseBannerListener.onAdLoaded();
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void inflateNativeAdInHouse(Boolean isSmall, CardView cardView) {

        // Add the Ad view into the ad container.
        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout adViews = (RelativeLayout) inflater.inflate(R.layout.ad_native, cardView, false);
        cardView.removeAllViews();

//            cardView.setBackground(getResources().getDrawable(R.drawable.gnt_rounded_corners_shape));
        cardView.addView(adViews);

        // get Interstitial Data
        ArrayList<IhAdsDetail> nativeDetails = adsPrefernce.getInHouseAds();

        try {
            if (nativeDetails.size() != 0) {
                // ad to show from position
                int current = getCurrentNativeAd(nativeDetails.size());

                ImageView iv_native_info = adViews.findViewById(R.id.iv_native_info);
                ImageView iv_ad_icon_native = adViews.findViewById(R.id.iv_ad_icon_native);
                ImageView iv_native_main_banner = adViews.findViewById(R.id.iv_native_main_banner);

                TextView tv_native_ad_title = adViews.findViewById(R.id.tv_native_ad_title);
                TextView tv_native_ad_subtitle = adViews.findViewById(R.id.tv_native_ad_subtitle);

                RatingBar native_ad_rating = adViews.findViewById(R.id.native_ad_rating);
                TextView tv_native_review_count = adViews.findViewById(R.id.tv_native_review_count);

                TextView btn_ad_install_native = adViews.findViewById(R.id.btn_ad_install_native);
                TextView tv_native_extra_text = adViews.findViewById(R.id.tv_native_extra_text);

                RelativeLayout lay_native_ad = adViews.findViewById(R.id.lay_native_ad);
                RelativeLayout bottom_view = adViews.findViewById(R.id.bottom_view);

                lay_native_ad.setVisibility(View.VISIBLE);


                // set Interstitial Data
                IhAdsDetail nativeAd = nativeDetails.get(current);


                if (!this.isFinishing() || !this.isDestroyed()) {
                    // icon
                    Glide.with(this).load(nativeAd.getIcon()).into(iv_ad_icon_native);
                    // banner
                    if (isSmall) {
                        iv_native_main_banner.setVisibility(View.GONE);
                    } else {
                        iv_native_main_banner.setVisibility(View.VISIBLE);
                        Glide.with(this).load(nativeAd.getBigimage()).into(iv_native_main_banner);

                    }

                }

                // title
                tv_native_ad_title.setText(nativeAd.getTitle());
                // subtitle
                tv_native_ad_subtitle.setText(nativeAd.getSubtitle());
                // install button Text
                btn_ad_install_native.setText(nativeAd.getButtontext());
                if (ConstantAds.ad_bg_drawable != 0) {
                    btn_ad_install_native.setBackgroundResource(ConstantAds.ad_bg_drawable);
                }

                // show rating or not and set rating image
                if (nativeAd.getShowrating()) {
                    native_ad_rating.setVisibility(View.VISIBLE);
                    native_ad_rating.setRating(Float.parseFloat(nativeAd.getRatingcount()));
                } else {
                    native_ad_rating.setVisibility(View.GONE);
                }

                // show reviews or not and set review count
                if (nativeAd.getShowreview().equals("1")) {
                    tv_native_review_count.setVisibility(View.VISIBLE);
                    tv_native_review_count.setText("  ( " + nativeAd.getReviewcount() + " )");
                } else {
                    tv_native_review_count.setVisibility(View.GONE);
                }

                // extra text
                tv_native_extra_text.setText(nativeAd.getExtratext());

                // set selected
                tv_native_ad_title.setSelected(true);
                tv_native_ad_subtitle.setSelected(true);
                tv_native_extra_text.setSelected(true);

                iv_native_info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showAdsPrivacyDialog();
                    }
                });

                btn_ad_install_native.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // open link
                        if (nativeAd.getOpenin().equals("playstore")) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(nativeAd.getApplink())));
                        } else {
                            Uri uri = Uri.parse(nativeAd.getApplink()); // missing 'http://' will cause crashed
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    }
                });

                if (ConstantAds.native_ad_bg != 0) {
                    lay_native_ad.setBackgroundResource(ConstantAds.native_ad_bg);
                }

            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


    }

    void showAdsPrivacyDialog() {
        Dialog privacyDialog = new Dialog(BaseAdsClass.this);
        privacyDialog.setContentView(R.layout.ads_privacy_dialog);
        Objects.requireNonNull(privacyDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        privacyDialog.setCancelable(false);
        TextView tv_ok_btn_ad_privacy = privacyDialog.findViewById(R.id.tv_ok_btn_ad_privacy);
        tv_ok_btn_ad_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                privacyDialog.dismiss();
            }
        });
        privacyDialog.show();

    }

    String getAppIdFromAppLink(String appLink) {
        String link = appLink;
        String[] s1 = link.split("id=");
        String[] s2 = s1[1].split("&");
        return s2[0].toString();
    }

    void loadInterstitialAds(Context context) {
        loadInterstitial1();
        loadInterstitial2();
        loadInterstitial3();
    }

    void loadInterstitialAdsFB(Context context) {
        loadInterstitial1FB();
        loadInterstitial2FB();
        loadInterstitial3FB();
    }

    void loadRewardedAds() {
        loadRewardAd1();
        loadRewardAd2();
        loadRewardAd3();
    }

    void loadAppOpenAds(Context context) {
        loadAppOpen2();
        loadAppOpen3();
    }

    public void AppService(String versionName) {

        if (adsPrefernce.allowAccess()) {
            if (isConnected(this) && !isServiceDialogShown) {
                serviceDialog(versionName);
            }
        } else {
            if (isvalidInstall) {
                if (isConnected(this) && !isServiceDialogShown) {
                    serviceDialog(versionName);
                }
            }
        }
    }

    public void AppAdDialog() {
        serviceDialog();
    }

    public void serviceDialog(String version_name) {

        if (!serviceDialog.isShowing()) {
            this.serviceDialog.setCancelable(false);
            this.serviceDialog.setContentView(R.layout.dialog_service);
            Objects.requireNonNull(this.serviceDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
            LinearLayout lay_updateApp = this.serviceDialog.findViewById(R.id.lay_updateApp);
            LinearLayout lay_message = this.serviceDialog.findViewById(R.id.lay_message);
            LinearLayout lay_ads = this.serviceDialog.findViewById(R.id.lay_ads);

            ImageView iv_ad_icon_title = this.serviceDialog.findViewById(R.id.iv_ad_icon_title);
            TextView tv_dialog_title = this.serviceDialog.findViewById(R.id.tv_dialog_title);

            //update
            TextView tv_updatetitle = this.serviceDialog.findViewById(R.id.tv_updatetitle);
            TextView tv_versionName = this.serviceDialog.findViewById(R.id.tv_versionName);
            TextView tv_updatemessage = this.serviceDialog.findViewById(R.id.tv_updatemessage);
            TextView tv_updatebutton = this.serviceDialog.findViewById(R.id.tv_updatebutton);
            TextView tv_canclebutton = this.serviceDialog.findViewById(R.id.tv_canclebutton);

            //message
            TextView tv_message = this.serviceDialog.findViewById(R.id.tv_message);
            TextView tv_not_cancel_button = this.serviceDialog.findViewById(R.id.tv_not_cancel_button);

            //ads
            TextView tv_ad_message = this.serviceDialog.findViewById(R.id.tv_ad_message);
            ImageView iv_ad_banner = this.serviceDialog.findViewById(R.id.iv_ad_banner);
            ImageView iv_app_icon = this.serviceDialog.findViewById(R.id.iv_app_icon);
            TextView tv_app_name = this.serviceDialog.findViewById(R.id.tv_app_name);
            TextView tv_app_shortdesc = this.serviceDialog.findViewById(R.id.tv_app_shortdesc);
            TextView tv_app_download = this.serviceDialog.findViewById(R.id.tv_app_download);
            TextView tv_app_cancel = this.serviceDialog.findViewById(R.id.tv_app_cancel);

            if (!isServiceDialogShown) {
                if (adsPrefernce.isUpdate()) {
                    if (!version_name.equals(adsPrefernce.updateVersionName())) {
                        iv_ad_icon_title.setVisibility(View.GONE);
                        lay_message.setVisibility(View.GONE);
                        lay_ads.setVisibility(View.GONE);
                        lay_updateApp.setVisibility(View.VISIBLE);
                        tv_dialog_title.setText(adsPrefernce.updateDialogTitle());

                        tv_updatetitle.setText(adsPrefernce.updateTitle());
                        tv_versionName.setText(adsPrefernce.updateVersionName());
                        tv_updatemessage.setText(adsPrefernce.updateMessage());

                        if (adsPrefernce.updateShowCancel()) {
                            tv_canclebutton.setVisibility(View.VISIBLE);
                        } else {
                            tv_canclebutton.setVisibility(View.GONE);
                        }

                        tv_updatebutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(adsPrefernce.updateAppUrl()));
                                startActivity(intent);
                            }
                        });

                        tv_canclebutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                serviceDialog.dismiss();
                            }
                        });
                        this.serviceDialog.show();
                    } else if (adsPrefernce.isNotification()) {
                        lay_ads.setVisibility(View.GONE);
                        lay_updateApp.setVisibility(View.GONE);
                        lay_message.setVisibility(View.VISIBLE);
                        tv_dialog_title.setText(adsPrefernce.notDialogTitle());
                        iv_ad_icon_title.setVisibility(View.GONE);
                        tv_message.setText(adsPrefernce.notMessage());
                        if (adsPrefernce.notShowCancel()) {
                            tv_not_cancel_button.setVisibility(View.VISIBLE);
                        } else {
                            tv_not_cancel_button.setVisibility(View.GONE);
                        }
                        tv_not_cancel_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                serviceDialog.dismiss();
                            }
                        });
                        this.serviceDialog.show();
                    } else if (adsPrefernce.isAds()) {
                        iv_ad_icon_title.setVisibility(View.VISIBLE);
                        lay_updateApp.setVisibility(View.GONE);
                        lay_message.setVisibility(View.GONE);
                        lay_ads.setVisibility(View.VISIBLE);
                        tv_dialog_title.setText(adsPrefernce.adDialogTitle());

                        if (adsPrefernce.adShowCancel()) {
                            tv_app_cancel.setVisibility(View.VISIBLE);
                            tv_app_cancel.setText(adsPrefernce.adButtonText());
                        } else {
                            tv_app_cancel.setVisibility(View.GONE);
                        }

                        tv_ad_message.setText(adsPrefernce.adMessage());
                        if (!this.isFinishing() && !this.isDestroyed()) {
                            Glide.with(this).load(adsPrefernce.adBannerUrl()).into(iv_ad_banner);
                            Glide.with(this).load(adsPrefernce.adIconUrl()).into(iv_app_icon);
                        }
                        tv_app_name.setText(adsPrefernce.adAppName());
                        tv_app_shortdesc.setText(adsPrefernce.adShortDesc());

                        tv_app_download.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(adsPrefernce.adAppUrl()));
                                startActivity(intent);
                            }
                        });

                        tv_app_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                serviceDialog.dismiss();
                            }
                        });

                        if (adsPrefernce.adAppUrl().contains("play.google.com")) {
                            String link = adsPrefernce.adAppUrl();
                            String[] s1 = link.split("id=");
                            String[] s2 = s1[1].split("&");
                            String app_id = s2[0].toString();
                            if (!isAppInstalled(app_id)) {
                                this.serviceDialog.show();
                            }
                        } else {
                            this.serviceDialog.show();
                        }
                    }
                } else if (adsPrefernce.isNotification()) {
                    lay_ads.setVisibility(View.GONE);
                    lay_updateApp.setVisibility(View.GONE);
                    lay_message.setVisibility(View.VISIBLE);
                    tv_dialog_title.setText(adsPrefernce.notDialogTitle());
                    iv_ad_icon_title.setVisibility(View.GONE);
                    tv_message.setText(adsPrefernce.notMessage());
                    if (adsPrefernce.notShowCancel()) {
                        tv_not_cancel_button.setVisibility(View.VISIBLE);
                    } else {
                        tv_not_cancel_button.setVisibility(View.GONE);
                    }
                    tv_not_cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            serviceDialog.dismiss();
                        }
                    });
                    this.serviceDialog.show();
                } else if (adsPrefernce.isAds()) {
                    iv_ad_icon_title.setVisibility(View.VISIBLE);
                    lay_updateApp.setVisibility(View.GONE);
                    lay_message.setVisibility(View.GONE);
                    lay_ads.setVisibility(View.VISIBLE);
                    tv_dialog_title.setText(adsPrefernce.adDialogTitle());
                    tv_app_download.setText(adsPrefernce.adButtonText());

                    if (adsPrefernce.adShowCancel()) {
                        tv_app_cancel.setVisibility(View.VISIBLE);
                    } else {
                        tv_app_cancel.setVisibility(View.GONE);
                    }

                    tv_ad_message.setText(adsPrefernce.adMessage());
                    if (!this.isFinishing() && !this.isDestroyed()) {
                        Glide.with(this).load(adsPrefernce.adBannerUrl()).into(iv_ad_banner);
                        Glide.with(this).load(adsPrefernce.adIconUrl()).into(iv_app_icon);
                    }
                    tv_app_name.setText(adsPrefernce.adAppName());
                    tv_app_shortdesc.setText(adsPrefernce.adShortDesc());

                    tv_app_download.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(adsPrefernce.adAppUrl()));
                            startActivity(intent);
                        }
                    });

                    tv_app_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            serviceDialog.dismiss();
                        }
                    });

                    if (adsPrefernce.adAppUrl().contains("play.google.com")) {
                        String link = adsPrefernce.adAppUrl();
                        String[] s1 = link.split("id=");
                        String[] s2 = s1[1].split("&");
                        String app_id = s2[0].toString();
                        if (!isAppInstalled(app_id)) {
                            this.serviceDialog.show();
                        }
                    } else {
                        this.serviceDialog.show();
                    }
                }
            }
        }


    }

    public void serviceDialog() {

        if (adsPrefernce.appAdDialogCount() != 0) {
            if (String.valueOf(adsPrefernce.appAdDialogCount()).contains(String.valueOf(currentAD)) && !serviceDialog.isShowing()) {
                this.serviceDialog.setCancelable(false);
                this.serviceDialog.setContentView(R.layout.dialog_service);
                Objects.requireNonNull(this.serviceDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                LinearLayout lay_updateApp = this.serviceDialog.findViewById(R.id.lay_updateApp);
                LinearLayout lay_message = this.serviceDialog.findViewById(R.id.lay_message);
                LinearLayout lay_ads = this.serviceDialog.findViewById(R.id.lay_ads);

                ImageView iv_ad_icon_title = this.serviceDialog.findViewById(R.id.iv_ad_icon_title);
                TextView tv_dialog_title = this.serviceDialog.findViewById(R.id.tv_dialog_title);

                //ads
                TextView tv_ad_message = this.serviceDialog.findViewById(R.id.tv_ad_message);
                ImageView iv_ad_banner = this.serviceDialog.findViewById(R.id.iv_ad_banner);
                ImageView iv_app_icon = this.serviceDialog.findViewById(R.id.iv_app_icon);
                TextView tv_app_name = this.serviceDialog.findViewById(R.id.tv_app_name);
                TextView tv_app_shortdesc = this.serviceDialog.findViewById(R.id.tv_app_shortdesc);
                TextView tv_app_download = this.serviceDialog.findViewById(R.id.tv_app_download);
                TextView tv_app_cancel = this.serviceDialog.findViewById(R.id.tv_app_cancel);


                iv_ad_icon_title.setVisibility(View.VISIBLE);
                lay_updateApp.setVisibility(View.GONE);
                lay_message.setVisibility(View.GONE);
                lay_ads.setVisibility(View.VISIBLE);
                tv_dialog_title.setText(adsPrefernce.adDialogTitle());
                tv_app_download.setText(adsPrefernce.adButtonText());

                if (adsPrefernce.adShowCancel()) {
                    tv_app_cancel.setVisibility(View.VISIBLE);
                } else {
                    tv_app_cancel.setVisibility(View.GONE);
                }

                tv_ad_message.setText(adsPrefernce.adMessage());
                if (!this.isFinishing() && !this.isDestroyed()) {
                    Glide.with(this).load(adsPrefernce.adBannerUrl()).into(iv_ad_banner);
                    Glide.with(this).load(adsPrefernce.adIconUrl()).into(iv_app_icon);
                }
                tv_app_name.setText(adsPrefernce.adAppName());
                tv_app_shortdesc.setText(adsPrefernce.adShortDesc());

                tv_app_download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(adsPrefernce.adAppUrl()));
                        startActivity(intent);
                    }
                });

                tv_app_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        serviceDialog.dismiss();
                    }
                });

                if (adsPrefernce.adAppUrl().contains("play.google.com")) {
                    String link = adsPrefernce.adAppUrl();
                    String[] s1 = link.split("id=");
                    String[] s2 = s1[1].split("&");
                    String app_id = s2[0].toString();
                    if (!isAppInstalled(app_id)) {
                        this.serviceDialog.show();
                    }
                } else {
                    this.serviceDialog.show();
                }
            }

        }


    }

    public boolean isAppInstalled(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public void loadInterstitial1() {
        if (isConnected(this) && adsPrefernce.showInter1() && mInterstitialAd1 == null) {
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
            InterstitialAd.load(this, adsPrefernce.gInter1(), adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    mInterstitialAd1 = interstitialAd;
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    mInterstitialAd1 = null;
                }
            });

        }
    }

    public void loadInterstitial2() {
        if (isConnected(this) && adsPrefernce.showInter2() && mInterstitialAd2 == null) {
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
            InterstitialAd.load(this, adsPrefernce.gInter2(), adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    mInterstitialAd2 = interstitialAd;
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    mInterstitialAd2 = null;
                }
            });
        }


    }

    public void loadInterstitial3() {
        if (isConnected(this) && adsPrefernce.showInter3() && mInterstitialAd3 == null) {
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
            InterstitialAd.load(this, adsPrefernce.gInter3(), adRequest, new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    mInterstitialAd3 = interstitialAd;
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    mInterstitialAd3 = null;
                }
            });
        }
    }

    public void loadInterstitial1FB() {
        if (isConnected(this) && adsPrefernce.showInter1_fb()) {
            if (interstitialAd1 == null) {
                interstitialAd1 = new com.facebook.ads.InterstitialAd(this, adsPrefernce.gInter1_fb());
                InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                    @Override
                    public void onInterstitialDisplayed(Ad ad) {

                    }

                    @Override
                    public void onInterstitialDismissed(Ad ad) {
                        loadInterstitial1FB();
                    }

                    @Override
                    public void onError(Ad ad, com.facebook.ads.AdError adError) {
                        interstitialAd1 = null;
                    }

                    @Override
                    public void onAdLoaded(Ad ad) {
                    }

                    @Override
                    public void onAdClicked(Ad ad) {

                    }

                    @Override
                    public void onLoggingImpression(Ad ad) {

                    }
                };
                interstitialAd1.loadAd(
                        interstitialAd1.buildLoadAdConfig()
                                .withAdListener(interstitialAdListener)
                                .build());
            }
        }
    }

    public void loadInterstitial2FB() {
        if (isConnected(this) && adsPrefernce.showInter2_fb() && interstitialAd2 == null) {
            interstitialAd2 = new com.facebook.ads.InterstitialAd(this, adsPrefernce.gInter2_fb());
            InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {

                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    loadInterstitial2FB();
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    interstitialAd2 = null;

                }

                @Override
                public void onAdLoaded(Ad ad) {
                }

                @Override
                public void onAdClicked(Ad ad) {

                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            };
            interstitialAd2.loadAd(
                    interstitialAd2.buildLoadAdConfig()
                            .withAdListener(interstitialAdListener)
                            .build());
        }
    }

    public void loadInterstitial3FB() {
        if (isConnected(this) && adsPrefernce.showInter3_fb() && interstitialAd3 == null) {
            interstitialAd3 = new com.facebook.ads.InterstitialAd(this, adsPrefernce.gInter3_fb());
            InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {

                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    loadInterstitial3FB();
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    interstitialAd3 = null;

                }

                @Override
                public void onAdLoaded(Ad ad) {
                }

                @Override
                public void onAdClicked(Ad ad) {

                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            };
            interstitialAd3.loadAd(
                    interstitialAd3.buildLoadAdConfig()
                            .withAdListener(interstitialAdListener)
                            .build());
        }
    }

    public void showBannerAd() {
        if (bannerNo == 1) {
            showBanner1();
        } else if (bannerNo == 2) {
            showBanner2();
        } else if (bannerNo == 3) {
            showBanner3();
        }
        setBannerNo();

    }

    public void showBannerAd(View bannerView) {
        if (bannerNo == 1) {
            showBanner1(bannerView);
        } else if (bannerNo == 2) {
            showBanner2(bannerView);
        } else if (bannerNo == 3) {
            showBanner3(bannerView);
        }
        setBannerNo();
    }

    public void showLargeBannerAd() {
        if (bannerNo == 1) {
            showLargeBanner1();
        } else if (bannerNo == 2) {
            showLargeBanner2();
        } else if (bannerNo == 3) {
            showLargeBanner3();
        }
        setBannerNo();
    }

    public void showLargeBannerAd(View largeBannerView) {
        if (bannerNo == 1) {
            showLargeBanner1(largeBannerView);
        } else if (bannerNo == 2) {
            showLargeBanner2(largeBannerView);
        } else if (bannerNo == 3) {
            showLargeBanner3(largeBannerView);
        }
        setBannerNo();
    }


    public void showNativeAd(View nativeView) {
        if (adsPrefernce.showRewardInter3()) {
            if (nativeNo == 1) {
                showNativeAd1(nativeView);
            } else if (nativeNo == 2) {
                showNativeAd2(nativeView);
            } else if (nativeNo == 3) {
                showNativeAd3(nativeView);
            }
        } else {
            showNativeAdBeta(nativeView);
        }
        setNativeNo();

    }

    void setInterNo() {
        if (adsPrefernce.adCount() == 3 && adsPrefernce.showInter1() && adsPrefernce.showInter2() && adsPrefernce.showInter3()) {
            if (currentAD % adsPrefernce.adCount() == 0) {
                if (interNo == 3) {
                    interNo = 1;
                } else {
                    interNo++;
                }
            }
        } else {
            if (interNo == 3) {
                interNo = 1;
            } else {
                interNo++;
            }
        }

    }

    void setBannerNo() {
        if (bannerNo == 3) {
            bannerNo = 1;
        } else {
            bannerNo++;
        }
    }

    void setNativeNo() {
        if (nativeNo == 3) {
            nativeNo = 1;
        } else {
            nativeNo++;
        }
    }

    void setRewardNo() {
        if (rewardNo == 3) {
            rewardNo = 1;
        } else {
            rewardNo++;
        }
    }

    public static RewardedAd gRewardedAd1 = null;
    public static RewardedAd gRewardedAd2 = null;
    public static RewardedAd gRewardedAd3 = null;
    public static boolean isUserRewarded1 = false;
    public static boolean isUserRewarded2 = false;
    public static boolean isUserRewarded3 = false;

    public void showRewardedAd(OnRewardAdClosedListener onRewardAdClosedListener) {
        if (rewardNo == 1) {
            showRewardAd1(onRewardAdClosedListener);
        } else if (rewardNo == 2) {
            showRewardAd2(onRewardAdClosedListener);
        } else if (rewardNo == 3) {
            showRewardAd3(onRewardAdClosedListener);
        } else {
            onRewardAdClosedListener.onRewardAdNotShown();
        }
        setRewardNo();
    }

    public void loadRewardAd1() {
        if (isConnected(this) && adsPrefernce.showRewarded1() && gRewardedAd1 == null) {
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(this, adsPrefernce.gRewarded1(),
                    adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            gRewardedAd1 = null;
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            gRewardedAd1 = rewardedAd;
                        }
                    });

        }

    }

    public void loadRewardAd2() {
        if (isConnected(this) && adsPrefernce.showRewarded2() && gRewardedAd2 == null) {
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(this, adsPrefernce.gRewarded2(),
                    adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            gRewardedAd2 = null;
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            gRewardedAd2 = rewardedAd;
                        }
                    });

        }

    }

    public void loadRewardAd3() {
        if (isConnected(this) && adsPrefernce.showRewarded3() && gRewardedAd3 == null) {
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(this, adsPrefernce.gRewarded3(),
                    adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            gRewardedAd3 = null;
                        }

                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            gRewardedAd3 = rewardedAd;
                        }
                    });

        }

    }

    public void showRewardAd1(OnRewardAdClosedListener onRewardAdClosedListener) {
        if (isConnected(this) && adsPrefernce.showRewarded1() && gRewardedAd1 != null) {
            gRewardedAd1.show(this, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    isUserRewarded1 = true;
                }
            });
            gRewardedAd1.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when ad fails to show.
                    onRewardAdClosedListener.onRewardAdNotShown();
                    gRewardedAd1 = null;
                    isUserRewarded1 = false;
                    loadRewardAd1();
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    if (isUserRewarded1) {
                        onRewardAdClosedListener.onRewardSuccess();
                    } else {
                        onRewardAdClosedListener.onRewardFailed();
                    }
                    gRewardedAd1 = null;
                    isUserRewarded1 = false;
                    loadRewardAd1();
                }
            });

        } else {
            onRewardAdClosedListener.onRewardAdNotShown();
        }
    }

    public void showRewardAd2(OnRewardAdClosedListener onRewardAdClosedListener) {
        if (isConnected(this) && adsPrefernce.showRewarded2() && gRewardedAd2 != null) {
            gRewardedAd2.show(this, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    isUserRewarded2 = true;
                }
            });
            gRewardedAd2.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when ad fails to show.
                    onRewardAdClosedListener.onRewardAdNotShown();
                    gRewardedAd2 = null;
                    isUserRewarded2 = false;
                    loadRewardAd2();
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    if (isUserRewarded2) {
                        onRewardAdClosedListener.onRewardSuccess();
                    } else {
                        onRewardAdClosedListener.onRewardFailed();
                    }
                    gRewardedAd2 = null;
                    isUserRewarded2 = false;
                    loadRewardAd2();
                }
            });

        } else {
            onRewardAdClosedListener.onRewardAdNotShown();
        }
    }

    public void showRewardAd3(OnRewardAdClosedListener onRewardAdClosedListener) {
        if (isConnected(this) && adsPrefernce.showRewarded3() && gRewardedAd3 != null) {
            gRewardedAd3.show(this, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    isUserRewarded3 = true;
                }
            });
            gRewardedAd3.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when ad fails to show.
                    onRewardAdClosedListener.onRewardAdNotShown();
                    gRewardedAd3 = null;
                    isUserRewarded3 = false;
                    loadRewardAd3();
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    if (isUserRewarded3) {
                        onRewardAdClosedListener.onRewardSuccess();
                    } else {
                        onRewardAdClosedListener.onRewardFailed();
                    }
                    gRewardedAd3 = null;
                    isUserRewarded3 = false;
                    loadRewardAd3();
                }
            });

        } else {
            onRewardAdClosedListener.onRewardAdNotShown();
        }
    }


    public void showInterstitialAd(Boolean isBackpressAd, Activity context, Callable<Void> callable) {
        if (adsPrefernce.showRewardInter2()) {
            if (interNo == 1) {
                showInterstitial1Back(context, callable);
            } else if (interNo == 2) {
                showInterstitial2Back(context, callable);
            } else if (interNo == 3) {
                showInterstitial3Back(context, callable);
            } else {
                try {
                    callable.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            setInterNo();
        } else {
            try {
                callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void showInterstitialAd(Activity context, Callable<Void> callable) {
        if (interNo == 1) {
            showInterstitial1(context, callable);
        } else if (interNo == 2) {
            showInterstitial2(context, callable);
        } else if (interNo == 3) {
            showInterstitial3(context, callable);
        } else {
            try {
                callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setInterNo();

    }

    void showInterstitialAdFB(Activity context, Callable<Void> callable) {
        if (interNo == 1) {
            showInterstitial1FB(context, callable);
        } else if (interNo == 2) {
            showInterstitial2FB(context, callable);
        } else if (interNo == 3) {
            showInterstitial3FB(context, callable);
        } else {
            try {
                callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setInterNo();

    }

    public void showSplashInterstitial1(Activity context, Callable<Void> params) {
        if (adsPrefernce.allowAccess()) {
            if (currentAD % adsPrefernce.adCount() == 0 && isConnected(this) && adsPrefernce.showInter1()) {
                if (mInterstitialAd1 != null) {
                    mInterstitialAd1.show((Activity) context);
                    mInterstitialAd1.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            loadInterstitial1();
                            try {
                                params.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            mInterstitialAd1 = null;
                            try {
                                params.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            mInterstitialAd1 = null;
                        }
                    });
                } else {
                    showInhouseInterAd(context,new InhouseInterstitialListener() {
                        @Override
                        public void onAdShown() {

                        }

                        @Override
                        public void onAdDismissed() {
                            try {
                                params.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } else {
                try {
                    params.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                params.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        currentAD++;
    }

    public void showInterstitial1(Activity context, Callable<Void> params) {
        if (currentAD % adsPrefernce.adCount() == 0 && isConnected(this) && adsPrefernce.showInter1()) {
            if (mInterstitialAd1 != null) {
                if (adsPrefernce.showloading()) {
                    withDelay(context,ConstantAds.AD_DELAY, ConstantAds.AD_MESSAGE, new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            mInterstitialAd1.show((Activity) context);
                            mInterstitialAd1.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    loadInterstitial1();
                                    try {
                                        params.call();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(AdError adError) {
                                    mInterstitialAd1 = null;
                                    showInterstitial1FB(context, params);
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    mInterstitialAd1 = null;
                                }
                            });
                            return null;
                        }
                    });
                } else {
                    mInterstitialAd1.show((Activity) context);
                    mInterstitialAd1.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            loadInterstitial1();
                            try {
                                params.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            mInterstitialAd1 = null;
                            showInterstitial1FB(context, params);
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            mInterstitialAd1 = null;
                        }
                    });
                }

            } else {
                showInterstitial1FB(context, params);
            }
        } else {
            try {
                params.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        currentAD++;
    }

    void showInterstitial1Splash(Activity context, Callable<Void> params) {
        if (mInterstitialAd1 != null) {
            mInterstitialAd1.show((Activity) context);
            mInterstitialAd1.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    loadInterstitial1();
                    try {
                        params.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    mInterstitialAd1 = null;
                    try {
                        params.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    mInterstitialAd1 = null;
                }
            });
        } else {
            showInterstitial1FB(context, params);
        }
    }


    public void showInterstitial2(Activity context, Callable<Void> params) {
        if (currentAD % adsPrefernce.adCount() == 0 && isConnected(this) && adsPrefernce.showInter2()) {
            if (mInterstitialAd2 != null) {
                if (adsPrefernce.showloading()) {
                    withDelay(context,ConstantAds.AD_DELAY, ConstantAds.AD_MESSAGE, new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            mInterstitialAd2.show((Activity) context);
                            mInterstitialAd2.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    loadInterstitial2();
                                    try {
                                        params.call();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(AdError adError) {
                                    mInterstitialAd1 = null;
                                    showInterstitial2FB(context, params);
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    mInterstitialAd2 = null;
                                }
                            });
                            return null;
                        }
                    });
                } else {
                    mInterstitialAd2.show((Activity) context);
                    mInterstitialAd2.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            loadInterstitial2();
                            try {
                                params.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            mInterstitialAd1 = null;
                            showInterstitial2FB(context, params);
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            mInterstitialAd2 = null;
                        }
                    });
                }

            } else {
                showInterstitial2FB(context, params);
            }
        } else {
            try {
                params.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        currentAD++;

    }

    public void showInterstitial3(Activity context, Callable<Void> params) {
        if (currentAD % adsPrefernce.adCount() == 0 && isConnected(this) && adsPrefernce.showInter3()) {
            if (mInterstitialAd3 != null) {
                if (adsPrefernce.showloading()) {
                    withDelay(context,ConstantAds.AD_DELAY, ConstantAds.AD_MESSAGE, new Callable<Void>() {
                        @Override
                        public Void call() throws Exception {
                            mInterstitialAd3.show((Activity) context);
                            mInterstitialAd3.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    loadInterstitial3();
                                    try {
                                        params.call();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(AdError adError) {
                                    mInterstitialAd1 = null;
                                    showInterstitial3FB(context, params);
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    mInterstitialAd3 = null;
                                }
                            });

                            return null;
                        }
                    });
                } else {
                    mInterstitialAd3.show((Activity) context);
                    mInterstitialAd3.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            loadInterstitial3();
                            try {
                                params.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            mInterstitialAd1 = null;
                            showInterstitial3FB(context, params);
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            mInterstitialAd3 = null;
                        }
                    });

                }
            } else {
                showInterstitial3FB(context, params);
            }
        } else {
            try {
                params.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        currentAD++;

    }

    void showInterstitial1FB(Activity context, Callable<Void> params) {
        if (adsPrefernce.showInter1_fb()) {
            if (interstitialAd1 != null && interstitialAd1.isAdLoaded() && !interstitialAd1.isAdInvalidated()) {
                try {
                    params.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                interstitialAd1.show();
                interstitialAd1 = null;
            } else {
                showInhouseInterAd(context,     new InhouseInterstitialListener() {
                    @Override
                    public void onAdShown() {

                    }

                    @Override
                    public void onAdDismissed() {
                        try {
                            params.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } else {
            showInhouseInterAd(context,new InhouseInterstitialListener() {
                @Override
                public void onAdShown() {

                }

                @Override
                public void onAdDismissed() {
                    try {
                        params.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    void showInterstitial1FBSplash(Activity context, Callable<Void> params) {
        if (isConnected(this)) {
            if (adsPrefernce.showInter1_fb()) {
                if (interstitialAd1 == null){
                    interstitialAd1 = new com.facebook.ads.InterstitialAd(this, adsPrefernce.gInter1_fb());
                    InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                        @Override
                        public void onInterstitialDisplayed(Ad ad) {

                        }

                        @Override
                        public void onInterstitialDismissed(Ad ad) {
                            loadInterstitial1FB();
                            try {
                                params.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Ad ad, com.facebook.ads.AdError adError) {
                            interstitialAd1 = null;
                            try {
                                params.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onAdLoaded(Ad ad) {
                            if (interstitialAd1 != null && interstitialAd1.isAdLoaded() && !interstitialAd1.isAdInvalidated()) {
                                try {
                                    params.call();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                interstitialAd1.show();
                                interstitialAd1 = null;
                            } else {
                                showInhouseInterAd(context,new InhouseInterstitialListener() {
                                    @Override
                                    public void onAdShown() {

                                    }

                                    @Override
                                    public void onAdDismissed() {
                                        try {
                                            params.call();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onAdClicked(Ad ad) {

                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {

                        }
                    };
                    interstitialAd1.loadAd(
                            interstitialAd1.buildLoadAdConfig()
                                    .withAdListener(interstitialAdListener)
                                    .build());
                }else {
                    if (interstitialAd1.isAdLoaded() && !interstitialAd1.isAdInvalidated()) {
                        try {
                            params.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        interstitialAd1.show();
                        interstitialAd1 = null;
                    } else {
                        showInhouseInterAd(context,new InhouseInterstitialListener() {
                            @Override
                            public void onAdShown() {

                            }

                            @Override
                            public void onAdDismissed() {
                                try {
                                    params.call();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            }
            else {
                showInhouseInterAd(context,new InhouseInterstitialListener() {
                    @Override
                    public void onAdShown() {

                    }

                    @Override
                    public void onAdDismissed() {
                        try {
                            params.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } else {
            try {
                params.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void showInterstitial2FB(Activity context, Callable<Void> params) {
        if (adsPrefernce.showInter2_fb()) {
            if (interstitialAd2 != null && interstitialAd2.isAdLoaded() && !interstitialAd2.isAdInvalidated()) {
                try {
                    params.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                interstitialAd2.show();
                interstitialAd2 = null;
            } else {
                showInhouseInterAd(context,new InhouseInterstitialListener() {
                    @Override
                    public void onAdShown() {

                    }

                    @Override
                    public void onAdDismissed() {
                        try {
                            params.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } else {
            try {
                params.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void showInterstitial3FB(Activity context, Callable<Void> params) {
        if (adsPrefernce.showInter3_fb()) {
            if (interstitialAd3 != null && interstitialAd3.isAdLoaded() && !interstitialAd3.isAdInvalidated()) {
                try {
                    params.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                interstitialAd3.show();
                interstitialAd3 = null;
            } else {
                showInhouseInterAd(context,new InhouseInterstitialListener() {
                    @Override
                    public void onAdShown() {

                    }

                    @Override
                    public void onAdDismissed() {
                        try {
                            params.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } else {
            try {
                params.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void showInterstitial1Back(Activity context, Callable<Void> params) {
        if (currentAD % adsPrefernce.adCount() == 0 && isConnected(this)) {
            if (mInterstitialAd1 != null && adsPrefernce.showInter1()) {
                mInterstitialAd1.show((Activity) context);
                mInterstitialAd1.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        loadInterstitial1();
                        try {
                            params.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        mInterstitialAd1 = null;
                        showInterstitial1FB(context, params);
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        mInterstitialAd1 = null;
                    }
                });
            } else {
                showInterstitial1FB(context, params);
            }
        } else {
            try {
                params.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        currentAD++;
    }

    void showInterstitial2Back(Activity context, Callable<Void> params) {
        if (currentAD % adsPrefernce.adCount() == 0 && isConnected(this)) {
            if (mInterstitialAd2 != null && adsPrefernce.showInter2()) {
                mInterstitialAd2.show((Activity) context);
                mInterstitialAd2.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        loadInterstitial2();
                        try {
                            params.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        mInterstitialAd1 = null;
                        showInterstitial2FB(context, params);
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        mInterstitialAd2 = null;
                    }
                });
            } else {
                showInterstitial2FB(context, params);
            }
        } else {
            try {
                params.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        currentAD++;
    }

    void showInterstitial3Back(Activity context, Callable<Void> params) {
        if (currentAD % adsPrefernce.adCount() == 0 && isConnected(this)) {
            if (mInterstitialAd3 != null && adsPrefernce.showInter3()) {
                mInterstitialAd3.show((Activity) context);
                mInterstitialAd3.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        loadInterstitial3();
                        try {
                            params.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        mInterstitialAd1 = null;
                        showInterstitial3FB(context, params);
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        mInterstitialAd3 = null;
                    }
                });
            } else {
                showInterstitial3FB(context, params);
            }
        } else {
            try {
                params.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        currentAD++;
    }


    void hideInhouseBanner() {
        RelativeLayout lay_banner_ad = findViewById(R.id.lay_banner_ad);
        lay_banner_ad.setVisibility(View.GONE);
    }

    void hideInhouseNative() {
        CardView cardView = findViewById(R.id.native_ad_container);
        cardView.setVisibility(View.GONE);
    }

    void hideInhouseNativeDialog(Dialog dialog) {
        CardView cardView = dialog.findViewById(R.id.native_ad_container);
        cardView.setVisibility(View.GONE);
    }

    void hideInhouseNative2() {
        CardView cardView = findViewById(R.id.native_ad_container2);
        cardView.setVisibility(View.GONE);
    }

    void hideInhouseNative3() {
        CardView cardView = findViewById(R.id.native_ad_container3);
        cardView.setVisibility(View.GONE);
    }

    void hideInhouseNativeAdapter(CardView cardView) {
//        CardView cardView = findViewById(R.id.native_ad_container);
        cardView.setVisibility(View.GONE);

    }

    public void showInhouseNativeAd(Boolean isSmall, CardView cardView, InhouseNativeListener
            inhouseNativeListener) {
        try {
            if (adsPrefernce.isInHouseAdLoaded()) {
                if (adsPrefernce.getInHouseAds().size() != 0) {
                    cardView.setVisibility(View.VISIBLE);
                    inflateNativeAdInHouse(isSmall, cardView);
                    inhouseNativeListener.onAdLoaded();
                    cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
                    cardView.setBackgroundColor(getResources().getColor(R.color.white));
                } else {
                    cardView.setVisibility(View.GONE);
                    inhouseNativeListener.onAdShowFailed();
                }
            } else {
                cardView.setVisibility(View.GONE);
                inhouseNativeListener.onAdShowFailed();
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            cardView.setVisibility(View.GONE);
            inhouseNativeListener.onAdShowFailed();
        }

    }


    void showBanner1() {
        if (isConnected(this) && adsPrefernce.showBanner1()) {
            LinearLayout adContainer = (LinearLayout) this.findViewById(R.id.banner_adView);
            AdView mAdView = new AdView(this);
            mAdView.setAdUnitId(adsPrefernce.gBanner1());
            hideInhouseBanner();
            adContainer.removeAllViews();
            adContainer.addView(mAdView);
            AdRequest adRequest = new AdRequest.Builder().build();
            com.google.android.gms.ads.AdSize adSize = getAdSize();
            mAdView.setAdSize(adSize);
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    mAdView.destroy();
                    showBanner1FB();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    hideInhouseBanner();
                }
            });
        }
    }
    void showBanner2() {
        if (isConnected(this) && adsPrefernce.showBanner2()) {
            LinearLayout adContainer = (LinearLayout) this.findViewById(R.id.banner_adView);
            AdView mAdView = new AdView(this);
            mAdView.setAdUnitId(adsPrefernce.gBanner2());
            hideInhouseBanner();
            adContainer.removeAllViews();
            adContainer.addView(mAdView);
            AdRequest adRequest = new AdRequest.Builder().build();
            com.google.android.gms.ads.AdSize adSize = getAdSize();
            mAdView.setAdSize(adSize);
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    mAdView.destroy();
                    showBanner2FB();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    hideInhouseBanner();
                }
            });

        }
    }
    void showBanner3() {
        if (isConnected(this) && adsPrefernce.showBanner3()) {
            LinearLayout adContainer = (LinearLayout) this.findViewById(R.id.banner_adView);
            AdView mAdView = new AdView(this);
            mAdView.setAdUnitId(adsPrefernce.gBanner3());
            hideInhouseBanner();
            adContainer.removeAllViews();
            adContainer.addView(mAdView);
            AdRequest adRequest = new AdRequest.Builder().build();
            com.google.android.gms.ads.AdSize adSize = getAdSize();
            mAdView.setAdSize(adSize);
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    mAdView.destroy();
                    showBanner3FB();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    hideInhouseBanner();
                }
            });

        }
    }

    void showBanner1FB() {
        com.facebook.ads.AdView adView = null;
        if (isConnected(this) && adsPrefernce.showBanner1_fb()) {
            com.facebook.ads.AdView finalAdView = adView;
            com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    if (finalAdView != null) {
                        finalAdView.destroy();
                    }
                    showInhouseBannerAd(new InhouseBannerListener() {
                        @Override
                        public void onAdLoaded() {
                        }

                        @Override
                        public void onAdShowFailed() {
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    hideInhouseBanner();
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                }
            };

            adView = new com.facebook.ads.AdView(this, adsPrefernce.gBanner1_fb(), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_adView);
            adContainer.addView(adView);
            adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
            hideInhouseBanner();
            adContainer.removeAllViews();
            adContainer.addView(adView);
        }
    }

    void showBanner2FB() {
        com.facebook.ads.AdView adView = null;
        if (isConnected(this) && adsPrefernce.showBanner2_fb()) {
            com.facebook.ads.AdView finalAdView = adView;
            com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    if (finalAdView != null) {
                        finalAdView.destroy();
                    }
                    showInhouseBannerAd(new InhouseBannerListener() {
                        @Override
                        public void onAdLoaded() {
                        }

                        @Override
                        public void onAdShowFailed() {
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    hideInhouseBanner();
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                }
            };

            adView = new com.facebook.ads.AdView(this, adsPrefernce.gBanner2_fb(), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_adView);
            adContainer.addView(adView);
            adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
            hideInhouseBanner();
            adContainer.removeAllViews();
            adContainer.addView(adView);
        }
    }

    void showBanner3FB() {
        com.facebook.ads.AdView adView = null;
        if (isConnected(this) && adsPrefernce.showBanner3_fb()) {
            com.facebook.ads.AdView finalAdView = adView;
            com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    if (finalAdView != null) {
                        finalAdView.destroy();
                    }
                    showInhouseBannerAd(new InhouseBannerListener() {
                        @Override
                        public void onAdLoaded() {
                        }

                        @Override
                        public void onAdShowFailed() {
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    hideInhouseBanner();
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                }
            };

            adView = new com.facebook.ads.AdView(this, adsPrefernce.gBanner3_fb(), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_adView);
            adContainer.addView(adView);
            adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
            hideInhouseBanner();
            adContainer.removeAllViews();
            adContainer.addView(adView);
        }
    }

    void showBanner1(View bannerView) {
        if (isConnected(this) && adsPrefernce.showBanner1()) {
            bannerView.setVisibility(View.VISIBLE);
            LinearLayout adContainer = (LinearLayout) this.findViewById(R.id.banner_adView);
            AdView mAdView = new AdView(this);
            mAdView.setAdUnitId(adsPrefernce.gBanner1());
            hideInhouseBanner();
            adContainer.removeAllViews();
            adContainer.addView(mAdView);
            AdRequest adRequest = new AdRequest.Builder().build();
            com.google.android.gms.ads.AdSize adSize = getAdSize();
            mAdView.setAdSize(adSize);
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    mAdView.destroy();
                    showBanner1FB(bannerView);
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    hideInhouseBanner();
                    bannerView.setVisibility(View.VISIBLE);
                }
            });
        } else {
            showBanner1FB(bannerView);
        }
    }

    void showBanner2(View bannerView) {
        if (isConnected(this) && adsPrefernce.showBanner2()) {
            bannerView.setVisibility(View.VISIBLE);
            LinearLayout adContainer = (LinearLayout) this.findViewById(R.id.banner_adView);
            AdView mAdView = new AdView(this);
            mAdView.setAdUnitId(adsPrefernce.gBanner2());
            hideInhouseBanner();
            adContainer.removeAllViews();
            adContainer.addView(mAdView);
            AdRequest adRequest = new AdRequest.Builder().build();
            com.google.android.gms.ads.AdSize adSize = getAdSize();
            mAdView.setAdSize(adSize);
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    mAdView.destroy();
                    showBanner2FB(bannerView);
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    hideInhouseBanner();
                    bannerView.setVisibility(View.VISIBLE);
                }
            });

        } else {
            showBanner2FB(bannerView);
        }
    }

    void showBanner3(View bannerView) {
        if (isConnected(this) && adsPrefernce.showBanner3()) {
            bannerView.setVisibility(View.VISIBLE);
            LinearLayout adContainer = (LinearLayout) this.findViewById(R.id.banner_adView);
            AdView mAdView = new AdView(this);
            mAdView.setAdUnitId(adsPrefernce.gBanner3());
            hideInhouseBanner();
            adContainer.removeAllViews();
            adContainer.addView(mAdView);
            AdRequest adRequest = new AdRequest.Builder().build();
            com.google.android.gms.ads.AdSize adSize = getAdSize();
            mAdView.setAdSize(adSize);
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    mAdView.destroy();
                    showBanner3FB(bannerView);
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    hideInhouseBanner();
                    bannerView.setVisibility(View.VISIBLE);
                }
            });

        } else {
            showBanner3FB(bannerView);

        }
    }

    void showBanner1FB(View bannerView) {
        com.facebook.ads.AdView adView = null;
        if (isConnected(this) && adsPrefernce.showBanner1_fb()) {
            bannerView.setVisibility(View.VISIBLE);
            com.facebook.ads.AdView finalAdView = adView;
            com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    if (finalAdView != null) {
                        finalAdView.destroy();
                    }
                    showInhouseBannerAd(new InhouseBannerListener() {
                        @Override
                        public void onAdLoaded() {
                            bannerView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdShowFailed() {
                            bannerView.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    hideInhouseBanner();
                    bannerView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                }
            };

            adView = new com.facebook.ads.AdView(this, adsPrefernce.gBanner1_fb(), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_adView);
            adContainer.addView(adView);
            adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
            hideInhouseBanner();
            adContainer.removeAllViews();
            adContainer.addView(adView);
        } else {
            bannerView.setVisibility(View.GONE);
        }
    }

    void showBanner2FB(View bannerView) {
        com.facebook.ads.AdView adView = null;
        if (isConnected(this) && adsPrefernce.showBanner2_fb()) {
            bannerView.setVisibility(View.VISIBLE);
            com.facebook.ads.AdView finalAdView = adView;
            com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    if (finalAdView != null) {
                        finalAdView.destroy();
                    }
                    showInhouseBannerAd(new InhouseBannerListener() {
                        @Override
                        public void onAdLoaded() {
                            bannerView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdShowFailed() {
                            bannerView.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    hideInhouseBanner();
                    bannerView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                }
            };

            adView = new com.facebook.ads.AdView(this, adsPrefernce.gBanner2_fb(), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_adView);
            adContainer.addView(adView);
            adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
            hideInhouseBanner();
            adContainer.removeAllViews();
            adContainer.addView(adView);
        } else {
            bannerView.setVisibility(View.GONE);
        }
    }

    void showBanner3FB(View bannerView) {
        com.facebook.ads.AdView adView = null;
        if (isConnected(this) && adsPrefernce.showBanner3_fb()) {
            bannerView.setVisibility(View.VISIBLE);
            com.facebook.ads.AdView finalAdView = adView;
            com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    if (finalAdView != null) {
                        finalAdView.destroy();
                    }
                    showInhouseBannerAd(new InhouseBannerListener() {
                        @Override
                        public void onAdLoaded() {
                            bannerView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdShowFailed() {
                            bannerView.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    hideInhouseBanner();
                    bannerView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                }
            };

            adView = new com.facebook.ads.AdView(this, adsPrefernce.gBanner3_fb(), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_adView);
            adContainer.addView(adView);
            adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
            hideInhouseBanner();
            adContainer.removeAllViews();
            adContainer.addView(adView);
        } else {
            bannerView.setVisibility(View.GONE);
        }
    }

    void showLargeBanner1() {
        if (isConnected(this) && adsPrefernce.showBanner1()) {
            LinearLayout adContainer = (LinearLayout) this.findViewById(R.id.banner_adView);
            AdView mAdView = new AdView(this);
            mAdView.setAdUnitId(adsPrefernce.gBanner1());
            hideInhouseBanner();
            adContainer.removeAllViews();
            adContainer.addView(mAdView);
            AdRequest adRequest = new AdRequest.Builder().build();
            com.google.android.gms.ads.AdSize adSize = AdSize.LARGE_BANNER;
            mAdView.setAdSize(adSize);
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    mAdView.destroy();
                    showLargeBanner1FB();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    hideInhouseBanner();
                }
            });

        }
    }

    void showLargeBanner2() {
        if (isConnected(this) && adsPrefernce.showBanner2()) {
            LinearLayout adContainer = (LinearLayout) this.findViewById(R.id.banner_adView);
            AdView mAdView = new AdView(this);
            mAdView.setAdUnitId(adsPrefernce.gBanner2());
            hideInhouseBanner();
            adContainer.removeAllViews();
            adContainer.addView(mAdView);
            AdRequest adRequest = new AdRequest.Builder().build();
            com.google.android.gms.ads.AdSize adSize = AdSize.LARGE_BANNER;
            mAdView.setAdSize(adSize);
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    mAdView.destroy();
                    showLargeBanner2FB();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    hideInhouseBanner();
                }
            });

        }
    }

    public void showLargeBanner3() {
        if (isConnected(this) && adsPrefernce.showBanner3()) {
            LinearLayout adContainer = (LinearLayout) this.findViewById(R.id.banner_adView);
            AdView mAdView = new AdView(this);
            mAdView.setAdUnitId(adsPrefernce.gBanner3());
            hideInhouseBanner();
            adContainer.removeAllViews();
            adContainer.addView(mAdView);
            AdRequest adRequest = new AdRequest.Builder().build();
            com.google.android.gms.ads.AdSize adSize = AdSize.LARGE_BANNER;
            mAdView.setAdSize(adSize);
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    mAdView.destroy();
                    showLargeBanner3FB();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    hideInhouseBanner();
                }
            });

        }
    }

    void showLargeBanner1FB() {
        com.facebook.ads.AdView adView = null;
        if (isConnected(this) && adsPrefernce.showBanner1_fb()) {
            com.facebook.ads.AdView finalAdView = adView;
            com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    if (finalAdView != null) {
                        finalAdView.destroy();
                    }
                    showInhouseBannerAd(new InhouseBannerListener() {
                        @Override
                        public void onAdLoaded() {
                        }

                        @Override
                        public void onAdShowFailed() {
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    hideInhouseBanner();
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                }
            };

            adView = new com.facebook.ads.AdView(this, adsPrefernce.gBanner1_fb(), com.facebook.ads.AdSize.BANNER_HEIGHT_90);
            LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_adView);
            adContainer.addView(adView);
            adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
            hideInhouseBanner();
            adContainer.removeAllViews();
            adContainer.addView(adView);
        }
    }

    void showLargeBanner2FB() {
        com.facebook.ads.AdView adView = null;
        if (isConnected(this) && adsPrefernce.showBanner2_fb()) {
            com.facebook.ads.AdView finalAdView = adView;
            com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    if (finalAdView != null) {
                        finalAdView.destroy();
                    }
                    showInhouseBannerAd(new InhouseBannerListener() {
                        @Override
                        public void onAdLoaded() {
                        }

                        @Override
                        public void onAdShowFailed() {
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    hideInhouseBanner();
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                }
            };

            adView = new com.facebook.ads.AdView(this, adsPrefernce.gBanner2_fb(), com.facebook.ads.AdSize.BANNER_HEIGHT_90);
            LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_adView);
            adContainer.addView(adView);
            adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
            hideInhouseBanner();
            adContainer.removeAllViews();
            adContainer.addView(adView);
        }
    }

    void showLargeBanner3FB() {
        com.facebook.ads.AdView adView = null;
        if (isConnected(this) && adsPrefernce.showBanner3_fb()) {
            com.facebook.ads.AdView finalAdView = adView;
            com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    if (finalAdView != null) {
                        finalAdView.destroy();
                    }
                    showInhouseBannerAd(new InhouseBannerListener() {
                        @Override
                        public void onAdLoaded() {
                        }

                        @Override
                        public void onAdShowFailed() {
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    hideInhouseBanner();
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                }
            };

            adView = new com.facebook.ads.AdView(this, adsPrefernce.gBanner3_fb(), com.facebook.ads.AdSize.BANNER_HEIGHT_90);
            LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_adView);
            adContainer.addView(adView);
            adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
            hideInhouseBanner();
            adContainer.removeAllViews();
            adContainer.addView(adView);
        }
    }


    void showLargeBanner1(View largeBannerView) {
        if (isConnected(this) && adsPrefernce.showBanner1()) {
            largeBannerView.setVisibility(View.VISIBLE);
            LinearLayout adContainer = (LinearLayout) this.findViewById(R.id.banner_adView);
            AdView mAdView = new AdView(this);
            mAdView.setAdUnitId(adsPrefernce.gBanner1());
            hideInhouseBanner();
            adContainer.removeAllViews();
            adContainer.addView(mAdView);
            AdRequest adRequest = new AdRequest.Builder().build();
            com.google.android.gms.ads.AdSize adSize = AdSize.LARGE_BANNER;
            mAdView.setAdSize(adSize);
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    mAdView.destroy();
                    showLargeBanner1FB(largeBannerView);
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    hideInhouseBanner();
                    largeBannerView.setVisibility(View.VISIBLE);
                }
            });

        } else {
            showLargeBanner1FB(largeBannerView);
        }

    }

    void showLargeBanner2(View largeBannerView) {
        if (isConnected(this) && adsPrefernce.showBanner2()) {
            largeBannerView.setVisibility(View.VISIBLE);
            LinearLayout adContainer = (LinearLayout) this.findViewById(R.id.banner_adView);
            AdView mAdView = new AdView(this);
            mAdView.setAdUnitId(adsPrefernce.gBanner2());
            hideInhouseBanner();
            adContainer.removeAllViews();
            adContainer.addView(mAdView);
            AdRequest adRequest = new AdRequest.Builder().build();
            com.google.android.gms.ads.AdSize adSize = AdSize.LARGE_BANNER;
            mAdView.setAdSize(adSize);
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    mAdView.destroy();
                    showLargeBanner2FB(largeBannerView);
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    hideInhouseBanner();
                    largeBannerView.setVisibility(View.VISIBLE);
                }
            });

        } else {
            showLargeBanner2FB(largeBannerView);

        }
    }

    void showLargeBanner3(View largeBannerView) {
        if (isConnected(this) && adsPrefernce.showBanner3()) {
            largeBannerView.setVisibility(View.VISIBLE);
            LinearLayout adContainer = (LinearLayout) this.findViewById(R.id.banner_adView);
            AdView mAdView = new AdView(this);
            mAdView.setAdUnitId(adsPrefernce.gBanner3());
            hideInhouseBanner();
            adContainer.removeAllViews();
            adContainer.addView(mAdView);
            AdRequest adRequest = new AdRequest.Builder().build();
            com.google.android.gms.ads.AdSize adSize = AdSize.LARGE_BANNER;
            mAdView.setAdSize(adSize);
            mAdView.loadAd(adRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    mAdView.destroy();
                    showLargeBanner3FB(largeBannerView);
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    hideInhouseBanner();
                    largeBannerView.setVisibility(View.VISIBLE);
                }
            });

        } else {
            showLargeBanner3FB(largeBannerView);

        }
    }

    void showLargeBanner1FB(View bannerView) {
        com.facebook.ads.AdView adView = null;
        if (isConnected(this) && adsPrefernce.showBanner1_fb()) {
            bannerView.setVisibility(View.VISIBLE);
            com.facebook.ads.AdView finalAdView = adView;
            com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    if (finalAdView != null) {
                        finalAdView.destroy();
                    }
                    showInhouseBannerAd(new InhouseBannerListener() {
                        @Override
                        public void onAdLoaded() {
                            bannerView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdShowFailed() {
                            bannerView.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    hideInhouseBanner();
                    bannerView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                }
            };

            adView = new com.facebook.ads.AdView(this, adsPrefernce.gBanner1_fb(), com.facebook.ads.AdSize.BANNER_HEIGHT_90);
            LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_adView);
            adContainer.addView(adView);
            adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
            hideInhouseBanner();
            adContainer.removeAllViews();
            adContainer.addView(adView);
        } else {
            bannerView.setVisibility(View.GONE);
        }
    }

    void showLargeBanner2FB(View bannerView) {
        com.facebook.ads.AdView adView = null;
        if (isConnected(this) && adsPrefernce.showBanner2_fb()) {
            bannerView.setVisibility(View.VISIBLE);
            com.facebook.ads.AdView finalAdView = adView;
            com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    if (finalAdView != null) {
                        finalAdView.destroy();
                    }
                    showInhouseBannerAd(new InhouseBannerListener() {
                        @Override
                        public void onAdLoaded() {
                            bannerView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdShowFailed() {
                            bannerView.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    hideInhouseBanner();
                    bannerView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                }
            };

            adView = new com.facebook.ads.AdView(this, adsPrefernce.gBanner2_fb(), com.facebook.ads.AdSize.BANNER_HEIGHT_90);
            LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_adView);
            adContainer.addView(adView);
            adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
            hideInhouseBanner();
            adContainer.removeAllViews();
            adContainer.addView(adView);
        } else {
            bannerView.setVisibility(View.GONE);
        }
    }

    void showLargeBanner3FB(View bannerView) {
        com.facebook.ads.AdView adView = null;
        if (isConnected(this) && adsPrefernce.showBanner3_fb()) {
            bannerView.setVisibility(View.VISIBLE);
            com.facebook.ads.AdView finalAdView = adView;
            com.facebook.ads.AdListener adListener = new com.facebook.ads.AdListener() {
                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    if (finalAdView != null) {
                        finalAdView.destroy();
                    }
                    showInhouseBannerAd(new InhouseBannerListener() {
                        @Override
                        public void onAdLoaded() {
                            bannerView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdShowFailed() {
                            bannerView.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    hideInhouseBanner();
                    bannerView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                }
            };

            adView = new com.facebook.ads.AdView(this, adsPrefernce.gBanner3_fb(), com.facebook.ads.AdSize.BANNER_HEIGHT_90);
            LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_adView);
            adContainer.addView(adView);
            adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build());
            hideInhouseBanner();
            adContainer.removeAllViews();
            adContainer.addView(adView);
        } else {
            bannerView.setVisibility(View.GONE);
        }
    }


    void showNativeAd1FBAdapter(View nativeContainer, RelativeLayout nativeView, CardView cardView) {
        if (adsPrefernce.showNative1_fb()) {
            nativeContainer.setVisibility(View.VISIBLE);
            nativeView.setVisibility(View.VISIBLE);
            final com.facebook.ads.NativeAd nativeAd;
            nativeAd = new com.facebook.ads.NativeAd(this, adsPrefernce.gNative1_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    showInhouseNativeAd(false, cardView, new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                            nativeContainer.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdShowFailed() {
                            nativeContainer.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeAd != ad) {
                        return;
                    }
                    cardView.setVisibility(View.GONE);
                    nativeContainer.setVisibility(View.VISIBLE);
                    nativeView.setVisibility(View.VISIBLE);
                    // Inflate Native Ad into Container
                    inflateNativeAdFacebook(nativeAd, nativeView);
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            };
            nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
        } else {
            nativeContainer.setVisibility(View.GONE);
        }
    }

    void showNativeAd2FBAdapter(View nativeContainer, RelativeLayout nativeView, CardView cardView) {
        if (adsPrefernce.showNative2_fb()) {
            nativeContainer.setVisibility(View.VISIBLE);
            nativeView.setVisibility(View.VISIBLE);
            final com.facebook.ads.NativeAd nativeAd;
            nativeAd = new com.facebook.ads.NativeAd(this, adsPrefernce.gNative2_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    showInhouseNativeAd(false, cardView, new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                            nativeContainer.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdShowFailed() {
                            nativeContainer.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeAd != ad) {
                        return;
                    }
                    cardView.setVisibility(View.GONE);
                    nativeContainer.setVisibility(View.VISIBLE);
                    nativeView.setVisibility(View.VISIBLE);
                    // Inflate Native Ad into Container
                    inflateNativeAdFacebook(nativeAd, nativeView);
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            };
            nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
        } else {
            nativeContainer.setVisibility(View.GONE);
        }
    }

    void showNativeAd3FBAdapter(View nativeContainer, RelativeLayout nativeView, CardView cardView) {
        if (adsPrefernce.showNative3_fb()) {
            nativeContainer.setVisibility(View.VISIBLE);
            nativeView.setVisibility(View.VISIBLE);
            final com.facebook.ads.NativeAd nativeAd;
            nativeAd = new com.facebook.ads.NativeAd(this, adsPrefernce.gNative3_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    showInhouseNativeAd(false, cardView, new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                            nativeContainer.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdShowFailed() {
                            nativeContainer.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeAd != ad) {
                        return;
                    }
                    cardView.setVisibility(View.GONE);
                    nativeContainer.setVisibility(View.VISIBLE);
                    nativeView.setVisibility(View.VISIBLE);
                    // Inflate Native Ad into Container
                    inflateNativeAdFacebook(nativeAd, nativeView);
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            };
            nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
        } else {
            nativeContainer.setVisibility(View.GONE);
        }
    }


    void showNativeBannerAd1FBAdapter(View nativeContainer, RelativeLayout nativeBannerView, CardView cardView) {
        if (adsPrefernce.showAppopen1_fb()) {
            nativeContainer.setVisibility(View.VISIBLE);
            nativeBannerView.setVisibility(View.VISIBLE);
            NativeBannerAd nativeBannerAd;
            nativeBannerAd = new NativeBannerAd(this, adsPrefernce.gAppopen1_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {

                @Override
                public void onMediaDownloaded(Ad ad) {
                    // Native ad finished downloading all assets
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    Log.e("showNativeBanner1", "onError");
                    showInhouseNativeAd(true, cardView, new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                            Log.e("showNativeBanner1", "OnAdLoaded");
                            nativeContainer.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdShowFailed() {
                            Log.e("showNativeBanner1", "onAdShowFailed");
                            nativeContainer.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeBannerAd == null || nativeBannerAd != ad) {
                        return;
                    }
                    cardView.setVisibility(View.GONE);
                    nativeContainer.setVisibility(View.VISIBLE);
                    nativeBannerView.setVisibility(View.VISIBLE);
                    inflateNativeBannerAd(nativeBannerAd, nativeBannerView);
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Native ad clicked
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Native ad impression
                }
            };
            // load the ad
            nativeBannerAd.loadAd(
                    nativeBannerAd.buildLoadAdConfig()
                            .withAdListener(nativeAdListener)
                            .build());
        } else {
            nativeContainer.setVisibility(View.GONE);
        }
    }

    void showNativeBannerAd2FBAdapter(View nativeContainer, RelativeLayout nativeBannerView, CardView cardView) {
        if (adsPrefernce.showAppopen2_fb()) {
            nativeContainer.setVisibility(View.VISIBLE);
            nativeBannerView.setVisibility(View.VISIBLE);
            NativeBannerAd nativeBannerAd;
            nativeBannerAd = new NativeBannerAd(this, adsPrefernce.gAppopen2_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {

                @Override
                public void onMediaDownloaded(Ad ad) {
                    // Native ad finished downloading all assets
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    Log.e("showNativeBanner2", "onError");
                    showInhouseNativeAd(true, cardView, new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                            Log.e("showNativeBanner2", "onAdLoaded");
                            nativeContainer.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdShowFailed() {
                            Log.e("showNativeBanner2", "onAdShowFailed");
                            nativeContainer.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeBannerAd == null || nativeBannerAd != ad) {
                        return;
                    }
                    cardView.setVisibility(View.GONE);
                    nativeContainer.setVisibility(View.VISIBLE);
                    nativeBannerView.setVisibility(View.VISIBLE);
                    inflateNativeBannerAd(nativeBannerAd, nativeBannerView);
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Native ad clicked
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Native ad impression
                }
            };
            // load the ad
            nativeBannerAd.loadAd(
                    nativeBannerAd.buildLoadAdConfig()
                            .withAdListener(nativeAdListener)
                            .build());
        } else {
            nativeContainer.setVisibility(View.GONE);
        }
    }

    void showNativeBannerAd3FBAdapter(View nativeContainer, RelativeLayout nativeBannerView, CardView cardView) {
        if (adsPrefernce.showAppopen3_fb()) {
            nativeContainer.setVisibility(View.VISIBLE);
            nativeBannerView.setVisibility(View.VISIBLE);
            NativeBannerAd nativeBannerAd;
            nativeBannerAd = new NativeBannerAd(this, adsPrefernce.gAppopen3_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {

                @Override
                public void onMediaDownloaded(Ad ad) {
                    // Native ad finished downloading all assets
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    Log.e("showNativeBanner3", "onError");
                    showInhouseNativeAd(true, cardView, new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                            Log.e("showNativeBanner3", "onAdLoaded");
                            nativeContainer.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdShowFailed() {
                            Log.e("showNativeBanner3", "onAdShowFailed");
                            nativeContainer.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeBannerAd == null || nativeBannerAd != ad) {
                        return;
                    }
                    cardView.setVisibility(View.GONE);
                    nativeContainer.setVisibility(View.VISIBLE);
                    nativeBannerView.setVisibility(View.VISIBLE);
                    inflateNativeBannerAd(nativeBannerAd, nativeBannerView);
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Native ad clicked
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Native ad impression
                }
            };
            // load the ad
            nativeBannerAd.loadAd(
                    nativeBannerAd.buildLoadAdConfig()
                            .withAdListener(nativeAdListener)
                            .build());
        } else {
            nativeContainer.setVisibility(View.GONE);
        }
    }

    public void showNativeAdAdapter(View nativeContainer, TemplateView view, RelativeLayout fbNativeAdView, CardView cardView) {
        if (isConnected(this)) {
            if (nativeNo == 1) {
                showNativeAd1Adapter(nativeContainer, view, fbNativeAdView, cardView);
            } else if (nativeNo == 2) {
                showNativeAd2Adapter(nativeContainer, view, fbNativeAdView, cardView);
            } else if (nativeNo == 3) {
                showNativeAd3Adapter(nativeContainer, view, fbNativeAdView, cardView);
            }
        } else {
            nativeContainer.setVisibility(View.GONE);
        }

        setNativeNo();
    }


    void showNativeAd1Adapter(View nativeContainer, TemplateView view, RelativeLayout fbNativeAdView, CardView cardView) {
        if (adsPrefernce.showNative1()) {
            AdLoader adLoader = new AdLoader.Builder(this, adsPrefernce.gNative1())
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            nativeContainer.setVisibility(View.VISIBLE);
                            view.setVisibility(View.VISIBLE);
                            view.setNativeAd(nativeAd);
                        }
                    }).withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            view.setVisibility(View.GONE);
                            if (view.getTemplateTypeName().equals("small_template")) {
                                showNativeBannerAd1FBAdapter(nativeContainer, fbNativeAdView, cardView);
                            } else {
                                showNativeAd1FBAdapter(nativeContainer, fbNativeAdView, cardView);
                            }
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                        }
                    })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        } else if (adsPrefernce.showNative1_fb()) {
            if (view.getTemplateTypeName().equals("small_template")) {
                showNativeBannerAd1FBAdapter(nativeContainer, fbNativeAdView, cardView);
            } else {
                showNativeAd1FBAdapter(nativeContainer, fbNativeAdView, cardView);
            }
        } else {
            nativeContainer.setVisibility(View.GONE);
        }

    }

    void showNativeAd2Adapter(View nativeContainer, TemplateView view, RelativeLayout fbNativeAdView, CardView cardView) {
        if (adsPrefernce.showNative2()) {
            AdLoader adLoader = new AdLoader.Builder(this, adsPrefernce.gNative2())
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            nativeContainer.setVisibility(View.VISIBLE);
                            view.setVisibility(View.VISIBLE);
                            view.setNativeAd(nativeAd);
                        }
                    }).withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            view.setVisibility(View.GONE);
                            if (view.getTemplateTypeName().equals("small_template")) {
                                showNativeBannerAd2FBAdapter(nativeContainer, fbNativeAdView, cardView);
                            } else {
                                showNativeAd2FBAdapter(nativeContainer, fbNativeAdView, cardView);
                            }
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                        }
                    })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        } else if (adsPrefernce.showNative2_fb()) {
            if (view.getTemplateTypeName().equals("small_template")) {
                showNativeBannerAd2FBAdapter(nativeContainer, fbNativeAdView, cardView);
            } else {
                showNativeAd2FBAdapter(nativeContainer, fbNativeAdView, cardView);
            }
        } else {
            nativeContainer.setVisibility(View.GONE);
        }

    }

    void showNativeAd3Adapter(View nativeContainer, TemplateView view, RelativeLayout fbNativeAdView, CardView cardView) {
        if (adsPrefernce.showNative3()) {
            AdLoader adLoader = new AdLoader.Builder(this, adsPrefernce.gNative3())
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            nativeContainer.setVisibility(View.VISIBLE);
                            view.setVisibility(View.VISIBLE);
                            view.setNativeAd(nativeAd);
                        }
                    }).withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            view.setVisibility(View.GONE);
                            if (view.getTemplateTypeName().equals("small_template")) {
                                showNativeBannerAd3FBAdapter(nativeContainer, fbNativeAdView, cardView);
                            } else {
                                showNativeAd3FBAdapter(nativeContainer, fbNativeAdView, cardView);
                            }
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                        }
                    })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        } else if (adsPrefernce.showNative3_fb()) {
            if (view.getTemplateTypeName().equals("small_template")) {
                showNativeBannerAd3FBAdapter(nativeContainer, fbNativeAdView, cardView);
            } else {
                showNativeAd3FBAdapter(nativeContainer, fbNativeAdView, cardView);
            }
        } else {
            nativeContainer.setVisibility(View.GONE);
        }

    }


//    void showNativeAd1Adapter(View nativeContainer, TemplateView view, CardView cardView) {
//        if (adsPrefernce.showNative1()) {
//            AdLoader adLoader = new AdLoader.Builder(this, adsPrefernce.gNative1())
//                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
//                        @Override
//                        public void onNativeAdLoaded(NativeAd nativeAd) {
//                            nativeContainer.setVisibility(View.VISIBLE);
//                            view.setVisibility(View.VISIBLE);
//                            view.setNativeAd(nativeAd);
//                        }
//                    }).withAdListener(new AdListener() {
//                        @Override
//                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                            super.onAdFailedToLoad(loadAdError);
//                            view.setVisibility(View.GONE);
//                            if (view.getTemplateTypeName().equals("small_template")) {
//                                showNativeBannerAd1FB(nativeContainer);
//                            } else {
//                                showNativeAd1FB(nativeContainer);
//                            }
//                        }
//
//                        @Override
//                        public void onAdLoaded() {
//                            super.onAdLoaded();
//                        }
//                    })
//                    .build();
//
//            adLoader.loadAd(new AdRequest.Builder().build());
//        } else if (adsPrefernce.showNative1_fb()) {
//            if (view.getTemplateTypeName().equals("small_template")) {
//                showNativeBannerAd1FB(nativeContainer);
//            } else {
//                showNativeAd1FB(nativeContainer);
//            }
//        } else {
//            nativeContainer.setVisibility(View.GONE);
//        }
//
//    }
//
//    void showNativeAd2Adapter(TemplateView view, CardView cardView) {
//        if (isConnected(this) && adsPrefernce.showNative2()) {
//            AdLoader adLoader = new AdLoader.Builder(this, adsPrefernce.gNative2())
//                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
//                        @Override
//                        public void onNativeAdLoaded(NativeAd nativeAd) {
////                            hideInhouseNativeAdapter(cardView);
////                            TemplateView template = findViewById(R.id.my_template);
//                            view.setVisibility(View.VISIBLE);
//                            view.setNativeAd(nativeAd);
//                        }
//                    }).withAdListener(new AdListener() {
//                        @Override
//                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                            super.onAdFailedToLoad(loadAdError);
////                            TemplateView template = findViewById(R.id.my_template);
//                            view.setVisibility(View.GONE);
//                            showInhouseNativeAd(view.getTemplateTypeName().equals("small_template"), cardView, new InhouseNativeListener() {
//                                @Override
//                                public void onAdLoaded() {
//                                }
//
//                                @Override
//                                public void onAdShowFailed() {
//
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onAdLoaded() {
//                            super.onAdLoaded();
//                        }
//                    })
//                    .build();
//
//            adLoader.loadAd(new AdRequest.Builder().build());
//        }
//    }
//
//    void showNativeAd3Adapter(TemplateView view, CardView cardView) {
//        if (isConnected(this) && adsPrefernce.showNative3()) {
//            AdLoader adLoader = new AdLoader.Builder(this, adsPrefernce.gNative3())
//                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
//                        @Override
//                        public void onNativeAdLoaded(NativeAd nativeAd) {
////                            hideInhouseNativeAdapter(cardView);
////                            TemplateView template = findViewById(R.id.my_template);
//                            view.setVisibility(View.VISIBLE);
//                            view.setNativeAd(nativeAd);
//                        }
//                    }).withAdListener(new AdListener() {
//                        @Override
//                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                            super.onAdFailedToLoad(loadAdError);
////                            TemplateView template = findViewById(R.id.my_template);
//                            view.setVisibility(View.GONE);
//                            showInhouseNativeAd(view.getTemplateTypeName().equals("small_template"), cardView, new InhouseNativeListener() {
//                                @Override
//                                public void onAdLoaded() {
//                                }
//
//                                @Override
//                                public void onAdShowFailed() {
//
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onAdLoaded() {
//                            super.onAdLoaded();
//                        }
//                    })
//                    .build();
//
//            adLoader.loadAd(new AdRequest.Builder().build());
//        }
//    }

    public void loadNativeAdBeta() {
        if (!adsPrefernce.showRewardInter3()) {
            loadNativeAd1Beta();
            loadNativeAd2Beta();
            loadNativeAd3Beta();
        }
    }

    void loadNativeAd1Beta() {
        if (isConnected(this) && adsPrefernce.showNative1() && nativeAd1Beta == null) {
            AdLoader adLoader = new AdLoader.Builder(this, adsPrefernce.gNative1())
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            nativeAd1Beta = nativeAd;

                        }
                    }).withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            nativeAd1Beta = null;
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                        }
                    })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        }
    }

    void loadNativeAd2Beta() {
        if (isConnected(this) && adsPrefernce.showNative2() && nativeAd2Beta == null) {
            AdLoader adLoader = new AdLoader.Builder(this, adsPrefernce.gNative2())
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            nativeAd2Beta = nativeAd;

                        }
                    }).withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            nativeAd2Beta = null;
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                        }
                    })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        }
    }

    void loadNativeAd3Beta() {
        if (isConnected(this) && adsPrefernce.showNative3() && nativeAd3Beta == null) {
            AdLoader adLoader = new AdLoader.Builder(this, adsPrefernce.gNative3())
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            nativeAd3Beta = nativeAd;

                        }
                    }).withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            nativeAd3Beta = null;
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                        }
                    })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        }
    }

    void showNativeAd1Beta(View nativeView) {
        if (isConnected(this) && adsPrefernce.showNative1()) {
            nativeView.setVisibility(View.VISIBLE);
            if (nativeAd1Beta != null) {
                hideInhouseNative();
                TemplateView template = findViewById(R.id.my_template);
                template.setVisibility(View.VISIBLE);
                template.setNativeAd(nativeAd1Beta);
            } else {
                TemplateView template = findViewById(R.id.my_template);
                template.setVisibility(View.GONE);
                if (template.getTemplateTypeName().equals("small_template")) {
                    showNativeBannerAd1FB(nativeView);
                } else {
                    showNativeAd1FB(nativeView);
                }
            }
        } else if (adsPrefernce.showNative1_fb()) {
            TemplateView template = findViewById(R.id.my_template);
            if (template.getTemplateTypeName().equals("small_template")) {
                showNativeBannerAd1FB(nativeView);
            } else {
                showNativeAd1FB(nativeView);
            }
        } else {
            nativeView.setVisibility(View.GONE);
        }
        nativeAd1Beta = null;
    }

    void showNativeAd2Beta(View nativeView) {
        if (isConnected(this) && adsPrefernce.showNative2()) {
            nativeView.setVisibility(View.VISIBLE);
            if (nativeAd2Beta != null) {
                hideInhouseNative();
                TemplateView template = findViewById(R.id.my_template);
                template.setVisibility(View.VISIBLE);
                template.setNativeAd(nativeAd2Beta);
            } else {
                TemplateView template = findViewById(R.id.my_template);
                template.setVisibility(View.GONE);
                if (template.getTemplateTypeName().equals("small_template")) {
                    showNativeBannerAd1FB(nativeView);
                } else {
                    showNativeAd1FB(nativeView);
                }
            }
        } else if (adsPrefernce.showNative2_fb()) {
            TemplateView template = findViewById(R.id.my_template);
            if (template.getTemplateTypeName().equals("small_template")) {
                showNativeBannerAd2FB(nativeView);
            } else {
                showNativeAd2FB(nativeView);
            }
        } else {
            nativeView.setVisibility(View.GONE);
        }
        nativeAd2Beta = null;
    }

    void showNativeAd3Beta(View nativeView) {
        if (isConnected(this) && adsPrefernce.showNative3()) {
            nativeView.setVisibility(View.VISIBLE);
            if (nativeAd3Beta != null) {
                hideInhouseNative();
                TemplateView template = findViewById(R.id.my_template);
                template.setVisibility(View.VISIBLE);
                template.setNativeAd(nativeAd3Beta);
            } else {
                TemplateView template = findViewById(R.id.my_template);
                template.setVisibility(View.GONE);
                if (template.getTemplateTypeName().equals("small_template")) {
                    showNativeBannerAd1FB(nativeView);
                } else {
                    showNativeAd1FB(nativeView);
                }
            }
        } else if (adsPrefernce.showNative3_fb()) {
            TemplateView template = findViewById(R.id.my_template);
            if (template.getTemplateTypeName().equals("small_template")) {
                showNativeBannerAd3FB(nativeView);
            } else {
                showNativeAd3FB(nativeView);
            }
        } else {
            nativeView.setVisibility(View.GONE);
        }
        nativeAd3Beta = null;
    }


    void showNativeAd1Beta() {
        if (isConnected(this) && adsPrefernce.showNative1() && nativeAd1Beta != null) {
            hideInhouseNative();
            TemplateView template = findViewById(R.id.my_template);
            template.setVisibility(View.VISIBLE);
            template.setNativeAd(nativeAd1Beta);
        } else {
            TemplateView template = findViewById(R.id.my_template);
            template.setVisibility(View.GONE);
            if (template.getTemplateTypeName().equals("small_template")) {
                showNativeBannerAd1FB();
            } else {
                showNativeAd1FB();
            }
        }
        nativeAd1Beta = null;
    }

    void showNativeAd2Beta() {
        if (isConnected(this) && adsPrefernce.showNative1() && nativeAd2Beta != null) {
            hideInhouseNative();
            TemplateView template = findViewById(R.id.my_template);
            template.setVisibility(View.VISIBLE);
            template.setNativeAd(nativeAd2Beta);
        } else {
            TemplateView template = findViewById(R.id.my_template);
            template.setVisibility(View.GONE);
            if (template.getTemplateTypeName().equals("small_template")) {
                showNativeBannerAd2FB();
            } else {
                showNativeAd2FB();
            }
        }
        nativeAd2Beta = null;
    }

    void showNativeAd3Beta() {
        if (isConnected(this) && adsPrefernce.showNative1() && nativeAd3Beta != null) {
            hideInhouseNative();
            TemplateView template = findViewById(R.id.my_template);
            template.setVisibility(View.VISIBLE);
            template.setNativeAd(nativeAd3Beta);
        } else {
            TemplateView template = findViewById(R.id.my_template);
            template.setVisibility(View.GONE);
            if (template.getTemplateTypeName().equals("small_template")) {
                showNativeBannerAd3FB();
            } else {
                showNativeAd3FB();
            }
        }
        nativeAd3Beta = null;
    }


    public void showNativeAdBeta() {
        if (nativeNo == 1) {
            showNativeAd1Beta();
        } else if (nativeNo == 2) {
            showNativeAd2Beta();
        } else if (nativeNo == 3) {
            showNativeAd3Beta();
        }
    }

    public void showNativeAdBeta(View nativeView) {
        if (nativeNo == 1) {
            showNativeAd1Beta(nativeView);
        } else if (nativeNo == 2) {
            showNativeAd2Beta(nativeView);
        } else if (nativeNo == 3) {
            showNativeAd3Beta(nativeView);
        }
    }

    public void showNativeAd() {
        if (adsPrefernce.showRewardInter3()) {
            if (nativeNo == 1) {
                showNativeAd1();
            } else if (nativeNo == 2) {
                showNativeAd2();
            } else if (nativeNo == 3) {
                showNativeAd3();
            }
        } else {
            showNativeAdBeta();
        }
        setNativeNo();

    }

    public void showNativeAd1() {
        if (isConnected(this) && adsPrefernce.showNative1()) {
            AdLoader adLoader = new AdLoader.Builder(this, adsPrefernce.gNative1())
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            hideInhouseNative();
                            TemplateView template = findViewById(R.id.my_template);
                            template.setVisibility(View.VISIBLE);
                            template.setNativeAd(nativeAd);
                        }
                    }).withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            TemplateView template = findViewById(R.id.my_template);
                            template.setVisibility(View.GONE);
                            if (template.getTemplateTypeName().equals("small_template")) {
                                showNativeBannerAd1FB();
                            } else {
                                showNativeAd1FB();
                            }
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                        }
                    })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        } else if (adsPrefernce.showNative1_fb()) {
            TemplateView template = findViewById(R.id.my_template);
            if (template.getTemplateTypeName().equals("small_template")) {
                showNativeBannerAd1FB();
            } else {
                showNativeAd1FB();
            }
        }
    }

    void showNativeAd2() {
        if (isConnected(this) && adsPrefernce.showNative2()) {
            AdLoader adLoader = new AdLoader.Builder(this, adsPrefernce.gNative2())
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            hideInhouseNative();
                            TemplateView template = findViewById(R.id.my_template);
                            template.setVisibility(View.VISIBLE);
                            template.setNativeAd(nativeAd);
                        }
                    }).withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            TemplateView template = findViewById(R.id.my_template);
                            template.setVisibility(View.GONE);
                            if (template.getTemplateTypeName().equals("small_template")) {
                                showNativeBannerAd2FB();
                            } else {
                                showNativeAd2FB();
                            }
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                        }
                    })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        } else if (adsPrefernce.showNative2_fb()) {
            TemplateView template = findViewById(R.id.my_template);
            if (template.getTemplateTypeName().equals("small_template")) {
                showNativeBannerAd2FB();
            } else {
                showNativeAd2FB();
            }
        }
    }

    void showNativeAd3() {
        if (isConnected(this) && adsPrefernce.showNative3()) {
            AdLoader adLoader = new AdLoader.Builder(this, adsPrefernce.gNative3())
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            hideInhouseNative();
                            TemplateView template = findViewById(R.id.my_template);
                            template.setVisibility(View.VISIBLE);
                            template.setNativeAd(nativeAd);
                        }
                    }).withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            TemplateView template = findViewById(R.id.my_template);
                            template.setVisibility(View.GONE);
                            if (template.getTemplateTypeName().equals("small_template")) {
                                showNativeBannerAd3FB();
                            } else {
                                showNativeAd3FB();
                            }
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                        }
                    })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        } else if (adsPrefernce.showNative3_fb()) {
            TemplateView template = findViewById(R.id.my_template);
            if (template.getTemplateTypeName().equals("small_template")) {
                showNativeBannerAd3FB();
            } else {
                showNativeAd3FB();
            }
        }
    }

    void showNativeAd1FB() {
        if (adsPrefernce.showNative1_fb()) {
            final com.facebook.ads.NativeAd nativeAd;
            nativeAd = new com.facebook.ads.NativeAd(this, adsPrefernce.gNative1_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    showInhouseNativeAd(false, findViewById(R.id.native_ad_container), new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                        }

                        @Override
                        public void onAdShowFailed() {

                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeAd != ad) {
                        return;
                    }
                    hideInhouseNative();
                    // Inflate Native Ad into Container
                    inflateNativeAdFacebook(nativeAd, findViewById(R.id._lay_native));
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            };
            nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
        }
    }

    void showNativeAd2FB() {
        if (adsPrefernce.showNative2_fb()) {
            final com.facebook.ads.NativeAd nativeAd;
            nativeAd = new com.facebook.ads.NativeAd(this, adsPrefernce.gNative2_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    showInhouseNativeAd(false, findViewById(R.id.native_ad_container), new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                        }

                        @Override
                        public void onAdShowFailed() {

                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeAd != ad) {
                        return;
                    }
                    hideInhouseNative();
                    // Inflate Native Ad into Container
                    inflateNativeAdFacebook(nativeAd, findViewById(R.id._lay_native));
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            };
            nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
        }
    }

    void showNativeAd3FB() {
        if (adsPrefernce.showNative3_fb()) {
            final com.facebook.ads.NativeAd nativeAd;
            nativeAd = new com.facebook.ads.NativeAd(this, adsPrefernce.gNative3_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    showInhouseNativeAd(false, findViewById(R.id.native_ad_container), new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                        }

                        @Override
                        public void onAdShowFailed() {

                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeAd != ad) {
                        return;
                    }
                    hideInhouseNative();
                    // Inflate Native Ad into Container
                    inflateNativeAdFacebook(nativeAd, findViewById(R.id._lay_native));
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            };
            nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
        }
    }

    void showNativeAd3FBDialog(Dialog dialog) {
        if (adsPrefernce.showNative3_fb()) {
            final com.facebook.ads.NativeAd nativeAd;
            nativeAd = new com.facebook.ads.NativeAd(this, adsPrefernce.gNative3_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    showInhouseNativeAd(false, dialog.findViewById(R.id.native_ad_container), new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                        }

                        @Override
                        public void onAdShowFailed() {

                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeAd != ad) {
                        return;
                    }
                    hideInhouseNativeDialog(dialog);
                    // Inflate Native Ad into Container
                    inflateNativeAdFacebook(nativeAd, dialog.findViewById(R.id._lay_native));
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            };
            nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
        }
    }

    void showNativeAd2FBExtra() {
        if (adsPrefernce.showNative2_fb()) {
            final com.facebook.ads.NativeAd nativeAd;
            nativeAd = new com.facebook.ads.NativeAd(this, adsPrefernce.gNative2_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    showInhouseNativeAd(false, findViewById(R.id.native_ad_container2), new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                        }

                        @Override
                        public void onAdShowFailed() {

                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeAd != ad) {
                        return;
                    }
                    hideInhouseNative2();
                    // Inflate Native Ad into Container
                    inflateNativeAdFacebook(nativeAd, findViewById(R.id._lay_native2));
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            };
            nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
        }
    }

    void showNativeAd3FBExtra() {
        if (adsPrefernce.showNative3_fb()) {
            final com.facebook.ads.NativeAd nativeAd;
            nativeAd = new com.facebook.ads.NativeAd(this, adsPrefernce.gNative3_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    showInhouseNativeAd(false, findViewById(R.id.native_ad_container3), new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                        }

                        @Override
                        public void onAdShowFailed() {

                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeAd != ad) {
                        return;
                    }
                    hideInhouseNative3();
                    // Inflate Native Ad into Container
                    inflateNativeAdFacebook(nativeAd, findViewById(R.id._lay_native3));
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            };
            nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
        }
    }

    void showNativeBannerAd1FB() {
        if (adsPrefernce.showAppopen1_fb()) {
            NativeBannerAd nativeBannerAd;
            nativeBannerAd = new NativeBannerAd(this, adsPrefernce.gAppopen1_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {

                @Override
                public void onMediaDownloaded(Ad ad) {
                    // Native ad finished downloading all assets
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    showInhouseNativeAd(true, findViewById(R.id.native_ad_container), new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                        }

                        @Override
                        public void onAdShowFailed() {

                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeBannerAd == null || nativeBannerAd != ad) {
                        return;
                    }
                    hideInhouseNative();
                    inflateNativeBannerAd(nativeBannerAd, findViewById(R.id._lay_native));
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Native ad clicked
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Native ad impression
                }
            };
            // load the ad
            nativeBannerAd.loadAd(
                    nativeBannerAd.buildLoadAdConfig()
                            .withAdListener(nativeAdListener)
                            .build());
        }

    }

    void showNativeBannerAd2FB() {
        if (adsPrefernce.showAppopen2_fb()) {
            NativeBannerAd nativeBannerAd;
            nativeBannerAd = new NativeBannerAd(this, adsPrefernce.gAppopen2_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {

                @Override
                public void onMediaDownloaded(Ad ad) {
                    // Native ad finished downloading all assets
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    showInhouseNativeAd(true, findViewById(R.id.native_ad_container), new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                        }

                        @Override
                        public void onAdShowFailed() {

                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeBannerAd == null || nativeBannerAd != ad) {
                        return;
                    }
                    hideInhouseNative();
                    inflateNativeBannerAd(nativeBannerAd, findViewById(R.id._lay_native));
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Native ad clicked
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Native ad impression
                }
            };
            // load the ad
            nativeBannerAd.loadAd(
                    nativeBannerAd.buildLoadAdConfig()
                            .withAdListener(nativeAdListener)
                            .build());
        }

    }

    void showNativeBannerAd2FBExtra() {
        if (adsPrefernce.showAppopen2_fb()) {
            NativeBannerAd nativeBannerAd;
            nativeBannerAd = new NativeBannerAd(this, adsPrefernce.gAppopen2_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {

                @Override
                public void onMediaDownloaded(Ad ad) {
                    // Native ad finished downloading all assets
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    showInhouseNativeAd(true, findViewById(R.id.native_ad_container2), new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                        }

                        @Override
                        public void onAdShowFailed() {

                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeBannerAd == null || nativeBannerAd != ad) {
                        return;
                    }
                    hideInhouseNative2();
                    inflateNativeBannerAd(nativeBannerAd, findViewById(R.id._lay_native2));
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Native ad clicked
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Native ad impression
                }
            };
            // load the ad
            nativeBannerAd.loadAd(
                    nativeBannerAd.buildLoadAdConfig()
                            .withAdListener(nativeAdListener)
                            .build());
        }

    }

    void showNativeBannerAd3FBExtra() {
        if (adsPrefernce.showAppopen3_fb()) {
            NativeBannerAd nativeBannerAd;
            nativeBannerAd = new NativeBannerAd(this, adsPrefernce.gAppopen3_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {

                @Override
                public void onMediaDownloaded(Ad ad) {
                    // Native ad finished downloading all assets
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    showInhouseNativeAd(true, findViewById(R.id.native_ad_container3), new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                        }

                        @Override
                        public void onAdShowFailed() {

                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeBannerAd == null || nativeBannerAd != ad) {
                        return;
                    }
                    hideInhouseNative3();
                    inflateNativeBannerAd(nativeBannerAd, findViewById(R.id._lay_native3));
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Native ad clicked
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Native ad impression
                }
            };
            // load the ad
            nativeBannerAd.loadAd(
                    nativeBannerAd.buildLoadAdConfig()
                            .withAdListener(nativeAdListener)
                            .build());
        }

    }

    void showNativeBannerAd3FB() {
        if (adsPrefernce.showAppopen3_fb()) {
            NativeBannerAd nativeBannerAd;
            nativeBannerAd = new NativeBannerAd(this, adsPrefernce.gAppopen3_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {

                @Override
                public void onMediaDownloaded(Ad ad) {
                    // Native ad finished downloading all assets
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    showInhouseNativeAd(true, findViewById(R.id.native_ad_container), new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                        }

                        @Override
                        public void onAdShowFailed() {

                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeBannerAd == null || nativeBannerAd != ad) {
                        return;
                    }
                    hideInhouseNative();
                    inflateNativeBannerAd(nativeBannerAd, findViewById(R.id._lay_native));
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Native ad clicked
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Native ad impression
                }
            };
            // load the ad
            nativeBannerAd.loadAd(
                    nativeBannerAd.buildLoadAdConfig()
                            .withAdListener(nativeAdListener)
                            .build());
        }
    }

    void showNativeBannerAd3FBDialog(Dialog dialog) {
        if (adsPrefernce.showAppopen3_fb()) {
            NativeBannerAd nativeBannerAd;
            nativeBannerAd = new NativeBannerAd(this, adsPrefernce.gAppopen3_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {

                @Override
                public void onMediaDownloaded(Ad ad) {
                    // Native ad finished downloading all assets
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    showInhouseNativeAd(true, dialog.findViewById(R.id.native_ad_container), new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                        }

                        @Override
                        public void onAdShowFailed() {

                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeBannerAd == null || nativeBannerAd != ad) {
                        return;
                    }
                    hideInhouseNativeDialog(dialog);
                    inflateNativeBannerAd(nativeBannerAd, dialog.findViewById(R.id._lay_native));
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Native ad clicked
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Native ad impression
                }
            };
            // load the ad
            nativeBannerAd.loadAd(
                    nativeBannerAd.buildLoadAdConfig()
                            .withAdListener(nativeAdListener)
                            .build());
        }
    }

    private void inflateNativeBannerAd(NativeBannerAd nativeBannerAd, RelativeLayout relativeLayout) {
        // Unregister last ad
        nativeBannerAd.unregisterView();

        // Add the Ad view into the ad container.
        NativeAdLayout nativeAdLayout = new NativeAdLayout(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.native_banner_ad_layout_fb, relativeLayout, false);
        relativeLayout.addView(adView);


        // Add the AdChoices icon
        RelativeLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(this, nativeBannerAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        MediaView nativeAdIconView = adView.findViewById(R.id.native_icon_view);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);
        if (ad_bg_drawable != 0 && new AdsPrefernce(this).showRewardInter1()) {
            nativeAdCallToAction.setBackgroundResource(ad_bg_drawable);
        }

        // Set the Text.
        nativeAdCallToAction.setText(nativeBannerAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(
                nativeBannerAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdTitle.setText(nativeBannerAd.getAdvertiserName());
        nativeAdSocialContext.setText(nativeBannerAd.getAdSocialContext());
        sponsoredLabel.setText(nativeBannerAd.getSponsoredTranslation());

        // Register the Title and CTA button to listen for clicks.
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        nativeBannerAd.registerViewForInteraction(adView, nativeAdIconView, clickableViews);
    }

    private void inflateNativeAdFacebook(com.facebook.ads.NativeAd nativeAd, RelativeLayout relativeLayout) {
        nativeAd.unregisterView();

        // Add the Ad view into the ad container.
        NativeAdLayout nativeAdLayout = new NativeAdLayout(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout adViews = (RelativeLayout) inflater.inflate(R.layout.native_ad_layout_fb, relativeLayout, false);
        relativeLayout.addView(adViews);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = adViews.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(this, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        com.facebook.ads.MediaView nativeAdIcon = adViews.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adViews.findViewById(R.id.native_ad_title);
        com.facebook.ads.MediaView nativeAdMedia = adViews.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adViews.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adViews.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adViews.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adViews.findViewById(R.id.native_ad_call_to_action);
        if (ad_bg_drawable != 0 && new AdsPrefernce(this).showRewardInter1()) {
            nativeAdCallToAction.setBackgroundResource(ad_bg_drawable);
        }

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
                adViews,
                nativeAdMedia,
                nativeAdIcon,
                clickableViews);
    }

    void showNativeAd1FB(View nativeView) {
        if (adsPrefernce.showNative1_fb()) {
            nativeView.setVisibility(View.VISIBLE);
            final com.facebook.ads.NativeAd nativeAd;
            nativeAd = new com.facebook.ads.NativeAd(this, adsPrefernce.gNative1_fb());

            NativeAdListener nativeAdListener = new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                }


                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    showInhouseNativeAd(false, findViewById(R.id.native_ad_container), new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                            nativeView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdShowFailed() {
                            nativeView.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeAd != ad) {
                        return;
                    }
                    hideInhouseNative();
                    nativeView.setVisibility(View.VISIBLE);
                    // Inflate Native Ad into Container
                    inflateNativeAdFacebook(nativeAd, findViewById(R.id._lay_native));
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            };
            nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
        } else {
            nativeView.setVisibility(View.GONE);
        }
    }

    void showNativeAd2FB(View nativeView) {
        if (adsPrefernce.showNative2_fb()) {
            nativeView.setVisibility(View.VISIBLE);
            final com.facebook.ads.NativeAd nativeAd;
            nativeAd = new com.facebook.ads.NativeAd(this, adsPrefernce.gNative2_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    showInhouseNativeAd(false, findViewById(R.id.native_ad_container), new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                            nativeView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdShowFailed() {
                            nativeView.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeAd != ad) {
                        return;
                    }
                    hideInhouseNative();
                    nativeView.setVisibility(View.VISIBLE);
                    // Inflate Native Ad into Container
                    inflateNativeAdFacebook(nativeAd, findViewById(R.id._lay_native));
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            };
            nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
        } else {
            nativeView.setVisibility(View.GONE);
        }
    }

    void showNativeAd3FB(View nativeView) {
        if (adsPrefernce.showNative3_fb()) {
            nativeView.setVisibility(View.VISIBLE);
            final com.facebook.ads.NativeAd nativeAd;
            nativeAd = new com.facebook.ads.NativeAd(this, adsPrefernce.gNative3_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    showInhouseNativeAd(false, findViewById(R.id.native_ad_container), new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                            nativeView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdShowFailed() {
                            nativeView.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeAd != ad) {
                        return;
                    }
                    hideInhouseNative();
                    nativeView.setVisibility(View.VISIBLE);
                    // Inflate Native Ad into Container
                    inflateNativeAdFacebook(nativeAd, findViewById(R.id._lay_native));
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            };
            nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
        } else {
            nativeView.setVisibility(View.GONE);
        }
    }

    void showNativeAd2FBExtra(View nativeView) {
        if (adsPrefernce.showNative2_fb()) {
            nativeView.setVisibility(View.VISIBLE);
            final com.facebook.ads.NativeAd nativeAd;
            nativeAd = new com.facebook.ads.NativeAd(this, adsPrefernce.gNative2_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    showInhouseNativeAd(false, findViewById(R.id.native_ad_container2), new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                            nativeView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdShowFailed() {
                            nativeView.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeAd != ad) {
                        return;
                    }
                    hideInhouseNative2();
                    nativeView.setVisibility(View.VISIBLE);
                    // Inflate Native Ad into Container
                    inflateNativeAdFacebook(nativeAd, findViewById(R.id._lay_native2));
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            };
            nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
        } else {
            nativeView.setVisibility(View.GONE);
        }
    }

    void showNativeAd3FBExtra(View nativeView) {
        if (adsPrefernce.showNative3_fb()) {
            nativeView.setVisibility(View.VISIBLE);
            final com.facebook.ads.NativeAd nativeAd;
            nativeAd = new com.facebook.ads.NativeAd(this, adsPrefernce.gNative3_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    showInhouseNativeAd(false, findViewById(R.id.native_ad_container3), new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                            nativeView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdShowFailed() {
                            nativeView.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeAd != ad) {
                        return;
                    }
                    hideInhouseNative3();
                    nativeView.setVisibility(View.VISIBLE);
                    // Inflate Native Ad into Container
                    inflateNativeAdFacebook(nativeAd, findViewById(R.id._lay_native3));
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            };
            nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
        } else {
            nativeView.setVisibility(View.GONE);
        }
    }


    void showNativeAd3FBDialog(Dialog dialog, View nativeView) {
        if (adsPrefernce.showNative3_fb()) {
            nativeView.setVisibility(View.VISIBLE);
            final com.facebook.ads.NativeAd nativeAd;
            nativeAd = new com.facebook.ads.NativeAd(this, adsPrefernce.gNative3_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    showInhouseNativeAd(false, dialog.findViewById(R.id.native_ad_container), new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                            nativeView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdShowFailed() {
                            nativeView.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeAd != ad) {
                        return;
                    }

                    hideInhouseNativeDialog(dialog);
                    nativeView.setVisibility(View.VISIBLE);
                    // Inflate Native Ad into Container
                    inflateNativeAdFacebook(nativeAd, dialog.findViewById(R.id._lay_native));
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            };
            nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
        } else {
            nativeView.setVisibility(View.GONE);
        }
    }

    void showNativeBannerAd1FB(View nativeBannerView) {
        if (adsPrefernce.showAppopen1_fb()) {
            nativeBannerView.setVisibility(View.VISIBLE);
            NativeBannerAd nativeBannerAd;
            nativeBannerAd = new NativeBannerAd(this, adsPrefernce.gAppopen1_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {

                @Override
                public void onMediaDownloaded(Ad ad) {
                    // Native ad finished downloading all assets
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    showInhouseNativeAd(true, findViewById(R.id.native_ad_container), new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                            nativeBannerView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdShowFailed() {
                            nativeBannerView.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeBannerAd == null || nativeBannerAd != ad) {
                        return;
                    }
                    hideInhouseNative();
                    nativeBannerView.setVisibility(View.VISIBLE);
                    inflateNativeBannerAd(nativeBannerAd, findViewById(R.id._lay_native));
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Native ad clicked
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Native ad impression
                }
            };
            // load the ad
            nativeBannerAd.loadAd(
                    nativeBannerAd.buildLoadAdConfig()
                            .withAdListener(nativeAdListener)
                            .build());
        } else {
            nativeBannerView.setVisibility(View.GONE);
        }

    }

    void showNativeBannerAd2FB(View nativeBannerView) {
        if (adsPrefernce.showAppopen2_fb()) {
            nativeBannerView.setVisibility(View.VISIBLE);
            NativeBannerAd nativeBannerAd;
            nativeBannerAd = new NativeBannerAd(this, adsPrefernce.gAppopen2_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {

                @Override
                public void onMediaDownloaded(Ad ad) {
                    // Native ad finished downloading all assets
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    showInhouseNativeAd(true, findViewById(R.id.native_ad_container), new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                            nativeBannerView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdShowFailed() {
                            nativeBannerView.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeBannerAd == null || nativeBannerAd != ad) {
                        return;
                    }
                    hideInhouseNative();
                    nativeBannerView.setVisibility(View.VISIBLE);
                    inflateNativeBannerAd(nativeBannerAd, findViewById(R.id._lay_native));
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Native ad clicked
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Native ad impression
                }
            };
            // load the ad
            nativeBannerAd.loadAd(
                    nativeBannerAd.buildLoadAdConfig()
                            .withAdListener(nativeAdListener)
                            .build());
        } else {
            nativeBannerView.setVisibility(View.GONE);
        }

    }

    void showNativeBannerAd3FB(View nativeBannerView) {
        if (adsPrefernce.showAppopen3_fb()) {
            nativeBannerView.setVisibility(View.VISIBLE);
            NativeBannerAd nativeBannerAd;
            nativeBannerAd = new NativeBannerAd(this, adsPrefernce.gAppopen3_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {

                @Override
                public void onMediaDownloaded(Ad ad) {
                    // Native ad finished downloading all assets
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    showInhouseNativeAd(true, findViewById(R.id.native_ad_container), new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                            nativeBannerView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdShowFailed() {
                            nativeBannerView.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeBannerAd == null || nativeBannerAd != ad) {
                        return;
                    }
                    hideInhouseNative();
                    nativeBannerView.setVisibility(View.VISIBLE);
                    inflateNativeBannerAd(nativeBannerAd, findViewById(R.id._lay_native));
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Native ad clicked
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Native ad impression
                }
            };
            // load the ad
            nativeBannerAd.loadAd(
                    nativeBannerAd.buildLoadAdConfig()
                            .withAdListener(nativeAdListener)
                            .build());
        } else {
            nativeBannerView.setVisibility(View.GONE);
        }

    }

    void showNativeBannerAd2FBExtra(View nativeBannerView) {
        if (adsPrefernce.showAppopen2_fb()) {
            nativeBannerView.setVisibility(View.VISIBLE);
            NativeBannerAd nativeBannerAd;
            nativeBannerAd = new NativeBannerAd(this, adsPrefernce.gAppopen2_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {

                @Override
                public void onMediaDownloaded(Ad ad) {
                    // Native ad finished downloading all assets
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    showInhouseNativeAd(true, findViewById(R.id.native_ad_container2), new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                            nativeBannerView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdShowFailed() {
                            nativeBannerView.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeBannerAd == null || nativeBannerAd != ad) {
                        return;
                    }
                    hideInhouseNative2();
                    nativeBannerView.setVisibility(View.VISIBLE);
                    inflateNativeBannerAd(nativeBannerAd, findViewById(R.id._lay_native2));
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Native ad clicked
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Native ad impression
                }
            };
            // load the ad
            nativeBannerAd.loadAd(
                    nativeBannerAd.buildLoadAdConfig()
                            .withAdListener(nativeAdListener)
                            .build());
        } else {
            nativeBannerView.setVisibility(View.GONE);
        }

    }

    void showNativeBannerAd3FBExtra(View nativeBannerView) {
        if (adsPrefernce.showAppopen3_fb()) {
            nativeBannerView.setVisibility(View.VISIBLE);
            NativeBannerAd nativeBannerAd;
            nativeBannerAd = new NativeBannerAd(this, adsPrefernce.gAppopen3_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {

                @Override
                public void onMediaDownloaded(Ad ad) {
                    // Native ad finished downloading all assets
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    showInhouseNativeAd(true, findViewById(R.id.native_ad_container3), new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                            nativeBannerView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdShowFailed() {
                            nativeBannerView.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeBannerAd == null || nativeBannerAd != ad) {
                        return;
                    }
                    hideInhouseNative3();
                    nativeBannerView.setVisibility(View.VISIBLE);
                    inflateNativeBannerAd(nativeBannerAd, findViewById(R.id._lay_native3));
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Native ad clicked
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Native ad impression
                }
            };
            // load the ad
            nativeBannerAd.loadAd(
                    nativeBannerAd.buildLoadAdConfig()
                            .withAdListener(nativeAdListener)
                            .build());
        } else {
            nativeBannerView.setVisibility(View.GONE);
        }

    }


    void showNativeBannerAd3FBDialog(Dialog dialog, View nativeBannerView) {
        if (adsPrefernce.showAppopen3_fb()) {
            nativeBannerView.setVisibility(View.VISIBLE);
            NativeBannerAd nativeBannerAd;
            nativeBannerAd = new NativeBannerAd(this, adsPrefernce.gAppopen3_fb());
            NativeAdListener nativeAdListener = new NativeAdListener() {

                @Override
                public void onMediaDownloaded(Ad ad) {
                    // Native ad finished downloading all assets
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    showInhouseNativeAd(true, dialog.findViewById(R.id.native_ad_container), new InhouseNativeListener() {
                        @Override
                        public void onAdLoaded() {
                            nativeBannerView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdShowFailed() {
                            nativeBannerView.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeBannerAd == null || nativeBannerAd != ad) {
                        return;
                    }
                    hideInhouseNativeDialog(dialog);
                    nativeBannerView.setVisibility(View.VISIBLE);
                    inflateNativeBannerAd(nativeBannerAd, dialog.findViewById(R.id._lay_native));
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Native ad clicked
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Native ad impression
                }
            };
            // load the ad
            nativeBannerAd.loadAd(
                    nativeBannerAd.buildLoadAdConfig()
                            .withAdListener(nativeAdListener)
                            .build());
        } else {
            nativeBannerView.setVisibility(View.GONE);
        }

    }

    void showNativeAd1(View nativeView) {
        if (isConnected(this) && adsPrefernce.showNative1()) {
            nativeView.setVisibility(View.VISIBLE);
            AdLoader adLoader = new AdLoader.Builder(this, adsPrefernce.gNative1())
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            hideInhouseNative();
                            nativeView.setVisibility(View.VISIBLE);
                            TemplateView template = findViewById(R.id.my_template);
                            template.setVisibility(View.VISIBLE);
                            template.setNativeAd(nativeAd);
                        }
                    }).withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            TemplateView template = findViewById(R.id.my_template);
                            template.setVisibility(View.GONE);
                            if (template.getTemplateTypeName().equals("small_template")) {
                                showNativeBannerAd1FB(nativeView);
                            } else {
                                showNativeAd1FB(nativeView);
                            }
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            nativeView.setVisibility(View.VISIBLE);
                        }
                    })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        } else if (adsPrefernce.showNative1_fb()) {
            TemplateView template = findViewById(R.id.my_template);
            if (template.getTemplateTypeName().equals("small_template")) {
                showNativeBannerAd1FB(nativeView);
            } else {
                showNativeAd1FB(nativeView);
            }
        } else {
            nativeView.setVisibility(View.GONE);
        }
    }

    void showNativeAd2(View nativeView) {
        if (isConnected(this) && adsPrefernce.showNative2()) {
            nativeView.setVisibility(View.VISIBLE);
            AdLoader adLoader = new AdLoader.Builder(this, adsPrefernce.gNative2())
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            hideInhouseNative();
                            nativeView.setVisibility(View.VISIBLE);
                            TemplateView template = findViewById(R.id.my_template);
                            template.setVisibility(View.VISIBLE);
                            template.setNativeAd(nativeAd);
                        }
                    }).withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            TemplateView template = findViewById(R.id.my_template);
                            template.setVisibility(View.GONE);
                            if (template.getTemplateTypeName().equals("small_template")) {
                                showNativeBannerAd2FB(nativeView);
                            } else {
                                showNativeAd2FB(nativeView);
                            }
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            nativeView.setVisibility(View.VISIBLE);
                        }
                    })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        } else if (adsPrefernce.showNative2_fb()) {
            TemplateView template = findViewById(R.id.my_template);
            if (template.getTemplateTypeName().equals("small_template")) {
                showNativeBannerAd2FB(nativeView);
            } else {
                showNativeAd2FB(nativeView);
            }
        } else {
            nativeView.setVisibility(View.GONE);
        }
    }

    void showNativeAd3(View nativeView) {
        if (isConnected(this) && adsPrefernce.showNative3()) {
            nativeView.setVisibility(View.VISIBLE);
            AdLoader adLoader = new AdLoader.Builder(this, adsPrefernce.gNative3())
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            hideInhouseNative();
                            nativeView.setVisibility(View.VISIBLE);
                            TemplateView template = findViewById(R.id.my_template);
                            template.setVisibility(View.VISIBLE);
                            template.setNativeAd(nativeAd);
                        }
                    }).withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            TemplateView template = findViewById(R.id.my_template);
                            template.setVisibility(View.GONE);
                            if (template.getTemplateTypeName().equals("small_template")) {
                                showNativeBannerAd3FB(nativeView);
                            } else {
                                showNativeAd3FB(nativeView);
                            }
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            nativeView.setVisibility(View.VISIBLE);
                        }
                    })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        } else if (adsPrefernce.showNative3_fb()) {
            TemplateView template = findViewById(R.id.my_template);
            if (template.getTemplateTypeName().equals("small_template")) {
                showNativeBannerAd3FB(nativeView);
            } else {
                showNativeAd3FB(nativeView);
            }
        } else {
            nativeView.setVisibility(View.GONE);
        }
    }

    public void showNativeAd2Extra() {
        if (isConnected(this) && adsPrefernce.showNative2()) {
            AdLoader adLoader = new AdLoader.Builder(this, adsPrefernce.gNative2())
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            hideInhouseNative2();
                            TemplateView template = findViewById(R.id.my_template2);
                            template.setVisibility(View.VISIBLE);
                            template.setNativeAd(nativeAd);
                        }
                    }).withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            TemplateView template = findViewById(R.id.my_template2);
                            template.setVisibility(View.GONE);
                            if (template.getTemplateTypeName().equals("small_template")) {
                                showNativeBannerAd2FBExtra();
                            } else {
                                showNativeAd2FBExtra();
                            }
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                        }
                    })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        }
    }

    public void showNativeAd2Extra(View nativeExtraView) {
        if (isConnected(this) && adsPrefernce.showNative2()) {
            nativeExtraView.setVisibility(View.VISIBLE);
            AdLoader adLoader = new AdLoader.Builder(this, adsPrefernce.gNative2())
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            hideInhouseNative2();
                            nativeExtraView.setVisibility(View.VISIBLE);
                            TemplateView template = findViewById(R.id.my_template2);
                            template.setVisibility(View.VISIBLE);
                            template.setNativeAd(nativeAd);
                        }
                    }).withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            TemplateView template = findViewById(R.id.my_template2);
                            template.setVisibility(View.GONE);
                            if (template.getTemplateTypeName().equals("small_template")) {
                                showNativeBannerAd2FBExtra();
                            } else {
                                showNativeAd2FBExtra();
                            }
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            nativeExtraView.setVisibility(View.VISIBLE);
                        }
                    })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        } else {
            nativeExtraView.setVisibility(View.GONE);

        }
    }

    public void showNativeAd3Extra() {
        if (isConnected(this) && adsPrefernce.showNative3()) {
            AdLoader adLoader = new AdLoader.Builder(this, adsPrefernce.gNative3())
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            hideInhouseNative3();
                            TemplateView template = findViewById(R.id.my_template3);
                            template.setVisibility(View.VISIBLE);
                            template.setNativeAd(nativeAd);
                        }
                    }).withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            TemplateView template = findViewById(R.id.my_template3);
                            template.setVisibility(View.GONE);
                            if (template.getTemplateTypeName().equals("small_template")) {
                                showNativeBannerAd3FBExtra();
                            } else {
                                showNativeAd3FBExtra();
                            }
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                        }
                    })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        }
    }

    public void showNativeAd3Extra(View nativeExtraView) {
        if (isConnected(this) && adsPrefernce.showNative3()) {
            nativeExtraView.setVisibility(View.VISIBLE);
            AdLoader adLoader = new AdLoader.Builder(this, adsPrefernce.gNative3())
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            hideInhouseNative3();
                            nativeExtraView.setVisibility(View.VISIBLE);
                            TemplateView template = findViewById(R.id.my_template3);
                            template.setVisibility(View.VISIBLE);
                            template.setNativeAd(nativeAd);
                        }
                    }).withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            TemplateView template = findViewById(R.id.my_template3);
                            template.setVisibility(View.GONE);
                            if (template.getTemplateTypeName().equals("small_template")) {
                                showNativeBannerAd3FBExtra(nativeExtraView);
                            } else {
                                showNativeAd3FBExtra(nativeExtraView);
                            }
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            nativeExtraView.setVisibility(View.VISIBLE);
                        }
                    })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        } else {
            nativeExtraView.setVisibility(View.GONE);

        }
    }

    public void showNativeAdDialog(Dialog dialog) {
        if (isConnected(this) && adsPrefernce.showNative3()) {
            AdLoader adLoader = new AdLoader.Builder(this, adsPrefernce.gNative3())
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            TemplateView template = dialog.findViewById(R.id.my_template);
                            template.setVisibility(View.VISIBLE);
                            template.setNativeAd(nativeAd);
                        }
                    }).withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            if (adsPrefernce.showNative3_fb()) {
                                TemplateView template = dialog.findViewById(R.id.my_template);
                                if (template.getTemplateTypeName().equals("small_template")) {
                                    showNativeBannerAd3FBDialog(dialog);
                                } else {
                                    showNativeAd3FBDialog(dialog);
                                }
                            }
                        }
                    })
                    .build();
            adLoader.loadAd(new AdRequest.Builder().build());
        } else {
            if (adsPrefernce.showNative3_fb()) {
                TemplateView template = dialog.findViewById(R.id.my_template);
                if (template.getTemplateTypeName().equals("small_template")) {
                    showNativeBannerAd3FBDialog(dialog);
                } else {
                    showNativeAd3FBDialog(dialog);
                }
            }
        }
    }

    public void showNativeAdDialog(Dialog dialog, View nativeDialogView) {
        if (isConnected(this) && adsPrefernce.showNative3()) {
            nativeDialogView.setVisibility(View.VISIBLE);
            AdLoader adLoader = new AdLoader.Builder(this, adsPrefernce.gNative3())
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            nativeDialogView.setVisibility(View.VISIBLE);
                            TemplateView template = dialog.findViewById(R.id.my_template);
                            template.setVisibility(View.VISIBLE);
                            template.setNativeAd(nativeAd);
                        }
                    }).withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            if (adsPrefernce.showNative3_fb()) {
                                TemplateView template = dialog.findViewById(R.id.my_template);
                                if (template.getTemplateTypeName().equals("small_template")) {
                                    showNativeBannerAd3FBDialog(dialog, nativeDialogView);
                                } else {
                                    showNativeAd3FBDialog(dialog, nativeDialogView);
                                }
                            } else {
                                nativeDialogView.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onAdLoaded() {
                            super.onAdLoaded();
                            nativeDialogView.setVisibility(View.VISIBLE);
                        }
                    })
                    .build();
            adLoader.loadAd(new AdRequest.Builder().build());
        } else if (adsPrefernce.showNative3_fb()) {
            TemplateView template = dialog.findViewById(R.id.my_template);
            if (template.getTemplateTypeName().equals("small_template")) {
                showNativeBannerAd3FBDialog(dialog, nativeDialogView);
            } else {
                showNativeAd3FBDialog(dialog, nativeDialogView);
            }
        } else {
            nativeDialogView.setVisibility(View.GONE);
        }
    }


    public void loadSplashAd() {
        if (isConnected(this)) {
            loadInterstitial1FB();
            if (adsPrefernce.showAppopen1()) {
                AppOpenAd.load((Context) this, adsPrefernce.gAppopen1(), new AdRequest.Builder().build(), 1, new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                        super.onAdLoaded(appOpenAd);
                        appOpenAd1 = appOpenAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        appOpenAd1 = null;
                    }
                });
            } else if (adsPrefernce.showInter1()) {
                loadInterstitial1();
            }
        }
    }

    public void loadAppOpen2() {
        if (isConnected(this) && adsPrefernce.showAppopen2()) {
            AppOpenAd.load((Context) this, adsPrefernce.gAppopen2(), new AdRequest.Builder().build(), 1, new AppOpenAd.AppOpenAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                    super.onAdLoaded(appOpenAd);
                    appOpenAd2 = appOpenAd;
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    appOpenAd2 = null;
                }
            });
        }
    }

    public void loadAppOpen3() {
        if (isConnected(this) && adsPrefernce.showAppopen3()) {
            AppOpenAd.load((Context) this, adsPrefernce.gAppopen3(), new AdRequest.Builder().build(), 1, new AppOpenAd.AppOpenAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                    super.onAdLoaded(appOpenAd);
                    appOpenAd3 = appOpenAd;
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    appOpenAd3 = null;
                }
            });
        }
    }

    public void showSplashAdFirst(Activity context, Callable<Void> callable) {
        if (isConnected(this)) {
            if (adsPrefernce.showAppopen1()) {
                AppOpenAd.load((Context) this, adsPrefernce.gAppopen1(), new AdRequest.Builder().build(), 1, new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                        super.onAdLoaded(appOpenAd);
                        appOpenAd1 = appOpenAd;
                        if (appOpenAd1 != null) {
                            appOpenAd1.show(BaseAdsClass.this);
                            appOpenAd1.setFullScreenContentCallback(new FullScreenContentCallback() {
                                public void onAdDismissedFullScreenContent() {
                                    try {
                                        callable.call();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                public void onAdFailedToShowFullScreenContent(AdError adError) {
                                    appOpenAd1 = null;
                                    showInhouseInterAd(context,new InhouseInterstitialListener() {
                                        @Override
                                        public void onAdShown() {

                                        }

                                        @Override
                                        public void onAdDismissed() {
                                            try {
                                                callable.call();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }

                                public void onAdShowedFullScreenContent() {
                                    appOpenAd1 = null;
                                }
                            });
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        appOpenAd1 = null;
                        showInterstitial1FBSplash(context, callable);
                    }
                });
            } else if (adsPrefernce.showInter1()) {
                if (mInterstitialAd1 == null) {
                    MobileAds.initialize(this, new OnInitializationCompleteListener() {
                        @Override
                        public void onInitializationComplete(InitializationStatus initializationStatus) {
                        }
                    });
                    InterstitialAd.load(this, adsPrefernce.gInter1(), adRequest, new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            mInterstitialAd1 = interstitialAd;
                            if (mInterstitialAd1 != null) {
                                mInterstitialAd1.show((Activity) context);
                                mInterstitialAd1.setFullScreenContentCallback(new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        loadInterstitial1();
                                        try {
                                            callable.call();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        mInterstitialAd1 = null;
                                        showInterstitial1FBSplash(context, callable);
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        mInterstitialAd1 = null;
                                    }
                                });
                            } else {
                                try {
                                    callable.call();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            mInterstitialAd1 = null;
                            showInterstitial1FBSplash(context, callable);
                        }
                    });
                } else {
                    mInterstitialAd1.show((Activity) context);
                    mInterstitialAd1.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            loadInterstitial1();
                            try {
                                callable.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            mInterstitialAd1 = null;
                            showInterstitial1FBSplash(context, callable);
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            mInterstitialAd1 = null;
                        }
                    });
                }
            } else {
                try {
                    callable.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        currentAD++;

    }

    public void showSplashAdSecond(Activity context, Callable<Void> callable) {
        if (isConnected(this)) {
            if (adsPrefernce.showAppopen1()) {
                showAppOpen1Splash(context, callable);
            } else if (adsPrefernce.showInter1()) {
                showInterstitial1Splash(context, callable);
            } else if (adsPrefernce.showInter1_fb()) {
                showInterstitial1FB(context, callable);
            } else {
                showInhouseInterAd(context,new InhouseInterstitialListener() {
                    @Override
                    public void onAdShown() {

                    }

                    @Override
                    public void onAdDismissed() {
                        try {
                            callable.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } else {
            try {
                callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        currentAD++;

    }

    public void showAppOpen1Splash(Activity context, Callable<Void> callable) {
        if (appOpenAd1 != null) {
            appOpenAd1.show(BaseAdsClass.this);
            appOpenAd1.setFullScreenContentCallback(new FullScreenContentCallback() {
                public void onAdDismissedFullScreenContent() {
                    try {
                        callable.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    appOpenAd1 = null;
                    showInhouseInterAd(context,new InhouseInterstitialListener() {
                        @Override
                        public void onAdShown() {

                        }

                        @Override
                        public void onAdDismissed() {
                            try {
                                callable.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

                public void onAdShowedFullScreenContent() {
                    appOpenAd1 = null;
                }
            });
        } else {
            showInterstitial1FB(context, callable);
        }

    }

    public void showAppOpen2(Activity context, Callable<Void> callable) {
        if (currentAD % adsPrefernce.adCount() == 0 && isConnected(this) && adsPrefernce.showAppopen2()) {
            if (appOpenAd2 != null) {
                appOpenAd2.show(BaseAdsClass.this);
                appOpenAd2.setFullScreenContentCallback(new FullScreenContentCallback() {
                    public void onAdDismissedFullScreenContent() {
                        loadAppOpen2();
                        try {
                            callable.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        appOpenAd1 = null;
                        showInterstitial2FB(context, callable);
                    }

                    public void onAdShowedFullScreenContent() {
                        appOpenAd2 = null;
                    }
                });

            } else {
                showInterstitial2FB(context, callable);

            }
        } else {
            try {
                callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        currentAD++;
    }

    public void showAppOpen3(Activity context, Callable<Void> callable) {
        if (currentAD % adsPrefernce.adCount() == 0 && isConnected(this) && adsPrefernce.showAppopen3()) {
            if (appOpenAd3 != null) {
                appOpenAd3.show(BaseAdsClass.this);
                appOpenAd3.setFullScreenContentCallback(new FullScreenContentCallback() {
                    public void onAdDismissedFullScreenContent() {
                        loadAppOpen3();
                        try {
                            callable.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        appOpenAd1 = null;
                        showInterstitial3FB(context, callable);
                    }

                    public void onAdShowedFullScreenContent() {
                        appOpenAd3 = null;
                    }
                });
            } else {
                showInterstitial3FB(context, callable);
            }

        } else {
            try {
                callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        currentAD++;
    }

    private com.google.android.gms.ads.AdSize getAdSize() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        return com.google.android.gms.ads.AdSize.getPortraitAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    public void hideStatusBar(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void exitApp() {

        ConstantAds.IS_APP_KILLED = true;
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        finish();
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void withDelay(int delay, Callable<Void> callable) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    callable.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, delay);
    }

    public void withDelay(Activity context, int delay, String message, Callable<Void> callable) {
        showProgress(context, message);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismisProgress(context);
                try {
                    callable.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, delay);
    }

    public void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void networkAvailable() {
        withDelay(500, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                if (!isLoaded_ADS) {
                    getAdsx();
                    getAdsFB();
                }

                return null;
            }
        });

    }

    @Override
    public void networkUnavailable() {

    }
}
