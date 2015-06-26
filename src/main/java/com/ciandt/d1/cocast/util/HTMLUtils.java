package com.ciandt.d1.cocast.util;

import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Utility class for HTML manipulation
 */
public class HTMLUtils {

    /**
     * Remove HTML tags.
     * 
     * @param str
     *            The HTML sting
     * @return
     */
    public static String cleanHTMLTags(String str) {
        if (str == null) {
            return null;
        }
        return Jsoup.parse(str).text();
    }

    /**
     * Remove the links.
     * 
     * @param str
     *            The HTML sting
     * @return
     */
    public static String removeHref(String str) {
        Document doc = Jsoup.parse(str);
        Elements linkList = doc.select("a");
        Iterator<Element> iterator = linkList.iterator();
        Element element = null;
        while (iterator.hasNext()) {
            element = iterator.next();
            str = str.replaceAll(element.outerHtml(), element.text());
        }
        return str;
    }
}
