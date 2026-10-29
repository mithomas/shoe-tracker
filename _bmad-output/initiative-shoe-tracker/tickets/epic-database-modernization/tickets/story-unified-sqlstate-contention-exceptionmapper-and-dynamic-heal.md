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

Hardens `DatabaseContentionExceptionMapper` (replacing `SQLiteExceptionMapper` in `ExceptionMappers.kt`) handling `java.sql.SQLException` across all supported database engines. Inspects the full causal `SQLException` chain (`cause` and `nextException`) and translates ANSI SQLState contention/timeout codes (`40001` serialization failure, `40P01` deadlock, `57P01` admin shutdown, `HYT00` timeout), H2 lock timeout error codes (`50200`, `90039`, `90072`), and MariaDB/MySQL lock wait timeout and deadlock error codes (`1205`, `1213`) into RFC 7807 `503 Service Unavailable` with `Retry-After: 1` header. Non-contention database errors are mapped to sanitized RFC 7807 `500 Internal Server Error` responses with a correlating UUID `error_id`.

Updates `DatabaseHealthCheck` in `org.shoetracker.health` to rename the readiness check from `"SQLite database health check"` to `"Database connection health check"` and dynamically report the live database product name via `conn.metaData?.databaseProductName ?: "Unknown"` (`withData("database", productName)`), ensuring accurate readiness probe data across H2 and MariaDB/MySQL.

## Requirements

- Ensure `DatabaseContentionExceptionMapper` in `ExceptionMappers.kt` walks the causal `SQLException` chain and maps ANSI SQLStates (`40001`, `40P01`, `57P01`, `HYT00`), H2 error codes (`50200`, `90039`, `90072`), and MariaDB/MySQL error codes (`1205`, `1213`) to HTTP 503 with `Retry-After: 1` and RFC 7807 `application/problem+json`.
- Map non-contention `SQLException` instances to sanitized HTTP 500 RFC 7807 responses with UUID `error_id`.
- Update `DatabaseHealthCheck` to use check name `"Database connection health check"` and report `withData("database", conn.metaData?.databaseProductName ?: "Unknown")`.
- Update `HealthResourceTest`, `SightingResourceTest`, and `TestFaultResource` to assert dynamic `"database"` product name (`"H2"`) in `/q/health/ready`, 503 mapping across SQLState (`40001`), H2 (`50200`), and MariaDB (`1205`) contention codes, and 500 mapping for non-contention `SQLException`s.
- Verify: Simulated lock contention (including wrapped `SQLException` and MariaDB/H2 vendor codes) returns HTTP 503 with `Retry-After: 1` header, and `/q/health/ready` dynamically reports `databaseProductName`.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-database-modernization/epic-database-modernization.md
- architecture - _bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md
