---
title: 'Always Publish a Dev Container Tag on Main Branch Pushes'
type: 'feature'
ticket: 'story-always-publish-dev-container-tag'
created: '2026-10-29'
status: 'done'
baseline_revision: 'ee79e76b424345b036eb7d4def43d96836112023'
route: 'oneshot'
route_source: 'auto'
risk: 'low'
review: ''
review_source: ''
lenses_ran: []
review_loop_iteration: 0
context: ["/home/tom/development/personal/shoe-tracker/bmad/_bmad-output/backlog/story-always-publish-dev-container-tag.md"]
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** `.github/workflows/ci.yml` only triggers the `release` container publishing job on `refs/tags/v*` and fails unconditionally if `DOCKERHUB_USERNAME` or `DOCKERHUB_TOKEN` secrets are not set, preventing automatic `:dev` image builds on `main` pushes and breaking unconfigured repositories.

**Approach:** Update `.github/workflows/ci.yml` so the `release` job runs on `github.event_name == 'push' && (github.ref == 'refs/heads/main' || startsWith(github.ref, 'refs/tags/v'))`, add a `type=raw,value=dev` tag rule in `docker/metadata-action@v5`, guard Docker Hub login and push steps when Docker Hub secrets are absent, and update `DevOpsAssetTest.kt` to assert the `dev` tag rule.

## Boundaries & Constraints

**Always:**
- Trigger the `release` job on `push` to `main` (`refs/heads/main`) or `v*` tags (`refs/tags/v*`), never on `pull_request`
- Publish `type=raw,value=dev` via `docker/metadata-action@v5` alongside existing `type=semver,pattern={{version}}`, `type=semver,pattern={{major}}.{{minor}}`, and `type=raw,value=latest,enable=${{ startsWith(github.ref, 'refs/tags/v') && !contains(github.ref, '-rc') && !contains(github.ref, '-beta') }}`
- Guard Docker Hub login and push when `secrets.DOCKERHUB_USERNAME` or `secrets.DOCKERHUB_TOKEN` are empty so pushes without Docker Hub credentials succeed without failing the workflow
- Keep all existing assertions in `DevOpsAssetTest.kt` intact while adding assertions for the `dev` tag configuration

**Never:**
- Do not modify files outside `.github/workflows/ci.yml`, `src/test/kotlin/org/shoetracker/devops/DevOpsAssetTest.kt`, and this plan file
- Do not run `./gradlew` concurrently while the Story 6.1 Implementer subagent is running Gradle

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior |
|----------|---------------|----------------------------|
| Push to `main` with secrets set | `github.ref == 'refs/heads/main'` | Publishes `<DOCKERHUB_USERNAME>/shoe-tracker:dev` (does not publish `latest`) |
| Push tag `v1.2.0` with secrets set | `github.ref == 'refs/tags/v1.2.0'` | Publishes `1.2.0`, `1.2`, `latest`, and `dev` |
| Push to `main` or `v*` without secrets | `DOCKERHUB_TOKEN` unset | Skips login/push cleanly without failing workflow |
| Pull request to `main` | `github.event_name == 'pull_request'` | `release` job is skipped |

</frozen-after-approval>

## Code Map

- `.github/workflows/ci.yml` -- `release` job -- Trigger on `main` and `v*` pushes, add `dev` tag rule, and guard missing Docker Hub secrets
- `src/test/kotlin/org/shoetracker/devops/DevOpsAssetTest.kt` -- `testGitHubActionsWorkflowConfiguration` -- Assert `type=raw,value=dev` and `refs/heads/main` in `ci.yml`

## Tasks & Acceptance

**Execution:**
- [x] `.github/workflows/ci.yml` -- Update `release` job `if:` condition, `docker/metadata-action@v5` tags (`type=raw,value=dev`), and secret check guard -- Enables `:dev` tag publishing on `main` and safe no-secret fallback
- [x] `src/test/kotlin/org/shoetracker/devops/DevOpsAssetTest.kt` -- Add assertions for `type=raw,value=dev` and `refs/heads/main` in `testGitHubActionsWorkflowConfiguration()` -- Verifies CI configuration contract

**Acceptance Criteria:**
- Given `.github/workflows/ci.yml`, when a commit is pushed to `main` or a `v*` tag, then `docker/metadata-action` includes `type=raw,value=dev` and `latest` is only enabled on non-prerelease `v*` tags.
- Given `.github/workflows/ci.yml`, when `DOCKERHUB_USERNAME` or `DOCKERHUB_TOKEN` is not configured, then login and push steps are safely skipped without failing the workflow.

## Implementation Notes

Oneshot route: small, isolated 2-file CI workflow and test assertion update.
- Updated `.github/workflows/ci.yml` `release` job `if:` condition to `github.event_name == 'push' && (github.ref == 'refs/heads/main' || startsWith(github.ref, 'refs/tags/v'))`.
- Added job-level `env` mappings for `DOCKERHUB_USERNAME` and `DOCKERHUB_TOKEN` and guarded `docker/login-action@v3`, `docker/metadata-action@v5`, and `docker/build-push-action@v6` steps with `if: env.DOCKERHUB_USERNAME != '' && env.DOCKERHUB_TOKEN != ''`.
- Added `type=raw,value=dev` to `docker/metadata-action@v5` tags and scoped `latest` enable condition to `startsWith(github.ref, 'refs/tags/v') && !contains(github.ref, '-rc') && !contains(github.ref, '-beta')`.
- Updated `DevOpsAssetTest.kt` `testGitHubActionsWorkflowConfiguration` to assert `refs/heads/main`, `startsWith(github.ref, 'refs/tags/v')`, `docker/metadata-action@v5`, `type=raw,value=dev`, and the `DOCKERHUB_USERNAME` / `DOCKERHUB_TOKEN` guard.

## Plan Change Log

## Review Triage Log

## Verification

**Commands:**
- Inspect `.github/workflows/ci.yml` and `src/test/kotlin/org/shoetracker/devops/DevOpsAssetTest.kt` (Gradle test execution will run once Story 6.1's Gradle run finishes).
