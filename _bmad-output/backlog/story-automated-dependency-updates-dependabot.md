---
type: story
title: "Automated Dependency Update Workflow (Dependabot)"
parent: ""
covers: []
sources: []
companions: []
after: []
risk: low
refined: false
---

# Automated Dependency Update Workflow (Dependabot)

## Description

Configures an automated GitHub Dependabot workflow to monitor, update, and patch project dependencies across Gradle libraries, Docker base images, and GitHub Actions workflows, automatically opening pull requests with changelogs when new versions are available.

## Background & Rationale

Keeping project libraries (Quarkus, Kotlin, Agroal, JDBC drivers) and container base images (Eclipse Temurin) up-to-date is critical for security patch management, performance improvements, and avoiding dependency debt. Automating this via Dependabot ensures dependencies are updated continuously on a predictable weekly schedule and verified by automated CI test runs.

## Acceptance Criteria

- Create `.github/dependabot.yml` configured for the repository:
  1. **Gradle Ecosystem:**
     - `package-ecosystem: "gradle"`
     - `directory: "/"`
     - `schedule.interval: "weekly"`
     - Monitors Quarkus platform BOM, Kotlin compiler, JDBC drivers, and testing libraries.
  2. **Docker Ecosystem:**
     - `package-ecosystem: "docker"`
     - `directory: "/"`
     - `schedule.interval: "weekly"`
     - Monitors `eclipse-temurin` base image tags in `Dockerfile`.
  3. **GitHub Actions Ecosystem:**
     - `package-ecosystem: "github-actions"`
     - `directory: "/"`
     - `schedule.interval: "weekly"`
     - Monitors workflow actions (`actions/checkout`, `actions/setup-java`, etc.).
- Pull requests opened by Dependabot automatically trigger the CI test workflow (`./gradlew check test`).
- Commit messages and PR labels are standardized (e.g., `dependencies`, `security`).
