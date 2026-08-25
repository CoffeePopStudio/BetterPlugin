# Command API Quick Start

::: warning Experimental API
`CommandBuilder` and `CommandException` are currently experimental APIs; the interfaces may change between versions.
:::

## Prerequisites

- The plugin declares a runtime dependency on BetterPlugin (`depend` in `plugin.yml`)
- Call it in `onEnable()`; `this` is the current plugin instance (either `JavaPlugin` or `PluginBase` works)
- `register()` must be called before `LifecycleEvents.COMMANDS`, i.e. during `onEnable()`

## Required Imports for the Examples

```java
import org.bukkit.plugin.java.JavaPlugin;
import org.coffeepop.betterPlugin.api.command.CommandBuilder;
import io.papermc.paper.command.brigadier.Commands; // Required for the subcommand example
import java.time.Duration;                          // Required for the cooldown example
import java.util.List;                              // Required for the completion example
```

## Minimal Command

```java
CommandBuilder.create(this)
        .name("ping")
        .executes((sender, command, label, args) -> {
            sender.sendPlainMessage("pong");
            return true;
        })
        .register();
```

## Common Configuration

```java
CommandBuilder.create(this)
        .name("greet")
        .permission("myplugin.greet")
        .aliases("hello")
        .description("Greets the sender")
        .executes((sender, command, label, args) -> {
            sender.sendPlainMessage("Hello, " + sender.getName() + "!");
            return true;
        })
        .register();
```

> `.usage()` and `.permissionMessage()` are currently only metadata attached to the callback parameter `command`; they do not affect the Brigadier registration result. See [API Reference](/command/api) for details.

## Completion, Restrictions, and Cooldown

```java
CommandBuilder.create(this)
        .name("feed")
        .playerOnly()
        .cooldown(Duration.ofSeconds(30))
        .executes((sender, command, label, args) -> {
            sender.sendPlainMessage("You are full");
            return true;
        })
        .register();

CommandBuilder.create(this)
        .name("give")
        .executes((sender, command, label, args) -> true)
        .tabCompleter((sender, command, alias, args) ->
                args.length == 1 ? List.of("diamond", "iron", "gold") : List.of())
        .register();
```

## Subcommands

Subcommands must keep the parent command's executor, otherwise `register()` throws `CommandException` because validation fails:

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
        .register();
```

> Note: after adding `.then(...)` child nodes, `.tabCompleter(...)` is ignored; the parent command's `.cooldown(...)` also only applies to the parent execution path, and child nodes are not affected.

For more configuration options, see [API Reference](/command/api); for complete examples, see [Examples](/command/examples).
