---
type: epic
title: "Architecture Hardening & Offline Resilience"
parent: "initiative-shoe-tracker"
covers: []
sources: []
companions: []
after: ["epic-open-source-devops"]
refined: false
risk: low
---

# Architecture Hardening & Offline Resilience

## Description

Hardens system resilience with global RFC 7807 500 error mappers, local vendoring of Leaflet assets for dead-zone vehicle resilience, eager prepared statements initialization in SightingRepository, and DOM CustomEvents pub/sub communication.

## Outcome

Shoe Tracker is hardened against edge-case failures, unifies all server errors under RFC 7807 problem details, operates 100% offline without CDN dependencies, and decouples frontend modules.

## Done when

- Catch-all `ExceptionMapper<Throwable>` guarantees all unhandled 500 exceptions produce RFC 7807 `application/problem+json` with correlation `error_id`.
- Leaflet 1.9.4 CSS, JS, and marker images are vendored locally in `META-INF/resources/vendor/leaflet/`.
- SightingRepository initializes prepared statements eagerly with fail-fast validation.
- Frontend components communicate via DOM CustomEvents (`sighting-added`, `sighting-deleted`, `user-changed`).

## Boundaries

- Database driver changes belong to Epic 6.

## References

- parent - _bmad-output/initiative-shoe-tracker/initiative-shoe-tracker.md
- architecture - _bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md
