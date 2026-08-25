# 配置说明

## plugin.yml

使用 BetterPlugin API 的第三方插件**不需要**在 `plugin.yml` 中声明命令：

```yaml
# 错误：不需要 commands 段
commands:
  mycommand:
    permission: myplugin.command
```

只需要声明常规字段和依赖：

<<< @/snippets/plugin.yml

- `api-version` 填写你服务端对应的 Minecraft 版本，本项目当前目标版本为 `26.1.2`
- `depend` 声明运行期依赖 `BetterPlugin`（编译期依赖见 [集成接入](/zh/guide/third-party)，推荐通过 JitPack 获取）

## 依赖关系

```text
使用方插件
    ↓ depend（声明运行期依赖）
BetterPlugin（核心库）
    ↓
Paper 服务器
```

- 使用方插件必须在 `depend` 中列出 `BetterPlugin`
- `depend` 只保证 BetterPlugin 先于使用方插件加载/启用，**并不代表服务器整体加载完成**

BetterPlugin 自身的 `paper-plugin.yml`（Bootstrap / Loader 入口）说明见 [构建与贡献](/zh/guide/building)。
