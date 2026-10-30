---
type: story
title: "Keep Smartphone Screen Awake via Screen Wake Lock API"
parent: ""
covers: []
sources: []
companions: []
after: []
refined: true
hitl: false
risk: low
---

# Keep Smartphone Screen Awake via Screen Wake Lock API

## Description

Prevents smartphones from dimming the display or entering standby while the Shoe Tracker web application is open in the foreground so passengers on the Autobahn can log spotted shoes immediately without unlocking their phone.

## Requirements

1. **Screen Wake Lock Acquisition**
   **Given** the user opens Shoe Tracker on a mobile browser supporting `navigator.wakeLock`
   **When** the page loads or the user interacts with the page
   **Then** the application requests an active screen wake lock (`navigator.wakeLock.request('screen')`) so the device screen does not dim or go into standby

2. **Automatic Re-Acquisition on Visibility Restoration**
   **Given** an active screen wake lock is released by the browser when the user switches tabs or minimizes the browser
   **When** the document becomes visible again (`document.visibilityState === 'visible'`)
   **Then** the application automatically re-requests the screen wake lock

3. **Graceful Fallback on Unsupported Browsers or Battery Saver Mode**
   **Given** `navigator.wakeLock` is unavailable (e.g., insecure HTTP origin or unsupported browser) or the OS rejects the wake lock request (e.g., low-battery mode)
   **When** the wake lock request fails or is unsupported
   **Then** the error is caught silently without breaking geolocation watching, spotting submission, or UI rendering

## Boundaries

- Must not change: Background GPS watcher lifecycle or Spot button submission flow.

## References

- parent - _bmad-output/initiative-shoe-tracker/initiative-shoe-tracker.md, section Requirements
- design - _bmad-output/initiative-shoe-tracker/ux-shoe-tracker/EXPERIENCE.md

## Notes

- Decision: 2026-10-30: Wake lock is active automatically whenever the app is visible in the foreground, without requiring a separate settings toggle.
