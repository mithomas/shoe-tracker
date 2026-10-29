---
title: 'Add Spotless, Closure Compiler, and Container Checks to CI Workflow'
type: 'chore'
ticket: '6'
created: '2026-10-29'
status: 'built'
baseline_revision: '00d0eae1b3260beeeaf9fc3251e06fa531278149'
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

**Problem:** Currently, `.github/workflows/ci.yml` runs `./gradlew check test --no-daemon`. While `check` triggers `spotlessCheck` and `lintJs`, they are not highlighted as discrete pipeline steps with dedicated visibility in GitHub Actions logs. Furthermore, container builds are only triggered on release tags (`v*`), meaning a broken `Dockerfile` or missing asset can pass CI on a pull request and only fail at release time.

**Approach:**
1. Update `.github/workflows/ci.yml` to split checks into clear, dedicated pipeline steps:
   - Run Spotless code formatting verification: `./gradlew spotlessCheck --no-daemon`
   - Run Closure Compiler JavaScript linting: `./gradlew lintJs --no-daemon`
   - Run tests & check suite: `./gradlew check test --no-daemon`
   - Verify Docker container build on every PR and main push: `docker build --build-arg GIT_COMMIT=${{ github.sha }} -t shoe-tracker:ci .`
2. Update `DevOpsAssetTest.kt` to assert the presence of these dedicated linter, checker, and container build steps in `.github/workflows/ci.yml`.
3. Validate YAML syntax and verify that `./gradlew test` and Spotless checks pass cleanly.

</frozen-after-approval>

## Implementation Notes

Oneshot route selected: Updating `.github/workflows/ci.yml` and `DevOpsAssetTest.kt`.
- Updated `.github/workflows/ci.yml` test job to include discrete steps for `spotlessCheck`, `lintJs`, and local PR container build verification (`docker build`).
- Validated YAML syntax with PyYAML.
- Updated `DevOpsAssetTest.kt` to verify that the GitHub Actions workflow contains all new checker, linter, and container build steps.

## Plan Change Log

## Review Triage Log

| Lens | Finding | Verdict | Route | Evidence / Rationale |
|---|---|---|---|---|
| quick | Container build argument consistency | pass | - | Container verification step in CI uses `--build-arg GIT_COMMIT=${{ github.sha }}` matching the release job. |
| quick | YAML structure and step ordering | pass | - | Fast linting steps run before full checks and container builds, failing early on formatting/lint errors. |

## Verification

**Commands:**
- `uv run --with pyyaml python -c "import yaml; yaml.safe_load(open('.github/workflows/ci.yml'))"` -- expected: YAML parses cleanly without syntax errors.
- `./gradlew test --tests org.shoetracker.devops.DevOpsAssetTest` -- expected: All DevOps asset tests pass including new CI step assertions.
- `./gradlew check test spotlessCheck lintJs` -- expected: Full test and hygiene suite passes with zero violations.
