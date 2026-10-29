---
id: 4
type: story
title: "GraalVM Native Image Compilation with Dockerfile.native"
parent: epic-database-modernization
covers: [R27]
after: [1, 2, 3]
hitl: false
risk: medium
---

# GraalVM Native Image Compilation with Dockerfile.native

## Description

Configures Quarkus GraalVM native image compilation using in-container Mandrel tooling (`-Dquarkus.native.enabled=true -Dquarkus.native.container-build=true`) and authors a minimal `src/main/docker/Dockerfile.native` based on `registry.access.redhat.com/ubi9/ubi-minimal`.

Annotates all REST DTOs in `SightingDtos.kt` (`CreateSightingRequest`, `LocationDto`, `UserDto`, `SightingResponse`, `SightingListResponse`, `ProblemDetails`) and `VersionResource.kt` (`VersionResponse`) with `@io.quarkus.runtime.annotations.RegisterForReflection` so Jackson serialization works in GraalVM native mode even when returned via erased `Response.entity(...)` types in `ExceptionMappers.kt`.

Note on native datasource target: Quarkus's `io.quarkus:quarkus-jdbc-h2` extension includes a GraalVM substitution (`io.quarkus.jdbc.h2.runtime.graal.Engine`) that disables embedded H2 (`jdbc:h2:file:...`) in native executables; therefore, native image runtime verification targets an external MariaDB/MySQL datasource (`QUARKUS_DATASOURCE_JDBC_URL=jdbc:mariadb://...`) or remote H2 TCP server (`jdbc:h2:tcp://...`), confirming sub-50ms startup time and full CRUD + RFC 7807 parity.

## Requirements

- Annotate all DTO classes in `SightingDtos.kt` and `VersionResource.kt` with `@RegisterForReflection`.
- Configure Gradle native build properties supporting containerized Mandrel compilation (`./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true`).
- Create `src/main/docker/Dockerfile.native` packaging the compiled native executable with non-root user (UID 185) and `/data` directory permissions, and add asset assertions in `DevOpsAssetTest.kt`.
- Verify native binary startup time is under 50ms.
- Verify `/api/v1/version`, `/q/health/live`, `/q/health/ready`, RFC 7807 error responses (400/500/503), and all REST CRUD operations execute identically in native binary mode against MariaDB/MySQL (or remote H2 TCP).
- Verify: Quarkus native binary compiles successfully, DTO reflection serialization works for 201/200/400/500/503 responses, and native container starts in under 50 milliseconds against external MariaDB/MySQL (or remote H2 TCP).

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-database-modernization/epic-database-modernization.md
- architecture - _bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md
