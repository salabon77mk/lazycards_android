package com.salabon.lazycards.Cards.Constants;
/* File contains the keys that constitute the body of the payload going to the lazycards server
* in creating a new card
* 
* IF YOU EDIT THIS, THEN ANDROID_JSON_KEYS.PY IN LAZYCARDS SERVER MUST BE EDITTED AS WELL
*/

public final class Json_Keys {
    public static final String WORD = "word";
    public static final String BACK_CARD = "back";
    public static final String DECK = "deck";
    public static final String TAGS = "tags";
 //   final static String ANKI_ACTION = "anki_action";
    public static final String OPTIONS = "options";

    public static final String API = "api";

    // NOTE: If you edit this, also edit the order of APIs:string.xml to reflect the order here
    public final class APIs{
        public static final int WORDS = 0;
        public static final int NONE = 1;
    }
}
