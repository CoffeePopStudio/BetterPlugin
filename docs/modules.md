# Modules

BetterPlugin ships a few small API modules that you can use with any Paper plugin, whether or not you extend `PluginBase`.

All modules are plain Java classes; you can create them anywhere in your plugin. If you extend `PluginBase`, some helpers are already integrated (see [Plugin entry](/plugin)).

---

## Scheduler

`TaskScheduler` schedules tasks for one plugin and lets you cancel them all at once.

```java
import org.coffeepop.betterPlugin.api.scheduler.TaskScheduler;

public final class MyPlugin extends JavaPlugin {

    private TaskScheduler scheduler;

    @Override
    public void onEnable() {
        scheduler = new TaskScheduler(this);

        // Run once on the main thread.
        scheduler.runSync(() -> getLogger().info("started"));

        // Run on a background thread.
        scheduler.runAsync(() -> fetchRemoteData());

        // Run later on the main thread.
        scheduler.runSyncLater(() -> getLogger().info("one second later"), 20L);

        // Repeat every 5 minutes on the main thread.
        scheduler.runSyncTimer(() -> saveData(), 0L, 20L * 60 * 5);

        // Repeat on a background thread.
        scheduler.runAsyncTimer(() -> fetchRemoteData(), 0L, 20L * 60);
    }

    @Override
    public void onDisable() {
        // Stops every task owned by this scheduler.
        scheduler.cancelAll();
    }
}
```

### Practical notes

- **If you extend `PluginBase`, use `tasks()` instead of creating a `TaskScheduler` yourself.** It is cancelled automatically on plugin disable.
- **Always call `cancelAll()` on plugin disable** when you create a standalone scheduler. If you don't, repeating tasks keep running after your plugin is disabled.
- **`cancelAll()` closes the scheduler.** After calling it, any task created through the same scheduler is cancelled immediately. Create a new `TaskScheduler` if you need to schedule again after a reload.
- **Background tasks must not touch Bukkit API that requires the main thread.** For example, opening inventories, modifying worlds, or sending packets usually must happen on the main thread. Use `runSync`/`runSyncTimer` for those.
- **Task callbacks run on whatever thread the scheduler picks.** Keep data shared between threads in thread-safe containers or synchronize explicitly.

---

## Config

`PluginConfig` is a typed wrapper around `config.yml`.

```java
import org.coffeepop.betterPlugin.api.config.PluginConfig;
import java.util.List;

public final class MyPlugin extends JavaPlugin {

    private PluginConfig config;

    @Override
    public void onEnable() {
        config = new PluginConfig(this, this::reloadSettings);

        // Read values with defaults.
        int interval = config.getInt("auto-save.interval-seconds", 300);
        boolean announce = config.getBoolean("join-announcement.enabled", true);
        String prefix = config.getString("message-prefix", "[MyPlugin] ");
        List<String> greetings = config.getStringList("greetings");

        // Reload manually when a server operator asks for it.
        config.reload();
    }

    private void reloadSettings() {
        // Called after every reload; rebuild timers or cached values here.
    }
}
```

### Practical notes

- **`reload()` applies missing defaults first, then reloads from disk.** It never overwrites values the server operator has changed.
- **After `reload()`, read values again through `PluginConfig`.** Do not keep long-lived copies of primitive settings if you support reload.
- **The reload callback runs on the thread that called `reload()`.** If you rebuild scheduler tasks inside it, make sure you are on the main thread (or delegate with `TaskScheduler.runSync`).
- **If your plugin has no `config.yml`, `reload()` is safe.** It simply reloads the (empty) config and runs the callback.

---

## Placeholders

`PlaceholderFormatter` replaces `{key}` tokens in a template.

```java
import org.coffeepop.betterPlugin.api.utils.PlaceholderFormatter;
import java.util.Map;

String message = PlaceholderFormatter.format(
        "Hello {player}, you have {amount} items.",
        Map.of(
                "player", player.getName(),
                "amount", String.valueOf(amount)
        )
);
```

### Practical notes

- **Replacement is single-pass.** If a replacement value contains `{another}`, that text is kept literally and not replaced again.
- **Unknown placeholders are left as-is.** That is usually better for debugging than silently removing them.
- **Build a `Map` for each message.** It is cheap and keeps the code readable; do not mutate the map while formatting.

---

## Events

`ListenerRegistry` registers event handlers without writing annotated `Listener` classes.

```java
import org.coffeepop.betterPlugin.api.event.ListenerRegistry;
import org.bukkit.event.player.PlayerJoinEvent;

public final class MyPlugin extends JavaPlugin {

    private ListenerRegistry listeners;

    @Override
    public void onEnable() {
        listeners = new ListenerRegistry(this);

        listeners.register(PlayerJoinEvent.class, event ->
                event.getPlayer().sendPlainMessage("Welcome!"));

        // You can register as many handlers as you want.
        listeners.register(PlayerQuitEvent.class, event ->
                getLogger().info(event.getPlayer().getName() + " left"));
    }

    @Override
    public void onDisable() {
        listeners.unregisterAll();
    }
}
```

### Practical notes

- **If you extend `PluginBase`, use `listeners()` instead of creating a `ListenerRegistry` yourself.** It is unregistered automatically on plugin disable.
- **Call `unregisterAll()` on plugin disable** when you use a standalone registry. Otherwise handlers keep firing for a disabled plugin.
- **Handlers run on the thread that fired the event.** Most Bukkit events fire on the main thread, but async events (for example, async chat/player login events) do not. Check the event's documentation before touching shared state.
- **Do not run long work inside an event handler.** If you need to save data or call a web service, schedule it with `TaskScheduler.runAsync` and return quickly.
- **`ListenerRegistry` is reusable.** After `unregisterAll()` you can register new handlers again; it creates a fresh internal listener.

