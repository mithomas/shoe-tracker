---
title: 'Add 20-Second Auto-Close Timer with Progress Outline to Sighting Confirmation Modal Accept Button'
type: 'feature'
ticket: '2'
created: '2026-10-30'
status: 'built'
baseline_revision: 'bd8c93f0b6d1601b950113d9207b809702883677'
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

**Problem:** After logging a shoe sighting, the Sighting Confirmation Modal (`#sightingModal`) remains open indefinitely until manually dismissed, requiring an extra tap during driving or passenger spotting even when no Undo or Map action is needed.

**Approach:** Add a 20-second (`20000ms`) auto-close countdown timer to `SightingModalManager` in `js/modal.js` paired with an SVG `<circle>` progress ring (`#modalAutoCloseRingProgress`) around the `210px` circular `#btnSightingAwesome` perimeter in `index.html` and `css/style.css` that drains clockwise (`100% -> 0%`) over 20 seconds. Cancel and reset the timer and SVG animation immediately on tapping `Awesome!`, `View on Map`, `Undo`, or re-opening the modal, and keep the modal open with the timer cancelled if `Undo` fails due to a network error.

</frozen-after-approval>

## Implementation Notes

Selected `oneshot` route because the change touches 4 files (`index.html`, `css/style.css`, `js/modal.js`, `FrontendAssetTest.kt`) with under 100 lines of focused frontend and test additions.
- Added `<svg class="modal-autoclose-ring" viewBox="0 0 226 226" aria-hidden="true">` with `.modal-autoclose-ring-bg` and `#modalAutoCloseRingProgress` around `#btnSightingAwesome`, plus `#sightingModalUndoError` in `src/main/resources/META-INF/resources/index.html`.
- Added `.modal-autoclose-ring`, `.modal-autoclose-ring-bg`, and `.modal-autoclose-ring-progress` styles in `src/main/resources/META-INF/resources/css/style.css`.
- Updated `SightingModalManager` in `src/main/resources/META-INF/resources/js/modal.js` with `startAutoCloseTimer()`, `clearAutoCloseTimer()`, 20-second (`20000ms`) clockwise SVG stroke-dashoffset animation (`0 -> -circumference`), and `handleUndo()` error handling that cancels the timer and keeps the modal open on network failure.
- Added `testSightingConfirmationModalAutoCloseTimerAndOutline()` in `src/test/kotlin/org/shoetracker/frontend/FrontendAssetTest.kt`.

## Plan Change Log

## Review Triage Log

### Iteration 1 (`quick`)
- 0 findings reported.

## Verification

**Commands:**
- `./gradlew check test spotlessCheck lintJs` -- expected: exit code 0 with all frontend asset and backend tests passing
