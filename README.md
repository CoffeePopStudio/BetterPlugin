# BetterPlugin

面向 Paper 服务器开发的插件框架，提供可复用的插件基础结构与命令注册能力。

> 📖 完整文档：**[BetterPlugin 文档站](https://CoffeePopStudio.github.io/BetterPlugin/)** · [本地构建](#构建文档站)

## 定位

BetterPlugin 是一个用于构建 Paper 插件的开源项目，目标是让插件开发者在编写服务端功能时复用通用基础设施，而不是重复实现命令注册、生命周期等样板代码。

API 按能力模块组织：

| 模块 | 包路径 | 说明 |
| --- | --- | --- |
| 插件基础 | `api.plugin` | 插件入口基类与生命周期基础能力 |
| 命令 API | `api.command` | Brigadier 命令注册与执行，含补全、冷却与子命令 |
| 异常 | `api.exception` | 框架公共异常类型 |

## 快速开始

在自己插件中使用：

```yaml
name: MyPlugin
version: 1.0.0
main: com.example.MyPlugin
depend: [ BetterPlugin ]
```

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

详细文档：

- [快速开始](https://CoffeePopStudio.github.io/BetterPlugin/guide)
- [命令 API](https://CoffeePopStudio.github.io/BetterPlugin/command/)
- [插件基础](https://CoffeePopStudio.github.io/BetterPlugin/plugin)
- [集成接入](https://CoffeePopStudio.github.io/BetterPlugin/guide/third-party)

## 构建与测试

```bash
./gradlew build
./gradlew test
```

## 构建文档站

文档站使用 [VitePress](https://vitepress.dev)，并采用 Material 风格主题。

```bash
npm install
npm run docs:dev    # 本地预览
npm run docs:build  # 构建到 docs/.vitepress/dist
```

GitHub Actions 会在推送 `docs/` 相关文件到 `master` 时自动构建并发布到 GitHub Pages。
