---
title: 'High-velocity spot button with cooldown and confirmation modal'
type: 'feature'
ticket: '3'
created: '2026-10-28'
status: 'built'
baseline_revision: '4fa9be5389933bd05ba29eea9fdc2b4047e68040'
route: 'full'
route_source: 'auto'
risk: 'low'
review: 'quick'
review_source: 'pinned'
lenses_ran: ['quick']
review_loop_iteration: 0
context:
  - '_bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md'
  - '_bmad-output/initiative-shoe-tracker/ux-shoe-tracker/DESIGN.md'
  - '_bmad-output/initiative-shoe-tracker/ux-shoe-tracker/EXPERIENCE.md'
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** Highway passengers traveling at 150 km/h need an oversized, unmistakable touch target that logs a shoe sighting with a single tap, while preventing accidental double-taps and providing instant visual confirmation with captured coordinates and timestamp.

**Approach:** Build the 210px circular high-velocity Spot Button with safety orange styling, an ambient glow when GPS is hot, a mandatory 3-second animated cooldown countdown ring that blocks rapid repeated submissions, and an instant post-tap confirmation modal displaying the captured navigational coordinates and timestamp.

## Boundaries & Constraints

**Always:**
- Button dimensions: 210px diameter pure circle (`rounded: full`), centered on mobile screen.
- Button colors: Safety orange `#F97316` (ready), `#EA580C` (pressed), slate `#475569` (cooldown lock).
- Read hot coordinates from `geoWatcher.getHotCoordinates()` without satellite acquisition delay.
- Enforce mandatory 3-second cooldown locking after tap; reject any further taps during cooldown.
- Pop up Sighting Confirmation Modal immediately on tap, displaying timestamp in local time and coordinates in standard format (`N XX° MM,MMM' / E XXX° MM,MMM'`).
- Actions on modal: `"Awesome!"` (dismisses modal) and `"View on Map"`.
- Trigger haptic feedback via `navigator.vibrate(50)` if supported by the browser.

**Never:**
- Never submit fake or default coordinates if GPS fix is unavailable.
- Never allow a tap to bypass or shorten the 3-second cooldown lock.
- Never show blocking modal prompts *before* the tap (onboarding modal is the only pre-spotting gate).

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|---|---|---|---|
| Single Tap (GPS Ready) | User taps 210px button | Reads hot coordinates, dispatches `POST /api/v1/sightings`, locks for 3s cooldown, pops up Confirmation Modal | Modal displays captured coords and local timestamp |
| Rapid Double-Tap / Mashing | User taps button 3 times in 1 second | First tap executes; subsequent 2 taps are strictly ignored by cooldown lock | No duplicate requests sent to backend |
| Tap with GPS Acquiring | User taps while GPS searching | Tap is blocked; banner displays `"Waiting for GPS lock before recording..."` | No HTTP request sent |
| Tap with GPS Denied | User taps while GPS denied | Tap is blocked; warning banner highlights required permissions | No HTTP request sent |
| Confirmation Modal Dismiss | User taps `"Awesome!"` on modal | Modal animates out; Spot screen remains active and ready | Returns to main view |
| Cooldown Release | 3 seconds elapse after tap | Button transitions from cooldown slate back to ready orange with ambient glow | Button is interactive again |

</frozen-after-approval>

## Code Map

- `src/main/resources/META-INF/resources/js/spot.js` -- 210px spot button controller, 3-second cooldown state machine, and haptics
- `src/main/resources/META-INF/resources/js/modal.js` -- Post-tap sighting confirmation modal controller
- `src/main/resources/META-INF/resources/css/style.css` -- Giant button styling, cooldown circular SVG countdown ring, and modal overlay animations
- `src/main/resources/META-INF/resources/index.html` -- Updated Spot section with 210px giant button and sighting modal markup
- `src/test/kotlin/org/shoetracker/frontend/FrontendAssetTest.kt` -- Automated tests verifying spot button and modal assets

## Tasks & Acceptance

