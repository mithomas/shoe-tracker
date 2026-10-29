---
title: 'Replace SQLite with Embedded H2 Database and Native Timestamps'
type: 'feature'
ticket: 1
created: '2026-10-29'
status: 'built'
baseline_revision: '119da48a03feac1f23d3226c60ca43253515c509'
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

**Problem:** Shoe Tracker currently persists sightings in SQLite (`org.xerial:sqlite-jdbc`) using `TEXT` timestamps and a single shared JDBC `Connection` / `PreparedStatement` set guarded by in-memory `ReentrantLock` instances, which introduces native JNI dependencies, serializes concurrent threads, and prevents GraalVM native compilation and MySQL/MariaDB compatibility.

**Approach:** Replace `org.xerial:sqlite-jdbc` with pure-Java `io.quarkus:quarkus-jdbc-h2` configured in MySQL compatibility mode (`MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE`), rewrite `V1__init_sightings.sql` to ANSI/MySQL DDL with `TIMESTAMP(3)` columns and `VARCHAR(255)` for `user_id`, refactor `SightingRepository` to per-operation Agroal connection checkouts (`dataSource.connection.use`) with UTC `Calendar` timestamp binding while preserving `@Startup` `@PostConstruct` fail-fast `SELECT 1` validation, and replace `org.sqlite.*` references in `ExceptionMappers.kt` and test classes with `java.sql.SQLException` so the build compiles and passes all tests cleanly.

## Boundaries & Constraints

**Always:**
- Use `io.quarkus:quarkus-jdbc-h2` managed by the Quarkus BOM in `build.gradle.kts` and remove `org.xerial:sqlite-jdbc` completely.
- Configure H2 in `application.properties` with `quarkus.datasource.db-kind=h2`, `quarkus.datasource.jdbc.url=jdbc:h2:file:./data/shoe-tracker;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE`, `quarkus.datasource.jdbc.enable-stm-cache=true`, and `%test.quarkus.datasource.jdbc.url=jdbc:h2:file:./build/test-data/shoe-tracker-test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE`.
- Remove SQLite `quarkus.datasource.jdbc.driver=org.sqlite.JDBC` and `quarkus.datasource.jdbc.init-sql=PRAGMA ...` from `application.properties`.
- Define `sightings` in `V1__init_sightings.sql` with `id VARCHAR(36) PRIMARY KEY`, `user_id VARCHAR(255) NOT NULL`, `latitude DOUBLE NOT NULL`, `longitude DOUBLE NOT NULL`, `recorded_at TIMESTAMP(3) NOT NULL`, and `created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)`.
- Preserve `@Startup` and `@PostConstruct fun init()` on `SightingRepository` executing a fail-fast `SELECT 1` query via `dataSource.connection.use` (satisfying `R19`), while removing `writeLock`, `readLock`, shared `Connection`, and shared `PreparedStatement` fields.
- Bind and read `Timestamp` values in `SightingRepository` using `Timestamp.from(instant)` / `rs.getTimestamp(..., utcCalendar()).toInstant()` with a fresh `Calendar.getInstance(TimeZone.getTimeZone("UTC"))` per call (since `java.util.Calendar` is not thread-safe).
- Replace `org.sqlite.*` imports in `ExceptionMappers.kt`, `TestFaultResource.kt`, and `SightingResourceTest.kt` with `java.sql.SQLException` so Story 6.1 compiles and passes `./gradlew check test` without waiting for Story 6.2.

**Never:**
- Do not retain `org.xerial:sqlite-jdbc` or any `org.sqlite.*` import anywhere in `build.gradle.kts` or `src/`.
- Do not share a single `Calendar` instance across threads in `SightingRepository`.
- Do not use unlengthed `TEXT` for `user_id` in `V1__init_sightings.sql` (MySQL/MariaDB rejects standard B-tree indexes on unlengthed `TEXT`).
- Do not modify `DatabaseHealthCheck.kt` (`"SQLite"` string in health check name is updated in Story 6.2) or add `quarkus-jdbc-mariadb` (scheduled for Story 6.3).

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Startup connectivity validation (`@PostConstruct init()`) | Reachable H2 file datasource (`./data/shoe-tracker` or test `./build/test-data/shoe-tracker-test`) | Executes `SELECT 1` inside `dataSource.connection.use` and completes startup cleanly; Flyway applies `V1__init_sightings.sql` | Throws `SQLException` and aborts startup if `DataSource.getConnection()` or `SELECT 1` fails |
| Insert and query sighting with UTC `TIMESTAMP(3)` | `Sighting` with `Instant` `recordedAt` and `createdAt` | Persists and retrieves `Sighting` with `recordedAt` and `createdAt` preserved in UTC at millisecond precision (`TIMESTAMP(3)`) | Propagates `SQLException` on JDBC failure |
| Concurrent multi-threaded CRUD on `SightingRepository` | Multiple threads concurrently calling `insert`, `findSightings`, `countSightings`, and `deleteById` | Each operation checks out a pooled Agroal connection via `dataSource.connection.use` without `ReentrantLock` blocking or parameter corruption | Propagates `SQLException` if pool or DB fails |
| Database lock contention vs general SQL error in `ExceptionMappers.kt` | `SQLException` with `sqlState = "40001"` vs `SQLException` with `sqlState = "HY000"` | `40001` returns HTTP 503 with `Retry-After: 1` and RFC 7807 `ProblemDetails`; non-contention returns HTTP 500 with sanitized detail and `error_id` | Logs warning for 503 and error with stack trace for 500 |

