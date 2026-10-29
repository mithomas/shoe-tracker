---
type: story
title: "Fully Move to JDK 25 for Local Development, Build Toolchain, and CI"
parent: ""
covers: []
sources: []
companions: []
after: []
risk: low
hitl: false
refined: true
---

# Fully Move to JDK 25 for Local Development, Build Toolchain, and CI

## Description

Upgrades the project's Gradle Java toolchain, source/target compatibility, Kotlin JVM bytecode target, CI workflow JDK setup, and developer documentation from Java 21 fallback to JDK 25 across local development and CI, matching the production container runtime (`eclipse-temurin:25`).

## Requirements

1. **Local Development & Gradle Build Toolchain on JDK 25**
   **Given** `build.gradle.kts` configures Java and Kotlin compilation for local development (`./gradlew quarkusDev`, `./gradlew check test`, `./gradlew build`)
   **When** the build is evaluated and compiled
   **Then** `java.sourceCompatibility` and `java.targetCompatibility` are set to `JavaVersion.VERSION_25`, the Gradle Java toolchain requests `JavaLanguageVersion.of(25)`, and `kotlin.compilerOptions.jvmTarget` targets `JvmTarget.JVM_25`
   **And** no Java 21 fallback remains in `build.gradle.kts`

2. **GitHub Actions CI Workflow on JDK 25**
   **Given** `.github/workflows/ci.yml` sets up Java for the `test` job
   **When** `actions/setup-java@v4` runs in CI
   **Then** `java-version` is set to `'25'` (`temurin` distribution) so CI compiles, lints, and tests with JDK 25

3. **Developer Documentation & Asset Verification**
   **Given** `README.md` documents the tech stack and local development prerequisites
   **When** a developer reviews the prerequisites and tech stack table
   **Then** `README.md` specifies **JDK 25** (replacing `Java 21/25` and `JDK 21+`)
   **And** `./gradlew check test` passes cleanly under JDK 25

## Boundaries

- Must not change: Production `Dockerfile` base images (`eclipse-temurin:25-jdk-alpine` / `eclipse-temurin:25-jre-alpine`), which already run on JDK 25.
