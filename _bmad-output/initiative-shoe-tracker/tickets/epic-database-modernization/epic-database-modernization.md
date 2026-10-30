---
type: epic
title: "Database Modernization & GraalVM Native Image"
parent: "initiative-shoe-tracker"
covers: ["R23", "R24", "R25", "R26", "R27"]
sources: ["_bmad-output/initiative-shoe-tracker/initiative-shoe-tracker.md"]
companions: ["_bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md"]
after: ["epic-architecture-resilience"]
refined: true
risk: medium
status: "done"
---

# Database Modernization & GraalVM Native Image

## Description

Retires SQLite in favor of a pure-Java embedded H2 database (`MODE=MySQL`) with native `TIMESTAMP(3)` columns and multi-threaded lock-free concurrency. Unifies database error translation using ANSI SQLState and vendor lock-timeout codes to return RFC 7807 503 on lock contention. Enables dynamic database health reporting, bundles both H2 and MariaDB/MySQL JDBC drivers internally so a single `QUARKUS_DATASOURCE_JDBC_URL` environment variable switches automatically at startup between embedded H2 file persistence (`/data`) and external MariaDB/MySQL based on the JDBC URL prefix (`jdbc:h2:file:...` vs `jdbc:mariadb://...`), and compiles the application into a standalone GraalVM native binary achieving sub-50ms cold starts.

## Outcome

Shoe Tracker is free of SQLite native C/JNI dependencies, supports native millisecond timestamp precision, runs embedded H2 with high-concurrency MVCC row locking by default, switches automatically at startup between embedded H2 file persistence and external MariaDB/MySQL based on a single JDBC URL string, and compiles into a minimal GraalVM native binary.

## Requirements

- R23: Embedded H2 database (`io.quarkus:quarkus-jdbc-h2`) replaces `sqlite-jdbc` with native `TIMESTAMP(3)` Flyway DDL, removing `writeLock` serialization from `SightingRepository` while preserving `@Startup` `@PostConstruct` fail-fast `SELECT 1` validation.
- R24: Unified `DatabaseContentionExceptionMapper` replaces `SQLiteExceptionMapper`, translating ANSI SQLState contention codes (`40001`, `40P01`, `57P01`, `HYT00`), H2 lock codes (`50200`, `90039`, `90072`), and MariaDB/MySQL lock/deadlock codes (`1205`, `1213`) across causal `SQLException` chains into RFC 7807 `503 Service Unavailable` with `Retry-After: 1` header across all database kinds.
- R25: `DatabaseHealthCheck` dynamically reports the connected database product name (`connection.metaData.databaseProductName`) in `/q/health/ready` under check name `"Database connection health check"`.
- R26: Unified single-JDBC-URL datasource configuration bundles both H2 (`quarkus-jdbc-h2`) and MariaDB/MySQL (`quarkus-jdbc-mariadb`) drivers internally and switches automatically at startup based on the driver prefix in `QUARKUS_DATASOURCE_JDBC_URL` (`jdbc:h2:file:...` for default embedded H2 file persistence in `/data` vs `jdbc:mariadb://...` / `jdbc:mysql://...` for external MariaDB/MySQL).
- R27: Quarkus GraalVM native image compilation builds a standalone Linux binary using in-container Mandrel tooling with `src/main/docker/Dockerfile.native` and `@RegisterForReflection` on REST DTOs, achieving sub-50ms cold starts.

## Done when

- `io.quarkus:quarkus-jdbc-h2` and `io.quarkus:quarkus-jdbc-mariadb` are bundled in dependencies, with `org.xerial:sqlite-jdbc` and all `org.sqlite.*` references completely removed.
- `V1__init_sightings.sql` uses ANSI/MySQL DDL with `id VARCHAR(36) PRIMARY KEY`, `user_id VARCHAR(255) NOT NULL`, `latitude DOUBLE NOT NULL`, `longitude DOUBLE NOT NULL`, and `TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)`.
- `SightingRepository` manages concurrent queries without `ReentrantLock` in-memory thread serialization while validating connectivity fail-fast at `@Startup` `@PostConstruct`.
- Database lock contention returns HTTP 503 `Retry-After: 1` via SQLState and vendor error code translation across causal exception chains.
- Application boots and switches automatically at startup based solely on the single `QUARKUS_DATASOURCE_JDBC_URL` string (`jdbc:h2:file:...` vs `jdbc:mariadb://...`), and `Dockerfile` / `docker-compose.yml` / `README.md` reflect the H2 default and single-URL override.
- Standalone GraalVM native binary compiles with `@RegisterForReflection` on DTOs and starts in under 50ms.

## Boundaries

- Existing SQLite `.db` files on disk are ignored; fresh H2 or MariaDB/MySQL schema is initialized at boot.
- H2 in-memory mode is not a supported user-facing deployment target; only embedded H2 file persistence and external MariaDB/MySQL are supported.
- Frontend UI components and localization belong to Epic 3.

## References

- parent - _bmad-output/initiative-shoe-tracker/initiative-shoe-tracker.md
- architecture - _bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md

## Notes

- Decision: 2026-10-29: Fresh start for H2 database; SQLite is retired without data migration scripts.
- Decision: 2026-10-29: Native `TIMESTAMP(3)` columns with `CURRENT_TIMESTAMP(3)` in Flyway migration, plus `VARCHAR(255)` for indexed `user_id` to satisfy MySQL/MariaDB B-tree index length constraints.
- Decision: 2026-10-29: `SQLiteExceptionMapper` replaced with unified `DatabaseContentionExceptionMapper` inspecting causal `SQLException` chains for ANSI SQLState codes (`40001`, `40P01`, `57P01`, `HYT00`), H2 lock codes (`50200`, `90039`, `90072`), and MariaDB/MySQL lock codes (`1205`, `1213`).
- Decision: 2026-10-29: Drop H2 in-memory mode as a user-facing runtime target. Both H2 (`quarkus-jdbc-h2`) and MariaDB/MySQL (`quarkus-jdbc-mariadb`) drivers are bundled internally and switched automatically at startup based solely on the driver prefix in the single `QUARKUS_DATASOURCE_JDBC_URL` string (`jdbc:h2:file:...` vs `jdbc:mariadb://...`).
- Decision: 2026-10-29: GraalVM native compilation configured via in-container Mandrel build with `Dockerfile.native` and `@RegisterForReflection` on REST DTOs; native executable verification targets external MariaDB/MySQL (or remote H2 TCP) because Quarkus `quarkus-jdbc-h2` disables embedded H2 in GraalVM native mode.
- Decision: 2026-10-29: Story 6.1 is the tracer bullet establishing H2 file persistence, ANSI/MySQL Flyway DDL, lock-free `SightingRepository`, and removal of `org.sqlite.*` imports so the build stays green at every step; Stories `6.1 -> 6.2 -> 6.3 -> 6.4 -> 6.5` execute sequentially in a single lane to prevent file edit collisions on `build.gradle.kts`, `application.properties`, and test suites.
