package io.cocast.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import java.net.IDN;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

public class Email {

    /*
     * Taken from Hibernate EmailValidator
     */
    private static String ATOM = "[a-z0-9!#$%&'*+/=?^_`{|}~-]";
    private static String DOMAIN = ATOM + "+(\\." + ATOM + "+)*";
    private static String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";

    /**
     * Regular expression for the local part of an email address (everything before '@')
     */
    private static final Pattern localPattern = java.util.regex.Pattern.compile(
            ATOM + "+(\\." + ATOM + "+)*", CASE_INSENSITIVE
    );

    /**
     * Regular expression for the domain part of an email address (everything after '@')
     */
    private static final Pattern domainPattern = java.util.regex.Pattern.compile(
            DOMAIN + "|" + IP_DOMAIN, CASE_INSENSITIVE
    );

    public static final Email EMPTY = new Email(StringUtils.EMPTY);

    @org.hibernate.validator.constraints.Email
    private String value;

    private Email(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (Email.class.isInstance(obj)) {
            Email other = Email.class.cast(obj);
            return Objects.equals(value, other.value);
        }
        return false;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return StringUtils.isBlank(value);
    }

    @JsonIgnore
    public boolean isNotEmpty() {
        return !this.isEmpty();
    }

    @JsonIgnore
    public String getDomain() {
        if (isNotEmpty()) {
            return value.split("@")[1];
        }
        return null;
    }

    @JsonIgnore
    public String getNickName() {
        if (isNotEmpty()) {
            return value.split("@")[0];
        }
        return null;
    }

    @JsonCreator
    public static Email of(@JsonProperty("value") String email) {
        if (StringUtils.isBlank(email)) {
            return EMPTY;
        }
        if (isValid(email)) {
            return new Email(email);
        }
        throw new IllegalArgumentException("This email is not valid: " + email);
    }

    /*
    Taken from hibernate validator
     */
    public static boolean isValid(CharSequence value) {
        if (value == null || value.length() == 0) {
            return true;
        }

        // split email at '@' and consider local and domain part separately;
        // note a split limit of 3 is used as it causes all characters following to an (illegal) second @ character to
        // be put into a separate array element, avoiding the regex application in this case since the resulting array
        // has more than 2 elements
        String[] emailParts = value.toString().split("@", 3);
        if (emailParts.length != 2) {
            return false;
        }

        // if we have a trailing dot in local or domain part we have an invalid email address.
        // the regular expression match would take care of this, but IDN.toASCII drops trailing the trailing '.'
        // (imo a bug in the implementation)
        if (emailParts[0].endsWith(".") || emailParts[1].endsWith(".")) {
            return false;
        }

        if (!matchPart(emailParts[0], localPattern)) {
            return false;
        }

        return matchPart(emailParts[1], domainPattern);
    }

    private static boolean matchPart(String part, Pattern pattern) {
        try {
            part = IDN.toASCII(part);
        } catch (IllegalArgumentException e) {
            // occurs when the label is too long (>63, even though it should probably be 64 - see http://www.rfc-editor.org/errata_search.php?rfc=3696,
            // practically that should not be a problem)
            return false;
        }
        Matcher matcher = pattern.matcher(part);
        return matcher.matches();
    }

}