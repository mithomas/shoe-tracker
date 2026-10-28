---
type: story
title: "Replace SQLite with Embedded H2 Database"
parent: ""
covers: []
sources: []
companions: []
after: []
risk: low
refined: false
---

# Replace SQLite with Embedded H2 Database

## Description

Replaces the embedded SQLite C/JNI database with a pure-Java embedded H2 database (`quarkus-jdbc-h2`) operating in `MODE=MySQL`. This eliminates in-process SQLite write locks, enables Quarkus GraalVM native image compilation, and maintains single-container zero-configuration file persistence in `/data`.

## Background & Rationale

SQLite currently powers Shoe Tracker via native C shared libraries (`sqlite-jdbc`). However, SQLite's single-writer architecture requires in-process serialization locks (`ReentrantLock`) and its JNI bridge complicates GraalVM native compilation. Moving to H2 in `MODE=MySQL` preserves zero-config single-container file storage, provides multi-threaded row-level concurrency, and paves the way for direct GraalVM native compilation and external MariaDB support.

## Acceptance Criteria

- Replace `org.xerial:sqlite-jdbc` with `io.quarkus:quarkus-jdbc-h2` in `build.gradle.kts`.
- Configure `application.properties` with:
  ```properties
  quarkus.datasource.db-kind=h2
  quarkus.datasource.jdbc.url=jdbc:h2:file:/data/shoe-tracker;MODE=MySQL;DATABASE_TO_LOWER=TRUE
  ```
- Update Flyway schema migration (`V1__init_sightings.sql`) to clean MySQL/ANSI DDL supported by H2.
- Remove `writeLock` (`ReentrantLock`) serialization in `SightingRepository.kt`, letting H2 manage internal concurrency.
- All integration tests pass cleanly against embedded H2.
