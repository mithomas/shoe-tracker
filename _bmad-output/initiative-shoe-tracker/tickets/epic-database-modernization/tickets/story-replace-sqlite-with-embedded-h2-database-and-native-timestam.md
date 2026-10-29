---
id: 1
type: story
title: "Replace SQLite with Embedded H2 Database and Native Timestamps"
parent: epic-database-modernization
covers: [R23]
after: []
hitl: false
risk: low
---

# Replace SQLite with Embedded H2 Database and Native Timestamps

## Description

Replaces `org.xerial:sqlite-jdbc` with `io.quarkus:quarkus-jdbc-h2` running in `MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE`. Migrates `V1__init_sightings.sql` to ANSI/MySQL DDL with `id VARCHAR(36) PRIMARY KEY`, `user_id VARCHAR(255) NOT NULL` (avoiding unlengthed `TEXT` column index failures on MySQL/MariaDB), `latitude DOUBLE NOT NULL`, `longitude DOUBLE NOT NULL`, and native `TIMESTAMP(3)` columns defaulting to `CURRENT_TIMESTAMP(3)`.

Refactors `SightingRepository` from single shared `PreparedStatement` instances under `ReentrantLock` to thread-safe connection checkouts (`dataSource.connection.use`) backed by Agroal statement caching (`quarkus.datasource.jdbc.enable-stm-cache=true`), eliminating parameter corruption risks and thread serialization while preserving `@Startup` and `@PostConstruct fun init()` executing a fail-fast `SELECT 1` connectivity check (maintaining R19 compliance). Binds and extracts `Instant` timestamps using UTC `Calendar` instances (`Calendar.getInstance(TimeZone.getTimeZone("UTC"))`) and aligns timestamp round-trip assertions to `TIMESTAMP(3)` millisecond precision (`ChronoUnit.MILLIS`).

Removes all `org.sqlite.*` imports from `ExceptionMappers.kt`, `TestFaultResource.kt`, and `SightingResourceTest.kt` (replacing them with `java.sql.SQLException` using SQLState `"40001"`) so `./gradlew check test` compiles and passes immediately at the end of Story 6.1.

## Requirements

- Replace `org.xerial:sqlite-jdbc` with `io.quarkus:quarkus-jdbc-h2` in `build.gradle.kts`.
- Configure `application.properties`:
  - Remove `quarkus.datasource.jdbc.driver=org.sqlite.JDBC` and `quarkus.datasource.jdbc.init-sql=PRAGMA journal_mode=WAL; PRAGMA busy_timeout=5000;` (H2 rejects SQLite `PRAGMA` statements).
  - Set:
    ```properties
    quarkus.datasource.db-kind=h2
    quarkus.datasource.jdbc.url=jdbc:h2:file:./data/shoe-tracker;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE
    quarkus.datasource.jdbc.enable-stm-cache=true
    %test.quarkus.datasource.jdbc.url=jdbc:h2:file:./build/test-data/shoe-tracker-test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE
    ```
- Rewrite `V1__init_sightings.sql` to ANSI/MySQL DDL using `id VARCHAR(36) PRIMARY KEY`, `user_id VARCHAR(255) NOT NULL`, `latitude DOUBLE NOT NULL`, `longitude DOUBLE NOT NULL`, and `recorded_at` / `created_at` as `TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)`.
- Update `SightingRepository` to retain `@Startup` and `@PostConstruct fun init()` with a fail-fast `SELECT 1` check via `dataSource.connection.use`, use per-operation connection checkout with UTC `Calendar` timestamp mapping (`setTimestamp` / `getTimestamp`), and remove `writeLock`, `readLock`, and shared `PreparedStatement` fields.
- Replace `org.sqlite.*` imports in `ExceptionMappers.kt`, `TestFaultResource.kt`, and `SightingResourceTest.kt` with `java.sql.SQLException` so the project compiles without `sqlite-jdbc`.
- Update `SightingRepositoryTest` to clean up H2 test files (`shoe-tracker-test.mv.db` / `.trace.db`), assert `@PostConstruct` startup validation and concurrent read/write safety without locks, and compare persisted timestamps at millisecond precision (`ChronoUnit.MILLIS`).
- Verify: Quarkus boots cleanly with H2, Flyway executes V1 DDL with `TIMESTAMP(3)`, `SightingRepository` performs concurrent CRUD operations without in-memory locks, zero `org.sqlite` references remain, and `./gradlew check test` passes.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-database-modernization/epic-database-modernization.md
- architecture - _bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md
