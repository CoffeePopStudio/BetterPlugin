# 命令 API 参考

命令 API 位于 `org.coffeepop.betterPlugin.api.command` 包，入口是 `CommandBuilder`。

::: warning 实验性 API
`CommandBuilder` 与 `CommandException` 目前标注为 `@ApiStatus.Experimental`，接口可能随版本调整。
:::

## 示例所需 import

```java
import org.bukkit.plugin.java.JavaPlugin;
import org.coffeepop.betterPlugin.api.command.CommandBuilder;
import io.papermc.paper.command.brigadier.Commands; // 子命令示例需要
import java.time.Duration;                        // 冷却示例需要
import java.util.List;                            // 补全示例需要
```

## 工厂方法

### `create()`

创建命令 Builder，命令默认归属于 BetterPlugin。

```java
CommandBuilder builder = CommandBuilder.create();
```

第三方插件请改用 `create(this)` 或 `.plugin(this)`，避免命令挂到 BetterPlugin 名下。

### `create(JavaPlugin plugin)`

创建命令 Builder，命令归属于指定插件。

```java
CommandBuilder builder = CommandBuilder.create(this);
```

## 方法速查

| 方法 | 类型 | 说明 |
| --- | --- | --- |
| `name(String name)` | 配置 | 设置命令名 |
| `permission(String permission)` | 配置 | 设置所需权限 |
| `aliases(String... aliases)` | 配置 | 设置命令别名 |
| `plugin(JavaPlugin plugin)` | 配置 | 设置命令归属插件 |
| `description(String description)` | 配置 | 设置命令描述 |
| `usage(String usage)` | 配置 | 设置 usage 元数据（只有回调参数 `command` 能读，不影响 Paper 命令注册） |
| `permissionMessage(String message)` | 配置 | 设置 permission-message 元数据（同上，不负责权限提示） |
| `playerOnly()` | 配置 | 仅玩家可执行 |
| `consoleOnly()` | 配置 | 仅控制台可执行 |
| `cooldown(Duration duration)` | 配置 | 设置根命令的玩家冷却 |
| `then(ArgumentBuilder)` | 配置 | 添加子命令/子节点（添加后 `tabCompleter` 不生效） |
| `context(CommandContext)` | 执行器 | 复用已有 Brigadier Context 中的 Command |
| `executes(Command)` | 执行器 | 设置 Brigadier 执行器 |
| `executes(CommandExecutor)` | 执行器 | 设置 Bukkit 执行器 |
| `tabCompleter(TabCompleter)` | 配置 | 设置 Tab 补全（无子节点时生效） |
| `register()` | 动作 | 校验并登记命令（真正注册发生在 COMMANDS 事件） |

## 执行器

### `executes(CommandExecutor)`

Bukkit 风格执行器，适合大多数场景：

```java
CommandBuilder.create(this)
        .name("ping")
        .executes((sender, command, label, args) -> {
            sender.sendPlainMessage("pong");
            return true;
        })
        .register();
```

### `executes(Command<CommandSourceStack>)`

底层 Brigadier 执行器：

```java
CommandBuilder.create(this)
        .name("raw")
        .executes(ctx -> {
            ctx.getSource().getSender().sendPlainMessage("raw");
            return 1;
        })
        .register();
```

### `context(CommandContext<CommandSourceStack>)`

复用某个已解析上下文中的 Brigadier Command，属于高级用法。可用的 `CommandContext<CommandSourceStack>` 通常来自 `CommandDispatcher.parse(...).getContext().build(...)`；绝大多数场景直接使用上面两种 `executes` 即可。

```java
// someContext 来自已解析的 Brigadier 上下文，例如
// dispatcher.parse("...", source).getContext().build("...")
CommandBuilder.create(this)
        .context(someContext)
        .register();
```

### 返回值语义

- Bukkit 风格执行器返回 `boolean`：`true` 映射为成功（`Command.SINGLE_SUCCESS`，即 `1`），`false` 映射为失败（`0`）
- Brigadier 执行器直接返回 `int`：`1` 为成功、`0` 为失败

### 优先级

如果同时设置了多个执行器，生效顺序为：`context` > `executes(Command)` > `executes(CommandExecutor)`。不建议混用。

## Tab 补全

```java
.tabCompleter((sender, command, alias, args) -> List.of("a", "b"))
```

- `command` 是轻量 `Command` 适配器，非 `null`
- `alias` 是用户输入的命令别名
- `args` 是当前参数数组
- 添加 `.then(...)` 子节点后，`.tabCompleter(...)` 会被忽略；子节点补全请使用 Brigadier 自身的 `suggests` / 参数类型

## 命令限制

### `playerOnly()`

仅 `Player` 可执行：

```java
CommandBuilder.create(this)
        .playerOnly()
        .executes((sender, command, label, args) -> {
            // sender 一定是 Player
            return true;
        })
        .register();
```

### `consoleOnly()`

仅控制台可执行：

```java
CommandBuilder.create(this)
        .consoleOnly()
        .executes((sender, command, label, args) -> true)
        .register();
```

> 限制条件会叠加：同时设置多个条件时，发送者必须全部满足。例如 `permission(...)` 加 `playerOnly()` 表示“有权限的玩家”。`playerOnly()` 与 `consoleOnly()` 不要同时设置，否则所有发送者都被拒绝。

## 冷却

```java
CommandBuilder.create(this)
        .cooldown(Duration.ofSeconds(60))
        .executes((sender, command, label, args) -> true)
        .register();
```

- 冷却仅对 `Player` 生效
- 冷却只作用于根命令执行路径；`.then(...)` 添加的子命令使用自己的执行器，不受该冷却影响
- 冷却期间命令返回失败（Bukkit 风格相当于 `false`），执行器不会执行，并向玩家发送固定英文提示 `"Please wait before using this command again."`（暂不可配置）

## 子命令

子命令必须保留父命令执行器：

```java
CommandBuilder.create(this)
        .name("parent")
        .executes((sender, command, label, args) -> true)
        .then(Commands.literal("sub")
                .executes(ctx -> 1))
        .register();
```

## 校验规则

调用 `register()` 时：

- 命令名不能为 `null` 或空白
- 别名不能为 `null`、空白，也不能与命令名相同
- 必须设置 `context`、`executes(Command)`、`executes(CommandExecutor)` 之一
- 使用 `context` 时，其中必须包含执行器
- 同一个 builder 不能注册两次

否则抛出 [CommandException](/zh/exception)。

## 已知限制

- `usage` 与 `permissionMessage` 目前只作为元数据挂在 `Command` 适配器上（可通过执行器 / 补全回调的 `command` 参数读取），不参与 Paper 的 Brigadier 注册，也不会在权限不足时显示
- `tabCompleter` 与 `then` 同时设置时：有子节点则 Bukkit 补全被忽略
- 冷却只包裹根执行路径，子命令会绕过冷却
- 冷却提示文案为固定英文，不可配置
- `register()` 只能在 `LifecycleEvents.COMMANDS` 之前调用（即 `onEnable()` 阶段），之后调用会记录警告且不会生效
- 整个命令 API 为实验性功能，接口可能变化
