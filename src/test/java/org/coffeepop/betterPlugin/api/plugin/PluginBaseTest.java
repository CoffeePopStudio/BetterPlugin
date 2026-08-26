package org.coffeepop.betterPlugin.api.plugin;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PluginBaseTest {

    public static class TestPlugin extends PluginBase {
        final List<String> events = new ArrayList<>();

        @Override
        protected void onPluginEnable() {
            events.add("enable");
        }

        @Override
        protected void onPluginDisable() {
            events.add("disable");
        }

        java.util.logging.Logger exposedLog() {
            return log();
        }

        String exposedConfigString(String path, String defaultValue) {
            return configString(path, defaultValue);
        }

        int exposedConfigInt(String path, int defaultValue) {
            return configInt(path, defaultValue);
        }

        long exposedConfigLong(String path, long defaultValue) {
            return configLong(path, defaultValue);
        }

        double exposedConfigDouble(String path, double defaultValue) {
            return configDouble(path, defaultValue);
        }

        boolean exposedConfigBoolean(String path, boolean defaultValue) {
            return configBoolean(path, defaultValue);
        }

        List<String> exposedConfigStringList(String path) {
            return configStringList(path);
        }
    }

    private ServerMock server;
    private TestPlugin plugin;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        server = MockBukkit.getMock();
        plugin = MockBukkit.load(TestPlugin.class);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void lifecycleHooksAreInvoked() {
        assertTrue(plugin.events.contains("enable"), "onPluginEnable should run when the plugin is enabled");

        server.getPluginManager().disablePlugin(plugin);

        assertTrue(plugin.events.contains("disable"), "onPluginDisable should run when the plugin is disabled");
    }

    @Test
    void logReturnsPluginLogger() {
        assertEquals(plugin.getLogger(), plugin.exposedLog(), "log() should return the plugin's own logger");
    }

    @Test
    void configHelpersReadValues() {
        var config = plugin.getConfig();
        config.set("text", "hello");
        config.set("number", 42);
        config.set("ratio", 1.5);
        config.set("flag", true);
        config.set("list", List.of("a", "b"));

        assertEquals("hello", plugin.exposedConfigString("text", "dflt"));
        assertEquals(42, plugin.exposedConfigInt("number", 0));
        assertEquals(42L, plugin.exposedConfigLong("number", 0L));
        assertEquals(1.5, plugin.exposedConfigDouble("ratio", 0.0));
        assertEquals(true, plugin.exposedConfigBoolean("flag", false));
        assertEquals(List.of("a", "b"), plugin.exposedConfigStringList("list"));
    }

    @Test
    void configHelpersFallBackToDefaults() {
        assertEquals("dflt", plugin.exposedConfigString("missing", "dflt"));
        assertEquals(7, plugin.exposedConfigInt("missing", 7));
        assertEquals(7L, plugin.exposedConfigLong("missing", 7L));
        assertEquals(2.5, plugin.exposedConfigDouble("missing", 2.5));
        assertEquals(true, plugin.exposedConfigBoolean("missing", true));
        assertEquals(List.of(), plugin.exposedConfigStringList("missing"));
    }
}
