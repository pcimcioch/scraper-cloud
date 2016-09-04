package scraper.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for string operations.
 */
public final class StringUtils {

    private StringUtils() {

    }

    /**
     * Returns single match from given {@code text}.
     *
     * @param text  text to match
     * @param regex regular expression to use
     * @param group index of the group in regular expression which value should be returned
     * @return value of the given group or <tt>null</tt>
     */
    public static String getSingleMatch(String text, String regex, int group) {
        return getSingleMatch(text, regex, group, null);
    }

    /**
     * Returns single match from given {@code text}.
     *
     * @param text         text to match
     * @param regex        regular expression to use
     * @param group        index of the group in regular expression which value should be returned
     * @param defaultValue default value to return in case regular expression do not match text. May be <tt>null</tt>
     * @return value of the given group or {@code defaultValue}
     */
    public static String getSingleMatch(String text, String regex, int group, String defaultValue) {
        Matcher m = Pattern.compile(regex).matcher(text);
        if (m.matches()) {
            return m.group(group);
        }

        return defaultValue;
    }

    /**
     * Checks if any of the given strings is blank (<tt>null</tt> or <tt>""</tt>).
     *
     * @param values values to check. May be empty. May contain <tt>null</tt> values
     * @return <tt>true</tt> if any o the given strings is blank. <tt>false</tt> otherwise
     */
    public static boolean isAnyBlank(String... values) {
        for (String val : values) {
            if (isBlank(val)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if given string is blank (<tt>null</tt> or <tt>""</tt>).
     *
     * @param val string to check. May be <tt>null</tt>
     * @return <tt>true</tt> if given string is <tt>null</tt> or <tt>""</tt>. <tt>false</tt> otherwise
     */
    public static boolean isBlank(String val) {
        return val == null || val.isEmpty();
    }

    /**
     * Checks if given string is not blank (not <tt>null</tt> nor <tt>""</tt>).
     *
     * @param val string to check. May be <tt>null</tt>
     * @return <tt>true</tt> if given string is not <tt>null</tt> nor <tt>""</tt>. <tt>false</tt> otherwise
     */
    public static boolean isNotBlank(String val) {
        return val != null && !val.isEmpty();
    }
}
