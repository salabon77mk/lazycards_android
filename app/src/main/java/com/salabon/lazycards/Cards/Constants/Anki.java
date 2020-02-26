package com.salabon.lazycards.Cards.Constants;

public final class Anki {
    public final class Actions{
        public static final int ADD_NOTE = 0;
        public static final int GET_DECK_NAMES = 1;
    }

    public final class Endpoints{
        // The WSGI alias can be adjusted within the vhosts file within apache/httpd
        // Look for the WSGIScriptAlias tag, the first entry is the alias
        public static final String wsgiAlias = "/lazycards";
        public static final String HTTP = "http://";

        // If these values are edited, then the routes in lazycards.py must be edited as well
        public static final String GET_DECKS = wsgiAlias + "/get_decks";
        public static final String ADD_NOTE = wsgiAlias + "/add_note";
    }

    // This class represents the json response body from AnkiConnect
    public final class JsonResults{
        public static final String RESULT = "result";

        public static final String ERROR = "error";
        public static final String SUCCESS = "null"; // if nothing went wrong, "null" is part of the json response
    }

    public final class ActionResult{
        public static final int SUCCESS = 1;
        public static final int OTHER_ERROR = 0;
        public static final int APACHE_SERVER_DOWN = -1;
        public static final int ANKI_SERVER_DOWN = -2;
        public static final int APACHE_UNREACHABLE = -3;
        public static final int ANKI_CONNECT_ERROR = -4;

        public static final int API_ERROR = -5; // used if an API returned an error
    }
}
