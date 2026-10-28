---
type: story
title: "Multi-Language Support (English and German)"
parent: ""
covers: []
sources: []
companions: []
after: []
risk: low
refined: false
---

# Multi-Language Support (English and German)

## Description

Adds internationalization (i18n) support for German (`de`) and English (`en`), enabling passengers to use the interface in their native language with authentic Autobahn-specific terminology.

## Acceptance Criteria

- User interface supports switching between German (`de`) and English (`en`).
- Detects browser language preference (`navigator.language`) by default.
- German localization includes terminology:
  - "SHOE SPOTTED!" -> "Schuh!" (button and modal title)
  - "GPS Ready" -> "GPS Bereit"
  - "Waiting for GPS..." -> "Warte auf GPS..."
  - "GPS Denied" -> "GPS Verweigert"
  - "Recent Sightings" -> "Letzte Sichtungen"
  - "What's your name?" -> "Wie heißt du?"
- Setting is persisted in browser `localStorage`.
