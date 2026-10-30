---
id: 6
type: story
title: "Remove 'No Shoes Yet' Empty Banner from Map View"
parent: "epic-cabin-ux-and-modal-redesign"
covers: ["R48"]
sources: []
companions: []
after: [1, 2, 3, 4, 5]
refined: true
hitl: false
risk: low
---

# Remove 'No Shoes Yet' Empty Banner from Map View

## Description

Removes the floating `"👟 No shoes spotted yet! Tap the Spot button to be the first."` (`#fullMapEmpty`) and `"👟 No shoes spotted by you yet - tap the Spot button to log your first!"` (`#fullMapEmptyOwn`) overlay banners from the full interactive map view so the map remains clean and unobstructed when zero sightings exist or match the active `"My Shoes"` filter.

## Requirements

1. **Remove Full-Map Empty Banner Markup, Logic, and Localization Keys**
   **Given** the user opens the `[Map]` tab (`#viewMap`)
   **When** zero sightings exist in the database or zero sightings match the active `"My Shoes"` filter
   **Then** no `#fullMapEmpty` or `#fullMapEmptyOwn` overlay banner is rendered over the map canvas
   **And** `#fullMapEmpty`, `#fullMapEmptyOwn`, `map.empty`, and `map.emptyOwn` are removed from `index.html`, `js/fullmap.js`, and `js/i18n.js`

2. **Preserve List View Empty States**
   **Given** the user opens the `[List]` tab (`#viewList`)
   **When** zero sightings exist or zero sightings match the `"My Shoes"` filter
   **Then** `#listEmptyCard` and `#listEmptyOwnCard` continue to render normally in the List view

3. **Automated Frontend Asset Test Alignment**
   **Given** `FrontendAssetTest.kt` verifies frontend markup and scripts
   **When** `./gradlew check test` runs
   **Then** tests assert the absence of `fullMapEmpty` and `fullMapEmptyOwn` while verifying `listEmptyCard` and `listEmptyOwnCard` remain intact

## Boundaries

- Must not change: `#fullMapLoading` loading indicator or `#listEmptyCard` / `#listEmptyOwnCard` in the List view.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-cabin-ux-and-modal-redesign/epic-cabin-ux-and-modal-redesign.md, section Requirements
- design - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/DESIGN.md
