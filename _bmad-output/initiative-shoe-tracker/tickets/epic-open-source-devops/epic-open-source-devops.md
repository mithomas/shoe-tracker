---
type: epic
title: "Open Source Licensing & CI/CD Publication"
parent: "initiative-shoe-tracker"
covers: ["R10", "R11", "R12", "R13", "R14"]
sources: []
companions: []
after: ["epic-views-and-map"]
refined: true
risk: low
---

# Open Source Licensing & CI/CD Publication

## Description

Adopts the GNU Affero General Public License (AGPL-3.0) for the repository, establishes a GitHub Actions workflow for automated test execution, publishes multi-platform container images to Docker Hub on release tags, exposes the Git commit hash in the version display, and configures automated Dependabot dependency updates.

## Outcome

Shoe Tracker is legally protected as copyleft open-source software, continuously tested and published to Docker Hub upon release, stamped with traceable Git commit metadata, and kept up-to-date automatically.

## Requirements

- R10: Repository and application artifacts are licensed under the GNU Affero General Public License v3.0 (AGPL-3.0) with copyleft notice and source code availability link in web footer.
- R11: Continuous Integration workflow executes Gradle check and test suites on every pull request and push to main.
- R12: Release workflow builds and publishes multi-platform container images to Docker Hub on semantic version tags (`v*`).
- R13: Version display in footer and `/api/v1/version` reports current Git short commit hash.
- R14: Dependabot monitors Gradle, Docker, and GitHub Actions dependencies weekly.

## Done when

- `LICENSE` file contains full text of GNU Affero General Public License v3 (AGPL-3.0).
- `README.md` documents AGPL-3.0 licensing and Docker Compose deployment instructions.
- Web interface footer links to source code availability per AGPL network copyleft terms.
- Application version badge and `/api/v1/version` output current Git short commit hash.
- `.github/workflows/ci.yml` runs `./gradlew check test` on PRs and main branch pushes.
- Release workflow pushes `<dockerhub-user>/shoe-tracker:<version>` and `:latest` images to Docker Hub on `v*` tags.
- `.github/dependabot.yml` monitors Gradle, Docker, and GitHub Actions dependencies.

## Boundaries

- Frontend UI feature enhancements and database migrations belong to other epics.

## References

- parent - _bmad-output/initiative-shoe-tracker/initiative-shoe-tracker.md
- architecture - _bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md

## Notes

- Decision: 2026-10-29: Uncoupled from Epic 3 to build immediately following Epic 2 (`epic-views-and-map`).
- Decision: 2026-10-29: Default Docker Hub target repository set to `${{ secrets.DOCKERHUB_USERNAME }}/shoe-tracker`.
- Decision: 2026-10-29: AGPL-3.0 copyleft source code link placed in web interface footer.
