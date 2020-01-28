package com.salabon.lazycards;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.salabon.lazycards.CustomExceptions.AnkiServerDownException;
import com.salabon.lazycards.CustomExceptions.WordException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;


// Responsible for creating, deleting, updating, fetching cards
public class CardService extends ServerCommService {

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
        Intent intent = new Intent(context, CardService.class);
        intent.putExtra(EXTRA_WORD, word);
        intent.putExtra(EXTRA_DECK, deck);
        intent.putExtra(EXTRA_ACTION, CREATE);
        intent.putExtra(EXTRA_TAGS, tags);
        intent.putExtra(EXTRA_OPTIONS, options);
        intent.putExtra(EXTRA_API, api);

        return intent;
    }

    public CardService() {
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
                    sendNewCard(payload);
                    break;
                default:
            }
        }
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

    private void sendNewCard(JSONObject payload){
        String strUrl = makeUrl(Anki.Endpoints.ADD_NOTE);
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

            handleApiResponseError(responseCode);

            JSONObject response = getResponseAndCloseConnection(con);
            checkAnkiConnectError(response);
            // TODO handle the response, put it in database of uploaded cards
        }
        catch (ConnectException e){
            finished(Anki.ActionResult.APACHE_SERVER_DOWN);
        }
        catch (WordException e){
            finishedWithErrorMessage(Anki.ActionResult.API_ERROR, e.getMessage());
        }
        catch (AnkiServerDownException e){
            finished(Anki.ActionResult.ANKI_SERVER_DOWN);
        }
        catch (IOException e) {
            finished(Anki.ActionResult.APACHE_UNREACHABLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addArrayToPayload(JSONObject payload, String jsonKey, String[] args) throws JSONException {
        if(args == null) return;

        JSONArray arr = new JSONArray();
        for(String s : args){
            arr.put(s);
        }
        payload.put(jsonKey, arr);
    }

}
