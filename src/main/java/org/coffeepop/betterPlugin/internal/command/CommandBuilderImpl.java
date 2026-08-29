package org.coffeepop.betterPlugin.internal.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.coffeepop.betterPlugin.api.command.CommandArguments;
import org.coffeepop.betterPlugin.api.command.CommandArgumentsExecutor;
import org.coffeepop.betterPlugin.api.command.CommandBuilder;
import org.coffeepop.betterPlugin.api.exception.CommandException;
import org.coffeepop.betterPlugin.bootstrap.BetterPlugin;
import org.jetbrains.annotations.ApiStatus;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Internal implementation of {@link CommandBuilder}.
 * <p>
 * This class is not part of the public plugin API and may change without notice.
 */
@ApiStatus.Internal
public class CommandBuilderImpl implements CommandBuilder {
    private String name;
    private String permission;
    private String[] aliases;
    private JavaPlugin plugin;
    private String description;
    private String usage;
    private String permissionMessage;
    private boolean playerOnly;
    private boolean consoleOnly;
    private Duration cooldown;
    private String cooldownMessage;
    private final List<ArgumentSpec> arguments = new ArrayList<>();
    private CommandArgumentsExecutor argumentExecutor;
    private final Map<String, Function<CommandSender, String>> placeholders = new HashMap<>();
    private BiFunction<String, CommandSender, String> messageFormatter;
    private final List<ArgumentBuilder<CommandSourceStack, ?>> children = new ArrayList<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private CommandContext<CommandSourceStack> context;
    private Command<CommandSourceStack> command;
    private CommandExecutor executor;
    private TabCompleter completer;
    private boolean registered;

    @Override
    public CommandBuilderImpl name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public CommandBuilderImpl permission(String permission) {
        this.permission = permission;
        return this;
    }

    @Override
    public CommandBuilderImpl aliases(String... aliases) {
        this.aliases = aliases == null ? null : aliases.clone();
        return this;
    }

    @Override
    public CommandBuilderImpl plugin(JavaPlugin plugin) {
        this.plugin = plugin;
        return this;
    }

    @Override
    public CommandBuilderImpl description(String description) {
        this.description = description;
        return this;
    }

    @Override
    public CommandBuilderImpl usage(String usage) {
        this.usage = usage;
        return this;
    }

    @Override
    public CommandBuilderImpl permissionMessage(String permissionMessage) {
        this.permissionMessage = permissionMessage;
        return this;
    }

    @Override
    public CommandBuilderImpl playerOnly() {
        this.playerOnly = true;
        return this;
    }

    @Override
    public CommandBuilderImpl consoleOnly() {
        this.consoleOnly = true;
        return this;
    }

    @Override
    public CommandBuilderImpl cooldown(Duration cooldown) {
        if (cooldown != null && cooldown.isNegative()) {
            throw new IllegalArgumentException("cooldown cannot be negative");
        }
        this.cooldown = cooldown;
        return this;
    }

    @Override
    public CommandBuilderImpl cooldownMessage(String cooldownMessage) {
        this.cooldownMessage = cooldownMessage;
        return this;
    }

