---
title: 'Update Docker Hub Username to mithomas and Image Name to shoe-tracker in Examples and README'
type: 'feature'
ticket: 4
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

**Problem:** `docker-compose.yml` defaults to placeholder `mitch/shoe-tracker:latest` instead of the official Docker Hub repository `mithomas/shoe-tracker`, and `README.md` does not explicitly document `mithomas/shoe-tracker:latest` / `mithomas/shoe-tracker:dev`.

**Approach:** Update `docker-compose.yml` to `${DOCKERHUB_IMAGE:-mithomas/shoe-tracker:latest}`, document `mithomas/shoe-tracker:latest` and `mithomas/shoe-tracker:dev` in `README.md` (while preserving `https://github.com/mitch/shoe-tracker` source code links), and update `DevOpsAssetTest.kt` to assert `mithomas/shoe-tracker` and forbid `mitch/shoe-tracker` in `docker-compose.yml` and `README.md`.

</frozen-after-approval>

## Implementation Notes

- Preserves GitHub repository URL (`https://github.com/mitch/shoe-tracker`) per ticket boundaries.

## Plan Change Log

## Review Triage Log

## Verification

**Commands:**
- `./gradlew check test spotlessCheck lintJs` -- expected: `BUILD SUCCESSFUL` with all unit/integration tests and linters passing.
