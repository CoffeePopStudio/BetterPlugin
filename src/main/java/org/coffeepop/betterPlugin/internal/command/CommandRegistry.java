package org.coffeepop.betterPlugin.internal.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.plugin.java.JavaPlugin;
import org.coffeepop.betterPlugin.bootstrap.BetterPlugin;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Internal registry that collects pending command registrations and applies them
 * when Paper fires the {@code COMMANDS} lifecycle event.
 * <p>
 * This class is not part of the public plugin API and may change without notice.
 */
@ApiStatus.Internal
public class CommandRegistry {
    private final List<Registration> registrations = new ArrayList<>();
    private volatile boolean commandsEventFired;

    /**
     * Adds a command registration without aliases.
     *
     * @param supplier supplies the command builder using the active {@link Commands} registrar
     */
    public void addCommand(Function<Commands, LiteralArgumentBuilder<CommandSourceStack>> supplier) {
        addCommand(supplier, List.of(), null, null);
    }

    /**
     * Adds a command registration with aliases.
     *
     * @param supplier supplies the command builder using the active {@link Commands} registrar
     * @param aliases  aliases to register with the command
     */
    public void addCommand(Function<Commands, LiteralArgumentBuilder<CommandSourceStack>> supplier, List<String> aliases) {
        addCommand(supplier, aliases, null, null);
    }

    /**
     * Adds a command registration with aliases and an explicit owning plugin.
     *
     * @param supplier supplies the command builder using the active {@link Commands} registrar
     * @param aliases  aliases to register with the command
     * @param owner    the plugin that should own the command; may be {@code null} to use the current lifecycle owner
     */
    public void addCommand(Function<Commands, LiteralArgumentBuilder<CommandSourceStack>> supplier, List<String> aliases, JavaPlugin owner) {
        addCommand(supplier, aliases, owner, null);
    }

    /**
     * Adds a command registration with aliases, an explicit owning plugin, and a description.
     *
     * @param supplier    supplies the command builder using the active {@link Commands} registrar
     * @param aliases     aliases to register with the command
     * @param owner       the plugin that should own the command; may be {@code null} to use the current lifecycle owner
     * @param description the command description, or {@code null}
     */
    public void addCommand(Function<Commands, LiteralArgumentBuilder<CommandSourceStack>> supplier, List<String> aliases, JavaPlugin owner, String description) {
        if (commandsEventFired) {
            warnTooLate();
        }
        synchronized (registrations) {
            registrations.add(new Registration(
                    supplier,
                    aliases == null ? List.of() : List.copyOf(aliases),
                    owner,
                    description
            ));
        }
    }

    /**
     * Registers all pending commands with the provided Paper {@link Commands} registrar,
     * then clears the queue. The queue is snapshotted and cleared before registration,
     * so a failing supplier cannot cause commands to be registered twice.
     *
     * @param commands the Paper command registrar
     */
    public void registerAll(Commands commands) {
        commandsEventFired = true;
        List<Registration> pending;
        synchronized (registrations) {
            pending = List.copyOf(registrations);
            registrations.clear();
        }
        for (Registration registration : pending) {
            var node = registration.supplier().apply(commands).build();
            if (registration.owner() != null) {
                commands.register(registration.owner().getPluginMeta(), node, registration.description(), registration.aliases());
            } else {
                commands.register(node, registration.description(), registration.aliases());
            }
        }
    }

    /**
     * Number of commands still waiting to be registered. Exposed for tests
     * and diagnostics.
     */
    int pendingCount() {
        synchronized (registrations) {
            return registrations.size();
        }
    }

    private void warnTooLate() {
        BetterPlugin instance = BetterPlugin.getInstance();
        if (instance != null) {
            instance.getLogger().warning("A command was added after Paper's COMMANDS event; it will not be registered.");
        }
    }

    private record Registration(
            Function<Commands, LiteralArgumentBuilder<CommandSourceStack>> supplier,
            List<String> aliases,
            JavaPlugin owner,
            String description
    ) {
    }
}
