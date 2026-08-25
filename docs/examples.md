# 示例

以下示例均假设在 `JavaPlugin.onEnable()` 中调用，`this` 为当前插件实例。

## 基础命令

```java
CommandBuilder.create(this)
        .name("hello")
        .executes((sender, command, label, args) -> {
            sender.sendPlainMessage("Hello!");
            return true;
        })
        .register();
```

## 权限 + 别名

```java
CommandBuilder.create(this)
        .name("heal")
        .permission("myplugin.heal")
        .aliases("healme", "hp")
        .executes((sender, command, label, args) -> {
            sender.sendPlainMessage("你被治疗了");
            return true;
        })
        .register();
```

## Tab 补全

```java
CommandBuilder.create(this)
        .name("give")
        .permission("myplugin.give")
        .executes((sender, command, label, args) -> {
            if (args.length < 1) {
                sender.sendPlainMessage("用法: /give <物品>");
                return false;
            }
            sender.sendPlainMessage("give " + String.join(" ", args));
            return true;
        })
        .tabCompleter((sender, command, alias, args) -> {
            if (args.length == 1) {
                return List.of("diamond", "iron", "gold");
            }
            return List.of();
        })
        .register();
```

## 玩家 / 控制台限定

```java
CommandBuilder.create(this)
        .name("feed")
        .playerOnly()
        .executes((sender, command, label, args) -> {
            sender.sendPlainMessage("你吃饱了");
            return true;
        })
        .register();

CommandBuilder.create(this)
        .name("restart")
        .consoleOnly()
        .executes((sender, command, label, args) -> {
            sender.sendPlainMessage("restarting...");
            return true;
        })
        .register();
```

## 冷却

```java
CommandBuilder.create(this)
        .name("reward")
        .cooldown(Duration.ofSeconds(60))
        .executes((sender, command, label, args) -> {
            sender.sendPlainMessage("领取成功");
            return true;
        })
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
        .then(Commands.literal("status")
                .executes(ctx -> {
                    ctx.getSource().getSender().sendPlainMessage("status ok");
                    return 1;
                }))
        .register();
```

## 组合示例

```java
List<String> targets = List.of("player1", "player2");

CommandBuilder.create(this)
        .name("tpa")
        .permission("myplugin.tpa")
        .aliases("tpahere")
        .description("请求传送到某玩家")
        .usage("/tpa <玩家>")
        .permissionMessage("你没有权限使用 /tpa")
        .cooldown(Duration.ofSeconds(30))
        .executes((sender, command, label, args) -> {
            if (args.length < 1) {
                sender.sendPlainMessage("用法: /tpa <玩家>");
                return false;
            }
            sender.sendPlainMessage("已向 " + args[0] + " 发出传送请求");
            return true;
        })
        .tabCompleter((sender, command, alias, args) -> args.length == 1 ? targets : List.of())
        .register();
```

> 提示：如需补全在线玩家，可结合 Bukkit API，例如
> `Bukkit.getOnlinePlayers().stream().map(Player::getName).toList()`。
