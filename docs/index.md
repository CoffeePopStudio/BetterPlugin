---
layout: home

hero:
  name: BetterPlugin
  text: 面向 Paper 服务器的插件开发框架
  tagline: 提供统一的插件入口基类与模块化 API，每个模块可独立使用、自由组合。
  actions:
    - theme: brand
      text: 快速开始
      link: /guide
---

## 特性 {#features}

- **统一插件入口**：`PluginBase` 提供 `JavaPlugin` 的统一继承入口，直接继承 `JavaPlugin` 同样可用
- **模块化 API**：按能力领域划分 API 模块，各模块可独立使用
- **命令注册**：基于 Paper Brigadier 的 Fluent API，支持权限、别名、补全、冷却与子命令（实验性）
- **公共异常**：统一的框架异常类型，便于调用方处理
- **第三方复用**：可作为运行时依赖被其他插件引用，命令归属调用方插件
