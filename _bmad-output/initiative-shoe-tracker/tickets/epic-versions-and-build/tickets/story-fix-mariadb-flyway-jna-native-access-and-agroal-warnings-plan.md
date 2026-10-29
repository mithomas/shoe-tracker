---
title: "Fix MariaDB 11.8 Flyway Support, JDK 25 JNA Native Access Warning, and Agroal Exception Sorter for MultiTargetJdbcDriver"
type: 'bug'
ticket: 7
created: '2026-10-29'
status: 'built'
route: 'oneshot'
route_source: 'auto'
risk: 'low'
review: 'quick'
review_source: 'auto'
lenses_ran: ['quick']
review_loop_iteration: 1
context: ["/home/tom/development/personal/shoe-tracker/bmad/_bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md"]
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** When starting the container on JDK 25 against MariaDB 11.8 (`QUARKUS_DATASOURCE_JDBC_URL=jdbc:mariadb://...`), three issues occur:
1. `org.flywaydb.core.api.FlywayException: Unsupported Database: MariaDB 11.8` because Flyway 12 (`flyway-core:12.0.0` in Quarkus 3.40.1.1) only bundles H2 and SQLite in `flyway-core` and requires `io.quarkus:quarkus-flyway-mysql` (`org.flywaydb:flyway-mysql`) for MariaDB and MySQL support.
2. `WARNING: A restricted method in java.lang.System has been called` (`System::load` in `com.sun.jna.Native`) because JDK 25 restricts native library loading in unnamed modules unless `--enable-native-access=ALL-UNNAMED` is passed.
3. `WARN [io.quarkus.agroal.runtime.AgroalConnectionConfigurer] Agroal does not support detecting if a connection is still usable after an exception for database kind: other` because `quarkus.datasource.db-kind=other` has no `@JdbcDriver("other")` `AgroalConnectionConfigurer` bean and falls back to `UnknownDbAgroalConnectionConfigurer`.

**Approach:**
1. Add `implementation("io.quarkus:quarkus-flyway-mysql")` to `build.gradle.kts` so `MariaDBDatabaseType` and `MySQLDatabaseType` are registered in Flyway's `DatabaseTypeRegister` alongside `H2DatabaseType`.
2. Add `--enable-native-access=ALL-UNNAMED` to `JAVA_TOOL_OPTIONS` and `ENTRYPOINT` in `Dockerfile` and to `tasks.withType<Test>()` in `build.gradle.kts`.
3. Add `MultiTargetAgroalConnectionConfigurer` (`@ApplicationScoped`, `@Unremovable`, `@JdbcDriver("other")`) implementing `AgroalConnectionConfigurer` and setting `MySQLExceptionSorter()` on the connection pool configuration (which handles standard `08*` SQLState connection errors for both H2 and MariaDB/MySQL as well as MariaDB/MySQL fatal error codes).
4. Add unit and asset tests in `MultiTargetJdbcDriverTest.kt` and `DevOpsAssetTest.kt`.

</frozen-after-approval>

## Implementation Notes

- `MySQLExceptionSorter` in `io.agroal.api.exceptionsorter.MySQLExceptionSorter` marks SQLState `08*` connection exceptions as fatal for all JDBC drivers (including H2 and MariaDB/MySQL) and additionally checks MariaDB/MySQL fatal server error codes (`1004..1130`), which do not overlap with H2 error codes.
- `io.quarkus:quarkus-flyway-mysql` is managed by the Quarkus BOM (`3.40.1.1`) and brings in `org.flywaydb:flyway-mysql:12.0.0` plus Quarkus native-image and build-time service registrations for MariaDB/MySQL Flyway migrations.

## Plan Change Log

## Review Triage Log

## Verification

**Commands:**
- `./gradlew check test spotlessCheck lintJs` -- expected: `BUILD SUCCESSFUL` with all unit/integration tests and linters passing.
