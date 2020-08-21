package com.vachel.sudo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by jianglixuan on 2020/8/21.
 * Describe:
 */
public class PreferencesUtils {
    public static boolean setLongPreference(Context context, String key, long value) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit().putLong(key, value).commit();
    }

    public static long getLongPreference(Context context, String key, long defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getLong(key, defaultValue);
    }

    public static boolean setBooleanPreference(Context context, String key, boolean value) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit().putBoolean(key, value).commit();
    }

    public static boolean getBooleanPreference(Context context, String key, boolean defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getBoolean(key, defaultValue);
    }

    public static void setStringPreference(Context context, String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit().putString(key, value).apply();
    }

    public static String getStringPreference(Context context, String key, String defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getString(key, defaultValue);
    }

    public static int getIntegerPreference(Context context, String key, int defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getInt(key, defaultValue);
    }

    public static void setIntegerPrefrence(Context context, String key, int value) {
        PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit().putInt(key, value).apply();
    }

    public static float getFloatPreference(Context context, String key, float defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getFloat(key, defaultValue);
    }

    public static void setFloatPrefrence(Context context, String key, float value) {
        PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit().putFloat(key, value).apply();
    }


    public static boolean setIntegerPrefrenceBlocking(Context context, String key, int value) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit().putInt(key, value).commit();
    }

    public static boolean removePreferenceByKey(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if (preferences.contains(key)) {
            return preferences.edit().remove(key).commit();
        } else {
            return true;
        }
    }
}
