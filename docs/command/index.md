# 命令 API 概览

对应 API 包：`org.coffeepop.betterPlugin.api.command`。

命令模块基于 Paper Brigadier 命令系统，提供 Fluent 风格的命令注册能力。

## 能力范围

- 链式声明命令名、权限、别名
- 命令描述、用法、无权限提示
- Bukkit 风格执行器与 Tab 补全
- 玩家 / 控制台限定
- 冷却
- 子命令与自定义 Brigadier 子节点
- 命令归属指定插件

## 工作流程

```text
CommandBuilder
    │  收集配置与执行器
    ▼
CommandBuilderImpl
    │  校验、构建 Brigadier 节点
    ▼
CommandRegistry
    │  汇总待注册命令
    ▼
LifecycleEvents.COMMANDS
    │  Paper 生命周期事件
    ▼
Paper 命令系统
```

## 快速导航

- [快速上手](/command/quick-start)
- [API 参考](/command/api)
- [示例](/command/examples)
