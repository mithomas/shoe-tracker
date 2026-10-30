---
title: 'Sync Repository README.md to Docker Hub Repository Overview in CI/CD'
type: 'feature'
ticket: '8'
created: '2026-10-30'
status: 'built'
baseline_revision: '9b64be07b6d2f0f349b2e9dad916fa5f22515a20'
route: 'oneshot'
route_source: 'auto'
risk: 'low'
review: 'quick'
review_source: 'pinned'
lenses_ran: ['quick']
review_loop_iteration: 1
context: []
---

<frozen-after-approval reason="human-owned intent - do not modify unless human renegotiates">

## Intent

**Problem:** The Docker Hub repository overview for `${{ secrets.DOCKERHUB_USERNAME }}/shoe-tracker` is not automatically updated when `README.md` changes on `main` or release tags.

**Approach:** Add a `Sync README.md to Docker Hub overview` step using `peter-evans/dockerhub-description@v5` guarded by `if: env.DOCKERHUB_USERNAME != '' && env.DOCKERHUB_TOKEN != ''` at the end of the `release` job in `.github/workflows/ci.yml`, and assert the step in `src/test/kotlin/org/shoetracker/devops/DevOpsAssetTest.kt`.

</frozen-after-approval>

## Implementation Notes

Selected `oneshot` route because the change adds 11 lines to `.github/workflows/ci.yml` and 5 assertion lines to `DevOpsAssetTest.kt`.
- Added `Sync README.md to Docker Hub overview` (`peter-evans/dockerhub-description@v5`) to `.github/workflows/ci.yml`.
- Added assertions in `testGitHubActionsWorkflowConfiguration()` in `src/test/kotlin/org/shoetracker/devops/DevOpsAssetTest.kt`.

## Plan Change Log

## Review Triage Log

### Iteration 1 (`quick`)
- 0 findings reported.

## Verification

**Commands:**
- `./gradlew check test spotlessCheck lintJs` -- expected: exit code 0 with all DevOps and frontend/backend tests passing
