---
title: 'Replace Shoe Icon in Sighting Confirmation Modal Accept Button with Smiley'
type: 'feature'
ticket: '7'
created: '2026-10-30'
status: 'built'
baseline_revision: '238cfeb8364d0808ff65ac4cfe7206cc31ea4aef'
route: 'oneshot'
route_source: 'auto'
risk: 'low'
review: 'quick'
review_source: 'pinned'
lenses_ran: ['quick']
review_loop_iteration: 1
context: []
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** The Sighting Confirmation Modal's primary `210px` circular accept button (`#btnSightingAwesome`) currently displays a shoe icon (`👟`) identical to the main `#btnSpotGiant` button, rather than a celebratory smiley icon.

**Approach:** Replace `<span class="icon">👟</span>` inside `#btnSightingAwesome` in `src/main/resources/META-INF/resources/index.html` with `<span class="icon">😊</span>` while preserving `👟` on `#btnSpotGiant` and `.modal-title`, and verify in `src/test/kotlin/org/shoetracker/frontend/FrontendAssetTest.kt`.

</frozen-after-approval>

## Implementation Notes

Selected `oneshot` route because the change updates 2 files (`index.html` and `FrontendAssetTest.kt`) totaling under 20 modified lines.
- Replaced `<span class="icon">👟</span>` inside `#btnSightingAwesome` with `<span class="icon">😊</span>` in `src/main/resources/META-INF/resources/index.html`.
- Updated `testSightingConfirmationModalCircularAcceptButtonLayout()` in `src/test/kotlin/org/shoetracker/frontend/FrontendAssetTest.kt` to assert `#btnSightingAwesome` contains `<span class="icon">😊</span>` while `#btnSpotGiant` keeps `<span class="icon">👟</span>`.

## Plan Change Log

## Review Triage Log

### Iteration 1 (`quick`)
- 0 findings reported.

## Verification

**Commands:**
- `./gradlew check test spotlessCheck lintJs` -- expected: exit code 0 with all frontend asset and backend tests passing