</frozen-after-approval>

## Code Map

- `build.gradle.kts` -- Replace `implementation("org.xerial:sqlite-jdbc:3.53.4.0")` (line 25) with `implementation("io.quarkus:quarkus-jdbc-h2")`.
- `src/main/resources/application.properties` -- Replace SQLite `db-kind=other`, `driver=org.sqlite.JDBC`, `url=jdbc:sqlite:...`, `init-sql=PRAGMA...`, and `%test.quarkus.datasource.jdbc.url` (lines 4-11, 22) with H2 configuration (`db-kind=h2`, `enable-stm-cache=true`, H2 file URLs with `MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE`).
- `src/main/resources/db/migration/V1__init_sightings.sql` -- Rewrite SQLite `TEXT`/`REAL`/`strftime` DDL (lines 1-16) to ANSI/MySQL DDL (`VARCHAR(36)`, `VARCHAR(255)`, `DOUBLE`, `TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)`).
- `src/main/kotlin/org/shoetracker/repository/SightingRepository.kt` -- Refactor repository (lines 19-163): remove `writeLock`, `readLock`, `isoFormatter`, and `lateinit var` statement/connection fields; keep `@Startup` and `@PostConstruct fun init()` executing `SELECT 1` via `dataSource.connection.use`; execute `insert`, `findSightings`, `countSightings`, `deleteAll`, and `deleteById` using `dataSource.connection.use { conn -> conn.prepareStatement(...).use { stmt -> ... } }` and `Timestamp.from(...)` / `rs.getTimestamp(..., utcCalendar()).toInstant()`.
- `src/main/kotlin/org/shoetracker/api/ExceptionMappers.kt` -- Remove `org.sqlite.*` imports (lines 10-11) and update `SQLiteExceptionMapper` (or `DatabaseContentionExceptionMapper`, lines 86-136) to implement `ExceptionMapper<SQLException>` checking SQLState (`40001`, `40P01`, `57P01`, `HYT00`) and H2 lock error codes (`50200`, `90039`, `90072`) so the codebase compiles without `sqlite-jdbc`.
- `src/test/kotlin/org/shoetracker/api/TestFaultResource.kt` -- Replace `org.sqlite.*` imports and exceptions (lines 6-7, 17-25) with `java.sql.SQLException("database is locked", "40001", 50200)` and `java.sql.SQLException("database disk image is malformed", "HY000", 90030)`.
- `src/test/kotlin/org/shoetracker/api/SightingResourceTest.kt` -- Replace `org.sqlite.SQLiteException` unit test references (lines 342-365) with `java.sql.SQLException` instances testing contention (`40001`, `50200`) and non-contention (`HY000`) responses.
- `src/test/kotlin/org/shoetracker/repository/SightingRepositoryTest.kt` -- Update repository tests (lines 33-100) to assert millisecond-truncated (`ChronoUnit.MILLIS`) `TIMESTAMP(3)` round-tripping, `@PostConstruct init()` fail-fast validation, and multi-threaded concurrent `insert`/`findSightings` execution without in-memory locks.

## Tasks & Acceptance

