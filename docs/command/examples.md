# Command Examples

All examples below assume they run in `JavaPlugin.onEnable()`, with `this` as the current plugin instance.

## Required Imports for the Examples

```java
import org.bukkit.plugin.java.JavaPlugin;
import org.coffeepop.betterPlugin.api.command.CommandBuilder;
import io.papermc.paper.command.brigadier.Commands; // Required for the subcommand example
import java.time.Duration;                          // Required for the cooldown example
import java.util.List;                              // Required for the completion example
```

## Basic Command

```java
CommandBuilder.create(this)
        .name("hello")
        .executes((sender, command, label, args) -> {
            sender.sendPlainMessage("Hello!");
            return true;
        })
        .register();
```

## Permission + Aliases

```java
CommandBuilder.create(this)
        .name("heal")
        .permission("myplugin.heal")
        .aliases("healme", "hp")
        .executes((sender, command, label, args) -> {
            sender.sendPlainMessage("You have been healed");
            return true;
        })
        .register();
```

## Tab Completion

```java
CommandBuilder.create(this)
        .name("give")
        .permission("myplugin.give")
        .executes((sender, command, label, args) -> {
            if (args.length < 1) {
                sender.sendPlainMessage("Usage: /give <item>");
                return false;
            }
            sender.sendPlainMessage("give " + String.join(" ", args));
            return true;
        })
        .tabCompleter((sender, command, alias, args) -> {
            if (args.length == 1) {
                return List.of("diamond", "iron", "gold");
            }
            return List.of();
        })
        .register();
```

## Player / Console Restrictions

```java
CommandBuilder.create(this)
        .name("feed")
        .playerOnly()
        .executes((sender, command, label, args) -> {
            sender.sendPlainMessage("You are full");
            return true;
        })
        .register();

CommandBuilder.create(this)
        .name("restart")
        .consoleOnly()
        .executes((sender, command, label, args) -> {
            sender.sendPlainMessage("restarting...");
            return true;
        })
        .register();
```

> Don't set both `playerOnly()` and `consoleOnly()` on the same command; the restrictions stack, and together they reject every sender.

## Cooldown

```java
CommandBuilder.create(this)
        .name("reward")
        .cooldown(Duration.ofSeconds(60))
        .executes((sender, command, label, args) -> {
            sender.sendPlainMessage("Claimed successfully");
            return true;
        })
        .register();
```

The cooldown only applies to players and only wraps the root execution path. The message is fixed English text for now; see [API Reference](/command/api) for details.

## Subcommands

Subcommands must keep the parent command's executor, otherwise `register()` throws `CommandException`:

```java
CommandBuilder.create(this)
        .name("admin")
        .permission("myplugin.admin")
        .executes((sender, command, label, args) -> {
            sender.sendPlainMessage("Usage: /admin <reload|status>");
            return true;
        })
        .then(Commands.literal("reload")
                .executes(ctx -> {
                    ctx.getSource().getSender().sendPlainMessage("reloaded!");
                    return 1;
                }))
        .then(Commands.literal("status")
                .executes(ctx -> {
                    ctx.getSource().getSender().sendPlainMessage("status ok");
                    return 1;
                }))
        .register();
```

## Combined Example

```java
List<String> targets = List.of("player1", "player2");

CommandBuilder.create(this)
        .name("tpa")
        .permission("myplugin.tpa")
        .aliases("tpahere")
        .description("Requests teleport to a player")
        .cooldown(Duration.ofSeconds(30))
        .executes((sender, command, label, args) -> {
            if (args.length < 1) {
                sender.sendPlainMessage("Usage: /tpa <player>");
                return false;
            }
            sender.sendPlainMessage("Sent teleport request to " + args[0]);
            return true;
        })
        .tabCompleter((sender, command, alias, args) -> args.length == 1 ? targets : List.of())
        .register();
```

> Tip: to complete online players, combine with the Bukkit API, for example
> `Bukkit.getOnlinePlayers().stream().map(Player::getName).toList()`.