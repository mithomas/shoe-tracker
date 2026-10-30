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

Streamlines in-car ergonomics, visual comfort, and map clarity across the Spot and Map screens. Redesigns the Sighting Confirmation Modal (`#sightingModal`) as a centered floating modal card where the primary accept button (`#btnSightingAwesome`, `"Awesome!"` / `"Super!"`) appears first as a full-size `210px` circular button matching the size, shape, and orange palette of the main Spot button (`#btnSpotGiant`), followed below by the captured sighting details and stacked secondary action buttons (`View on Map` and `Undo`). Adds a 20-second auto-close timer with an SVG circular progress outline ring around `#btnSightingAwesome` that drains clockwise (`100% -> 0%`). Tones down the orange gradient and outer glow identically across both Dark and Light themes for comfortable night driving, keeps smartphone screens awake while the app is open in the foreground via a dedicated `/js/wakelock.js` Screen Wake Lock module, and removes the `"HUD Radar"` badge from the HUD mini-map and the `"No shoes spotted yet"` empty-state overlay banners from the full interactive map.

## Outcome

Passengers on the Autobahn can confirm or let sightings auto-confirm effortlessly with a 210px circular orange accept button and 20-second clockwise draining countdown ring in a centered modal card, use the app comfortably at night without high-glare button halos or phone screen standby interruptions, and view both the HUD mini-map and full interactive map without obstructive text badges or empty-state banners.

## Requirements

- R43: Sighting Confirmation Modal (`#sightingModal`) renders as a centered floating modal card with its primary accept button (`#btnSightingAwesome`, `"Awesome!"` / `"Super!"`) first as a full-size `210px` circular button matching the size, shape, and orange color palette of the main Spot button (`#btnSpotGiant`) and displaying a smiley icon (`😊`) above the label, followed below by the captured sighting details box (`Time`, `Location`, `Spotted By`) and stacked secondary action buttons (`View on Map` and `Undo`).
- R44: Sighting Confirmation Modal primary accept button (`#btnSightingAwesome`) includes a 20-second automatic dismissal timer displayed as an SVG circular progress outline ring around the `210px` button perimeter that drains clockwise (`100% -> 0%`) over 20 seconds, automatically closing the modal after 20 seconds and immediately cancelling/resetting the timer and animation on manual `Awesome!`, `View on Map`, or `Undo` actions or modal re-open (keeping the modal open if `Undo` fails).
- R45: Giant `210px` circular action buttons (`#btnSpotGiant` / `.btn-spot-giant` and `#btnSightingAwesome`) use a toned-down orange/amber gradient and reduced outer `box-shadow` glow intensity applied identically across both Dark and Light themes for comfortable cabin use while preserving WCAG AA contrast (`>= 4.5:1`) and clear visual distinction from muted disabled states (`Waiting for GPS...` / `No GPS`).
- R46: Dedicated `/js/wakelock.js` module requests an active Screen Wake Lock (`navigator.wakeLock.request('screen')`) on page load, automatically re-acquires the wake lock when `document.visibilityState` returns to `'visible'`, includes a one-time `pointerdown`/`click` fallback if initial load requires a user gesture, and fails silently when unsupported or rejected by OS battery-saver policies.
- R47: HUD mini-map (`#spotMiniMap` in `.mini-map-container`) renders without the floating `"HUD Radar"` / `"HUD-Radar"` badge (`#miniMapBadge`, `.mini-map-badge`, `spot.hudRadar`), leaving the upper-left corner of the mini-map unobstructed while preserving `#miniMapNoGpsOverlay`.
- R48: Full interactive map view (`#viewMap` / `#fullMapContainer`) removes the floating empty-state banners (`#fullMapEmpty` and `#fullMapEmptyOwn` in `index.html`, `fullmap.js`, and `i18n.js`) so the map canvas remains unobstructed when zero sightings exist or match the active `"My Shoes"` filter, while preserving empty-state cards (`#listEmptyCard`, `#listEmptyOwnCard`) in the List view.

## Done when

- `#sightingModal` renders as a centered floating modal card with `#btnSightingAwesome` first as a `210px` circular orange accept button matching `#btnSpotGiant` and displaying a smiley icon (`😊`), followed by `#sightingModalDetails` (`Time`, `Location`, `Spotted By`) and stacked secondary action buttons (`#btnSightingViewMap`, `#btnSightingUndo`).
- Opening `#sightingModal` starts a 20-second auto-close timer with an SVG circular progress outline ring around `#btnSightingAwesome` draining clockwise (`100% -> 0%`) that automatically closes the modal after 20 seconds and cancels cleanly on manual `Awesome!`, `View on Map`, `Undo` (staying open if `Undo` fails), or re-open.
- `.btn-spot-giant` and `#btnSightingAwesome` use a toned-down orange/amber gradient and reduced outer `box-shadow` glow applied identically in both Dark and Light themes while keeping WCAG AA contrast and clear differentiation from `.waiting` / `.no-gps` states.
- `/js/wakelock.js` is loaded in `index.html` and requests `navigator.wakeLock.request('screen')` on page load, on `visibilitychange` (`document.visibilityState === 'visible'`), and on a one-time `pointerdown`/`click` fallback with silent error handling on unsupported browsers or rejected requests.
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
- Decision: 2026-10-30: Story `8.1` renders `#sightingModal` as a centered floating modal card with the `210px` circular `#btnSightingAwesome` button first (matching the Spot button's orange palette so both giant circles look identical), sighting details below it, and stacked secondary action buttons (`View on Map` and `Undo`) at the bottom.
- Decision: 2026-10-30: Story `8.2` renders an SVG ring around the `210px` `#btnSightingAwesome` perimeter that drains clockwise (`100% -> 0%`) over 20s, cancelling immediately on any modal button tap and keeping the modal open if `Undo` fails.
- Decision: 2026-10-30: Story `8.3` tones down the orange gradient and outer glow identically across both Dark and Light themes for the giant `210px` circular buttons.
- Decision: 2026-10-30: Story `8.4` implements Screen Wake Lock in a dedicated `/js/wakelock.js` module with initial load request, `visibilitychange` (`'visible'`) re-acquisition, and a one-time `pointerdown`/`click` user-gesture fallback.
