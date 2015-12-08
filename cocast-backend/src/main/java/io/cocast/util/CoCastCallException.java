package io.cocast.util;

import javax.servlet.http.HttpServletResponse;

/**
 * Exception thrown when executing something inside Co-Cast
 */
public class CoCastCallException extends RuntimeException {

    private int status;

    /**
     * Constructor
     */
    public CoCastCallException(String message, int status) {
        super(message);
        this.status = status;
    }

    /**
     * Constructor
     */
    public CoCastCallException(String message) {
        this(message, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    public int getStatus() {
        return status;
    }
}
