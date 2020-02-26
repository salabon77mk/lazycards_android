package com.salabon.lazycards.Cards.CustomExceptions;

public class AnkiServerDownException extends Exception {
    public AnkiServerDownException(){}

    public AnkiServerDownException(String message){
        super(message);
    }

    public AnkiServerDownException(String message, Throwable cause){
        super(message, cause);
    }
}
