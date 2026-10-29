---
title: 'Fully Move to JDK 25 for Local Development, Build Toolchain, and GitHub Actions CI Workflow'
type: 'feature'
ticket: 1
created: '2026-10-29'
status: 'built'
route: 'oneshot'
route_source: 'auto'
risk: 'low'
review: 'quick'
review_source: 'auto'
lenses_ran: ['quick']
review_loop_iteration: 1
context: ["/home/tom/development/personal/shoe-tracker/bmad/_bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md"]
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** `build.gradle.kts`, `.github/workflows/ci.yml`, and `README.md` still target or allow fallback to Java 21 (`JavaVersion.VERSION_21`, `JvmTarget.JVM_21`, `Set up JDK 21` / `java-version: '21'`, and `Java 21/25` / `JDK 21+`), creating a mismatch with the production container runtime (`eclipse-temurin:25`).

**Approach:** Configure `java.sourceCompatibility = JavaVersion.VERSION_25`, `java.targetCompatibility = JavaVersion.VERSION_25`, `java.toolchain { languageVersion.set(JavaLanguageVersion.of(25)) }`, and `kotlin.compilerOptions.jvmTarget = JvmTarget.JVM_25` in `build.gradle.kts`; update `.github/workflows/ci.yml` to `Set up JDK 25` and `java-version: '25'`; update `README.md` to `Java 25` and `JDK 25`; and enforce these assertions in `DevOpsAssetTest.kt`.

</frozen-after-approval>

## Implementation Notes

- Vendor-agnostic `JavaLanguageVersion.of(25)` toolchain resolves both local OpenJDK 25 and CI Eclipse Temurin 25.

## Plan Change Log

## Review Triage Log

## Verification

**Commands:**
- `./gradlew check test spotlessCheck lintJs` -- expected: `BUILD SUCCESSFUL` with all unit/integration tests and linters passing.
