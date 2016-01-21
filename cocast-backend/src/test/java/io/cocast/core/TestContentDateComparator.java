package io.cocast.core;

import io.cocast.test.BaseTest;
import io.cocast.util.DateUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Tests date comparator
 */
public class TestContentDateComparator extends BaseTest {

    @Test
    public void shouldOrderContentListByDate() throws Exception {
        List<Content> contentList = new ArrayList<Content>();

        Content c1 = new Content();
        c1.setId("c1");
        c1.setDate(null);

        Content c2 = new Content();
        c2.setId("c2");
        c2.setDate(DateUtils.now());

        contentList.add(c2);
        contentList.add(c1);

        contentList.sort(new ContentComparator());
        assertEquals("c1", contentList.get(0).getId());

        c2.setDate(null);
        c1.setDate(DateUtils.now());
        contentList.sort(new ContentComparator());
        assertEquals("c2", contentList.get(0).getId());

        c1.setDate(DateUtils.now());
        TimeUnit.SECONDS.sleep(1);
        c2.setDate(DateUtils.now());
        contentList.sort(new ContentComparator());
        assertEquals("c2", contentList.get(0).getId());
    }

    @Test
    public void shouldCompareToContents() throws Exception {
        Content c1 = new Content();
        c1.setId("c1");
        c1.setDate(null);

        Content c2 = new Content();
        c2.setId("c2");
        c2.setDate(DateUtils.now());

        ContentComparator contentComparator = new ContentComparator(ContentComparator.DATE);

        assertEquals(-1, contentComparator.compare(c1, c2));

        c1.setDate(new Date(System.currentTimeMillis()));
        c2.setDate(new Date(System.currentTimeMillis() + 10));

        assertEquals(-1, contentComparator.compare(c2, c1));
    }
}
