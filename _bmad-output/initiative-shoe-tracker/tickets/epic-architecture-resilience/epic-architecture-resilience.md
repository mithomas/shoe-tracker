---
type: epic
title: "Architecture Hardening & Offline Resilience"
parent: "initiative-shoe-tracker"
covers: ["R18", "R19", "R20", "R21"]
sources: []
companions: []
after: ["epic-open-source-devops"]
refined: true
risk: low
---

# Architecture Hardening & Offline Resilience

## Description

Hardens system resilience against unhandled runtime errors, database contention, and container lifecycle failures. Adds a global RFC 7807 500 error mapper with UUID error correlation and sanitized production details, converts `SightingRepository` to eager `@PostConstruct` statement compilation with startup fail-fast validation, translates SQLite lock timeouts (`SQLITE_BUSY`) into RFC 7807 503 Service Unavailable with `Retry-After` headers, and integrates SmallRye Health liveness and readiness probes (`/q/health`) for container orchestration.

## Outcome

Shoe Tracker is hardened against edge-case failures, unifies all server errors under RFC 7807 problem details with traceable error IDs, validates database connectivity and statements fail-fast at boot, protects clients with retryable 503 responses on DB locks, and provides standard Kubernetes/container health probes.

## Requirements

- R18: Catch-all RFC 7807 `ExceptionMapper<Throwable>` returns 500 `application/problem+json` with unique UUID `error_id`, logging stack traces internally and returning sanitized details to clients.
- R19: `SightingRepository` eagerly initializes its dedicated JDBC connection and compiled prepared statements during `@PostConstruct` with fail-fast `SELECT 1` startup validation.
- R20: `SQLiteExceptionMapper` translates `org.sqlite.SQLiteException` (such as `SQLITE_BUSY` lock contention) into RFC 7807 `503 Service Unavailable` with `Retry-After: 1` header.
- R21: Quarkus SmallRye Health exposes `/q/health/live` and `/q/health/ready` endpoints verifying application liveness and SQLite database connectivity.

## Done when

- Catch-all `ExceptionMapper<Throwable>` guarantees unhandled 500 exceptions produce RFC 7807 `application/problem+json` with correlation `error_id` and sanitized detail.
- `SightingRepository` compiles prepared statements at bean initialization without `by lazy` delegates, failing fast if the database or queries are invalid.
- Tapping API during forced write contention returns HTTP 503 with standard RFC 7807 problem payload and `Retry-After: 1`.
- `/q/health/live` returns HTTP 200 UP and `/q/health/ready` validates SQLite connection health.

## Boundaries

- Multi-database drivers (MySQL/MariaDB) and GraalVM native image compilation belong to Epic 6.
- Leaflet high-DPI retina assets (`marker-icon-2x.png`) and offline map tile notices are scheduled for Epic 3.

## References

- parent - _bmad-output/initiative-shoe-tracker/initiative-shoe-tracker.md
- architecture - _bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md

## Notes

- Decision: 2026-10-29: Option B adopted for connection lifecycle: dedicated connection with eager `@PostConstruct` statement compilation and fail-fast `SELECT 1`.
- Decision: 2026-10-29: Production 500 error details are sanitized, while full stack trace is logged with correlating UUID `error_id`.
- Decision: 2026-10-29: Leaflet local vendoring and DOM CustomEvents pub/sub confirmed already implemented in Epic 2.
- Decision: 2026-10-29: Candidate 3 (Leaflet high-DPI assets & offline tile notice) transferred to Epic 3 (`epic-spotting-polish`).
