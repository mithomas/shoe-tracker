---
title: 'Eager Prepared Statements & Startup Fail-Fast Validation in SightingRepository'
type: 'refactor'
ticket: '2'
created: '2026-10-29'
status: 'built'
baseline_revision: '31eb7454848245ba8a89df881f3b3d1ffae40e79'
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

**Problem:** `SightingRepository` uses `by lazy` for its dedicated JDBC connection and all 6 SQL prepared statements. This lazy initialization defers SQL compilation, connection checkout, and potential database failures until the first runtime request. If a query has a syntax error or the database is unavailable, the application starts successfully and only fails during user traffic. Additionally, closing uninitialized statements during shutdown can trigger unexpected on-demand initialization.

**Approach:**
1. Annotate `SightingRepository` with `@io.quarkus.runtime.Startup` and add `@PostConstruct init()` to compile all 6 prepared statements eagerly on application bootstrap.
2. In `@PostConstruct`, execute a fail-fast probe (`SELECT 1`) to guarantee SQLite connectivity and schema validity before accepting traffic.
3. Replace lazy properties with initialized fields, and guard `@PreDestroy close()` using `isInitialized` checks to avoid phantom lazy initialization on shutdown.
4. Add automated tests asserting eager initialization, statement compilation, and fail-fast behavior on corrupt/invalid datasources.

</frozen-after-approval>

## Implementation Notes

Oneshot route selected: Refactoring `SightingRepository` to eager `@Startup` and `@PostConstruct` initialization with fail-fast validation.
- Added `@Startup` and `@PostConstruct init()` to `SightingRepository` to precompile all statements (`insertStmt`, `findWithUserStmt`, `findAllStmt`, `countWithUserStmt`, `countAllStmt`, `deleteAllStmt`, `deleteByIdStmt`) at boot.
- Added fail-fast `SELECT 1` execution at startup.
- Guarded resource cleanup in `@PreDestroy close()` with `::property.isInitialized` checks.
- Created `SightingRepositoryTest.kt` validating CRUD, statement lifecycle, and fail-fast startup behavior on broken/unreachable datasources.

## Plan Change Log

## Review Triage Log

| Lens | Finding | Verdict | Route | Evidence / Rationale |
|---|---|---|---|---|
| quick | Eager compilation of deleteById query | pass | patch | Precompiled `deleteByIdStmt` in `@PostConstruct` alongside all other queries instead of compiling per invocation. |
| quick | Safe shutdown without uninitialized property exceptions | pass | - | Guarded all 7 statements and connection with `isInitialized` checks before calling `close()`. |

## Verification

**Commands:**
- `./gradlew test --tests org.shoetracker.repository.SightingRepositoryTest` -- expected: All repository and fail-fast tests pass cleanly.
- `./gradlew check test spotlessCheck lintJs` -- expected: Full test and hygiene suite passes with zero violations.
