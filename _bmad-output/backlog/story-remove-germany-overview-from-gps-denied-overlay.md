---
type: story
title: "Remove '— Germany Overview' Suffix from GPS Denied Mini-Map Overlay"
parent: "epic-versions-and-build"
covers: ["R40"]
sources: []
companions: []
after: []
risk: low
hitl: false
refined: true
---

# Remove '— Germany Overview' Suffix from GPS Denied Mini-Map Overlay

## Description

Removes the redundant ` — Germany Overview` (EN) and ` — Deutschland-Übersicht` (DE) suffix from the HUD mini-map location-denied overlay (`#miniMapDeniedOverlay`) when GPS access is unavailable or denied, keeping the warning concise (`⚠️ Location Access Denied` / `⚠️ Standortzugriff verweigert`).

## Requirements

1. **Concise Default HTML Overlay Copy (`index.html`)**
   **Given** `#miniMapDeniedOverlay` in `src/main/resources/META-INF/resources/index.html` renders the fallback `<span data-i18n="spot.deniedOverlay">`
   **When** the Spot screen loads and GPS access is denied (`status === 'denied'`)
   **Then** the default HTML text is `⚠️ Location Access Denied` without ` — Germany Overview`

2. **English and German Localization Strings (`i18n.js`)**
   **Given** `src/main/resources/META-INF/resources/js/i18n.js` defines `spot.deniedOverlay` in `TRANSLATIONS.en` and `TRANSLATIONS.de`
   **When** the language is set to English (`en`) or German (`de`)
   **Then** `TRANSLATIONS.en['spot.deniedOverlay']` is `'⚠️ Location Access Denied'` and `TRANSLATIONS.de['spot.deniedOverlay']` is `'⚠️ Standortzugriff verweigert'`
   **And** neither `Germany Overview` nor `Deutschland-Übersicht` appears in `index.html` or `i18n.js`

3. **Automated Frontend Asset Test Alignment (`FrontendAssetTest.kt`)**
   **Given** `FrontendAssetTest.kt` validates `index.html` and `i18n.js`
   **When** `./gradlew check test` runs
   **Then** the test suite verifies that `index.html` and `i18n.js` contain `⚠️ Location Access Denied` and `⚠️ Standortzugriff verweigert` and do not contain `Germany Overview` or `Deutschland-Übersicht`

## Boundaries

- Must not change: HUD mini-map fallback center coordinates (`[50.8, 9.7]`, zoom `6` in `minimap.js`), top warning banner (`#gpsWarningBanner`), or Spot button disabled state when GPS is denied.
