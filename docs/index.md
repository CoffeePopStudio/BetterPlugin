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

<div class="module-grid">
  <div class="module-card">
    <strong>统一插件入口</strong>
    <span><code>PluginBase</code> 提供 <code>JavaPlugin</code> 的统一继承入口，直接继承 <code>JavaPlugin</code> 同样可用。</span>
  </div>
  <div class="module-card">
    <strong>模块化 API</strong>
    <span>按能力领域划分 API 模块，各模块可独立使用、自由组合。</span>
  </div>
  <div class="module-card">
    <strong>命令注册</strong>
    <span>基于 Paper Brigadier 的 Fluent API，支持权限、别名、补全、冷却与子命令（实验性）。</span>
  </div>
  <div class="module-card">
    <strong>公共异常</strong>
    <span>统一的框架异常类型，便于调用方处理。</span>
  </div>
  <div class="module-card">
    <strong>第三方复用</strong>
    <span>可作为运行时依赖被其他插件引用，命令归属调用方插件。</span>
  </div>
</div>
