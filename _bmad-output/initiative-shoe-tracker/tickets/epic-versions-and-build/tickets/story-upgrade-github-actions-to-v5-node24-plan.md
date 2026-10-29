---
title: 'Upgrade GitHub Actions in CI Workflow from v4 (Node 20) to v5 (Node 24)'
type: 'feature'
ticket: 2
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

**Problem:** `.github/workflows/ci.yml` uses `@v4` (Node 20) for `actions/checkout`, `actions/setup-java`, and `gradle/actions/wrapper-validation`, triggering Node 20 deprecation warnings on GitHub Actions.

**Approach:** Upgrade `actions/checkout@v4` (both `test` and `release` jobs), `actions/setup-java@v4`, and `gradle/actions/wrapper-validation@v4` to `@v5` (Node 24) in `.github/workflows/ci.yml`, and assert `@v5` (and `.doesNotContain("@v4")`) in `DevOpsAssetTest.kt`.

</frozen-after-approval>

## Implementation Notes

- Oneshot route selected for declarative CI workflow action version upgrade.

## Plan Change Log

## Review Triage Log

## Verification

**Commands:**
- `./gradlew check test spotlessCheck lintJs` -- expected: `BUILD SUCCESSFUL` with all unit/integration tests and linters passing.
