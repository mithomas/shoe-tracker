---
title: 'Redesign Sighting Confirmation Modal with Full-Size Circular Accept Button'
type: 'feature'
ticket: '1'
created: '2026-10-30'
status: 'built'
baseline_revision: 'b1b3b2099eda9261c9948bc02d36abaa0631f31b'
route: 'oneshot'
route_source: 'auto'
risk: 'low'
review: 'quick'
review_source: 'pinned'
lenses_ran: ['quick']
review_loop_iteration: 0
context: []
---

<frozen-after-approval reason="human-owned intent - do not modify unless human renegotiates">

## Intent

**Problem:** The Sighting Confirmation Modal (`#sightingModal`) currently renders as a bottom sheet with a small rectangular accept button below the details box, requiring passengers to shift their thumb position and visual focus after tapping the giant `210px` circular Spot button.

**Approach:** Redesign `#sightingModal` in `index.html` and `css/style.css` as a centered floating modal card where the primary accept button (`#btnSightingAwesome`, `"Awesome!"` / `"Super!"`) appears first as a `210px` circular button matching the size, shape, and orange color palette of `#btnSpotGiant`, followed below by the captured sighting details box (`Time`, `Location`, `Spotted By`) and stacked secondary action buttons (`#btnSightingViewMap` and `#btnSightingUndo`), and verify in `FrontendAssetTest.kt`.

</frozen-after-approval>

## Implementation Notes

Selected `oneshot` route because the change is a self-contained UI markup and CSS update across 3 files (`index.html`, `css/style.css`, and `FrontendAssetTest.kt`) totaling under 100 modified lines.
- Updated `#sightingModal` in `src/main/resources/META-INF/resources/index.html` with `.modal-backdrop-centered`, `.modal-card-centered`, a `.modal-accept-area > .btn-spot-container` wrapper around `#btnSightingAwesome.btn-spot-giant.btn-modal-accept-giant` (containing `.icon` and `#btnSightingAwesomeLabel[data-i18n="modal.awesome"]` so `i18n.translateDom()` preserves the shoe icon), `#sightingModalDetails` below it, and `.modal-actions-stacked` for `#btnSightingViewMap` and `#btnSightingUndo`.
- Added `.modal-backdrop.modal-backdrop-centered`, `.modal-sheet.modal-card-centered`, `.modal-accept-area`, and `.modal-actions.modal-actions-stacked` rules in `src/main/resources/META-INF/resources/css/style.css`.
- Added `testSightingConfirmationModalCircularAcceptButtonLayout()` in `src/test/kotlin/org/shoetracker/frontend/FrontendAssetTest.kt`.

## Plan Change Log

## Review Triage Log

- Pass 1 (`quick`): 0 high, 1 medium, 0 low, 0 false, 0 maybe-false.
  - `src/main/resources/META-INF/resources/css/style.css` (`.modal-sheet.modal-card-centered` vertical spacing on `600px`-high mobile viewports when `.modal-subtitle` wraps to 2 lines) - verdict: `medium`, route: `patch`, action: tightened `.modal-card-centered` padding (`16px`), `.modal-subtitle` bottom margin (`12px`), `.modal-accept-area` bottom margin (`14px`), `.modal-detail-box` padding/margin (`10px 12px` / `12px`), and stacked secondary button padding (`10px 14px`) so total height (`~534px`) fits comfortably inside `calc(100vh - 32px)` (`568px` at `600px` viewport height).

## Verification

**Commands:**
- `./gradlew check test spotlessCheck lintJs` -- expected: exit code 0 with all frontend asset and backend tests passing
