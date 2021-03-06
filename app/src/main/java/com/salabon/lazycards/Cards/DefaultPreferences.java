package com.salabon.lazycards.Cards;

import android.content.Context;

import androidx.preference.PreferenceManager;

import java.util.HashSet;
import java.util.Set;


public final class DefaultPreferences {
    private static final String PREF_IP = "ip";
    private static final String PREF_PORT = "port";
    private static final String PREF_DECKS = "decks";
    private static final String PREF_CURR_DECK = "curr_deck";

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
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_PORT, "80");
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

    public static void setCurrentDeck(Context context, String deck){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_CURR_DECK, deck)
                .apply();
    }

    // The "Default" is the one deck that EVERY Anki user comes with
    public static String getCurrentDeck(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_CURR_DECK, "Default");
    }
}
