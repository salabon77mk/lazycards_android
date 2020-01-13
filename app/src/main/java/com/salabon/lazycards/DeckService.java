package com.salabon.lazycards;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

// Allows us to get various information regarding decks
public class DeckService extends IntentService {
    static final String ACTION_FINISHED =
            "com.salabon.lazycards.FINISHED";
    static final String PERM_PRIVATE =
            "com.salabon.lazycards.PRIVATE";

    private static final String EXTRA_ACTION =
            "com.salabon.lazycards.getDeckNames";

    private static final int TIMEOUT = 3000;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     *
     */
    public DeckService() {
        super("DeckService");
    }


    static Intent newIntentGetDecks(Context context){
        Intent intent = new Intent(context, DeckService.class);
        intent.putExtra(EXTRA_ACTION, Anki.Actions.GET_DECK_NAMES);
        return intent;
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int action = intent.getIntExtra(EXTRA_ACTION, 0);
        switch (action){
            case Anki.Actions.GET_DECK_NAMES:
                getDecks();
                break;
            default:
                break;
        }
    }

    private void getDecks(){
        try {
            String strUrl = Anki.Endpoints.HTTP + DefaultPreferences.getIp(this) + Anki.Endpoints.GET_DECKS;
            URL url = new URL(strUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(TIMEOUT);
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();

            // TODO handle the response codes
            if(responseCode == HTTPStatusCode.INTERNAL_SERVER_ERROR){

            }
            if(responseCode ==  HTTPStatusCode.SERVICE_UNAVAILABLE){

            }

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            con.disconnect();

            JSONObject obj = new JSONObject(response.toString());

            JSONArray jsonDecks = obj.getJSONArray(Anki.JsonResults.RESULT);
            Set<String> decks = new HashSet<>();

            for(int i = 0; i < jsonDecks.length(); i++){
                String deck = jsonDecks.getString(i);
                decks.add(deck);
            }

            DefaultPreferences.setDecks(this, decks);
            finished();
        }
        // TODO Flesh out all the excveptions
        catch(Exception ex){
            System.out.println("woops");
            ex.printStackTrace();
        }
    }

    // Currently a function as I think this might be used often
    private void finished(){
        Intent i = new Intent(ACTION_FINISHED);
        sendBroadcast(i, PERM_PRIVATE);
    }
}
