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
      this.pendingFocusId = null;
      this.isFetching = false;
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
            this.invalidateSize(true);
          }
        }
      });

      // Event Bus listeners for real-time optimistic updates
      window.addEventListener('sightings-loaded', (e) => {
        this.sightings = Array.isArray(e.detail) ? e.detail.slice() : [];
        if (this.isInitialized) {
          this.renderAllMarkers();
        }
      });

      window.addEventListener('sighting-added', (e) => {
        this.addSighting(e.detail);
      });

      window.addEventListener('sighting-deleted', (e) => {
        this.removeSighting(e.detail);
      });

      window.addEventListener('user-changed', () => {
        this.recolorMarkers();
      });

      window.addEventListener('filter-changed', (e) => {
        this.ownOnly = e.detail.ownOnly;
        if (this.isInitialized) {
          this.renderAllMarkers();
        }
      });

      window.addEventListener('lang-changed', () => {
        if (this.isInitialized) {
          this.renderAllMarkers();
        }
      });
    }

    bootstrapMap() {
      if (!this.containerEl || typeof L === 'undefined') return;

      this.map = L.map(this.containerEl, {
        zoomControl: true,
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

      this.markersLayer = L.layerGroup().addTo(this.map);
      this.isInitialized = true;

      this.map.on('zoomend', () => {
        this.renderAllMarkers();
      });

      // Invalidate size after layout completes
      this.invalidateSize();

      if (this.sightings.length > 0) {
        this.renderAllMarkers();
      }

      // Load sightings from REST API
      this.fetchSightings();
    }

    async fetchSightings() {
      const loadingEl = document.getElementById('fullMapLoading');

      if (loadingEl) loadingEl.style.display = 'block';

      this.isFetching = true;
      try {
        const res = await fetch('/api/v1/sightings?limit=500');
        if (!res.ok) return;
        const data = await res.json();
        this.sightings = data.items || [];
        this.renderAllMarkers();

        if (this.pendingFocusId) {
          const targetId = this.pendingFocusId;
          this.pendingFocusId = null;
          this.isFetching = false;
          this.focusSighting(targetId);
        } else if (this.sightings.length > 0) {
          const latLngs = this.sightings.map(s => [s.location.latitude, s.location.longitude]);
          const bounds = L.latLngBounds(latLngs);
          if (bounds.isValid()) {
            this.map.fitBounds(bounds, { padding: [40, 40], maxZoom: 14 });
          }
        }
      } catch (err) {
        console.error('Failed to fetch sightings for map:', err);
      } finally {
        this.isFetching = false;
        if (loadingEl) loadingEl.style.display = 'none';
      }
    }

    renderAllMarkers() {
      if (!this.markersLayer) return;
      this.markersLayer.clearLayers();
      this.markersById = {};

      const { onboarding, filterManager } = window.ShoeTracker;
      const ownOnly = filterManager ? filterManager.isOwnOnly() : (this.ownOnly || false);
      const currentUserId = (onboarding && onboarding.getUserId()) ? onboarding.getUserId().trim().toLowerCase() : '';

      const filtered = this.sightings.filter(s => {
        if (!ownOnly) return true;
        const sUserId = (s.user ? s.user.id : (s.user_id || '')).trim().toLowerCase();
        return currentUserId && sUserId === currentUserId;
      });

      const currentZoom = this.map ? this.map.getZoom() : this.defaultZoom;
      const clusters = this.clusterSightings(filtered, currentZoom, currentUserId);

      clusters.forEach((cluster) => {
        if (cluster.items.length === 1) {
          this.createMarker(cluster.items[0]);
        } else {
          this.createClusterMarker(cluster);
        }
      });
    }

    isOwnSighting(sighting, currentUserId) {
      if (!currentUserId || !sighting) return false;
      const sUserId = (sighting.user ? sighting.user.id : (sighting.user_id || '')).trim().toLowerCase();
      return sUserId === currentUserId;
    }

    clusterSightings(sightings, zoom, currentUserId) {
      const valid = sightings.filter((s) => s && s.location);
      if (zoom >= 15) {
        return valid.map((s) => ({
          items: [s],
          lat: s.location.latitude,
          lon: s.location.longitude,
          hasOwn: this.isOwnSighting(s, currentUserId)
        }));
      }

      // Zoom-adaptive spatial threshold in degrees (collapses nearby sightings at zoom < 15)
      const cellSize = 180 / Math.pow(2, zoom);
      const clusters = [];

      valid.forEach((sighting) => {
        const lat = sighting.location.latitude;
        const lon = sighting.location.longitude;
        const isOwn = this.isOwnSighting(sighting, currentUserId);

        let matched = null;
        for (let i = 0; i < clusters.length; i++) {
          const c = clusters[i];
          if (Math.abs(c.lat - lat) <= cellSize && Math.abs(c.lon - lon) <= cellSize) {
            matched = c;
            break;
          }
        }

        if (matched) {
          const count = matched.items.length;
          matched.lat = (matched.lat * count + lat) / (count + 1);
          matched.lon = (matched.lon * count + lon) / (count + 1);
          matched.items.push(sighting);
          if (isOwn) {
            matched.hasOwn = true;
          }
        } else {
          clusters.push({
            items: [sighting],
            lat: lat,
            lon: lon,
            hasOwn: isOwn
          });
        }
      });

      return clusters;
    }

    createClusterMarker(cluster) {
      if (!this.markersLayer || !cluster || !cluster.items || cluster.items.length < 2) return;

      const count = cluster.items.length;
      const isOwn = cluster.hasOwn;
      const badgeClass = isOwn ? 'shoe-cluster-own' : 'shoe-cluster-other';
      const size = count >= 10 ? 42 : 36;
      const anchor = size / 2;
      const { i18n } = window.ShoeTracker;
      const titleStr = i18n
        ? i18n.t('map.clusterTitle', { count })
        : `${count} Sightings - click to zoom`;

      const clusterIcon = L.divIcon({
        className: 'shoe-cluster-container',
        html: `<div class="shoe-cluster-badge ${badgeClass}" title="${this.escapeHtml(titleStr)}"><span>${count}</span></div>`,
        iconSize: [size, size],
        iconAnchor: [anchor, anchor]
      });

      const marker = L.marker([cluster.lat, cluster.lon], {
        icon: clusterIcon
      });

      marker.on('click', () => {
        if (!this.map) return;
        const latLngs = cluster.items.map((s) => [s.location.latitude, s.location.longitude]);
        const clusterBounds = L.latLngBounds(latLngs);
        if (!clusterBounds.isValid()) return;

        const latSpan = Math.abs(clusterBounds.getNorth() - clusterBounds.getSouth());
        const lngSpan = Math.abs(clusterBounds.getEast() - clusterBounds.getWest());
        const currentZoom = this.map.getZoom();
        const targetZoom = typeof this.map.getBoundsZoom === 'function'
          ? this.map.getBoundsZoom(clusterBounds, false, L.point(80, 80))
          : currentZoom + 2;

        if ((latSpan < 0.0005 && lngSpan < 0.0005) || targetZoom <= currentZoom) {
          this.map.setView(clusterBounds.getCenter(), Math.max(currentZoom + 2, 15));
        } else {
          this.map.fitBounds(clusterBounds, { padding: [40, 40], maxZoom: 15 });
        }
      });

      this.markersLayer.addLayer(marker);
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
      const { i18n } = window.ShoeTracker;
      const locStr = formatLocation(sighting.location.latitude, sighting.location.longitude);
      const recDate = new Date(sighting.recorded_at);
      const datePart = recDate.toLocaleDateString([], { month: 'short', day: 'numeric' });
      const timePart = recDate.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: false });
      const formattedTimestamp = `${datePart}, ${timePart}`;
      const displayUser = sighting.user ? sighting.user.id : (sighting.user_id || 'Unknown');
      const yourSightingStr = i18n ? i18n.t('map.yourSighting') : 'Your Sighting';
      const userSightingStr = i18n
        ? i18n.t('map.userSighting', { user: this.escapeHtml(displayUser) })
        : `${this.escapeHtml(displayUser)}'s Sighting`;
      const popupTitle = isOwn ? yourSightingStr : userSightingStr;
      const spottedByLabel = i18n ? i18n.t('map.spottedBy') : 'Spotted By:';
      const timeLabel = i18n ? i18n.t('map.time') : 'Time:';
      const locLabel = i18n ? i18n.t('map.location') : 'Location:';

      const popupHtml = `
        <div class="sighting-popup-card">
          <div class="popup-header">
            <span class="popup-title">👟 ${popupTitle}</span>
            ${isOwn ? `<span class="badge-own">${yourSightingStr}</span>` : ''}
          </div>
          <div class="popup-body">
            <div class="popup-row">
              <span class="popup-label">${spottedByLabel}</span>
              <span class="popup-val">${this.escapeHtml(displayUser)}</span>
            </div>
            <div class="popup-row">
              <span class="popup-label">${timeLabel}</span>
              <span class="popup-val">${formattedTimestamp}</span>
            </div>
            <div class="popup-row">
              <span class="popup-label">${locLabel}</span>
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
        this.renderAllMarkers();
      }
    }

    removeSighting(id) {
      if (!id) return;
      this.sightings = this.sightings.filter(s => s.id !== id);
      if (this.isInitialized) {
        this.renderAllMarkers();
      }
    }

    recolorMarkers() {
      if (this.isInitialized) {
        this.renderAllMarkers();
      }
    }

    /**
     * Deterministically switches to the Map tab, bootstraps the map if needed,
     * resets the 'My Shoes' filter if targeting another user's sighting,
     * zooms to 16 so unclustered pins populate markersById, and opens the target popup.
     */
    focusSighting(sightingOrId) {
      if (!sightingOrId) return;

      const { tabManager, filterManager, onboarding, listView } = window.ShoeTracker;
      let target = null;

      if (typeof sightingOrId === 'object' && sightingOrId.id) {
        target = sightingOrId;
        if (!this.sightings.some((s) => s.id === target.id)) {
          this.sightings.unshift(target);
        }
      } else {
        const idStr = String(sightingOrId);
        target = this.sightings.find((s) => String(s.id) === idStr) || null;
        if (!target && listView && Array.isArray(listView.sightings)) {
          target = listView.sightings.find((s) => String(s.id) === idStr) || null;
          if (target) {
            this.sightings.unshift(target);
          }
        }
      }

      const targetId = target ? target.id : sightingOrId;
      this.pendingFocusId = targetId;

      if (tabManager) {
        tabManager.switchTab('map');
      }

      if (!this.isInitialized) {
        this.bootstrapMap();
      }

      this.invalidateSize(true);

      if (!target || !target.location || !this.map) return;

      const currentUserId = (onboarding && onboarding.getUserId())
        ? onboarding.getUserId().trim().toLowerCase()
        : '';
      const isOwn = this.isOwnSighting(target, currentUserId);

      if (filterManager && filterManager.isOwnOnly() && !isOwn) {
        filterManager.setFilter('all');
      }

      this.map.setView([target.location.latitude, target.location.longitude], 16);
      this.renderAllMarkers();

      const marker = this.markersById[target.id];
      if (marker) {
        marker.openPopup();
        if (!this.isFetching) {
          this.pendingFocusId = null;
        }
      }
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

    escapeHtml(str) {
      return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
    }
  }

  window.ShoeTracker.fullMap = new FullMapManager();
})();
