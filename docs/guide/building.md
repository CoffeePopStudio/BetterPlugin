# Building & Contributing

## Prerequisites

- JDK 25 (the toolchain in `build.gradle.kts` requires Java 25; the current build does not configure Foojay toolchain auto-download, so JDK 25 must be installed locally)
- Node.js 20+ (the docs site uses VitePress 1.6)

## Build & Test

Linux / macOS:

```bash
./gradlew build    # Build and run all tests
./gradlew test     # Run tests only
```

Windows:

```bash
gradlew.bat build
gradlew.bat test
```

> The `build` task already includes tests, so you don't need to run `test` before `build`.

## Publishing a New Version

1. Update `version` in `gradle.properties`.
2. Add an entry for the matching version at the top of the root `CHANGELOG.md`; write it for regular users, without programming or Java terms (see the contribution guidelines).
3. Push to `master`, then run the `Release` workflow manually from the Actions tab; it builds, creates the tag, and creates the GitHub Release.
4. JitPack builds the artifact the first time that tag is referenced; third-party plugins can then depend on it directly (see [Integration](/guide/third-party)).

## Docs Site

```bash
npm install
npm run docs:dev      # Local preview
npm run docs:build    # Build to docs/.vitepress/dist
npm run docs:preview  # Preview the built site
```

GitHub Actions automatically builds and publishes to GitHub Pages when `docs/`-related files are pushed to `master`.

## paper-plugin.yml

BetterPlugin itself is also a Paper plugin; `paper-plugin.yml` configures the Bootstrap and Loader entry points:

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

Notes when modifying `paper-plugin.yml`:

- `main`, `bootstrapper`, and `loader` must point to the correct fully qualified class names
- `processResources` substitutes `${version}` in `paper-plugin.yml`