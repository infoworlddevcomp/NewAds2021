package com.adscomapanion2021;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.newAds2021.Interfaces.InhouseBannerListener;
import com.newAds2021.Interfaces.InhouseInterstitialListener;
import com.newAds2021.Interfaces.InhouseNativeListener;
import com.newAds2021.adsmodels.AdsPrefernce;
import com.newAds2021.adsmodels.ConstantAds;

import java.util.concurrent.Callable;

public class MainActivity extends BaseActivity {

    AdsPrefernce adsPrefernce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        withDelay(3000, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
//                AppService("1.0");
                ShowQurekaInterstationAds(MainActivity.this,1);

                return null;
            }
        });





//        setAdsUrlID("AKfycbwWa0oIwNsZ4b7b-aIGi61iyJ98XFCy2kbfXNC-ZhiIkHtlHu2R88r-gzHc7eigJykh7A/exec");
//        ConstantAds.setAdsUrlID("AKfycbwWa0oIwNsZ4b7b-aIGi61iyJ98XFCy2kbfXNC-ZhiIkHtlHu2R88r-gzHc7eigJykh7A/exec");


    }
    public void Inter1(View view) {
       // loadAppOpen1();

        showInterstitialAd(this, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Toast.makeText(MainActivity.this, "Inter1Dismissed", Toast.LENGTH_SHORT).show();
                return null;
            }
        });
    }

    public void Inter2(View view) {
        showInterstitial2(this, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Toast.makeText(MainActivity.this, "Inter2Dismissed", Toast.LENGTH_SHORT).show();
                return null;
            }
        });
    }

    public void Inter3(View view) {
        showInterstitial3(this, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Toast.makeText(MainActivity.this, "Inter3Dismissed", Toast.LENGTH_SHORT).show();
                return null;
            }
        });
    }

    public void showBanner(View view) {

      //  showMAXBanner();
        showLargeBanner3();
    }

    public void showNative(View view) {
        showNativeAd();
    }

    public void AppOpen1(View view) {
//        showAppOpen1(new Callable<Void>() {
//            @Override
//            public Void call() throws Exception {
//                Toast.makeText(MainActivity.this, "AppOpen1 Dismissed", Toast.LENGTH_SHORT).show();
//                return null;
//            }
//        });
    }

    public void showIHBanner(View view) {
        LinearLayout banner_adView = findViewById(R.id.banner_adView);
        banner_adView.setVisibility(View.VISIBLE);
        showInhouseBannerAd(new InhouseBannerListener() {
            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onAdShowFailed() {

            }
        });
    }

    public void showIHInter(View view) {
//        showInhouseInterAd(new InhouseInterstitialListener() {
//            @Override
//            public void onAdShown() {
//
//            }
//
//            @Override
//            public void onAdDismissed() {
//                toast("closed");
//            }
//        });
    }

    public void showIHNative(View view) {
        showInhouseNativeAd(false ,findViewById(R.id.native_ad_container), new InhouseNativeListener() {
            @Override
            public void onAdLoaded() {
                findViewById(R.id.native_ad_container).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdShowFailed() {

            }
        });
    }
}