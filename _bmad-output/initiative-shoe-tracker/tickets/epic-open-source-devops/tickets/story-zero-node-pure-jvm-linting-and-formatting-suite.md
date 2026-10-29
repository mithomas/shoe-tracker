---
id: 7
type: story
title: "Zero-Node Pure-JVM Linting and Formatting Suite"
parent: epic-open-source-devops
covers: [R16]
after: [8]
hitl: false
risk: low
---

# Zero-Node Pure-JVM Linting and Formatting Suite

## Description

Adds Spotless Gradle plugin with ktlint for Kotlin source and *.gradle.kts, pure-JVM web formatting for HTML/CSS/JS, and pure-JVM JavaScript static analysis via Google Closure Compiler into ./gradlew check.

## Requirements

Verify: ./gradlew check, ./gradlew spotlessCheck, and lint tasks pass cleanly with zero Node.js dependencies.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-open-source-devops/epic-open-source-devops.md
