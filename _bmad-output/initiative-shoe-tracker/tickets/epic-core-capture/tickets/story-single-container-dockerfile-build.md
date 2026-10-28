---
id: 4
type: story
title: "Single-container Dockerfile build"
parent: epic-core-capture
covers: [R6]
after: [3]
hitl: false
risk: low
---

# Single-container Dockerfile build

## Description

Creates multi-stage Dockerfile compiling Vite frontend and Quarkus backend into a single runnable container.

## Requirements

Verify: Container builds with docker/podman, starts, and serves frontend at / and API at /api/v1/sightings on port 8080.

## References

- parent — _bmad-output/initiative-shoe-tracker/tickets/epic-core-capture/epic-core-capture.md
