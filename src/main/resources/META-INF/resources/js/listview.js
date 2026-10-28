/**
 * Chronological Historical Sightings List Controller for Shoe Tracker (List Tab).
 * Renders descending chronological sighting cards with distinct styling for own shoes.
 */
(function() {
  window.ShoeTracker = window.ShoeTracker || {};

  class ListViewManager {
    constructor() {
      this.containerEl = null;
      this.listEl = null;
      this.counterEl = null;
      this.loadingEl = null;
      this.errorEl = null;
      this.emptyEl = null;
      this.sightings = [];
      this.isLoaded = false;
    }

    init() {
      this.containerEl = document.getElementById('listViewContainer');
      this.listEl = document.getElementById('sightingsList');
      this.counterEl = document.getElementById('listCountBadge');
      this.loadingEl = document.getElementById('listLoadingBanner');
      this.errorEl = document.getElementById('listErrorBanner');
      this.emptyEl = document.getElementById('listEmptyCard');

      const btnRefresh = document.getElementById('btnListRefresh');
      if (btnRefresh) {
        btnRefresh.addEventListener('click', () => this.fetchSightings());
      }

      // Fetch sightings when switching to List tab if not yet loaded
      window.addEventListener('tab-switched', (e) => {
        if (e.detail.tab === 'list') {
          if (!this.isLoaded) {
            this.fetchSightings();
          }
        }
      });

      // Event bus listeners for live optimistic updates
      window.addEventListener('sighting-added', (e) => {
        this.addSighting(e.detail);
      });

      window.addEventListener('sighting-deleted', (e) => {
        this.removeSighting(e.detail);
      });

      window.addEventListener('user-changed', () => {
        this.recolorCards();
      });
    }

    async fetchSightings() {
      if (this.loadingEl) this.loadingEl.style.display = 'block';
      if (this.errorEl) this.errorEl.style.display = 'none';
      if (this.emptyEl) this.emptyEl.style.display = 'none';

      try {
        const res = await fetch('/api/v1/sightings?limit=500');
        if (!res.ok) {
          if (this.errorEl) this.errorEl.style.display = 'block';
          return;
        }
        const data = await res.json();
        this.sightings = data.items || [];
        this.isLoaded = true;
        this.renderList();
      } catch (err) {
        console.error('Failed to fetch sightings for list:', err);
        if (this.errorEl) this.errorEl.style.display = 'block';
      } finally {
        if (this.loadingEl) this.loadingEl.style.display = 'none';
      }
    }

    renderList() {
      if (!this.listEl) return;
      this.listEl.innerHTML = '';

      this.updateCounter();

      if (this.sightings.length === 0) {
        if (this.emptyEl) this.emptyEl.style.display = 'block';
        return;
      }

      if (this.emptyEl) this.emptyEl.style.display = 'none';

      this.sightings.forEach(sighting => {
        const card = this.buildCardElement(sighting);
        this.listEl.appendChild(card);
      });
    }

    buildCardElement(sighting) {
      const { onboarding, formatLocation } = window.ShoeTracker;
      const currentUserId = (onboarding && onboarding.getUserId()) ? onboarding.getUserId().trim().toLowerCase() : '';
      const sightingUserId = (sighting.user ? sighting.user.id : (sighting.user_id || '')).trim().toLowerCase();
      const isOwn = currentUserId && (currentUserId === sightingUserId);

      const card = document.createElement('div');
      card.className = `sighting-card ${isOwn ? 'sighting-card-own' : 'sighting-card-other'}`;
      card.id = `sighting-card-${sighting.id}`;

      const locStr = formatLocation(sighting.location.latitude, sighting.location.longitude);
      const recDate = new Date(sighting.recorded_at);
      const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
      const monthStr = months[recDate.getMonth()];
      const dayStr = recDate.getDate();
      const hh = String(recDate.getHours()).padStart(2, '0');
      const mm = String(recDate.getMinutes()).padStart(2, '0');
      const ss = String(recDate.getSeconds()).padStart(2, '0');
      const formattedTimestamp = `${monthStr} ${dayStr}, ${hh}:${mm}:${ss}`;
      const displayUser = sighting.user ? sighting.user.id : (sighting.user_id || 'Unknown');

      card.innerHTML = `
        <div class="sighting-card-header">
          <div class="card-author">
            <span class="user-avatar">👤</span>
            <span class="user-name">${this.escapeHtml(displayUser)}</span>
            ${isOwn ? '<span class="badge-own">My Sighting</span>' : ''}
          </div>
          <span class="card-timestamp">${formattedTimestamp}</span>
        </div>
        <div class="sighting-card-body">
          <div class="card-location">
            <span class="coords-icon">📍</span>
            <span class="coords-text">${locStr}</span>
          </div>
        </div>
      `;

      return card;
    }

    addSighting(sighting) {
      if (!sighting || !sighting.id) return;
      this.sightings.unshift(sighting);
      this.updateCounter();

      if (this.emptyEl) this.emptyEl.style.display = 'none';

      if (this.isLoaded && this.listEl) {
        const card = this.buildCardElement(sighting);
        card.classList.add('card-new-anim');
        this.listEl.insertBefore(card, this.listEl.firstChild);
      }
    }

    removeSighting(id) {
      if (!id) return;
      this.sightings = this.sightings.filter(s => s.id !== id);
      this.updateCounter();

      const card = document.getElementById(`sighting-card-${id}`);
      if (card) {
        card.classList.add('card-remove-anim');
        setTimeout(() => {
          if (card.parentNode) card.parentNode.removeChild(card);
          if (this.sightings.length === 0 && this.emptyEl) {
            this.emptyEl.style.display = 'block';
          }
        }, 250);
      }
    }

    recolorCards() {
      if (this.isLoaded) {
        this.renderList();
      }
    }

    updateCounter() {
      if (this.counterEl) {
        const total = this.sightings.length;
        this.counterEl.textContent = `${total} ${total === 1 ? 'sighting' : 'sightings'}`;
      }
    }

    escapeHtml(str) {
      return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
    }
  }

  window.ShoeTracker.listView = new ListViewManager();
})();
