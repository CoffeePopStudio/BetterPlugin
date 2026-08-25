package org.coffeepop.betterPlugin.internal.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.coffeepop.betterPlugin.api.command.CommandBuilder;
import org.coffeepop.betterPlugin.api.exception.CommandException;
import org.coffeepop.betterPlugin.bootstrap.BetterPlugin;
import org.jetbrains.annotations.ApiStatus;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
    private final List<ArgumentBuilder<CommandSourceStack, ?>> children = new ArrayList<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private CommandContext<CommandSourceStack> context;
    private Command<CommandSourceStack> command;
    private CommandExecutor executor;
    private TabCompleter completer;

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
        this.aliases = aliases;
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
        this.cooldown = cooldown;
        return this;
    }

    @Override
    public CommandBuilderImpl then(ArgumentBuilder<CommandSourceStack, ?> child) {
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
    public void register() {
        if (name == null || name.isEmpty()) {
            throw new CommandException("Command name cannot be null or empty");
        }
        if (context == null && command == null && executor == null) {
            throw new CommandException("Command executor cannot be null; set context(...), executes(Command) or executes(CommandExecutor)");
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
                .<CommandSourceStack>literal(name)
                .executes(executorCommand);

        Predicate<CommandSourceStack> requirement = source -> true;
        if (permission != null && !permission.isEmpty()) {
            requirement = requirement.and(source -> source.getSender().hasPermission(permission));
        }
        if (playerOnly) {
            requirement = requirement.and(source -> source.getSender() instanceof Player);
        }
        if (consoleOnly) {
            requirement = requirement.and(source -> source.getSender() instanceof ConsoleCommandSender);
        }
        command.requires(requirement);

        for (ArgumentBuilder<CommandSourceStack, ?> child : children) {
            command.then(child);
        }

        if (children.isEmpty() && (completer != null || executor != null)) {
            var argsBuilder = Commands.argument("args", StringArgumentType.greedyString())
                    .executes(executorCommand);

            if (completer != null) {
                argsBuilder = argsBuilder.suggests((context, suggestionsBuilder) -> {
                    String[] parts = suggestionsBuilder.getInput().split(" ", -1);
                    String alias = parts.length > 0 ? parts[0] : name;
                    String[] args = parts.length > 1
                            ? Arrays.copyOfRange(parts, 1, parts.length)
                            : new String[0];

                    List<String> completions = completer.onTabComplete(
                            context.getSource().getSender(),
                            commandAdapter,
                            alias,
                            args
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

        JavaPlugin owner = plugin != null ? plugin : BetterPlugin.getInstance();
        BetterPlugin.getInstance().getCommandRegistry().addCommand(
                _ -> command,
                aliasesList,
                owner,
                description
        );
    }

    private Command<CommandSourceStack> resolveExecutor(BukkitCommandAdapter commandAdapter) {
        if (context != null) {
            return context.getCommand();
        }
        if (command != null) {
            return command;
        }
        return ctx -> {
            String[] parts = ctx.getInput().split(" ", -1);
            String label = parts.length > 0 ? parts[0] : name;
            String[] args = parts.length > 1
                    ? Arrays.copyOfRange(parts, 1, parts.length)
                    : new String[0];

            boolean result = executor.onCommand(ctx.getSource().getSender(), commandAdapter, label, args);
            return result ? Command.SINGLE_SUCCESS : 0;
        };
    }

    private Command<CommandSourceStack> applyCooldown(Command<CommandSourceStack> delegate) {
        return ctx -> {
            CommandSender sender = ctx.getSource().getSender();
            if (sender instanceof Player player) {
                long now = System.currentTimeMillis();
                Long until = cooldowns.get(player.getUniqueId());
                if (until != null && until > now) {
                    player.sendPlainMessage("Please wait before using this command again.");
                    return 0;
                }
                cooldowns.put(player.getUniqueId(), now + cooldown.toMillis());
            }
            return delegate.run(ctx);
        };
    }
}
