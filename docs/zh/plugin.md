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
import org.coffeepop.betterPlugin.api.plugin.PluginBase;

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

当前 `PluginBase` 仅提供统一的入口继承关系，不包含额外的生命周期或服务访问能力。

## 与 JavaPlugin 的关系

- `CommandBuilder.create(...)` 只要求传入 `JavaPlugin` 实例，因此使用方插件直接继承 `JavaPlugin` 也可以使用命令 API
- 继承 `PluginBase` 是可选方案，适合希望保持统一插件入口继承关系的插件
- 两条路径互不冲突，命令注册行为完全一致

## 模块边界

- 插件入口、生命周期：`org.coffeepop.betterPlugin.api.plugin`
- 命令注册：`org.coffeepop.betterPlugin.api.command`
- 公共异常：`org.coffeepop.betterPlugin.api.exception`
- 内部实现：`internal` 包，不保证兼容，不应在外部插件中使用

## 相关页面

- [快速开始](/zh/guide)
- [第三方插件接入](/zh/guide/third-party)
- [命令 API 概览](/zh/command/)
