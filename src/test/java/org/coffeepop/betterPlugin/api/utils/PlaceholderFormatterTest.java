package org.coffeepop.betterPlugin.api.utils;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlaceholderFormatterTest {

    @Test
    void replacesKnownPlaceholders() {
        String result = PlaceholderFormatter.format(
                "Hello {player}, you have {amount} items.",
                Map.of("player", "alice", "amount", "3")
        );

        assertEquals("Hello alice, you have 3 items.", result);
    }

    @Test
    void leavesUnknownPlaceholdersUntouched() {
        String result = PlaceholderFormatter.format(
                "Value: {value}",
                Map.of("other", "x")
        );

        assertEquals("Value: {value}", result);
    }

    @Test
    void insertedValuesAreNotReplacedAgain() {
        String result = PlaceholderFormatter.format(
                "{outer}",
                Map.of("outer", "text {inner}", "inner", "INNER")
        );

        assertEquals("text {inner}", result, "values inserted by a placeholder must not be re-scanned");
    }
}
