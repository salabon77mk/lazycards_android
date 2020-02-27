package com.salabon.lazycards.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.salabon.lazycards.Cards.Card;
import com.salabon.lazycards.Database.CardsSchema.QueuedCardsTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class CardDbManager {
    private static CardDbManager sCardsDbManager;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private CardDbManager(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new CardsHelper(mContext).getWritableDatabase();
    }

    public static CardDbManager getInstance(Context context){
        if(sCardsDbManager == null){
            sCardsDbManager = new CardDbManager(context);
        }
        return sCardsDbManager;
    }

    public void addCard(Card card){
        ContentValues values = getContentValues(card);
        mDatabase.insert(QueuedCardsTable.NAME, null, values);
    }

    public List<Card> getCards(){
        List<Card> cards = new ArrayList<>();

        //TEMPORARY CODE
        String wordTemp = "WORD_";
        String deckTemp = "DECK_";
        for(int i = 0; i < 20; i++){
            cards.add(newCard(wordTemp+ i, deckTemp + i));
        }

        /*
        // TODO TEMPORARY COMMENT OUT
        CardCursorWrapper cursor = queryQueuedCards(null, null); // gives whole table
        try{
            if(cursor.getCount() == 0){
                return cards;
            }

            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                cards.add(cursor.getCard());
                cursor.moveToNext();
            }
        }
        finally {
            cursor.close();
        }
        */
        return cards;
    }

    // TODO Debug method, delete later
    private Card newCard(String vocab, String deck){
        Card card = new Card();
        card.setApi(0);
        card.setVocabWord(vocab);
        card.setDeck(deck);
        return card;
    }

    // TODO should get Card by vocab word?
    public Card getCard(String vocabWord, String deck){
        String whereClause = QueuedCardsTable.Cols.VOCAB_WORD + " = ? AND " +
                QueuedCardsTable.Cols.DECK + " = ?";
        CardCursorWrapper cursor = queryQueuedCards(
                whereClause,
                new String[] { vocabWord, deck }
        );

        try{
            if (cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCard();
        }
        finally {
            cursor.close();
        }
    }

    public void updateCard(Card card){
        String vocabWord = card.getVocabWord();
        String deck = card.getDeck();
        String whereClause = QueuedCardsTable.Cols.VOCAB_WORD + " = ? AND " +
                QueuedCardsTable.Cols.DECK + " = ?";
        ContentValues values = getContentValues(card);

        mDatabase.update(QueuedCardsTable.NAME, values,
                whereClause,
                new String[] { vocabWord, deck});
    }

    private static ContentValues getContentValues(Card card){
        ContentValues values = new ContentValues();
        values.put(QueuedCardsTable.Cols.VOCAB_WORD, card.getVocabWord());
        values.put(QueuedCardsTable.Cols.BACK_OF_CARD , card.getBackofCard());
        values.put(QueuedCardsTable.Cols.DECK, card.getDeck());
        values.put(QueuedCardsTable.Cols.TAGS, card.getSelectOptionsAsString());
        values.put(QueuedCardsTable.Cols.OPTIONS, card.getSelectOptionsAsString());
        values.put(QueuedCardsTable.Cols.API, card.getApi());

        return values;
    }

    private CardCursorWrapper queryQueuedCards(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                QueuedCardsTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );

        return new CardCursorWrapper(cursor);
    }
}
