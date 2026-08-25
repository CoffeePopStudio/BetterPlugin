# BetterPlugin

面向 Paper 服务器开发的插件框架，提供可复用的插件基础结构与命令注册能力。

> 📖 完整文档：**[BetterPlugin 文档站](https://oneachina.github.io/BetterPlugin/)** · [本地构建](#构建文档站)

## 定位

BetterPlugin 是一个用于构建 Paper 插件的开源项目，目标是让插件开发者在编写服务端功能时复用通用基础设施，而不是重复实现命令注册、生命周期等样板代码。

当前包含：

- **插件基础结构**：`PluginBase`、Bootstrap、Loader 等 Paper 插件入口
- **命令 API**：基于 Brigadier 的命令注册 Builder，支持权限、别名、补全、冷却与子命令
- **第三方依赖能力**：其他插件可以声明依赖 BetterPlugin，并复用其 API

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

完整用法见 [文档站 - 指南](https://oneachina.github.io/BetterPlugin/guide) 与 [示例](https://oneachina.github.io/BetterPlugin/examples)。

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