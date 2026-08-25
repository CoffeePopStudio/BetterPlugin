# 配置说明

## plugin.yml

使用 BetterPlugin API 的第三方插件**不需要**在 `plugin.yml` 中声明命令：

```yaml
# 错误：不需要 commands 段
commands:
  mycommand:
    permission: myplugin.command
```

只需要声明依赖：

```yaml
name: MyPlugin
version: 1.0.0
main: com.example.MyPlugin
depend: [BetterPlugin]
api-version: '1.21'
```

## paper-plugin.yml

BetterPlugin 自身也是 Paper 插件，`paper-plugin.yml` 中配置了 Bootstrap 与 Loader 入口：

```yaml
name: BetterPlugin
version: '${version}'
main: org.coffeepop.betterPlugin.bootstrap.BetterPlugin
bootstrapper: org.coffeepop.betterPlugin.bootstrap.BetterPluginBootstrap
loader: org.coffeepop.betterPlugin.bootstrap.BetterPluginLoader
api-version: '26.1.2'
load: STARTUP
authors: [Neamyoo]
```

如果你在本地修改配置，记得：

- `main`、`bootstrapper`、`loader` 都必须指向正确的全限定类名
- `processResources` 使用 `paper-plugin.yml` 做变量替换 ${version}

## 依赖关系

```text
第三方插件（client）
        ↓ depend
BetterPlugin（核心库）
        ↓
Paper 服务器
```

- 第三方插件必须在 `depend` 中列出 `BetterPlugin`
- BetterPlugin 必须在服务端完整加载之后，第三方插件才会加载