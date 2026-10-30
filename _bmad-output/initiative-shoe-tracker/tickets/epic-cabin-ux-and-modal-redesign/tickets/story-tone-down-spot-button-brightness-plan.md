---
title: 'Tone Down Spot Button Brightness and Glow'
type: 'feature'
ticket: '3'
created: '2026-10-30'
status: 'built'
baseline_revision: 'aa5125ef767f984ef44196b45bc8595a93d4d812'
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

**Problem:** The giant `210px` circular buttons (`#btnSpotGiant` and `#btnSightingAwesome`) use a high-luminance orange gradient (`#F97316 -> #EA580C`) and an intense `32px` outer glow (`rgba(249, 115, 22, 0.45)`) that are overly bright during evening and night Autobahn travel and have < 4.5:1 contrast at the top gradient stop.

**Approach:** Tone down `.btn-spot-giant` in `src/main/resources/META-INF/resources/css/style.css` to a warmer, lower-glare deep orange gradient (`linear-gradient(135deg, #C2410C 0%, #9A3412 100%)`) and a softer outer glow (`0 0 18px rgba(194, 65, 12, 0.25)`) shared identically across Dark and Light themes and across both `#btnSpotGiant` and `#btnSightingAwesome`, achieving >= 5.18:1 WCAG AA contrast with `#FFFFFF` while preserving clear visual separation from `.waiting-gps` and `.no-gps` states.

</frozen-after-approval>

## Implementation Notes

Selected `oneshot` route because the change updates 2 files (`css/style.css` and `FrontendAssetTest.kt`) with under 50 lines modified.
- Added `--spot-btn-gradient: linear-gradient(135deg, #C2410C 0%, #9A3412 100%)` and `--spot-btn-glow: 0 0 18px rgba(194, 65, 12, 0.25)` in `:root` and applied them to `.btn-spot-giant` in `src/main/resources/META-INF/resources/css/style.css`.
- Added `testTonedDownSpotButtonBrightnessAndContrast()` in `src/test/kotlin/org/shoetracker/frontend/FrontendAssetTest.kt` verifying the toned-down gradient, reduced glow, and WCAG AA contrast ratio (`>= 4.5:1`) for both gradient stops against white (`#FFFFFF`).

## Plan Change Log

## Review Triage Log

### Iteration 1 (`quick`)
- 0 findings reported.

## Verification

**Commands:**
- `./gradlew check test spotlessCheck lintJs` -- expected: exit code 0 with all frontend asset and backend tests passing
