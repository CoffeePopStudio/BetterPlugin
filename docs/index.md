---
layout: home

hero:
  name: BetterPlugin
  text: 面向 Paper 服务器的插件框架
  tagline: 提供可复用的插件基础结构与命令注册能力，减少样板代码，让开发者专注于具体功能实现。
  actions:
    - theme: brand
      text: 快速开始
      link: /guide
    - theme: alt
      text: 查看示例
      link: /examples

features:
  - title: 插件基础结构
    details: 提供 PluginBase、Bootstrap 与 Loader 等 Paper 插件入口，统一插件的启动与加载流程。
  - title: 命令 API
    details: 基于 Brigadier 的命令注册 Builder，链式配置权限、别名、补全、冷却与子命令。
  - title: Bukkit 友好
    details: 支持 CommandExecutor 与 TabCompleter，日常功能无需直接接触 Brigadier。
  - title: 第三方复用
    details: 其他插件可声明依赖并复用 API，命令可归属于调用方插件，实现能力共享。
  - title: 测试保障
    details: 基于 MockBukkit 编写单元测试，持续验证核心行为。
  - title: 开源协作
    details: 面向社区开放，通过 Issue 与 PR 持续演进能力边界。
---