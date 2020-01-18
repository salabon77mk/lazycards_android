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

// Responsible for creating, deleting, updating, fetching cards
class UploadCardService extends IntentService {

    private static final String TAG = "uploadcardservice";
    private static final String EXTRA_WORD =
            "com.salabon.lazycards.word";
    private static final String EXTRA_DECK =
            "com.salabon.lazycards.deck";
    private static final String EXTRA_ACTION =
            "com.salabon.lazycards.action";
    private static final String EXTRA_TAGS =
            "com.salabon.lazycards.tags";
    private static final String EXTRA_OPTIONS =
            "com.salabon.lazycards.options";
    private static final String EXTRA_API =
            "com.salabon.lazycards.api";

    private static final int CREATE = 0; // create new card

    /*
    * @param word : Word to retrieve definition of
    * @param deck : Deck to add card to
    * @param api : The api that will be used
    * @param tags : Tags for the word
    * @param options : What parts of speech to include for word, eg antonyms, synonyms
    * at the moment these options are part of the words api, this will need to change
    * if we include more APIs
     */
    static Intent newIntentCreateCard(Context context, String word, String deck,
                             int api, String[] tags, String[] options){
        Intent intent = new Intent(context, UploadCardService.class);
        intent.putExtra(EXTRA_WORD, word);
        intent.putExtra(EXTRA_DECK, deck);
        intent.putExtra(EXTRA_ACTION, CREATE);
        intent.putExtra(EXTRA_TAGS, tags);
        intent.putExtra(EXTRA_OPTIONS, options);
        intent.putExtra(EXTRA_API, api);

        return intent;
    }

    public UploadCardService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent == null) return;

        int action = intent.getIntExtra(EXTRA_ACTION, -1);

        JSONObject payload = null;

        switch (action){
            case CREATE:
                payload = createJsonNewCard(intent);
                break;
            default:
                // do nothing
        }

        if(payload != null){
            switch (action){
                case CREATE:
                    break;
                default:
            }
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

    private JSONObject createJsonNewCard(Intent intent){
        String word = intent.getStringExtra(EXTRA_WORD);
        String deck = intent.getStringExtra(EXTRA_DECK);
        String[] tags = intent.getStringArrayExtra(EXTRA_TAGS);
        String[] options = intent.getStringArrayExtra(EXTRA_OPTIONS);
        int api = intent.getIntExtra(EXTRA_API, Json_Keys.APIs.WORDS);

        JSONObject payload = new JSONObject();
        try {
            payload.put(Json_Keys.WORD, word);
            payload.put(Json_Keys.DECK, deck);
            payload.put(Json_Keys.API, api);

            JSONArray jsonTags = new JSONArray();
            for(int i = 0; i < tags.length; i++){
                jsonTags.put(tags[i]);
            }
            payload.put(Json_Keys.TAGS, jsonTags);

            JSONArray jsonOptions = new JSONArray();
            for(String op : options){
                jsonOptions.put(op);
            }
            payload.put(Json_Keys.OPTIONS, jsonOptions);

        } catch (JSONException e) {
            return null;
        }
        return payload;
    }

}
