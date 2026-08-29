package org.coffeepop.betterPlugin.api.registry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleRegistryTest {

    @Test
    void registerGetContainsAndRemove() {
        SimpleRegistry<String> registry = new SimpleRegistry<>();

        registry.register("greeting", "hello");

        assertTrue(registry.contains("greeting"));
        assertEquals("hello", registry.get("greeting").orElseThrow());
        assertEquals("hello", registry.remove("greeting").orElseThrow());
        assertFalse(registry.contains("greeting"));
    }

    @Test
    void keysAndValuesAreSnapshots() {
        SimpleRegistry<String> registry = new SimpleRegistry<>();
        registry.register("a", "1");
        registry.register("b", "2");

        assertEquals(2, registry.keys().size());
        assertEquals(2, registry.values().size());

        registry.clear();

        assertTrue(registry.keys().isEmpty());
        assertTrue(registry.values().isEmpty());
    }
}
