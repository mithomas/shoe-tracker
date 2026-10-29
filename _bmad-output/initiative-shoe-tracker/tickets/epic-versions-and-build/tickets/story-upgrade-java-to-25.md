---
id: 1
type: story
title: "Fully Move to JDK 25 for Local Development, Build Toolchain, and GitHub Actions CI Workflow"
parent: epic-versions-and-build
covers: [R36]
after: []
hitl: false
risk: low
---

# Fully Move to JDK 25 for Local Development, Build Toolchain, and GitHub Actions CI Workflow

## Description

Upgrades the project's Gradle Java toolchain, source/target compatibility, Kotlin JVM bytecode target, GitHub Actions CI workflow (`.github/workflows/ci.yml`) JDK setup, and developer documentation from Java 21 fallback to JDK 25 across local development and CI, matching the production container runtime (`eclipse-temurin:25`).

## Requirements

- In `build.gradle.kts`:
  - Set `java.sourceCompatibility = JavaVersion.VERSION_25` and `java.targetCompatibility = JavaVersion.VERSION_25`.
  - Configure `java.toolchain { languageVersion.set(JavaLanguageVersion.of(25)) }` (vendor-agnostic so both local OpenJDK 25 and CI Temurin 25 resolve natively).
  - Set `kotlin.compilerOptions.jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25`.
  - Ensure zero `VERSION_21` or `JVM_21` references remain.
- In `.github/workflows/ci.yml`:
  - Update the step name from `- name: Set up JDK 21` to `- name: Set up JDK 25`.
  - Update `java-version: '21'` to `java-version: '25'` (with `distribution: 'temurin'` and `cache: gradle`).
  - Ensure zero `JDK 21` or `java-version: '21'` references remain in `.github/workflows/ci.yml`.
- In `README.md`:
  - Update `Java 21/25` in the Tech Stack table to `Java 25` and `JDK 21+` in Prerequisites to `JDK 25`.
- In `src/test/kotlin/org/shoetracker/devops/DevOpsAssetTest.kt`:
  - In `testGitHubActionsWorkflowConfiguration`, assert `.contains("Set up JDK 25")`, `.contains("java-version: '25'")`, `.doesNotContain("Set up JDK 21")`, and `.doesNotContain("java-version: '21'")`.
  - Assert `JavaVersion.VERSION_25`, `JavaLanguageVersion.of(25)`, and `JvmTarget.JVM_25` (and `.doesNotContain("VERSION_21")`, `.doesNotContain("JVM_21")`) in `build.gradle.kts`, and `JDK 25` in `README.md`.
- Verify: `build.gradle.kts` compiles and runs tests on JDK 25 with zero `VERSION_21`/`JVM_21` references, `.github/workflows/ci.yml` configures `Set up JDK 25` and `java-version: '25'`, `README.md` specifies `JDK 25`, and `./gradlew check test` passes.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-versions-and-build/epic-versions-and-build.md
