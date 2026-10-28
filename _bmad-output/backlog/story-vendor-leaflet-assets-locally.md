---
type: story
title: "Vendor Leaflet 1.9.4 Assets Locally for Offline Resilience"
parent: ""
covers: []
sources: []
companions: []
after: []
risk: low
refined: false
---

# Vendor Leaflet 1.9.4 Assets Locally for Offline Resilience

## Description

Vendors Leaflet 1.9.4 CSS, JavaScript, and marker icon assets locally into `src/main/resources/META-INF/resources/vendor/leaflet/`. Eliminates external runtime CDN dependencies (`unpkg.com`), ensuring map shells and markers load reliably even in rural Autobahn cellular dead zones.

## Background & Rationale

Shoe Tracker is designed for highway passengers traveling at high speeds where network connectivity is intermittent. If Leaflet assets are loaded via a third-party CDN, a cold app launch in a tunnel or rural 3G dead zone fails to initialize the map entirely. Serving Leaflet directly from the local Quarkus container fulfills constraint C4 (single self-contained container) and guarantees offline readiness.

## Acceptance Criteria

- `leaflet.js`, `leaflet.css`, and default marker images (`marker-icon.png`, `marker-icon-2x.png`, `marker-shadow.png`) are stored in `src/main/resources/META-INF/resources/vendor/leaflet/`.
- `index.html` references `/vendor/leaflet/leaflet.css` and `/vendor/leaflet/leaflet.js` without any external network CDN links.
- Automated tests verify that `/vendor/leaflet/leaflet.js` and `/vendor/leaflet/leaflet.css` are served with HTTP 200 OK directly by Quarkus.
