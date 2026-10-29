---
type: epic
title: "Database Modernization & GraalVM Native Image"
parent: "initiative-shoe-tracker"
covers: ["R23", "R24", "R25", "R26", "R27"]
sources: []
companions: []
after: ["epic-architecture-resilience"]
refined: true
risk: medium
---

# Database Modernization & GraalVM Native Image

## Description

Retires SQLite in favor of a pure-Java embedded H2 database (`MODE=MySQL`) with native `TIMESTAMP(3)` columns and multi-threaded lock-free concurrency. Unifies database error translation using ANSI SQLState codes to return RFC 7807 503 on lock contention. Enables dynamic database health reporting, allows single-variable switching between external MySQL/MariaDB, H2 file persistence (`/data`), or H2 in-memory via `QUARKUS_DATASOURCE_*`, and compiles the application into a standalone GraalVM native binary achieving sub-50ms cold starts.

## Outcome

Shoe Tracker is free of SQLite native C/JNI dependencies, supports native timestamp precision, runs embedded H2 with high-concurrency row locking by default, allows instant switching to external MySQL or in-memory mode via runtime environment variables, and compiles into a minimal GraalVM native binary.

## Requirements

- R23: Embedded H2 database (`io.quarkus:quarkus-jdbc-h2`) replaces `sqlite-jdbc` with native `TIMESTAMP(3)` Flyway DDL, removing `writeLock` serialization from `SightingRepository`.
- R24: Unified `DatabaseContentionExceptionMapper` replaces `SQLiteExceptionMapper`, translating ANSI SQLState contention codes (`40001`, `57P01`) into RFC 7807 `503 Service Unavailable` with `Retry-After: 1` header across all database kinds.
- R25: `DatabaseHealthCheck` dynamically reports the connected database product name (`connection.metaData.databaseProductName`) in `/q/health/ready`.
- R26: Unified datasource environment configuration supports selecting external MySQL/MariaDB, embedded H2 file persistence (`/data`), or embedded H2 in-memory via `QUARKUS_DATASOURCE_*` settings.
- R27: Quarkus GraalVM native image compilation builds a standalone Linux binary using in-container Mandrel tooling with `src/main/docker/Dockerfile.native` achieving sub-50ms cold starts.

## Done when

- `io.quarkus:quarkus-jdbc-h2` and `io.quarkus:quarkus-jdbc-mariadb` are available in dependencies, with SQLite completely removed.
- `V1__init_sightings.sql` uses ANSI/MySQL DDL with `VARCHAR(36) PRIMARY KEY` and `TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3)`.
- `SightingRepository` manages concurrent queries without `ReentrantLock` in-memory thread serialization.
- Database lock contention returns HTTP 503 `Retry-After: 1` via SQLState translation.
- Application boots successfully with default H2 file, in-memory H2, and simulated external MySQL/MariaDB configuration.
- Standalone GraalVM native binary compiles and starts in under 50ms.

## Boundaries

- Existing SQLite `.db` files on disk are ignored; fresh H2 schema is initialized at boot.
- Frontend UI components and localization belong to Epic 3.

## References

- parent - _bmad-output/initiative-shoe-tracker/initiative-shoe-tracker.md
- architecture - _bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md

## Notes

- Decision: 2026-10-29: Fresh start for H2 database; SQLite is retired without data migration scripts.
- Decision: 2026-10-29: Native `TIMESTAMP(3)` columns with `CURRENT_TIMESTAMP(3)` in Flyway migration.
- Decision: 2026-10-29: `SQLiteExceptionMapper` replaced with unified `DatabaseContentionExceptionMapper` inspecting ANSI SQLState codes (`40001`, `57P01`).
- Decision: 2026-10-29: Runtime environment variables allow selecting external MySQL/MariaDB, H2 file persistence, or H2 in-memory.
- Decision: 2026-10-29: GraalVM native compilation configured via in-container Mandrel build with `Dockerfile.native`.
