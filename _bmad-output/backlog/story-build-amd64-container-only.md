---
type: story
title: "Build and Publish Only linux/amd64 Container Image in CI Workflow"
parent: ""
covers: []
sources: []
companions: []
after: []
risk: low
hitl: false
refined: true
---

# Build and Publish Only linux/amd64 Container Image in CI Workflow

## Description

Updates the GitHub Actions Docker Hub release job in `.github/workflows/ci.yml` to build and publish only the `linux/amd64` container platform (removing `linux/arm64` cross-compilation and unnecessary QEMU emulation overhead).

## Requirements

1. **Restrict Container Build Target to `linux/amd64`**
   **Given** `.github/workflows/ci.yml` builds and pushes container images via `docker/build-push-action` in the `release` job
   **When** the `release` job runs on `main` pushes or `v*` tags
   **Then** `platforms` is set to `linux/amd64` only (removing `linux/arm64`)
   **And** the `docker/setup-qemu-action` step is removed since native `amd64` runners do not require QEMU emulation

2. **Automated DevOps Asset Test Alignment**
   **Given** `DevOpsAssetTest.kt` validates `.github/workflows/ci.yml`
   **When** `./gradlew check test` runs
   **Then** `testGitHubActionsWorkflowConfiguration` asserts `platforms: linux/amd64` and verifies that `linux/arm64` is not present

## Boundaries

- Must not change: Tag metadata rules (`dev`, `{{version}}`, `{{major}}.{{minor}}`, `latest`), GitHub Actions Buildx layer caching (`type=gha`), or `GIT_COMMIT` build-arg injection.
