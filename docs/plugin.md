# 插件基础

对应 API 包：`org.coffeepop.betterPlugin.api.plugin`。

## PluginBase

`PluginBase` 是 BetterPlugin 提供的插件入口基类，继承自 `JavaPlugin`：

```java
public abstract class PluginBase extends JavaPlugin {
}
```

使用方式：

```java
public final class MyPlugin extends PluginBase {

    @Override
    public void onEnable() {
        // 插件启用逻辑
    }

    @Override
    public void onDisable() {
        // 插件卸载逻辑
    }
}
```

当前 `PluginBase` 仅提供统一的入口继承关系，后续的通用生命周期能力、服务访问等会在此模块内扩展。

## 模块边界

- 插件入口、生命周期：`api.plugin`
- 命令注册：`api.command`
- 公共异常：`api.exception`
- 内部实现：`internal` 包，不保证兼容，不应在外部插件中使用
