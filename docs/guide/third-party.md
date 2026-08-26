# Third-Party Plugin Integration

BetterPlugin can be used by other plugins as a runtime dependency. Commands registered with `create(plugin)` belong to the calling plugin, not BetterPlugin.

> The command module is experimental right now, so the interfaces may change between versions.

## 1. Add the Dependency

Third-party plugins use JitPack to consume published tags directly; no local build or publishing is needed. The first time a version is requested, JitPack builds it in the cloud, which can take a moment.

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

> The `depend` entry in `plugin.yml` only controls runtime load order; you still need the compile-time dependency from step 1.

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

The main class can also extend `PluginBase`; both approaches work. See [Plugin Basics](/plugin).

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

> Note: if you don't set `.plugin()`, the command is registered under BetterPlugin. Third-party plugins must use `create(this)` or `.plugin(this)`.

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