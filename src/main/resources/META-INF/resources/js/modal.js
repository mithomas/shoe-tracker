/**
 * Sighting Confirmation Modal Controller for Shoe Tracker.
 * Displays post-tap confirmation with captured coordinates, local timestamp, and Undo action.
 */
(function() {
  window.ShoeTracker = window.ShoeTracker || {};

  class SightingModalManager {
    constructor() {
      this.modalEl = null;
      this.timeEl = null;
      this.coordsEl = null;
      this.userIdEl = null;
      this.btnAwesome = null;
      this.btnViewMap = null;
      this.btnUndo = null;
      this.ringProgressEl = null;
      this.undoErrorEl = null;
      this.currentSighting = null;
      this.autoCloseDurationMs = 20000;
      this.autoCloseTimer = null;
      this.circumference = 2 * Math.PI * 108;
    }

    init() {
      this.modalEl = document.getElementById('sightingModal');
      this.timeEl = document.getElementById('sightingModalTime');
      this.coordsEl = document.getElementById('sightingModalCoords');
      this.userIdEl = document.getElementById('sightingModalUser');
      this.btnAwesome = document.getElementById('btnSightingAwesome');
      this.btnViewMap = document.getElementById('btnSightingViewMap');
      this.btnUndo = document.getElementById('btnSightingUndo');
      this.ringProgressEl = document.getElementById('modalAutoCloseRingProgress');
      this.undoErrorEl = document.getElementById('sightingModalUndoError');

      if (this.ringProgressEl) {
        this.ringProgressEl.style.strokeDasharray = `${this.circumference} ${this.circumference}`;
        this.ringProgressEl.style.strokeDashoffset = '0';
      }

      if (this.btnAwesome) {
        this.btnAwesome.addEventListener('click', () => this.hide());
      }
      if (this.btnViewMap) {
        this.btnViewMap.addEventListener('click', () => {
          const target = this.currentSighting;
          this.hide();
          const { fullMap } = window.ShoeTracker;
          if (fullMap && typeof fullMap.focusSighting === 'function' && target) {
            fullMap.focusSighting(target);
          } else {
            const mapTab = document.getElementById('tabMap');
            if (mapTab) mapTab.click();
          }
        });
      }
      if (this.btnUndo) {
        this.btnUndo.addEventListener('click', () => this.handleUndo());
      }
    }

    startAutoCloseTimer() {
      this.clearAutoCloseTimer();

      if (this.ringProgressEl) {
        this.ringProgressEl.style.transition = 'none';
        this.ringProgressEl.style.strokeDashoffset = '0';
        void this.ringProgressEl.getBoundingClientRect();
        this.ringProgressEl.style.transition = `stroke-dashoffset ${this.autoCloseDurationMs}ms linear`;
        this.ringProgressEl.style.strokeDashoffset = `-${this.circumference}`;
      }

      this.autoCloseTimer = setTimeout(() => {
        this.autoCloseTimer = null;
        this.hide();
      }, this.autoCloseDurationMs);
    }

    clearAutoCloseTimer() {
      if (this.autoCloseTimer !== null) {
        clearTimeout(this.autoCloseTimer);
        this.autoCloseTimer = null;
      }
      if (this.ringProgressEl) {
        this.ringProgressEl.style.transition = 'none';
        this.ringProgressEl.style.strokeDashoffset = '0';
      }
    }

    clearUndoError() {
      if (this.undoErrorEl) {
        this.undoErrorEl.textContent = '';
        this.undoErrorEl.style.display = 'none';
      }
    }

    showUndoError() {
      const { i18n, spotManager } = window.ShoeTracker;
      if (this.undoErrorEl) {
        this.undoErrorEl.textContent = i18n
          ? i18n.t('banner.networkBody')
          : 'Could not reach server. Check connection.';
        this.undoErrorEl.style.display = 'block';
      }
      if (spotManager && typeof spotManager.showNetworkAlert === 'function') {
        spotManager.showNetworkAlert();
      }
    }

    showSighting(sighting) {
      if (!this.modalEl) return;
      this.currentSighting = sighting;
      this.clearUndoError();
      const { formatLocationMultiline } = window.ShoeTracker;

      const locHtml = formatLocationMultiline(sighting.location.latitude, sighting.location.longitude);
      const recDate = new Date(sighting.recorded_at);
      const timeStr = recDate.toLocaleTimeString();

      if (this.coordsEl) this.coordsEl.innerHTML = locHtml;
      if (this.timeEl) this.timeEl.textContent = timeStr;
      if (this.userIdEl) this.userIdEl.textContent = sighting.user ? sighting.user.id : (sighting.user_id || 'You');

      this.modalEl.classList.add('active');
      this.startAutoCloseTimer();
    }

    hide() {
      if (!this.modalEl) return;
      this.clearAutoCloseTimer();
      this.clearUndoError();
      this.modalEl.classList.remove('active');
    }

    async handleUndo() {
      this.clearAutoCloseTimer();

      if (!this.currentSighting || !this.currentSighting.id) {
        this.hide();
        return;
      }

      const id = this.currentSighting.id;

      try {
        const res = await fetch(`/api/v1/sightings/${id}`, { method: 'DELETE' });
        if (res.ok) {
          this.hide();
          window.dispatchEvent(new CustomEvent('sighting-deleted', { detail: id }));
        } else {
          this.showUndoError();
        }
      } catch (err) {
        console.error('Error undoing sighting:', err);
        this.showUndoError();
      }
    }
  }

  window.ShoeTracker.modal = new SightingModalManager();
})();
