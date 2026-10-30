/**
 * Screen Wake Lock Manager (Story 8.4).
 * Prevents smartphone displays from dimming or entering standby while
 * Shoe Tracker is open in the foreground using the Screen Wake Lock API,
 * with automatic re-acquisition on visibilitychange ('visible'), a one-time
 * pointerdown/click user-gesture fallback, and silent fallback when
 * unsupported or rejected (e.g., low-battery mode or insecure context).
 */
(function () {
  window.ShoeTracker = window.ShoeTracker || {};

  class WakeLockManager {
    constructor() {
      this.sentinel = null;
      this.initialized = false;
      this.gestureFallbackHandler = null;
      this.requestInFlight = null;
    }

    init() {
      if (this.initialized) return;
      this.initialized = true;

      if (typeof document !== 'undefined' && typeof document.addEventListener === 'function') {
        document.addEventListener('visibilitychange', () => {
          if (document.visibilityState === 'visible') {
            this.requestWakeLock();
          }
        });
      }

      this.registerGestureFallback();
      this.requestWakeLock();
    }

    registerGestureFallback() {
      if (this.gestureFallbackHandler) return;
      if (typeof document === 'undefined' || typeof document.addEventListener !== 'function') return;

      this.gestureFallbackHandler = () => {
        this.requestWakeLock();
      };

      document.addEventListener('pointerdown', this.gestureFallbackHandler, { passive: true });
      document.addEventListener('click', this.gestureFallbackHandler, { passive: true });
    }

    removeGestureFallback() {
      if (!this.gestureFallbackHandler) return;
      if (typeof document !== 'undefined' && typeof document.removeEventListener === 'function') {
        document.removeEventListener('pointerdown', this.gestureFallbackHandler);
        document.removeEventListener('click', this.gestureFallbackHandler);
      }
      this.gestureFallbackHandler = null;
    }

    async requestWakeLock() {
      if (this.requestInFlight) {
        return this.requestInFlight;
      }

      this.requestInFlight = (async () => {
        try {
          if (
            typeof navigator === 'undefined' ||
            !navigator.wakeLock ||
            typeof navigator.wakeLock.request !== 'function'
          ) {
            return false;
          }

          if (
            typeof document !== 'undefined' &&
            document.visibilityState &&
            document.visibilityState !== 'visible'
          ) {
            return false;
          }

          if (this.sentinel && !this.sentinel.released) {
            this.removeGestureFallback();
            return true;
          }

          const sentinel = await navigator.wakeLock.request('screen');
          this.sentinel = sentinel;
          this.removeGestureFallback();

          if (sentinel && typeof sentinel.addEventListener === 'function') {
            sentinel.addEventListener('release', () => {
              if (this.sentinel === sentinel) {
                this.sentinel = null;
                this.registerGestureFallback();
              }
            });
          }

          return true;
        } catch (e) {
          // Silently ignore unsupported browser, gesture requirement, or OS battery-saver rejection
          if (!this.sentinel || this.sentinel.released) {
            this.registerGestureFallback();
          }
          return false;
        } finally {
          this.requestInFlight = null;
        }
      })();

      return this.requestInFlight;
    }
  }

  window.ShoeTracker.wakeLockManager = new WakeLockManager();
})();
