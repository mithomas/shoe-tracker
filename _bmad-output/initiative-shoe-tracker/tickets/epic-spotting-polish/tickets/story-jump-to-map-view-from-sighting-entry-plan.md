---
title: 'Jump to Map View from Sighting Entry'
type: 'feature'
ticket: 7
created: '2026-10-29'
status: 'built'
baseline_revision: 'e2ca10611387c048745619c816fe114b6cca4b77'
route: 'oneshot'
route_source: 'auto'
risk: 'low'
review: 'quick'
review_source: 'pinned'
lenses_ran: ['quick']
review_loop_iteration: 1
context: []
---

<frozen-after-approval reason="human-owned intent - do not modify unless human renegotiates">

## Intent

**Problem:** Users browsing the Sightings List (`#viewList`) or viewing the Sighting Confirmation Modal (`#sightingModal`) cannot jump directly to a specific shoe's pin and popup on the full interactive map (`#fullMap`).

**Approach:**
1. Add `focusSighting(sightingOrId)` to `FullMapManager` (`fullmap.js`) that:
   - Switches to the `map` tab via `tabManager.switchTab('map')`,
   - Bootstraps `#fullMap` if not yet initialized and flushes `this.invalidateSize(true)`,
   - Automatically resets the `"My Shoes"` filter (`own`) to `"All Shoes"` (`all`) via `filterManager.setFilter('all')` if the target sighting belongs to another user,
   - Centers and zooms `#fullMap` to `zoom 16` at `[lat, lon]` so unclustered individual pins materialize in `this.markersById`,
   - Calls `this.renderAllMarkers()` and opens `this.markersById[id].openPopup()` (preserving focus across initial async `fetchSightings()`).
2. Render a localized `"View on Map"` (`i18n.t('list.viewOnMap')`) action button (`.btn-card-map`) on each sighting card in `listview.js` wired to `fullMap.focusSighting(sighting)`.
3. Update `#btnSightingViewMap` in `modal.js` to invoke `fullMap.focusSighting(this.currentSighting)`.

</frozen-after-approval>

## Implementation Notes

- Selected `oneshot` route.
- Updated `filter.js` so `setFilter` accepts both boolean and `'all'` / `'own'` arguments.
- Added `focusSighting(sightingOrId)` to `FullMapManager` in `fullmap.js`.
- Added `.btn-card-map` button to each sighting card in `listview.js` and styled it in `style.css`.
- Updated `modal.js` `#btnSightingViewMap` click handler to call `fullMap.focusSighting(this.currentSighting)`.
- Added `testJumpToMapViewFromSightingEntry` in `FrontendAssetTest.kt`.

## Plan Change Log

## Review Triage Log

- Iteration 1 (`quick` lens): 1 low finding (`F1`: clear `this.pendingFocusId = null` after `focusSighting` completes so state is not left set) -> fixed in `fullmap.js`.

## Verification

**Commands:**
- `./gradlew check test` -- expected: `BUILD SUCCESSFUL`, all unit/integration tests, `spotlessCheck`, and `lintJs` pass.
