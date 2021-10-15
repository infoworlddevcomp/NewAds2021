package com.adscomapanion2021;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import com.newAds2021.adsmodels.ConstantAds;
import com.newAds2021.adutils.BaseAdsClass;


public class BaseActivity extends BaseAdsClass {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConstantAds.setAdsUrlID("https://script.google.com/macros/s/AKfycbwWa0oIwNsZ4b7b-aIGi61iyJ98XFCy2kbfXNC-ZhiIkHtlHu2R88r-gzHc7eigJykh7A/");
        ConstantAds.setIHAdsID("https://script.google.com/macros/s/AKfycbwV0QJZQ0bg6w_Rqc4w84OFv4cUSI_YZuKKd-exgyFzw-lXSyJ6vbV8-cT1kUBc4MZWwA/");
        ConstantAds.preloadAppopen(true);
        ConstantAds.preloadInterstitial(true);
    }

}
