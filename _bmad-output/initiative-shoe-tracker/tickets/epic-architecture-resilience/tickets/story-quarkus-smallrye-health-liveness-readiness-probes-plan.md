---
title: 'Quarkus SmallRye Health Liveness & Readiness Probes'
type: 'feature'
ticket: '4'
created: '2026-10-29'
status: 'built'
baseline_revision: 'ce6a72cdb47fb6f616e0c66f91cb1a1796d66929'
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

**Problem:** The Shoe Tracker container has no standard health, liveness, or readiness probes. Container orchestrators (Docker Compose, Kubernetes) cannot verify whether the JVM process is alive or whether the database connection is ready to accept traffic, leading to traffic routing before SQLite is initialized or container restarts on transient delays.

**Approach:**
1. Add `io.quarkus:quarkus-smallrye-health` dependency to `build.gradle.kts`.
2. Implement a dedicated `@Readiness` health check (`DatabaseHealthCheck`) verifying SQLite connectivity via `SELECT 1`.
3. Verify `/q/health`, `/q/health/live`, and `/q/health/ready` return HTTP 200 with `status: UP` and database status data.
4. Add automated integration tests in `HealthResourceTest.kt` asserting health probe responses and status shapes using AssertJ.

</frozen-after-approval>

## Implementation Notes

Oneshot route selected: Adding `quarkus-smallrye-health` dependency, `@Readiness` database probe, and automated tests.
- Added `io.quarkus:quarkus-smallrye-health` to `build.gradle.kts`.
- Implemented `DatabaseHealthCheck` providing a dedicated `@Readiness` check executing `SELECT 1` on SQLite and returning database type metadata.
- Implemented `HealthResourceTest.kt` asserting HTTP 200 UP on `/q/health/live`, `/q/health/ready`, and `/q/health`.

## Plan Change Log

## Review Triage Log

| Lens | Finding | Verdict | Route | Evidence / Rationale |
|---|---|---|---|---|
| quick | Collision with built-in Agroal health check name | pass | patch | Renamed custom check to `"SQLite database health check"` to avoid collision with Agroal's default `"Database connections health check"`. |
| quick | Probe performance and connection checkout | pass | - | Database probe uses `dataSource.connection.use` with auto-closing statement for zero connection leaks. |

## Verification

**Commands:**
- `./gradlew test --tests org.shoetracker.health.HealthResourceTest` -- expected: Tests pass verifying HTTP 200 UP on `/q/health/live` and `/q/health/ready`.
- `./gradlew check test spotlessCheck lintJs` -- expected: Full test and hygiene suite passes with zero violations.
