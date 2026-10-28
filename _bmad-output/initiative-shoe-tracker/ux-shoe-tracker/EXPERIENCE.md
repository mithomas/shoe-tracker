---
name: Shoe Tracker
status: final
sources:
  - ../initiative-shoe-tracker.md
  - ../architecture-shoe-tracker/architecture-shoe-tracker.md
updated: '2026-10-28'
---

# Shoe Tracker - Experience Specification

## Foundation

Single-surface mobile web application designed for smartphones mounted in vehicle holders or held by highway passengers. Clean Vanilla TypeScript, CSS3, and HTML5. `DESIGN.md` governs visual tokens and styling; this spine governs user interaction, state machines, and component behavior. Dark mode is default and non-negotiable to minimize driving glare. Coordinates are displayed in standard navigational format: `N XX° MM,MMM'` / `E XXX° MM,MMM'`.

## Information Architecture

| Surface | Reached from | Purpose |
|---|---|---|
| **Spot Screen** | Default cold open (or Tab 1) | The primary logging interface: upper area renders locked HUD mini-map tracking real-time GPS position; lower area houses the 210px Spot Button and live telemetry. |
| **Map Screen** | Tab 2 | Dedicated full Leaflet map displaying all sightings, filter toggle, and marker details. Lazy-initialized on first view. |
| **List Screen** | Tab 3 | Chronological list of recorded sightings, filter toggle, and local time display. |
| **Onboarding Sheet** | Overlay on cold open (if no user name in `localStorage`) | Prompts for user name before first spotting. Cannot be dismissed without a name. |
| **Sighting Modal** | Overlay on Spot Screen (after tap) | Displays recorded time and coordinates with confirmation and quick navigation options. |

Fixed bottom navigation bar (`[Spot]`, `[Map]`, `[List]`). View transitions are instant without disorienting sliding animations.

## Voice and Tone

Microcopy is crisp, reassuring, and immediate. Never use conversational filler, lengthy explanations, or exclamation overload.

| Do | Don't |
|---|---|
| "SHOE SPOTTED!" | "Click here to submit your shoe sighting" |
| "Logged! Cooldown 3s..." | "Your entry was successfully sent to the server!" |
| "GPS Ready" | "Satellite fix acquired with accuracy 4.2m" |
| "Acquiring GPS lock..." | "Waiting for browser permissions..." |
| "What's your name?" | "Please create an account to continue" |

## Component Patterns

### 1. The Spot Button
- **Placement**: Centered vertically in viewport, min diameter 200px.
- **Behavior**:
  - Tap immediately reads the cached coordinate from background `watchPosition`.
  - Sends asynchronous `POST /api/v1/sightings`.
  - Disables button for 3 seconds, starts visual countdown animation: `"Logged! 3s"`, `"2s"`, `"1s"`, then returns to `"SHOE SPOTTED!"`.
  - Triggers the Sighting Modal overlay showing captured details.
  - Rejects any further taps during cooldown.
  - If GPS is not ready, button displays `"Acquiring GPS..."` and does not submit incomplete coordinates.

### 2. Sighting Modal
- **Trigger**: Appears immediately after tapping the Spot Button.
- **Content**:
  - Header: `"👟 Shoe Spotted!"`
  - Meta: Captured timestamp in browser local time (e.g. `14:32:05`), latitude & longitude.
  - Primary button: `"Awesome!"` (dismisses modal and returns to Spot screen).
  - Secondary button: `"View on Map"` (dismisses modal and switches to Map tab).
  - Tertiary action: `"Undo"` (cancels the sighting).

### 3. Onboarding Sheet
- **Trigger**: App mount detects `localStorage.getItem("user_id") === null`.
- **UI**: Bottom drawer modal over dimmed background. Single input field: `"Enter your name"`.
- **Validation**: 1 to 64 characters, trimmed.
- **Action**: Tapping `"Start Spotting"` saves to `localStorage.setItem("user_id", name)` and dismisses the sheet immediately.

### 3. Sighting Map Marker
- **Behavior**:
  - Markers matching `sighting.user.id === localStorage.getItem("user_id")` are highlighted in `{colors.marker-own}` (Signal Blue).
  - All other markers render in `{colors.marker-other}` (Neutral Slate).
  - Tap on marker opens popup: `"Tom's Shoe"`, local time formatted (e.g. `Oct 28, 14:32`), and coordinates.

### 4. Visibility Toggle Bar
- **Behavior**: Two-option pill toggle on Map and List screens: `[ All Shoes ]` / `[ My Shoes ]`.
- Toggling `"My Shoes"` hides all markers/rows where `sighting.user.id !== localStorage.getItem("user_id")`.

## State Patterns

| State | Surface | Treatment |
|---|---|---|
| **First Launch** | Spot Screen | Displays Onboarding Sheet. Primary button disabled until name is confirmed. |
| **GPS Searching** | Header & Button | Header badge shows blinking amber `"Locating..."`. Spot button displays `"Waiting for GPS..."`. |
| **GPS Locked (Hot)** | Header & Button | Header badge shows solid emerald `"GPS Ready"`. Spot button pulses ready. |
| **Tap Success** | Spot Screen | Button enters 3s countdown cooldown. Sighting Modal pops up displaying captured location and timestamp. |
| **GPS Permission Denied** | Spot Screen | Header badge turns red `"GPS Denied"`. Banner prompts: `"Location access required to log shoes on the autobahn."` |
| **Empty Map / List** | Map & List | `"No shoes spotted yet! Tap the Spot button to be the first."` |
| **Network Error on Tap** | Spot Screen | Top alert: `"Could not reach server. Check connection."` Cooldown releases after 3s. |

## Interaction Primitives

- Single tap for all primary actions.
- Pinch-to-zoom and drag on Leaflet map.
- Tap marker to inspect popup.
- Haptic feedback (via `navigator.vibrate(50)`) on button press where supported by mobile browser.
- Strictly banned: Double-tap requirements, long-press actions, nested accordion menus.

## Accessibility Floor

- **Touch targets**: Primary button is 210px. Navigation tabs are min 48px × 48px.
- **Contrast**: All text elements exceed WCAG AA 4.5:1 ratio against dark slate background.
- **Screen Reader**:
  - Spot button has `aria-label="Spot shoe and log location"`.
  - Cooldown announces `aria-live="polite"` state.

## Key Flows

### Flow 1: High-Speed Spotting
1. **Context**: Max is riding in the passenger seat on the A9 near Nuremberg at 160 km/h. He sees a single lost boot on the highway shoulder.
2. **Action**: He unlocks his phone, opens Shoe Tracker. The screen is already dark, GPS shows `"GPS Ready"`.
3. **Climax**: Max slams his thumb down anywhere on the giant orange button.
4. **Outcome**: The button compresses, vibrates slightly, and immediately shifts to `"Logged! 3s"`. Coordinates are already sent to the backend with zero satellite lag, and the Sighting Modal appears displaying `"👟 Shoe Spotted! Logged at 14:32:05 near KM 424"`. Max taps `"Awesome!"` and watches for the next shoe.

### Flow 2: Reviewing My Shoes on the Map
1. **Context**: At a rest stop, Max wants to see where his shoes were logged.
2. **Action**: He taps the `[Map]` tab in the bottom bar.
3. **Outcome**: The map opens centered on Germany, showing blue pins for his shoes and slate pins for others. He toggles `"My Shoes"` to clear the clutter, taps a blue pin, and sees his sighting from 15 minutes ago with its exact timestamp.
