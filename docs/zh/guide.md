# 快速开始

## 项目定位

BetterPlugin 是面向 Paper 服务器的插件开发框架，按能力拆成几个模块：

| 模块 | 包路径 | 说明 | 文档 |
| --- | --- | --- | --- |
| 插件基础 | `org.coffeepop.betterPlugin.api.plugin` | 插件入口基类（`PluginBase`） | [插件基础](/zh/plugin) |
| 命令 API | `org.coffeepop.betterPlugin.api.command` | 命令注册与执行 | [命令 API](/zh/command/) |
| 异常 | `org.coffeepop.betterPlugin.api.exception` | 框架公共异常类型 | [异常](/zh/exception) |

## 选择入口类

你有两条等价的路：

- 直接继承 `JavaPlugin`。命令模块只要求传入 `JavaPlugin` 实例，这条路完全可行。
- 继承 `PluginBase`。它在 `JavaPlugin` 之上提供常用助手：带默认值的配置读取、日志短别名、自动清理的任务调度、`runWhenReady` 和 `command()` 快捷方式。详见 [插件入口](/zh/plugin)。

两条路都能正常使用 `CommandBuilder`，详见 [插件基础](/zh/plugin)。

## 添加依赖

第三方插件通过 JitPack 使用已发布的 tag，不用本地构建。在使用方项目的 `build.gradle.kts` 中添加：

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.CoffeePopStudio:BetterPlugin:{{plugin_version}}")
}
```

> 某个版本第一次被请求时，JitPack 会在云端构建，可能需要等一会儿；构建状态与版本列表见 [JitPack](https://jitpack.io/#CoffeePopStudio/BetterPlugin/{{plugin_version}})。

## 声明运行期依赖

在 `plugin.yml` 中声明依赖，并把 BetterPlugin 的 jar 放到服务端的 `plugins/` 目录：

<<< @/snippets/plugin.yml

完整接入说明见 [集成接入](/zh/guide/third-party)。

## 注册第一条命令

```java
import org.bukkit.plugin.java.JavaPlugin;
import org.coffeepop.betterPlugin.api.command.CommandBuilder;

public class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        CommandBuilder.create(this)
                .name("ping")
                .executes((sender, command, label, args) -> {
                    sender.sendPlainMessage("pong");
                    return true;
                })
                .register();
    }
}
```

无需在 `plugin.yml` 中维护 `commands:` 段，命令由框架统一注册。

> `register()` 必须在 `onEnable()` 阶段调用，也就是 Paper 的 `COMMANDS` 启动事件之前；放在命令执行器、定时任务或服务器启动完成后调用，命令不会生效，并会记录警告。

## 进一步阅读

- [命令 API 概览](/zh/command/)
- [命令 API 快速上手](/zh/command/quick-start)
- [插件基础](/zh/plugin)
- [异常](/zh/exception)
- [集成接入](/zh/guide/third-party)
- [配置说明](/zh/guide/configuration)
- [构建与贡献](/zh/guide/building)
