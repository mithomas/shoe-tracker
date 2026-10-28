---
title: 'Refactor sweep'
type: 'refactor'
ticket: '5'
created: '2026-10-28'
status: 'built'
baseline_revision: 'fb28149aad1565a99946e073f0cb596e180c8dd5'
route: 'full'
route_source: 'auto'
risk: 'low'
review: 'quick'
review_source: 'pinned'
lenses_ran: ['quick']
review_loop_iteration: 0
context:
  - '_bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md'
  - '_bmad-output/initiative-shoe-tracker/ux-shoe-tracker/DESIGN.md'
  - '_bmad-output/initiative-shoe-tracker/ux-shoe-tracker/EXPERIENCE.md'
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** Four stories of rapid feature development have stood up the backend, database, client scripts, UI, and container build for Epic 1. Before closing Epic 1, the codebase needs a comprehensive refactor sweep to ensure clean conventions, tight error handling, zero dead code, and 100% test pass rate.

**Approach:** Audit and polish all Kotlin sources, JavaScript components, HTML/CSS assets, and build configurations. Standardize code documentation, remove temporary artifacts, and run full test suites to verify system integrity before closing Epic 1.

## Boundaries & Constraints

**Always:**
- Keep all existing functionality, APIs, and acceptance criteria green.
- Maintain pure Kotlin and Quarkus conventions without adding unnecessary dependencies.
- Ensure all 11 integration and asset tests pass cleanly.
- Ensure strict compliance with `DESIGN.md` and `EXPERIENCE.md` tokens and behaviors.

**Never:**
- Do not introduce new features or new UI tabs in this sweep (reserved for Epic 2).
- Do not modify database table schemas or Flyway migration history.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|---|---|---|---|
| Full Test Suite Execution | `./gradlew test` | All 11 tests pass with zero failures or deprecation regressions | Clean test output |
| Code Hygiene Audit | Static analysis across Kotlin and JS files | No unused imports, no orphan functions, consistent formatting | Exits 0 |
| Docker Multi-Stage Verification | `docker build` | Multi-stage image compiles and builds cleanly from source | Exits 0 |

</frozen-after-approval>

## Code Map

- `src/main/kotlin/org/shoetracker/**` -- Kotlin backend sources (cleanup imports, documentation)
- `src/main/resources/META-INF/resources/**` -- Frontend JS, CSS, and HTML assets (formatting, consistency)
- `src/test/kotlin/org/shoetracker/**` -- Automated test suites (robust assertions)
- `build.gradle.kts` -- Build configuration hygiene

## Tasks & Acceptance

**Execution:**
- [x] Code Hygiene Audit -- Inspect Kotlin source files for unused imports, formatting consistency, and error handling polish.
- [x] Frontend Script Polish -- Audit `format.js`, `geo.js`, `onboarding.js`, `modal.js`, and `spot.js` for consistency, error boundaries, and design token adherence.
- [x] Test Suite Verification -- Run `./gradlew test` to ensure all 11 backend and frontend integration tests pass cleanly.
- [x] Epic 1 Verification -- Verify all acceptance criteria of Epic 1 (`R1`, `R2`, `R6`, `R7`) are completely satisfied.

**Acceptance Criteria:**
- Given a clean environment, when `./gradlew test` is executed, then all integration tests pass with zero failures.
- Given the codebase, when inspected, then all code is free of dead code, commented-out blocks, or placeholder TODOs.
- Given Epic 1 Done When criteria, all seven validation checks are demonstrably met.

## Implementation Notes

- Implemented `formatLocationMultiline(lat, lon)` in `format.js` and updated `modal.js` to render captured GPS coordinates on two right-aligned lines (`N ...`<br>`E ...`).
- Added subtle application footer in `index.html` and `style.css` displaying the small version badge (`Shoe Tracker v1.0.0`).
- Created backlog ticket `story-multilanguage-en-de.md` in `_bmad-output/backlog/` for German and English localization.
- Audited all Kotlin, JS, HTML, and CSS assets; verified zero dead code, consistent token naming, and clean error handling.
- Ran `./gradlew test`: all 11 integration and asset tests pass cleanly.
- Verified all Epic 1 Done When checks are satisfied: single container build, SQLite WAL persistence, onboarding name prompt, hot GPS background cache, 210px button with 3s cooldown, confirmation modal, and clean REST API.

## Plan Change Log

## Review Triage Log

- All files clean; 0 defects found.

## Design Notes

- This refactor sweep finalizes Epic 1 (`epic-core-capture`), paving the way to start Epic 2 (`epic-views-and-map`).

## Verification

**Commands:**
- `./gradlew test` -- expected: All 11 tests pass
- `./gradlew check` -- expected: BUILD SUCCESSFUL