    @Override
    public CommandBuilderImpl argument(String name, ArgumentType<?> type) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("argument name cannot be null or blank");
        }
        if (!arguments.isEmpty() && arguments.get(arguments.size() - 1).optional()) {
            throw new IllegalStateException("Cannot add an argument after an optional argument; optional must be last");
        }
        arguments.add(new ArgumentSpec(name, type));
        return this;
    }

    @Override
    public CommandBuilderImpl suggestions(String... values) {
        return suggestions(values == null ? List.of() : Arrays.asList(values));
    }

    @Override
    public CommandBuilderImpl suggestions(Collection<String> values) {
        requireLastArgument().setStaticSuggestions(List.copyOf(values));
        return this;
    }

    @Override
    public CommandBuilderImpl suggestOnlinePlayers() {
        requireLastArgument().setSuggestionProvider(source -> source.getSender().getServer().getOnlinePlayers().stream()
                .map(Player::getName)
                .toList());
        return this;
    }

    @Override
    public CommandBuilderImpl optional() {
        requireLastArgument().setOptional(true);
        return this;
    }

    @Override
    public CommandBuilderImpl arguments(CommandArgumentsExecutor executor) {
        this.argumentExecutor = executor;
        return this;
    }

    @Override
    public CommandBuilderImpl placeholder(String key, Function<CommandSender, String> resolver) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("placeholder key cannot be null or blank");
        }
        placeholders.put(key, resolver);
        return this;
    }

    @Override
    public CommandBuilderImpl messageFormatter(BiFunction<String, CommandSender, String> formatter) {
        this.messageFormatter = formatter;
        return this;
    }

    @Override
    public CommandBuilderImpl then(ArgumentBuilder<CommandSourceStack, ?> child) {
        if (child == null) {
            throw new NullPointerException("child");
        }
        children.add(child);
        return this;
    }

    @Override
    public CommandBuilderImpl context(CommandContext<CommandSourceStack> context) {
        this.context = context;
        return this;
    }

    @Override
    public CommandBuilderImpl executes(Command<CommandSourceStack> command) {
        this.command = command;
        return this;
    }

    @Override
    public CommandBuilderImpl executes(CommandExecutor executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public CommandBuilderImpl tabCompleter(TabCompleter completer) {
        this.completer = completer;
        return this;
    }

    @Override
    @SuppressWarnings("deprecation") // Command#setPermissionMessage is deprecated but still used for Bukkit adapter metadata
    public void register() {
        if (registered) {
            throw new CommandException("This builder has already been registered; create a new CommandBuilder for each command");
        }
        if (name == null || name.isBlank()) {
            throw new CommandException("Command name cannot be null or blank");
        }
        if (aliases != null) {
            for (String alias : aliases) {
                if (alias == null || alias.isBlank()) {
                    throw new CommandException("Command alias cannot be null or blank");
                }
                if (name.equals(alias)) {
                    throw new CommandException("Command alias cannot be the same as the command name");
                }
            }
        }
        if (context == null && command == null && executor == null && argumentExecutor == null) {
            throw new CommandException("Command executor cannot be null; set context(...), executes(Command), executes(CommandExecutor) or executes(CommandArgumentsExecutor)");
        }
        if (argumentExecutor != null && arguments.isEmpty()) {
            throw new CommandException("CommandArgumentsExecutor requires at least one argument(...)");
        }
        if (!arguments.isEmpty() && argumentExecutor == null) {
            throw new CommandException("Arguments require an executor; set executes(CommandArgumentsExecutor)");
        }
        if (context != null && context.getCommand() == null) {
            throw new CommandException("Command context does not contain an executor; provide a context built from a node with executes(...)");
        }

        List<String> aliasesList = aliases == null ? List.of() : Arrays.asList(aliases);
        BukkitCommandAdapter commandAdapter = new BukkitCommandAdapter(name, aliasesList, permission);
        if (description != null) {
            commandAdapter.setDescription(description);
        }
        if (usage != null) {
            commandAdapter.setUsage(usage);
        }
        if (permissionMessage != null) {
            commandAdapter.setPermissionMessage(permissionMessage);
        }

        Command<CommandSourceStack> executorCommand = resolveExecutor(commandAdapter);
        if (cooldown != null) {
            executorCommand = applyCooldown(executorCommand);
        }

        LiteralArgumentBuilder<CommandSourceStack> command = LiteralArgumentBuilder
                .<CommandSourceStack>literal(name);

        Predicate<CommandSourceStack> requirement = source -> true;
        if (permission != null && !permission.isEmpty()) {
            requirement = requirement.and(source -> {
                if (source.getSender().hasPermission(permission)) {
                    return true;
                }
                if (permissionMessage != null && !permissionMessage.isBlank()) {
                    source.getSender().sendPlainMessage(formatMessage(permissionMessage, source.getSender(), Map.of()));
                }
                return false;
            });
        }
        if (playerOnly) {
            requirement = requirement.and(source -> source.getSender() instanceof Player);
        }
        if (consoleOnly) {
            requirement = requirement.and(source -> source.getSender() instanceof ConsoleCommandSender);
        }
        command.requires(requirement);

        if (!arguments.isEmpty()) {
            Command<CommandSourceStack> argumentCommand = ctx -> {
                CommandArguments args = new CommandArgumentsImpl(ctx);
                boolean result = argumentExecutor.execute(
                        ctx.getSource().getSender(),
                        commandAdapter,
                        parseInput(ctx.getInput()).label(),
                        args
                );
                return result ? Command.SINGLE_SUCCESS : 0;
            };
            if (cooldown != null) {
                argumentCommand = applyCooldown(argumentCommand);
            }
            buildArgumentTree(command, argumentCommand);
        } else {
            command.executes(executorCommand);

            for (ArgumentBuilder<CommandSourceStack, ?> child : children) {
                command.then(child);
            }

            if (children.isEmpty() && (completer != null || executor != null)) {
                var argsBuilder = Commands.argument("args", StringArgumentType.greedyString())
                        .executes(executorCommand);

                if (completer != null) {
                    argsBuilder = argsBuilder.suggests((context, suggestionsBuilder) -> {
                        ParsedInput parsed = parseInput(suggestionsBuilder.getInput());

                        List<String> completions = completer.onTabComplete(
                                context.getSource().getSender(),
                                commandAdapter,
                                parsed.label(),
                                parsed.args()
                        );
                        if (completions != null) {
                            for (String completion : completions) {
                                suggestionsBuilder.suggest(completion);
                            }
                        }
                        return suggestionsBuilder.buildFuture();
                    });
                }

                command.then(argsBuilder);
            }
        }

        JavaPlugin owner = plugin != null ? plugin : BetterPlugin.getInstance();
        BetterPlugin.getInstance().getCommandRegistry().addCommand(
                _ -> command,
                aliasesList,
                owner,
                description
        );
        registered = true;
    }

    private Command<CommandSourceStack> resolveExecutor(BukkitCommandAdapter commandAdapter) {
        if (context != null) {
            return context.getCommand();
        }
        if (command != null) {
            return command;
        }
        return ctx -> {
            ParsedInput parsed = parseInput(ctx.getInput());

            boolean result = executor.onCommand(ctx.getSource().getSender(), commandAdapter, parsed.label(), parsed.args());
            return result ? Command.SINGLE_SUCCESS : 0;
        };
    }

    private ParsedInput parseInput(String input) {
        try {
            StringReader reader = new StringReader(input);
            List<String> parts = new ArrayList<>();
            while (reader.canRead()) {
                reader.skipWhitespace();
                if (!reader.canRead()) {
                    break;
                }
                parts.add(reader.readString());
            }
            String[] all = parts.toArray(String[]::new);
            String label = all.length > 0 ? all[0] : name;
            String[] args = all.length > 1
                    ? Arrays.copyOfRange(all, 1, all.length)
                    : new String[0];
            return new ParsedInput(label, args);
        } catch (CommandSyntaxException e) {
            String[] parts = input.split(" ", -1);
            String label = parts.length > 0 ? parts[0] : name;
            String[] args = parts.length > 1
                    ? Arrays.copyOfRange(parts, 1, parts.length)
                    : new String[0];
            return new ParsedInput(label, args);
        }
    }

    private void buildArgumentTree(LiteralArgumentBuilder<CommandSourceStack> command, Command<CommandSourceStack> argumentCommand) {
        boolean childOptional = false;
        ArgumentBuilder<CommandSourceStack, ?> chain = null;
        for (int i = arguments.size() - 1; i >= 0; i--) {
            ArgumentSpec spec = arguments.get(i);
            RequiredArgumentBuilder<CommandSourceStack, ?> node = Commands.argument(spec.name(), spec.type());
            applySuggestions(node, spec);
            if (chain == null) {
                node.executes(argumentCommand);
            } else {
                node.then(chain);
                if (childOptional) {
                    node.executes(argumentCommand);
                }
            }
            childOptional = spec.optional();
            chain = node;
        }
        command.then(chain);
        if (childOptional) {
            command.executes(argumentCommand);
        }
    }

    private void applySuggestions(RequiredArgumentBuilder<CommandSourceStack, ?> node, ArgumentSpec spec) {
        if (spec.suggestionProvider() != null) {
            node.suggests((context, builder) -> {
                for (String suggestion : spec.suggestionProvider().apply(context.getSource())) {
                    builder.suggest(suggestion);
                }
                return builder.buildFuture();
            });
        }
    }

    private ArgumentSpec requireLastArgument() {
        if (arguments.isEmpty()) {
            throw new IllegalStateException("No argument has been added yet");
        }
        return arguments.get(arguments.size() - 1);
    }

    private String formatMessage(String template, CommandSender sender, Map<String, String> runtimeValues) {
        if (messageFormatter != null) {
            return messageFormatter.apply(template, sender);
        }
        Map<String, String> values = new HashMap<>(runtimeValues);
        values.put("player", sender.getName());
        for (Map.Entry<String, Function<CommandSender, String>> entry : placeholders.entrySet()) {
            values.put(entry.getKey(), entry.getValue().apply(sender));
        }
        String result = template;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }

    private Command<CommandSourceStack> applyCooldown(Command<CommandSourceStack> delegate) {
        return ctx -> {
            CommandSender sender = ctx.getSource().getSender();
            if (sender instanceof Player player) {
                long now = System.nanoTime();
                if (isCoolingDown(player.getUniqueId(), now)) {
                    String message = cooldownMessage == null
                            ? "Please wait before using this command again."
                            : cooldownMessage;
                    long remainingSeconds = Math.max(0, (cooldowns.get(player.getUniqueId()) - now) / 1_000_000_000L);
                    player.sendPlainMessage(formatMessage(message, sender, Map.of("cooldown", Long.toString(remainingSeconds))));
                    return 0;
                }
                if (cooldowns.size() > 1000) {
                    cooldowns.entrySet().removeIf(entry -> entry.getValue() <= now);
                }
                cooldowns.put(player.getUniqueId(), now + cooldown.toNanos());
            }
            return delegate.run(ctx);
        };
    }

    private boolean isCoolingDown(UUID uuid, long now) {
        Long until = cooldowns.get(uuid);
        if (until == null) {
            return false;
        }
        if (until <= now) {
            cooldowns.remove(uuid);
            return false;
        }
        return true;
    }

    private record ParsedInput(String label, String[] args) {
    }

    private static final class ArgumentSpec {
        private final String name;
        private final ArgumentType<?> type;
        private Function<CommandSourceStack, Collection<String>> suggestionProvider;
        private boolean optional;

        ArgumentSpec(String name, ArgumentType<?> type) {
            this.name = name;
            this.type = type;
        }

        String name() {
            return name;
        }

        ArgumentType<?> type() {
            return type;
        }

        Function<CommandSourceStack, Collection<String>> suggestionProvider() {
            return suggestionProvider;
        }

        void setStaticSuggestions(List<String> values) {
            this.suggestionProvider = source -> values;
        }

        void setSuggestionProvider(Function<CommandSourceStack, Collection<String>> suggestionProvider) {
            this.suggestionProvider = suggestionProvider;
        }

        boolean optional() {
            return optional;
        }

        void setOptional(boolean optional) {
            this.optional = optional;
        }
    }

    private static final class CommandArgumentsImpl implements CommandArguments {
        private final CommandContext<CommandSourceStack> context;

        CommandArgumentsImpl(CommandContext<CommandSourceStack> context) {
            this.context = context;
        }

        @Override
        public boolean contains(String name) {
            try {
                context.getArgument(name, Object.class);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        @Override
        public String getString(String name) {
            return get(name, String.class);
        }

        @Override
        public int getInt(String name) {
            return get(name, Integer.class);
        }

        @Override
        public double getDouble(String name) {
            return get(name, Double.class);
        }

        @Override
        public boolean getBoolean(String name) {
            return get(name, Boolean.class);
        }

        private <T> T get(String name, Class<T> type) {
            try {
                return context.getArgument(name, type);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Missing argument '" + name + "'", e);
            }
        }
    }
}
