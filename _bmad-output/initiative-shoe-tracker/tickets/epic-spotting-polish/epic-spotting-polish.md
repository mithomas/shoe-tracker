---
type: epic
title: "Spotting UX Polish & German Localization"
parent: "initiative-shoe-tracker"
covers: ["R28", "R29", "R30", "R31", "R32", "R33", "R34", "R35"]
sources: ["_bmad-output/initiative-shoe-tracker/initiative-shoe-tracker.md"]
companions: ["_bmad-output/initiative-shoe-tracker/ux-shoe-tracker/DESIGN.md", "_bmad-output/initiative-shoe-tracker/ux-shoe-tracker/EXPERIENCE.md"]
after: ["epic-views-and-map"]
refined: true
risk: low
---

# Spotting UX Polish & German Localization

## Description

Polishes the passenger road-trip experience on the Autobahn. Eliminates redundant button cooldown locks now that the confirmation modal intercepts taps, adds a light/dark theme mode toggle in the top bar with system auto-detection, introduces German and English localization with authentic Autobahn terminology ("SCHUH!"), renders nearby existing shoe sightings directly on the live tracking mini-map, clusters nearby map sightings by zoom level with circular count badges and own-shoe cluster highlighting, adds a "View on Map" action on list sighting cards to jump directly to the map marker and open its popup, displays reactive total and personal sighting counters ("Total" / "Mine"), includes high-DPI retina Leaflet marker assets with graceful offline tile fallback notices, adds a dedicated sneaker favicon (SVG & ICO) to eliminate browser 404 errors, and transitions Leaflet assets from raw vendoring to an automated mvnpm Maven dependency (org.mvnpm:leaflet:1.9.4) for Dependabot tracking.

## Outcome

Passengers enjoy a frictionless, high-contrast, localized spotting and exploration experience adapted for both daytime glare and nighttime Autobahn driving, with real-time awareness of nearby shoes on the mini-map, decluttered zoom-adaptive marker clustering on the main map, one-tap navigation from list entries to map locations, live global and personal sighting counters, and graceful offline map notices in rural cellular dead zones.

## Requirements

