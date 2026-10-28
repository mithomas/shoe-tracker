/**
 * Full Interactive Map Controller for Shoe Tracker (Map Tab).
 * Lazy-initializes Leaflet, renders Signal Blue vs Slate markers, and displays popups.
 */
(function() {
  window.ShoeTracker = window.ShoeTracker || {};

  class FullMapManager {
    constructor() {
      this.map = null;
      this.markersLayer = null;
      this.containerEl = null;
      this.isInitialized = false;
      this.sightings = [];
      this.markersById = {};
      this.defaultCenter = [50.8, 9.7]; // Central Germany Autobahn hub
      this.defaultZoom = 6;
    }

    init() {
      this.containerEl = document.getElementById('fullMap');

      // Lazy-initialize when switching to the Map tab
      window.addEventListener('tab-switched', (e) => {
        if (e.detail.tab === 'map') {
          if (!this.isInitialized) {
            this.bootstrapMap();
          } else {
            this.invalidateSize();
          }
        }
      });

      // Event Bus listeners for real-time optimistic updates
      window.addEventListener('sighting-added', (e) => {
        this.addSighting(e.detail);
      });

      window.addEventListener('sighting-deleted', (e) => {
        this.removeSighting(e.detail);
      });

      window.addEventListener('user-changed', () => {
        this.recolorMarkers();
      });
    }

    bootstrapMap() {
      if (!this.containerEl || typeof L === 'undefined') return;

      this.map = L.map(this.containerEl, {
        zoomControl: true,
        attributionControl: false
      }).setView(this.defaultCenter, this.defaultZoom);

      // OpenStreetMap Tiles
      L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        crossOrigin: true
      }).addTo(this.map);

      this.markersLayer = L.layerGroup().addTo(this.map);
      this.isInitialized = true;

      // Invalidate size after layout completes
      this.invalidateSize();

      // Load sightings from REST API
      this.fetchSightings();
    }

    async fetchSightings() {
      const loadingEl = document.getElementById('fullMapLoading');
      const emptyEl = document.getElementById('fullMapEmpty');

      if (loadingEl) loadingEl.style.display = 'block';
      if (emptyEl) emptyEl.style.display = 'none';

      try {
        const res = await fetch('/api/v1/sightings?limit=500');
        if (!res.ok) return;
        const data = await res.json();
        this.sightings = data.items || [];
        this.renderAllMarkers();

        if (this.sightings.length === 0) {
          if (emptyEl) emptyEl.style.display = 'block';
        } else {
          if (emptyEl) emptyEl.style.display = 'none';
          const latLngs = this.sightings.map(s => [s.location.latitude, s.location.longitude]);
          const bounds = L.latLngBounds(latLngs);
          if (bounds.isValid()) {
            this.map.fitBounds(bounds, { padding: [40, 40], maxZoom: 14 });
          }
        }
      } catch (err) {
        console.error('Failed to fetch sightings for map:', err);
      } finally {
        if (loadingEl) loadingEl.style.display = 'none';
      }
    }

    renderAllMarkers() {
      if (!this.markersLayer) return;
      this.markersLayer.clearLayers();
      this.markersById = {};

      this.sightings.forEach(sighting => {
        this.createMarker(sighting);
      });
    }

    createMarker(sighting) {
      if (!this.markersLayer || !sighting || !sighting.location) return;

      const { onboarding, formatLocation } = window.ShoeTracker;
      const currentUserId = (onboarding && onboarding.getUserId()) ? onboarding.getUserId().trim().toLowerCase() : '';
      const sightingUserId = (sighting.user ? sighting.user.id : (sighting.user_id || '')).trim().toLowerCase();
      const isOwn = currentUserId && (currentUserId === sightingUserId);

      // Sizes per DESIGN.md: My Shoe = 32px, Other Shoe = 26px
      const markerClass = isOwn ? 'shoe-pin-own' : 'shoe-pin-other';
      const size = isOwn ? 32 : 26;
      const anchor = size / 2;
      const iconDim = isOwn ? 18 : 14;

      const shoeSvg = `
        <svg width="${iconDim}" height="${iconDim}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" style="color:#FFFFFF;">
          <path d="M3 13h2a2 2 0 0 1 2 2v2a2 2 0 0 0 2 2h8a4 4 0 0 0 4-4v-4a2 2 0 0 0-2-2h-3a2 2 0 0 0-2 2v1"/>
          <line x1="3" y1="19" x2="21" y2="19"/>
        </svg>
      `;

      const pinIcon = L.divIcon({
        className: 'shoe-marker-container',
        html: `<div class="shoe-pin ${markerClass}" title="${isOwn ? 'Your Sighting' : 'Sighting'}">${shoeSvg}</div>`,
        iconSize: [size, size],
        iconAnchor: [anchor, anchor],
        popupAnchor: [0, -anchor - 4]
      });

      const marker = L.marker([sighting.location.latitude, sighting.location.longitude], {
        icon: pinIcon
      });

      // Bind detail popup with UX formatted timestamp (e.g. Oct 28, 14:32)
      const locStr = formatLocation(sighting.location.latitude, sighting.location.longitude);
      const recDate = new Date(sighting.recorded_at);
      const datePart = recDate.toLocaleDateString([], { month: 'short', day: 'numeric' });
      const timePart = recDate.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: false });
      const formattedTimestamp = `${datePart}, ${timePart}`;
      const displayUser = sighting.user ? sighting.user.id : (sighting.user_id || 'Unknown');
      const popupTitle = isOwn ? 'Your Sighting' : `${this.escapeHtml(displayUser)}'s Sighting`;

      const popupHtml = `
        <div class="sighting-popup-card">
          <div class="popup-header">
            <span class="popup-title">👟 ${popupTitle}</span>
            ${isOwn ? '<span class="badge-own">Your Sighting</span>' : ''}
          </div>
          <div class="popup-body">
            <div class="popup-row">
              <span class="popup-label">Spotted By:</span>
              <span class="popup-val">${this.escapeHtml(displayUser)}</span>
            </div>
            <div class="popup-row">
              <span class="popup-label">Time:</span>
              <span class="popup-val">${formattedTimestamp}</span>
            </div>
            <div class="popup-row">
              <span class="popup-label">Location:</span>
              <span class="popup-val coords">${locStr}</span>
            </div>
          </div>
        </div>
      `;

      marker.bindPopup(popupHtml, { className: 'sighting-leaflet-popup' });
      this.markersLayer.addLayer(marker);
      this.markersById[sighting.id] = marker;
    }

    addSighting(sighting) {
      if (!sighting || !sighting.id) return;
      this.sightings.unshift(sighting);
      if (this.isInitialized) {
        this.createMarker(sighting);
      }
    }

    removeSighting(id) {
      if (!id) return;
      this.sightings = this.sightings.filter(s => s.id !== id);
      const marker = this.markersById[id];
      if (marker && this.markersLayer) {
        this.markersLayer.removeLayer(marker);
        delete this.markersById[id];
      }
    }

    recolorMarkers() {
      if (this.isInitialized) {
        this.renderAllMarkers();
      }
    }

    invalidateSize() {
      if (this.map) {
        setTimeout(() => {
          this.map.invalidateSize();
        }, 50);
      }
    }

    escapeHtml(str) {
      return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
    }
  }

  window.ShoeTracker.fullMap = new FullMapManager();
})();
