# 快速开始

## 项目定位

BetterPlugin 是一个面向 Paper 服务器的插件开发框架，按能力领域提供 API 模块：

| 模块 | 包路径 | 说明 |
| --- | --- | --- |
| 插件基础 | `api.plugin` | 插件入口基类 |
| 命令 API | `api.command` | Brigadier 命令注册与执行 |
| 异常 | `api.exception` | 框架公共异常 |

## 在第三方插件中使用

BetterPlugin 的 API 可以直接被其他插件复用。使用方插件只需要声明依赖：

```yaml
name: MyPlugin
version: 1.0.0
main: com.example.MyPlugin
depend: [ BetterPlugin ]
```

## 注册第一条命令

```java
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

## 本地构建

```bash
./gradlew test
./gradlew build
```

文档站本地预览：

```bash
npm install
npm run docs:dev
```

## 进一步阅读

- [命令 API 概览](/command/)
- [插件基础](/plugin)
- [异常](/exception)
- [集成接入](/guide/third-party)
- [配置说明](/guide/configuration)
