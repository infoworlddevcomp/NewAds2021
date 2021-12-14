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

        ConstantAds.setAdsURL("1w-FaPp1dmNwYi2t5zhg27B2Bm4NJR1z1xC58uknnBbo");
       // getAdsx();
        ConstantAds.preloadAppopen(true);
        ConstantAds.preloadInterstitial(true);

    }

}
