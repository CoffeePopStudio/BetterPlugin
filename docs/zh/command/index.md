# 命令概览

对应包：`org.coffeepop.betterPlugin.api.command`。

命令模块基于 Paper 命令系统，用链式调用注册命令。

## 能力范围

- 链式声明命令名、权限、别名
- 命令描述、用法、无权限提示元数据
- Bukkit 风格执行器与 Tab 补全
- 玩家 / 控制台限定
- 冷却
- 子命令与自定义子节点
- 命令归属指定插件

## 工作流程

```text
CommandBuilder
    │  收集配置与执行器
    ▼
校验并加入待注册队列
    ▼
LifecycleEvents.COMMANDS
    │  Paper 生命周期事件
    ▼
Paper 命令系统
```

> `register()` 只负责把命令加入待注册队列，真正注册发生在 `LifecycleEvents.COMMANDS`。
> 因此请在插件 `onEnable()` 阶段调用；放在命令执行器、定时任务或服务器启动完成后调用，命令不会生效，也不会报错。

## 快速导航

- [快速上手](/zh/command/quick-start)
- [API 参考](/zh/command/api)
- [示例](/zh/command/examples)
