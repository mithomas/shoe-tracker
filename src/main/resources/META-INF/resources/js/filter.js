/**
 * Unified Filter Controller for Shoe Tracker.
 * Manages the "All Shoes" / "My Shoes" toggle state and synchronizes across views.
 */
(function() {
  window.ShoeTracker = window.ShoeTracker || {};

  class FilterManager {
    constructor() {
      this.ownOnly = localStorage.getItem('filter_own_only') === 'true';
      this.pills = [];
    }

    init() {
      // Find all filter toggles (rendered in both Map and List views)
      this.pills = document.querySelectorAll('.filter-pill-container');

      this.pills.forEach(pill => {
        const btnAll = pill.querySelector('[data-filter="all"]');
        const btnOwn = pill.querySelector('[data-filter="own"]');

        if (btnAll) btnAll.addEventListener('click', () => this.setFilter(false));
        if (btnOwn) btnOwn.addEventListener('click', () => this.setFilter(true));
      });

      this.applyDOMState();

      // Listen for user changes to re-evaluate active filters
      window.addEventListener('user-changed', () => {
        if (this.ownOnly) {
          this.dispatchChange();
        }
      });
    }

    setFilter(ownOnly) {
      const normalized = ownOnly === true || ownOnly === 'own';
      if (this.ownOnly === normalized) return;
      this.ownOnly = normalized;
      localStorage.setItem('filter_own_only', this.ownOnly ? 'true' : 'false');

      this.applyDOMState();
      this.dispatchChange();
    }

    applyDOMState() {
      this.pills.forEach(pill => {
        const btnAll = pill.querySelector('[data-filter="all"]');
        const btnOwn = pill.querySelector('[data-filter="own"]');
        const bgSlide = pill.querySelector('.filter-pill-bg');

        if (this.ownOnly) {
          if (btnAll) btnAll.classList.remove('active');
          if (btnOwn) btnOwn.classList.add('active');
          if (bgSlide) bgSlide.style.transform = 'translateX(100%)';
        } else {
          if (btnAll) btnAll.classList.add('active');
          if (btnOwn) btnOwn.classList.remove('active');
          if (bgSlide) bgSlide.style.transform = 'translateX(0)';
        }
      });
    }

    dispatchChange() {
      window.dispatchEvent(new CustomEvent('filter-changed', {
        detail: { ownOnly: this.ownOnly }
      }));
    }

    isOwnOnly() {
      return this.ownOnly;
    }
  }

  window.ShoeTracker.filterManager = new FilterManager();
})();
