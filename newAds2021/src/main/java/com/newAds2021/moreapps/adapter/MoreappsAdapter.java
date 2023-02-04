package com.newAds2021.moreapps.adapter;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.newAds2021.R;
import com.newAds2021.adsmodels.AppsDetails;
import com.newAds2021.adsmodels.ConstantAds;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MoreappsAdapter extends RecyclerView.Adapter<MoreappsAdapter.Viewholder> {

    private Context context;
    private List<AppsDetails> appsDetailsList;
    String type = "";
    String theme = "";
     int appnameTextColor = 0;
     Drawable installButtonBackground = null;
     int installButtonTextColor = 0;
     Drawable adBackground = null;
     Drawable itemBackground = null;


    public MoreappsAdapter(Context context, List<AppsDetails> appsDetailsList, String type, String theme, int appnameTextColor, Drawable installButtonBackground,
                           int installButtonTextColor, Drawable adIcon, Drawable itemBackground) {
        this.context = context;
        this.appsDetailsList = appsDetailsList;
        this.type = type;
        this.theme = theme;
        this.appnameTextColor = appnameTextColor;
        this.installButtonBackground = installButtonBackground;
        this.installButtonTextColor = installButtonTextColor;
        this.itemBackground = itemBackground;
        adBackground = adIcon;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = null;
        if (type.equals("grid")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_moreapps_main, parent, false);
        } else if (type.equals("linear")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_moreapps_main2, parent, false);
        }
        return new Viewholder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MoreappsAdapter.Viewholder holder, int position) {
        if (type.equals("grid") || type.equals("linear")) {

            if (theme.equals("light")) {
                if (appnameTextColor != 0) {
                    holder.tvname.setTextColor(appnameTextColor);
                } else {
                    holder.tvname.setTextColor(context.getResources().getColor(R.color.black));
                }
                if (installButtonTextColor != 0) {
                    holder.tv_install.setTextColor(installButtonTextColor);
                } else {
                    holder.tv_install.setTextColor(context.getResources().getColor(R.color.black));
                }
                if (installButtonBackground != null) {
                    holder.tv_install.setBackground(installButtonBackground);
                }
                if (adBackground != null) {
                    holder.tv_ad_moreapps.setBackground(adBackground);
                }
                if (itemBackground != null) {
                    holder.fl_color.setBackground(itemBackground);
                } else {
                    holder.fl_color.setBackground(context.getResources().getDrawable(R.drawable.bg_moreapps_light));
                }
            } else {
                if (appnameTextColor != 0) {
                    holder.tvname.setTextColor(appnameTextColor);
                } else {
                    holder.tvname.setTextColor(context.getResources().getColor(R.color.white));
                }
                if (installButtonTextColor != 0) {
                    holder.tv_install.setTextColor(installButtonTextColor);
                } else {
                    holder.tv_install.setTextColor(context.getResources().getColor(R.color.white));
                }
                if (installButtonBackground != null) {
                    holder.tv_install.setBackground(installButtonBackground);
                }
                if (adBackground != null) {
                    holder.tv_ad_moreapps.setBackground(adBackground);
                }

                if (itemBackground != null) {
                    holder.fl_color.setBackground(itemBackground);
                } else {
                    holder.fl_color.setBackground(context.getResources().getDrawable(R.drawable.bg_moreapps_dark));
                }
            }


            holder.tv_install.setText(appsDetailsList.get(position).getButton_text());
            holder.tvname.setText(appsDetailsList.get(position).getAppName());
            holder.tvname.setSelected(true);
            Glide.with(context).load(appsDetailsList.get(position).getAppIcon()).placeholder(R.drawable.bg_btn_moreapps).into(holder.appimage);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse(appsDetailsList.get(position).getAppLink())));
                    } catch (ActivityNotFoundException e) {
                    }
                }
            });
        }

    }


    @Override
    public int getItemCount() {

//        if (type.equals("grid") || type.equals("linear")) {
//            return appsDetailsList.size();
//        } else if (type.equals("category")) {
////            return ConstantAds.category.size();
//            return 1;
//        }
        return appsDetailsList.size();
    }

    public boolean checkCategory(ArrayList<String> category, String str) {
//
        for (int i = 0; i < category.size(); i++) {
            if (str.equals(category.get(i))) {
                return true;
            } else if (i == category.size()) {
                return false;
            }
        }
        return false;
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private ImageView appimage;
        private FrameLayout fl_color;
        private TextView tvname, tv_catName, tv_install, tv_ad_moreapps;

        public Viewholder(@NonNull @NotNull View itemView) {
            super(itemView);

            appimage = itemView.findViewById(R.id.appimage);
            fl_color = itemView.findViewById(R.id.fl_color);
            tvname = itemView.findViewById(R.id.tvname);
            tv_install = itemView.findViewById(R.id.tv_install);
            tv_catName = itemView.findViewById(R.id.tv_catName);
            tv_ad_moreapps = itemView.findViewById(R.id.tv_ad_moreapps);

        }
    }
}
