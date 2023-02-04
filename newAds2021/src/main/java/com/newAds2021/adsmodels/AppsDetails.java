package com.newAds2021.adsmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class  AppsDetails {

@SerializedName("id")
@Expose
private String id;
@SerializedName("App_icon")
@Expose
private String appIcon;
@SerializedName("App_name")
@Expose
private String appName;
@SerializedName("App_link")
@Expose
private String appLink;
@SerializedName("Show_ads")
@Expose
private Boolean showAds;
@SerializedName("Category")
@Expose
private String category;
@SerializedName("Open_in")
@Expose
private String openIn;
@SerializedName("botton_text")
@Expose
private String button_text;

public String getId() {
return id;
}

public void setId(String id) {
this.id = id;
}

public String getAppIcon() {
return appIcon;
}

public void setAppIcon(String appIcon) {
this.appIcon = appIcon;
}

public String getAppName() {
return appName;
}

public void setAppName(String appName) {
this.appName = appName;
}

public String getAppLink() {
return appLink;
}

public void setAppLink(String appLink) {
this.appLink = appLink;
}

public Boolean getShowAds() {
return showAds;
}

public void setShowAds(Boolean showAds) {
this.showAds = showAds;
}

public String getCategory() {
return category;
}

public void setCategory(String category) {
this.category = category;
}

public String getOpenIn() {
return openIn;
}

public void setOpenIn(String openIn) {
this.openIn = openIn;
}

public String getButton_text() {
return button_text;
}

public void setButton_text(String onOff) {
this.button_text = button_text;
}

    public AppsDetails(String id, String appIcon, String appName, String appLink, Boolean showAds, String category, String openIn, String button_text) {
        this.id = id;
        this.appIcon = appIcon;
        this.appName = appName;
        this.appLink = appLink;
        this.showAds = showAds;
        this.category = category;
        this.openIn = openIn;
        this.button_text = button_text;
    }
}