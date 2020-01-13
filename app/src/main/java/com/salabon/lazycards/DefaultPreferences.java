package com.salabon.lazycards;

import android.content.Context;

import androidx.preference.PreferenceManager;

import java.util.Set;


public final class DefaultPreferences {
    private static final String PREF_IP = "ip";
    private static final String PREF_PORT = "port";
    private static final String PREF_DECKS = "decks";

    public static void setIp(Context context, String ip){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_IP, ip)
                .apply();
    }

    public static String getIp(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_IP, null);
    }

    public static void setPort(Context context, String port){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_PORT, port)
                .apply();
    }

    public static String getPort(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_PORT, null);
    }

    public static void setDecks(Context context, Set<String> decks){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putStringSet(PREF_DECKS, decks)
                .apply();
    }

    public static Set<String> getDecks(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getStringSet(PREF_DECKS, null);
    }
}
