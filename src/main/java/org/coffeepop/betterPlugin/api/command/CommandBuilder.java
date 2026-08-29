package org.coffeepop.betterPlugin.api.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.coffeepop.betterPlugin.internal.command.CommandBuilderImpl;

import java.time.Duration;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Fluent builder for registering Paper Brigadier commands.
 */
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
     * Sets the permission message sent when a sender without the required
     * permission tries to run this command.
     * <p>
     * The value is also exposed through the lightweight {@link Command} adapter
     * passed to executors and tab completers. If not set, no custom message is
     * sent when the permission check fails.
     *
     * @param permissionMessage the permission message
     * @return this builder, for chaining
     */
    CommandBuilder permissionMessage(String permissionMessage);

    /**
     * Restricts this command to players only.
     * <p>
     * Restrictions combine: a command restricted with both {@link #permission(String)}
     * and this method only runs for players who also have the permission.
     *
     * @return this builder, for chaining
     */
    CommandBuilder playerOnly();

    /**
     * Restricts this command to console senders only.
     * <p>
     * Restrictions combine: a command restricted with both {@link #permission(String)}
     * and this method only runs for console senders who also have the permission.
     *
     * @return this builder, for chaining
     */
    CommandBuilder consoleOnly();

    /**
     * Adds a per-player cooldown to the root execution path of this command.
     * <p>
     * Subcommands added via {@link #then(ArgumentBuilder)} use their own
     * executors and are not affected by this cooldown. When a player is
     * blocked, the executor does not run and a message is sent (see
     * {@link #cooldownMessage(String)} for the default).
     *
     * @param cooldown the cooldown duration
     * @return this builder, for chaining
     */
    CommandBuilder cooldown(Duration cooldown);

    /**
     * Sets the message shown when a player is still on cooldown.
     * <p>
     * If not set, the fixed English message
     * {@code "Please wait before using this command again."} is used.
     *
     * @param cooldownMessage the cooldown message
     * @return this builder, for chaining
     */
    CommandBuilder cooldownMessage(String cooldownMessage);

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
     * Adds a typed argument to the command.
     * <p>
     * Arguments are chained in declaration order. Use
     * {@link #suggestions(String...)} or {@link #suggestOnlinePlayers()} to add
     * tab completion for the most recent argument, and
     * {@link #optional()} to make the final argument optional.
     *
     * @param name the argument name, used by {@link CommandArguments}
     * @param type the Brigadier argument type
     * @return this builder, for chaining
     */
    CommandBuilder argument(String name, ArgumentType<?> type);

    /**
     * Adds static tab-completion suggestions to the most recent argument.
     *
     * @param values the suggestion values
     * @return this builder, for chaining
     */
    CommandBuilder suggestions(String... values);

    /**
     * Adds static tab-completion suggestions to the most recent argument.
     *
     * @param values the suggestion values
     * @return this builder, for chaining
     */
    CommandBuilder suggestions(Collection<String> values);

    /**
     * Adds the names of all online players as tab-completion suggestions to
     * the most recent argument.
     *
     * @return this builder, for chaining
     */
    CommandBuilder suggestOnlinePlayers();

    /**
     * Makes the final argument optional. Must be called after
     * {@link #argument(String, ArgumentType)} and before adding another
     * argument.
     *
     * @return this builder, for chaining
     */
    CommandBuilder optional();

    /**
     * Sets an executor that receives typed arguments.
     * <p>
     * This overload is only valid when the command declares at least one
     * {@link #argument(String, ArgumentType)}.
     *
     * @param executor the argument executor
     * @return this builder, for chaining
     */
    CommandBuilder arguments(CommandArgumentsExecutor executor);

    /**
     * Registers a custom placeholder resolver used when formatting command
     * messages such as permission and cooldown messages.
     * <p>
     * The resolver receives the command sender and returns the replacement
     * text. Built-in placeholders include {@code {player}} and, for cooldown
     * messages, {@code {cooldown}}.
     *
     * @param key      the placeholder name without braces, e.g. {@code "prefix"}
     * @param resolver the resolver producing the replacement text
     * @return this builder, for chaining
     */
    CommandBuilder placeholder(String key, Function<CommandSender, String> resolver);

    /**
     * Replaces the default message formatting logic entirely.
     * <p>
     * The formatter receives the raw message template and the command sender,
     * and returns the final text. When not set, built-in and custom
     * placeholders are replaced by the default formatter.
     *
     * @param formatter the message formatter
     * @return this builder, for chaining
     */
    CommandBuilder messageFormatter(BiFunction<String, CommandSender, String> formatter);

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
     *                                                                  {@code null} or blank, an alias is {@code null},
     *                                                                  blank or equal to the name, if neither
     *                                                                  {@link #context(CommandContext)},
     *                                                                  {@link #executes(Command)},
     *                                                                  nor {@link #executes(CommandExecutor)} was set,
     *                                                                  if the context does not contain an executor,
     *                                                                  or if this builder has already been registered
     */
    void register();
}
