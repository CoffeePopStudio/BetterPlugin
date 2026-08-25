# Quick Start

## Project Overview

BetterPlugin is a plugin development framework for Paper servers, providing API modules by capability area:

| Module | Package | Description | Documentation |
| --- | --- | --- | --- |
| Plugin base | `org.coffeepop.betterPlugin.api.plugin` | Plugin entry base class (`PluginBase`) | [Plugin base](/plugin) |
| Command API | `org.coffeepop.betterPlugin.api.command` | Brigadier command registration and execution | [Command API](/command/) |
| Exceptions | `org.coffeepop.betterPlugin.api.exception` | Common framework exception types | [Exceptions](/exception) |

## Choosing an Entry Class

Plugins using the framework have two equivalent paths:

- Extend `JavaPlugin` directly. The command API only requires a `JavaPlugin` instance, so this path is fully supported.
- Extend `PluginBase`. `PluginBase` currently only provides a unified entry inheritance relationship and adds no extra capabilities; choose this path when you need a unified plugin entry inheritance relationship.

Both can use `CommandBuilder` normally; see [Plugin base](/plugin) for details.

## Adding the Dependency

Third-party plugins use published tags through JitPack, with no local build required. Add the following to the consumer project's `build.gradle.kts`:

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.CoffeePopStudio:BetterPlugin:{{plugin_version}}")
}
```

> When a version is requested for the first time, JitPack builds it in the cloud and it may take a moment; see [JitPack](https://jitpack.io/#CoffeePopStudio/BetterPlugin/{{plugin_version}}) for build status and the version list.

## Declaring the Runtime Dependency

Declare the dependency in `plugin.yml`, and place the BetterPlugin jar in the server's `plugins/` directory:

<<< @/snippets/plugin.yml

See [Integration](/guide/third-party) for full setup instructions.

## Registering Your First Command

```java
import org.bukkit.plugin.java.JavaPlugin;
import org.coffeepop.betterPlugin.api.command.CommandBuilder;

public class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        CommandBuilder.create(this)
                .name("ping")
                .executes((sender, command, label, args) -> {
                    sender.sendPlainMessage("pong");
                    return true;
                })
                .register();
    }
}
```

There is no need to maintain a `commands:` section in `plugin.yml`; commands are registered uniformly by the framework.

> The command API (`CommandBuilder` / `CommandException`) is currently experimental, and its interfaces may change between versions.
> `register()` must be called during the `onEnable()` phase (before Paper's `COMMANDS` lifecycle event); if it is called from a command executor, a scheduled task, or after server startup has completed, the command will not take effect and no error will be reported.

## Further Reading

- [Command API overview](/command/)
- [Command API quick start](/command/quick-start)
- [Plugin base](/plugin)
- [Exceptions](/exception)
- [Integration](/guide/third-party)
- [Configuration](/guide/configuration)
- [Building & contributing](/guide/building)
