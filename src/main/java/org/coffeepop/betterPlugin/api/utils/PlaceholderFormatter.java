package org.coffeepop.betterPlugin.api.utils;

import java.util.Map;

/**
 * Small placeholder formatting utility.
 * <p>
 * Replaces {@code {key}} occurrences in a template with values from a map.
 */
public final class PlaceholderFormatter {

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
        String result = template;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }
}
