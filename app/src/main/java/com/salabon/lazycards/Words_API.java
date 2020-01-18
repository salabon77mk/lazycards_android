package com.salabon.lazycards;

import java.util.ArrayList;

/* Contains the options for com.salabon.lazycards.Words_API
*  Example: If option Synonyms and Antonyms are used, then we
*  want to include Synonyms, and Antonyms in our card
*
*
*  IF EDITING THE VALUES IN THIS FILE, EDIT words_api.py IN LAZYCARDS
*  SERVER AS WELL
*/
final class Words_API {
    private static final String EVERYTHING = "EVERYTHING";
    private static final String DEFINITION = "definition";
    private static final String SYNONYMS = "synonyms";
    private static final String ANTONYMS = "antonyms";
    private static final String EXAMPLES = "examples";

    private static final ArrayList<String> OPTIONS = new ArrayList<>();

    // First option is the default option in our option menu
    static {
        OPTIONS.add(DEFINITION);
        OPTIONS.add(EVERYTHING);
        OPTIONS.add(SYNONYMS);
        OPTIONS.add(ANTONYMS);
        OPTIONS.add(EXAMPLES);
    }

    static final String DEFAULT = DEFINITION;

    static ArrayList<String> getOptions(){
        return OPTIONS;
    }

}
