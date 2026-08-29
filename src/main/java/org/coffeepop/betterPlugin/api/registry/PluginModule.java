package org.coffeepop.betterPlugin.api.registry;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * A named module that can be enabled and disabled through
 * {@link ModuleRegistry}.
 */
public interface PluginModule {

    /**
     * Called when the module is enabled.
     *
     * @param plugin the owning plugin
     */
    void onEnable(JavaPlugin plugin);

    /**
     * Called when the module is disabled.
     */
    void onDisable();
}
