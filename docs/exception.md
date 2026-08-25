# 异常

对应 API 包：`org.coffeepop.betterPlugin.api.exception`。

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

### 使用

```java
try {
    CommandBuilder.create()
            .name("broken")
            .register(); // 没有执行器，抛出 CommandException
} catch (CommandException e) {
    e.printStackTrace();
}
```

> 该异常属于实验性 API，后续可能调整。
