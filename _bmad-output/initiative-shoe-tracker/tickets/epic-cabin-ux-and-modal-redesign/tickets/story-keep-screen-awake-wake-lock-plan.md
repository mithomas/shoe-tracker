---
source_ticket: "_bmad-output/initiative-shoe-tracker/tickets/epic-cabin-ux-and-modal-redesign/tickets/story-keep-screen-awake-wake-lock.md"
title: "Keep Smartphone Screen Awake via Screen Wake Lock API"
route: "oneshot"
status: "built"
review_loop_iteration: 1
---

# Implementation Plan - Keep Smartphone Screen Awake via Screen Wake Lock API (Story 8.4)

## Overview
Add a dedicated `/js/wakelock.js` module (`window.ShoeTracker.wakeLockManager`) loaded and initialized in `index.html` that keeps smartphone screens awake while Shoe Tracker is in the foreground via `navigator.wakeLock.request('screen')`, re-acquires the lock when `document.visibilityState === 'visible'` on `visibilitychange`, registers a one-time `pointerdown`/`click` fallback when initial load requires a user gesture, and catches unsupported/rejected wake lock errors silently.

## Tasks
1. **Create `/js/wakelock.js`**:
   - Define `WakeLockManager` attached to `window.ShoeTracker.wakeLockManager`.
   - Implement `init()` to register a `visibilitychange` listener on `document` that calls `this.requestWakeLock()` when `document.visibilityState === 'visible'`, register a one-time `pointerdown`/`click` fallback listener (`registerGestureFallback()`), and invoke `this.requestWakeLock()`.
   - Implement `async requestWakeLock()` to check `navigator.wakeLock && typeof navigator.wakeLock.request === 'function'`, guard against hidden document or already-active sentinel, call `await navigator.wakeLock.request('screen')` inside a `try/catch`, remove the gesture fallback once acquired, attach a `release` listener on the sentinel to clear `this.sentinel`, and silently catch errors.
2. **Update `src/main/resources/META-INF/resources/index.html`**:
   - Include `<script src="/js/wakelock.js"></script>` and initialize `if (wakeLockManager) wakeLockManager.init();` on `DOMContentLoaded`.
3. **Update `src/test/kotlin/org/shoetracker/frontend/FrontendAssetTest.kt`**:
   - Add `testScreenWakeLockModuleServedAndWired()` verifying `/js/wakelock.js` is served (HTTP 200), contains `navigator.wakeLock.request('screen')`, `visibilitychange`, `document.visibilityState === 'visible'`, `pointerdown`, `click`, and `try` / `catch`, and verifying `index.html` loads `/js/wakelock.js` and initializes `wakeLockManager.init()`.
