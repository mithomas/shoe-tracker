---
type: story
title: "GitHub Actions CI Workflow and Docker Hub Publication"
parent: epic-core-capture
covers: []
sources: []
companions: []
after: []
risk: low
refined: false
---

# GitHub Actions CI Workflow and Docker Hub Publication

## Description

Configures an automated GitHub Actions CI/CD workflow that tests the application, builds the single-container OCI image, and publishes it to Docker Hub on release or push to main.

## Acceptance Criteria

- `.github/workflows/ci.yml` runs `./gradlew test` on every pull request and push to `main`.
- Multi-stage Dockerfile is built and verified.
- Authenticates with Docker Hub using repository secrets (`DOCKERHUB_USERNAME`, `DOCKERHUB_TOKEN`) and publishes tagged images (`latest`, version tag).
