---
id: 4
type: story
title: "Update Docker Hub Username to mithomas and Image Name to shoe-tracker in Examples and README"
parent: epic-versions-and-build
covers: [R39]
after: [1, 2, 3]
hitl: false
risk: low
---

# Update Docker Hub Username to mithomas and Image Name to shoe-tracker in Examples and README

## Description

Updates all Docker Hub image references, container run examples, and `docker-compose.yml` defaults across project documentation and configuration files to use the official Docker Hub username `mithomas` and image name `shoe-tracker` (`mithomas/shoe-tracker`).

## Requirements

- In `docker-compose.yml`:
  - Update `image: ${DOCKERHUB_IMAGE:-mitch/shoe-tracker:latest}` to `image: ${DOCKERHUB_IMAGE:-mithomas/shoe-tracker:latest}`.
- In `README.md`:
  - Document `mithomas/shoe-tracker:latest` and `mithomas/shoe-tracker:dev` in the Docker / Docker Compose deployment section.
  - Preserve `https://github.com/mitch/shoe-tracker` for the GitHub source repository link.
- In `src/test/kotlin/org/shoetracker/devops/DevOpsAssetTest.kt`:
  - Update `testDockerComposeConfiguration` to assert that `docker-compose.yml` and `README.md` contain `mithomas/shoe-tracker` and do not contain `mitch/shoe-tracker`.
- Verify: `docker-compose.yml` and `README.md` reference `mithomas/shoe-tracker` with zero `mitch/shoe-tracker` Docker Hub references (while preserving `github.com/mitch/shoe-tracker` source links), and `DevOpsAssetTest` passes.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-versions-and-build/epic-versions-and-build.md
