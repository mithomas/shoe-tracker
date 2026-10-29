/**
 * High-Velocity Spot Button Controller for Shoe Tracker.
 * Manages the 210px button, in-flight submission lock, and tap submission.
 */
(function() {
  window.ShoeTracker = window.ShoeTracker || {};

  class SpotButtonManager {
    constructor() {
      this.buttonEl = null;
      this.labelEl = null;
      this.networkAlertEl = null;
      this.isSubmitting = false;
      this.gpsStatus = 'locating';
    }

    init() {
      this.buttonEl = document.getElementById('btnSpotGiant');
      this.labelEl = document.getElementById('btnSpotLabel');
      this.networkAlertEl = document.getElementById('networkAlertBanner');

      if (this.buttonEl) {
        this.buttonEl.addEventListener('click', (e) => {
          e.preventDefault();
          this.handleTap();
        });
      }

      window.addEventListener('lang-changed', () => {
        this.setGpsStatus(this.gpsStatus);
      });
    }

    setGpsStatus(status) {
      this.gpsStatus = status;
      if (this.isSubmitting) return;

      if (!this.buttonEl || !this.labelEl) return;

      const { i18n } = window.ShoeTracker;
      this.buttonEl.classList.remove('waiting-gps', 'no-gps');

      if (status === 'ready') {
        this.buttonEl.disabled = false;
        this.labelEl.textContent = i18n ? i18n.t('spot.ready') : 'SHOE SPOTTED!';
      } else if (status === 'locating') {
        this.buttonEl.disabled = true;
        this.buttonEl.classList.add('waiting-gps');
        this.labelEl.textContent = i18n ? i18n.t('spot.waiting') : 'Waiting for GPS...';
      } else {
        this.buttonEl.disabled = true;
        this.buttonEl.classList.add('no-gps');
        this.labelEl.textContent = i18n ? i18n.t('spot.denied') : 'GPS Denied';
      }
    }

    async handleTap() {
      // 1. In-Flight Submission Lock: Strictly ignore duplicate taps while POST is pending
      if (this.isSubmitting || this.gpsStatus !== 'ready') {
        return;
      }

      const { onboarding, geoWatcher, modal } = window.ShoeTracker;

      // 2. Identity Check
      const userId = onboarding ? onboarding.getUserId() : null;
      if (!userId) {
        if (onboarding) onboarding.show('');
        return;
      }

      // 3. Hot Coordinates Check
      const hot = geoWatcher ? geoWatcher.getHotCoordinates() : null;
      if (!hot) {
        return;
      }

      // 4. Engage in-flight lock during network submission
      this.isSubmitting = true;
      if (this.buttonEl) {
        this.buttonEl.disabled = true;
      }

      // 5. Trigger Haptic Feedback
      if ('vibrate' in navigator) {
        try { navigator.vibrate(50); } catch (e) { /* ignore */ }
      }

      // 6. Submit Sighting to REST API
      try {
        const res = await fetch('/api/v1/sightings', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            user_id: userId,
            location: { latitude: hot.latitude, longitude: hot.longitude },
            recorded_at: hot.recorded_at
          })
        });

        if (res.ok) {
          const data = await res.json();
          // Open Confirmation Modal
          if (modal) {
            modal.showSighting(data);
          }
          // Dispatch global sighting-added event for Map, List, and Counter views
          window.dispatchEvent(new CustomEvent('sighting-added', { detail: data }));
        } else {
          this.showNetworkAlert();
        }
      } catch (err) {
        this.showNetworkAlert();
      } finally {
        this.isSubmitting = false;
        this.setGpsStatus(this.gpsStatus);
      }
    }

    showNetworkAlert() {
      if (this.networkAlertEl) {
        this.networkAlertEl.classList.add('visible');
        setTimeout(() => {
          this.networkAlertEl.classList.remove('visible');
        }, 4000);
      }
    }
  }

  window.ShoeTracker.spotManager = new SpotButtonManager();
})();
