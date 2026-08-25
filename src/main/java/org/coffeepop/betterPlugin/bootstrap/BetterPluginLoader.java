package org.coffeepop.betterPlugin.bootstrap;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import org.jetbrains.annotations.ApiStatus;

/**
 * Paper plugin loader entry point for BetterPlugin.
 * <p>
 * This class is internal bootstrap code and not part of the public API.
 */
@ApiStatus.Internal
public class BetterPluginLoader implements PluginLoader {

    @Override
    public void classloader(final PluginClasspathBuilder builder) {
        // Add dynamically loaded libraries here
    }
}
