---
type: story
title: "Eliminate Lazy Initialization of Prepared Statements in SightingRepository"
parent: ""
covers: []
sources: []
companions: []
after: []
risk: low
refined: false
---

# Eliminate Lazy Initialization of Prepared Statements in SightingRepository

## Description

Replaces lazy property initialization (`by lazy`) of the JDBC connection and PreparedStatements in `SightingRepository.kt` with eager fail-fast initialization. Ensures any SQL syntax or connection errors fail during application bootstrap rather than at first user request, and prevents uninitialized lazy statements from triggering on-demand compilation during shutdown.

## Background & Rationale

Currently, `SightingRepository` uses `by lazy` delegates for its connection and compiled statements (`insertStmt`, `findWithUserStmt`, `findAllStmt`). This introduces two issues:
1. Cold-start latency: The very first user request pays the penalty of opening the connection and compiling SQL statements.
2. Shutdown compilation: In `@PreDestroy close()`, accessing an uninitialized lazy property forces on-demand SQL compilation just before closing it.
Eagerly preparing statements during `@PostConstruct` (or managing statements cleanly within connection checkout blocks) guarantees fail-fast validation and clean resource teardown.

## Acceptance Criteria

- Remove `by lazy` initialization of connection and prepared statements in `SightingRepository.kt`.
- Prepared statements are eagerly created and validated during bean initialization (or prepared per connection checkout).
- Application startup fails fast if database connection or SQL syntax is invalid.
- `@PreDestroy close()` only closes active resources without triggering lazy on-demand compilation during shutdown.
- All database integration tests pass cleanly.
