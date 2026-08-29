package org.coffeepop.betterPlugin.api.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.plugin.PluginMock;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PluginConfigTest {

    private PluginMock plugin;
    private PluginConfig config;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.createMockPlugin();
        config = new PluginConfig(plugin);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void typedGettersReadValuesAndDefaults() {
        plugin.getConfig().set("text", "hello");
        plugin.getConfig().set("number", 42);
        plugin.getConfig().set("ratio", 1.5);
        plugin.getConfig().set("flag", true);
        plugin.getConfig().set("list", List.of("a", "b"));

        assertEquals("hello", config.getString("text", "dflt"));
        assertEquals(42, config.getInt("number", 0));
        assertEquals(42L, config.getLong("number", 0L));
        assertEquals(1.5, config.getDouble("ratio", 0.0));
        assertEquals(true, config.getBoolean("flag", false));
        assertEquals(List.of("a", "b"), config.getStringList("list"));

        assertEquals("dflt", config.getString("missing", "dflt"));
        assertEquals(7, config.getInt("missing", 7));
        assertEquals(List.of(), config.getStringList("missing"));
    }

    @Test
    void reloadRunsCallback() {
        AtomicInteger reloads = new AtomicInteger();
        PluginConfig withCallback = new PluginConfig(plugin, reloads::incrementAndGet);

        withCallback.reload();

        assertEquals(1, reloads.get());
    }
}
