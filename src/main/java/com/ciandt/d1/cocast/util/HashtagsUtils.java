package com.ciandt.d1.cocast.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Utility class for hashtag manipulation
 */
public final class HashtagsUtils {

    /**
     * Search hash tags inside any text.
     *
     * @param providerText
     *            Text with hashtags
     * @return All the hashtags w/o the '#' in front of it
     */
    public static List<String> parseHashtags(String providerText) {
        List<String> hashtagsList = new ArrayList<String>();

        providerText = StringUtils.replace(providerText, Character.toString((char) 160), Character.toString((char) 32));
        if (StringUtils.isEmpty(providerText)) {
            return hashtagsList;
        }

        StringTokenizer strToken = new StringTokenizer(providerText, " ");
        while (strToken.hasMoreTokens()) {
            String hashtag = strToken.nextToken().trim();
            if ((hashtag.length() > 1) && (hashtag.startsWith("#"))) {
                hashtagsList.add(hashtag.substring(1));
            }
        }

        return hashtagsList;
    }

    /**
     * Returns the text w/o any hashtag
     *
     * @param providerText
     *            Text with hashtags
     * @return The same text w/o any hashtags
     */
    public static String removeHashtags(String providerText) {
        // remove non-breaking spaces
        providerText = StringUtils.replace(providerText, Character.toString((char) 160), Character.toString((char) 32));
        if (StringUtils.isEmpty(providerText)) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        StringTokenizer strToken = new StringTokenizer(providerText, " ");
        String word = null;
        boolean hasBegun = false;
        while (strToken.hasMoreTokens()) {
            word = strToken.nextToken();
            if (word.startsWith("#")) {
                // ignore
            } else {
                if (hasBegun) {
                    sb.append(" ");
                    sb.append(word);
                } else {
                    sb.append(word);
                    hasBegun = true;
                }
            }
        }

        return sb.toString();
    }

    /**
     * Remove hrefs from hashtags. This behavior is present in some social
     * networks such as Google Plus, for example.
     *
     * @param str
     *            Hashtag with href
     * @return Hashtag w/o href
     */
    public static String removeHashtagsHref(String str) {
        Document doc = Jsoup.parse(str);
        Elements linkList = doc.select("a");
        Iterator<Element> iterator = linkList.iterator();
        while (iterator.hasNext()) {
            Element element = iterator.next();
            if (element.outerHtml().contains("hashtag")) {
                str = str.replaceAll(element.outerHtml(), " " + element.text() + " ");
            }
        }

        return str;
    }
}

