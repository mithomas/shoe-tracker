---
type: epic
title: "Spotting UX Polish & German Localization"
parent: "initiative-shoe-tracker"
covers: []
sources: []
companions: []
after: ["epic-views-and-map"]
refined: false
risk: low
---

# Spotting UX Polish & German Localization

## Description

Polishes the passenger road-trip experience on the Autobahn. Eliminates redundant button cooldown locks now that the confirmation modal intercepts taps, adds a light/dark theme mode toggle in the top bar with system auto-detection, introduces German and English localization with authentic Autobahn terminology ("SCHUH!"), and renders nearby existing shoe sightings directly on the live tracking mini-map.

## Outcome

Passengers enjoy a frictionless, high-contrast, localized spotting experience adapted for both daytime glare and nighttime Autobahn driving, with real-time awareness of nearby shoes on the mini-map.

## Done when

- Light and dark mode switcher operates from top bar with automatic OS matching and localStorage persistence.
- Interface supports German and English language switching, rendering "SCHUH!" on the spot button and modal in German mode.
- 3-second button cooldown ring is removed in favor of the immediate confirmation modal.
- Live tracking mini-map renders nearby logged shoes as compact dot markers as the vehicle moves.

## Boundaries

- Backend architecture and multi-database support belong to subsequent infrastructure epics.

## References

- parent - _bmad-output/initiative-shoe-tracker/initiative-shoe-tracker.md
- ux - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/DESIGN.md
- ux - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/EXPERIENCE.md
