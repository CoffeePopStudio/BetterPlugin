# Changelog

Every release must be described here in plain, human-readable language. Readers should understand what changed without any programming or Java knowledge.

## 26.11.0-mc26.1.2 - 2026-08-26

- Commands can now declare typed inputs (numbers, text, and more) and get player-name autocomplete without writing low-level details.
- The framework itself now has a basic `/betterplugin` command that shows its version.
- Plugins that use the shared starting point can now reload their settings with a simple callback.
- Command messages support custom placeholders and a fully custom formatter.

## 26.10.0-mc26.1.2 - 2026-08-26

- Commands can now show a custom message while a player waits for a cooldown; the default message is still English.
- Commands can show a custom message when a player does not have permission.
- The command system is now considered stable instead of experimental.
- Plugins that use the shared starting point can now bundle extra default files (for example, message files) and have them copied into the server automatically when they are missing.
- Build checks now warn earlier about outdated internal components.

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
