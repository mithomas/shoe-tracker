---
id: 3
type: story
title: "Build and Publish Only linux/amd64 Container Image in CI Workflow"
parent: epic-versions-and-build
covers: [R38]
after: [1, 2]
hitl: false
risk: low
---

# Build and Publish Only linux/amd64 Container Image in CI Workflow

## Description

Updates the GitHub Actions Docker Hub release job in `.github/workflows/ci.yml` to build and publish only the `linux/amd64` container platform (removing `linux/arm64` cross-compilation, `docker/setup-qemu-action@v3` overhead, and stale "Multi-Platform" job naming).

## Requirements

- In `.github/workflows/ci.yml`:
  - Update `release` job `name:` from `Publish Multi-Platform Container to Docker Hub` to `Publish Container to Docker Hub`.
  - Remove the `Set up QEMU` (`docker/setup-qemu-action@v3`) step.
  - Set `platforms: linux/amd64` on `docker/build-push-action@v6` (removing `linux/arm64`).
- In `src/test/kotlin/org/shoetracker/devops/DevOpsAssetTest.kt`:
  - Update `testGitHubActionsWorkflowConfiguration` to assert `.contains("platforms: linux/amd64")`, `.doesNotContain("linux/arm64")`, and `.doesNotContain("setup-qemu-action")` (removing the old `.contains("docker/setup-qemu-action@v3")` and `.contains("linux/amd64,linux/arm64")` assertions).
- Verify: `.github/workflows/ci.yml` builds only `linux/amd64` without QEMU setup or `linux/arm64`, and `DevOpsAssetTest` passes.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-versions-and-build/epic-versions-and-build.md
