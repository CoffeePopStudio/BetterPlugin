---
layout: home

hero:
  name: BetterPlugin
  text: 面向 Paper 服务器的插件开发框架
  tagline: 提供统一的插件入口与可扩展的 API 模块，让每个功能领域独立演进、独立使用。
  actions:
    - theme: brand
      text: 快速开始
      link: /guide
    - theme: alt
      text: 浏览模块
      link: /#modules
---

## 模块

BetterPlugin 按能力领域组织 API，每个模块独立维护、独立使用。新能力加入时只需新增模块，不会改变首页结构。

<div class="module-grid">
  <a class="module-card" href="/plugin">
    <strong>插件基础</strong>
    <code>api.plugin</code>
    <span>Paper 插件入口基类与生命周期基础能力。</span>
  </a>
  <a class="module-card" href="/command/">
    <strong>命令 API</strong>
    <code>api.command</code>
    <span>基于 Brigadier 的命令注册与执行，含补全、冷却与子命令。</span>
  </a>
  <a class="module-card" href="/exception">
    <strong>异常</strong>
    <code>api.exception</code>
    <span>框架公共异常类型。</span>
  </a>
  <a class="module-card" href="/guide/third-party">
    <strong>集成接入</strong>
    <code>集成指南</code>
    <span>在其他插件中依赖并复用 BetterPlugin。</span>
  </a>
</div>

## 快速开始

在 `plugin.yml` 中声明依赖：

```yaml
name: MyPlugin
version: 1.0.0
main: com.example.MyPlugin
depend: [ BetterPlugin ]
```

注册一条命令：

```java
CommandBuilder.create(this)
        .name("ping")
        .executes((sender, command, label, args) -> {
            sender.sendPlainMessage("pong");
            return true;
        })
        .register();
```

完整流程见 [快速开始](/guide)，命令能力见 [命令 API 模块](/command/)。
