# 模块

BetterPlugin 自带几个小型 API 模块，任何 Paper 插件都可以直接用，不一定要继承 `PluginBase`。

## 调度

```java
TaskScheduler scheduler = new TaskScheduler(this);

scheduler.runSyncTimer(() -> saveData(), 0L, 20L * 60);
scheduler.runAsync(() -> fetchRemoteData());

// 插件卸载时：
scheduler.cancelAll();
```

## 配置

```java
PluginConfig config = new PluginConfig(this, this::onReload);

int interval = config.getInt("auto-save.interval-seconds", 300);
boolean announce = config.getBoolean("join-announcement.enabled", true);
List<String> greetings = config.getStringList("greetings");

// 重新加载并触发回调：
config.reload();
```

## 占位符

```java
String text = PlaceholderFormatter.format(
        "你好 {player}，你有 {amount} 个物品。",
        Map.of("player", "alice", "amount", "3")
);
```

## 事件

```java
ListenerRegistry listeners = new ListenerRegistry(this);

listeners.register(PlayerJoinEvent.class, event ->
        event.getPlayer().sendPlainMessage("欢迎！"));

// 插件卸载时：
listeners.unregisterAll();
```

## 物品

```java
ItemStack item = ItemBuilder.of(Material.DIAMOND)
        .amount(2)
        .name("剑")
        .lore("第一行", "第二行")
        .build();
```

## GUI

```java
InventoryGui gui = InventoryGui.builder(this, 9, "菜单")
        .item(0, new ItemStack(Material.DIAMOND), event ->
                event.getWhoClicked().sendPlainMessage("点击了！"))
        .build();

gui.open(player);

// 最后一个玩家关闭 GUI 后，内部监听器会自动移除。
```

## 相关页面

- [插件入口](/zh/plugin)
- [命令概览](/zh/command/)
