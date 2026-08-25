# Command API Overview

Corresponding API package: `org.coffeepop.betterPlugin.api.command`.

The command module is built on Paper's Brigadier command system and provides fluent-style command registration.

::: warning Experimental API
`CommandBuilder` and `CommandException` are currently annotated `@ApiStatus.Experimental`; the interfaces may change between versions.
:::

## Capabilities

- Chainable declaration of command name, permission, and aliases
- Command description, usage, and no-permission message metadata
- Bukkit-style executors and tab completion
- Player / console restrictions
- Cooldowns
- Subcommands and custom Brigadier child nodes
- Assigning commands to a specific plugin

## Workflow

```text
CommandBuilder
    │  Collects configuration and executor
    ▼
Validates and adds to the pending registration queue
    ▼
LifecycleEvents.COMMANDS
    │  Paper lifecycle event
    ▼
Paper command system
```

> `register()` only adds the command to the pending registration queue; the actual registration happens during `LifecycleEvents.COMMANDS`.
> Therefore call it during your plugin's `onEnable()` phase; calling it from a command executor, a scheduled task, or after the server has finished starting will silently have no effect.

## Quick Navigation

- [Quick Start](/command/quick-start)
- [API Reference](/command/api)
- [Examples](/command/examples)
