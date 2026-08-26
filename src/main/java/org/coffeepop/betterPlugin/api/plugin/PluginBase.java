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
}
