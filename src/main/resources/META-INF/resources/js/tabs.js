/**
 * Tab Navigation Controller & View Manager for Shoe Tracker.
 * Manages bottom navigation tabs ([Spot], [Map], [List]) and lifecycle events.
 */
(function() {
  window.ShoeTracker = window.ShoeTracker || {};

  class TabNavigationManager {
    constructor() {
      this.activeTab = 'spot';
      this.tabButtons = {};
      this.viewPanes = {};
    }

    init() {
      this.tabButtons = {
        spot: document.getElementById('tabSpot'),
        map: document.getElementById('tabMap'),
        list: document.getElementById('tabList')
      };

      this.viewPanes = {
        spot: document.getElementById('viewSpot'),
        map: document.getElementById('viewMap'),
        list: document.getElementById('viewList')
      };

      Object.entries(this.tabButtons).forEach(([tabKey, btn]) => {
        if (btn) {
          btn.addEventListener('click', () => this.switchTab(tabKey));
        }
      });
    }

    switchTab(tabKey) {
      if (!this.viewPanes[tabKey] || this.activeTab === tabKey) return;

      const previousTab = this.activeTab;
      this.activeTab = tabKey;

      // Update button active state
      Object.entries(this.tabButtons).forEach(([key, btn]) => {
        if (btn) {
          if (key === tabKey) {
            btn.classList.add('active');
          } else {
            btn.classList.remove('active');
          }
        }
      });

      // Update view pane visibility
      Object.entries(this.viewPanes).forEach(([key, pane]) => {
        if (pane) {
          if (key === tabKey) {
            pane.classList.add('active');
          } else {
            pane.classList.remove('active');
          }
        }
      });

      // Dispatch global tab-switched event for map lifecycle invalidation
      const event = new CustomEvent('tab-switched', {
        detail: { tab: tabKey, previousTab }
      });
      window.dispatchEvent(event);

      // Trigger map resize invalidation
      if (tabKey === 'spot' && window.ShoeTracker.miniMap) {
        window.ShoeTracker.miniMap.invalidateSize();
      } else if (tabKey === 'map' && window.ShoeTracker.fullMap) {
        window.ShoeTracker.fullMap.invalidateSize();
      }
    }

    getActiveTab() {
      return this.activeTab;
    }
  }

  window.ShoeTracker.tabManager = new TabNavigationManager();
})();
