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
      this.sightingsLayer = null;
      this.sightings = [];
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
      if (L.Icon && L.Icon.Default) {
        L.Icon.Default.imagePath = '/vendor/leaflet/images/';
      }
      const tileLayer = L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        crossOrigin: true
      }).addTo(this.map);

      const offlineBanner = document.getElementById('offlineTileBanner');
      tileLayer.on('tileerror', () => {
        if (offlineBanner) offlineBanner.classList.add('visible');
      });
      tileLayer.on('tileload', () => {
        if (offlineBanner) offlineBanner.classList.remove('visible');
      });

      // Layer for nearby spotted shoe dots
      this.sightingsLayer = L.layerGroup().addTo(this.map);

      // Pulsing Live GPS Marker
      const gpsIcon = L.divIcon({
        className: 'gps-dot-container',
        html: '<div class="live-gps-dot"><div class="live-gps-halo"></div></div>',
        iconSize: [24, 24],
        iconAnchor: [12, 12]
      });

      this.marker = L.marker(this.defaultCenter, { icon: gpsIcon, interactive: false, zIndexOffset: 1000 }).addTo(this.map);
      this.isInitialized = true;

      // Listen for sighting events to render nearby shoe dots on HUD radar
      window.addEventListener('sightings-loaded', (e) => {
        this.sightings = Array.isArray(e.detail) ? e.detail.slice() : [];
        this.renderSightingDots();
      });

      window.addEventListener('sighting-added', (e) => {
        if (e.detail && e.detail.id) {
          this.sightings.unshift(e.detail);
          this.renderSightingDots();
        }
      });

      window.addEventListener('sighting-deleted', (e) => {
        if (e.detail) {
          this.sightings = this.sightings.filter(s => s.id !== e.detail);
          this.renderSightingDots();
        }
      });

      window.addEventListener('user-changed', () => {
        this.renderSightingDots();
      });

      // Listen for tab switches to pause/resume rendering
      window.addEventListener('tab-switched', (e) => {
        if (e.detail.tab === 'spot') {
          this.isPaused = false;
          this.invalidateSize(true);
          this.syncCurrentPosition();
        } else {
          this.isPaused = true;
        }
      });

      this.overlayEl = document.getElementById('miniMapNoGpsOverlay');

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
          if (status === 'no-gps') {
            if (this.overlayEl) this.overlayEl.style.display = 'flex';
            this.map.setView(this.defaultCenter, this.defaultZoom);
          } else if (status === 'ready') {
            if (this.overlayEl) this.overlayEl.style.display = 'none';
          }
        });
      }

      // Cold boot size invalidation after initial layout settles
      this.invalidateSize();
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

    renderSightingDots() {
      if (!this.sightingsLayer) return;
      this.sightingsLayer.clearLayers();

      const { onboarding } = window.ShoeTracker;
      const currentUserId = (onboarding && onboarding.getUserId()) ? onboarding.getUserId().trim().toLowerCase() : '';

      this.sightings.forEach((s) => {
        if (!s || !s.location) return;
        const sUserId = (s.user ? s.user.id : (s.user_id || '')).trim().toLowerCase();
        const isOwn = currentUserId && sUserId === currentUserId;
        const dotClass = isOwn ? 'mini-shoe-dot mini-shoe-dot-own' : 'mini-shoe-dot mini-shoe-dot-other';
        const size = isOwn ? 10 : 8;
        const anchor = size / 2;

        const dotIcon = L.divIcon({
          className: 'mini-shoe-dot-container',
          html: `<div class="${dotClass}"></div>`,
          iconSize: [size, size],
          iconAnchor: [anchor, anchor]
        });

        const dotMarker = L.marker([s.location.latitude, s.location.longitude], {
          icon: dotIcon,
          interactive: false
        });
        this.sightingsLayer.addLayer(dotMarker);
      });
    }

    invalidateSize(immediate = false) {
      if (this.map) {
        if (immediate) {
          this.map.invalidateSize();
        }
        setTimeout(() => {
          if (this.map) this.map.invalidateSize();
        }, 50);
        setTimeout(() => {
          if (this.map) this.map.invalidateSize();
        }, 200);
      }
    }
  }

  window.ShoeTracker.miniMap = new HudMiniMapManager();
})();
