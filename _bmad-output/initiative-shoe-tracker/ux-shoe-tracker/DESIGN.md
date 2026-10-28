---
name: Shoe Tracker
description: High-velocity mobile autobahn utility. High contrast, dark asphalt canvas, oversized touch targets, instant visual feedback.
status: final
colors:
  surface-base: '#0F172A'       # Slate 900 (asphalt background)
  surface-raised: '#1E293B'     # Slate 800 (cards, navbar, modals)
  surface-overlay: '#334155'    # Slate 700 (chips, hover states)
  ink-primary: '#F8FAFC'        # Slate 50 (high legibility text)
  ink-secondary: '#94A3B8'      # Slate 400 (timestamps, coordinates)
  ink-muted: '#64748B'          # Slate 500 (disabled, placeholders)
  action-primary: '#F97316'     # Safety Orange 500 (big spot button)
  action-hover: '#EA580C'       # Orange 600
  action-cooldown: '#475569'    # Slate 600 (button cooldown state)
  marker-own: '#3B82F6'         # Blue 500 (current user's shoes)
  marker-other: '#94A3B8'       # Slate 400 (other users' shoes)
  status-gps-ready: '#10B981'   # Emerald 500
  status-gps-warn: '#F59E0B'    # Amber 500
  status-gps-error: '#EF4444'   # Red 500
  border-subtle: '#334155'      # Slate 700
typography:
  display:
    note: 'System Bold, 32px / 36px line height (stats, countdown)'
  headline:
    note: 'System Semibold, 20px / 24px line height (page headers, modal titles)'
  body:
    note: 'System Regular, 16px / 22px line height (list items, descriptions)'
  caption:
    note: 'System Medium, 12px / 16px line height (GPS status, coordinates, tags)'
rounded:
  sm: '6px'
  md: '12px'
  lg: '20px'
  full: '9999px'
spacing:
  '1': '4px'
  '2': '8px'
  '3': '12px'
  '4': '16px'
  '5': '24px'
  '6': '32px'
  '8': '48px'
---

# Shoe Tracker - Visual Design Specification

## Brand & Style

Shoe Tracker is an ergonomic, playful mobile utility designed for passengers spotting lone shoes along the German Autobahn. Not mission-critical or clinical software — it is a fun, tactile road-trip companion. The visual language pairs an asphalt dark-slate canvas (`#0F172A`) with high-visibility safety orange (`#F97316`) and sharp signal blue (`#3B82F6`).

Every screen is tuned for high vibration, sunlight glare, and single-handed thumb operation. Fiddly controls, delicate typography, and low-contrast grey-on-grey text are strictly avoided.

## Colors

- **Asphalt Base (`#0F172A`)**: Deep slate blue canvas that minimizes night-driving glare and keeps attention focused on the action button.
- **Card & Navigation Surface (`#1E293B`)**: Raised slate for cards, bottom tabs, and headers.
- **Spot Action Button (`#F97316`)**: Radiant highway safety orange. High luminance contrast against the dark background, instantly identifiable in peripheral vision.
- **User Marker (`#3B82F6`)**: High-saturation signal blue, clearly marking "my shoes" on both the map and list views.
- **Other Marker (`#94A3B8`)**: Neutral muted slate for shoe sightings logged by other drivers.
- **GPS Status Green (`#10B981`)**: Reassuring visual confirmation that GPS satellites are locked and coordinates are hot.

## Typography

Native system font stack (`system-ui`, `-apple-system`, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif).
- **Big Button & Countdown**: `display` (32px Bold), uppercase tracking.
- **Screen & Section Titles**: `headline` (20px Semibold).
- **Coordinates & Timestamps**: `caption` (12px Medium, monospace numbers `font-variant-numeric: tabular-nums`).
- **Coordinate Display Convention**: Formatted in standard navigational notation: `N XX° MM,MMM'` / `E XXX° MM,MMM'` (e.g. `N 50° 06,654' / E 008° 40,928'`) using comma decimal separator.

## Layout & Spacing

Mobile-first single-column layout constrained to `max-width: 480px` centered on wider screens.
- **Safe Area Insets**: Honored for mobile notches and bottom swipe bars (`env(safe-area-inset-bottom)`).
- **Navigation**: Fixed bottom tab bar (`height: 64px`) with 3 touch targets: `[👟 Spot]`, `[🗺️ Map]`, `[📋 List]`. Minimum touch target size: 48px × 48px.
- **Spot Screen Split Layout**: Upper area houses the tactical HUD mini-map (`max-height: 35vh; min-height: 200px; border-radius: 12px; overflow: hidden;`) with touch dragging disabled and dark-slate tile background (`#0F172A`). Lower area houses the 210px Spot Button, guaranteed to remain 100% visible without scrolling on screens ≥ 600px height.

## Elevation & Depth

No heavy skeumorphic drop shadows. Modern subtle edge elevation:
- Cards: `box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.3)`.
- Main Spot Button: Outer ambient glow `0 0 24px rgba(249, 115, 22, 0.35)` when ready to tap. Pressed state scales down (`transform: scale(0.96)`) with instant haptic feel.

## Shapes

- **The Spot Button**: Pure circle (`rounded: full`), diameter `210px` on mobile screens.
- **Cards & Dialogs**: `rounded: md` (12px) for a modern, tactile feel.
- **Pill Toggles & Badges**: `rounded: full` for status chips and filter toggles.

## Components

### 1. The Spot Button (`.btn-spot`)
- Dimensions: `210px × 210px` circle, centered vertically and horizontally.
- Visuals: Bold orange fill, white shoe icon + "SHOE SPOTTED!" label.
- States:
  - **Ready**: Orange gradient, pulsing subtle ambient halo.
  - **Pressed**: Depressed 4%, halo compresses.
  - **Cooldown**: Switches to muted slate (`#475569`), displays circular countdown SVG stroke and seconds remaining (`"Logged! 3s"`).
  - **No GPS**: Muted grey, yellow warning badge `"Waiting for GPS..."`.

### 2. GPS Status Pill (`.gps-pill`)
- Sits in top right of header: green dot + `"GPS Ready"` or blinking amber `"Locating..."`.

### 3. Sighting Map Marker
- Custom SVG divIcon in Leaflet.
- "My Shoe": Blue circle marker with shoe icon and white border (`size: 32px`).
- "Other Shoe": Neutral slate circle marker (`size: 26px`).

### 4. Filter Toggle Bar
- Fixed above the map and list: Segmented pill toggle: `[ All Shoes (42) ]` vs `[ My Shoes (7) ]`.

### 5. Post-Tap Sighting Modal (`.modal-sighting`)
- Triggered immediately after tapping the Spot Button.
- Dark slate card overlay with rounded corners (`rounded: md`).
- Content:
  - Header: `"👟 Shoe Spotted!"`
  - Meta: Recorded timestamp in local time and coordinates.
  - Action button: Large `"Got It!"` or `"View on Map"` button to dismiss, plus a quiet `"Undo"` option.

## Do's and Don'ts

### Do
- Ensure all clickable targets on the Spot screen are at least 48px × 48px.
- Use tabular numbers for timestamps and lat/long coordinates.
- Show an engaging confirmation modal immediately after button tap with coordinates and time.

### Don't
- Never use low-contrast text on dark backgrounds (must satisfy WCAG AA 4.5:1 ratio).
- Do not let map tiles load in blinding pure-white; use standard OSM tiles with CSS filter or dark carto tiles.
