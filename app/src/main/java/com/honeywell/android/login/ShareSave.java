package com.honeywell.android.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by musk on 2017/5/25.
 */

public class ShareSave {
    private static final String SHARED_NAME = "share";

    private static SharedPreferences getShared(Context context) {
        return context.getSharedPreferences(SHARED_NAME, Context.MODE_MULTI_PROCESS);
    }

    public static void clear(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        sp.edit().clear().commit();
    }

    public static int getInt(Context context, String key) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sp.getInt(key, 0);
    }

    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sp.getInt(key, defaultValue);
    }

    public static void setInt(Context context, String key, int value) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        sp.edit().putInt(key, value).commit();
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sp.getBoolean(key, false);
    }

    public static boolean getBoolean(Context context, String key,
                                     boolean defaultValue) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sp.getBoolean(key, defaultValue);
    }

    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        sp.edit().putBoolean(key, value).commit();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sp.getString(key, "");
    }

    public static String getString(Context context, String key,
                                   String defaultValue) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sp.getString(key, defaultValue);
    }

    public static void setString(Context context, String key, String value) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        sp.edit().putString(key, value).commit();
    }

    public static float getFloat(Context context, String key) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sp.getFloat(key, 0f);
    }

    public static float getFloat(Context context, String key, float defaultValue) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sp.getFloat(key, defaultValue);
    }

    public static void setFloat(Context context, String key, float value) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        sp.edit().putFloat(key, value).commit();
    }

    public static long getLong(Context context, String key) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sp.getLong(key, 0L);
    }

    public static long getLong(Context context, String key, long defaultValue) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sp.getLong(key, defaultValue);
    }

    public static void setLong(Context context, String key, long value) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        sp.edit().putLong(key, value).commit();
    }
}
