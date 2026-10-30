---
type: epic
title: "Cabin Ergonomics, Map Decluttering & Sighting Modal Redesign"
parent: "initiative-shoe-tracker"
covers: ["R43", "R44", "R45", "R46", "R47", "R48"]
sources: ["_bmad-output/initiative-shoe-tracker/initiative-shoe-tracker.md"]
companions: ["_bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md", "_bmad-output/initiative-shoe-tracker/ux-shoe-tracker/ux-shoe-tracker.md"]
after: []
refined: true
risk: low
---

# Cabin Ergonomics, Map Decluttering & Sighting Modal Redesign

## Description

Streamlines in-car ergonomics, visual comfort, and map clarity across the Spot and Map screens. Redesigns the Sighting Confirmation Modal (`#sightingModal`) so the primary accept button (`#btnSightingAwesome`, `"Awesome!"` / `"Super!"`) is a full-size `210px` circular button matching the form factor and vertical position of the main Spot button (`#btnSpotGiant`), with the captured sighting details and secondary buttons (`View on Map` and `Undo`) stacked below it. Adds a 20-second auto-close timer with an animated progress outline ring around the circular accept button. Tones down the Spot button's ready-state brightness and outer glow for comfortable night driving, keeps smartphone screens awake while the app is open in the foreground via the Screen Wake Lock API, and removes the `"HUD Radar"` badge from the HUD mini-map and the `"No shoes spotted yet"` empty-state overlay banners from the full interactive map.

## Outcome

Passengers on the Autobahn can confirm or let sightings auto-confirm effortlessly with a 210px circular accept button and 20-second visual countdown ring, use the app comfortably at night without high-glare button halos or phone screen standby interruptions, and view both the HUD mini-map and full interactive map without obstructive text badges or empty-state banners.

## Requirements

- R43: Sighting Confirmation Modal (`#sightingModal`) renders its primary accept button (`#btnSightingAwesome`, `"Awesome!"` / `"Super!"`) as a full-size `210px` circular button matching the form factor and screen alignment of the main Spot button (`#btnSpotGiant`), with the captured sighting details box (`Time`, `Location`, `Spotted By`) and secondary action buttons (`View on Map` and `Undo`) stacked cleanly below it within the mobile viewport.
- R44: Sighting Confirmation Modal primary accept button (`#btnSightingAwesome`) includes a 20-second automatic dismissal timer displayed as an animated progress outline/ring around the circular accept button perimeter, automatically closing the modal after 20 seconds and immediately cancelling/resetting the timer and animation on manual `Awesome!`, `View on Map`, or `Undo` actions or modal re-open.
- R45: Giant `210px` Spot button (`#btnSpotGiant` / `.btn-spot-giant`) uses a toned-down orange/amber gradient and reduced outer `box-shadow` glow intensity in its ready state for comfortable evening and night cabin use while preserving WCAG AA contrast (`>= 4.5:1`) and clear visual distinction from muted disabled states (`Waiting for GPS...` / `No GPS`).
- R46: Application requests an active Screen Wake Lock (`navigator.wakeLock.request('screen')`) when open in the foreground and automatically re-acquires the wake lock when `document.visibilityState` returns to `'visible'`, failing silently when unsupported or rejected by OS battery-saver policies.
- R47: HUD mini-map (`#spotMiniMap` in `.mini-map-container`) renders without the floating `"HUD Radar"` / `"HUD-Radar"` badge (`#miniMapBadge`, `.mini-map-badge`, `spot.hudRadar`), leaving the upper-left corner of the mini-map unobstructed while preserving `#miniMapNoGpsOverlay`.
- R48: Full interactive map view (`#viewMap` / `#fullMapContainer`) removes the floating empty-state banners (`#fullMapEmpty` and `#fullMapEmptyOwn` in `index.html`, `fullmap.js`, and `i18n.js`) so the map canvas remains unobstructed when zero sightings exist or match the active `"My Shoes"` filter, while preserving empty-state cards (`#listEmptyCard`, `#listEmptyOwnCard`) in the List view.

## Done when

- `#sightingModal` renders `#btnSightingAwesome` as a `210px` circular accept button above `#sightingModalDetails` (`Time`, `Location`, `Spotted By`) and secondary action buttons (`#btnSightingViewMap`, `#btnSightingUndo`), fitting cleanly within mobile viewports.
- Opening `#sightingModal` starts a 20-second auto-close timer with an animated outline/progress ring around `#btnSightingAwesome` that automatically closes the modal after 20 seconds and cancels cleanly on manual `Awesome!`, `View on Map`, `Undo`, or re-open.
- `.btn-spot-giant` ready state uses toned-down orange/amber gradient and reduced outer `box-shadow` glow while keeping WCAG AA contrast and clear differentiation from `.waiting` / `.no-gps` states.
- Foreground page load and `visibilitychange` (`document.visibilityState === 'visible'`) request `navigator.wakeLock.request('screen')` with silent error handling on unsupported browsers or rejected requests.
- `#miniMapBadge`, `.mini-map-badge`, and `spot.hudRadar` are removed from `index.html`, `style.css`, and `i18n.js`.
- `#fullMapEmpty`, `#fullMapEmptyOwn`, `map.empty`, and `map.emptyOwn` are removed from `index.html`, `fullmap.js`, and `i18n.js` while `#listEmptyCard` and `#listEmptyOwnCard` remain intact.
- `FrontendAssetTest.kt` verifies all updated markup, styles, scripts, and removed overlays, and `./gradlew check test spotlessCheck lintJs` passes cleanly.

## Boundaries

- Must not change: Sighting creation (`POST /api/v1/sightings`), `Undo` deletion (`DELETE /api/v1/sightings/{id}`), or `View on Map` tab switching and marker focus behavior.
- Must not change: `#miniMapNoGpsOverlay` (`⚠️ No GPS` / `⚠️ Kein GPS`) on the HUD mini-map or `#listEmptyCard` / `#listEmptyOwnCard` in the List view.

## References

- parent - _bmad-output/initiative-shoe-tracker/initiative-shoe-tracker.md
- architecture - _bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md
- ux - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/ux-shoe-tracker.md

## Notes

- Decision: 2026-10-30: Stories `8.1 -> 8.2 -> 8.3 -> 8.4 -> 8.5 -> 8.6` execute sequentially as one ordered lane because they share edits to `index.html`, `css/style.css`, `js/i18n.js`, and `FrontendAssetTest.kt`.
- Decision: 2026-10-30: Story `8.1` is the tracer bullet establishing the redesigned circular accept button layout in `#sightingModal` before Story `8.2` adds the 20-second progress outline timer to that button.
