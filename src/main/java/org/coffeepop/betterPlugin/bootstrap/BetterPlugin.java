package org.coffeepop.betterPlugin.bootstrap;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.coffeepop.betterPlugin.api.plugin.PluginBase;
import org.coffeepop.betterPlugin.internal.command.CommandRegistry;
import org.jetbrains.annotations.ApiStatus;

/**
 * Main plugin class. This class is internal bootstrap code and not part of the public API.
 */
@ApiStatus.Internal
public class BetterPlugin extends PluginBase {
    private static volatile BetterPlugin instance;
    private CommandRegistry commandRegistry;

    @Override
    protected void onPluginEnable() {
        instance = this;
        this.commandRegistry = new CommandRegistry();

        this.getLifecycleManager().registerEventHandler(
                LifecycleEvents.COMMANDS,
                event -> commandRegistry.registerAll(event.registrar())
        );

        command()
                .name("betterplugin")
                .description("Shows BetterPlugin version information")
                .executes((sender, _, _, _) -> {
                    sender.sendPlainMessage("BetterPlugin " + getPluginMeta().getVersion());
                    sender.sendPlainMessage("A framework for building Paper plugins.");
                    return true;
                })
                .register();
    }

    @Override
    protected void onPluginDisable() {
        instance = null;
    }

    @ApiStatus.Internal
    public static BetterPlugin getInstance() {
        return instance;
    }

    @ApiStatus.Internal
    public CommandRegistry getCommandRegistry() {
        return commandRegistry;
    }
}
