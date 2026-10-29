---
title: 'Git Commit Hash Capture & Version Display'
type: 'feature'
ticket: '2'
created: '2026-10-29'
status: 'built'
baseline_revision: '593f825f98632b31166783cabde70e055ecd6102'
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
  - '_bmad-output/initiative-shoe-tracker/tickets/epic-open-source-devops/epic-open-source-devops.md'
---

<frozen-after-approval reason="human-owned intent - do not modify unless human renegotiates">

## Intent

**Problem:** The application displays a static version string ("Shoe Tracker v1.0.0") without identifying the underlying Git revision. During rapid road-trip iterations and deployments, operators and users cannot identify which exact Git commit is running.

**Approach:**
1. In `build.gradle.kts`, create a `generateVersionProperties` resource generation task that resolves the 7-character Git commit hash (via `git rev-parse --short=7 HEAD` with `GIT_COMMIT` environment variable fallback) and writes `version.properties`.
2. In `Dockerfile`, add `ARG GIT_COMMIT=""` and `ENV GIT_COMMIT=${GIT_COMMIT}` so Docker builds can receive and stamp the commit hash without copying `.git`.
3. Create `VersionResource` exposing `GET /api/v1/version` returning JSON `{ "version": ..., "commit": ... }`.
4. In `index.html`, give version text spans the `.app-version-text` class and asynchronously populate with `Shoe Tracker v1.0.0 (<commit-hash>)` on startup.
5. Add automated integration tests verifying `/api/v1/version` returns HTTP 200 with a valid 7-character commit hash, and that frontend assets contain version updating logic.

</frozen-after-approval>

## Implementation Notes

Oneshot route selected: straightforward Gradle build stamping task, REST endpoint, and frontend metadata display touching under 100 lines.

## Review Triage Log

| Lens | Finding | Verdict | Route | Evidence / Rationale |
|---|---|---|---|---|
| quick | `build.gradle.kts:51` `generateVersionProperties` had no input tracking, causing Gradle caching issues | medium | patch | Added `outputs.upToDateWhen { false }` to always generate current commit hash on build. |
| quick | `build.gradle.kts:59` Empty `GIT_COMMIT` env var bypassed fallback in Docker builds | medium | patch | Enforced `it.length >= 7` before taking hash and fallback to `"unknown"` across build and `VersionResource.kt`. |
| quick | `build.gradle.kts:59` Missing minimum length validation on `GIT_COMMIT` | low | patch | Solved with `it.length >= 7` check. |
| quick | `VersionResourceTest.kt:20` Permitted `"unknown"` in test assertion | medium | patch | Strictly enforced 7-character hexadecimal pattern `^[0-9a-f]{7}$`. |
| quick | `FrontendAssetTest.kt:35` Missing assertion on client version updating logic | low | patch | Added unit assertion `testFrontendVersionFormattingLogic()`. |
| quick | `index.html:230` `data.version` ignored in footer update | low | patch | Formatted version string using `data.version` from API. |

## Verification

**Commands:**
- `./gradlew test` -- expected: All integration and frontend asset tests pass.
- `curl -s http://localhost:8080/api/v1/version` -- expected: HTTP 200 with version and commit hash fields.