- R28 (parent R28): Spot button removes the 3-second countdown cooldown ring (`cooldown-ring`, `cooldownRingProgress`, `startCooldown`, `releaseCooldown`) in favor of the immediate Sighting Confirmation Modal barrier (`#sightingModal`) and an in-flight request lock during `POST /api/v1/sightings`, returning `#btnSpotGiant` to its ready state immediately upon response and modal dismissal.
- R29 (parent R29): Top header bar provides a compact Light and Dark Mode Theme Switcher (`#themeToggleBtn`) that auto-detects OS preference via `window.matchMedia('(prefers-color-scheme: dark)')`, persists manual selection in `localStorage.theme` (`'light'` or `'dark'`), applies `data-theme="light"` / `data-theme="dark"` on `document.documentElement` with light-mode CSS tokens (`--surface-base: #F8FAFC`, `--surface-raised: #FFFFFF`, `--surface-overlay: #F1F5F9`, `--ink-primary: #0F172A`, `--ink-secondary: #475569`, `--border-subtle: #E2E8F0`), resets `#spotMiniMap .leaflet-tile, #fullMap .leaflet-tile { filter: none; }` in light mode for native high-contrast daylight map tiles, and uses theme-aware surface variables on `.map-overlay-banner`, `.mini-map-hud-badge`, and `.mini-map-overlay`.
- R30 (parent R30): Multi-language localization (`i18n.js`) supports runtime switching between English (`en`) and German (`de`) from a compact top-bar toggle (`#langToggleBtn`) inside a `flex-wrap: wrap` header that fits 360px mobile screens without overflow, auto-detects browser language (`navigator.language`), persists selection in `localStorage.lang`, and localizes UI strings including authentic Autobahn terminology (`"SHOE SPOTTED!"` -> `"SCHUH!"` on the Spot button and confirmation modal, `"GPS Ready"` -> `"GPS Bereit"`, `"Waiting for GPS..."` -> `"Warte auf GPS..."`, `"GPS Denied"` -> `"GPS Verweigert"`, `"Recent Sightings"` -> `"Letzte Sichtungen"`, `"What's your name?"` -> `"Wie heißt du?"`).
- R31 (parent R31): Live tracking HUD mini-map (`#spotMiniMap`) warms sightings on initial app startup (`GET /api/v1/sightings?limit=500`) and renders nearby recorded shoe sightings as compact dot markers (Signal Blue `#3B82F6` for the active user's shoes, Slate `#94A3B8` for others) alongside the live GPS dot, updating reactively on `sighting-added`, `sighting-deleted`, and `user-changed` events.
- R32 (parent R32): Full interactive map (`#fullMap`) dynamically clusters nearby sighting markers at lower zoom levels (`zoom < 15`) into circular count badges (styled in Signal Blue `#3B82F6` when the cluster includes at least one of the active user's sightings, or Slate `#475569` otherwise), expands clusters on click via `fitBounds(clusterBounds, { padding: [40, 40], maxZoom: 15 })` (zooming directly to `Math.max(currentZoom + 2, 15)` when tightly co-located points span `< 0.0005` degrees so clicking always reaches `zoom >= 15`), renders individual pins at street/highway zoom (`zoom >= 15`), and recalculates clusters on `zoomend`, `sighting-added`, `sighting-deleted`, `user-changed`, and `filter-changed`.
- R33 (parent R33): Sighting entries in the List view (`#sightingsList`) include a `"View on Map"` action button that invokes `fullMap.focusSighting(id)` to deterministically switch to the `[Map]` tab (bootstrapping `#fullMap` if not yet initialized and flushing `invalidateSize(true)`), automatically switch the filter to `"All Shoes"` (`all`) if `"My Shoes"` (`own`) is active and the target sighting belongs to another user, set the map view to `zoom 16` at `[lat, lon]` so unclustered markers materialize in `markersById`, and open `markersById[id].openPopup()` (also wired for `"View on Map"` in the Sighting Confirmation Modal).
- R34 (parent R34): Interface fetches sightings on initial boot and replaces the legacy raw JSON `<pre>` debug box (`#output` / `#btnRefresh`) on the Spot screen (`#viewSpot`) with a reactive Sighting Counter Card (`"12 Total | 4 Mine"` in English / `"12 Gesamt | 4 Meine"` in German) while displaying the same `Total | Mine` breakdown in the List view header (`#listCountBadge`), recalculating immediately on `sighting-added`, `sighting-deleted`, `user-changed`, and `lang-changed` without requiring a page reload.
- R35 (parent R35): Static web assets consume Leaflet via `org.mvnpm:leaflet:1.9.4` in `build.gradle.kts` (tracked by Dependabot) unpacked at build time (`processResources`) into `/vendor/leaflet/**` so committed `src/main/resources/META-INF/resources/vendor/leaflet/**` is deleted from git while `index.html` retains version-agnostic `/vendor/leaflet/leaflet.css`, `/vendor/leaflet/leaflet.js`, and `/vendor/leaflet/images/marker-icon-2x.png` URLs, plus an offline tile fallback indicator when map tiles fail to load and a dedicated sneaker favicon (`/favicon.svg` and `/favicon.ico`) declared in `index.html`.

## Done when

- Light and dark mode switcher operates from top bar with automatic OS matching and localStorage persistence.
- Interface supports German and English language switching, rendering "SCHUH!" on the spot button and modal in German mode.
- 3-second button cooldown ring is removed in favor of the immediate confirmation modal.
- Live tracking mini-map renders nearby logged shoes as compact dot markers as the vehicle moves.
- Main map view groups nearby sighting markers at lower zoom levels into circular cluster bubbles displaying sighting counts, highlights clusters containing the active user's shoes, expands clusters on click/zoom, and recalculates clusters when the "My Shoes" filter toggles.
- List view sighting entries include a "View on Map" action that switches to the Map tab, smoothly centers and zooms (zoom 16) to the sighting's coordinates, opens its marker popup, and handles active "My Shoes" filter state.
- Interface displays total network sightings and personal sightings for the active user (e.g., "12 Total | 4 Mine"), updating reactively when sightings are created, undone/deleted, or when the active username changes.
- Leaflet high-DPI retina marker assets (`marker-icon-2x.png`) are locally available and an offline indicator informs the user if map tiles cannot load.
- Dedicated sneaker favicon (SVG & ICO) is served at /favicon.svg and /favicon.ico and declared in index.html to eliminate 404s.
- Leaflet is consumed via org.mvnpm:leaflet:1.9.4 in build.gradle.kts and tracked automatically by Dependabot.

## Boundaries

- Must not change: Core REST API contract (`/api/v1/sightings`) and locked HUD radar behavior (touch drag disabled, scroll lock, max 35vh height) on the Spot screen mini-map (`C6`).
- Backend architecture and multi-database support belong to completed infrastructure epics (Epics 4, 5, and 6).
- Real-time Server-Sent Events (SSE) push streaming remains deferred to the post-MVP backlog.

## References

- parent - _bmad-output/initiative-shoe-tracker/initiative-shoe-tracker.md, section Requirements
- ux - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/DESIGN.md
- ux - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/EXPERIENCE.md

## Notes

- Decision: 2026-10-29: Expanded Epic 3 scope to incorporate three UX backlog enhancements: map marker clustering by zoom level (`R32`), jump to map from sighting entry (`R33`), and reactive global/personal sighting counters (`R34`).
- Decision: 2026-10-29: Story 3.1 is the tracer bullet establishing `org.mvnpm:leaflet:1.9.4` in `build.gradle.kts`, high-DPI retina marker assets (`marker-icon-2x.png`), offline tile fallback notices, and the sneaker favicon (`/favicon.svg` & `/favicon.ico`).
- Decision: 2026-10-29: Stories `3.1 -> 3.2 -> 3.3 -> 3.4 -> 3.5 -> 3.6 -> 3.7 -> 3.8` execute in a single sequential lane because all stories touch shared frontend files (`index.html`, `style.css`, and `FrontendAssetTest.kt`).
- Decision: 2026-10-29: Map marker clustering (`R32`) is implemented in pure vanilla JS inside `fullmap.js` (zero external third-party plugin dependencies) using pixel-grid / distance clustering below zoom 15 and unclustered individual markers at zoom >= 15, ensuring compatibility with `lintJs` (Google Closure Compiler) and Story 3.7's zoom-16 `"View on Map"` popup targeting.
- Decision: 2026-10-29: Grilled & hardened 5 edge-case contracts: (1) Gradle `processResources` extracts `org.mvnpm:leaflet` dist files into generated `/vendor/leaflet/**` and deletes raw committed `vendor/leaflet` files so Dependabot bumps never break hardcoded version paths in `index.html`; (2) sightings are fetched on initial boot and the legacy raw JSON `<pre>` debug card on `#viewSpot` is replaced by a reactive `Total | Mine` Sighting Counter Card (with matching `Total | Mine` counter in `#listCountBadge`); (3) cluster click uses `maxZoom: 15` (and direct zoom `Math.max(currentZoom + 2, 15)` when tightly co-located `< 0.0005` deg) while `focusSighting(id)` bootstraps the lazy map, resets `"My Shoes"` filter when targeting another user's shoe, zooms to `16` to uncluster, and opens `markersById[id].openPopup()`; (4) `[data-theme="light"]` resets `.leaflet-tile { filter: none; }` and uses compact header pills with `flex-wrap: wrap` for 360px screens; (5) the 3-second cooldown ring is removed while keeping the Sighting Confirmation Modal (`#sightingModal`).
