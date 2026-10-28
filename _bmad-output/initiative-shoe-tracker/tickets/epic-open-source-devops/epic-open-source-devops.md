---
type: epic
title: "Open Source Licensing & CI/CD Publication"
parent: "initiative-shoe-tracker"
covers: []
sources: []
companions: []
after: ["epic-spotting-polish"]
refined: false
risk: low
---

# Open Source Licensing & CI/CD Publication

## Description

Adopts the GNU Affero General Public License (AGPL-3.0) for the repository, establishes a GitHub Actions workflow for automated test execution, and publishes multi-stage container images to Docker Hub on release tags.

## Outcome

Shoe Tracker is legally protected as copyleft open-source software and automatically tested and published to Docker Hub upon release.

## Done when

- `LICENSE` file contains full text of GNU Affero General Public License v3 (AGPL-3.0).
- Project documentation and headers reference AGPL-3.0 licensing.
- `.github/workflows/ci.yml` runs `./gradlew test` on every pull request and push to main.
- Workflow builds the multi-stage container image and pushes to Docker Hub upon tagged releases.

## Boundaries

- Application features and UI polish belong to earlier epics.

## References

- parent - _bmad-output/initiative-shoe-tracker/initiative-shoe-tracker.md
