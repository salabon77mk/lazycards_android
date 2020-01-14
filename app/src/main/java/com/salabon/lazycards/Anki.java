package com.salabon.lazycards;

final class Anki {
    final class Actions{
        static final int ADD_NOTE = 0;
        static final int GET_DECK_NAMES = 1;
    }

    final class Endpoints{
        // The WSGI alias can be adjusted within the vhosts file within apache/httpd
        // Look for the WSGIScriptAlias tag, the first entry is the alias
        static final String wsgiAlias = "/lazycards";
        static final String HTTP = "http://";

        static final String GET_DECKS = wsgiAlias + "/get_decks";
    }

    // This class represents the json response body
    final class JsonResults{
        static final String RESULT = "result";
    }

    final class ActionResult{
        static final int SUCCESS = 1;
        static final int OTHER_ERROR = 0;
        static final int APACHE_SERVER_DOWN = -1;
        static final int ANKI_SERVER_DOWN = -2;
    }
}
