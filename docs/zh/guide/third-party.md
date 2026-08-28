# 第三方插件接入

BetterPlugin 可以作为运行时依赖被其他插件复用。通过 `create(plugin)` 注册的命令会归属于调用方插件，而不是 BetterPlugin。

## 1. 添加依赖

第三方插件通过 JitPack 直接使用已发布的 tag，不用本地构建或发布。某个版本第一次被请求时，JitPack 会在云端构建，可能需要等一会儿。

在使用方项目的 `build.gradle.kts` 中添加：

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.CoffeePopStudio:BetterPlugin:{{plugin_version}}")
}
```

构建状态与版本列表见 [JitPack](https://jitpack.io/#CoffeePopStudio/BetterPlugin/{{plugin_version}})。

## 2. 添加运行期依赖

使用方插件必须在 `plugin.yml` 中声明依赖 BetterPlugin：

<<< @/snippets/plugin.yml

同时把 BetterPlugin 的 jar 放到服务端的 `plugins/` 目录，例如从 JitPack 下载：

```text
https://jitpack.io/com/github/CoffeePopStudio/BetterPlugin/{{plugin_version}}/BetterPlugin-{{plugin_version}}.jar
```

> `plugin.yml` 的 `depend` 只负责运行期加载顺序；编译还是要靠第 1 步的依赖。

## 3. 注册命令

```java
import org.bukkit.plugin.java.JavaPlugin;
import org.coffeepop.betterPlugin.api.command.CommandBuilder;

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

入口类也可以继承 `PluginBase`，两种方式都可用，见 [插件基础](/zh/plugin)。

## 4. 指定插件

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

## 5. 权限、别名、补全

```java
import java.util.List;
import org.coffeepop.betterPlugin.api.command.CommandBuilder;

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

更多用法与限制见 [命令 API 参考](/zh/command/api)。
