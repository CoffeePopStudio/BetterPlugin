# Changelog

Every release must be described here in plain, human-readable language. Readers should understand what changed without any programming or Java knowledge.

## 26.9.1-mc26.1.2 - 2026-08-26

- Commands now understand quoted text: typing `/say "hello world"` keeps the two words together as one piece of text.
- Work that waits for the server to finish starting is more reliable, including a rare case where it could be skipped.
- Commands with unclear settings (empty names, invalid alternate names, registering the same command twice, or negative cooldowns) are now rejected early with a clear error instead of failing later.
- Old cooldown records are cleaned up automatically, so long-running servers use less memory.
- Registering a command too late now writes a warning to the server log instead of silently doing nothing.
- A few internal checks were tightened for stability and clear failures.

## 26.9.0-mc26.1.2 - 2026-08-26

- The shared plugin starting point now includes everyday helpers: reading settings from the config file with sensible fallbacks, a shorter way to write to the server log, and starting background or repeating tasks that are stopped automatically when the plugin is disabled.
- Plugins can now wait until the server has finished starting before running setup work.
- Registering a command from a plugin got even shorter.
- Fixed a bug where a command that required both a permission and a player/console restriction only kept the last one; now all restrictions are checked together.

## 26.8.1-mc26.1.2 - 2026-08-25

- First public release of BetterPlugin.
- Plugins can now register commands with a few short lines.
- Commands can have permissions, alternate names, autocomplete, cooldowns and subcommands.
- Other plugins can reuse BetterPlugin as a shared dependency.
- Added the documentation website.
