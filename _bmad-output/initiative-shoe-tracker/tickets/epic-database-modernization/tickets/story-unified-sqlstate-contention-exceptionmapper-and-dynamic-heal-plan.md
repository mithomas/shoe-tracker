---
title: 'Unified SQLState Contention ExceptionMapper and Dynamic Health Reporting'
type: 'feature'
ticket: 2
created: '2026-10-29'
status: 'built'
baseline_revision: '33c54e2'
route: 'full'
route_source: 'auto'
risk: 'low'
review: 'quick'
review_source: 'pinned'
lenses_ran: ['quick']
review_loop_iteration: 0
context: ["/home/tom/development/personal/shoe-tracker/bmad/_bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md"]
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** Database contention handling and readiness probe reporting were originally coupled to SQLite-specific error codes and static `"SQLite database health check"` / `"sqlite"` labels, which fail to detect wrapped causal `SQLException` chains, MariaDB/MySQL lock timeout/deadlock codes (`1205`, `1213`), or report the live database engine in `/q/health/ready`.

**Approach:** Harden `DatabaseContentionExceptionMapper` in `ExceptionMappers.kt` to traverse the full causal chain (`Throwable.cause` and `SQLException.nextException`) and map ANSI SQLState contention codes (`40001`, `40P01`, `57P01`, `HYT00`), H2 lock codes (`50200`, `90039`, `90072`), and MariaDB/MySQL lock/deadlock codes (`1205`, `1213`) to RFC 7807 HTTP 503 with `Retry-After: 1` (and non-contention SQL errors to sanitized HTTP 500 with UUID `error_id`), while updating `DatabaseHealthCheck.kt` to report `"Database connection health check"` and dynamic `conn.metaData?.databaseProductName ?: "Unknown"`.

## Boundaries & Constraints

**Always:**
- Implement `DatabaseContentionExceptionMapper : ExceptionMapper<SQLException>` in `org.shoetracker.api.ExceptionMappers.kt` annotated with `@Provider`.
- Walk the full exception chain (`cause` and `SQLException.nextException`, guarded against cycles/depth up to 16 steps) when checking whether an `SQLException` represents lock contention or timeout (`R24`).
- Treat ANSI SQLStates `40001` (serialization failure), `40P01` (deadlock), `57P01` (admin shutdown), `HYT00` (timeout), H2 vendor error codes `50200`, `90039`, `90072`, and MariaDB/MySQL vendor error codes `1205` (lock wait timeout), `1213` (deadlock) as transient contention returning HTTP 503 `Service Unavailable` with `Retry-After: 1`, `Content-Type: application/problem+json`, and `ProblemDetails` containing `errorId`.
- Map all other `SQLException` instances to HTTP 500 `Internal Server Error` (`application/problem+json`) with a sanitized message (`"A database error occurred. Please reference error ID: $errorId"`) that never leaks raw SQL or vendor messages to the client.
- Name the readiness check `"Database connection health check"` in `DatabaseHealthCheck.kt` and populate `.withData("database", productName)` using `conn.metaData?.databaseProductName ?: "Unknown"` (`R25`).

**Never:**
- Do not leak internal SQL statements, schema details, or raw exception messages in HTTP 500 `ProblemDetails.detail` responses.
- Do not hardcode `"sqlite"` or `"H2"` in `DatabaseHealthCheck.kt`; always query `conn.metaData?.databaseProductName`.
- Do not introduce `org.sqlite.*` references or modify database driver dependencies in this story (`quarkus-jdbc-mariadb` is added in Story 6.3).

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| ANSI SQLState or H2 lock contention | `SQLException` with `sqlState = "40001"` or `errorCode = 50200` (top-level or nested via `cause` / `nextException`) | HTTP 503 with header `Retry-After: 1` and RFC 7807 `ProblemDetails` (`status = 503`, `title = "Service Unavailable"`, `error_id` UUID) | Logs `WARN` with `error_id`, `sqlState`, and `errorCode` |
| MariaDB/MySQL lock timeout or deadlock | `SQLException` with `errorCode = 1205` or `1213` | HTTP 503 with header `Retry-After: 1` and RFC 7807 `ProblemDetails` (`status = 503`) | Logs `WARN` with `error_id`, `sqlState`, and `errorCode` |
| Non-contention database failure | `SQLException` with `sqlState = "HY000"`, `errorCode = 90030`, message `"database disk image is malformed"` | HTTP 500 with RFC 7807 `ProblemDetails` (`status = 500`, `detail` sanitized without raw message, `error_id` UUID) | Logs `ERROR` with stack trace and `error_id` |
| Readiness probe on active H2 datasource | `GET /q/health/ready` | HTTP 200 JSON with check `"Database connection health check"`, `status = "UP"`, and `data.database = "H2"` | Returns HTTP 503 `status = "DOWN"` with `data.error` if `SELECT 1` fails |

</frozen-after-approval>

## Code Map

