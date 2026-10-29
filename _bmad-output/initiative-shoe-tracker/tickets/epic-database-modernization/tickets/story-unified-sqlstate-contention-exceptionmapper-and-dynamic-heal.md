---
id: 2
type: story
title: "Unified SQLState Contention ExceptionMapper and Dynamic Health Reporting"
parent: epic-database-modernization
covers: [R24, R25]
after: [1]
hitl: false
risk: low
---

# Unified SQLState Contention ExceptionMapper and Dynamic Health Reporting

## Description

Replaces `SQLiteExceptionMapper` with `DatabaseContentionExceptionMapper` handling generic `java.sql.SQLException`. Translates ANSI SQLState contention codes (e.g. `40001` serialization failure, `40P01` deadlock, `57P01`) and H2 lock timeout error codes (`90039`, `90072`, `50200`) into RFC 7807 `503 Service Unavailable` with `Retry-After: 1` header. Non-contention database errors are mapped to sanitized RFC 7807 500 errors with UUID `error_id`.

Updates `DatabaseHealthCheck` in `org.shoetracker.health` to dynamically report the live database product name via `conn.metaData?.databaseProductName` with safe fallback, ensuring accurate readiness probe data across H2 and MariaDB.

## Requirements

- Remove `SQLiteExceptionMapper` and register `DatabaseContentionExceptionMapper` handling `SQLException`.
- Map SQLState `40001`, `40P01`, `57P01` and vendor error codes for lock timeouts to HTTP 503 with `Retry-After: 1`.
- Update `DatabaseHealthCheck` to extract `conn.metaData.databaseProductName` and report it in the readiness check data.
- Update `HealthResourceTest` and `SightingResourceTest` to assert dynamic health output and SQLState contention mapping.
- Verify: Simulated lock contention returns HTTP 503 with `Retry-After: 1` header, and `/q/health/ready` dynamically reports `databaseProductName`.

## References

- parent — _bmad-output/initiative-shoe-tracker/tickets/epic-database-modernization/epic-database-modernization.md
