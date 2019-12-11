package com.salabon.lazycards;

import android.content.Context;

import androidx.preference.PreferenceManager;


public class DefaultPreferences {
    private static final String PREF_IP = "ip";

    public static void setIp(Context context, String ip){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_IP, ip)
                .apply();
    }

    public static String getIp(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_IP, null);
    }
}
