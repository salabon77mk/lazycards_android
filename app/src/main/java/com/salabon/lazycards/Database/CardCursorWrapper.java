package com.salabon.lazycards.Database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.salabon.lazycards.Cards.Card;
import com.salabon.lazycards.Database.CardsSchema.QueuedCardsTable;

import java.util.UUID;

public class CardCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public CardCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Card getCard(){
        String uuidString = getString(getColumnIndex(QueuedCardsTable.Cols.UUID));
        String vocabWord = getString(getColumnIndex(QueuedCardsTable.Cols.VOCAB_WORD));
        String backCard = getString(getColumnIndex(QueuedCardsTable.Cols.BACK_OF_CARD));
        String deck = getString(getColumnIndex(QueuedCardsTable.Cols.DECK));
        String tags = getString(getColumnIndex(QueuedCardsTable.Cols.TAGS));
        String options = getString(getColumnIndex(QueuedCardsTable.Cols.OPTIONS));
        int api = getInt(getColumnIndex(QueuedCardsTable.Cols.API));

        Card card = new Card(UUID.fromString(uuidString));
        card.setVocabWord(vocabWord);
        card.setBackofCard(backCard);
        card.setDeck(deck);
        card.setTags(tags);
        card.setSelectedOptions(options);
        card.setApi(api);

        return card;
    }
}
