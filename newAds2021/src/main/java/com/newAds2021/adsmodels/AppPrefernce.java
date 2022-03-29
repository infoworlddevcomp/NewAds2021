package com.newAds2021.adsmodels;

import android.content.Context;
import android.content.SharedPreferences;

import com.newAds2021.R;


public class AppPrefernce {

    Context context;
    SharedPreferences appPreference;
    SharedPreferences.Editor editor;

    public AppPrefernce(Context context) {
        this.context = context;
        appPreference = context.getSharedPreferences("MyAppPrefrence", Context.MODE_PRIVATE);
        editor = appPreference.edit();

    }

    public void setAppDefaults(String show_movies, boolean show_telegram_blog, String show_watch_cricket, String show_channel,
                               boolean show_first_activity, boolean show_two_activity, boolean show_three_activity, boolean show_four_activity,
                               String create_channel,String live_score,boolean ipl_cricket,boolean show_get_password,String app_password,String show_blog_movies, String show_mymovie,
                               String qureka_url1,String qureka_url2,String qureka_url3,boolean show_max_banner,boolean show_max_inter,String max_banner_id,String max_inter_id,
                               boolean show_max_native,String max_native_id,boolean show_max_small_native,String max_small_native_id) {
        if (appPreference != null) {
            editor = appPreference.edit();
            editor.putString("show_movies", show_movies);
            editor.putBoolean("show_telegram_blog", show_telegram_blog);
            editor.putString("show_watch_cricket", show_watch_cricket);
            editor.putString("show_channel", show_channel);
            editor.putBoolean("show_first_activity", show_first_activity);
            editor.putBoolean("show_two_activity", show_two_activity);
            editor.putBoolean("show_three_activity", show_three_activity);
            editor.putBoolean("show_four_activity", show_four_activity);
            editor.putString("create_channel", create_channel);
            editor.putString("live_score", live_score);
            editor.putBoolean("ipl_cricket", ipl_cricket);
            editor.putBoolean("show_get_password", show_get_password);
            editor.putString("app_password", app_password);
            editor.putString("show_blog_movies", show_blog_movies);
            editor.putString("show_mymovie", show_mymovie);
            editor.putString("qureka_url1", qureka_url1);
            editor.putString("qureka_url2", qureka_url2);
            editor.putString("qureka_url3", qureka_url3);
            editor.putBoolean("show_max_banner", show_max_banner);
            editor.putBoolean("show_max_inter", show_max_inter);
            editor.putString("max_banner_id", max_banner_id);
            editor.putString("max_inter_id", max_inter_id);
            editor.putBoolean("show_max_native", show_max_native);
            editor.putString("max_native_id", max_native_id);
            editor.putBoolean("show_max_small_native", show_max_small_native);
            editor.putString("max_small_native_id", max_small_native_id);


            editor.apply();
        }
    }

    public String showMovies() {
        String var = "";
        if (appPreference != null) {
            var = appPreference.getString("show_movies", context.getString(R.string.show_movies));
        }
        return var;
    }

    public boolean showTelegramBlog() {
        boolean output = false;
        if (appPreference != null) {
            output = appPreference.getBoolean("show_telegram_blog", true);
        }
        return output;
    }

    public String showWatchCricket() {
        String var = "";
        if (appPreference != null) {
            var = appPreference.getString("show_watch_cricket", context.getString(R.string.showWatchCricket));
        }
        return var;
    }

    public String showChannel() {
        String var = "";
        if (appPreference != null) {
            var = appPreference.getString("show_channel", context.getString(R.string.showChannel));
        }
        return var;
    }

    public boolean showFirstActivity() {
        boolean output = false;
        if (appPreference != null) {
            output = appPreference.getBoolean("show_first_activity", true);
        }
        return output;
    }

    public boolean showTwoActivity() {
        boolean output = false;
        if (appPreference != null) {
            output = appPreference.getBoolean("show_two_activity", true);
        }
        return output;
    }

    public boolean showThreeActivity() {
        boolean output = false;
        if (appPreference != null) {
            output = appPreference.getBoolean("show_three_activity", true);
        }
        return output;
    }
    public boolean showFourActivity() {
        boolean output = false;
        if (appPreference != null) {
            output = appPreference.getBoolean("show_four_activity", true);
        }
        return output;
    }
    public String showCreateChannel() {
        String var = "";
        if (appPreference != null) {
            var = appPreference.getString("create_channel", context.getString(R.string.showChannel));
        }
        return var;
    }
    public String showLiveScore() {
        String var = "";
        if (appPreference != null) {
            var = appPreference.getString("live_score", context.getString(R.string.showChannel));
        }
        return var;
    }
    public boolean showIplCricket() {
        boolean output = false;
        if (appPreference != null) {
            output = appPreference.getBoolean("ipl_cricket", true);
        }
        return output;
    }

    public boolean showGetPassword() {
        boolean output = false;
        if (appPreference != null) {
            output = appPreference.getBoolean("show_get_password", true);
        }
        return output;
    }
    public String AppPassword() {
        String var = "";
        if (appPreference != null) {
            var = appPreference.getString("app_password", context.getString(R.string.AppPassword));
        }
        return var;
    }
    public String ShowBlogMovies() {
        String var = "";
        if (appPreference != null) {
            var = appPreference.getString("show_blog_movies", context.getString(R.string.show_movies));
        }
        return var;
    }
    public String ShowMyMovies() {
        String var = "";
        if (appPreference != null) {
            var = appPreference.getString("show_mymovie", context.getString(R.string.show_movies));
        }
        return var;
    }

    public String QurekaUrl1() {
        String var = "";
        if (appPreference != null) {
            var = appPreference.getString("qureka_url1", context.getString(R.string.Qureka_Url));
        }
        return var;
    }
    public String QurekaUrl2() {
        String var = "";
        if (appPreference != null) {
            var = appPreference.getString("qureka_url2", context.getString(R.string.Qureka_Url));
        }
        return var;
    }
    public String QurekaUrl3() {
        String var = "";
        if (appPreference != null) {
            var = appPreference.getString("qureka_url3", context.getString(R.string.Qureka_Url));
        }
        return var;
    }

    public boolean showMaxBanner() {
        boolean output = false;
        if (appPreference != null) {
            output = appPreference.getBoolean("show_max_banner", false);
        }
        return output;
    }

    public boolean showMaxInter() {
        boolean output = false;
        if (appPreference != null) {
            output = appPreference.getBoolean("show_max_inter", false);
        }
        return output;
    }

    public String maxBannerId() {
        String var = "";
        if (appPreference != null) {
            var = appPreference.getString("max_banner_id", "na");
        }
        return var;
    }

    public String maxInterId() {
        String var = "";
        if (appPreference != null) {
            var = appPreference.getString("max_inter_id", "");
        }
        return var;
    }

    public boolean showMaxNative() {
        boolean output = false;
        if (appPreference != null) {
            output = appPreference.getBoolean("show_max_native", false);
        }
        return output;
    }

    public String maxNativeId() {
        String var = "";
        if (appPreference != null) {
            var = appPreference.getString("max_native_id", "");
        }
        return var;
    }
    public boolean showMaxSmallNative() {
        boolean output = false;
        if (appPreference != null) {
            output = appPreference.getBoolean("show_max_small_native", false);
        }
        return output;
    }

    public String maxSmallNativeId() {
        String var = "";
        if (appPreference != null) {
            var = appPreference.getString("max_small_native_id", "");
        }
        return var;
    }

}
