/**
 * @Author: oneachina
 * @link: github.com/oneachina
 */
package org.coffeepop.betterPlugin.internal.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.plugin.java.JavaPlugin;
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
        registrations.add(new Registration(
                supplier,
                aliases == null ? List.of() : List.copyOf(aliases),
                owner,
                description
        ));
    }

    /**
     * Registers all pending commands with the provided Paper {@link Commands} registrar,
     * then clears the queue.
     *
     * @param commands the Paper command registrar
     */
    public void registerAll(Commands commands) {
        for (Registration registration : registrations) {
            var node = registration.supplier().apply(commands).build();
            if (registration.owner() != null) {
                commands.register(registration.owner().getPluginMeta(), node, registration.description(), registration.aliases());
            } else {
                commands.register(node, registration.description(), registration.aliases());
            }
        }
        registrations.clear();
    }

    private record Registration(
            Function<Commands, LiteralArgumentBuilder<CommandSourceStack>> supplier,
            List<String> aliases,
            JavaPlugin owner,
            String description
    ) {
    }
}