---

## Items

`ItemBuilder` creates `ItemStack`s with display names and lore.

```java
import org.coffeepop.betterPlugin.api.item.ItemBuilder;
import org.bukkit.Material;

ItemStack sword = ItemBuilder.of(Material.DIAMOND_SWORD)
        .amount(1)
        .name("Hero Sword")
        .lore("Legendary", "Right-click to activate")
        .build();

// You can also use Adventure components directly.
ItemStack fancy = ItemBuilder.of(Material.PAPER)
        .name(Component.text("Notice").color(NamedTextColor.GOLD))
        .lore(List.of(Component.text("Line 1"), Component.text("Line 2")))
        .build();
```

### Practical notes

- **Build items once and reuse them when possible.** Creating item meta repeatedly is wasteful; cache static items in fields.
- **`amount(int)` must be positive.** The builder rejects zero and negative amounts.
- **Lore lines are appended.** Calling `lore(...)` twice adds both sets of lines; call it once with all lines if you want a single list.
- **The builder does not apply enchantments, flags, or damage yet.** Use `item.getItemMeta()` directly after `build()` for advanced meta.

---

## GUI

`InventoryGui` is a minimal inventory menu helper.

```java
import org.coffeepop.betterPlugin.api.gui.InventoryGui;
import org.coffeepop.betterPlugin.api.item.ItemBuilder;
import org.bukkit.Material;

InventoryGui gui = InventoryGui.builder(this, 9, "Menu")
        .item(0, ItemBuilder.of(Material.DIAMOND).name("Click me").build(), event ->
                event.getWhoClicked().sendPlainMessage("You clicked diamond!"))
        .item(4, ItemBuilder.of(Material.BARRIER).name("Close").build(), event ->
                event.getWhoClicked().closeInventory())
        .build();

// Open for a player.
gui.open(player);

// Later, update an item while the GUI is open.
gui.setItem(0, ItemBuilder.of(Material.EMERALD).name("Changed").build());

// Close callback example:
InventoryGui withClose = InventoryGui.builder(this, 9, "Menu")
        .onClose(p -> p.sendPlainMessage("Menu closed"))
        .build();
```

### Practical notes

- **All clicks in the GUI view are cancelled**, including clicks in the player's bottom inventory while the GUI is open. This keeps the menu read-only.
- **The internal click listener is registered lazily on first `open()`.** It is automatically unregistered when the **last viewer closes** the GUI. You do not normally need to call `close()`.
- **`onClose(Consumer<Player>)` adds a callback that runs each time a player closes the GUI.**
- **`setItem(int, ItemStack)` updates a slot while the GUI is open.**
- **Calling `close()` manually is safe.** It unregisters listeners and clears viewers; the GUI can be reopened later and the listener is re-registered.
- **Click handlers run on the main thread** (inventory events are main-thread events). Do not block there.
- **The handler receives the event after it is already cancelled.** You can still read slot, click type, and player from it.
- **Inventory size must be a positive multiple of 9.** Slots outside the size are rejected at build time.

---

## Registry

`Registry<T>` is a named, thread-safe map. `SimpleRegistry<T>` is the default implementation.

```java
import org.coffeepop.betterPlugin.api.registry.Registry;
import org.coffeepop.betterPlugin.api.registry.SimpleRegistry;

// Standalone usage:
Registry<Storage> storages = new SimpleRegistry<>();
storages.register("main", new FileStorage());
storages.get("main").ifPresent(Storage::load);
```

If you extend `PluginBase`, a shared registry is already available:

```java
registry().register("storage", new FileStorage());
registry().get("storage").ifPresent(Storage::load);
```

### Modules

`ModuleRegistry` manages named modules with `enable`/`disable` lifecycle.

```java
import org.coffeepop.betterPlugin.api.registry.ModuleRegistry;
import org.coffeepop.betterPlugin.api.registry.PluginModule;

public final class MyPlugin extends JavaPlugin {

    private ModuleRegistry modules;

    @Override
    public void onEnable() {
        modules = new ModuleRegistry(this);

        modules.register("backup", new PluginModule() {
            @Override
            public void onEnable(JavaPlugin plugin) {
                // start backup task
            }

            @Override
            public void onDisable() {
                // stop backup task
            }
        });

        modules.enableAll();
    }

    @Override
    public void onDisable() {
        modules.disableAll();
    }
}
```

### Practical notes

- **If you extend `PluginBase`, use `modules()` instead of creating a `ModuleRegistry` yourself.** It is disabled automatically on plugin disable.
- **Registry operations are thread-safe.** You can register and read from async tasks.
- **`register(...)` replaces an existing value with the same key.** Use unique keys to avoid accidental overwrites.
- **`enable(...)` is idempotent.** Enabling an already-enabled module does not call `onEnable` again.
- **`disable(...)` is also idempotent.** Disabling a module that is not enabled does nothing.
- **`unregister(...)` disables the module first if it is enabled**, then removes it.
- **Call `disableAll()` on plugin disable** so every module can clean up its own tasks/listeners.

---

## Common pitfalls checklist

- [ ] Cancel schedulers on disable (`cancelAll()`).
- [ ] Unregister event listeners on disable (`unregisterAll()`).
- [ ] Disable all modules on disable (`disableAll()`).
- [ ] Use main-thread tasks for Bukkit API that must run on the main thread.
- [ ] Re-read config values after `reload()`.
- [ ] Don't keep GUI references after the last viewer closes unless you reopen it.
- [ ] Give registry keys unique, stable names.
- [ ] Build static items once and reuse them.

## Related Pages

- [Plugin entry](/plugin)
- [Commands overview](/command/)
