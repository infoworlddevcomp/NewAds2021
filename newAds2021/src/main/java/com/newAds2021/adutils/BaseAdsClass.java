package com.newAds2021.adutils;

import static com.newAds2021.adsmodels.ConstantAds.dismisProgress;
import static com.newAds2021.adsmodels.ConstantAds.showProgress;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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
import com.newAds2021.adsmodels.AdsDetails;
import com.newAds2021.adsmodels.AdsPrefernce;
import com.newAds2021.adsmodels.AdsData;
import com.newAds2021.adsmodels.AppsDetails;
import com.newAds2021.adsmodels.IHAPI;
import com.newAds2021.adsmodels.IHAdsData;
import com.newAds2021.adsmodels.IhAdsDetail;
import com.newAds2021.adsmodels.ConstantAds;
import com.newAds2021.nativeadtemplate.TemplateView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;

import retrofit2.Call;
import retrofit2.Response;

//"AKfycbwWa0oIwNsZ4b7b-aIGi61iyJ98XFCy2kbfXNC-ZhiIkHtlHu2R88r-gzHc7eigJykh7A/exec"
public class BaseAdsClass extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    private NetworkStateReceiver networkStateReceiver;
    public static boolean isvalidInstall = false;

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
    static ArrayList<AppsDetails> moreAppsArrayList;

    public static int interNo = 1;
    public static int bannerNo = 1;
    public static int nativeNo = 1;
    public static int rewardNo = 1;


    Dialog serviceDialog;
    public static int currentAD = 1;
    ArrayList<AdsData> adsDetailsArrayList;
    public static InterstitialAd mInterstitialAd1, mInterstitialAd2, mInterstitialAd3 = null;
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


        withDelay(500, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                if (!isLoaded_ADS) {
                    getAds();
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

    public void getAds() {
        API.apiInterface().getAds().enqueue(new retrofit2.Callback<AdsDetails>() {
            @Override
            public void onResponse(@NonNull Call<AdsDetails> call, @NonNull Response<AdsDetails> response) {
                AdsDetails adsDetails = response.body();
                adsDetailsArrayList = new ArrayList<>();
                ihAdsDetails = new ArrayList<>();
                try {
                    if (adsDetails.getAdsData() != null) {
                        adsDetailsArrayList = adsDetails.getAdsData();
                        ihAdsDetails = adsDetails.getIhAdsDetail();
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
                            }
                            if (ConstantAds.PRELOAD_REWARD) {
                                loadRewardedAds();
                            }
                            if (ConstantAds.PRELOAD_APPOPEN) {
                                loadAppOpenAds(BaseAdsClass.this);
                            }

                        }


                        if (ihAdsDetails != null && ihAdsDetails.size() > 0) {
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
        Log.e("totalInter", String.valueOf(totalAds));
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

    public void showInhouseInterAd(InhouseInterstitialListener inhouseInterstitialListener) {

        if (adsPrefernce.isInHouseAdLoaded()) {
            if (isConnected(this)) {
                if (finalIHAds.size() != 0) {
                    // get Interstitial Data
                    ArrayList<IhAdsDetail> interAdDetails = adsPrefernce.getInHouseAds();

                    if (finalIHAds.size() != 0) {
                        // ad to show from position
                        int current = getCurrentInterAd(finalIHAds.size());


                        final Dialog interDialog = new Dialog(this);
                        interDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        interDialog.setContentView(R.layout.ad_interstitial);
                        interDialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
                        interDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
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
                        IhAdsDetail interAd = interAdDetails.get(current);

                        // icon
                        Glide.with(this).load(interAd.getIcon()).into(iv_ad_icon);
                        // banner
                        Glide.with(this).load(interAd.getBigimage()).into(iv_inter_main_banner);
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
                            if (!isAppInstalled(getAppIdFromAppLink(interAd.getApplink()))) {
                                interDialog.show();
                                inhouseInterstitialListener.onAdShown();
                            } else {
                                inhouseInterstitialListener.onAdDismissed();
                            }
                        } else {
                            interDialog.show();
                            inhouseInterstitialListener.onAdShown();
                        }

                    } else {
                        inhouseInterstitialListener.onAdDismissed();
                    }

                } else {
                    if (adsPrefernce.isInHouseAdLoaded()) {
                        // get Interstitial Data
                        ArrayList<IhAdsDetail> savedInterAdDetails = adsPrefernce.getInHouseAds();


                        if (savedInterAdDetails.size() != 0) {
                            // ad to show from position
                            int current = getCurrentInterAd(savedInterAdDetails.size());


                            final Dialog interDialog = new Dialog(this);
                            interDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            interDialog.setContentView(R.layout.ad_interstitial);
                            interDialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
                            interDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
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

                            // icon
                            Glide.with(this).load(interAd.getIcon()).into(iv_ad_icon);
                            // banner
                            Glide.with(this).load(interAd.getBigimage()).into(iv_inter_main_banner);
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
                                if (!isAppInstalled(getAppIdFromAppLink(interAd.getApplink()))) {
                                    interDialog.show();
                                    inhouseInterstitialListener.onAdShown();
                                } else {
                                    inhouseInterstitialListener.onAdDismissed();
                                }
                            } else {
                                interDialog.show();
                                inhouseInterstitialListener.onAdShown();
                            }
                        } else {
                            inhouseInterstitialListener.onAdDismissed();
                        }

                    } else {
                        inhouseInterstitialListener.onAdDismissed();
                    }
                }
            } else {
                // get Interstitial Data
                ArrayList<IhAdsDetail> savedInterAdDetails = adsPrefernce.getInHouseAds();


                if (savedInterAdDetails.size() != 0) {
                    // ad to show from position
                    int current = getCurrentInterAd(savedInterAdDetails.size());


                    final Dialog interDialog = new Dialog(this);
                    interDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    interDialog.setContentView(R.layout.ad_interstitial);
                    interDialog.getWindow().setBackgroundDrawableResource(android.R.color.white);
                    interDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
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

                    // icon
                    Glide.with(this).load(interAd.getIcon()).into(iv_ad_icon);
                    // banner
                    Glide.with(this).load(interAd.getBigimage()).into(iv_inter_main_banner);
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
                        if (!isAppInstalled(getAppIdFromAppLink(interAd.getApplink()))) {
                            interDialog.show();
                            inhouseInterstitialListener.onAdShown();
                        } else {
                            inhouseInterstitialListener.onAdDismissed();
                        }
                    } else {
                        interDialog.show();
                        inhouseInterstitialListener.onAdShown();
                    }
                } else {
                    inhouseInterstitialListener.onAdDismissed();
                }

            }

        } else {
            inhouseInterstitialListener.onAdDismissed();
        }
    }

    public void showInhouseBannerAd(InhouseBannerListener inhouseBannerListener) {
        if (adsPrefernce.isInHouseAdLoaded()) {
            if (isConnected(this)) {
                if (finalIHAds.size() != 0) {
                    LinearLayout banner_adView = findViewById(R.id.banner_adView);
                    banner_adView.removeAllViews();

                    // get Interstitial Data
                    ArrayList<IhAdsDetail> ihAdsDetails = adsPrefernce.getInHouseAds();

                    // ad to show from position
                    int current = getCurrentBannerAd(finalIHAds.size());

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
                    IhAdsDetail bannerAd;

                    RelativeLayout lay_banner_ad = findViewById(R.id.lay_banner_ad);
                    lay_banner_ad.setVisibility(View.VISIBLE);


                    // set Banner Data
                    bannerAd = ihAdsDetails.get(current);

                    // icon
                    if (!this.isFinishing()) {
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


                } else {
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
                    } else {
                        inhouseBannerListener.onAdShowFailed();
                    }
                }
            } else {
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
        }
    }

    private void inflateNativeAdInHouse(Boolean isSmall, CardView cardView) {

        // Add the Ad view into the ad container.
//        NativeAdLayout nativeAdLayout = new NativeAdLayout(context);
        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout adViews = (RelativeLayout) inflater.inflate(R.layout.ad_native, cardView, false);
        cardView.removeAllViews();
        if (finalIHAds.size() != 0) {
            cardView.setBackground(getResources().getDrawable(R.drawable.gnt_rounded_corners_shape));

            cardView.addView(adViews);

            // get Interstitial Data
            ArrayList<IhAdsDetail> nativeDetails = adsPrefernce.getInHouseAds();

            // ad to show from position
            int current = getCurrentNativeAd(finalIHAds.size());

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
    void loadRewardedAds() {
        loadRewardAd1();
        loadRewardAd2();
        loadRewardAd3();
    }

    void loadAppOpenAds(Context context) {
        loadAppOpen1();
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
                        Glide.with(this).load(adsPrefernce.adBannerUrl()).into(iv_ad_banner);
                        Glide.with(this).load(adsPrefernce.adIconUrl()).into(iv_app_icon);
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
                    Glide.with(this).load(adsPrefernce.adBannerUrl()).into(iv_ad_banner);
                    Glide.with(this).load(adsPrefernce.adIconUrl()).into(iv_app_icon);
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
            Glide.with(this).load(adsPrefernce.adBannerUrl()).into(iv_ad_banner);
            Glide.with(this).load(adsPrefernce.adIconUrl()).into(iv_app_icon);
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

    public void showNativeAd() {
        if (nativeNo == 1) {
            showNativeAd1();
        } else if (nativeNo == 2) {
            showNativeAd2();
        } else if (nativeNo == 3) {
            showNativeAd3();
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

    public void showRewardedAd(OnRewardAdClosedListener onRewardAdClosedListener){
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


    public void showInterstitialAd(Context context, Callable<Void> callable) {
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

    public void showSplashInterstitial1(Context context, Callable<Void> params) {
        if (adsPrefernce.allowAccess()) {
            if (currentAD % adsPrefernce.adCount() == 0 && isConnected(this) && adsPrefernce.showInter1()) {
                if (mInterstitialAd1 != null) {
                    if (adsPrefernce.showloading()) {
                        withDelay(ConstantAds.AD_DELAY, ConstantAds.AD_MESSAGE, new Callable<Void>() {
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
                    }

                } else {
                    showInhouseInterAd(new InhouseInterstitialListener() {
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

    public void showInterstitial1(Context context, Callable<Void> params) {
        if (currentAD % adsPrefernce.adCount() == 0 && isConnected(this) && adsPrefernce.showInter1()) {
            if (mInterstitialAd1 != null) {
                if (adsPrefernce.showloading()) {
                    withDelay(ConstantAds.AD_DELAY, ConstantAds.AD_MESSAGE, new Callable<Void>() {
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
                }

            } else {
                showInhouseInterAd(new InhouseInterstitialListener() {
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
        currentAD++;
    }

    public void showInterstitial2(Context context, Callable<Void> params) {
        if (currentAD % adsPrefernce.adCount() == 0 && isConnected(this) && adsPrefernce.showInter2()) {
            if (mInterstitialAd2 != null) {
                if (adsPrefernce.showloading()) {
                    withDelay(ConstantAds.AD_DELAY, ConstantAds.AD_MESSAGE, new Callable<Void>() {
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
                                    try {
                                        params.call();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
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
                            try {
                                params.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            mInterstitialAd2 = null;
                        }
                    });
                }

            } else {
                showInhouseInterAd(new InhouseInterstitialListener() {
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
        currentAD++;

    }

    public void showInterstitial3(Context context, Callable<Void> params) {
        if (currentAD % adsPrefernce.adCount() == 0 && isConnected(this) && adsPrefernce.showInter3()) {
            if (mInterstitialAd3 != null) {
                if (adsPrefernce.showloading()) {
                    withDelay(ConstantAds.AD_DELAY, ConstantAds.AD_MESSAGE, new Callable<Void>() {
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
                                    try {
                                        params.call();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
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
                            try {
                                params.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            mInterstitialAd3 = null;
                        }
                    });

                }
            } else {
                showInhouseInterAd(new InhouseInterstitialListener() {
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

    void hideInhouseNativeAdapter(CardView cardView) {
//        CardView cardView = findViewById(R.id.native_ad_container);
        cardView.setVisibility(View.GONE);

    }

    public void showInhouseNativeAd(Boolean isSmall, CardView cardView, InhouseNativeListener
            inhouseNativeListener) {
        if (adsPrefernce.isInHouseAdLoaded()) {
            if (isConnected(this)) {
                if (finalIHAds.size() != 0) {
                    cardView.setVisibility(View.VISIBLE);
                    inflateNativeAdInHouse(isSmall, cardView);
                    inhouseNativeListener.onAdLoaded();
                } else {
                    if (adsPrefernce.isInHouseAdLoaded()) {
                        cardView.setVisibility(View.VISIBLE);
                        inflateNativeAdInHouse(isSmall, cardView);
                        inhouseNativeListener.onAdLoaded();
                    } else {
                        inhouseNativeListener.onAdShowFailed();
                    }

                }
            } else {
                cardView.setVisibility(View.VISIBLE);
                inflateNativeAdInHouse(isSmall, cardView);
                inhouseNativeListener.onAdLoaded();
            }

        } else {
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
                public void onAdLoaded() {
                    super.onAdLoaded();
                    hideInhouseBanner();
                }
            });

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
                public void onAdLoaded() {
                    super.onAdLoaded();
                    hideInhouseBanner();
                }
            });

        }
    }

    public void showNativeAdAdapter(TemplateView view, CardView cardView) {
        if (nativeNo == 1) {
            showNativeAd1Adapter(view, cardView);
        } else if (nativeNo == 2) {
            showNativeAd2Adapter(view, cardView);
        } else if (nativeNo == 3) {
            showNativeAd3Adapter(view, cardView);
        }
        setNativeNo();
    }

    void showNativeAd1Adapter(TemplateView view, CardView cardView) {
        if (isConnected(this) && adsPrefernce.showNative1()) {
            AdLoader adLoader = new AdLoader.Builder(this, adsPrefernce.gNative1())
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
//                            hideInhouseNativeAdapter(cardView);
//                            TemplateView template = findViewById(R.id.my_template);
                            view.setVisibility(View.VISIBLE);
                            view.setNativeAd(nativeAd);
                        }
                    }).withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
//                            TemplateView template = findViewById(R.id.my_template);
                            view.setVisibility(View.GONE);
                            showInhouseNativeAd(view.getTemplateTypeName().equals("small_template"), cardView, new InhouseNativeListener() {
                                @Override
                                public void onAdLoaded() {
                                }

                                @Override
                                public void onAdShowFailed() {

                                }
                            });
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

    void showNativeAd2Adapter(TemplateView view, CardView cardView) {
        if (isConnected(this) && adsPrefernce.showNative2()) {
            AdLoader adLoader = new AdLoader.Builder(this, adsPrefernce.gNative2())
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
//                            hideInhouseNativeAdapter(cardView);
//                            TemplateView template = findViewById(R.id.my_template);
                            view.setVisibility(View.VISIBLE);
                            view.setNativeAd(nativeAd);
                        }
                    }).withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
//                            TemplateView template = findViewById(R.id.my_template);
                            view.setVisibility(View.GONE);
                            showInhouseNativeAd(view.getTemplateTypeName().equals("small_template"), cardView, new InhouseNativeListener() {
                                @Override
                                public void onAdLoaded() {
                                }

                                @Override
                                public void onAdShowFailed() {

                                }
                            });
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

    void showNativeAd3Adapter(TemplateView view, CardView cardView) {
        if (isConnected(this) && adsPrefernce.showNative3()) {
            AdLoader adLoader = new AdLoader.Builder(this, adsPrefernce.gNative3())
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
//                            hideInhouseNativeAdapter(cardView);
//                            TemplateView template = findViewById(R.id.my_template);
                            view.setVisibility(View.VISIBLE);
                            view.setNativeAd(nativeAd);
                        }
                    }).withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
//                            TemplateView template = findViewById(R.id.my_template);
                            view.setVisibility(View.GONE);
                            showInhouseNativeAd(view.getTemplateTypeName().equals("small_template"), cardView, new InhouseNativeListener() {
                                @Override
                                public void onAdLoaded() {
                                }

                                @Override
                                public void onAdShowFailed() {

                                }
                            });
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
                            showInhouseNativeAd(template.getTemplateTypeName().equals("small_template"), findViewById(R.id.native_ad_container), new InhouseNativeListener() {
                                @Override
                                public void onAdLoaded() {
                                }

                                @Override
                                public void onAdShowFailed() {

                                }
                            });
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
                            showInhouseNativeAd(template.getTemplateTypeName().equals("small_template"), findViewById(R.id.native_ad_container), new InhouseNativeListener() {
                                @Override
                                public void onAdLoaded() {
                                }

                                @Override
                                public void onAdShowFailed() {

                                }
                            });
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
                            showInhouseNativeAd(template.getTemplateTypeName().equals("small_template"), findViewById(R.id.native_ad_container), new InhouseNativeListener() {
                                @Override
                                public void onAdLoaded() {
                                }

                                @Override
                                public void onAdShowFailed() {

                                }
                            });
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

    public void showNativeAd2Extra() {
        if (isConnected(this) && adsPrefernce.showNative2()) {
            AdLoader adLoader = new AdLoader.Builder(this, adsPrefernce.gNative2())
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            hideInhouseNative();
                            TemplateView template = findViewById(R.id.my_template2);
                            template.setVisibility(View.VISIBLE);
                            template.setNativeAd(nativeAd);
                        }
                    }).withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            TemplateView template = findViewById(R.id.my_template);
                            template.setVisibility(View.GONE);
                            showInhouseNativeAd(template.getTemplateTypeName().equals("small_template"), findViewById(R.id.native_ad_container), new InhouseNativeListener() {
                                @Override
                                public void onAdLoaded() {
                                }

                                @Override
                                public void onAdShowFailed() {

                                }
                            });
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

    public void showNativeAd3Extra() {
        if (isConnected(this) && adsPrefernce.showNative3()) {
            AdLoader adLoader = new AdLoader.Builder(this, adsPrefernce.gNative3())
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(NativeAd nativeAd) {
                            hideInhouseNative();
                            TemplateView template = findViewById(R.id.my_template3);
                            template.setVisibility(View.VISIBLE);
                            template.setNativeAd(nativeAd);
                        }
                    }).withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            TemplateView template = findViewById(R.id.my_template);
                            template.setVisibility(View.GONE);
                            showInhouseNativeAd(template.getTemplateTypeName().equals("small_template"), findViewById(R.id.native_ad_container), new InhouseNativeListener() {
                                @Override
                                public void onAdLoaded() {
                                }

                                @Override
                                public void onAdShowFailed() {

                                }
                            });
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
                    })
                    .build();
            adLoader.loadAd(new AdRequest.Builder().build());
        }
    }

    public void loadAppOpen1() {
        if (isConnected(this) && adsPrefernce.showAppopen1()) {
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

    public void showAppOpen1(Callable<Void> callable) {
        if (currentAD % adsPrefernce.adCount() == 0 && isConnected(this) && adsPrefernce.showAppopen1()) {
            if (appOpenAd1 != null) {
                appOpenAd1.show(BaseAdsClass.this);
                appOpenAd1.setFullScreenContentCallback(new FullScreenContentCallback() {
                    public void onAdDismissedFullScreenContent() {
                        loadAppOpen1();
                        try {
                            callable.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        appOpenAd1 = null;
                        try {
                            callable.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    public void onAdShowedFullScreenContent() {
                        appOpenAd1 = null;
                    }
                });
            } else {
                showInhouseInterAd(new InhouseInterstitialListener() {
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

    public void showAppOpen2(Callable<Void> callable) {
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
                        try {
                            callable.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    public void onAdShowedFullScreenContent() {
                        appOpenAd2 = null;
                    }
                });

            } else {
                showInhouseInterAd(new InhouseInterstitialListener() {
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

    public void showAppOpen3(Callable<Void> callable) {
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
                        try {
                            callable.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    public void onAdShowedFullScreenContent() {
                        appOpenAd3 = null;
                    }
                });
            } else {
                showInhouseInterAd(new InhouseInterstitialListener() {
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

    private com.google.android.gms.ads.AdSize getAdSize() {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        return com.google.android.gms.ads.AdSize.getPortraitAnchoredAdaptiveBannerAdSize(this, adWidth);
    }

    public void exitApp() {
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

    public void withDelay(int delay, String message, Callable<Void> callable) {
        showProgress(this, message);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismisProgress();
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
                    getAds();
                }

                return null;
            }
        });

    }

    @Override
    public void networkUnavailable() {

    }
}
