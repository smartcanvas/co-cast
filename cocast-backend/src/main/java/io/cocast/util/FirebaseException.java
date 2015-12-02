package io.cocast.util;

/**
 * Exception thrown when communicating with Firebase
 */
public class FirebaseException extends Exception {

    /**
     * Constructor
     */
    public FirebaseException(String message) {
        super(message);
    }

}
