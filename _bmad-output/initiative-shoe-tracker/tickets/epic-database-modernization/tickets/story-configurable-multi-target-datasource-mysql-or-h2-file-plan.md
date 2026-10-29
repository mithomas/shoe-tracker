---
title: 'Configurable Multi-Target Datasource (MySQL or H2 File)'
type: 'feature'
ticket: 3
created: '2026-10-29'
status: 'built'
baseline_revision: 'f83f755'
route: 'full'
route_source: 'auto'
risk: 'low'
review: 'quick'
review_source: 'pinned'
lenses_ran: ['quick']
review_loop_iteration: 1
context: ["/home/tom/development/personal/shoe-tracker/bmad/_bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md"]
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** Quarkus fixes `quarkus.datasource.db-kind` at build time (`SRCFG00025`), preventing operators from switching between the default embedded H2 file database (`jdbc:h2:file:...`) and an external MariaDB/MySQL server (`jdbc:mariadb://...` or `jdbc:mysql://...`) at runtime using only a single `QUARKUS_DATASOURCE_JDBC_URL` environment variable; additionally, `Dockerfile`, `docker-compose.yml`, and `README.md` still reference SQLite URLs.

**Approach:** Bundle `io.quarkus:quarkus-jdbc-mariadb` alongside `io.quarkus:quarkus-jdbc-h2` and implement a delegating `java.sql.Driver` (`MultiTargetJdbcDriver` configured with `quarkus.datasource.db-kind=other` and `quarkus.datasource.jdbc.driver=org.shoetracker.repository.MultiTargetJdbcDriver`) that inspects the JDBC URL prefix at connection time (`jdbc:h2:...` -> `org.h2.Driver`, `jdbc:mariadb://...` or `jdbc:mysql://...` -> `org.mariadb.jdbc.Driver`), update `Dockerfile` and `docker-compose.yml` to default to H2 file persistence with optional MariaDB compose examples, and document single-URL datasource switching in `README.md`.

## Boundaries & Constraints

**Always:**
- Bundle both `io.quarkus:quarkus-jdbc-h2` and `io.quarkus:quarkus-jdbc-mariadb` in `build.gradle.kts` (`R26`, `AD-9`).
- Support runtime switching between embedded H2 (`jdbc:h2:file:...` or `jdbc:h2:tcp:...`) and external MariaDB/MySQL (`jdbc:mariadb://...` or `jdbc:mysql://...`) purely by setting `QUARKUS_DATASOURCE_JDBC_URL` (plus optional `QUARKUS_DATASOURCE_USERNAME` and `QUARKUS_DATASOURCE_PASSWORD`), without requiring build-time `db-kind` overrides (`R26`).
- Normalize `jdbc:mysql://` URLs to `jdbc:mariadb://` when delegating to `org.mariadb.jdbc.Driver` if `org.mariadb.jdbc.Driver.acceptsURL` requires the `jdbc:mariadb:` scheme, or verify `org.mariadb.jdbc.Driver` accepts both `jdbc:mariadb:` and `jdbc:mysql:`.
- Keep default `quarkus.datasource.jdbc.url=jdbc:h2:file:./data/shoe-tracker;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE` in `application.properties` and `jdbc:h2:file:/data/shoe-tracker;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE` in `Dockerfile` and `docker-compose.yml`.
- Ensure Flyway migrations (`quarkus.flyway.migrate-at-start=true`) and `DatabaseHealthCheck` (`conn.metaData.databaseProductName`) work seamlessly through the routed driver for both H2 and MariaDB/MySQL.

