# 模块

BetterPlugin 自带几个小型 API 模块，任何 Paper 插件都可以直接用，不一定要继承 `PluginBase`。

所有模块都是普通 Java 类，你可以在插件任意位置创建。如果继承 `PluginBase`，部分能力已经集成（见 [插件入口](/zh/plugin)）。

---

## 调度

`TaskScheduler` 为一个插件调度任务，并允许一次性取消全部任务。

```java
import org.coffeepop.betterPlugin.api.scheduler.TaskScheduler;

public final class MyPlugin extends JavaPlugin {

    private TaskScheduler scheduler;

    @Override
    public void onEnable() {
        scheduler = new TaskScheduler(this);

        // 在主线程立即执行一次。
        scheduler.runSync(() -> getLogger().info("started"));

        // 在后台线程执行。
        scheduler.runAsync(() -> fetchRemoteData());

        // 在主线程延迟执行。
        scheduler.runSyncLater(() -> getLogger().info("一秒后"), 20L);

        // 每 5 分钟在主线程重复执行。
        scheduler.runSyncTimer(() -> saveData(), 0L, 20L * 60 * 5);

        // 在后台线程重复执行。
        scheduler.runAsyncTimer(() -> fetchRemoteData(), 0L, 20L * 60);
    }

    @Override
    public void onDisable() {
        // 停止这个 scheduler 拥有的所有任务。
        scheduler.cancelAll();
    }
}
```

### 实际开发注意

- **插件卸载时一定要调用 `cancelAll()`。** 否则重复任务会在插件禁用后继续运行。
- **`cancelAll()` 之后 scheduler 就关闭了。** 之后再通过同一个 scheduler 创建的任务会立即被取消；需要重新调度时请 new 一个新的 `TaskScheduler`。
- **后台任务不能碰必须主线程执行的 Bukkit API。** 例如打开背包、修改世界、发送包等通常必须在主线程。这类操作请用 `runSync` / `runSyncTimer`。
- **任务回调在调度器选择的线程上运行。** 多线程共享数据请使用线程安全容器或自行加锁。

---

## 配置

`PluginConfig` 是对 `config.yml` 的类型化封装。

```java
import org.coffeepop.betterPlugin.api.config.PluginConfig;
import java.util.List;

public final class MyPlugin extends JavaPlugin {

    private PluginConfig config;

    @Override
    public void onEnable() {
        config = new PluginConfig(this, this::reloadSettings);

        // 带默认值读取。
        int interval = config.getInt("auto-save.interval-seconds", 300);
        boolean announce = config.getBoolean("join-announcement.enabled", true);
        String prefix = config.getString("message-prefix", "[MyPlugin] ");
        List<String> greetings = config.getStringList("greetings");

        // 服主请求重载时手动调用。
        config.reload();
    }

    private void reloadSettings() {
        // 每次重载后调用；在这里重建定时任务或缓存值。
    }
}
```

### 实际开发注意

- **`reload()` 会先补默认值，再从磁盘重载。** 不会覆盖服主已经改过的内容。
- **重载后要重新通过 `PluginConfig` 读取。** 如果支持热重载，不要长期缓存旧的基础配置值。
- **重载回调运行在调用 `reload()` 的线程上。** 如果要在回调里重建调度任务，请确认在主线程，或用 `TaskScheduler.runSync` 转交。
- **没有 `config.yml` 的插件也能安全调用 `reload()`。** 它只是重载空配置并触发回调。

---

## 占位符

`PlaceholderFormatter` 负责替换模板里的 `{key}`。

```java
import org.coffeepop.betterPlugin.api.utils.PlaceholderFormatter;
import java.util.Map;

String message = PlaceholderFormatter.format(
        "你好 {player}，你有 {amount} 个物品。",
        Map.of(
                "player", player.getName(),
                "amount", String.valueOf(amount)
        )
);
```

### 实际开发注意

- **替换是单遍的。** 如果某个替换值里包含 `{another}`，它会原样保留，不会再次被替换。
- **未注册的占位符保持原样。** 这通常比静默删除更适合排查问题。
- **每次格式化单独构建一个 `Map`。** 便宜且易读；不要在格式化过程中修改 map。

---

## 事件

`ListenerRegistry` 可以不用写带注解的 `Listener` 类，直接注册事件处理器。

```java
import org.coffeepop.betterPlugin.api.event.ListenerRegistry;
import org.bukkit.event.player.PlayerJoinEvent;

public final class MyPlugin extends JavaPlugin {

    private ListenerRegistry listeners;

    @Override
    public void onEnable() {
        listeners = new ListenerRegistry(this);

        listeners.register(PlayerJoinEvent.class, event ->
                event.getPlayer().sendPlainMessage("欢迎！"));

        // 可以注册任意多个处理器。
        listeners.register(PlayerQuitEvent.class, event ->
                getLogger().info(event.getPlayer().getName() + " 离开了"));
    }

    @Override
    public void onDisable() {
        listeners.unregisterAll();
    }
}
```

### 实际开发注意

