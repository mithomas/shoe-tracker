---
title: 'Sneaker Favicon, Leaflet mvnpm Dependency, Retina Assets & Offline Tile Notice'
type: 'feature'
ticket: 1
created: '2026-10-29'
status: 'built'
baseline_revision: '709e9387e63e5374b8810895185b23985803d469'
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

**Problem:** Leaflet assets (`leaflet.js`, `leaflet.css`, and marker images) are manually committed under `src/main/resources/META-INF/resources/vendor/leaflet/` without `marker-icon-2x.png` retina support or Dependabot version tracking, the browser logs 404 errors for missing `/favicon.svg` and `/favicon.ico`, and rural Autobahn cellular dead zones cause silent map tile load failures without user feedback.

**Approach:** Add `org.mvnpm:leaflet:1.9.4` to `build.gradle.kts` and configure a Gradle build task wired into `processResources` to unpack `META-INF/resources/_static/leaflet/*/dist/**` into generated `META-INF/resources/vendor/leaflet/**` while removing committed `src/main/resources/META-INF/resources/vendor/leaflet/**` from git so `index.html` retains version-agnostic `/vendor/leaflet/*` URLs (including `/vendor/leaflet/images/marker-icon-2x.png`). Add `/favicon.svg` and `/favicon.ico` sneaker icons linked in `index.html`, and attach `tileerror` / `tileload` listeners to `#spotMiniMap` and `#fullMap` to toggle an offline map tile fallback notice banner (`#offlineTileBanner`).

</frozen-after-approval>

## Implementation Notes

- Selected `oneshot` route because the change is localized (< 100 lines modified across `build.gradle.kts`, `index.html`, `minimap.js`, `fullmap.js`, `favicon.svg`, `favicon.ico`, and `FrontendAssetTest.kt`).
- Added `implementation("org.mvnpm:leaflet:1.9.4")` and `extractLeafletAssets` task to `build.gradle.kts` wired into `sourceSets.main.resources` and `processResources`, unpacking `META-INF/resources/_static/leaflet/*/dist/**` into `META-INF/resources/vendor/leaflet/**` and removing the raw committed `src/main/resources/META-INF/resources/vendor/leaflet/**` files from git.
- Created `/favicon.svg` and `/favicon.ico` and linked both in `index.html`.
- Added `#offlineTileBanner` in `index.html` and wired `tileerror` / `tileload` listeners plus `L.Icon.Default.imagePath = '/vendor/leaflet/images/'` in `minimap.js` and `fullmap.js`.
- Added `testFaviconRetinaAndMvnpmLeafletAssetsServed` in `FrontendAssetTest.kt`.

## Plan Change Log

## Review Triage Log

## Verification

**Commands:**
- `./gradlew check test` -- expected: `BUILD SUCCESSFUL`, all unit/integration tests, `spotlessCheck`, and `lintJs` pass.
