# Commands Overview

Corresponding package: `org.coffeepop.betterPlugin.api.command`.

The command module is built on Paper's command system and offers chainable command registration.

## Capabilities

- Chainable setup of command name, permission, and aliases
- Command description, usage, and no-permission message metadata
- Bukkit-style executors and tab completion
- Player / console restrictions
- Cooldowns
- Subcommands and custom child nodes
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

> `register()` only adds the command to the pending registration queue; the actual registration happens when `LifecycleEvents.COMMANDS` fires.
> So call it during your plugin's `onEnable()`. Calling it from a command executor, a scheduled task, or after the server has finished starting will silently have no effect.

## Quick Navigation

- [Quick Start](/command/quick-start)
- [API Reference](/command/api)
- [Examples](/command/examples)