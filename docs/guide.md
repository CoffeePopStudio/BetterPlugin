# Quick Start

## Project Overview

BetterPlugin is a plugin development framework for Paper servers. Its features are grouped by capability area:

| Module | Package | Description | Documentation |
| --- | --- | --- | --- |
| Plugin base | `org.coffeepop.betterPlugin.api.plugin` | Base class for your plugin (`PluginBase`) | [Plugin base](/plugin) |
| Command module | `org.coffeepop.betterPlugin.api.command` | Command registration and execution | [Command API](/command/) |
| Exceptions | `org.coffeepop.betterPlugin.api.exception` | Common framework exception types | [Exceptions](/exception) |

## Choosing an Entry Class

Plugins that use the framework have two options:

- Extend `JavaPlugin` directly. The command module only needs a `JavaPlugin` instance, so this path works fine.
- Extend `PluginBase`. It adds everyday helpers on top of `JavaPlugin`: typed config reading, a short logger alias, task scheduling with automatic cleanup on disable, `runWhenReady`, and a `command()` shortcut. See [Plugin entry](/plugin) for details.

Both can use `CommandBuilder`; see [Plugin base](/plugin) for details.

## Adding the Dependency

Third-party plugins use published tags from JitPack, so no local build is needed. Add the following to your project's `build.gradle.kts`:

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.CoffeePopStudio:BetterPlugin:{{plugin_version}}")
}
```

> The first time a version is requested, JitPack builds it in the cloud, which can take a moment. See [JitPack](https://jitpack.io/#CoffeePopStudio/BetterPlugin/{{plugin_version}}) for build status and the version list.

## Declaring the Runtime Dependency

Declare the dependency in `plugin.yml`, and place the BetterPlugin jar in the server's `plugins/` directory:

<<< @/snippets/plugin.yml

See [Integration](/guide/third-party) for the full setup.

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

You don't need to keep a `commands:` section in `plugin.yml`; the framework registers commands for you.

> Call `register()` during `onEnable()`, before Paper's `COMMANDS` event fires. If you call it from a command executor, a scheduled task, or after startup, the command won't be registered and a warning is logged.

## Further Reading

- [Modules](/modules)
- [Command API overview](/command/)
- [Command API quick start](/command/quick-start)
- [Plugin base](/plugin)
- [Exceptions](/exception)
- [Integration](/guide/third-party)
- [Configuration](/guide/configuration)
- [Building & contributing](/guide/building)
