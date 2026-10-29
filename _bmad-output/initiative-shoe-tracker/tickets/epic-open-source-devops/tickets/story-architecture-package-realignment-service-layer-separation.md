---
id: 8
type: story
title: "Architecture Package Realignment (Service Layer Separation)"
parent: epic-open-source-devops
covers: [R17]
after: [6]
hitl: false
risk: low
---

# Architecture Package Realignment (Service Layer Separation)

## Description

Moves SightingService out of org.shoetracker.domain into dedicated org.shoetracker.service package, restoring pure DDD boundaries, and documents the Quarkus META-INF/resources static asset convention.

## Requirements

Verify: Domain package contains zero outward dependencies, SightingService is cleanly positioned in org.shoetracker.service, and all tests pass.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-open-source-devops/epic-open-source-devops.md
