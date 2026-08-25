# 第三方插件接入

BetterPlugin 可作为依赖库供其他插件复用。通过 `create(plugin)` 注册的命令会归属于调用方插件，而不是 BetterPlugin。

## 1. 添加依赖

使用方插件必须在 `plugin.yml` 中声明依赖 BetterPlugin：

```yaml
name: MyPlugin
version: 1.0.0
main: com.example.MyPlugin
depend: [ BetterPlugin ]
```

BetterPlugin 的 plugin name 是 `BetterPlugin`，请按你服务端配置里的实际名称填写。

## 2. 注册命令

```java
public class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        CommandBuilder.create(this)
                .name("hello")
                .executes((sender, command, label, args) -> {
                    sender.sendPlainMessage("Hello from MyPlugin");
                    return true;
                })
                .register();
    }
}
```

## 3. 指定插件

两种写法等价：

```java
CommandBuilder.create(this)
        .name("hello")
        .executes((sender, command, label, args) -> true)
        .register();
```

或：

```java
CommandBuilder.create()
        .plugin(this)
        .name("hello")
        .executes((sender, command, label, args) -> true)
        .register();
```

> 注意：如果不指定 `.plugin()`，命令会注册到 BetterPlugin 名下。第三方插件请务必使用 `create(this)` 或 `.plugin(this)`。

## 4. 权限、别名、补全

```java
CommandBuilder.create(this)
        .name("give")
        .permission("myplugin.give")
        .aliases("i")
        .executes((sender, command, label, args) -> {
            // ...
            return true;
        })
        .tabCompleter((sender, command, label, args) -> {
            if (args.length == 1) {
                return List.of("diamond", "iron", "gold");
            }
            return List.of();
        })
        .register();
```