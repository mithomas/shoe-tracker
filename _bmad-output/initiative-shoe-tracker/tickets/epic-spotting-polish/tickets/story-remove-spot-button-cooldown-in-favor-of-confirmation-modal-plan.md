---
title: 'Remove Spot Button Cooldown in Favor of Confirmation Modal'
type: 'feature'
ticket: 2
created: '2026-10-29'
status: 'built'
baseline_revision: 'a8177cb99246ff3f2b8d80f398bebd4e427b8ecc'
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

**Problem:** The 3-second countdown timer and SVG cooldown ring (`cooldown-ring`, `cooldownRingProgress`, `startCooldown`, `releaseCooldown`) are redundant now that the Sighting Confirmation Modal (`#sightingModal`) intercepts taps after a sighting, unnecessarily delaying back-to-back spotting after dismissing the modal.

**Approach:** Remove the `<svg class="cooldown-ring">` markup from `index.html`, `.cooldown-ring` and `.btn-spot-giant.cooldown` styles from `style.css`, and the 3-second timer methods (`startCooldown`, `releaseCooldown`, `isCooldown`, `cooldownSeconds`) from `spot.js`. Replace the timer lock with a lightweight `isSubmitting` in-flight guard during `POST /api/v1/sightings` so `#btnSpotGiant` is immediately ready upon response and modal dismissal.

</frozen-after-approval>

## Implementation Notes

- Selected `oneshot` route (< 100 lines modified across `index.html`, `style.css`, `spot.js`, and `FrontendAssetTest.kt`).
- Removed `<svg class="cooldown-ring">` and `#cooldownRingProgress` from `index.html` and `.cooldown-ring` / `.btn-spot-giant.cooldown` from `style.css`.
- Replaced `isCooldown`, `cooldownSeconds`, `startCooldown()`, and `releaseCooldown()` in `spot.js` with `isSubmitting` in-flight request lock released in `finally`.
- Updated `FrontendAssetTest.kt` to verify removal of `cooldown-ring` / `startCooldown` and presence of `isSubmitting`.

## Plan Change Log

## Review Triage Log

- `quick` review subagent (`d5e7ca08-12fd-4f63-addf-612549c44a86`): 0 findings (`[]`).

## Verification

**Commands:**
- `./gradlew check test` -- expected: `BUILD SUCCESSFUL`, all unit/integration tests, `spotlessCheck`, and `lintJs` pass.
