package com.salabon.lazycards;
/* File contains the keys that constitute the body of the payload going to the lazycards server
* in creating a new card
* 
* IF YOU EDIT THIS, THEN ANDROID_JSON_KEYS.PY IN LAZYCARDS SERVER MUST BE EDITTED AS WELL
*/

final class Json_Keys {
    static final String WORD = "word";
    static final String BACK_CARD = "back";
    static final String DECK = "deck";
    static final String TAGS = "tags";
 //   final static String ANKI_ACTION = "anki_action";
    static final String OPTIONS = "options";

    static final String API = "api";

    // NOTE: If you edit this, also edit the order of APIs:string.xml to reflect the order here
    final class APIs{
        static final int WORDS = 0;
        static final int NONE = 1;
    }
}
