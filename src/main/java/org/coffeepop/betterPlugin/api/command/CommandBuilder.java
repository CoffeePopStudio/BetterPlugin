package org.coffeepop.betterPlugin.api.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.coffeepop.betterPlugin.internal.command.CommandBuilderImpl;
import org.jetbrains.annotations.ApiStatus;

import java.time.Duration;

/**
 * Fluent builder for registering Paper Brigadier commands.
 * <p>
 * This API is still experimental and may change in future versions.
 */
@ApiStatus.Experimental
public interface CommandBuilder {

    /**
     * Creates a new command builder.
     *
     * @return a new {@link CommandBuilder} instance
     */
    static CommandBuilder create() {
        return new CommandBuilderImpl();
    }

    /**
     * Creates a new command builder owned by the given plugin.
     * <p>
     * This is useful when this API is used by another plugin: the registered command
     * will be associated with that plugin instead of BetterPlugin.
     *
     * @param plugin the plugin that will own the command
     * @return a new {@link CommandBuilder} instance
     */
    static CommandBuilder create(JavaPlugin plugin) {
        return new CommandBuilderImpl().plugin(plugin);
    }

    /**
     * Sets the command name.
     *
     * @param name the command name; must not be {@code null} or empty
     * @return this builder, for chaining
     */
    CommandBuilder name(String name);

    /**
     * Sets the permission required to execute this command.
     *
     * @param permission the permission node, or {@code null} for no permission
     * @return this builder, for chaining
     */
    CommandBuilder permission(String permission);

    /**
     * Sets additional command aliases.
     *
     * @param aliases the aliases to register
     * @return this builder, for chaining
     */
    CommandBuilder aliases(String... aliases);

    /**
     * Sets the plugin that will own this command.
     * <p>
     * If not set, the command is owned by BetterPlugin.
     *
     * @param plugin the owning plugin
     * @return this builder, for chaining
     */
    CommandBuilder plugin(JavaPlugin plugin);

    /**
     * Sets the command description shown in help.
     *
     * @param description the description
     * @return this builder, for chaining
     */
    CommandBuilder description(String description);

    /**
     * Sets the usage metadata exposed through the lightweight {@link Command}
     * adapter passed to executors and tab completers.
     * <p>
     * This value is not used by Paper's Brigadier registration itself.
     *
     * @param usage the usage string, e.g. {@code "/cmd <player>"}
     * @return this builder, for chaining
     */
    CommandBuilder usage(String usage);

    /**
     * Sets the permission-message metadata exposed through the lightweight
     * {@link Command} adapter passed to executors and tab completers.
     * <p>
     * Permission enforcement is handled by Brigadier's {@code requires}
     * predicate; this value is not shown automatically when a sender lacks
     * the required permission.
     *
     * @param permissionMessage the permission message
     * @return this builder, for chaining
     */
    CommandBuilder permissionMessage(String permissionMessage);

    /**
     * Restricts this command to players only.
     *
     * @return this builder, for chaining
     */
    CommandBuilder playerOnly();

    /**
     * Restricts this command to console senders only.
     *
     * @return this builder, for chaining
     */
    CommandBuilder consoleOnly();

    /**
     * Adds a per-player cooldown to the root execution path of this command.
     * <p>
     * Subcommands added via {@link #then(ArgumentBuilder)} use their own
     * executors and are not affected by this cooldown. When a player is
     * blocked, the executor does not run and a fixed English message is sent.
     *
     * @param cooldown the cooldown duration
     * @return this builder, for chaining
     */
    CommandBuilder cooldown(Duration cooldown);

    /**
     * Adds a child node to the command, enabling subcommands or arguments.
     * <p>
     * When child nodes are present, {@link #tabCompleter(TabCompleter)} is
     * not applied; use Brigadier node APIs for completion instead.
     *
     * @param child the child Brigadier node builder
     * @return this builder, for chaining
     */
    CommandBuilder then(ArgumentBuilder<CommandSourceStack, ?> child);

    /**
     * Sets the Brigadier context whose command will be used as the executor.
     *
     * @param context the command context; must not be {@code null}
     * @return this builder, for chaining
     */
    CommandBuilder context(CommandContext<CommandSourceStack> context);

    /**
     * Sets a raw Brigadier command as the executor.
     *
     * @param command the Brigadier command
     * @return this builder, for chaining
     */
    CommandBuilder executes(Command<CommandSourceStack> command);

    /**
     * Sets a Bukkit {@link CommandExecutor} as the executor.
     * <p>
     * This overload is more convenient for Bukkit-style code: the executor receives
     * the {@link org.bukkit.command.CommandSender} and {@code String[] args} directly,
     * so you do not need to call {@code ctx.getSource().getSender()}.
     *
     * @param executor the Bukkit command executor
     * @return this builder, for chaining
     */
    CommandBuilder executes(CommandExecutor executor);

    /**
     * Sets a Bukkit {@link TabCompleter} used for argument tab completion.
     * <p>
     * This completer is only used when no child nodes are registered via
     * {@link #then(ArgumentBuilder)}. For subcommands, provide suggestions
     * with Brigadier's own node APIs.
     *
     * @param completer the tab completer, or {@code null} for no custom completion
     * @return this builder, for chaining
     */
    CommandBuilder tabCompleter(TabCompleter completer);

    /**
     * Validates the builder and queues the command for registration with the
     * plugin's command registry.
     * <p>
     * Actual registration happens when Paper fires the {@code COMMANDS}
     * lifecycle event, so this method must be called before that event
     * (typically during {@code onEnable()}). Calls made after the event has
     * fired will not take effect.
     *
     * @throws org.coffeepop.betterPlugin.api.exception.CommandException if the command name is
     *                                                                  {@code null}/{@code empty}, or if neither
     *                                                                  {@link #context(CommandContext)},
     *                                                                  {@link #executes(Command)},
     *                                                                  nor {@link #executes(CommandExecutor)} was set
     */
    void register();
}
