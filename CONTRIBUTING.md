# Contributing to BetterPlugin

Thanks for contributing to BetterPlugin. Please read the following conventions before submitting changes.

## 1. Reporting Issues

- Search existing issues first to avoid duplicates.
- Include reproduction steps, the Paper version and relevant logs.

## 2. Pull Requests

- Keep one PR focused on a single change.
- Make sure `./gradlew build` passes before submitting.
- Keep commit messages clear; Conventional Commits are preferred.

## 3. Commit Messages

Preferred format: `<type>: <summary>`, for example `feat: add home command`.

Common types:

- `feat` — new feature
- `fix` — bug fix
- `refactor` — refactoring
- `chore` — chores (dependency updates, build config, etc.)
- `docs` — documentation updates

## 4. Branch Naming

### 4.1 Branch Naming

All pull request branches must use one of the following prefixes:

| Prefix | Purpose |
| --- | --- |
| `feat/` | New feature |
| `fix/` | Bug fix |
| `refactor/` | Refactoring |
| `chore/` | Chores (dependency updates, build config, etc.) |
| `docs/` | Documentation updates |

Rules:

- Branch names use the format `<prefix>/<description>`, for example `feat/add-login`.
- Do not open pull requests directly from `main` or `master`.

## 5. Changelog

Every released version must have an entry in the root `CHANGELOG.md`.

- Write for users: explain what is new, what changed and what was fixed.
- Use plain, human-readable language that anyone can understand.
- Do **not** use programming or Java terminology, class names, method names, build internals or implementation details.

Good example:

> Commands now ask players to wait before using the same command again.

Bad example:

> Added `cooldown(Duration)` to `CommandBuilder` so `applyCooldown` wraps the executor command.

