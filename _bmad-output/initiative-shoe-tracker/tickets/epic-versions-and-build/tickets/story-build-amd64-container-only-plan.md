---
title: 'Build and Publish Only linux/amd64 Container Image in CI Workflow'
type: 'feature'
ticket: 3
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

**Problem:** The `release` job in `.github/workflows/ci.yml` cross-compiles `linux/arm64` via QEMU emulation (`docker/setup-qemu-action@v3`), adding substantial build latency when only `linux/amd64` container images are needed.

**Approach:** Rename the `release` job to `Publish Container to Docker Hub`, remove the `docker/setup-qemu-action@v3` step, set `platforms: linux/amd64` on `docker/build-push-action@v6`, and update `DevOpsAssetTest.kt` to assert `platforms: linux/amd64` and forbid `linux/arm64` and `setup-qemu-action`.

</frozen-after-approval>

## Implementation Notes

- Native `ubuntu-latest` (`amd64`) runner builds `linux/amd64` directly via Buildx without QEMU overhead.

## Plan Change Log

## Review Triage Log

## Verification

**Commands:**
- `./gradlew check test spotlessCheck lintJs` -- expected: `BUILD SUCCESSFUL` with all unit/integration tests and linters passing.
