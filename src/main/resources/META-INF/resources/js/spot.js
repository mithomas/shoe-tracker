/**
 * High-Velocity Spot Button Controller for Shoe Tracker.
 * Manages the 210px button, 3-second mandatory cooldown, and tap submission.
 */
(function() {
  window.ShoeTracker = window.ShoeTracker || {};

  class SpotButtonManager {
    constructor() {
      this.buttonEl = null;
      this.labelEl = null;
      this.cooldownRingEl = null;
      this.networkAlertEl = null;
      this.isCooldown = false;
      this.gpsStatus = 'locating';
      this.cooldownSeconds = 3;
      this.cooldownTimer = null;
      this.circumference = 2 * Math.PI * 105; // radius = 105px for 210px button
    }

    init() {
      this.buttonEl = document.getElementById('btnSpotGiant');
      this.labelEl = document.getElementById('btnSpotLabel');
      this.cooldownRingEl = document.getElementById('cooldownRingProgress');
      this.networkAlertEl = document.getElementById('networkAlertBanner');

      if (this.cooldownRingEl) {
        this.cooldownRingEl.style.strokeDasharray = `${this.circumference} ${this.circumference}`;
        this.cooldownRingEl.style.strokeDashoffset = '0';
      }

      if (this.buttonEl) {
        this.buttonEl.addEventListener('click', (e) => {
          e.preventDefault();
          this.handleTap();
        });
      }
    }

    setGpsStatus(status) {
      this.gpsStatus = status;
      if (this.isCooldown) return;

      if (!this.buttonEl || !this.labelEl) return;

      this.buttonEl.classList.remove('waiting-gps', 'no-gps');

      if (status === 'ready') {
        this.buttonEl.disabled = false;
        this.labelEl.textContent = 'SHOE SPOTTED!';
      } else if (status === 'locating') {
        this.buttonEl.disabled = true;
        this.buttonEl.classList.add('waiting-gps');
        this.labelEl.textContent = 'Waiting for GPS...';
      } else {
        this.buttonEl.disabled = true;
        this.buttonEl.classList.add('no-gps');
        this.labelEl.textContent = 'GPS Denied';
      }
    }

    async handleTap() {
      // 1. Mandatory Cooldown Lock: Strictly ignore repeated taps
      if (this.isCooldown || this.gpsStatus !== 'ready') {
        return;
      }

      const { onboarding, geoWatcher, modal, formatLocation } = window.ShoeTracker;
      const outputEl = document.getElementById('output');

      // 2. Identity Check
      const userId = onboarding ? onboarding.getUserId() : null;
      if (!userId) {
        if (onboarding) onboarding.show('');
        return;
      }

      // 3. Hot Coordinates Check
      const hot = geoWatcher ? geoWatcher.getHotCoordinates() : null;
      if (!hot) {
        if (outputEl) {
          outputEl.innerHTML = `<pre style="color:#F59E0B;">⚠️ Waiting for GPS satellite lock before recording.</pre>`;
        }
        return;
      }

      // 4. Start 3-Second Mandatory Cooldown immediately
      this.startCooldown();

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

        const data = await res.json();
        if (res.ok) {
          // Open Confirmation Modal
          if (modal) {
            modal.showSighting(data);
          }
          if (outputEl) {
            const locStr = formatLocation(data.location.latitude, data.location.longitude);
            outputEl.innerHTML = `<pre>POST 201 Created:\nLocation: ${locStr}\n\n${JSON.stringify(data, null, 2)}</pre>`;
          }
        } else {
          if (outputEl) {
            outputEl.innerHTML = `<pre style="color:#EF4444;">HTTP ${res.status} ${res.statusText}:\n${JSON.stringify(data, null, 2)}</pre>`;
          }
        }
      } catch (err) {
        this.showNetworkAlert();
        if (outputEl) {
          outputEl.innerHTML = `<pre style="color:#EF4444;">Could not reach server. Check connection.</pre>`;
        }
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

    startCooldown() {
      this.isCooldown = true;
      let remaining = this.cooldownSeconds;

      if (this.buttonEl) {
        this.buttonEl.classList.add('cooldown');
        this.buttonEl.disabled = true;
      }
      if (this.labelEl) {
        this.labelEl.textContent = `Logged! ${remaining}s`;
      }
      if (this.cooldownRingEl) {
        this.cooldownRingEl.style.transition = 'none';
        this.cooldownRingEl.style.strokeDashoffset = '0';
        void this.cooldownRingEl.offsetWidth;
        this.cooldownRingEl.style.transition = `stroke-dashoffset ${this.cooldownSeconds}s linear`;
        this.cooldownRingEl.style.strokeDashoffset = `${this.circumference}`;
      }

      this.cooldownTimer = setInterval(() => {
        remaining -= 1;
        if (remaining > 0) {
          if (this.labelEl) this.labelEl.textContent = `Logged! ${remaining}s`;
        } else {
          this.releaseCooldown();
        }
      }, 1000);
    }

    releaseCooldown() {
      if (this.cooldownTimer !== null) {
        clearInterval(this.cooldownTimer);
        this.cooldownTimer = null;
      }
      this.isCooldown = false;

      if (this.buttonEl) {
        this.buttonEl.classList.remove('cooldown');
      }
      this.setGpsStatus(this.gpsStatus);

      if (this.cooldownRingEl) {
        this.cooldownRingEl.style.transition = 'none';
        this.cooldownRingEl.style.strokeDashoffset = '0';
      }
    }
  }

  window.ShoeTracker.spotManager = new SpotButtonManager();
})();
