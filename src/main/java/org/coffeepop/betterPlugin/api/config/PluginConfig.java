package org.coffeepop.betterPlugin.api.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Small typed wrapper around a plugin's {@code config.yml}.
 * <p>
 * Unlike {@link JavaPlugin#getConfig()}, this class gives you typed getters
 * with defaults and a single {@link #reload()} entry point that also runs a
 * callback when the config is refreshed.
 */
public final class PluginConfig {

    private final JavaPlugin plugin;
    private final Runnable reloadCallback;

    /**
     * Creates a config wrapper without a reload callback.
     *
     * @param plugin the owning plugin
     */
    public PluginConfig(JavaPlugin plugin) {
        this(plugin, () -> {
        });
    }

    /**
     * Creates a config wrapper with a callback that runs after each reload.
     *
     * @param plugin         the owning plugin
     * @param reloadCallback code to run after {@link #reload()}
     */
    public PluginConfig(JavaPlugin plugin, Runnable reloadCallback) {
        this.plugin = plugin;
        this.reloadCallback = reloadCallback;
    }

    /**
     * Applies missing defaults, reloads {@code config.yml}, and runs the reload
     * callback.
     */
    public void reload() {
        if (plugin.getResource("config.yml") != null) {
            plugin.saveDefaultConfig();
        }
        plugin.reloadConfig();
        reloadCallback.run();
    }

    /**
     * Returns the underlying Bukkit configuration.
     */
    public FileConfiguration get() {
        return plugin.getConfig();
    }

    /**
     * Reads a string value with a default.
     */
    public String getString(String path, String defaultValue) {
        FileConfiguration config = get();
        return config.isSet(path) ? config.getString(path, defaultValue) : defaultValue;
    }

    /**
     * Reads an int value with a default.
     */
    public int getInt(String path, int defaultValue) {
        FileConfiguration config = get();
        return config.isSet(path) ? config.getInt(path, defaultValue) : defaultValue;
    }

    /**
     * Reads a long value with a default.
     */
    public long getLong(String path, long defaultValue) {
        FileConfiguration config = get();
        return config.isSet(path) ? config.getLong(path, defaultValue) : defaultValue;
    }

    /**
     * Reads a double value with a default.
     */
    public double getDouble(String path, double defaultValue) {
        FileConfiguration config = get();
        return config.isSet(path) ? config.getDouble(path, defaultValue) : defaultValue;
    }

    /**
     * Reads a boolean value with a default.
     */
    public boolean getBoolean(String path, boolean defaultValue) {
        FileConfiguration config = get();
        return config.isSet(path) ? config.getBoolean(path, defaultValue) : defaultValue;
    }

    /**
     * Reads a string list; returns an empty list when the path is not set.
     */
    public List<String> getStringList(String path) {
        FileConfiguration config = get();
        if (!config.isSet(path)) {
            return List.of();
        }
        return config.getStringList(path);
    }
}
