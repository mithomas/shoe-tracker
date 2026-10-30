---
title: 'Fix Distorted Elliptical Center Button in Sighting Modal on iOS Safari and Brave'
type: 'bugfix'
ticket: '10'
created: '2026-10-30'
status: 'built'
baseline_revision: 'cfa390b175e8cf86a9d82a973957f8266df8e729'
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

**Problem:** On iPhone Safari and Brave (iOS WebKit), opening `#sightingModal` renders `#btnSightingAwesome` as a narrow vertical ellipse (`height: 210px`, width narrower than `210px`) because `.btn-spot-giant` inside the `display: flex` `.btn-spot-container` can shrink horizontally along the row flex axis.

**Approach:** Enforce `min-width: 210px; max-width: 210px; min-height: 210px; max-height: 210px; flex: 0 0 210px; aspect-ratio: 1 / 1; padding: 0; box-sizing: border-box; -webkit-appearance: none; appearance: none;` on `.btn-spot-giant` and `min-width: 210px; min-height: 210px; flex: 0 0 210px; aspect-ratio: 1 / 1;` on `.btn-spot-container`, add explicit `top: -8px; left: -8px;` to `.modal-autoclose-ring` in `css/style.css`, add `width="226" height="226"` attributes to `<svg class="modal-autoclose-ring">` in `index.html`, and assert the rules in `FrontendAssetTest.kt`.

</frozen-after-approval>

## Implementation Notes

Selected `oneshot` route because the fix touches `css/style.css`, `index.html`, `js/modal.js`, `application.properties`, and `FrontendAssetTest.kt` with localized styling, cache-busting, and timer updates.
- Updated `.btn-spot-container` (`display: block`), `.btn-spot-giant`, and `.modal-autoclose-ring` in `src/main/resources/META-INF/resources/css/style.css`.
- Added inline `style` and SVG presentation attributes (`stroke="#F97316"`, `stroke-width="6"`, `position:absolute`) on `<svg class="modal-autoclose-ring">` and `<button id="btnSightingAwesome">`, plus `?v=8.10` cache-busting query strings on `/css/style.css` and `/js/*.js` in `src/main/resources/META-INF/resources/index.html`.
- Updated `startAutoCloseTimer()` and `clearAutoCloseTimer()` in `src/main/resources/META-INF/resources/js/modal.js` to animate `stroke-dashoffset` via `requestAnimationFrame` so iOS WebKit renders the 20s countdown smoothly when `#sightingModal` becomes visible.
- Configured `Cache-Control: no-cache, must-revalidate` in `src/main/resources/application.properties`.
- Added regression assertions in `src/test/kotlin/org/shoetracker/frontend/FrontendAssetTest.kt`.

## Plan Change Log

## Review Triage Log

### Iteration 1 (`quick`)
- 0 findings reported.

## Verification

**Commands:**
- `./gradlew check test spotlessCheck lintJs` -- expected: exit code 0 with all frontend asset and backend tests passing
