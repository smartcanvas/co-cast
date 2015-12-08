package io.cocast.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Extra utility methods for Strings
 */
public class ExtraStringUtils {

    private static Logger logger = LogManager.getLogger(ExtraStringUtils.class.getName());

    /**
     * Generate a mnemonic based on a name
     *
     * @param name Name
     */
    public static String generateMnemonic(String name) {

        if (StringUtils.isEmpty(name)) {
            logger.info("Generating mnemonic for an empty string, changing for timestamp");
            name = String.valueOf(System.currentTimeMillis());
        }

        String genMnemonic = StringEscapeUtils.unescapeHtml4(name);
        // Replaces special character.
        genMnemonic = replaceSpecialChars(genMnemonic, "-");
        // Lower case
        genMnemonic = genMnemonic.toLowerCase();
        // Max length
        int lengthMnemonic = genMnemonic.length();
        genMnemonic = genMnemonic.substring(0, (lengthMnemonic > 64 ? 64 : lengthMnemonic));

        return genMnemonic;
    }

    /**
     * Replace special chars from a string with "-".
     *
     * @param text The text to be manipulated
     * @return The text with special characters replaced by "-"
     */
    public static String replaceSpecialChars(String text, String replace) {
        String result = replaceAccentedLetters(text);

        result = result.replaceAll("[^0-9A-Za-z]+", replace);
        result = result.replaceAll("-{2,}", replace);
        result = result.replaceAll("^" + replace + "+", "");
        result = result.replaceAll(replace + "+$", "");

        return result;
    }

    /**
     * Replace accented letter for the respective letter without accent
     *
     * @param text The text to be manipulated
     * @return The text without accents
     */
    public static String replaceAccentedLetters(String text) {
        if (text != null) {
            text = text.replaceAll("[ÂÀÁÄÃÅ]", "A");
            text = text.replaceAll("[âãàáäå]", "a");
            text = text.replaceAll("[ÊÈÉË]", "E");
            text = text.replaceAll("[êèéë]", "e");
            text = text.replaceAll("[ÎÍÌÏ]", "I");
            text = text.replaceAll("[îíìï]", "i");
            text = text.replaceAll("[ÔÕÒÓÖ]", "O");
            text = text.replaceAll("[ôõòóö]", "o");
            text = text.replaceAll("[ÛÙÚÜ]", "U");
            text = text.replaceAll("[ûúùü]", "u");
            text = text.replaceAll("Ç", "C");
            text = text.replaceAll("ç", "c");
            text = text.replaceAll("[ýÿ]", "y");
            text = text.replaceAll("Ý", "Y");
            text = text.replaceAll("ñ", "n");
            text = text.replaceAll("Ñ", "N");
        }

        return text;
    }
}
