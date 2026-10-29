---
type: story
title: "Build and Publish Only linux/amd64 Container Image in CI Workflow"
parent: "epic-versions-and-build"
covers: ["R38"]
sources: []
companions: []
after: ["story-upgrade-github-actions-to-v5-node24.md"]
risk: low
hitl: false
refined: true
---

# Build and Publish Only linux/amd64 Container Image in CI Workflow

## Description

Updates the GitHub Actions Docker Hub release job in `.github/workflows/ci.yml` to build and publish only the `linux/amd64` container platform (removing `linux/arm64` cross-compilation, `docker/setup-qemu-action@v3` overhead, and stale "Multi-Platform" job naming).

## Requirements

1. **Restrict Container Build Target to `linux/amd64` and Remove QEMU Step**
   **Given** `.github/workflows/ci.yml` builds and pushes container images via `docker/build-push-action@v6` in the `release` job
   **When** the `release` job runs on `main` pushes or `v*` tags
   **Then** `platforms` is set to `linux/amd64` only (removing `linux/arm64`)
   **And** the `Set up QEMU` (`docker/setup-qemu-action@v3`) step is removed
   **And** the `release` job `name:` is updated from `Publish Multi-Platform Container to Docker Hub` to `Publish Container to Docker Hub`

2. **Automated DevOps Asset Test Alignment**
   **Given** `DevOpsAssetTest.kt` validates `.github/workflows/ci.yml`
   **When** `./gradlew check test` runs
   **Then** `testGitHubActionsWorkflowConfiguration` replaces `.contains("docker/setup-qemu-action@v3")` and `.contains("linux/amd64,linux/arm64")` with `.contains("platforms: linux/amd64")`, `.doesNotContain("linux/arm64")`, and `.doesNotContain("setup-qemu-action")`

## Boundaries

- Must not change: Tag metadata rules (`dev`, `{{version}}`, `{{major}}.{{minor}}`, `latest`), GitHub Actions Buildx layer caching (`type=gha`), or `GIT_COMMIT` build-arg injection.
