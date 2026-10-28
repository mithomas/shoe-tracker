---
type: story
title: "Light and Dark Mode Theme Switcher"
parent: ""
covers: []
sources: []
companions: []
after: []
risk: low
refined: false
---

# Light and Dark Mode Theme Switcher

## Description

Adds a light and dark theme mode toggle directly in the top header bar, supports automatic matching of system OS theme preferences, and persists the user's manual selection in local storage.

## Background & Rationale

Shoe Tracker currently defaults to an asphalt dark slate theme (`#0F172A`). During sunny daytime drives on the Autobahn, sunlight glare can make dark mode difficult to read on mobile screens mounted on dashboards. Introducing a high-contrast light mode with an immediate top-bar toggle and system preference auto-detection ensures optimal visibility under all driving conditions.

## Acceptance Criteria

- Top header bar includes an accessible theme toggle button (e.g. ☀️ / 🌙 icon).
- Listens to system OS preference changes via `window.matchMedia('(prefers-color-scheme: dark)')` and updates the active theme automatically when no manual override is active.
- Tapping the toggle in the top bar switches between light and dark modes instantly.
- Manual theme selection is persisted in `localStorage.theme` (`'light'` or `'dark'`).
- Defines clean light mode CSS tokens:
  - `--surface-base`: `#F8FAFC` (slate-50)
  - `--surface-raised`: `#FFFFFF`
  - `--surface-overlay`: `#F1F5F9`
  - `--ink-primary`: `#0F172A`
  - `--ink-secondary`: `#475569`
  - `--border-subtle`: `#E2E8F0`
- Leaflet map containers and tiles adapt cleanly between themes without visual clipping.
- Automated tests verify theme toggle presence, localStorage persistence, and attribute application on the document root (`data-theme="light"` / `data-theme="dark"`).
