package com.salabon.lazycards.CustomExceptions;

/**
 * Thrown if the word does not exist
 */
public class WordException extends Exception {
    private static final String ERROR = "That word was not found in the currently selected API";

    public WordException(){
        super(ERROR);
    }

    public WordException(String message){
        super(message);
    }

    public WordException(String message, Throwable cause){
        super(message, cause);
    }
}
