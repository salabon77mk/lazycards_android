package com.salabon.lazycards.Cards.Services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.salabon.lazycards.Cards.Constants.Anki;
import com.salabon.lazycards.Cards.CustomExceptions.AnkiServerDownException;
import com.salabon.lazycards.Cards.DefaultPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

// Allows us to get various information regarding decks
public class DeckService extends ServerCommService {

    private static final String TAG = "DeckService";
    private static final String EXTRA_ACTION =
            "com.salabon.lazycards.getDeckNames";

    private static final int TIMEOUT = 3000;

    public DeckService() {
        super(TAG);
    }

    public static Intent newIntentGetDecks(Context context){
        Intent intent = new Intent(context, DeckService.class);
        intent.putExtra(EXTRA_ACTION, Anki.Actions.GET_DECK_NAMES);
        return intent;
    }

    @Override
    public void onHandleIntent(@Nullable Intent intent) {
        if(intent == null) return;

        int action = intent.getIntExtra(EXTRA_ACTION, 0);
        switch (action){
            case Anki.Actions.GET_DECK_NAMES:
                getDecks();
                break;
            default:
                break;
        }
    }

    // Queries the LazyCards server and gets back the list of available decks
    // Errors: Anki could be down
    private void getDecks(){
        try {
            // TODO Change below line to accomdate custom port options
            String strUrl = makeUrl(Anki.Endpoints.GET_DECKS);
            //String strUrl = Anki.Endpoints.HTTP + DefaultPreferences.getIp(this) + Anki.Endpoints.GET_DECKS;
            URL url = new URL(strUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(TIMEOUT);
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            handleServerError(responseCode);

            JSONObject obj = getResponseAndCloseConnection(con);

            JSONArray jsonDecks = obj.getJSONArray(Anki.JsonResults.RESULT);
            Set<String> decks = new HashSet<>();

            for(int i = 0; i < jsonDecks.length(); i++){
                String deck = jsonDecks.getString(i);
                decks.add(deck);
            }

            DefaultPreferences.setDecks(this, decks);
            finished(Anki.ActionResult.SUCCESS);
        }
        catch (ConnectException e){
            finished(Anki.ActionResult.APACHE_SERVER_DOWN);
        }
        catch (AnkiServerDownException e){
            finished(Anki.ActionResult.ANKI_SERVER_DOWN);
        }
        catch (IOException e) {
            finished(Anki.ActionResult.APACHE_UNREACHABLE);
        }
        catch (JSONException e) {
            Log.e(TAG, "JSON Exception");
            e.printStackTrace();
        }
    }
}
