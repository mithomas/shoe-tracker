---
type: story
title: "Quarkus GraalVM Native Image Compilation"
parent: ""
covers: []
sources: []
companions: []
after: ["story-replace-sqlite-with-h2.md"]
risk: medium
refined: false
---

# Quarkus GraalVM Native Image Compilation

## Description

Compiles the Shoe Tracker application into a standalone GraalVM native Linux binary using Quarkus Native Image tooling. This delivers sub-50ms container cold starts and reduces container memory footprint to under 30MB with zero JVM dependencies.

## Background & Rationale

Previously blocked by SQLite's JNI native C library (`sqlite-jdbc`), native compilation becomes fully viable once the database is migrated to MySQL, which communicates purely over standard Java network sockets.

## Acceptance Criteria

- Configure `./gradlew build -Dquarkus.native.enabled=true` in build pipeline.
- Author `src/main/docker/Dockerfile.native` based on `scratch` or `registry.access.redhat.com/ubi9/ubi-minimal`.
- Verify container startup in under 50 milliseconds.
- Verify container image size is reduced to under 60MB.
- Ensure all REST endpoints and static web assets function identically in native mode.
