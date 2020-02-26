package com.salabon.lazycards.Cards.Services;

import android.app.IntentService;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import com.salabon.lazycards.Cards.Constants.Anki;
import com.salabon.lazycards.Cards.CustomExceptions.AnkiServerDownException;
import com.salabon.lazycards.Cards.CustomExceptions.WordException;
import com.salabon.lazycards.Cards.DefaultPreferences;
import com.salabon.lazycards.Cards.Constants.HTTPStatusCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public abstract class ServerCommService extends IntentService {
    public static final String ACTION_FINISHED =
            "com.salabon.lazycards.FINISHED";
    public static final String PERM_PRIVATE =
            "com.salabon.lazycards.PRIVATE";
    public static final String ACTION_STATUS =
            "com.salabon.lazycards.STATUS";
    public static final String ERROR_BODY =
            "com.salabon.lazycards.BODY";
    private static final String TAG = "ServerCommService";

    public ServerCommService(String tag) {
        super(tag);
    }

    void finished(int msg){
        Intent i = new Intent(ACTION_FINISHED);
        i.putExtra(ACTION_STATUS, msg);
        sendBroadcast(i, PERM_PRIVATE);
    }

    void finishedWithErrorMessage(int msg, String error){
        Intent i = new Intent(ACTION_FINISHED);
        i.putExtra(ACTION_STATUS, Anki.ActionResult.ANKI_CONNECT_ERROR);
        i.putExtra(ERROR_BODY, error);
        sendBroadcast(i, PERM_PRIVATE);
    }

    void handleApiResponseError(int errorCode) throws AnkiServerDownException, WordException {
        // should retrieve the server's stack trace?
        if(errorCode == HTTPStatusCode.INTERNAL_SERVER_ERROR){
            finished(Anki.ActionResult.OTHER_ERROR);
        }
        else if(errorCode == HTTPStatusCode.SERVICE_UNAVAILABLE){
            throw new AnkiServerDownException();
        }
        else if(errorCode == HTTPStatusCode.PAGE_NOT_FOUND){
            throw new WordException();
        }
    }

    // TODO move these Error handlers into an interface?
    void handleServerError(int errorCode) throws AnkiServerDownException {
        if(errorCode == HTTPStatusCode.INTERNAL_SERVER_ERROR){
            finished(Anki.ActionResult.OTHER_ERROR);
        }
        else if(errorCode == HTTPStatusCode.SERVICE_UNAVAILABLE){
            throw new AnkiServerDownException();
        }
    }

    JSONObject getResponseAndCloseConnection(HttpURLConnection connection)
            throws IOException, JSONException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));

        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();
        connection.disconnect();

        return new JSONObject(response.toString());
    }

    String makeUrl(String endPoint){
        return Anki.Endpoints.HTTP +
                DefaultPreferences.getIp(this) +
                ":" + DefaultPreferences.getPort(this) +
                endPoint;
    }

    void checkAnkiConnectError(JSONObject response) throws JSONException {
        String res = response.getString(Anki.JsonResults.ERROR);
        if(!res.equals(Anki.JsonResults.SUCCESS)){
            finishedWithErrorMessage(Anki.ActionResult.ANKI_CONNECT_ERROR, res);
        }
        else{
            finished(Anki.ActionResult.SUCCESS);
        }
    }

    boolean isWifiConnected(){
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
}
