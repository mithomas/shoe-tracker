---
type: story
title: "Database Migration from SQLite to MySQL"
parent: ""
covers: []
sources: []
companions: []
after: []
risk: low
refined: false
---

# Database Migration from SQLite to MySQL

## Description

Migrates the Shoe Tracker backend persistence from single-file embedded SQLite to an external, client-server MySQL (or MariaDB) database. This enables concurrent multi-writer scaling, connection pooling without local filesystem locks, and unblocks ahead-of-time native binary compilation.

## Background & Rationale

SQLite currently provides zero-config embedded persistence for MVP. However, SQLite relies on native C shared libraries via JNI and requires in-process serialization locks for writes. Migrating to MySQL decouples the database from the application container filesystem, supports standard network transactions, and eliminates native JNI barriers.

## Acceptance Criteria

- Add `quarkus-jdbc-mysql` dependency to `build.gradle.kts`.
- Adapt Flyway schema migrations (`V1__init_sightings.sql`) for MySQL syntax (e.g., `VARCHAR(36)`, `DATETIME(3)` / `TIMESTAMP(3)`).
- Update DataSource configuration in `application.properties` to connect via JDBC URL with credentials from environment variables (`QUARKUS_DATASOURCE_JDBC_URL`, `QUARKUS_DATASOURCE_USERNAME`, `QUARKUS_DATASOURCE_PASSWORD`).
- Remove SQLite write locks and file-system volume mount dependencies from the Quarkus application.
- All integration tests pass against MySQL (or Testcontainers MySQL container).
