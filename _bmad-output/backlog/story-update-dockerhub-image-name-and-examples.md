---
type: story
title: "Update Docker Hub Username to mithomas and Image Name to shoe-tracker in Examples and README"
parent: "epic-versions-and-build"
covers: ["R39"]
sources: []
companions: []
after: ["story-build-amd64-container-only.md"]
risk: low
hitl: false
refined: true
---

# Update Docker Hub Username to mithomas and Image Name to shoe-tracker in Examples and README

## Description

Updates all Docker Hub image references, container run examples, and `docker-compose.yml` defaults across project documentation and configuration files to use the official Docker Hub username `mithomas` and image name `shoe-tracker` (`mithomas/shoe-tracker`).

## Requirements

1. **Default Image in Docker Compose**
   **Given** `docker-compose.yml` defines the container image with an environment fallback
   **When** `DOCKERHUB_IMAGE` is not explicitly set
   **Then** the default image reference resolves to `mithomas/shoe-tracker:latest` (`${DOCKERHUB_IMAGE:-mithomas/shoe-tracker:latest}`) instead of `mitch/shoe-tracker:latest`

2. **README Container and Compose Examples**
   **Given** `README.md` documents running Shoe Tracker via Docker and Docker Compose
   **When** a user reads the container deployment and quickstart examples
   **Then** the documentation explicitly references `mithomas/shoe-tracker` (`mithomas/shoe-tracker:latest` and `mithomas/shoe-tracker:dev`) as the published Docker Hub image
   **And** no Docker Hub image example uses the placeholder `mitch/shoe-tracker`

3. **Automated DevOps Asset Verification**
   **Given** `DevOpsAssetTest.kt` validates `docker-compose.yml` and `README.md`
   **When** `./gradlew check test` runs
   **Then** `testDockerComposeConfiguration` asserts that `docker-compose.yml` and `README.md` reference `mithomas/shoe-tracker` and do not contain `mitch/shoe-tracker`

## Boundaries

- Must not change: GitHub repository links (`https://github.com/mitch/shoe-tracker` in `README.md` and `index.html`) or `.github/workflows/ci.yml` secret interpolation (`${{ secrets.DOCKERHUB_USERNAME }}/shoe-tracker`).
