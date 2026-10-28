---
type: story
title: "Include Latest Git Commit Hash in Version Display"
parent: ""
covers: []
sources: []
companions: []
after: []
risk: low
refined: false
---

# Include Latest Git Commit Hash in Version Display

## Description

Includes the latest Git commit short hash in the application version display (e.g. `Shoe Tracker v1.0.0 (36183c8)`), ensuring deployed containers and browser clients can unambiguously identify the exact source revision running in production.

## Background & Rationale

Currently, the footer renders a static version badge (`Shoe Tracker v1.0.0`). During rapid road-trip iterations, bug reports, and CI/CD deployments, having the exact 7-character Git commit hash displayed in the client UI and exposed via an API health endpoint makes troubleshooting and verification immediate.

## Acceptance Criteria

- Gradle build captures the current Git short commit hash during compilation (e.g., via lightweight task writing to a properties file in `resources`).
- In Docker builds, the Git commit hash is passed via build argument or stamped during build.
- Backend exposes version metadata (version string and commit hash) via `/api/v1/version` or embeds it in the client assets.
- Footer badge in `index.html` displays the format: `Shoe Tracker v1.0.0 (<commit-hash>)`.
- Automated tests verify that the version string contains a valid 7-character hexadecimal Git commit hash.