**Never:**
- Do not require the user/operator to set `QUARKUS_DATASOURCE_DB_KIND` or `QUARKUS_DATASOURCE_JDBC_DRIVER` at runtime when switching between H2 and MariaDB/MySQL.
- Do not leave any `jdbc:sqlite:` URLs in `Dockerfile`, `docker-compose.yml`, or `README.md`.
- Do not break pure domain boundaries (`org.shoetracker.domain` must remain free of JDBC/driver classes per `AD-8`).

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Default H2 file JDBC URL | `QUARKUS_DATASOURCE_JDBC_URL=jdbc:h2:file:./data/shoe-tracker;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE` | `MultiTargetJdbcDriver` delegates to `org.h2.Driver`, Flyway runs `V1__init_sightings.sql`, `/q/health/ready` reports `"H2"` | Propagates `SQLException` if file path is unwritable |
| MariaDB or MySQL JDBC URL | `QUARKUS_DATASOURCE_JDBC_URL=jdbc:mariadb://host:3306/shoetracker` or `jdbc:mysql://host:3306/shoetracker` | `MultiTargetJdbcDriver` delegates to `org.mariadb.jdbc.Driver` | Throws `SQLException` at startup (`SightingRepository.init()`) if server is unreachable |
| Unsupported JDBC URL prefix | `QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://localhost:5432/db` | `acceptsURL` returns `false` / `connect` returns `null` (causing `DriverManager` / Agroal to throw `SQLException`) | Aborts startup fail-fast with clear `SQLException` |

</frozen-after-approval>

## Code Map

- `build.gradle.kts` -- Add `implementation("io.quarkus:quarkus-jdbc-mariadb")` alongside `implementation("io.quarkus:quarkus-jdbc-h2")`.
- `src/main/kotlin/org/shoetracker/repository/MultiTargetJdbcDriver.kt` -- New delegating `java.sql.Driver` implementation routing `jdbc:h2:` URLs to `org.h2.Driver()` and `jdbc:mariadb:` / `jdbc:mysql:` URLs to `org.mariadb.jdbc.Driver()`, annotated with `@RegisterForReflection` for GraalVM native compatibility.
- `src/main/resources/application.properties` -- Configure `quarkus.datasource.db-kind=other`, `quarkus.datasource.jdbc.driver=org.shoetracker.repository.MultiTargetJdbcDriver`, default H2 file URL, and `quarkus.datasource.jdbc.enable-stm-cache=true`.
- `Dockerfile` -- Update `QUARKUS_DATASOURCE_JDBC_URL` (line 31) from `jdbc:sqlite:/data/shoe-tracker.db...` to `jdbc:h2:file:/data/shoe-tracker;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE`.
- `docker-compose.yml` -- Update `QUARKUS_DATASOURCE_JDBC_URL` (line 15) to H2 `/data/shoe-tracker` URL and add commented external MariaDB service + `QUARKUS_DATASOURCE_JDBC_URL=jdbc:mariadb://...` override example.
- `README.md` -- Update Tech Stack, Docker Compose description, and add a "Database Configuration (H2 vs MariaDB/MySQL)" section documenting `QUARKUS_DATASOURCE_JDBC_URL`, `QUARKUS_DATASOURCE_USERNAME`, and `QUARKUS_DATASOURCE_PASSWORD`.
- `src/test/kotlin/org/shoetracker/repository/MultiTargetJdbcDriverTest.kt` -- New unit/integration test verifying driver delegation for `jdbc:h2:...`, `jdbc:mariadb://...`, `jdbc:mysql://...`, and rejection of unsupported schemes.
- `src/test/kotlin/org/shoetracker/devops/DevOpsAssetTest.kt` -- Add assertions verifying `Dockerfile`, `docker-compose.yml`, and `README.md` specify the H2 file URL and document MariaDB/MySQL `QUARKUS_DATASOURCE_JDBC_URL` switching.

## Tasks & Acceptance

