---
type: epic
title: "Database Modernization & GraalVM Native Image"
parent: "initiative-shoe-tracker"
covers: []
sources: []
companions: []
after: ["epic-architecture-resilience"]
refined: false
risk: low
---

# Database Modernization & GraalVM Native Image

## Description

Replaces SQLite with pure-Java embedded H2 database (`MODE=MySQL`) with zero in-process write locks, enables a configurable external MariaDB/MySQL datasource with embedded H2 fallback, and compiles the application into a standalone GraalVM native binary.

## Outcome

Shoe Tracker achieves sub-50ms cold starts, eliminates SQLite JNI write locks, runs with embedded H2 out of the box, and supports external production MariaDB instances via environment variables.

## Done when

- Embedded H2 database (`io.quarkus:quarkus-jdbc-h2`) replaces `sqlite-jdbc` with zero in-process write locks.
- Flyway migrations run cleanly in ANSI/MySQL syntax on both H2 and MariaDB.
- External MariaDB/MySQL datasource is configurable via standard Quarkus environment variables with H2 as default fallback.
- Application compiles into a GraalVM native image starting in under 50 milliseconds.

## Boundaries

- Sighting exploration UI belongs to Epic 2; UX polish to Epic 3.

## References

- parent - _bmad-output/initiative-shoe-tracker/initiative-shoe-tracker.md
- architecture - _bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md