- `src/main/kotlin/org/shoetracker/api/ExceptionMappers.kt` -- Refine `DatabaseContentionExceptionMapper` (lines 86-136) to walk `cause` and `SQLException.nextException` chains and match ANSI SQLStates (`40001`, `40P01`, `57P01`, `HYT00`), H2 codes (`50200`, `90039`, `90072`), and MariaDB/MySQL codes (`1205`, `1213`).
- `src/main/kotlin/org/shoetracker/health/DatabaseHealthCheck.kt` -- Update readiness check name (line 15) from `"SQLite database health check"` to `"Database connection health check"` and read `val productName = conn.metaData?.databaseProductName ?: "Unknown"` inside `dataSource.connection.use` (lines 17-22).
- `src/test/kotlin/org/shoetracker/health/HealthResourceTest.kt` -- Update `testHealthReadyEndpoint` (lines 28-49) to assert check name `"Database connection health check"` and `data["database"] == "H2"`.
- `src/test/kotlin/org/shoetracker/api/TestFaultResource.kt` -- Expose fault endpoints (lines 16-25) throwing `SQLException` for contention (`sqlState = "40001", vendorCode = 50200`) and non-contention (`sqlState = "HY000", vendorCode = 90030`).
- `src/test/kotlin/org/shoetracker/api/SightingResourceTest.kt` -- Expand mapper unit and integration tests (lines 303-365) to cover ANSI SQLStates (`40001`, `40P01`, `57P01`, `HYT00`), H2 codes (`50200`, `90039`, `90072`), MariaDB/MySQL codes (`1205`, `1213`), wrapped/chained `SQLException` causes, and sanitized 500 responses.

## Tasks & Acceptance

**Execution:**
- [x] `src/main/kotlin/org/shoetracker/api/ExceptionMappers.kt` -- Implement causal-chain traversal (`cause` and `nextException`) in `DatabaseContentionExceptionMapper` matching SQLStates `40001`, `40P01`, `57P01`, `HYT00`, H2 codes `50200`, `90039`, `90072`, and MariaDB/MySQL codes `1205`, `1213` -- Ensures consistent HTTP 503 `Retry-After: 1` responses across H2 and MariaDB/MySQL (`R24`).
- [x] `src/main/kotlin/org/shoetracker/health/DatabaseHealthCheck.kt` -- Rename check to `"Database connection health check"` and report `withData("database", productName)` from `conn.metaData?.databaseProductName ?: "Unknown"` -- Reports live database engine dynamically in readiness probes (`R25`).
- [x] `src/test/kotlin/org/shoetracker/health/HealthResourceTest.kt` -- Update readiness assertions for `"Database connection health check"` and `data["database"] == "H2"`, plus add a unit test with a stubbed `DataSource` verifying fallback `"Unknown"` and `DOWN` reporting when connection fails -- Verifies dynamic readiness reporting and failure edge cases.
- [x] `src/test/kotlin/org/shoetracker/api/TestFaultResource.kt` and `src/test/kotlin/org/shoetracker/api/SightingResourceTest.kt` -- Add unit and REST tests verifying HTTP 503 + `Retry-After: 1` for SQLStates (`40001`, `40P01`, `57P01`, `HYT00`), H2 (`50200`, `90039`, `90072`), MariaDB (`1205`, `1213`), nested/chained `SQLException`s, and sanitized HTTP 500 for non-contention SQL errors -- Verifies all rows of the I/O & Edge-Case Matrix.

**Acceptance Criteria:**
- Given a top-level or nested `SQLException` with an ANSI contention SQLState, H2 lock code, or MariaDB/MySQL lock/deadlock code, when handled by `DatabaseContentionExceptionMapper`, then it returns HTTP 503 with `Retry-After: 1` and RFC 7807 `application/problem+json`.
- Given a non-contention `SQLException`, when handled by `DatabaseContentionExceptionMapper`, then it returns HTTP 500 with a correlating UUID `error_id` and no leaked SQL or vendor error text.
- Given the running application backed by H2, when `GET /q/health/ready` is requested, then the `"Database connection health check"` entry reports `status: "UP"` and `data.database: "H2"`.

## Implementation Notes

- Updated `DatabaseContentionExceptionMapper` in `ExceptionMappers.kt` with `isContentionChain` walking both `Throwable.cause` and `SQLException.nextException` up to depth 16, matching ANSI SQLStates (`40001`, `40P01`, `57P01`, `HYT00`), H2 vendor codes (`50200`, `90039`, `90072`), and MariaDB/MySQL vendor codes (`1205`, `1213`).
- Updated `DatabaseHealthCheck.kt` to report `"Database connection health check"` and query `conn.metaData?.databaseProductName ?: "Unknown"`.
- Expanded `HealthResourceTest.kt` and `SightingResourceTest.kt` to cover `"Database connection health check"` (`database = "H2"`), `DOWN` readiness fallback, MariaDB/H2 vendor codes, and nested `SQLException` chains.

## Plan Change Log

## Review Triage Log

- `HealthResourceTest.kt:58-89` (`low` -> `patch`): Added stubbed `DataSource` test verifying `"Unknown"` fallback when `conn.metaData` returns `null`.
- `ExceptionMappers.kt:91-97` (`low` -> `patch`): Changed `isContentionChain` to `findContentionException(root): SQLException?` so `logger.warn` logs the matched contention `SQLException`'s `sqlState`, `errorCode`, and `message`.

## Design Notes

**Causal Chain Traversal for `SQLException`:**
Agroal or JDBC drivers may wrap a vendor `SQLException` inside another `SQLException` via `initCause` or `setNextException`:
```kotlin
private fun isContentionChain(root: SQLException): Boolean {
    var current: Throwable? = root
    var depth = 0
    while (current != null && depth++ < 16) {
        if (current is SQLException && isBusyOrLocked(current)) return true
        current = if (current is SQLException && current.nextException != null && current.nextException !== current.cause) {
            current.nextException
        } else {
            current.cause
        }
    }
    return false
}
```

## Verification

**Commands:**
- `./gradlew check test spotlessCheck lintJs` -- expected: `BUILD SUCCESSFUL` with all health check and exception mapper tests passing.
