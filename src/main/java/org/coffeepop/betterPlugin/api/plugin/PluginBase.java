package org.coffeepop.betterPlugin.api.plugin;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin entry base class for BetterPlugin users.
 * <p>
 * Subclasses override {@link #onPluginEnable()} / {@link #onPluginDisable()}
 * instead of {@code onEnable()} / {@code onDisable()}, so the framework can
 * guarantee cleanup (for example, cancelling owned tasks) on disable.
 */
public abstract class PluginBase extends JavaPlugin {

    @Override
    public final void onEnable() {
        onPluginEnable();
    }

    @Override
    public final void onDisable() {
        onPluginDisable();
    }

    /**
     * Called when the plugin is enabled. Override this instead of
     * {@code onEnable()}.
     */
    protected void onPluginEnable() {
    }

    /**
     * Called when the plugin is disabled. Override this instead of
     * {@code onDisable()}.
     */
    protected void onPluginDisable() {
    }

    /**
     * Returns the plugin's logger. Equivalent to {@link #getLogger()}, kept
     * as a short alias for convenience.
     */
    protected java.util.logging.Logger log() {
        return getLogger();
    }

    /**
     * Reads a {@code String} from the plugin config, falling back to
     * {@code defaultValue} when the path is not set.
     */
    protected String configString(String path, String defaultValue) {
        var config = getConfig();
        return config.isSet(path) ? config.getString(path, defaultValue) : defaultValue;
    }

    /**
     * Reads an {@code int} from the plugin config, falling back to
     * {@code defaultValue} when the path is not set.
     */
    protected int configInt(String path, int defaultValue) {
        var config = getConfig();
        return config.isSet(path) ? config.getInt(path, defaultValue) : defaultValue;
    }

    /**
     * Reads a {@code long} from the plugin config, falling back to
     * {@code defaultValue} when the path is not set.
     */
    protected long configLong(String path, long defaultValue) {
        var config = getConfig();
        return config.isSet(path) ? config.getLong(path, defaultValue) : defaultValue;
    }

    /**
     * Reads a {@code double} from the plugin config, falling back to
     * {@code defaultValue} when the path is not set.
     */
    protected double configDouble(String path, double defaultValue) {
        var config = getConfig();
        return config.isSet(path) ? config.getDouble(path, defaultValue) : defaultValue;
    }

    /**
     * Reads a {@code boolean} from the plugin config, falling back to
     * {@code defaultValue} when the path is not set.
     */
    protected boolean configBoolean(String path, boolean defaultValue) {
        var config = getConfig();
        return config.isSet(path) ? config.getBoolean(path, defaultValue) : defaultValue;
    }

    /**
     * Reads a string list from the plugin config. Returns an empty list when
     * the path is not set.
     */
    protected java.util.List<String> configStringList(String path) {
        var config = getConfig();
        if (!config.isSet(path)) {
            return java.util.List.of();
        }
        return config.getStringList(path);
    }
}
