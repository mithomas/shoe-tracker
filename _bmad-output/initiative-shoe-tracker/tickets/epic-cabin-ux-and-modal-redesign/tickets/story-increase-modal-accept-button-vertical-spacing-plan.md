---
title: 'Increase Vertical Spacing Above and Below Sighting Modal Accept Button'
type: 'feature'
ticket: '8'
created: '2026-10-30'
status: 'built'
baseline_revision: '03e7aac46cc073d8c30e623b5cd33b8d100f77ba'
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

**Problem:** The `210px` circular accept button (`#btnSightingAwesome`), its `226px` SVG progress ring (`.modal-autoclose-ring`), and its outer glow inside `.modal-accept-area` have tight vertical spacing (`margin: 0 0 14px 0`) between `.modal-subtitle` above and `#sightingModalDetails` below.

**Approach:** Update `.modal-accept-area` in `src/main/resources/META-INF/resources/css/style.css` to `margin: 24px 0 28px 0;` and verify in `src/test/kotlin/org/shoetracker/frontend/FrontendAssetTest.kt`.

</frozen-after-approval>

## Implementation Notes

Selected `oneshot` route because the change updates 2 files (`css/style.css` and `FrontendAssetTest.kt`) with under 20 lines modified.
- Updated `.modal-accept-area` in `src/main/resources/META-INF/resources/css/style.css` from `margin: 0 0 14px 0;` to `margin: 24px 0 28px 0;`.
- Updated `testSightingConfirmationModalCircularAcceptButtonLayout()` in `src/test/kotlin/org/shoetracker/frontend/FrontendAssetTest.kt` to assert `margin: 24px 0 28px 0;`.

## Plan Change Log

## Review Triage Log

### Iteration 1 (`quick`)
- 0 findings reported.

## Verification

**Commands:**
- `./gradlew check test spotlessCheck lintJs` -- expected: exit code 0 with all frontend asset and backend tests passing
