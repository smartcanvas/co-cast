package io.cocast.util;

import java.util.Date;

/**
 * Utility class for dates
 */
public class DateUtils {

    /**
     * Return the current date
     */
    public static Date now() {
        return new Date();
    }

    /**
     * Return the date the system considers eternity
     */
    public static Date eternity() {
        return new Date(System.currentTimeMillis() + (10 * 365 * 24 * 60 * 60 * 1000));
    }
}
