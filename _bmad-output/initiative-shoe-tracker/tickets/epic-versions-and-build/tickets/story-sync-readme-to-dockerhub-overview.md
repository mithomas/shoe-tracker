---
id: 8
type: story
title: "Sync Repository README.md to Docker Hub Repository Overview in CI/CD"
parent: "epic-versions-and-build"
covers: ["R38", "R39"]
sources: []
companions: []
after: [1, 2, 3, 4]
refined: true
hitl: false
risk: low
---

# Sync Repository README.md to Docker Hub Repository Overview in CI/CD

## Description

Adds an automated step (`peter-evans/dockerhub-description@v5`) to the `release` job in `.github/workflows/ci.yml` that synchronizes `README.md` (and a concise short description) to the Docker Hub repository overview for `${{ secrets.DOCKERHUB_USERNAME }}/shoe-tracker` whenever the container image is published to Docker Hub, so the Docker Hub page always reflects current documentation, environment variables, and usage instructions.

## Requirements

1. **Automated Docker Hub Overview Synchronization in CI/CD**
   **Given** the `release` job in `.github/workflows/ci.yml` runs on a `push` to `main` or `v*` tag and `DOCKERHUB_USERNAME` and `DOCKERHUB_TOKEN` secrets are configured
   **When** the container image build and push step succeeds
   **Then** a workflow step (`peter-evans/dockerhub-description@v5`) updates the Docker Hub repository overview for `${{ secrets.DOCKERHUB_USERNAME }}/shoe-tracker` from `./README.md` with `enable-url-completion: true`

2. **Graceful Secret Guard Matching Existing Release Steps**
   **Given** `DOCKERHUB_USERNAME` or `DOCKERHUB_TOKEN` is empty (such as in forks or unconfigured environments)
   **When** the `release` job executes
   **Then** the Docker Hub overview sync step is skipped cleanly via `if: env.DOCKERHUB_USERNAME != '' && env.DOCKERHUB_TOKEN != ''` without failing the workflow

3. **Asset Test Verification**
   **Given** `DevOpsAssetTest.kt` verifies `.github/workflows/ci.yml`
   **When** `./gradlew check test spotlessCheck lintJs` is executed
   **Then** `DevOpsAssetTest.kt` asserts that `.github/workflows/ci.yml` includes the Docker Hub README synchronization step (`peter-evans/dockerhub-description@v5`)

## Boundaries

- Must not change: Existing container image tag rules (`dev`, semver tags, `latest`) or `linux/amd64` build configuration in `.github/workflows/ci.yml`.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-versions-and-build/epic-versions-and-build.md, section Requirements
- workflow - .github/workflows/ci.yml

## Notes

- Decision: 2026-10-30: Use `peter-evans/dockerhub-description@v5` (Node 24) in `.github/workflows/ci.yml` after `Build and push container image`.
