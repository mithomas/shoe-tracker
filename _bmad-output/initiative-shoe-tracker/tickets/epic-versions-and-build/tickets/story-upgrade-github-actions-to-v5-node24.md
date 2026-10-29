---
id: 2
type: story
title: "Upgrade GitHub Actions in CI Workflow from v4 (Node 20) to v5 (Node 24)"
parent: epic-versions-and-build
covers: [R37]
after: [1]
hitl: false
risk: low
---

# Upgrade GitHub Actions in CI Workflow from v4 (Node 20) to v5 (Node 24)

## Description

Upgrades deprecated GitHub Actions in `.github/workflows/ci.yml` (`actions/setup-java@v4`, `actions/checkout@v4` in both `test` and `release` jobs, and `gradle/actions/wrapper-validation@v4`) to `v5` so the CI/CD pipeline runs natively on Node.js 24 without deprecation warnings.

## Requirements

- In `.github/workflows/ci.yml`:
  - Upgrade both `actions/checkout@v4` steps (`test` and `release` jobs) to `actions/checkout@v5`.
  - Upgrade `actions/setup-java@v4` to `actions/setup-java@v5`.
  - Upgrade `gradle/actions/wrapper-validation@v4` to `gradle/actions/wrapper-validation@v5`.
  - Ensure zero `@v4` references remain in `.github/workflows/ci.yml`.
- In `src/test/kotlin/org/shoetracker/devops/DevOpsAssetTest.kt`:
  - Update `testGitHubActionsWorkflowConfiguration` to assert `actions/checkout@v5`, `actions/setup-java@v5`, `gradle/actions/wrapper-validation@v5`, and `.doesNotContain("@v4")`.
- Verify: `.github/workflows/ci.yml` uses `actions/checkout@v5`, `actions/setup-java@v5`, and `gradle/actions/wrapper-validation@v5` with zero `@v4` references, and `DevOpsAssetTest` passes.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-versions-and-build/epic-versions-and-build.md
