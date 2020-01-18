package com.salabon.lazycards;
/* File contains the keys that constitute the body of the payload going to the lazycards server
* in creating a new card
* 
* IF YOU EDIT THIS, THEN ANDROID_JSON_KEYS.PY IN LAZYCARDS SERVER MUST BE EDITTED AS WELL
*/

final class Json_Keys {
    final static String WORD = "word";
    final static String DECK = "deck";
    final static String TAGS = "tags";
 //   final static String ANKI_ACTION = "anki_action";
    final static String OPTIONS = "options";

    final static String API = "api";
    final class APIs{
        static final int WORDS = 0;
    }
}
