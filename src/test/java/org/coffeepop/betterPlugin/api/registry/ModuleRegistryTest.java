package org.coffeepop.betterPlugin.api.registry;

import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.plugin.PluginMock;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModuleRegistryTest {

    @Test
    void enableAndDisableModule() {
        MockBukkit.mock();
        try {
            PluginMock plugin = MockBukkit.createMockPlugin();
            ModuleRegistry registry = new ModuleRegistry(plugin);
            AtomicInteger enabled = new AtomicInteger();
            AtomicInteger disabled = new AtomicInteger();

            registry.register("mod", new PluginModule() {
                @Override
                public void onEnable(JavaPlugin plugin) {
                    enabled.incrementAndGet();
                }

                @Override
                public void onDisable() {
                    disabled.incrementAndGet();
                }
            });

            registry.enable("mod");
            assertEquals(1, enabled.get());
            assertTrue(registry.enabledKeys().contains("mod"));

            registry.disable("mod");
            assertEquals(1, disabled.get());
            assertTrue(registry.enabledKeys().isEmpty());
        } finally {
            MockBukkit.unmock();
        }
    }

    @Test
    void disableAllDisablesEnabledModules() {
        MockBukkit.mock();
        try {
            PluginMock plugin = MockBukkit.createMockPlugin();
            ModuleRegistry registry = new ModuleRegistry(plugin);
            AtomicInteger disabled = new AtomicInteger();

            registry.register("one", new PluginModule() {
                @Override
                public void onEnable(JavaPlugin plugin) {
                }

                @Override
                public void onDisable() {
                    disabled.incrementAndGet();
                }
            });
            registry.register("two", new PluginModule() {
                @Override
                public void onEnable(JavaPlugin plugin) {
                }

                @Override
                public void onDisable() {
                    disabled.incrementAndGet();
                }
            });

            registry.enableAll();
            assertEquals(2, registry.enabledKeys().size());

            registry.disableAll();

            assertEquals(2, disabled.get());
            assertTrue(registry.enabledKeys().isEmpty());
        } finally {
            MockBukkit.unmock();
        }
    }
}
