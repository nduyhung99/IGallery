package com.example.igallery.rm.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class RmSave {

    private static final long TIME_END_ADS_CLICK = 180000;
    private static final long TIME_LOAD_NATIVE = 600000;

    private static SharedPreferences getShare(Context c) {
        return c.getSharedPreferences("preferences_rm", Context.MODE_PRIVATE);
    }

    //PREMIUM
    public static void putPay(Context c, boolean pay) {
        getShare(c).edit().putBoolean("pay", pay).apply();
    }

    public static boolean getPay(Context c) {
        return getShare(c).getBoolean("pay", false);
    }

    public static void putFist(Context c, String str) {
        getShare(c).edit().putString("key_fist", str).apply();
    }

    public static boolean getGoogleFist(Context c) {
        String str = getShare(c).getString("key_fist", "");
        if (str.isEmpty())
            return true;
        try {
            Type type = new TypeToken<ItemFist>() {
            }.getType();
            ItemFist itemNative = new Gson().fromJson(str, type);
            if (itemNative != null)
                return itemNative.googleFist(c.getPackageName());
        } catch (Exception e) {
            return true;
        }
        return true;
    }

    public static void putAppClick(Context c, String pkg) {
        ArrayList<String> arr = arrAppClick(c);
        for (String s : arr) {
            if (s.equals(pkg))
                return;
        }
        arr.add(pkg);
        getShare(c).edit().putString("app_click", new Gson().toJson(arr)).apply();
    }

    public static ArrayList<String> arrAppClick(Context c) {
        String str = getShare(c).getString("app_click", "");
        ArrayList<String> arr = null;
        if (str != null && !str.isEmpty()) {
            try {
                Type type = new TypeToken<ArrayList<String>>() {
                }.getType();
                arr = new Gson().fromJson(str, type);
            } catch (Exception ignored) {

            }
        }
        if (arr == null)
            arr = new ArrayList<>();
        return arr;
    }

    //time ads click
    public static void putTimeAdsClick(Context c) {
        getShare(c).edit().putLong("ads_click", System.currentTimeMillis()).apply();
    }

    public static boolean isLoadAds(Context c) {
        return Math.abs(System.currentTimeMillis() - getShare(c).getLong("ads_click", 0)) > TIME_END_ADS_CLICK;
    }
}
