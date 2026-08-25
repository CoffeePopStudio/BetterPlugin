# 快速开始

BetterPlugin 在 Java 代码中注册 Paper Brigadier 命令，无需在 `plugin.yml` 中维护 `commands:` 段。

## 在 BetterPlugin 插件中注册

BetterPlugin 自身的主类中可以直接使用：

```java
public class BetterPlugin extends PluginBase {

    @Override
    public void onEnable() {
        CommandBuilder.create()
                .name("ping")
                .executes((sender, command, label, args) -> {
                    sender.sendPlainMessage("pong");
                    return true;
                })
                .register();
    }
}
```

## 在其他插件中使用 BetterPlugin API

1. 在 `plugin.yml` 中声明依赖：

```yaml
name: MyPlugin
version: 1.0.0
main: com.example.MyPlugin
depend: [ BetterPlugin ]
```

2. 使用 `CommandBuilder.create(this)` 注册命令：

```java
public class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        CommandBuilder.create(this)
                .name("greet")
                .permission("myplugin.greet")
                .aliases("hello")
                .executes((sender, command, label, args) -> {
                    sender.sendPlainMessage("Hello, " + sender.getName() + "!");
                    return true;
                })
                .register();
    }
}
```

## 构建与测试

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

- [API 参考](/api)
- [完整示例](/examples)
- [第三方插件接入](/guide/third-party)
- [配置说明](/guide/configuration)
