# Third-Party Plugin Integration

BetterPlugin can be reused by other plugins as a runtime dependency. Commands registered via `create(plugin)` belong to the calling plugin, not BetterPlugin.

> The command API is currently an experimental API; the interfaces may change between versions.

## 1. Add the Dependency

Third-party plugins use JitPack to consume published tags directly; no local build or publishing is required. The first time a version is requested, JitPack builds it in the cloud, which may take a moment.

Add the following to the consuming project's `build.gradle.kts`:

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.CoffeePopStudio:BetterPlugin:{{plugin_version}}")
}
```

See [JitPack](https://jitpack.io/#CoffeePopStudio/BetterPlugin/{{plugin_version}}) for build status and the list of versions.

## 2. Add the Runtime Dependency

Consumer plugins must declare the BetterPlugin dependency in `plugin.yml`:

<<< @/snippets/plugin.yml

Also place the BetterPlugin jar in the server's `plugins/` directory, for example by downloading it from JitPack:

```text
https://jitpack.io/com/github/CoffeePopStudio/BetterPlugin/{{plugin_version}}/BetterPlugin-{{plugin_version}}.jar
```

> The `depend` entry in `plugin.yml` only controls runtime load order; compilation still requires the dependency from step 1.

## 3. Register Commands

```java
import org.bukkit.plugin.java.JavaPlugin;
import org.coffeepop.betterPlugin.api.command.CommandBuilder;

public class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        CommandBuilder.create(this)
                .name("hello")
                .executes((sender, command, label, args) -> {
                    sender.sendPlainMessage("Hello from MyPlugin");
                    return true;
                })
                .register();
    }
}
```

The main class can also extend `PluginBase`; both approaches work, see [Plugin Basics](/plugin).

## 4. Specify the Plugin

The two forms are equivalent:

```java
CommandBuilder.create(this)
        .name("hello")
        .executes((sender, command, label, args) -> true)
        .register();
```

Or:

```java
CommandBuilder.create()
        .plugin(this)
        .name("hello")
        .executes((sender, command, label, args) -> true)
        .register();
```

> Note: if `.plugin()` is not specified, the command is registered under BetterPlugin. Third-party plugins must use `create(this)` or `.plugin(this)`.

## 5. Permission, Aliases, Completion

```java
import java.util.List;
import org.coffeepop.betterPlugin.api.command.CommandBuilder;

CommandBuilder.create(this)
        .name("give")
        .permission("myplugin.give")
        .aliases("i")
        .executes((sender, command, label, args) -> {
            // ...
            return true;
        })
        .tabCompleter((sender, command, label, args) -> {
            if (args.length == 1) {
                return List.of("diamond", "iron", "gold");
            }
            return List.of();
        })
        .register();
```

For more capabilities and limitations, see [Command API Reference](/command/api).
