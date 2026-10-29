---
title: 'Fix German Spot Button Casing, Button Line-Wrapping, Light Mode Contrast, and Map/Spot Edge Cases'
type: 'bugfix'
ticket: 9
created: '2026-10-29'
status: 'built'
baseline_revision: '55683a7'
route: 'oneshot'
route_source: 'auto'
risk: 'low'
review: 'quick'
review_source: 'auto'
lenses_ran: ['quick']
review_loop_iteration: 1
context: ["/home/tom/development/personal/shoe-tracker/bmad/_bmad-output/initiative-shoe-tracker/ux-shoe-tracker/DESIGN.md", "/home/tom/development/personal/shoe-tracker/bmad/_bmad-output/initiative-shoe-tracker/ux-shoe-tracker/EXPERIENCE.md"]
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** User testing and the Epic 3 implementation grill review identified six UI/UX issues: (1) the German Spot button renders `'SCHUH!'` in all-caps instead of `'Schuh!'` due to `de['spot.ready'] = 'SCHUH!'` and `text-transform: uppercase;` on `.btn-spot-giant .label`, (2) German button labels in the confirmation modal (`'Auf Karte zeigen'`, `'Rückgängig'`) and Spot button (`'Warte auf GPS...'`, `'GPS Verweigert'`) wrap across lines on mobile widths, (3) `.btn-spot-giant.waiting-gps .label, .btn-spot-giant.no-gps .label` hardcodes `color: #E2E8F0;`, making the button text nearly invisible against `--surface-overlay` in Light Mode, (4) clicking `'View on Map'` before `#fullMap` is initialized triggers an async `bootstrapMap() -> fetchSightings()` completion that runs `fitBounds(..., { maxZoom: 14 })` and recreates markers, closing the focused popup and zooming out from 16, (5) clicking a medium-span cluster at zoom 13-14 on 360px screens can compute a `fitBounds` target zoom `<= currentZoom` so the map does not advance zoom, and (6) `spot.js` does not show the network alert on non-2xx HTTP responses, `listview.js` ignores `data.total` in `updateCounter()`, and `README.md` retains a stale `"3-second cooldown ring"` reference.

**Approach:** Update `de['spot.ready']` to `'Schuh!'` in `i18n.js` and remove `text-transform: uppercase;` on `.btn-spot-giant .label` in `style.css`; add `white-space: nowrap;` and responsive sizing to `.btn-spot-giant .label` and `.modal-actions` buttons; replace hardcoded `color: #E2E8F0;` with `color: var(--ink-primary);` on `.btn-spot-giant.waiting-gps .label, .btn-spot-giant.no-gps .label`; track `this.isFetching` in `fullmap.js` so `focusSighting` preserves `this.pendingFocusId` while `fetchSightings()` is in-flight and `createClusterMarker` advances zoom whenever `getBoundsZoom(...) <= currentZoom`; add `else { this.showNetworkAlert(); }` in `spot.js`; respect `data.total` in `listview.js`; remove the stale phrase in `README.md`; and update `FrontendAssetTest.kt`.

</frozen-after-approval>

## Implementation Notes

- Oneshot route selected because all changes are self-contained UI/JS/CSS bugfixes.

## Plan Change Log

## Review Triage Log

## Verification

**Commands:**
- `./gradlew check test spotlessCheck lintJs` -- expected: `BUILD SUCCESSFUL` with all unit/integration tests and linters passing.
