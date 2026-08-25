# Plugin Base

Corresponding API package: `org.coffeepop.betterPlugin.api.plugin`.

## PluginBase

`PluginBase` is the plugin entry base class provided by BetterPlugin, extending `JavaPlugin`:

```java
public abstract class PluginBase extends JavaPlugin {
}
```

Usage:

```java
import org.coffeepop.betterPlugin.api.plugin.PluginBase;

public final class MyPlugin extends PluginBase {

    @Override
    public void onEnable() {
        // plugin enable logic
    }

    @Override
    public void onDisable() {
        // plugin disable logic
    }
}
```

Currently, `PluginBase` only provides a unified entry inheritance relationship and includes no additional lifecycle or service access capabilities.

## Relationship to JavaPlugin

- `CommandBuilder.create(...)` only requires a `JavaPlugin` instance, so plugins that extend `JavaPlugin` directly can also use the command API
- Extending `PluginBase` is optional and suits plugins that want to keep a unified plugin entry inheritance relationship
- The two paths do not conflict with each other, and command registration behavior is identical

## Module Boundaries

- Plugin entry and lifecycle: `org.coffeepop.betterPlugin.api.plugin`
- Command registration: `org.coffeepop.betterPlugin.api.command`
- Common exceptions: `org.coffeepop.betterPlugin.api.exception`
- Internal implementation: the `internal` package; no compatibility is guaranteed, and it should not be used in external plugins

## Related Pages

- [Quick start](/guide)
- [Third-party plugin integration](/guide/third-party)
- [Command API overview](/command/)
