# 命令 API 参考

命令 API 位于 `org.coffeepop.betterPlugin.api.command.CommandBuilder`。

```java
import org.coffeepop.betterPlugin.api.command.CommandBuilder;
```

## 工厂方法

### `create()`

创建命令 Builder，命令默认归属于 BetterPlugin。

```java
CommandBuilder builder = CommandBuilder.create();
```

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
| `usage(String usage)` | 配置 | 设置命令用法 |
| `permissionMessage(String message)` | 配置 | 设置无权限提示 |
| `playerOnly()` | 配置 | 仅玩家可执行 |
| `consoleOnly()` | 配置 | 仅控制台可执行 |
| `cooldown(Duration duration)` | 配置 | 设置玩家冷却 |
| `then(ArgumentBuilder)` | 配置 | 添加子命令/子节点 |
| `context(CommandContext)` | 执行器 | 使用已有 Brigadier Context |
| `executes(Command)` | 执行器 | 设置 Brigadier 执行器 |
| `executes(CommandExecutor)` | 执行器 | 设置 Bukkit 执行器 |
| `tabCompleter(TabCompleter)` | 配置 | 设置 Tab 补全 |
| `register()` | 动作 | 校验并注册命令 |

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

使用已有 Context：

```java
CommandBuilder.create(this)
        .context(someContext)
        .register();
```

## Tab 补全

```java
.tabCompleter((sender, command, alias, args) -> List.of("a", "b"))
```

- `command` 是轻量 `Command` 适配器，非 `null`
- `alias` 是用户输入的命令别名
- `args` 是当前参数数组

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

## 冷却

```java
CommandBuilder.create(this)
        .cooldown(Duration.ofSeconds(60))
        .executes((sender, command, label, args) -> true)
        .register();
```

- 冷却仅对 `Player` 生效
- 冷却期间命令返回 `false`，且不会执行

## 子命令

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

- 命令名不能为 `null` 或空字符串
- 必须设置 `context`、`executes(Command)`、`executes(CommandExecutor)` 之一

否则抛出 [CommandException](/exception)。
