# Plugin Base

Corresponding package: `org.coffeepop.betterPlugin.api.plugin`.

## PluginBase

`PluginBase` is BetterPlugin's plugin entry base class. It extends `JavaPlugin`:

```java
public abstract class PluginBase extends JavaPlugin {
}
```

Use it like this:

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

Right now, `PluginBase` only gives you a shared entry base. It adds no extra setup or service helpers.

## Relationship to JavaPlugin

- `CommandBuilder.create(...)` only needs a `JavaPlugin` instance, so plugins that extend `JavaPlugin` directly can also use the command module
- Extending `PluginBase` is optional; use it when you want a shared plugin entry base
- The two paths work together fine, and command registration behaves the same either way

## Module Boundaries

- Plugin entry: `org.coffeepop.betterPlugin.api.plugin`
- Command registration: `org.coffeepop.betterPlugin.api.command`
- Common exceptions: `org.coffeepop.betterPlugin.api.exception`
- Internal implementation: the `internal` package; compatibility is not guaranteed, so don't use it from external plugins

## Related Pages

- [Quick start](/guide)
- [Third-party plugin integration](/guide/third-party)
- [Command API overview](/command/)
