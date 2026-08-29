# BetterPlugin

A plugin development framework for Paper servers, providing a reusable plugin entry base class and command registration API.

> 📖 Full documentation: **[BetterPlugin docs](https://CoffeePopStudio.github.io/BetterPlugin/)** · [Building the docs site](#building-the-docs-site)

## Overview

BetterPlugin is an open-source framework for building Paper plugins. It lets plugin developers reuse common infrastructure instead of reimplementing command registration and other boilerplate.

API modules:

| Module | Package | Description |
| --- | --- | --- |
| Plugin base | `org.coffeepop.betterPlugin.api.plugin` | Plugin entry base class (unified inheritance entry) |
| Command API | `org.coffeepop.betterPlugin.api.command` | Brigadier command registration and execution, including completion, cooldowns and subcommands (experimental API) |
| Exceptions | `org.coffeepop.betterPlugin.api.exception` | Common framework exception types |

## Quick start

In your own plugin:

```yaml
name: MyPlugin
version: 1.0.0
main: com.example.MyPlugin
api-version: '26.1.2'
depend: [BetterPlugin]
```

```java
public class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        CommandBuilder.create(this)
                .name("ping")
                .executes((sender, command, label, args) -> {
                    sender.sendPlainMessage("pong");
                    return true;
                })
                .register();
    }
}
```

Detailed docs:

- [Quick start](https://CoffeePopStudio.github.io/BetterPlugin/guide)
- [Command API](https://CoffeePopStudio.github.io/BetterPlugin/command/)
- [Plugin base](https://CoffeePopStudio.github.io/BetterPlugin/plugin)
- [Third-party integration](https://CoffeePopStudio.github.io/BetterPlugin/guide/third-party)

## Dependency

### JitPack

Use the released tag directly from JitPack; no local build or publishing is required:

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.CoffeePopStudio:BetterPlugin:26.12.0-mc26.1.2")
}
```

Build status and available versions: <https://jitpack.io/#CoffeePopStudio/BetterPlugin/26.12.0-mc26.1.2>

## Building and testing

```bash
./gradlew build       # Linux / macOS: build and run all tests
gradlew.bat build     # Windows
```

## Building the docs site

The docs site uses [VitePress](https://vitepress.dev).

```bash
npm install
npm run docs:dev    # local preview
npm run docs:build  # build to docs/.vitepress/dist
```

GitHub Actions builds and publishes the docs to GitHub Pages when files under `docs/` are pushed to `master`.
