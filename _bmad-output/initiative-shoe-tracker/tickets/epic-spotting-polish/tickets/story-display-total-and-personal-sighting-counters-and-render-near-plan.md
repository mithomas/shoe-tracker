---
title: 'Display Total and Personal Sighting Counters and Render Nearby Shoes on Mini-Map'
type: 'feature'
ticket: 5
created: '2026-10-29'
status: 'built'
baseline_revision: '609ea04b72b8ee9e7902199b9a4830010cf39e2e'
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

**Problem:** The Spot screen still shows a raw developer `<pre>` JSON box that requires clicking `Refresh` to populate, `#listCountBadge` does not show the combined `Total | Mine` count, and the HUD mini-map (`#spotMiniMap`) only shows the user's GPS dot without nearby spotted shoes.

**Approach:**
1. Fetch `/api/v1/sightings?limit=500` on startup (`listView.init()`) and dispatch `sightings-loaded` so sighting counters and `#spotMiniMap` dots are warm on boot.
2. Replace the legacy raw JSON `<pre>` debug card (`#output` / `#btnRefresh`) on `#viewSpot` with a reactive Sighting Counter Card (`#spotCounterCard`, `#spotCounterSummary`, `#spotTotalCount`, `#spotMineCount`) and update `#listCountBadge` to show `{total} Total | {mine} Mine` (`{total} Gesamt | {mine} Meine` in German) on `sightings-loaded`, `sighting-added`, `sighting-deleted`, `user-changed`, and `lang-changed`.
3. Update `minimap.js` (`#spotMiniMap`) with a `sightingsLayer` that renders compact dot markers (`.mini-shoe-dot-own` in Signal Blue `#3B82F6`, `.mini-shoe-dot-other` in Slate `#94A3B8`) alongside the live GPS dot.

</frozen-after-approval>

## Implementation Notes

- Selected `oneshot` route.
- Replaced `#output` / `#btnRefresh` raw JSON `<pre>` card in `index.html` with `#spotCounterCard` (`#spotCounterSummary`, `#spotTotalCount`, `#spotMineCount`).
- Updated `listview.js` to fetch `/api/v1/sightings?limit=500` on startup, dispatch `sightings-loaded`, and update both `#spotCounterCard` and `#listCountBadge` (`{total} Total | {mine} Mine` / `{total} Gesamt | {mine} Meine`).
- Updated `minimap.js` and `style.css` to render compact Signal Blue (`#3B82F6`) and Slate (`#94A3B8`) dot markers (`mini-shoe-dot-own`, `mini-shoe-dot-other`) on `#spotMiniMap`.
- Cleaned up legacy `#output` references in `spot.js` and `modal.js`.
- Added verification assertions in `FrontendAssetTest.kt`.

## Plan Change Log

## Review Triage Log

- Iteration 1 (`quick` lens): 0 findings.

## Verification

**Commands:**
- `./gradlew check test` -- expected: `BUILD SUCCESSFUL`, all unit/integration tests, `spotlessCheck`, and `lintJs` pass.
