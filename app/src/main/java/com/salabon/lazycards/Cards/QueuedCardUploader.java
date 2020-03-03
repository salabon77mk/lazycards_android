package com.salabon.lazycards.Cards;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.salabon.lazycards.Cards.Services.CardService;
import com.salabon.lazycards.Database.CardDbManager;

import org.json.JSONObject;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class QueuedCardUploader extends HandlerThread {
    private static final String TAG = "QueuedCardUploader";

    private static final int UPLOAD = 0;
    private static final int KILL_ME = 1;

    private boolean mHasQuit = false;
    private Handler mRequestHandler;

    private Queue<Card> mCardQueue;

    private Context mContext;

    private QueuedCardUploaderListener mListener;



    @FunctionalInterface
    public interface QueuedCardUploaderListener{
        void onUploaded(int pos);
    }

    public void setQueuedCardUploaderListener(QueuedCardUploaderListener uploader){
        mListener = uploader;
    }

    public QueuedCardUploader(Context context){
        super(TAG);
        mContext = context;

        CardDbManager db = CardDbManager.getInstance(context);

        // Could be refactored so that db returns a queue right away
        List<Card> cards = db.getCards();
        mCardQueue = new LinkedList<>(cards);
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared(){
        mRequestHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what == UPLOAD){
                    if(!mCardQueue.isEmpty()) {
                        handleRequest(mCardQueue.remove());
                    }
                }
            }
        };
    }

    @Override
    public boolean quit(){
        mHasQuit = true;
        mCardQueue.clear();
        return super.quit();
    }

    private void handleRequest(final Card card){
        // convert card to json
        JSONObject payload = card.toJson();
        if(payload != null){
            Intent i = CardService.newIntentCreate(mContext, payload);
            mContext.startService(i);
        }

    }

    void submitCard(){
        mRequestHandler.obtainMessage(UPLOAD).sendToTarget();
    }

}