- **插件卸载时调用 `unregisterAll()`。** 否则禁用插件后处理器仍会触发。
- **处理器运行在事件触发的线程上。** 大多数 Bukkit 事件在主线程，但异步事件（例如异步聊天/登录事件）不是。修改共享状态前先确认事件线程。
- **不要在事件处理器里做耗时操作。** 需要保存数据或请求网络时，用 `TaskScheduler.runAsync` 调度并尽快返回。
- **`ListenerRegistry` 可复用。** `unregisterAll()` 之后还能继续注册新处理器。

---

## 物品

`ItemBuilder` 用来创建带名字和 lore 的 `ItemStack`。

```java
import org.coffeepop.betterPlugin.api.item.ItemBuilder;
import org.bukkit.Material;

ItemStack sword = ItemBuilder.of(Material.DIAMOND_SWORD)
        .amount(1)
        .name("英雄之剑")
        .lore("传说级", "右键激活")
        .build();

// 也可以直接用 Adventure 组件。
ItemStack fancy = ItemBuilder.of(Material.PAPER)
        .name(Component.text("公告").color(NamedTextColor.GOLD))
        .lore(List.of(Component.text("第一行"), Component.text("第二行")))
        .build();
```

### 实际开发注意

- **尽量只构建一次并复用静态物品。** 频繁创建 ItemMeta 是浪费。
- **`amount(int)` 必须为正数。** 0 和负数会被拒绝。
- **lore 是追加的。** 多次调用 `lore(...)` 会叠加；想得到单一列表就一次传完。
- **构建器暂不支持附魔、Flag、伤害值等高级 meta。** 需要时 `build()` 之后用 `item.getItemMeta()` 自行修改。

---

## GUI

`InventoryGui` 是一个极简背包菜单助手。

```java
import org.coffeepop.betterPlugin.api.gui.InventoryGui;
import org.coffeepop.betterPlugin.api.item.ItemBuilder;
import org.bukkit.Material;

InventoryGui gui = InventoryGui.builder(this, 9, "菜单")
        .item(0, ItemBuilder.of(Material.DIAMOND).name("点我").build(), event ->
                event.getWhoClicked().sendPlainMessage("你点了钻石！"))
        .item(4, ItemBuilder.of(Material.BARRIER).name("关闭").build(), event ->
                event.getWhoClicked().closeInventory())
        .build();

// 给玩家打开。
gui.open(player);
```

### 实际开发注意

- **GUI 视图内所有点击都会被取消**，包括 GUI 打开时玩家底部背包里的点击。菜单是只读的。
- **内部点击监听器在第一次 `open()` 时懒注册。** 最后一个查看者关闭 GUI 后会自动注销，通常不需要手动调用 `close()`。
- **手动调用 `close()` 也是安全的。** 它会注销监听器并清空查看者；之后还能重新 `open()`。
- **点击处理器运行在主线程**（背包事件是主线程事件），不要在里面阻塞。
- **处理器收到的事件已经是被取消的。** 你仍然可以读槽位、点击类型和玩家。
- **背包大小必须是 9 的正倍数。** 超出大小的槽位在构建时会直接报错。

---

## Registry

`Registry<T>` 是一个命名、线程安全的注册表。`SimpleRegistry<T>` 是默认实现。

```java
import org.coffeepop.betterPlugin.api.registry.Registry;
import org.coffeepop.betterPlugin.api.registry.SimpleRegistry;

// 独立使用：
Registry<Storage> storages = new SimpleRegistry<>();
storages.register("main", new FileStorage());
storages.get("main").ifPresent(Storage::load);
```

如果继承 `PluginBase`，可以直接用共享注册表：

```java
registry().register("storage", new FileStorage());
registry().get("storage").ifPresent(Storage::load);
```

### 模块

`ModuleRegistry` 管理带 `enable` / `disable` 生命周期的命名模块。

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
                // 启动备份任务
            }

            @Override
            public void onDisable() {
                // 停止备份任务
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

### 实际开发注意

- **Registry 操作是线程安全的。** 可以在异步任务里注册和读取。
- **`register(...)` 会覆盖同 key 的旧值。** 使用唯一且稳定的 key 避免误覆盖。
- **`enable(...)` 是幂等的。** 重复启用已启用模块不会再次调用 `onEnable`。
- **`disable(...)` 也是幂等的。** 禁用未启用的模块不会做任何事。
- **`unregister(...)` 会先禁用模块（如果已启用），再移除。**
- **插件卸载时调用 `disableAll()`**，让每个模块清理自己的任务和监听器。

---

## 常见开发问题清单

- [ ] 卸载时取消调度器（`cancelAll()`）。
- [ ] 卸载时注销事件监听器（`unregisterAll()`）。
- [ ] 卸载时禁用所有模块（`disableAll()`）。
- [ ] 必须主线程的 Bukkit API 用主线程任务执行。
- [ ] 热重载后重新读取配置。
- [ ] 最后一个查看者关闭后不要继续持有 GUI 引用（除非你要重新打开）。
- [ ] Registry key 使用唯一、稳定的命名。
- [ ] 静态物品只构建一次并复用。

## 相关页面

- [插件入口](/zh/plugin)
- [命令概览](/zh/command/)
