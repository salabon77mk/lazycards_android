package com.salabon.lazycards;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class UploadCardService extends IntentService {
    private static final String EXTRA_WORD =
            "com.salabon.lazycards.word";
    private static final String EXTRA_DECK =
            "com.salabon.lazycards.deck";
    private static final String EXTRA_ANKI_ACTION =
            "com.salabon.lazycards.anki_action";
    private static final String EXTRA_TAGS =
            "com.salabon.lazycards.tags";

    static Intent newIntentWithCard(Context context, String word, String deck,
                            int ankiAction, String[] tags){
        Intent intent = new Intent(context, UploadCardService.class);
        intent.putExtra(EXTRA_WORD, word);
        intent.putExtra(EXTRA_DECK, deck);
        intent.putExtra(EXTRA_ANKI_ACTION, ankiAction);
        intent.putExtra(EXTRA_TAGS, tags);

        return intent;
    }

    public UploadCardService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        JSONObject payload = createJsonFromExtras(intent);

        if(payload != null){
            //network stuff
            // TODO upload to target server
        }
    }


    // TODO test this one CASES: No mobile data, No Wifi, and vice versa
    private boolean isWifiConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm != null) {
            if (Build.VERSION.SDK_INT < 23) {
                final NetworkInfo ni = cm.getActiveNetworkInfo();
                if (ni != null) {
                    return (ni.isConnected() && ni.getType() == ConnectivityManager.TYPE_WIFI);
                }
            } else {
                final Network network = cm.getActiveNetwork();
                if (network != null) {
                    final NetworkCapabilities nc = cm.getNetworkCapabilities(network);
                    return (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
                }
            }
        }
        return false;
    }

    private JSONObject createJsonFromExtras(Intent intent){
        String word = intent.getStringExtra(EXTRA_WORD);
        String deck = intent.getStringExtra(EXTRA_DECK);
        String action = intent.getStringExtra(EXTRA_ANKI_ACTION);
        String[] tags = intent.getStringArrayExtra(EXTRA_TAGS);

        JSONObject payload = new JSONObject();
        try {
            payload.put(Json_Keys.WORD, word);
            payload.put(Json_Keys.DECK, deck);
            payload.put(Json_Keys.ANKI_ACTION, action);

            JSONArray jsonTags = new JSONArray();
            for(int i = 0; i < tags.length; i++){
                jsonTags.put(tags[i]);
            }
            payload.put(Json_Keys.TAGS, jsonTags);
        } catch (JSONException e) {
            return null;
        }
        return payload;
    }
}
