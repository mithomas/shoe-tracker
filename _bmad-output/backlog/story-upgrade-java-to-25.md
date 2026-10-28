---
type: story
title: "Upgrade Toolchain and Bytecode Target to Java 25"
parent: ""
covers: []
sources: []
companions: []
after: []
risk: low
refined: false
---

# Upgrade Toolchain and Bytecode Target to Java 25

## Description

Upgrades the project's build toolchain, Kotlin JVM target, and bytecode compilation configuration from Java 21 to Java 25. Aligns the local Gradle build with the container runtime (`eclipse-temurin:25`) and enables language and runtime features introduced in Java 22 through 25.

## Background & Rationale

Shoe Tracker's production `Dockerfile` already builds and runs on Eclipse Temurin 25. However, `build.gradle.kts` was temporarily pinned to `JavaVersion.VERSION_21` and `JvmTarget.JVM_21` for local developer machine fallback. With Debian 13 (Trixie) offering official `openjdk-25-jdk` packages and modern toolchains readily available, upgrading the Gradle build directly targets Java 25.

## Acceptance Criteria

- Update `build.gradle.kts`:
  - `sourceCompatibility = JavaVersion.VERSION_25`
  - `targetCompatibility = JavaVersion.VERSION_25`
  - `kotlin { compilerOptions { jvmTarget = JvmTarget.JVM_25 } }`
  - Configure Gradle Java toolchain to target Java 25:
    ```kotlin
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }
    ```
- All automated unit, integration, and frontend asset tests pass cleanly under Java 25 (`./gradlew check test`).
- Document installation instructions for Debian 13 (Trixie): `sudo apt install openjdk-25-jdk`.
- Multi-stage Docker container build compiles and runs on Java 25 with zero warnings.
