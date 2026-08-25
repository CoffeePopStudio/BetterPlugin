# 快速开始

## 项目定位

BetterPlugin 是一个面向 Paper 服务器的插件开发框架，按能力领域提供 API 模块：

| 模块 | 包路径 | 说明 | 文档 |
| --- | --- | --- | --- |
| 插件基础 | `org.coffeepop.betterPlugin.api.plugin` | 插件入口基类（`PluginBase`） | [插件基础](/plugin) |
| 命令 API | `org.coffeepop.betterPlugin.api.command` | Brigadier 命令注册与执行 | [命令 API](/command/) |
| 异常 | `org.coffeepop.betterPlugin.api.exception` | 框架公共异常类型 | [异常](/exception) |

## 选择入口类

使用方插件有两条等价路径：

- 直接继承 `JavaPlugin`。命令 API 只要求传入 `JavaPlugin` 实例，这条路完全可行。
- 继承 `PluginBase`。`PluginBase` 目前仅提供统一的入口继承关系，不会带来额外能力；需要统一的插件入口继承关系时可选择这条路。

两者都可以正常使用 `CommandBuilder`，详见 [插件基础](/plugin)。

## 在第三方插件中使用

BetterPlugin 的 API 可以直接被其他插件复用。使用方插件只需要声明运行期依赖（编译期依赖见 [集成接入](/guide/third-party)）：

<<< @/snippets/plugin.yml

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

> 命令 API（`CommandBuilder` / `CommandException`）目前是实验性 API，接口可能随版本调整。
> `register()` 必须在 `onEnable()` 阶段（Paper 的 `COMMANDS` 生命周期事件之前）调用；在命令执行器、定时任务或服务器启动完成后调用，命令不会生效且不会报错。

## 进一步阅读

- [命令 API 概览](/command/)
- [命令 API 快速上手](/command/quick-start)
- [插件基础](/plugin)
- [异常](/exception)
- [集成接入](/guide/third-party)
- [配置说明](/guide/configuration)
- [构建与贡献](/guide/building)
