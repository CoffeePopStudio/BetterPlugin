# 异常

对应 API 包：`org.coffeepop.betterPlugin.api.exception`。

::: warning 实验性 API
`CommandBuilder` 与 `CommandException` 目前均为实验性 API（`@ApiStatus.Experimental`），接口可能随版本调整。
:::

## CommandException

`CommandException` 在命令无法注册时抛出，表示 Builder 处于无效状态：

```java
public class CommandException extends RuntimeException {
    public CommandException(String message) {
        super(message);
    }
}
```

### 抛出条件

调用 `CommandBuilder.register()` 时：

- 命令名为 `null` 或空字符串
- 未设置任何执行器（`context` / `executes(Command)` / `executes(CommandExecutor)`）

### 示例

触发异常的情况：

```java
CommandBuilder.create()
        .name("broken")
        .register(); // 没有执行器，抛出 CommandException
```

正确写法是补上执行器：

```java
CommandBuilder.create()
        .name("fixed")
        .executes((sender, command, label, args) -> true)
        .register();
```

> 如需捕获该异常，请记录日志并处理；避免在生产代码中直接 `printStackTrace()`。

## 相关页面

- [命令 API 参考](/zh/command/api)
- [命令 API 示例](/zh/command/examples)
