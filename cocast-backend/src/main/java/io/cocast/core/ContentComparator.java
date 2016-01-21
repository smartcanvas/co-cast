package io.cocast.core;

import java.util.Comparator;

/**
 * Compares a content by somre criteria
 */
public class ContentComparator implements Comparator<Content> {

    public static final Integer DATE = 1;
    public static final Integer POPULARITY = 2;

    private Integer criterium;

    /**
     * Constructor
     */
    public ContentComparator() {
        this(ContentComparator.DATE);
    }

    /**
     * Constructor
     */
    public ContentComparator(Integer criterium) {
        this.criterium = criterium;
    }

    @Override
    public int compare(Content o1, Content o2) {

        if (ContentComparator.DATE.equals(this.criterium)) {
            if (o1.getDate() == null) {
                return -1;
            } else if (o2.getDate() == null) {
                return 1;
            } else {
                return o2.getDate().compareTo(o1.getDate());
            }
        } else if (ContentComparator.POPULARITY.equals(this.criterium)) {
            if (o1.getLikeCounter() == null) {
                return -1;
            } else if (o2.getLikeCounter() == null) {
                return 1;
            } else {
                return o2.getLikeCounter().compareTo(o1.getLikeCounter());
            }
        } else {
            return -1;
        }
    }
}
