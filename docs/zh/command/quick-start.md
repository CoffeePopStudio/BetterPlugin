# 命令快速上手

::: warning 实验性功能
`CommandBuilder` 与 `CommandException` 目前是实验性功能，接口可能随版本调整。
:::

## 前提

- 插件在 `plugin.yml` 的 `depend` 里声明运行期依赖 BetterPlugin
- 在 `onEnable()` 中调用，`this` 是当前插件实例（`JavaPlugin` 或 `PluginBase` 均可）
- `register()` 必须在 `LifecycleEvents.COMMANDS` 之前调用，也就是 `onEnable()` 期间

## 示例所需 import

```java
import org.bukkit.plugin.java.JavaPlugin;
import org.coffeepop.betterPlugin.api.command.CommandBuilder;
import io.papermc.paper.command.brigadier.Commands; // 子命令示例需要
import java.time.Duration;                        // 冷却示例需要
import java.util.List;                            // 补全示例需要
```

## 最简命令

```java
CommandBuilder.create(this)
        .name("ping")
        .executes((sender, command, label, args) -> {
            sender.sendPlainMessage("pong");
            return true;
        })
        .register();
```

## 常见配置

```java
CommandBuilder.create(this)
        .name("greet")
        .permission("myplugin.greet")
        .aliases("hello")
        .description("向发送者问好")
        .executes((sender, command, label, args) -> {
            sender.sendPlainMessage("Hello, " + sender.getName() + "!");
            return true;
        })
        .register();
```

> `.usage()` 与 `.permissionMessage()` 目前只是挂在回调参数 `command` 上的元数据，不会影响 Paper 的命令注册结果，详见 [API 参考](/zh/command/api)。

## 补全、限定与冷却

```java
CommandBuilder.create(this)
        .name("feed")
        .playerOnly()
        .cooldown(Duration.ofSeconds(30))
        .executes((sender, command, label, args) -> {
            sender.sendPlainMessage("你吃饱了");
            return true;
        })
        .register();

CommandBuilder.create(this)
        .name("give")
        .executes((sender, command, label, args) -> true)
        .tabCompleter((sender, command, alias, args) ->
                args.length == 1 ? List.of("diamond", "iron", "gold") : List.of())
        .register();
```

## 子命令

子命令必须保留父命令执行器，否则 `register()` 校验失败，会抛出 `CommandException`：

```java
CommandBuilder.create(this)
        .name("admin")
        .permission("myplugin.admin")
        .executes((sender, command, label, args) -> {
            sender.sendPlainMessage("用法: /admin <reload|status>");
            return true;
        })
        .then(Commands.literal("reload")
                .executes(ctx -> {
                    ctx.getSource().getSender().sendPlainMessage("reloaded!");
                    return 1;
                }))
        .register();
```

> 注意：添加 `.then(...)` 子节点后，`.tabCompleter(...)` 会被忽略；父命令的 `.cooldown(...)` 也只作用于父命令执行路径，子节点自身不受影响。

更多配置见 [API 参考](/zh/command/api)，完整用例见 [示例](/zh/command/examples)。
