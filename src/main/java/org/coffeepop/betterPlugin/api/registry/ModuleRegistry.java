package org.coffeepop.betterPlugin.api.registry;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A module registry built on top of {@link Registry}.
 * <p>
 * Modules are registered by name and can be enabled/disabled together or
 * individually. Enabling a module invokes {@link PluginModule#onEnable}, and
 * disabling invokes {@link PluginModule#onDisable}.
 */
public final class ModuleRegistry {

    private final JavaPlugin plugin;
    private final Registry<PluginModule> modules = new SimpleRegistry<>();
    private final Set<String> enabled = ConcurrentHashMap.newKeySet();

    /**
     * Creates a module registry owned by the given plugin.
     *
     * @param plugin the owning plugin
     */
    public ModuleRegistry(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    /**
     * Registers a module without enabling it.
     *
     * @param key    the module key
     * @param module the module
     */
    public void register(String key, PluginModule module) {
        modules.register(key, module);
    }

    /**
     * Enables a registered module.
     *
     * @param key the module key
     * @throws IllegalArgumentException if the module is not registered
     */
    public void enable(String key) {
        PluginModule module = modules.get(key)
                .orElseThrow(() -> new IllegalArgumentException("Module not registered: " + key));
        if (enabled.add(key)) {
            module.onEnable(plugin);
        }
    }

    /**
     * Disables a registered module.
     *
     * @param key the module key
     */
    public void disable(String key) {
        if (enabled.remove(key)) {
            modules.get(key).ifPresent(PluginModule::onDisable);
        }
    }

    /**
     * Enables all registered modules that are not already enabled.
     */
    public void enableAll() {
        for (String key : modules.keys()) {
            enable(key);
        }
    }

    /**
     * Disables all enabled modules.
     */
    public void disableAll() {
        for (String key : Set.copyOf(enabled)) {
            disable(key);
        }
    }

    /**
     * Unregisters a module, disabling it first if it is enabled.
     *
     * @param key the module key
     */
    public void unregister(String key) {
        disable(key);
        modules.remove(key);
    }

    /**
     * Returns the set of currently enabled module keys.
     *
     * @return an immutable snapshot
     */
    public Set<String> enabledKeys() {
        return Set.copyOf(enabled);
    }
}
