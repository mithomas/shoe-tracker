---
title: 'Mobile geolocation watcher and user onboarding'
type: 'feature'
ticket: '2'
created: '2026-10-28'
status: 'built'
baseline_revision: 'e75278ed1dc5bdb9fea56f465625cdc7760f626e'
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

**Problem:** Users opening Shoe Tracker need to be asked their name on first visit, and the app must eliminate GPS satellite acquisition latency at highway speeds by maintaining hot coordinates in the background before the spot button is pressed, while providing constant visual reassurance by displaying current coordinates updated every 5 seconds.

**Approach:** Implement a client-side geolocation manager using `navigator.geolocation.watchPosition` with high accuracy, a visual status badge, and a live coordinate telemetry display updating every 5 seconds, alongside an onboarding modal bottom sheet that prompts for the user's name on first launch and stores it in `localStorage.user_id`. Format coordinates in standard navigational notation (`N XX° MM,MMM' / E XXX° MM,MMM'`).

## Boundaries & Constraints

**Always:**
- Use browser `navigator.geolocation.watchPosition` with `enableHighAccuracy: true`, `maximumAge: 5000`, and `timeout: 10000`.
- Display live GPS coordinates on the screen in standard navigational notation (`N XX° MM,MMM' / E XXX° MM,MMM'`), updating every 5 seconds.
- Maintain live GPS state machine: `Acquiring` (amber pulse), `Ready` (emerald), `Denied` (red).
- Prompt for user name on first visit if `localStorage.getItem("user_id")` is absent; require 1..64 non-blank characters.
- Store user name directly into `localStorage.setItem("user_id", name)` per the MVP identity decision.
- Display coordinates in standard navigational notation: `N XX° MM,MMM' / E XXX° MM,MMM'`.
- Match styling tokens from `DESIGN.md` (`surface-base: #0F172A`, `surface-raised: #1E293B`, `action-primary: #F97316`).

**Never:**
- Do not make server round-trips for user registration or profiles in this story (deferred to backlog).
- Do not build the full interactive map view or full list view in this story (reserved for Epic 2).
- Do not allow empty or whitespace-only user names to pass onboarding.

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|---|---|---|---|
| First-time visit | `localStorage.getItem("user_id") === null` | Onboarding modal sheet appears over dimmed backdrop; main interface locked until name entered | Cannot dismiss without submitting valid name |
| Returning visit | `localStorage.getItem("user_id") === "Tom"` | Onboarding modal stays hidden; header displays user chip `👤 Tom` | No prompt displayed |
| Name validation: whitespace | User enters `"   "` and clicks "Start Spotting" | Form rejects input, highlights border red, shows `"Please enter your name."` | Input field receives focus |
| Name validation: valid | User enters `"Tom"` | Saves `"Tom"` to `localStorage`, dismisses modal, updates header chip | Modal animates out |
| GPS Permission Granted | Geolocation success callback with `lat: 50.1109, lon: 8.6821` | Header badge shows solid green `GPS Ready`; displays `N 50° 06,654' / E 008° 40,928'` | Cached in memory for instant tap access |
| 5-Second Coordinate Refresh | Geolocation locked and moving | Coordinate display refreshes every 5 seconds with latest fix | Updates seamlessly |
| GPS Searching | Geolocation waiting for satellite lock | Header badge shows amber `Locating...`; displays `Waiting for satellites...` | Coordinates remain uncommitted |
| GPS Permission Denied | User clicks "Don't Allow" on browser prompt | Header badge turns red `GPS Denied`; warning banner explains location access is required | Informs user how to enable permissions |

</frozen-after-approval>

## Code Map

- `src/main/resources/META-INF/resources/js/geo.js` -- Continuous `watchPosition` background loop, coordinate cache, 5s ticker, and GPS status emitter
- `src/main/resources/META-INF/resources/js/onboarding.js` -- First-visit onboarding modal, input validation, and `localStorage` manager
- `src/main/resources/META-INF/resources/js/format.js` -- Standard navigational coordinate formatting (`N XX° MM,MMM' / E XXX° MM,MMM'`)
- `src/main/resources/META-INF/resources/index.html` -- Updated layout with header user chip, GPS pill badge, 5s live coordinates card, onboarding modal, and hot coordinate display
- `src/main/resources/META-INF/resources/css/style.css` -- Modular dark theme styles matching `DESIGN.md` tokens
- `src/test/kotlin/org/shoetracker/frontend/FrontendAssetTest.kt` -- Automated verification that frontend assets and scripts compile and are served by Quarkus

## Tasks & Acceptance

