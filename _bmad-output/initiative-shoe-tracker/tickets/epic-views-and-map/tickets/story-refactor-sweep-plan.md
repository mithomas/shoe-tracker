---
title: 'Refactor sweep'
type: 'refactor'
ticket: '5'
created: '2026-10-29'
status: 'built'
baseline_revision: 'fc9727f37471f24e84947422914e993851eea10b'
route: 'oneshot'
route_source: 'auto'
risk: 'low'
review: 'quick'
review_source: 'pinned'
lenses_ran:
  - 'quick'
review_loop_iteration: 0
context:
  - '_bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md'
  - '_bmad-output/initiative-shoe-tracker/ux-shoe-tracker/DESIGN.md'
  - '_bmad-output/initiative-shoe-tracker/ux-shoe-tracker/EXPERIENCE.md'
  - '_bmad-output/initiative-shoe-tracker/tickets/epic-views-and-map/epic-views-and-map.md'
---

<frozen-after-approval reason="human-owned intent - do not modify unless human renegotiates">

## Intent

**Problem:** Leaflet maps (`#spotMiniMap` and `#fullMap`) lack automated window resize and orientation change handling, which can leave grey unrendered tile borders when changing viewport dimensions or rotating mobile devices, and require verification of full lifecycle robustness across all view transitions.

**Approach:** Audit Leaflet container lifecycles in `minimap.js`, `fullmap.js`, and `tabs.js`. Implement debounced window resize and orientationchange handlers in `tabs.js` to trigger two-stage `invalidateSize()` across view transitions, and add automated test coverage verifying resize lifecycle resilience.

</frozen-after-approval>

## Implementation Notes

Oneshot route selected: straightforward lifecycle audit and window resize handling touching under 100 lines across map controllers and tests.

## Review Triage Log

| Lens | Finding | Verdict | Route | Evidence / Rationale |
|---|---|---|---|---|
| quick | `tabs.js:77` Redundant `invalidateActiveMap()` called on tab switch | low | patch | `switchTab` already dispatches `tab-switched`, which handles invalidation; removed duplicate call from `switchTab` while retaining it for window resize. |
| quick | `minimap.js:113` & `fullmap.js:235` Missing immediate `invalidateSize()` per AD-6 | medium | patch | Added `immediate = true` argument to `invalidateSize()` and called it immediately on `tab-switched` alongside settling timeouts. |
| quick | `tabs.js:83` Resizing on List tab does not mark maps dirty | false | rejected | Disproved: Map containers on inactive tabs have `display: none` (0x0 dimensions); calling `invalidateSize` while hidden would corrupt Leaflet dimensions. Maps invalidate immediately upon activation. |
| quick | `FrontendAssetTest.kt:330` Lifecycle test asserted only static substrings | low | patch | Expanded `testMapLifecycleAndResizeResilience()` to check multi-stage timings (`200ms`), `resizeDebounce`, and immediate invalidation flags. |
| quick | `minimap.js:20` Missing cold boot invalidation in `minimap.init()` | medium | patch | Added `this.invalidateSize()` call to end of `minimap.init()` to ensure layout settles after cold boot. |
| quick | `story-refactor-sweep-plan.md` & `story-refactor-sweep.md` Non-ASCII em-dash characters | low | patch | Replaced all non-ASCII em-dash characters (`—`) with standard ASCII hyphens (`-`) per `AGENTS.md`. |

## Verification

**Commands:**
- `./gradlew test` -- expected: All integration and frontend asset tests pass.
- `curl -s http://localhost:8080/` -- expected: HTTP 200 containing all views and scripts.
