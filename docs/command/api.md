# Command API Reference

The command module lives in the `org.coffeepop.betterPlugin.api.command` package, and `CommandBuilder` is its entry point.

## Required Imports for the Examples

```java
import org.bukkit.plugin.java.JavaPlugin;
import org.coffeepop.betterPlugin.api.command.CommandBuilder;
import io.papermc.paper.command.brigadier.Commands; // Required for the subcommand example
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import java.time.Duration;                          // Required for the cooldown example
import java.util.List;                              // Required for the completion example
```

## Factory Methods

### `create()`

Creates a `CommandBuilder`; the command belongs to BetterPlugin by default.

```java
CommandBuilder builder = CommandBuilder.create();
```

Third-party plugins should use `create(this)` or `.plugin(this)` instead, so commands don't get registered under BetterPlugin.

### `create(JavaPlugin plugin)`

Creates a `CommandBuilder`; the command belongs to the plugin you pass in.

```java
CommandBuilder builder = CommandBuilder.create(this);
```

## Method Reference

| Method | Type | Description |
| --- | --- | --- |
| `name(String name)` | Configuration | Sets the command name |
| `permission(String permission)` | Configuration | Sets the required permission |
| `aliases(String... aliases)` | Configuration | Sets the command aliases |
| `plugin(JavaPlugin plugin)` | Configuration | Sets the plugin that owns the command |
| `description(String description)` | Configuration | Sets the command description |
| `usage(String usage)` | Configuration | Sets usage metadata (only readable through the callback parameter `command`; does not affect Brigadier registration) |
| `permissionMessage(String message)` | Configuration | Sets the message sent when the sender lacks the required permission |
| `playerOnly()` | Configuration | Only players can execute |
| `consoleOnly()` | Configuration | Only the console can execute |
| `cooldown(Duration duration)` | Configuration | Sets the root command's player cooldown |
| `cooldownMessage(String message)` | Configuration | Sets the cooldown message (default is fixed English) |
| `argument(String name, ArgumentType type)` | Configuration | Adds a typed argument |
| `suggestions(String... values)` | Configuration | Adds static tab-completion suggestions to the latest argument |
| `suggestOnlinePlayers()` | Configuration | Adds online player names as suggestions to the latest argument |
| `optional()` | Configuration | Makes the latest argument optional (must be the last argument) |
| `then(ArgumentBuilder)` | Configuration | Adds a subcommand / child node (after adding, `tabCompleter` no longer applies) |
| `context(CommandContext)` | Executor | Reuses a command from an existing Brigadier context |
| `executes(Command)` | Executor | Sets the Brigadier executor |
| `executes(CommandExecutor)` | Executor | Sets the Bukkit executor |
| `arguments(CommandArgumentsExecutor)` | Executor | Sets an executor that receives typed arguments |
| `placeholder(String key, Function)` | Configuration | Registers a custom message placeholder |
| `messageFormatter(BiFunction)` | Configuration | Replaces the default message formatting entirely |
| `tabCompleter(TabCompleter)` | Configuration | Sets tab completion (only applies when there are no child nodes) |
| `register()` | Action | Validates and queues the command (actual registration happens during the COMMANDS event) |

## Executors

### `executes(CommandExecutor)`

Bukkit-style executor; this fits most cases:

```java
CommandBuilder.create(this)
        .name("ping")
        .executes((sender, command, label, args) -> {
            sender.sendPlainMessage("pong");
            return true;
        })
        .register();
```

### `executes(Command<CommandSourceStack>)`

Low-level Brigadier executor:

```java
CommandBuilder.create(this)
        .name("raw")
        .executes(ctx -> {
            ctx.getSource().getSender().sendPlainMessage("raw");
            return 1;
        })
        .register();
```

### `context(CommandContext<CommandSourceStack>)`

Reuses a Brigadier command from an already-parsed context. This is for advanced use. You usually get a `CommandContext<CommandSourceStack>` from `CommandDispatcher.parse(...).getContext().build(...)`; in most cases, one of the two `executes` methods above is enough.

```java
// someContext comes from a parsed Brigadier context, for example
// dispatcher.parse("...", source).getContext().build("...")
CommandBuilder.create(this)
        .context(someContext)
        .register();
```

### Return Value Semantics

- The Bukkit-style executor returns a `boolean`: `true` maps to success (`Command.SINGLE_SUCCESS`, i.e. `1`), and `false` maps to failure (`0`)
- The Brigadier executor returns an `int` directly: `1` means success and `0` means failure

### Priority

