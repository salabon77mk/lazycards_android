package com.salabon.lazycards.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.salabon.lazycards.Database.CardsSchema.QueuedCardsTable;

public class CardsHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "lazycards.db";

    public CardsHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        onUpgrade(db, 0, VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 1){
            db.execSQL("create table " + QueuedCardsTable.NAME + "(" +
                    " _id integer primary key autoincrement, " +
                    QueuedCardsTable.Cols.VOCAB_WORD + "TEXT, " +
                    QueuedCardsTable.Cols.BACK_OF_CARD + "TEXT, " +
                    QueuedCardsTable.Cols.DECK + "TEXT, " +
                    QueuedCardsTable.Cols.TAGS + "TEXT, " +
                    QueuedCardsTable.Cols.OPTIONS + "TEXT, " +
                    QueuedCardsTable.Cols.API+ "INTEGER" + ");" );
        }
        if(oldVersion < 2){
            // for future?
        }
    }
}
