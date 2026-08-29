# 插件入口

对应包：`org.coffeepop.betterPlugin.api.plugin`。

## PluginBase

`PluginBase` 继承 `JavaPlugin`，在它之上加了几个常用助手。你不再覆写 `onEnable` / `onDisable`，而是覆写 `onPluginEnable` / `onPluginDisable`：

```java
import org.coffeepop.betterPlugin.api.plugin.PluginBase;

import java.util.List;

public final class MyPlugin extends PluginBase {

    @Override
    protected void onPluginEnable() {
        saveDefaultConfig();

        int interval = configInt("auto-save.interval-seconds", 300);
        boolean announce = configBoolean("join-announcement.enabled", true);
        List<String> greetings = configStringList("greetings");

        log().info("MyPlugin 已就绪。");

        runSyncTimer(this::saveData, 0L, interval * 20L);
        runAsync(this::fetchRemoteNews);

        runWhenReady(() -> log().info("服务器启动完成。"));

        command()
                .name("greet")
                .playerOnly()
                .executes((sender, command, label, args) -> {
                    sender.sendPlainMessage(greetings.isEmpty() ? "Hi!" : greetings.get(0));
                    return true;
                })
                .register();
    }

    @Override
    protected void onPluginDisable() {
        log().info("MyPlugin 已停止。");
    }

    private void saveData() {
        // 保存数据
    }

    private void fetchRemoteNews() {
        // 拉取远程数据
    }
}
```

## 内置助手

| 助手 | 作用 |
| --- | --- |
| `configString` / `configInt` / `configLong` / `configDouble` / `configBoolean` / `configStringList` | 从 `config.yml` 读取指定类型的值，路径缺失时返回默认值 |
| `log()` | 插件 logger 的短别名 |
| `saveDefaultResource(path)` | 把插件 jar 里的文件复制到插件数据目录，仅当目标不存在时执行 |
| `runSync` / `runAsync` / `runSyncLater` / `runSyncTimer` / `runAsyncTimer` | 调度任务；这些方法创建的任务会在插件卸载时自动取消 |
| `isServerReady` / `runWhenReady` | 在服务器启动完成后执行代码 |
| `reloadPluginConfig` / `onConfigReload` | 重新加载 `config.yml`，并通过回调重建配置/任务 |
| `command()` | `CommandBuilder.create(this)` 的快捷方式 |
| `registry()` | 共享的线程安全注册表，用于服务/处理器 |

## 与 JavaPlugin 的关系

- `CommandBuilder.create(...)` 只要求传入 `JavaPlugin` 实例，所以直接继承 `JavaPlugin` 也能用命令模块
- 继承 `PluginBase` 是可选方案，想要这些内置助手时再用
- 两条路不冲突，命令注册行为完全一致

## 模块边界

- 插件入口：`org.coffeepop.betterPlugin.api.plugin`
- 命令注册：`org.coffeepop.betterPlugin.api.command`
- 公共异常：`org.coffeepop.betterPlugin.api.exception`
- 内部实现：`internal` 包，不保证兼容，外部插件不要使用

## 相关页面

- [快速开始](/zh/guide)
- [第三方插件接入](/zh/guide/third-party)
- [命令概览](/zh/command/)
