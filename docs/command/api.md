# Command API Reference

The command module lives in the `org.coffeepop.betterPlugin.api.command` package, and `CommandBuilder` is its entry point.

::: warning Experimental API
`CommandBuilder` and `CommandException` are currently annotated `@ApiStatus.Experimental`; the interfaces may change between versions.
:::

## Required Imports for the Examples

```java
import org.bukkit.plugin.java.JavaPlugin;
import org.coffeepop.betterPlugin.api.command.CommandBuilder;
import io.papermc.paper.command.brigadier.Commands; // Required for the subcommand example
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
| `permissionMessage(String message)` | Configuration | Sets permission-message metadata (same as above; does not handle permission-denied messages) |
| `playerOnly()` | Configuration | Only players can execute |
| `consoleOnly()` | Configuration | Only the console can execute |
| `cooldown(Duration duration)` | Configuration | Sets the root command's player cooldown |
| `then(ArgumentBuilder)` | Configuration | Adds a subcommand / child node (after adding, `tabCompleter` no longer applies) |
| `context(CommandContext)` | Executor | Reuses a command from an existing Brigadier context |
| `executes(Command)` | Executor | Sets the Brigadier executor |
| `executes(CommandExecutor)` | Executor | Sets the Bukkit executor |
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
- During the cooldown the command returns failure (equivalent to `false` for the Bukkit style), the executor does not run, and the player sees the fixed English message `"Please wait before using this command again."` (not configurable for now)

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

- `usage` and `permissionMessage` are only metadata on the `Command` adapter for now (readable through the `command` parameter of the executor / completion callback); they don't take part in Paper's Brigadier registration and are not shown when permission is missing
- When `tabCompleter` and `then` are both set and child nodes exist, the Bukkit completion is ignored
- The cooldown only wraps the root execution path; subcommands bypass the cooldown
- The cooldown message is fixed English text and is not configurable
- `register()` can only be called before `LifecycleEvents.COMMANDS` (that is, during `onEnable()`); calling it later logs a warning and has no effect
- The whole command module is experimental; interfaces may change