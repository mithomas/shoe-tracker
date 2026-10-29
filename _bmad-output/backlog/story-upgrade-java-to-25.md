---
type: story
title: "Fully Move to JDK 25 for Local Development, Build Toolchain, and GitHub Actions CI Workflow"
parent: "epic-versions-and-build"
covers: ["R36"]
sources: []
companions: []
after: []
risk: low
hitl: false
refined: true
---

# Fully Move to JDK 25 for Local Development, Build Toolchain, and GitHub Actions CI Workflow

## Description

Upgrades the project's Gradle Java toolchain, source/target compatibility, Kotlin JVM bytecode target, GitHub Actions CI workflow (`.github/workflows/ci.yml`) JDK setup, and developer documentation from Java 21 fallback to JDK 25 across local development and CI, matching the production container runtime (`eclipse-temurin:25`).

## Requirements

1. **Local Development & Gradle Build Toolchain on JDK 25 (`build.gradle.kts`)**
   **Given** `build.gradle.kts` configures Java and Kotlin compilation for local development (`./gradlew quarkusDev`, `./gradlew check test`, `./gradlew build`)
   **When** the build is evaluated and compiled
   **Then** `java.sourceCompatibility` and `java.targetCompatibility` are set to `JavaVersion.VERSION_25`, the Gradle Java toolchain requests `JavaLanguageVersion.of(25)` (without restricting vendor so both local OpenJDK 25 and CI Temurin 25 match), and `kotlin.compilerOptions.jvmTarget` targets `JvmTarget.JVM_25`
   **And** no Java 21 (`VERSION_21` or `JVM_21`) fallback remains in `build.gradle.kts`

2. **GitHub Actions CI Workflow on JDK 25 (`.github/workflows/ci.yml`)**
   **Given** `.github/workflows/ci.yml` sets up the JVM for the `test` job (`spotlessCheck`, `lintJs`, `check test`)
   **When** the JDK setup step runs in GitHub Actions
   **Then** the step name is updated from `Set up JDK 21` to `Set up JDK 25` and `with.java-version` is updated from `'21'` to `'25'` (`distribution: 'temurin'`, `cache: gradle`)
   **And** no `JDK 21` or `java-version: '21'` reference remains anywhere in `.github/workflows/ci.yml`

3. **Developer Documentation & Automated DevOps Asset Verification (`README.md`, `DevOpsAssetTest.kt`)**
   **Given** `README.md` documents the tech stack and local development prerequisites, and `DevOpsAssetTest.kt` validates build and CI workflow assets
   **When** `./gradlew check test` runs
   **Then** `README.md` specifies `Java 25` in the tech stack table and `JDK 25` in the prerequisites (replacing `Java 21/25` and `JDK 21+`)
   **And** `DevOpsAssetTest.kt` asserts `Set up JDK 25` and `java-version: '25'` (and `.doesNotContain("Set up JDK 21")`, `.doesNotContain("java-version: '21'")`) in `.github/workflows/ci.yml`, `JavaVersion.VERSION_25`, `JavaLanguageVersion.of(25)`, and `JvmTarget.JVM_25` in `build.gradle.kts`, and `JDK 25` in `README.md`

## Boundaries

- Must not change: Quarkus platform version (`3.40.1.1`), Kotlin version (`2.4.10`), or multi-stage `Dockerfile` (`eclipse-temurin:25-jdk` / `eclipse-temurin:25-jre`).
