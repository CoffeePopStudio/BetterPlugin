---
layout: home

hero:
  name: BetterPlugin
  text: A plugin development framework for Paper servers
  tagline: Provides a unified plugin entry base class and a modular API; each module can be used independently and combined freely.
  actions:
    - theme: brand
      text: Quick Start
      link: /guide
---

## Features {#features}

<div class="module-grid">
  <a class="module-card" href="/plugin">
    <strong>Unified plugin entry</strong>
    <span><code>PluginBase</code> provides a unified inheritance entry point for <code>JavaPlugin</code>; extending <code>JavaPlugin</code> directly works just as well.</span>
  </a>
  <a class="module-card" href="/guide">
    <strong>Modular API</strong>
    <span>API modules are organized by capability area; each module can be used independently and combined freely.</span>
  </a>
  <a class="module-card" href="/command/">
    <strong>Command registration</strong>
    <span>A Fluent API built on Paper Brigadier, supporting permissions, aliases, completion, cooldowns, and subcommands (experimental).</span>
  </a>
  <a class="module-card" href="/exception">
    <strong>Common exceptions</strong>
    <span>Unified framework exception types that are easy for callers to handle.</span>
  </a>
  <a class="module-card" href="/guide/third-party">
    <strong>Third-party reuse</strong>
    <span>Can be referenced by other plugins as a runtime dependency; commands belong to the calling plugin.</span>
  </a>
</div>
