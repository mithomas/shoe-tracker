---
id: 2
type: story
title: "Document Mock GPS Simulation Mode in README"
parent: "epic-mock-gps-and-testing-docs"
covers: ["R50"]
sources: []
companions: []
after: [1]
refined: true
hitl: false
risk: low
---

# Document Mock GPS Simulation Mode in README

## Description

Documents the built-in mock GPS simulation mode (`?mockGps=true`, static custom coordinates via `?mockGps=<lat>,<lon>`, and disabling via `?mockGps=false`) in `README.md` so users and developers can test Shoe Tracker locally even when OS or browser location services are blocked.

## Requirements

1. **README Documentation for Simulated GPS Testing**
   **Given** `README.md` contains Quickstart and manual testing instructions
   **When** a developer or user reviews `README.md` for local testing without live GPS hardware or OS location permissions
   **Then** `README.md` documents how to activate simulated moving highway GPS via `?mockGps=true`, how to supply non-persisted static coordinates via `?mockGps=<lat>,<lon>`, and how to exit persisted mock mode via `?mockGps=false`

## Boundaries

- Must not change: Existing cURL, Gradle, Docker Compose, or GraalVM native build instructions in `README.md`.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-mock-gps-and-testing-docs/epic-mock-gps-and-testing-docs.md, section Requirements
