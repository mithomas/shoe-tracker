---
title: 'Database Modernization Refactor Sweep and Multi-Target Verification'
type: 'feature'
ticket: 5
created: '2026-10-29'
status: 'built'
baseline_revision: 'fd1d000'
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

**Problem:** After completing Stories 6.1 through 6.4, the repository needs a comprehensive end-to-end audit and automated regression suite confirming zero SQLite remnants across all code and DevOps assets, verifying single-URL runtime switching between embedded H2 file persistence and MariaDB/MySQL, and validating both JVM fast-jar and GraalVM native container builds.

**Approach:** Add an automated repository-wide SQLite remnant audit and multi-target datasource verification test in `DevOpsAssetTest.kt` / `MultiTargetJdbcDriverTest.kt`, execute the full Gradle verification suite (`./gradlew check test spotlessCheck lintJs`), verify single-URL startup switching and `/q/health/ready` product name reporting, and verify `docker build` for both `Dockerfile` (JVM fast-jar) and `src/main/docker/Dockerfile.native` (GraalVM native).

## Boundaries & Constraints

**Always:**
- Verify zero occurrences of `org.xerial:sqlite-jdbc`, `org.sqlite`, or `jdbc:sqlite:` across `build.gradle.kts`, `src/main/`, `Dockerfile`, `docker-compose.yml`, `.github/workflows/ci.yml`, and `README.md` (`R23`, `R26`).
- Verify single-URL datasource switching via `QUARKUS_DATASOURCE_JDBC_URL` (`jdbc:h2:file:...` vs `jdbc:mariadb://...` / `jdbc:mysql://...`) and live `/q/health/ready` reporting (`R24`, `R25`, `R26`).
- Verify both `docker build -t shoe-tracker:h2 .` and `docker build -f src/main/docker/Dockerfile.native -t shoe-tracker:native .` complete cleanly (`R27`).

**Never:**
- Do not leave temporary test databases or scratch files untracked in the repository root.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|----------|--------------|---------------------------|----------------|
| Repository-wide SQLite remnant check | Inspect `build.gradle.kts`, `src/main/`, `Dockerfile`, `docker-compose.yml`, `README.md` | Zero occurrences of `sqlite-jdbc`, `org.sqlite`, or `jdbc:sqlite:` | Test fails with file name and offending line if any SQLite reference remains |
| Multi-target H2 & MariaDB/MySQL verification | Startup and health check with `QUARKUS_DATASOURCE_JDBC_URL` | Reports `"Database connection health check"` with `"H2"` (or `"MariaDB"` when pointed at MariaDB) | Fails health check with HTTP 503 if DB is unreachable |
| Container builds (JVM & Native) | `docker build -t shoe-tracker:h2 .` and `docker build -f src/main/docker/Dockerfile.native -t shoe-tracker:native .` | Both container images build cleanly and run as non-root `USER 185` | Build exits non-zero if artifacts or permissions are invalid |

</frozen-after-approval>

## Code Map

- `src/test/kotlin/org/shoetracker/devops/DevOpsAssetTest.kt` -- Add `testZeroSqliteRemnantsAcrossProjectAssets` scanning `build.gradle.kts`, `src/main/`, `Dockerfile`, `docker-compose.yml`, `.github/workflows/ci.yml`, and `README.md` for `sqlite-jdbc`, `org.sqlite`, and `jdbc:sqlite:`.
- `src/test/kotlin/org/shoetracker/health/HealthResourceTest.kt` -- Extend `testDatabaseHealthCheckReportsDownWhenDataSourceFails` to verify `DatabaseHealthCheck` dynamically reports `"MariaDB"` when `metaData.databaseProductName` returns `"MariaDB"`.

## Tasks & Acceptance

**Execution:**
- [x] `src/test/kotlin/org/shoetracker/devops/DevOpsAssetTest.kt` -- Add `testZeroSqliteRemnantsAcrossProjectAssets` verifying zero SQLite references remain in `build.gradle.kts`, `src/main/`, `Dockerfile`, `docker-compose.yml`, and `README.md` -- Enforces permanent regression protection against SQLite remnants (`R23`--`R27`).
- [x] Run full verification sweep (`./gradlew check test spotlessCheck lintJs`, JVM container build, and native container build) -- Confirms all Epic 6 requirements (`R23`--`R27`) pass end-to-end.

**Acceptance Criteria:**
- Given the repository after Stories 6.1--6.5, when `./gradlew check test spotlessCheck lintJs` runs, then all tests (including `testZeroSqliteRemnantsAcrossProjectAssets`) and linters pass with zero warnings/errors.
- Given `Dockerfile` and `src/main/docker/Dockerfile.native`, when built with Docker, then both images build cleanly and pass health/CRUD checks.

## Implementation Notes

- Added `testZeroSqliteRemnantsAcrossProjectAssets` in `DevOpsAssetTest.kt` scanning `build.gradle.kts`, `Dockerfile`, `docker-compose.yml`, `.github/workflows/ci.yml`, `README.md`, and all files under `src/main/` for `sqlite-jdbc`, `org.sqlite`, and `jdbc:sqlite:`.
- Extended `HealthResourceTest.kt` to verify dynamic `"MariaDB"` product name reporting in `DatabaseHealthCheck` alongside live H2 `/q/health/ready` checks.
- Verified `./gradlew check test spotlessCheck lintJs`, `docker build -t shoe-tracker:h2 .`, and `docker build -f src/main/docker/Dockerfile.native -t shoe-tracker:native .`.

## Plan Change Log

## Review Triage Log

- No findings from `quick` review lens.

## Verification

**Commands:**
- `./gradlew check test spotlessCheck lintJs` -- expected: `BUILD SUCCESSFUL`.
- `docker build -t shoe-tracker:h2 .` -- expected: `exit code 0`.
- `docker build -f src/main/docker/Dockerfile.native -t shoe-tracker:native .` -- expected: `exit code 0`.
