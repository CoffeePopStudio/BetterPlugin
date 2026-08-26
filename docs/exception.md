# Exceptions

Corresponding package: `org.coffeepop.betterPlugin.api.exception`.

::: warning Experimental API
`CommandBuilder` and `CommandException` are experimental right now, so their interfaces may change between versions.
:::

## CommandException

`CommandException` is thrown when a command cannot be registered because the builder is in an invalid state:

```java
public class CommandException extends RuntimeException {
    public CommandException(String message) {
        super(message);
    }
}
```

### Throwing Conditions

When you call `CommandBuilder.register()`:

- The command name is `null` or an empty string
- No executor is set (`context` / `executes(Command)` / `executes(CommandExecutor)`)

### Example

This case throws the exception:

```java
CommandBuilder.create()
        .name("broken")
        .register(); // no executor set, throws CommandException
```

The correct way is to add an executor:

```java
CommandBuilder.create()
        .name("fixed")
        .executes((sender, command, label, args) -> true)
        .register();
```

> If you catch this exception, log it and handle it. Avoid calling `printStackTrace()` directly in production code.

## Related Pages

- [Command API reference](/command/api)
- [Command API examples](/command/examples)