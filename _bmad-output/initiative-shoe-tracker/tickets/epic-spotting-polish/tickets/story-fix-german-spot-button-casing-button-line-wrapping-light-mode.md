---
id: 9
type: story
title: "Fix German Spot Button Casing, Button Line-Wrapping, Light Mode Contrast, and Map/Spot Edge Cases"
parent: epic-spotting-polish
covers: [R28, R29, R30, R32, R33, R34]
after: [8]
hitl: false
risk: low
---

# Fix German Spot Button Casing, Button Line-Wrapping, Light Mode Contrast, and Map/Spot Edge Cases

## Description

Polishes the German localization, button layout, Light Mode contrast, and map/spotting edge cases discovered during user testing and the Epic 3 implementation grill review:

1. **German Spot Button Casing (`'Schuh!'`):** Updates `'spot.ready'` in `src/main/resources/META-INF/resources/js/i18n.js` (`de`) from `'SCHUH!'` to `'Schuh!'` and removes `text-transform: uppercase;` on `.btn-spot-giant .label` in `src/main/resources/META-INF/resources/css/style.css` so the German Spot button renders `'Schuh!'` in title case (while `'SHOE SPOTTED!'` in English remains uppercase in its string literal and `'modal.sighting.title'` keeps `'SCHUH!'` in the confirmation modal).
2. **German Button Line-Wrapping:** Prevents line-wrapping on German buttons inside the Sighting Confirmation Modal (`.modal-actions`: `#btnSightingAwesome` `'Super!'`, `#btnSightingViewMap` `'Auf Karte zeigen'`, `#btnSightingUndo` `'Rückgängig'`) and inside the giant Spot button (`.btn-spot-giant .label`: `'Warte auf GPS...'`, `'GPS Verweigert'`) on mobile screens (`<= 480px`) using `white-space: nowrap`, responsive sizing, and clean flex layout.
3. **Light Mode Spot Button Contrast:** Replaces hardcoded `color: #E2E8F0;` on `.btn-spot-giant.waiting-gps .label, .btn-spot-giant.no-gps .label` in `style.css` with `color: var(--ink-primary);` so button text is clearly visible against `--surface-overlay` (`#E2E8F0` / `#F1F5F9`) in Light Mode as well as Dark Mode.
4. **First `"View on Map"` Click Race Condition (`fullmap.js`):** When `focusSighting(id)` is invoked before `#fullMap` is initialized (`this.isInitialized === false`), prevents the asynchronous `bootstrapMap() -> fetchSightings()` completion from overwriting the zoom-16 focused view with `fitBounds(..., { maxZoom: 14 })` and destroying the open popup.
5. **Medium-Span Cluster Click Zoom Advance (`fullmap.js`):** When clicking a cluster at zoom 13-14 whose bounding box causes `fitBounds(clusterBounds, { padding: [40, 40], maxZoom: 15 })` to compute a target zoom `<= currentZoom` on 360px screens, advances zoom directly (`Math.min(currentZoom + 2, 15)` / `15`) centered on `[cluster.lat, cluster.lon]` so clicking a cluster always zooms in.
6. **Non-2xx Spot Error Feedback & Counter Polish (`spot.js`, `listview.js`, `README.md`):** Calls `this.showNetworkAlert()` in `spot.js` when `POST /api/v1/sightings` returns a non-2xx HTTP status (`!res.ok`), uses `data.total` in `listview.js` `updateCounter()`, and removes the stale `"3-second cooldown ring"` mention in `README.md`.

## Requirements

- In `src/main/resources/META-INF/resources/js/i18n.js`, change `de['spot.ready']` from `'SCHUH!'` to `'Schuh!'`.
- In `src/main/resources/META-INF/resources/css/style.css`:
  - Remove `text-transform: uppercase;` from `.btn-spot-giant .label` so `'Schuh!'`, `'Warte auf GPS...'`, and `'GPS Verweigert'` render with natural casing.
  - Prevent line wrapping on `.btn-spot-giant .label` and `.btn-spot-giant.waiting-gps .label, .btn-spot-giant.no-gps .label` (`white-space: nowrap;`) and replace hardcoded `color: #E2E8F0;` with `color: var(--ink-primary);`.
  - Prevent line wrapping on `.modal-actions` buttons (`.btn-primary-action`, `.btn-modal-secondary`, `.btn-modal-tertiary`) with `white-space: nowrap;` and responsive flex sizing so `'Auf Karte zeigen'` and `'Rückgängig'` never break across lines.
- In `src/main/resources/META-INF/resources/js/fullmap.js`:
  - Fix the first-time `focusSighting(id)` race condition when `#fullMap` is uninitialized so `fetchSightings()` does not run initial `fitBounds` or close the focused popup when a sighting has been focused.
  - Ensure cluster click handler always advances zoom when `this.map.getBoundsZoom(clusterBounds, false, L.point(80, 80)) <= currentZoom`.
- In `src/main/resources/META-INF/resources/js/spot.js`, call `this.showNetworkAlert()` in the `else` branch when `!res.ok`.
- In `src/main/resources/META-INF/resources/js/listview.js`, respect `data.total` if higher than `this.allSightings.length` in `updateCounter()` and clean up unused arguments.
- In `README.md`, remove the stale `"3-second cooldown ring"` phrase.
- Update `FrontendAssetTest.kt` to assert `'Schuh!'` for `spot.ready`, Light Mode contrast (`var(--ink-primary)`), and `white-space: nowrap` button rules.
- Verify: German Spot button renders `'Schuh!'`, German modal and Spot button labels do not wrap mid-phrase across lines, waiting-gps/no-gps button text uses `var(--ink-primary)` for high contrast in Light Mode, first `'View on Map'` click preserves zoom 16 and open popup, medium-span cluster click advances zoom, non-2xx POST shows network alert, and `./gradlew check test spotlessCheck lintJs` passes.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-spotting-polish/epic-spotting-polish.md
- ux - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/DESIGN.md
- ux - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/EXPERIENCE.md
