---
title: 'Architecture Resilience Refactor Sweep & Stress Verification'
type: 'chore'
ticket: '5'
created: '2026-10-29'
status: 'built'
baseline_revision: '08322f9ea34267e7c8ec178b66e13352932c021f'
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

**Problem:** Epic 5 introduced global RFC 7807 500 mapping with error ID correlation, eager statement initialization in `SightingRepository`, SQLite contention error mapping to HTTP 503, and SmallRye Health liveness and readiness probes. A final refactor sweep is needed to audit the exception hierarchy, verify health probes under container execution, run all test and Spotless suites, and confirm multi-stage container build and live execution without regressions.

**Approach:**
1. Audit exception mapping priority and ordering to ensure 400, 500, and 503 mappers behave consistently.
2. Execute full Gradle verification suite (`./gradlew check test spotlessCheck lintJs`).
3. Build the multi-stage Docker container image with Git commit hash stamping.
4. Launch container and verify `/api/v1/version`, `/q/health/live`, `/q/health/ready`, and web assets.

</frozen-after-approval>

## Implementation Notes

Oneshot route selected: Comprehensive verification and refactor sweep for Epic 5.
- Verified full Gradle check and test suite (`./gradlew check test spotlessCheck lintJs`): all 18 tasks passed.
- Built multi-stage Docker container (`shoe-tracker:resilience-sweep`) with `GIT_COMMIT=08322f9` on JDK 25.
- Executed live container smoke test:
  - `/api/v1/version`: HTTP 200 with commit `08322f9`.
  - `/q/health/live`: HTTP 200 `UP`.
  - `/q/health/ready`: HTTP 200 `UP` confirming both Agroal and SQLite database connectivity probes.

## Plan Change Log

## Review Triage Log

| Lens | Finding | Verdict | Route | Evidence / Rationale |
|---|---|---|---|---|
| quick | Container liveness and readiness probe verification | pass | - | Live container curl verified both `/q/health/live` and `/q/health/ready` return 200 UP. |
| quick | Zero formatting and linting regressions | pass | - | `./gradlew check test spotlessCheck lintJs` passed cleanly in 11s across all 18 tasks. |

## Verification

**Commands:**
- `./gradlew check test spotlessCheck lintJs` -- expected: All Gradle check suites pass cleanly.
- `docker build --build-arg GIT_COMMIT=08322f9 -t shoe-tracker:resilience-sweep .` -- expected: Container builds without daemon errors.
- Live container health probe check (`curl http://localhost:8083/q/health`) -- expected: Returns HTTP 200 UP.
