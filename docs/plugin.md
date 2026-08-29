# Plugin entry

Corresponding package: `org.coffeepop.betterPlugin.api.plugin`.

## PluginBase

`PluginBase` extends `JavaPlugin` and adds a few convenience helpers on top. Instead of overriding `onEnable` / `onDisable`, you override `onPluginEnable` / `onPluginDisable`:

```java
import org.coffeepop.betterPlugin.api.plugin.PluginBase;

import java.util.List;

public final class MyPlugin extends PluginBase {

    @Override
    protected void onPluginEnable() {
        saveDefaultConfig();

        int interval = configInt("auto-save.interval-seconds", 300);
        boolean announce = configBoolean("join-announcement.enabled", true);
        List<String> greetings = configStringList("greetings");

        log().info("MyPlugin is ready.");

        runSyncTimer(this::saveData, 0L, interval * 20L);
        runAsync(this::fetchRemoteNews);

        runWhenReady(() -> log().info("Server finished starting."));

        command()
                .name("greet")
                .playerOnly()
                .executes((sender, command, label, args) -> {
                    sender.sendPlainMessage(greetings.isEmpty() ? "Hi!" : greetings.get(0));
                    return true;
                })
                .register();
    }

    @Override
    protected void onPluginDisable() {
        log().info("MyPlugin stopped.");
    }

    private void saveData() {
        // save your data
    }

    private void fetchRemoteNews() {
        // fetch remote data
    }
}
```

## Built-in helpers

| Helper | What it does |
| --- | --- |
| `configString` / `configInt` / `configLong` / `configDouble` / `configBoolean` / `configStringList` | Reads typed values from `config.yml`, with defaults when a path is missing |
| `log()` | Short alias for the plugin logger |
| `saveDefaultResource(path)` | Copies a file from the plugin jar into the plugin data folder only if it doesn't already exist |
| `runSync` / `runAsync` / `runSyncLater` / `runSyncTimer` / `runAsyncTimer` | Schedules tasks; every task created this way is cancelled automatically on disable |
| `isServerReady` / `runWhenReady` | Runs code once the server has finished starting |
| `reloadPluginConfig` / `onConfigReload` | Reloads `config.yml` and runs a callback for rebuilding settings |
| `command()` | Shortcut for `CommandBuilder.create(this)` |
| `registry()` | Shared thread-safe registry for services and handlers |

## Relationship to JavaPlugin

- `CommandBuilder.create(...)` only needs a `JavaPlugin` instance, so plugins that extend `JavaPlugin` directly can also use the command module
- Extending `PluginBase` is optional; use it when you want the built-in helpers
- The two paths work together fine, and command registration behaves the same either way

## Module Boundaries

- Plugin entry: `org.coffeepop.betterPlugin.api.plugin`
- Command registration: `org.coffeepop.betterPlugin.api.command`
- Common exceptions: `org.coffeepop.betterPlugin.api.exception`
- Internal implementation: the `internal` package; compatibility is not guaranteed, so don't use it from external plugins

## Related Pages

- [Quick start](/guide)
- [Third-party plugin integration](/guide/third-party)
- [Commands overview](/command/)