**Execution:**
- [x] `src/main/resources/META-INF/resources/js/format.js` -- Implement pure coordinate formatting utility functions with comprehensive unit tests.
- [x] `src/main/resources/META-INF/resources/js/geo.js` -- Implement GeolocationWatcher with `watchPosition`, accuracy tracking, 5-second interval refresh, and state listeners (`ready`, `locating`, `denied`).
- [x] `src/main/resources/META-INF/resources/js/onboarding.js` -- Implement OnboardingManager checking `localStorage`, modal display, validation, and storage.
- [x] `src/main/resources/META-INF/resources/css/style.css` -- Create modular CSS stylesheet containing design tokens, telemetry cards, modal sheet animations, and badge colors.
- [x] `src/main/resources/META-INF/resources/index.html` -- Integrate header (user chip + GPS badge), live 5s coordinate telemetry display, onboarding modal sheet, and live GPS coordinate display into web UI.
- [x] `src/test/kotlin/org/shoetracker/frontend/FrontendAssetTest.kt` -- Add integration tests confirming static scripts, stylesheets, and index load with 200 OK.

**Acceptance Criteria:**
- Given a browser with no `user_id` in `localStorage`, when visiting `/`, then the onboarding sheet appears and requires a valid name.
- Given a valid name submitted, when saved, then `localStorage.getItem("user_id")` holds the name and the header displays `👤 <name>`.
- Given browser geolocation enabled, when the app mounts, then `watchPosition` actively caches coordinates and the header badge displays `GPS Ready`.
- Given locked coordinates, when displayed, then latitude and longitude are formatted as `N XX° MM,MMM' / E XXX° MM,MMM'` and update every 5 seconds.
- Given `./gradlew test`, then all backend and frontend asset verification tests pass.

## Implementation Notes

- Implemented `format.js` exporting `formatCoordinate(coord, isLat)` and `formatLocation(lat, lon)` using navigational GPS notation (`N XX° MM,MMM' / E XXX° MM,MMM'`) with minute leading zero padding.
- Implemented `geo.js` managing continuous background `navigator.geolocation.watchPosition` with high accuracy, state machine (`ready` / `locating` / `denied`), and 5-second interval telemetry event emitter.
- Implemented `onboarding.js` checking `localStorage.user_id`, displaying the modal sheet on first visit, validating 1..64 trimmed non-empty characters, saving identity, and updating header user badge.
- Implemented `css/style.css` incorporating `DESIGN.md` tokens: asphalt dark canvas (`#0F172A`), raised card (`#1E293B`), status pill badges (`#10B981` emerald for ready, `#F59E0B` amber pulse for locating, `#EF4444` red for denied), and smooth bottom sheet modal animations.
- Updated `index.html` wiring all modules, header user chip, live 5s GPS telemetry card, and hot coordinate injection into spot submission.
- Created `FrontendAssetTest.kt` verifying static assets are served properly and testing coordinate formatting parity. All 10 tests pass cleanly in 11s.

## Plan Change Log

## Review Triage Log

- Location: `geo.js:55-67` | Verdict: medium | Evidence: Telemetry update was firing unthrottled on every raw GPS fix. Patched to emit strictly on initial fix and 5-second interval timer. | Route: patch
- Location: `index.html:108-155` | Verdict: medium | Evidence: Missing warning banner for denied GPS state and button submitted hardcoded coordinates when GPS uncommitted. Patched with `.warning-banner` element and requiring active GPS fix before tap submission. | Route: patch
- Location: `geo.js:69-76` | Verdict: medium | Evidence: State remained `ready` on subsequent GPS timeout/loss. Patched `handleError` to return to `locating` on non-permission errors. | Route: patch
- Location: `geo.js:70-73` | Verdict: medium | Evidence: Permission denial did not clear cached position or cancel 5s ticker. Patched to nullify position and stop ticker on denial. | Route: patch
- Location: `format.js:10-17` | Verdict: medium | Evidence: Minutes rounding up to 60.000 failed to roll over into degrees. Patched with rollover check (`degrees += 1; minFixed = "00.000"`). | Route: patch
- Location: `FrontendAssetTest.kt:60-84` | Verdict: low | Evidence: Asset test lacked rollover test case. Patched with 60-minute rollover test case and script verification. | Route: patch
- Location: `architecture-shoe-tracker.md:AD-2` | Verdict: low | Evidence: Plain JS in `META-INF/resources` vs Vite TypeScript bundling. Bundling pipeline is scheduled for Story 4. | Route: defer

## Design Notes

- **Separation of Concerns:** Keep `format.js`, `geo.js`, and `onboarding.js` modular so they can be seamlessly reused in Story 3 (Big Button) and Epic 2 (Map & List views).

## Verification

**Commands:**
- `./gradlew test` -- expected: All integration and asset tests pass.
- `curl -s http://localhost:8080/js/geo.js` -- expected: HTTP 200 with geolocation watcher code.
- `curl -s http://localhost:8080/js/onboarding.js` -- expected: HTTP 200 with onboarding sheet code.
- `curl -s http://localhost:8080/` -- expected: HTTP 200 containing modal and GPS status markup.
