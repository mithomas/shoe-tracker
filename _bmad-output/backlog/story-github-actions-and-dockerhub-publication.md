---
type: story
title: "GitHub Actions CI Workflow and Docker Hub Publication"
parent: ""
covers: []
sources: []
companions: []
after: []
risk: low
refined: false
---

# GitHub Actions CI Workflow and Docker Hub Publication

## Description

Configures an automated GitHub Actions CI/CD workflow that runs all Gradle tests on every pull request and push to main, builds the single-container multi-stage Docker image on Java 25, and automatically publishes tagged images to Docker Hub upon release.

## Background & Rationale

Automating the build and release pipeline ensures continuous quality verification and allows users and server operators to pull and run `docker pull <username>/shoe-tracker:latest` directly without compiling from source locally.

## Acceptance Criteria

- Create `.github/workflows/ci.yml` triggered on pushes to `main`, pull requests, and Git release tags (`v*`).
- Automated CI pipeline steps:
  1. Sets up JDK 21/25 with Gradle caching.
  2. Executes `./gradlew check test` verifying all unit, integration, and asset tests.
  3. Sets up Docker Buildx.
  4. Builds multi-stage container image using root `Dockerfile`.
- Release publishing:
  - On version tags (e.g. `v1.0.0`), authenticates to Docker Hub using repository secrets `DOCKERHUB_USERNAME` and `DOCKERHUB_TOKEN`.
  - Publishes both semantic version tag (`<user>/shoe-tracker:1.0.0`) and `latest` tag.
- Workflow completes cleanly in GitHub Actions without proprietary dependency blockers.