**Execution:**
- [x] `build.gradle.kts` -- Replace `implementation("org.xerial:sqlite-jdbc:3.53.4.0")` with `implementation("io.quarkus:quarkus-jdbc-h2")` -- Removes native JNI SQLite driver and enables Quarkus H2 Agroal integration (`R23`).
- [x] `src/main/resources/application.properties` -- Configure `quarkus.datasource.db-kind=h2`, `quarkus.datasource.jdbc.url=jdbc:h2:file:./data/shoe-tracker;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE`, `quarkus.datasource.jdbc.enable-stm-cache=true`, and `%test.quarkus.datasource.jdbc.url=jdbc:h2:file:./build/test-data/shoe-tracker-test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE`, removing `driver` and `init-sql` PRAGMAs -- Configures embedded H2 in MySQL mode with Agroal statement caching.
- [x] `src/main/resources/db/migration/V1__init_sightings.sql` -- Replace SQLite DDL with ANSI/MySQL DDL (`id VARCHAR(36) PRIMARY KEY`, `user_id VARCHAR(255) NOT NULL`, `latitude DOUBLE NOT NULL`, `longitude DOUBLE NOT NULL`, `recorded_at TIMESTAMP(3) NOT NULL`, `created_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)`, and indexes `idx_sightings_recorded_at`, `idx_sightings_user_id`) -- Establishes native `TIMESTAMP(3)` schema compatible with both H2 (`MODE=MySQL`) and MariaDB/MySQL.
- [x] `src/main/kotlin/org/shoetracker/repository/SightingRepository.kt` -- Remove `writeLock`, `readLock`, `isoFormatter`, and shared connection/statement fields; keep `@Startup` `@PostConstruct fun init()` running `SELECT 1` via `dataSource.connection.use`; implement `insert`, `findSightings`, `countSightings`, `deleteAll`, and `deleteById` using `dataSource.connection.use` and UTC `Calendar` `Timestamp` binding -- Eliminates thread serialization and shared-statement parameter races while preserving fail-fast startup validation (`R19`, `R23`).
- [x] `src/main/kotlin/org/shoetracker/api/ExceptionMappers.kt` -- Replace `org.sqlite.*` imports and `ExceptionMapper<SQLiteException>` with `java.sql.SQLException` handling (`DatabaseContentionExceptionMapper`) matching SQLStates `40001`, `40P01`, `57P01`, `HYT00` and H2 lock codes `50200`, `90039`, `90072` -- Removes broken `org.sqlite.*` compilation dependencies while preserving HTTP 503 contention and HTTP 500 error contracts.
- [x] `src/test/kotlin/org/shoetracker/api/TestFaultResource.kt` and `src/test/kotlin/org/shoetracker/api/SightingResourceTest.kt` -- Replace `org.sqlite.*` imports and `SQLiteException` instantiations with `java.sql.SQLException` equivalents -- Keeps all REST fault injection and unit tests compiling and passing without `sqlite-jdbc`.
- [x] `src/test/kotlin/org/shoetracker/repository/SightingRepositoryTest.kt` -- Update CRUD assertions for `TIMESTAMP(3)` millisecond precision (`ChronoUnit.MILLIS`) and add a multi-threaded concurrency test verifying parallel `insert` and `findSightings` calls without locks -- Verifies `TIMESTAMP(3)` fidelity, fail-fast `init()`, and lock-free concurrency.

**Acceptance Criteria:**
- Given the application configuration and Gradle build, when `./gradlew check test` runs, then zero `org.sqlite` references exist in `build.gradle.kts` or `src/`, Flyway executes `V1__init_sightings.sql` on H2 (`MODE=MySQL`), and all tests and linters pass.
- Given `SightingRepository`, when multiple threads concurrently insert and query sightings, then all records persist and return accurate UTC `TIMESTAMP(3)` timestamps (millisecond precision) without `ReentrantLock` serialization.
- Given an unreachable `DataSource`, when `SightingRepository.init()` runs at startup, then it throws `SQLException` fail-fast on `SELECT 1`.

## Implementation Notes

- Replaced `org.xerial:sqlite-jdbc:3.53.4.0` with `io.quarkus:quarkus-jdbc-h2` in `build.gradle.kts`.
- Configured embedded H2 (`MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE`) and `quarkus.datasource.jdbc.enable-stm-cache=true` in `application.properties`, with test database isolated in `./build/test-data/shoe-tracker-test`.
- Migrated `V1__init_sightings.sql` to ANSI/MySQL DDL (`VARCHAR(36)`, `VARCHAR(255)`, `DOUBLE`, `TIMESTAMP(3)`).
- Refactored `SightingRepository.kt` to remove `ReentrantLock`s and shared `Connection`/`PreparedStatement` fields, using per-operation `dataSource.connection.use` checkouts and fresh UTC `Calendar` instances for `Timestamp` binding while retaining `@Startup` `@PostConstruct fun init()` `SELECT 1` validation.
- Replaced `SQLiteExceptionMapper` with `DatabaseContentionExceptionMapper : ExceptionMapper<SQLException>` in `ExceptionMappers.kt` and updated `TestFaultResource.kt`, `SightingResourceTest.kt`, and `SightingRepositoryTest.kt` (including 8-thread concurrent CRUD test).

## Plan Change Log

## Review Triage Log

- `quick` lens: No unmet acceptance criteria, broken rules, or bugs found.

## Design Notes

**UTC Timestamp Binding with `Calendar`:**
JDBC `PreparedStatement.setTimestamp(index, Timestamp, Calendar)` and `ResultSet.getTimestamp(column, Calendar)` mutate the passed `Calendar` internally. Always allocate a fresh helper `private fun utcCalendar(): Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))` per call:
```kotlin
stmt.setTimestamp(5, Timestamp.from(sighting.recordedAt), utcCalendar())
stmt.setTimestamp(6, Timestamp.from(sighting.createdAt), utcCalendar())
// and in mapRow(rs):
recordedAt = rs.getTimestamp("recorded_at", utcCalendar()).toInstant(),
createdAt = rs.getTimestamp("created_at", utcCalendar()).toInstant(),
```

## Verification

**Commands:**
- `./gradlew check test spotlessCheck lintJs` -- expected: `BUILD SUCCESSFUL` with all JUnit 5 tests, Spotless checks, and Closure Compiler JS lint passing.
- `git grep -i "org\.sqlite\|sqlite-jdbc" -- build.gradle.kts src/` -- expected: no matches (exit code 1).
