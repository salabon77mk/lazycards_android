package com.salabon.lazycards;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;

<<<<<<< Updated upstream:app/src/main/java/com/salabon/lazycards/UploadCardService.java
import org.json.JSONArray;
=======
import com.salabon.lazycards.CustomExceptions.AnkiServerDownException;
import com.salabon.lazycards.CustomExceptions.WordException;

>>>>>>> Stashed changes:app/src/main/java/com/salabon/lazycards/CardService.java
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static com.salabon.lazycards.DeckService.ACTION_FINISHED;
import static com.salabon.lazycards.DeckService.ACTION_STATUS;
import static com.salabon.lazycards.DeckService.PERM_PRIVATE;

// Responsible for creating, deleting, updating, fetching cards
public class UploadCardService extends IntentService {

    private static final String TAG = "uploadcardservice";
    // We will be sending the JSON body inside of intents
    private final static String EXTRA_JSON_BODY =
            "com.salabon.lazycards.JSON_BODY;";
    private static final String EXTRA_ACTION =
            "com.salabon.lazycards.action";

    private static final int CREATE = 0; // create new card

<<<<<<< Updated upstream:app/src/main/java/com/salabon/lazycards/UploadCardService.java
    /**
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
=======
    static Intent newIntentCreate(Context context, JSONObject json){
        Intent intent = new Intent(context, CardService.class);
        String jsonStr = json.toString();
        intent.putExtra(EXTRA_JSON_BODY, jsonStr);
>>>>>>> Stashed changes:app/src/main/java/com/salabon/lazycards/CardService.java
        intent.putExtra(EXTRA_ACTION, CREATE);
        return intent;
    }

    public UploadCardService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent == null) return;

        int action = intent.getIntExtra(EXTRA_ACTION, -1);

        JSONObject payload;
        try {
            payload = new JSONObject(intent.getStringExtra(EXTRA_JSON_BODY));
        }catch (JSONException e){
            payload = null;
        }

        if(payload != null){
            switch (action){
                case CREATE:
                    sendNewCard(payload); break;
                default:
            }
        }
    }

<<<<<<< Updated upstream:app/src/main/java/com/salabon/lazycards/UploadCardService.java

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

            addArrayToPayload(payload, Json_Keys.TAGS, tags);
            addArrayToPayload(payload, Json_Keys.OPTIONS, options);

        } catch (JSONException e) {
            return null;
        }
        return payload;
    }

=======
>>>>>>> Stashed changes:app/src/main/java/com/salabon/lazycards/CardService.java
    private void sendNewCard(JSONObject payload){
        String strUrl = Anki.Endpoints.HTTP + DefaultPreferences.getIp(this)
                + Anki.Endpoints.ADD_NOTE;
        try {
            URL url = new URL(strUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());

            wr.write(payload.toString().getBytes());
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();

            //TODO replace this if block with ServerCommService.handleError
            if(responseCode == HTTPStatusCode.INTERNAL_SERVER_ERROR){
                finished(Anki.ActionResult.OTHER_ERROR);
                return;
            }
            else if(responseCode ==  HTTPStatusCode.SERVICE_UNAVAILABLE){
                finished(Anki.ActionResult.ANKI_SERVER_DOWN);
                return;
            }

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // TODO handle the response, put it in database of uploaded cards
        }
        catch (ConnectException e){
            finished(Anki.ActionResult.APACHE_SERVER_DOWN);
        }
        catch (IOException e) {
            finished(Anki.ActionResult.APACHE_UNREACHABLE);
        }
    }
<<<<<<< Updated upstream:app/src/main/java/com/salabon/lazycards/UploadCardService.java

    // TODO Remove this bit of code duplication, also found in DeckService
    // Create an abstract class that extends IntentService?
    private void finished(int msg){
        Intent i = new Intent(ACTION_FINISHED);
        i.putExtra(ACTION_STATUS, msg);
        sendBroadcast(i, PERM_PRIVATE);
    }

    private void addArrayToPayload(JSONObject payload, String jsonKey, String[] args) throws JSONException {
        if(args == null) return;

        JSONArray arr = new JSONArray();
        for(String s : args){
            arr.put(s);
        }
        payload.put(jsonKey, arr);
    }

=======
>>>>>>> Stashed changes:app/src/main/java/com/salabon/lazycards/CardService.java
}
