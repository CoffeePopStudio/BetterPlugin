# 构建与贡献

## 前置要求

- JDK 25（`build.gradle.kts` 的 toolchain 要求 Java 25；当前构建未配置 Foojay 工具链自动下载，需要本机安装 JDK 25）
- Node.js 20+（文档站使用 VitePress 1.6）

## 构建与测试

Linux / macOS：

```bash
./gradlew build    # 构建并运行全部测试
./gradlew test     # 只运行测试
```

Windows：

```bash
gradlew.bat build
gradlew.bat test
```

> `build` 任务已经包含测试，不需要先 `test` 再 `build`。

## 发布新版本

1. 在 `gradle.properties` 中更新 `version`。
2. 在根目录 `CHANGELOG.md` 顶部添加对应版本的条目；写法必须用普通用户可以读懂的语言，不使用编程或 Java 术语（见贡献规范）。
3. 推送到 `master`，GitHub Actions 会自动构建、创建 tag 与 GitHub Release。
4. 该 tag 首次被 JitPack 引用时会构建产物，第三方插件直接引用即可（见 [集成接入](/zh/guide/third-party)）。

## 文档站

```bash
npm install
npm run docs:dev      # 本地预览
npm run docs:build    # 构建到 docs/.vitepress/dist
npm run docs:preview  # 预览构建产物
```

GitHub Actions 会在推送 `docs/` 相关文件到 `master` 时自动构建并发布到 GitHub Pages。

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

修改 `paper-plugin.yml` 时需注意：

- `main`、`bootstrapper`、`loader` 必须指向正确的全限定类名
- `processResources` 会对 `paper-plugin.yml` 中的 `${version}` 做变量替换
