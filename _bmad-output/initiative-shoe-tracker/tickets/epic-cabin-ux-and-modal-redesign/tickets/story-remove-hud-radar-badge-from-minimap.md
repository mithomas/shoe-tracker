---
id: 5
type: story
title: "Remove 'HUD Radar' Badge from HUD Mini-Map"
parent: "epic-cabin-ux-and-modal-redesign"
covers: ["R47"]
sources: []
companions: []
after: [1, 2, 3, 4, 7]
refined: true
hitl: false
risk: low
---

# Remove 'HUD Radar' Badge from HUD Mini-Map

## Description

Removes the floating `"HUD Radar"` / `"HUD-Radar"` text badge from the top-left corner of the Spot screen's HUD mini-map so the map viewport is uncluttered and has room for in-map controls.

## Requirements

1. **Remove HUD Radar Badge Element and Styling**
   **Given** the Spot screen renders the HUD mini-map container
   **When** the user views the Spot screen in English or German
   **Then** no `"HUD Radar"` or `"HUD-Radar"` badge overlay is rendered inside `.mini-map-container`
   **And** unused badge markup, CSS rules, and localization keys for the badge are removed cleanly

2. **Automated Frontend Asset Test Alignment**
   **Given** the frontend asset test suite checks `index.html` and localization strings
   **When** `./gradlew check test` runs
   **Then** tests verify that the HUD mini-map renders without the `"HUD Radar"` badge

## Boundaries

- Must not change: The `#miniMapNoGpsOverlay` (`⚠️ No GPS` / `⚠️ Kein GPS`) overlay when GPS is unavailable, or the mini-map's live GPS dot and nearby sighting dots.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-cabin-ux-and-modal-redesign/epic-cabin-ux-and-modal-redesign.md, section Requirements
- design - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/DESIGN.md
