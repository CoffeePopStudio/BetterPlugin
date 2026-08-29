# Modules

BetterPlugin ships a few small API modules that you can use with any Paper plugin, whether or not you extend `PluginBase`.

## Scheduler

```java
TaskScheduler scheduler = new TaskScheduler(this);

scheduler.runSyncTimer(() -> saveData(), 0L, 20L * 60);
scheduler.runAsync(() -> fetchRemoteData());

// On plugin disable:
scheduler.cancelAll();
```

## Config

```java
PluginConfig config = new PluginConfig(this, this::onReload);

int interval = config.getInt("auto-save.interval-seconds", 300);
boolean announce = config.getBoolean("join-announcement.enabled", true);
List<String> greetings = config.getStringList("greetings");

// Reload with the callback:
config.reload();
```

## Placeholders

```java
String text = PlaceholderFormatter.format(
        "Hello {player}, you have {amount} items.",
        Map.of("player", "alice", "amount", "3")
);
```

## Events

```java
ListenerRegistry listeners = new ListenerRegistry(this);

listeners.register(PlayerJoinEvent.class, event ->
        event.getPlayer().sendPlainMessage("Welcome!"));

// On plugin disable:
listeners.unregisterAll();
```

## Items

```java
ItemStack item = ItemBuilder.of(Material.DIAMOND)
        .amount(2)
        .name("Sword")
        .lore("First line", "Second line")
        .build();
```

## GUI

```java
InventoryGui gui = InventoryGui.builder(this, 9, "Menu")
        .item(0, new ItemStack(Material.DIAMOND), event ->
                event.getWhoClicked().sendPlainMessage("Clicked!"))
        .build();

gui.open(player);

// The internal listener is removed automatically when the last viewer closes.
```

## Related Pages

- [Plugin entry](/plugin)
- [Commands overview](/command/)
