# 命令 API 快速上手

## 前提

- 插件声明依赖 BetterPlugin
- 使用 `CommandBuilder.create(this)` 创建 Builder

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
        .usage("/greet")
        .permissionMessage("你没有权限执行此命令")
        .executes((sender, command, label, args) -> {
            sender.sendPlainMessage("Hello, " + sender.getName() + "!");
            return true;
        })
        .register();
```

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

```java
CommandBuilder.create(this)
        .name("admin")
        .permission("myplugin.admin")
        .then(Commands.literal("reload")
                .executes(ctx -> {
                    ctx.getSource().getSender().sendPlainMessage("reloaded!");
                    return 1;
                }))
        .register();
```

更多配置见 [API 参考](/command/api)，完整用例见 [示例](/command/examples)。
