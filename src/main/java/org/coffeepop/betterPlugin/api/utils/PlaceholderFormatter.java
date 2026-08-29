package org.coffeepop.betterPlugin.api.utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Small placeholder formatting utility.
 * <p>
 * Replaces {@code {key}} occurrences in a template with values from a map.
 * Replacement is single-pass: values inserted by one placeholder are never
 * treated as placeholders themselves.
 */
public final class PlaceholderFormatter {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{([^{}]+)\\}");

    private PlaceholderFormatter() {
    }

    /**
     * Replaces every {@code {key}} in the template with the corresponding map
     * value. Keys that are not present are left untouched.
     *
     * @param template the template text
     * @param values   placeholder values keyed by name (without braces)
     * @return the formatted text
     */
    public static String format(String template, Map<String, String> values) {
        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String replacement = values.get(matcher.group(1));
            if (replacement == null) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group(0)));
            } else {
                matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
