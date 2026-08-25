# Contributing to BetterPlugin

感谢你为 BetterPlugin 做出贡献。提交代码前请先阅读以下约定。

## 1. 报告问题

- 先搜索已有 issue，避免重复提交。
- 提供复现步骤、Paper 版本与相关日志。

## 2. Pull Request

- 一个 PR 只做一件事。
- 提交前确保 `./gradlew build` 通过。
- 保持提交信息清晰，推荐使用 Conventional Commits。

## 3. 提交信息

推荐格式：`<type>: <summary>`，例如 `feat: add home command`。

常用 type：

- `feat` — 新功能
- `fix` — 修复 bug
- `refactor` — 重构
- `chore` — 杂项（依赖更新、构建配置等）
- `docs` — 文档更新

## 4. 分支命名

### 4.1 Branch Naming

所有 PR 分支必须使用以下前缀：

| 前缀 | 用途 |
| --- | --- |
| `feat/` | 新功能 |
| `fix/` | 修复 bug |
| `refactor/` | 重构 |
| `chore/` | 杂项（依赖更新、构建配置等） |
| `docs/` | 文档更新 |

规则：

- 分支名格式为 `<prefix>/<description>`，例如 `feat/add-login`。
- 不要直接从 `main` 或 `master` 分支发起 PR。