**Execution:**
- [x] `build.gradle.kts` -- Add `implementation("io.quarkus:quarkus-jdbc-mariadb")` -- Bundles the MariaDB/MySQL JDBC driver alongside H2 (`R26`, `AD-9`).
- [x] `src/main/kotlin/org/shoetracker/repository/MultiTargetJdbcDriver.kt` and `src/main/resources/application.properties` -- Implement `MultiTargetJdbcDriver : java.sql.Driver` delegating `jdbc:h2:` to `org.h2.Driver` and `jdbc:mariadb:` / `jdbc:mysql:` to `org.mariadb.jdbc.Driver`, and wire it as `quarkus.datasource.jdbc.driver` with `quarkus.datasource.db-kind=other` -- Enables single-variable runtime URL switching without build-time `db-kind` lock-in (`R26`).
- [x] `Dockerfile`, `docker-compose.yml`, and `README.md` -- Replace stale `jdbc:sqlite:` URLs with `jdbc:h2:file:/data/shoe-tracker;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE` and document MariaDB/MySQL single-URL configuration -- Aligns container defaults and operator documentation with `AD-9`.
- [x] `src/test/kotlin/org/shoetracker/repository/MultiTargetJdbcDriverTest.kt` and `src/test/kotlin/org/shoetracker/devops/DevOpsAssetTest.kt` -- Add unit tests for `MultiTargetJdbcDriver` (`jdbc:h2:`, `jdbc:mariadb:`, `jdbc:mysql:`, and invalid prefixes) and DevOps asset assertions for `Dockerfile`, `docker-compose.yml`, and `README.md` -- Verifies multi-target routing and container/documentation parity.

**Acceptance Criteria:**
- Given only `QUARKUS_DATASOURCE_JDBC_URL` is changed between `jdbc:h2:file:...` and `jdbc:mariadb://...` (or `jdbc:mysql://...`), when the datasource connects at startup, then `MultiTargetJdbcDriver` dispatches to `org.h2.Driver` or `org.mariadb.jdbc.Driver` respectively without any `SRCFG00025` build-time property mismatch.
- Given `Dockerfile`, `docker-compose.yml`, and `README.md`, when inspected by `DevOpsAssetTest`, then zero `jdbc:sqlite:` references remain and both H2 default and MariaDB/MySQL override URLs are present.

## Implementation Notes

- Added `io.quarkus:quarkus-jdbc-mariadb` to `build.gradle.kts`.
- Created `MultiTargetJdbcDriver` (`src/main/kotlin/org/shoetracker/repository/MultiTargetJdbcDriver.kt`) routing `jdbc:h2:` to `org.h2.Driver` and `jdbc:mariadb:` / `jdbc:mysql:` to `org.mariadb.jdbc.Driver`, and wired it in `application.properties` via `quarkus.datasource.db-kind=other` and `quarkus.datasource.jdbc.driver=org.shoetracker.repository.MultiTargetJdbcDriver`.
- Updated `Dockerfile`, `src/main/docker/Dockerfile.jvm`, `docker-compose.yml`, and `README.md` to use `jdbc:h2:file:/data/shoe-tracker;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE` and document single-URL MariaDB/MySQL switching.
- Added `MultiTargetJdbcDriverTest.kt` and expanded `DevOpsAssetTest.kt`.

## Plan Change Log

## Review Triage Log

### Iteration 1
- `quick`: 0 findings (`[]`).

## Design Notes

**Delegating `MultiTargetJdbcDriver`:**
Because Quarkus locks `quarkus.datasource.db-kind` at build time, setting `quarkus.datasource.db-kind=other` and `quarkus.datasource.jdbc.driver=org.shoetracker.repository.MultiTargetJdbcDriver` allows a single runtime `QUARKUS_DATASOURCE_JDBC_URL` to select either bundled driver:
```kotlin
@RegisterForReflection
class MultiTargetJdbcDriver : Driver {
    private val h2Driver = org.h2.Driver()
    private val mariaDbDriver = org.mariadb.jdbc.Driver()

    override fun acceptsURL(url: String?): Boolean =
        url != null && (url.startsWith("jdbc:h2:") || url.startsWith("jdbc:mariadb:") || url.startsWith("jdbc:mysql:"))

    override fun connect(url: String?, info: Properties?): Connection? = when {
        url == null -> null
        url.startsWith("jdbc:h2:") -> h2Driver.connect(url, info)
        url.startsWith("jdbc:mariadb:") || url.startsWith("jdbc:mysql:") -> mariaDbDriver.connect(url, info)
        else -> null
    }
}
```

## Verification

**Commands:**
- `./gradlew check test spotlessCheck lintJs` -- expected: `BUILD SUCCESSFUL` with `MultiTargetJdbcDriverTest` and `DevOpsAssetTest` passing.
- `git grep -i "jdbc:sqlite" -- Dockerfile docker-compose.yml README.md src/` -- expected: no matches (exit code 1).
