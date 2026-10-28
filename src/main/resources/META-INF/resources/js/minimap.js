/**
 * HUD Tactical Mini-Map Controller for Shoe Tracker (Spot Screen).
 * Operates in locked HUD mode (touch panning disabled, auto-centered at zoom 15).
 */
(function() {
  window.ShoeTracker = window.ShoeTracker || {};

  class HudMiniMapManager {
    constructor() {
      this.map = null;
      this.marker = null;
      this.containerEl = null;
      this.isInitialized = false;
      this.isPaused = false;
      this.defaultCenter = [50.8, 9.7]; // Central Germany Autobahn hub
      this.defaultZoom = 6;
      this.activeZoom = 15;
    }

    init() {
      this.containerEl = document.getElementById('spotMiniMap');
      if (!this.containerEl || typeof L === 'undefined') return;

      // Invariant C6: Locked HUD radar mode (all manual navigation disabled)
      this.map = L.map(this.containerEl, {
        dragging: false,
        touchZoom: false,
        doubleClickZoom: false,
        scrollWheelZoom: false,
        boxZoom: false,
        keyboard: false,
        zoomControl: false,
        attributionControl: false
      }).setView(this.defaultCenter, this.defaultZoom);

      // OpenStreetMap Tiles
      L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        crossOrigin: true
      }).addTo(this.map);

      // Pulsing Live GPS Marker
      const gpsIcon = L.divIcon({
        className: 'gps-dot-container',
        html: '<div class="live-gps-dot"><div class="live-gps-halo"></div></div>',
        iconSize: [24, 24],
        iconAnchor: [12, 12]
      });

      this.marker = L.marker(this.defaultCenter, { icon: gpsIcon, interactive: false }).addTo(this.map);
      this.isInitialized = true;

      // Listen for tab switches to pause/resume rendering
      window.addEventListener('tab-switched', (e) => {
        if (e.detail.tab === 'spot') {
          this.isPaused = false;
          this.invalidateSize();
          this.syncCurrentPosition();
        } else {
          this.isPaused = true;
        }
      });

      this.overlayEl = document.getElementById('miniMapDeniedOverlay');

      // Hook into Geolocation Watcher
      const { geoWatcher } = window.ShoeTracker;
      if (geoWatcher) {
        geoWatcher.onTelemetryUpdate((pos, status) => {
          if (this.isPaused || !this.isInitialized) return;
          if (status === 'ready' && pos) {
            if (this.overlayEl) this.overlayEl.style.display = 'none';
            this.updatePosition(pos.latitude, pos.longitude);
          }
        });

        geoWatcher.onStatusChange((status) => {
          if (!this.isInitialized) return;
          if (status === 'denied') {
            if (this.overlayEl) this.overlayEl.style.display = 'flex';
            this.map.setView(this.defaultCenter, this.defaultZoom);
          } else if (status === 'ready') {
            if (this.overlayEl) this.overlayEl.style.display = 'none';
          }
        });
      }
    }

    updatePosition(lat, lon) {
      if (!this.map || !this.marker) return;

      const latLng = [lat, lon];
      this.marker.setLatLng(latLng);

      // Smoothly pan map to new position at active highway tracking zoom (15)
      if (this.map.getZoom() !== this.activeZoom) {
        this.map.setView(latLng, this.activeZoom);
      } else {
        this.map.panTo(latLng, { animate: true, duration: 0.5 });
      }
    }

    syncCurrentPosition() {
      const { geoWatcher } = window.ShoeTracker;
      if (geoWatcher) {
        const hot = geoWatcher.getHotCoordinates();
        if (hot) {
          this.updatePosition(hot.latitude, hot.longitude);
        }
      }
    }

    invalidateSize() {
      if (this.map) {
        setTimeout(() => {
          this.map.invalidateSize();
        }, 50);
      }
    }
  }

  window.ShoeTracker.miniMap = new HudMiniMapManager();
})();
