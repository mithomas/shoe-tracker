/**
 * Theme Switcher Manager (Story 3.3).
 * Auto-detects OS color scheme via prefers-color-scheme, allows manual override
 * persisted in localStorage.theme ('light' | 'dark'), and applies data-theme
 * on document.documentElement.
 */
(function () {
  window.ShoeTracker = window.ShoeTracker || {};

  const STORAGE_KEY = 'theme';

  class ThemeManager {
    constructor() {
      this.currentTheme = 'dark';
      this.toggleBtnEl = null;
      this.mediaQuery = null;
    }

    init() {
      this.toggleBtnEl = document.getElementById('themeToggleBtn');

      const stored = this.getStoredTheme();
      if (stored === 'light' || stored === 'dark') {
        this.applyTheme(stored, false);
      } else {
        const prefersDark = this.detectOsDarkPreference();
        this.applyTheme(prefersDark ? 'dark' : 'light', false);
      }

      if (window.matchMedia) {
        this.mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
        const onMediaChange = (e) => {
          // Only follow OS changes when user has not set a manual override
          if (!this.getStoredTheme()) {
            this.applyTheme(e.matches ? 'dark' : 'light', false);
          }
        };
        if (typeof this.mediaQuery.addEventListener === 'function') {
          this.mediaQuery.addEventListener('change', onMediaChange);
        }
      }

      if (this.toggleBtnEl) {
        this.toggleBtnEl.addEventListener('click', () => {
          this.toggleTheme();
        });
      }

      window.addEventListener('lang-changed', () => {
        this.updateToggleButton();
      });
    }

    getStoredTheme() {
      try {
        const val = localStorage.getItem(STORAGE_KEY);
        if (val === 'light' || val === 'dark') {
          return val;
        }
      } catch (e) {
        // Ignore storage errors
      }
      return null;
    }

    detectOsDarkPreference() {
      try {
        if (window.matchMedia) {
          return window.matchMedia('(prefers-color-scheme: dark)').matches;
        }
      } catch (e) {
        // Fallback to dark
      }
      return true;
    }

    getTheme() {
      return this.currentTheme;
    }

    toggleTheme() {
      const next = this.currentTheme === 'dark' ? 'light' : 'dark';
      this.applyTheme(next, true);
    }

    applyTheme(theme, persist) {
      const resolved = theme === 'light' ? 'light' : 'dark';
      this.currentTheme = resolved;

      if (document.documentElement) {
        document.documentElement.setAttribute('data-theme', resolved);
      }

      if (persist) {
        try {
          localStorage.setItem(STORAGE_KEY, resolved);
        } catch (e) {
          // Ignore storage errors
        }
      }

      this.updateToggleButton();

      window.dispatchEvent(
        new CustomEvent('theme-changed', {
          detail: { theme: resolved }
        })
      );
    }

    updateToggleButton() {
      if (!this.toggleBtnEl) return;
      const isLight = this.currentTheme === 'light';
      this.toggleBtnEl.textContent = isLight ? '🌙' : '☀️';
      const { i18n } = window.ShoeTracker;
      const label = isLight
        ? (i18n ? i18n.t('theme.switchDark') : 'Switch to dark mode')
        : (i18n ? i18n.t('theme.switchLight') : 'Switch to light mode');
      this.toggleBtnEl.setAttribute('aria-label', label);
      this.toggleBtnEl.setAttribute('title', label);
    }
  }

  window.ShoeTracker.themeManager = new ThemeManager();
})();
