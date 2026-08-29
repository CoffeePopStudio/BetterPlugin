package org.coffeepop.betterPlugin.api.plugin;

import org.bukkit.scheduler.BukkitTask;
import net.kyori.adventure.text.Component;
import org.bukkit.event.player.PlayerJoinEvent;
import org.coffeepop.betterPlugin.api.command.CommandBuilder;
import org.coffeepop.betterPlugin.api.event.ListenerRegistry;
import org.coffeepop.betterPlugin.api.registry.ModuleRegistry;
import org.coffeepop.betterPlugin.api.registry.PluginModule;
import org.coffeepop.betterPlugin.api.registry.Registry;
import org.coffeepop.betterPlugin.api.scheduler.TaskScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PluginBaseTest {

    public static class TestPlugin extends PluginBase {
        final List<String> events = new ArrayList<>();
        boolean configReloaded;

        @Override
        protected void onPluginEnable() {
            events.add("enable");
        }

        @Override
        protected void onPluginDisable() {
            events.add("disable");
        }

        @Override
        protected void onConfigReload() {
            configReloaded = true;
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

        BukkitTask exposedRunSyncTimer(Runnable task, long delayTicks, long periodTicks) {
            return runSyncTimer(task, delayTicks, periodTicks);
        }

        boolean exposedServerReady() {
            return isServerReady();
        }

        void exposedRunWhenReady(Runnable task) {
            runWhenReady(task);
        }

        CommandBuilder exposedCommand() {
            return command();
        }

        Registry<String> exposedRegistry() {
            return registry();
        }

        TaskScheduler exposedTasks() {
            return tasks();
        }

        ListenerRegistry exposedListeners() {
            return listeners();
        }

        ModuleRegistry exposedModules() {
            return modules();
        }

        void exposedSaveDefaultResource(String path) {
            saveDefaultResource(path);
        }

        void exposedReloadPluginConfig() {
            reloadPluginConfig();
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

    @Test
    void ownedTasksAreCancelledOnDisable() {
        AtomicInteger runs = new AtomicInteger();
        BukkitTask task = plugin.exposedRunSyncTimer(runs::incrementAndGet, 0L, 1L);

        assertFalse(task.isCancelled(), "task should not be cancelled while the plugin is enabled");

        server.getScheduler().performTicks(3);
        assertTrue(runs.get() > 0, "repeating task should run when ticks pass");

        server.getPluginManager().disablePlugin(plugin);

        assertTrue(task.isCancelled(), "owned task should be cancelled when the plugin is disabled");
    }

    @Test
    void runWhenReadyDefersUntilServerIsReady() {
        AtomicBoolean ran = new AtomicBoolean();

        assertFalse(plugin.exposedServerReady(), "server should not be ready during onEnable");

        plugin.exposedRunWhenReady(() -> ran.set(true));
        assertFalse(ran.get(), "task should wait while the server is still starting");

        server.getScheduler().performTicks(1);

        assertTrue(plugin.exposedServerReady(), "server should be ready after the first tick");
        assertTrue(ran.get(), "deferred task should run once the server is ready");

        AtomicBoolean ranImmediately = new AtomicBoolean();
        plugin.exposedRunWhenReady(() -> ranImmediately.set(true));
        assertTrue(ranImmediately.get(), "task should run immediately when the server is already ready");
    }

    @Test
    void runWhenReadyDoesNotSkipTasksAfterAFailure() {
        AtomicBoolean secondRan = new AtomicBoolean();

        plugin.exposedRunWhenReady(() -> {
            throw new IllegalStateException("boom");
        });
        plugin.exposedRunWhenReady(() -> secondRan.set(true));

        server.getScheduler().performTicks(1);

        assertTrue(secondRan.get(), "a failing task must not prevent later tasks from running");
    }

    @Test
    void commandReturnsBuilderForThisPlugin() {
        CommandBuilder builder = plugin.exposedCommand();

        assertNotNull(builder, "command() should return a builder for the plugin");
    }

    @Test
    void registryIsSharedAcrossCalls() {
        Registry<String> first = plugin.exposedRegistry();
        first.register("service", "value");

        Registry<String> second = plugin.exposedRegistry();

        assertEquals("value", second.get("service").orElseThrow(), "registry() should return the same shared registry");
    }

    @Test
    void sharedTasksAreCancelledOnDisable() {
        BukkitTask task = plugin.exposedTasks().runSyncTimer(() -> {
        }, 0L, 1L);

        server.getScheduler().performTicks(1);
        server.getPluginManager().disablePlugin(plugin);

        assertTrue(task.isCancelled(), "tasks created through tasks() should be cancelled on disable");
    }

    @Test
    void sharedListenersAreUnregisteredOnDisable() {
        AtomicBoolean received = new AtomicBoolean();
        plugin.exposedListeners().register(PlayerJoinEvent.class, event -> received.set(true));

        server.getPluginManager().disablePlugin(plugin);
        server.getPluginManager().callEvent(new PlayerJoinEvent(server.addPlayer(), Component.text("joined")));

        assertFalse(received.get(), "listeners registered through listeners() should stop on disable");
    }

    @Test
    void sharedModulesAreDisabledOnDisable() {
        AtomicInteger disabled = new AtomicInteger();
        plugin.exposedModules().register("test", new PluginModule() {
            @Override
            public void onEnable(org.bukkit.plugin.java.JavaPlugin plugin) {
            }

            @Override
            public void onDisable() {
                disabled.incrementAndGet();
            }
        });
        plugin.exposedModules().enableAll();

        server.getPluginManager().disablePlugin(plugin);

        assertEquals(1, disabled.get(), "modules enabled through modules() should be disabled on disable");
    }

    @Test
    void saveDefaultResourceCopiesFileFromClasspath() {
        plugin.exposedSaveDefaultResource("test-resource.txt");

        File file = new File(plugin.getDataFolder(), "test-resource.txt");
        assertTrue(file.isFile(), "resource should be copied into the plugin data folder");
    }

    @Test
    void reloadPluginConfigInvokesCallback() {
        plugin.exposedReloadPluginConfig();

        assertTrue(plugin.configReloaded, "onConfigReload should run after reloadPluginConfig");
    }
}
