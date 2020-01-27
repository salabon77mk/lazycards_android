package com.salabon.lazycards;

import android.app.IntentService;
import android.content.Intent;

public abstract class ServerCommService extends IntentService {
    static final String ACTION_FINISHED =
            "com.salabon.lazycards.FINISHED";
    static final String PERM_PRIVATE =
            "com.salabon.lazycards.PRIVATE";
    static final String ACTION_STATUS =
            "com.salabon.lazycards.STATUS";
    private static final String TAG = "ServerCommService";


    public ServerCommService() {
        super(TAG);
    }

    void finished(int msg){
        Intent i = new Intent(ACTION_FINISHED);
        i.putExtra(ACTION_STATUS, msg);
        sendBroadcast(i, PERM_PRIVATE);
    }

    void handleErrorIfExists(int errorCode){
        if(errorCode == HTTPStatusCode.INTERNAL_SERVER_ERROR){
            finished(Anki.ActionResult.OTHER_ERROR);
        }
        if(errorCode ==  HTTPStatusCode.SERVICE_UNAVAILABLE){
            finished(Anki.ActionResult.ANKI_SERVER_DOWN);
        }
    }
}
