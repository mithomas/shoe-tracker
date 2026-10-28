---
type: story
title: "Decouple Frontend Modules via DOM CustomEvents"
parent: ""
covers: []
sources: []
companions: []
after: []
risk: low
refined: false
---

# Decouple Frontend Modules via DOM CustomEvents

## Description

Decouples client-side modules (`spot.js`, `modal.js`, `onboarding.js`, `minimap.js`, `map.js`, `list.js`) using native browser `CustomEvent` broadcasting over `window`. Enables Map and List views to update optimistically when sightings are logged or deleted, and dynamically re-evaluates "My Shoes" filters when the user name is updated.

## Background & Rationale

Currently, frontend modules communicate via direct global method calls (e.g. `spot.js` calling `modal.showSighting()`). As we add the Map and List views in Epic 2, having each component directly reference every other component creates tight coupling and race conditions. An event-driven architecture using native browser `CustomEvent` patterns provides clean, decoupled pub/sub communication.

## Acceptance Criteria

- `spot.js` dispatches `sighting-added` event with payload `{ sighting }` when a sighting is logged.
- `modal.js` dispatches `sighting-deleted` event with payload `{ id }` when an undo action succeeds.
- `onboarding.js` dispatches `user-changed` event with payload `{ userId }` when the user updates their name.
- View components (`map.js`, `list.js`, `minimap.js`) listen for these events to update their state optimistically without tight coupling or full-page API refetches.
- Automated tests verify event emission and registration patterns across frontend assets.
