---
id: 4
type: story
title: "GraalVM Native Image Compilation with Dockerfile.native"
parent: epic-database-modernization
covers: [R27]
after: [1]
hitl: false
risk: medium
---

# GraalVM Native Image Compilation with Dockerfile.native

## Description

Configures Quarkus GraalVM native image compilation using in-container Mandrel tooling (`-Dquarkus.native.enabled=true -Dquarkus.native.container-build=true`), authoring a minimal `src/main/docker/Dockerfile.native` based on `registry.access.redhat.com/ubi9/ubi-minimal`. Validates that the standalone binary starts in under 50 milliseconds and reduces container memory footprint below 35MB.

## Requirements

- Configure Gradle native build task supporting containerized compilation: `./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true`.
- Create `src/main/docker/Dockerfile.native` packaging the compiled native executable with non-root user (UID 185) and write permissions on `/data`.
- Verify native binary startup time is under 50ms.
- Verify `/api/v1/version`, `/q/health/live`, `/q/health/ready`, and all REST CRUD operations execute identically in native binary mode.
- Verify: Quarkus native binary compiles successfully and container starts in under 50 milliseconds.

## References

- parent — _bmad-output/initiative-shoe-tracker/tickets/epic-database-modernization/epic-database-modernization.md
