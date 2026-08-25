# Configuration

## plugin.yml

Third-party plugins that use the BetterPlugin API **do not** need to declare commands in `plugin.yml`:

```yaml
# Wrong: no commands section is needed
commands:
  mycommand:
    permission: myplugin.command
```

Only the regular fields and the dependency need to be declared:

<<< @/snippets/plugin.yml

- `api-version` should be set to the Minecraft version of your server; this project currently targets version `26.1.2`
- `depend` declares the runtime dependency `BetterPlugin` (for the compile-time dependency, see [Integration](/guide/third-party); obtaining it via JitPack is recommended)

## Dependency Relationship

```text
Consumer plugin
    ↓ depend (declares runtime dependency)
BetterPlugin (core library)
    ↓
Paper server
```

- Consumer plugins must list `BetterPlugin` in `depend`
- `depend` only guarantees that BetterPlugin loads/enables before the consumer plugin; **it does not mean the whole server has finished loading**

BetterPlugin's own `paper-plugin.yml` (Bootstrap / Loader entry points) is described in [Building & Contributing](/guide/building).
