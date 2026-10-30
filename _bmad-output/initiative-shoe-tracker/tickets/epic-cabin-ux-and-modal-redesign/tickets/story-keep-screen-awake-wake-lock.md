---
id: 4
type: story
title: "Keep Smartphone Screen Awake via Screen Wake Lock API"
parent: "epic-cabin-ux-and-modal-redesign"
covers: ["R46"]
sources: []
companions: []
after: [1, 2, 3, 7]
refined: true
hitl: false
risk: low
---

# Keep Smartphone Screen Awake via Screen Wake Lock API

## Description

Prevents smartphones from dimming the display or entering standby while the Shoe Tracker web application is open in the foreground via a dedicated `/js/wakelock.js` module so passengers on the Autobahn can log spotted shoes immediately without unlocking their phone.

## Requirements

1. **Dedicated `/js/wakelock.js` Screen Wake Lock Acquisition with Gesture Fallback**
   **Given** the user opens Shoe Tracker on a mobile browser supporting `navigator.wakeLock`
   **When** `/js/wakelock.js` initializes on page load
   **Then** it requests an active screen wake lock (`navigator.wakeLock.request('screen')`) so the device screen does not dim or go into standby
   **And** registers a one-time `pointerdown`/`click` fallback listener so the wake lock is acquired on the first user interaction if the browser blocks the initial load request before a user gesture

2. **Automatic Re-Acquisition on Visibility Restoration**
   **Given** an active screen wake lock is released by the browser when the user switches tabs or minimizes the browser
   **When** the document becomes visible again (`document.visibilityState === 'visible'`)
   **Then** `/js/wakelock.js` automatically re-requests the screen wake lock

3. **Graceful Fallback on Unsupported Browsers or Battery Saver Mode**
   **Given** `navigator.wakeLock` is unavailable (e.g., insecure HTTP origin or unsupported browser) or the OS rejects the wake lock request (e.g., low-battery mode)
   **When** the wake lock request fails or is unsupported
   **Then** the error is caught silently without breaking geolocation watching, spotting submission, or UI rendering

## Boundaries

- Must not change: Background GPS watcher lifecycle or Spot button submission flow.

## References

- parent - _bmad-output/initiative-shoe-tracker/tickets/epic-cabin-ux-and-modal-redesign/epic-cabin-ux-and-modal-redesign.md, section Requirements
- design - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/EXPERIENCE.md

## Notes

- Decision: 2026-10-30: Implemented in a dedicated `/js/wakelock.js` module (loaded in `index.html`) that requests the wake lock on load, re-acquires on `visibilitychange` (`'visible'`), and includes a one-time `pointerdown`/`click` fallback if initial load requires a user gesture.