If you set more than one executor, the effective order is: `context` > `executes(Command)` > `executes(CommandExecutor)`. Mixing them is not recommended.

## Typed Arguments

Declare arguments with `argument(...)` and handle them with `arguments(...)`:

```java
CommandBuilder.create(this)
        .name("give")
        .argument("player", StringArgumentType.word())
        .suggestOnlinePlayers()
        .argument("amount", IntegerArgumentType.integer(1))
        .arguments((sender, command, label, args) -> {
            String player = args.getString("player");
            int amount = args.getInt("amount");
            sender.sendPlainMessage("Given " + amount + " to " + player);
            return true;
        })
        .register();
```

- `suggestions(...)` adds static tab-completion values to the latest argument
- `suggestOnlinePlayers()` suggests online player names
- `optional()` makes the latest argument optional; `args.contains("name")` tells you whether it was provided
- `CommandArguments` provides `getString`, `getInt`, `getDouble`, `getBoolean`, and `contains`

## Message Formatting

Cooldown and permission messages support placeholders:

```java
CommandBuilder.create(this)
        .name("pay")
        .permission("myplugin.pay")
        .permissionMessage("{prefix} You need permission to use this command")
        .cooldown(Duration.ofSeconds(10))
        .cooldownMessage("{prefix} Please wait {cooldown} seconds")
        .placeholder("prefix", sender -> getConfig().getString("message-prefix", "[MyPlugin] "))
        .executes(...)
        .register();
```

- Built-in placeholders include `{player}` and `{cooldown}` (cooldown messages only)
- `.placeholder(key, resolver)` registers or overrides a placeholder; the resolver receives the command sender
- `.messageFormatter((template, sender) -> ...)` replaces the default formatting entirely

## Tab Completion

```java
.tabCompleter((sender, command, alias, args) -> List.of("a", "b"))
```

- `command` is a lightweight `Command` adapter, never `null`
- `alias` is the alias the user typed
- `args` is the current argument array
- After you add `.then(...)` child nodes, `.tabCompleter(...)` is ignored; use Brigadier's own `suggests` / argument types for child-node completion

## Command Restrictions

### `playerOnly()`

Only a `Player` can execute:

```java
CommandBuilder.create(this)
        .playerOnly()
        .executes((sender, command, label, args) -> {
            // sender is always a Player
            return true;
        })
        .register();
```

### `consoleOnly()`

Only the console can execute:

```java
CommandBuilder.create(this)
        .consoleOnly()
        .executes((sender, command, label, args) -> true)
        .register();
```

> Restrictions stack: when you set more than one, the sender must satisfy all of them. For example, `permission(...)` plus `playerOnly()` means "a player with the permission". Don't set `playerOnly()` and `consoleOnly()` together; that would reject every sender.

## Cooldown

```java
CommandBuilder.create(this)
        .cooldown(Duration.ofSeconds(60))
        .executes((sender, command, label, args) -> true)
        .register();
```

- Cooldowns only apply to `Player`
- The cooldown only applies to the root command execution path; subcommands added with `.then(...)` use their own executors and are not affected
- During the cooldown the command returns failure (equivalent to `false` for the Bukkit style), the executor does not run, and the player sees the cooldown message (default: `"Please wait before using this command again."`)

## Subcommands

Subcommands must keep the parent command's executor:

```java
CommandBuilder.create(this)
        .name("parent")
        .executes((sender, command, label, args) -> true)
        .then(Commands.literal("sub")
                .executes(ctx -> 1))
        .register();
```

## Validation Rules

When you call `register()`:

- The command name must not be `null` or blank
- An alias must not be `null`, blank, or the same as the command name
- One of `context`, `executes(Command)`, or `executes(CommandExecutor)` must be set
- A `context` must contain an executor
- A builder cannot be registered twice

Otherwise a [CommandException](/exception) is thrown.

## Known Limitations

- `usage` is metadata on the `Command` adapter for now (readable through the `command` parameter of the executor / completion callback); it doesn't take part in Paper's Brigadier registration
- `permissionMessage` is sent when a sender lacks the required permission, and is also exposed through the `Command` adapter
- When `tabCompleter` and `then` are both set and child nodes exist, the Bukkit completion is ignored
- The cooldown only wraps the root execution path; subcommands bypass the cooldown
- The cooldown message defaults to fixed English text and can be changed with `cooldownMessage(String)`
- `register()` can only be called before `LifecycleEvents.COMMANDS` (that is, during `onEnable()`); calling it later logs a warning and has no effect