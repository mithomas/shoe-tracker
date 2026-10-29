---
type: story
title: "Always Publish a Dev Container Tag on Main Branch Pushes"
parent: ""
covers: []
sources: []
companions: []
after: []
risk: low
refined: false
---

# Always Publish a Dev Container Tag on Main Branch Pushes

## Description

Updates the GitHub Actions CI/CD workflow (`.github/workflows/ci.yml`) so that pushes to the `main` branch automatically build and publish a mutable `dev` container tag (`<DOCKERHUB_USERNAME>/shoe-tracker:dev`) to Docker Hub, while preserving semantic version tag publishing (`{{version}}`, `{{major}}.{{minor}}`, and `latest`) for `v*` release tags.

## Requirements

- Trigger the container publishing step/job in `.github/workflows/ci.yml` on pushes to `main` (`github.ref == 'refs/heads/main'`) in addition to `v*` release tags (`startsWith(github.ref, 'refs/tags/v')`).
- Configure `docker/metadata-action` (or tag rules) so:
  - Pushes to `main` publish `<DOCKERHUB_USERNAME>/shoe-tracker:dev` (and do not overwrite `latest` or semver tags).
  - Pushes of `v*` tags continue publishing `<DOCKERHUB_USERNAME>/shoe-tracker:{{version}}`, `{{major}}.{{minor}}`, and `latest` (for non-prerelease tags), or also update `dev` if desired.
- Gracefully skip or guard Docker Hub login/push when `DOCKERHUB_USERNAME` or `DOCKERHUB_TOKEN` secrets are not configured so fork/unconfigured branch builds do not fail CI checks unnecessarily.
- Update `DevOpsAssetTest.kt` to verify the `dev` tag publishing rule in `.github/workflows/ci.yml`.

## Boundaries

- Pull requests (`pull_request` event) must never push images to Docker Hub or expose registry credentials.
- Must preserve multi-platform (`linux/amd64,linux/arm64`) build and `GIT_COMMIT` build-arg injection.
