package com.salabon.lazycards.Database;

public class CardsSchema {
    public static final class QueuedCardsTable{
        public static final String NAME = "queued_cards";

        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String VOCAB_WORD = "vocab_word";
            public static final String BACK_OF_CARD = "back_of_card";
            public static final String DECK = "deck";
            public static final String TAGS = "tags";
            public static final String OPTIONS = "options";
            public static final String API = "api";
        }
    }
}
