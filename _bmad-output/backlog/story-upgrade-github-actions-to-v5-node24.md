---
type: story
title: "Upgrade GitHub Actions in CI Workflow from v4 (Node 20) to v5 (Node 24)"
parent: ""
covers: []
sources: []
companions: []
after: []
risk: low
hitl: false
refined: true
---

# Upgrade GitHub Actions in CI Workflow from v4 (Node 20) to v5 (Node 24)

## Description

Upgrades deprecated GitHub Actions in `.github/workflows/ci.yml` (`actions/setup-java@v4`, `actions/checkout@v4`, and `gradle/actions/wrapper-validation@v4`) to `v5` so the CI/CD pipeline runs natively on Node.js 24 without deprecation warnings.

## Requirements

1. **Migrate `actions/setup-java` to `v5`**
   **Given** `.github/workflows/ci.yml` configures the JDK in the `test` job
   **When** the `Set up JDK` step executes
   **Then** it uses `actions/setup-java@v5` instead of the deprecated `actions/setup-java@v4`

2. **Migrate Node 20 Actions (`checkout` and `wrapper-validation`) to `v5`**
   **Given** `.github/workflows/ci.yml` checks out the repository in both `test` and `release` jobs and validates the Gradle wrapper
   **When** the workflow runs on GitHub Actions
   **Then** all `actions/checkout` steps use `actions/checkout@v5` and the Gradle wrapper step uses `gradle/actions/wrapper-validation@v5` (targeting Node.js 24 natively)
   **And** no `@v4` references remain for `actions/checkout`, `actions/setup-java`, or `gradle/actions/wrapper-validation`

3. **Automated DevOps Asset Test Alignment**
   **Given** `DevOpsAssetTest.kt` verifies `.github/workflows/ci.yml` action versions
   **When** `./gradlew check test` runs
   **Then** `testGitHubActionsWorkflowConfiguration` asserts `actions/checkout@v5`, `actions/setup-java@v5`, and `gradle/actions/wrapper-validation@v5`

## Boundaries

- Must not change: CI/CD job triggers (`main` branch pushes, pull requests, `v*` tags), Spotless/Closure Compiler/test execution steps, or Docker Hub multi-platform release publishing logic.