**Execution:**
- [x] `src/main/resources/META-INF/resources/js/spot.js` -- Implement SpotButtonManager with tap handler, hot coordinate submission, 3-second cooldown countdown, and haptics.
- [x] `src/main/resources/META-INF/resources/js/modal.js` -- Implement SightingModalManager displaying captured timestamp and formatted coordinates with dismiss actions.
- [x] `src/main/resources/META-INF/resources/css/style.css` -- Add styles for 210px circular button, ambient glow, active depress, cooldown countdown ring, and confirmation modal.
- [x] `src/main/resources/META-INF/resources/index.html` -- Replace minimal test button with 210px giant spot button and sighting confirmation modal sheet.
- [x] `src/test/kotlin/org/shoetracker/frontend/FrontendAssetTest.kt` -- Add test cases verifying spot button assets, cooldown logic, and confirmation modal elements.

**Acceptance Criteria:**
- Given GPS locked, when tapping the 210px spot button, then `POST /api/v1/sightings` is called with hot coordinates and status 201 is returned.
- Given a button tap, when triggered, then the button immediately enters a 3-second cooldown displaying countdown text (`Logged! 3s`, `2s`, `1s`) and blocking subsequent taps.
- Given a successful tap, when executed, then the Sighting Confirmation Modal displays the formatted coordinates (`N XX° MM,MMM' / E XXX° MM,MMM'`) and local timestamp.
- Given `./gradlew test`, then all backend integration and frontend asset tests pass cleanly.

## Implementation Notes

- Implemented `spot.js` creating `SpotButtonManager` which controls the 210px circular button, reads hot coordinates from `geoWatcher`, triggers haptics (`navigator.vibrate(50)`), dispatches `POST /api/v1/sightings`, and locks out subsequent taps for 3 seconds with an animated SVG countdown ring and text countdown.
- Implemented `modal.js` creating `SightingModalManager` which opens `#sightingModal` displaying captured local time and formatted coordinates (`N XX° MM,MMM' / E XXX° MM,MMM'`), with `"Awesome!"` and `"View on Map"` dismiss actions.
- Added comprehensive styles to `style.css` for `.btn-spot-giant` (210px circle, safety orange gradient, ambient glow, scale down on tap), SVG `.cooldown-ring`, and `.modal-detail-box`.
- Updated `index.html` with `.spot-area` container, SVG ring, giant spot button, and `#sightingModal`.
- Updated `FrontendAssetTest.kt` verifying all new assets and markup elements. All 10 tests pass cleanly in 1m 6s.

## Plan Change Log

## Review Triage Log

- Location: `index.html` & `modal.js` | Verdict: medium | Evidence: Confirmation modal lacked the required "Undo" tertiary action. Patched by adding `DELETE /api/v1/sightings/{id}` to backend, `#btnSightingUndo` to modal, and full deletion flow. | Route: patch
- Location: `spot.js` & `index.html` | Verdict: medium | Evidence: Spot button did not reflect GPS search or denied states. Patched with `setGpsStatus()` updating button label and disabling button when GPS is not locked. | Route: patch
- Location: `index.html` | Verdict: low | Evidence: Missing `aria-live="polite"` on button countdown label. Patched by adding attribute to `#btnSpotLabel`. | Route: patch
- Location: `spot.js` & `index.html` | Verdict: low | Evidence: Network errors on tap wrote only to debug output instead of top alert banner. Patched with `#networkAlertBanner` appearing for 4 seconds on network failure. | Route: patch

## Design Notes

- **Cooldown Animation:** Use SVG circular stroke-dashoffset or CSS transition to animate the 3-second countdown ring smoothly around the button perimeter.

## Verification

**Commands:**
- `./gradlew test` -- expected: All integration and frontend asset tests pass.
- `curl -s http://localhost:8080/js/spot.js` -- expected: HTTP 200 with spot controller code.
- `curl -s http://localhost:8080/js/modal.js` -- expected: HTTP 200 with modal controller code.
- `curl -s http://localhost:8080/` -- expected: HTTP 200 containing `.btn-spot-giant` and `.modal-sighting`.
