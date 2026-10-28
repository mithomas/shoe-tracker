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
      this.currentSighting = null;
    }

    init() {
      this.modalEl = document.getElementById('sightingModal');
      this.timeEl = document.getElementById('sightingModalTime');
      this.coordsEl = document.getElementById('sightingModalCoords');
      this.userIdEl = document.getElementById('sightingModalUser');
      this.btnAwesome = document.getElementById('btnSightingAwesome');
      this.btnViewMap = document.getElementById('btnSightingViewMap');
      this.btnUndo = document.getElementById('btnSightingUndo');

      if (this.btnAwesome) {
        this.btnAwesome.addEventListener('click', () => this.hide());
      }
      if (this.btnViewMap) {
        this.btnViewMap.addEventListener('click', () => {
          this.hide();
          const mapTab = document.getElementById('tabMap');
          if (mapTab) mapTab.click();
        });
      }
      if (this.btnUndo) {
        this.btnUndo.addEventListener('click', () => this.handleUndo());
      }
    }

    showSighting(sighting) {
      if (!this.modalEl) return;
      this.currentSighting = sighting;
      const { formatLocationMultiline } = window.ShoeTracker;

      const locHtml = formatLocationMultiline(sighting.location.latitude, sighting.location.longitude);
      const recDate = new Date(sighting.recorded_at);
      const timeStr = recDate.toLocaleTimeString();

      if (this.coordsEl) this.coordsEl.innerHTML = locHtml;
      if (this.timeEl) this.timeEl.textContent = timeStr;
      if (this.userIdEl) this.userIdEl.textContent = sighting.user ? sighting.user.id : (sighting.user_id || 'You');

      this.modalEl.classList.add('active');
    }

    hide() {
      if (!this.modalEl) return;
      this.modalEl.classList.remove('active');
    }

    async handleUndo() {
      if (!this.currentSighting || !this.currentSighting.id) {
        this.hide();
        return;
      }

      const id = this.currentSighting.id;
      this.hide();

      try {
        const res = await fetch(`/api/v1/sightings/${id}`, { method: 'DELETE' });
        const outputEl = document.getElementById('output');
        if (res.ok) {
          window.dispatchEvent(new CustomEvent('sighting-deleted', { detail: id }));
          if (outputEl) {
            outputEl.innerHTML = `<pre style="color:#F59E0B;">↩️ Sighting undone and removed from registry.</pre>`;
          }
        }
      } catch (err) {
        console.error('Error undoing sighting:', err);
      }
    }
  }

  window.ShoeTracker.modal = new SightingModalManager();
})();
