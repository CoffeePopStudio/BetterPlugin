# Command API Reference

The command API is located in the `org.coffeepop.betterPlugin.api.command` package, and its entry point is `CommandBuilder`.

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

Creates a command Builder; the command belongs to BetterPlugin by default.

```java
CommandBuilder builder = CommandBuilder.create();
```

Third-party plugins should use `create(this)` or `.plugin(this)` instead to avoid registering commands under BetterPlugin.

### `create(JavaPlugin plugin)`

Creates a command Builder; the command belongs to the specified plugin.

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
| `usage(String usage)` | Configuration | Sets usage metadata (only readable via the callback parameter `command`; does not affect Brigadier registration) |
| `permissionMessage(String message)` | Configuration | Sets permission-message metadata (same as above; does not handle permission-denied messages) |
| `playerOnly()` | Configuration | Only players can execute |
| `consoleOnly()` | Configuration | Only the console can execute |
| `cooldown(Duration duration)` | Configuration | Sets the root command's player cooldown |
| `then(ArgumentBuilder)` | Configuration | Adds a subcommand / child node (after adding, `tabCompleter` no longer applies) |
| `context(CommandContext)` | Executor | Reuses a Command from an existing Brigadier Context |
| `executes(Command)` | Executor | Sets the Brigadier executor |
| `executes(CommandExecutor)` | Executor | Sets the Bukkit executor |
| `tabCompleter(TabCompleter)` | Configuration | Sets tab completion (only applies when there are no child nodes) |
| `register()` | Action | Validates and queues the command (actual registration happens during the COMMANDS event) |

## Executors

### `executes(CommandExecutor)`

Bukkit-style executor, suitable for most cases:

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

Reuses a Brigadier Command from an already-parsed context. This is an advanced usage. An available `CommandContext<CommandSourceStack>` typically comes from `CommandDispatcher.parse(...).getContext().build(...)`; in most cases you can simply use one of the two `executes` methods above.

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

If multiple executors are set at the same time, the effective order is: `context` > `executes(Command)` > `executes(CommandExecutor)`. Mixing them is not recommended.

## Tab Completion

```java
.tabCompleter((sender, command, alias, args) -> List.of("a", "b"))
```

- `command` is a lightweight `Command` adapter, never `null`
- `alias` is the command alias the user typed
- `args` is the current argument array
- After adding `.then(...)` child nodes, `.tabCompleter(...)` is ignored; for child-node completion, use Brigadier's own `suggests` / argument types

## Command Restrictions

### `playerOnly()`

Only `Player` can execute:

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

> Restrictions stack: when multiple conditions are set at the same time, the sender must satisfy all of them. For example, `permission(...)` plus `playerOnly()` means "a player with the permission". Do not set `playerOnly()` and `consoleOnly()` together, otherwise all senders will be rejected.

## Cooldown

```java
CommandBuilder.create(this)
        .cooldown(Duration.ofSeconds(60))
        .executes((sender, command, label, args) -> true)
        .register();
```

- Cooldowns only apply to `Player`
- The cooldown only applies to the root command execution path; subcommands added with `.then(...)` use their own executors and are not affected by this cooldown
- During the cooldown the command returns failure (equivalent to `false` for the Bukkit style), the executor does not run, and the player receives the fixed English message `"Please wait before using this command again."` (not configurable for now)

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

When calling `register()`:

- The command name must not be `null` or an empty string
- One of `context`, `executes(Command)`, or `executes(CommandExecutor)` must be set

Otherwise a [CommandException](/exception) is thrown.

## Known Limitations

- `usage` and `permissionMessage` are currently only metadata attached to the `Command` adapter (readable through the `command` parameter of the executor / completion callback); they do not participate in Paper's Brigadier registration and are not shown when permission is missing
- When `tabCompleter` and `then` are set at the same time: if child nodes exist, the Bukkit completion is ignored
- The cooldown only wraps the root execution path; subcommands bypass the cooldown
- The cooldown message is fixed English text and is not configurable
- `register()` can only be called before `LifecycleEvents.COMMANDS` (i.e. during the `onEnable()` phase); calling it later has no effect and does not error
- The entire command API is experimental; interfaces may change
