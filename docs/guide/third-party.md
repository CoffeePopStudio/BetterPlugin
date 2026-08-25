# 第三方插件接入

BetterPlugin 可作为运行时依赖被其他插件复用。通过 `create(plugin)` 注册的命令会归属于调用方插件，而不是 BetterPlugin。

> 命令 API 目前是实验性 API，接口可能随版本调整。

## 1. 添加编译期依赖

代码里用到的 `org.coffeepop.betterPlugin.api.command.CommandBuilder` 来自 BetterPlugin 的 jar，需要先让它进入编译 classpath。

### 方式 A：通过 JitPack 获取（推荐）

直接使用已发布的 tag，无需本地构建或发布。某个版本第一次被请求时 JitPack 会在云端构建，可能需要等待片刻。

```kotlin
repositories {
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("com.github.CoffeePopStudio:BetterPlugin:26.8.1-mc26.1.2")
}
```

构建状态与版本列表见 [JitPack](https://jitpack.io/#CoffeePopStudio/BetterPlugin/26.8.1-mc26.1.2)。

### 方式 B：发布到 Maven Local

先在 BetterPlugin 项目内发布一次：

```bash
./gradlew publishToMavenLocal        # Linux / macOS
gradlew.bat publishToMavenLocal      # Windows
```

然后在使用方项目的 `build.gradle.kts` 中添加：

```kotlin
repositories {
    mavenLocal()
}

dependencies {
    compileOnly("org.coffeepop:BetterPlugin:26.8.1-mc26.1.2")
}
```

### 方式 C：直接引用本地 jar

先在 BetterPlugin 项目内构建，再复制产物：

```bash
./gradlew build                      # Linux / macOS
gradlew.bat build                    # Windows
```

把 `build/libs/BetterPlugin-26.8.1-mc26.1.2.jar` 复制到使用方项目的 `libs/` 目录，然后添加：

```kotlin
dependencies {
    compileOnly(files("libs/BetterPlugin-26.8.1-mc26.1.2.jar"))
}
```

## 2. 添加运行期依赖

使用方插件必须在 `plugin.yml` 中声明依赖 BetterPlugin：

<<< @/snippets/plugin.yml

- 运行期：把 BetterPlugin 的 jar 放到服务端的 `plugins/` 目录（JitPack 产物 jar 同样可用）
- 如果从 JitPack 获取的 jar 文件名带构建后缀，放进 `plugins/` 前重命名为 `BetterPlugin.jar` 即可

> `plugin.yml` 的 `depend` 只负责运行期加载顺序；编译仍需第 1 步的依赖。

## 3. 注册命令

```java
import org.bukkit.plugin.java.JavaPlugin;
import org.coffeepop.betterPlugin.api.command.CommandBuilder;

public class MyPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        CommandBuilder.create(this)
                .name("hello")
                .executes((sender, command, label, args) -> {
                    sender.sendPlainMessage("Hello from MyPlugin");
                    return true;
                })
                .register();
    }
}
```

入口类也可以继承 `PluginBase`，两种方式都可用，见 [插件基础](/plugin)。

## 4. 指定插件

两种写法等价：

```java
CommandBuilder.create(this)
        .name("hello")
        .executes((sender, command, label, args) -> true)
        .register();
```

或：

```java
CommandBuilder.create()
        .plugin(this)
        .name("hello")
        .executes((sender, command, label, args) -> true)
        .register();
```

> 注意：如果不指定 `.plugin()`，命令会注册到 BetterPlugin 名下。第三方插件请务必使用 `create(this)` 或 `.plugin(this)`。

## 5. 权限、别名、补全

```java
import java.util.List;
import org.coffeepop.betterPlugin.api.command.CommandBuilder;

CommandBuilder.create(this)
        .name("give")
        .permission("myplugin.give")
        .aliases("i")
        .executes((sender, command, label, args) -> {
            // ...
            return true;
        })
        .tabCompleter((sender, command, label, args) -> {
            if (args.length == 1) {
                return List.of("diamond", "iron", "gold");
            }
            return List.of();
        })
        .register();
```

更多能力与限制见 [命令 API 参考](/command/api)。
