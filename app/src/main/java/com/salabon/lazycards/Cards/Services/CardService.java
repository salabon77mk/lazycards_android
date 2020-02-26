package com.salabon.lazycards.Cards.Services;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.salabon.lazycards.Cards.Constants.Anki;
import com.salabon.lazycards.Cards.CustomExceptions.AnkiServerDownException;
import com.salabon.lazycards.Cards.CustomExceptions.WordException;

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

    private static final String EXTRA_ACTION =
            "com.salabon.lazycards.action";
    private final static String EXTRA_JSON_BODY =
            "com.salabon.lazycards.JSON_BODY;";

    private static final int CREATE = 0; // create new card

    public static Intent newIntentCreate(Context context, JSONObject json){
        Intent intent = new Intent(context, CardService.class);
        String jsonStr = json.toString();
        intent.putExtra(EXTRA_JSON_BODY, jsonStr);
        intent.putExtra(EXTRA_ACTION, CREATE);
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
        try {
            payload = new JSONObject(intent.getStringExtra(EXTRA_JSON_BODY));
        }catch (JSONException e){
            payload = null;
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
}
