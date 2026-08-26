# 异常

对应包：`org.coffeepop.betterPlugin.api.exception`。

::: warning 实验性功能
`CommandBuilder` 与 `CommandException` 目前都是实验性功能，接口可能随版本调整。
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
- 没有设置任何执行器（`context` / `executes(Command)` / `executes(CommandExecutor)`）

### 示例

下面这种情况会触发异常：

```java
CommandBuilder.create()
        .name("broken")
        .register(); // 没有执行器，抛出 CommandException
```

补上执行器就正确了：

```java
CommandBuilder.create()
        .name("fixed")
        .executes((sender, command, label, args) -> true)
        .register();
```

> 如需捕获该异常，请记录日志再处理；避免在生产代码中直接 `printStackTrace()`。

## 相关页面

- [命令 API 参考](/zh/command/api)
- [命令 API 示例](/zh/command/examples)
