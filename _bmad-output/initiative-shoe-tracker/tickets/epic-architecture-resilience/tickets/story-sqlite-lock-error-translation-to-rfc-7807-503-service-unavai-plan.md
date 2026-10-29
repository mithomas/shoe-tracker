---
title: 'SQLite Lock Error Translation to RFC 7807 503 Service Unavailable'
type: 'feature'
ticket: '3'
created: '2026-10-29'
status: 'built'
baseline_revision: '1d6296068bc866c10eb30dae5124119859f77647'
route: 'oneshot'
route_source: 'auto'
risk: 'low'
review: 'quick'
review_source: 'pinned'
lenses_ran:
  - 'quick'
review_loop_iteration: 0
context:
  - '_bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md'
  - '_bmad-output/initiative-shoe-tracker/tickets/epic-architecture-resilience/epic-architecture-resilience.md'
---

<frozen-after-approval reason="human-owned intent - do not modify unless human renegotiates">

## Intent

**Problem:** When SQLite encounters write lock contention, concurrency timeouts, or busy database files (`SQLITE_BUSY`, `SQLITE_LOCKED`), it throws `org.sqlite.SQLiteException`. Without specialized handling, these transient concurrency events result in generic 500 errors, giving clients no retry guidance and misrepresenting transient database load as a permanent server failure.

**Approach:**
1. Implement `@Provider class SQLiteExceptionMapper : ExceptionMapper<SQLiteException>` in `ExceptionMappers.kt`.
2. Inspect `exception.resultCode`: if it represents lock or busy contention (`SQLITE_BUSY*` or `SQLITE_LOCKED*`), return HTTP 503 `Service Unavailable` with `Retry-After: 1` and RFC 7807 `application/problem+json` payload.
3. For non-transient SQLite errors (e.g. `SQLITE_CORRUPT`, `SQLITE_FULL`), return HTTP 500 `Internal Server Error` with UUID `error_id` and sanitized detail.
4. Add automated unit and fault-injection tests verifying HTTP 503, `Retry-After: 1` header, and RFC 7807 problem structure.

</frozen-after-approval>

## Implementation Notes

Oneshot route selected: Adding `SQLiteExceptionMapper` in `ExceptionMappers.kt` with transient contention translation to HTTP 503.
- Implemented `SQLiteExceptionMapper` handling `org.sqlite.SQLiteException`.
- Contention error codes (`SQLITE_BUSY`, `SQLITE_LOCKED`, and sub-codes) produce HTTP 503 with header `Retry-After: 1`.
- Non-contention SQLite errors map to HTTP 500 with sanitized details.
- Extended `TestFaultResource` with `/api/test/fault/sqlite-busy` and `/api/test/fault/sqlite-error`.
- Added unit and REST integration tests in `SightingResourceTest.kt`.

## Plan Change Log

## Review Triage Log

| Lens | Finding | Verdict | Route | Evidence / Rationale |
|---|---|---|---|---|
| quick | Subcode matching for busy and locked | pass | - | Matched all `SQLITE_BUSY*` and `SQLITE_LOCKED*` variants via name prefix inspection. |
| quick | Retry-After header presence | pass | - | Verified `Retry-After: 1` header in both unit and HTTP integration assertions. |

## Verification

**Commands:**
- `./gradlew test --tests org.shoetracker.api.SightingResourceTest` -- expected: Tests pass verifying HTTP 503 on SQLITE_BUSY.
- `./gradlew check test spotlessCheck lintJs` -- expected: Full test and hygiene suite passes with zero violations.
