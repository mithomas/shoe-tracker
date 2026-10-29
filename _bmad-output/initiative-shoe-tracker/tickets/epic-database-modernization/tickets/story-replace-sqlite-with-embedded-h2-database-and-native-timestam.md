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

Replaces `sqlite-jdbc` with `io.quarkus:quarkus-jdbc-h2` running in `MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE`. Migrates `V1__init_sightings.sql` to ANSI/MySQL DDL with `VARCHAR(36) PRIMARY KEY` and native `TIMESTAMP(3)` columns defaulting to `CURRENT_TIMESTAMP(3)`.

Refactors `SightingRepository` from single shared `PreparedStatement` instances under `ReentrantLock` to thread-safe connection checkouts (`dataSource.connection.use`) backed by Agroal statement caching (`quarkus.datasource.jdbc.enable-stm-cache=true`), eliminating parameter corruption risks and thread serialization. Binds and extracts `Instant` timestamps using UTC `Calendar` instances (`Calendar.getInstance(TimeZone.getTimeZone("UTC"))`).

## Requirements

- Replace `sqlite-jdbc` with `io.quarkus:quarkus-jdbc-h2` in `build.gradle.kts`.
- Configure `application.properties` with:
  ```properties
  quarkus.datasource.db-kind=h2
  quarkus.datasource.jdbc.url=jdbc:h2:file:./data/shoe-tracker;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE
  quarkus.datasource.jdbc.enable-stm-cache=true
  ```
- Rewrite `V1__init_sightings.sql` to ANSI/MySQL DDL using `VARCHAR(36) PRIMARY KEY` and `TIMESTAMP(3)`.
- Update `SightingRepository` to use per-operation connection checkout with UTC calendar timestamp mapping, removing `writeLock` and `readLock`.
- Verify: Quarkus boots cleanly with H2, Flyway executes V1 DDL with `TIMESTAMP(3)`, `SightingRepository` performs concurrent CRUD operations without in-memory locks, and all tests pass.

## References

- parent — _bmad-output/initiative-shoe-tracker/tickets/epic-database-modernization/epic-database-modernization.md
