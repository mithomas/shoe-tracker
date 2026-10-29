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

Replaces sqlite-jdbc with quarkus-jdbc-h2 (MODE=MySQL), migrates Flyway DDL to ANSI/MySQL with native TIMESTAMP(3) columns, and removes ReentrantLock thread serialization from SightingRepository.

## Requirements

Verify: Quarkus boots cleanly with H2, Flyway executes V1 DDL with TIMESTAMP(3), SightingRepository performs concurrent CRUD operations without in-memory locks, and all tests pass.

## References

- parent — _bmad-output/initiative-shoe-tracker/tickets/epic-database-modernization/epic-database-modernization.md
