package org.coffeepop.betterPlugin.api.plugin;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.util.ArrayList;
import java.util.List;

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
}
