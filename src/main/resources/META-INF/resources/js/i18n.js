/**
 * Internationalization (i18n) Manager for Shoe Tracker (Story 3.4).
 * Supports English ('en') and German ('de') with automatic navigator.language
 * detection, localStorage.lang persistence, data-i18n DOM updates, and
 * lang-changed custom event dispatch.
 */
(function () {
  window.ShoeTracker = window.ShoeTracker || {};

  const STORAGE_KEY = 'lang';

  const TRANSLATIONS = {
    en: {
      'gps.locating': 'Locating...',
      'gps.ready': 'GPS Ready',
      'gps.denied': 'GPS Denied',
      'spot.ready': 'SHOE SPOTTED!',
      'spot.waiting': 'Waiting for GPS...',
      'spot.denied': 'GPS Denied',
      'spot.recent': 'Recent Sightings',
      'spot.refresh': 'Refresh',
      'spot.hudBadge': 'HUD Radar',
      'spot.deniedOverlay': '⚠️ Location Access Denied — Germany Overview',
      'banner.gpsTitle': '⚠️ Location Access Required:',
      'banner.gpsBody':
        'Shoe Tracker requires GPS to record coordinate tags on the Autobahn. Please enable location permissions in your browser or device settings.',
      'banner.networkTitle': '⚠️ Network Error:',
      'banner.networkBody': 'Could not reach server. Check connection.',
      'banner.offlineTitle': '📡 Offline Map Mode:',
      'banner.offlineBody': 'Map tiles could not be loaded. GPS spotting remains active.',
      'filter.all': 'All Shoes',
      'filter.own': 'My Shoes',
      'map.loading': '⏳ Loading highway sightings...',
      'map.empty': '👟 No shoes spotted yet! Tap the Spot button to be the first.',
      'map.emptyOwn': '👟 No shoes spotted by you yet — tap the Spot button to log your first!',
      'map.yourSighting': 'Your Sighting',
      'map.userSighting': "{user}'s Sighting",
      'map.spottedBy': 'Spotted By:',
      'map.time': 'Time:',
      'map.location': 'Location:',
      'map.clusterTitle': '{count} Sightings - click to zoom',
      'list.loading': '⏳ Loading sightings history...',
      'list.error': '⚠️ Could not load sightings. Check connection.',
      'list.emptyTitle': 'No shoes spotted yet!',
      'list.emptyBody': 'No shoes spotted yet! Tap the Spot button to be the first.',
      'list.emptyOwnBody': 'No shoes spotted by you yet — tap the Spot button to log your first!',
      'list.mySighting': 'My Sighting',
      'list.viewOnMap': 'View on Map',
      'nav.spot': 'Spot',
      'nav.map': 'Map',
      'nav.list': 'List',
      'modal.title': '👟 Shoe Spotted!',
      'modal.subtitle': 'Your sighting has been logged to the highway registry.',
      'modal.time': 'Time:',
      'modal.location': 'Location:',
      'modal.spottedBy': 'Spotted By:',
      'modal.awesome': 'Awesome!',
      'modal.viewMap': 'View on Map',
      'modal.undo': 'Undo',
      'onboarding.title': 'Welcome to Shoe Tracker! 👟',
      'onboarding.subtitle': "What's your name? We'll associate it with your shoe sightings on the Autobahn.",
      'onboarding.label': 'Your Name:',
      'onboarding.placeholder': 'e.g. Tom',
      'onboarding.submit': 'Start Spotting',
      'onboarding.errEmpty': 'Please enter your name.',
      'onboarding.errLong': 'Name must be 64 characters or fewer.',
      'counter.total': 'Total',
      'counter.mine': 'Mine',
      'counter.summary': '{total} Total | {mine} Mine',
      'header.userChipTitle': 'Click to view/change user name',
      'theme.switchDark': 'Switch to dark mode',
      'theme.switchLight': 'Switch to light mode'
    },
    de: {
      'gps.locating': 'Suche GPS...',
      'gps.ready': 'GPS Bereit',
      'gps.denied': 'GPS Verweigert',
      'spot.ready': 'SCHUH!',
      'spot.waiting': 'Warte auf GPS...',
      'spot.denied': 'GPS Verweigert',
      'spot.recent': 'Letzte Sichtungen',
      'spot.refresh': 'Aktualisieren',
      'spot.hudBadge': 'HUD-Radar',
      'spot.deniedOverlay': '⚠️ Standortzugriff verweigert — Deutschland-Übersicht',
      'banner.gpsTitle': '⚠️ Standortzugriff erforderlich:',
      'banner.gpsBody':
        'Shoe Tracker benötigt GPS, um Koordinaten auf der Autobahn zu erfassen. Bitte aktiviere die Standortfreigabe in deinem Browser oder Gerät.',
      'banner.networkTitle': '⚠️ Netzwerkfehler:',
      'banner.networkBody': 'Server nicht erreichbar. Bitte Verbindung prüfen.',
      'banner.offlineTitle': '📡 Offline-Kartenmodus:',
      'banner.offlineBody': 'Kartenkacheln konnten nicht geladen werden. GPS-Meldung bleibt aktiv.',
      'filter.all': 'Alle Schuhe',
      'filter.own': 'Meine Schuhe',
      'map.loading': '⏳ Lade Autobahn-Sichtungen...',
      'map.empty': '👟 Noch keine Schuhe gemeldet! Tippe auf den Button für die erste Meldung.',
      'map.emptyOwn': '👟 Du hast noch keine Schuhe gemeldet — tippe auf den Button für deinen ersten!',
      'map.yourSighting': 'Deine Sichtung',
      'map.userSighting': 'Sichtung von {user}',
      'map.spottedBy': 'Gesehen von:',
      'map.time': 'Zeit:',
      'map.location': 'Ort:',
      'map.clusterTitle': '{count} Sichtungen - zum Zoomen klicken',
      'list.loading': '⏳ Lade Sichtungsverlauf...',
      'list.error': '⚠️ Sichtungen konnten nicht geladen werden. Bitte Verbindung prüfen.',
      'list.emptyTitle': 'Noch keine Schuhe gemeldet!',
      'list.emptyBody': 'Noch keine Schuhe gemeldet! Tippe auf den Button für die erste Meldung.',
      'list.emptyOwnBody': 'Du hast noch keine Schuhe gemeldet — tippe auf den Button für deinen ersten!',
      'list.mySighting': 'Meine Sichtung',
      'list.viewOnMap': 'Auf Karte zeigen',
      'nav.spot': 'Melden',
      'nav.map': 'Karte',
      'nav.list': 'Liste',
      'modal.title': '👟 Schuh gesehen!',
      'modal.subtitle': 'Deine Sichtung wurde im Autobahn-Register gespeichert.',
      'modal.time': 'Zeit:',
      'modal.location': 'Ort:',
      'modal.spottedBy': 'Gesehen von:',
      'modal.awesome': 'Super!',
      'modal.viewMap': 'Auf Karte zeigen',
      'modal.undo': 'Rückgängig',
      'onboarding.title': 'Willkommen bei Shoe Tracker! 👟',
      'onboarding.subtitle':
        'Wie heißt du? Wir verknüpfen deinen Namen mit deinen Schuh-Sichtungen auf der Autobahn.',
      'onboarding.label': 'Dein Name:',
      'onboarding.placeholder': 'z.B. Tom',
      'onboarding.submit': "Los geht's",
      'onboarding.errEmpty': 'Bitte gib deinen Namen ein.',
      'onboarding.errLong': 'Der Name darf maximal 64 Zeichen lang sein.',
      'counter.total': 'Gesamt',
      'counter.mine': 'Meine',
      'counter.summary': '{total} Gesamt | {mine} Meine',
      'header.userChipTitle': 'Klicken, um den Benutzernamen zu ändern',
      'theme.switchDark': 'Zum Dunkelmodus wechseln',
      'theme.switchLight': 'Zum Hellmodus wechseln'
    }
  };

  class I18nManager {
    constructor() {
      this.currentLang = 'en';
      this.toggleBtnEl = null;
    }

    init() {
      this.toggleBtnEl = document.getElementById('langToggleBtn');

      const stored = this.getStoredLang();
      if (stored === 'en' || stored === 'de') {
        this.applyLang(stored, false);
      } else {
        const detected = this.detectBrowserLang();
        this.applyLang(detected, false);
      }

      if (this.toggleBtnEl) {
        this.toggleBtnEl.addEventListener('click', () => {
          this.toggleLang();
        });
      }
    }

    getStoredLang() {
      try {
        const val = localStorage.getItem(STORAGE_KEY);
        if (val === 'en' || val === 'de') {
          return val;
        }
      } catch (e) {
        // Ignore storage errors
      }
      return null;
    }

    detectBrowserLang() {
      try {
        const navLang = (navigator.language || '').toLowerCase();
        if (navLang.startsWith('de')) {
          return 'de';
        }
      } catch (e) {
        // Fallback to English
      }
      return 'en';
    }

    getLang() {
      return this.currentLang;
    }

    toggleLang() {
      const next = this.currentLang === 'en' ? 'de' : 'en';
      this.applyLang(next, true);
    }

    applyLang(lang, persist) {
      const resolved = lang === 'de' ? 'de' : 'en';
      this.currentLang = resolved;

      if (document.documentElement) {
        document.documentElement.setAttribute('lang', resolved);
      }

      if (persist) {
        try {
          localStorage.setItem(STORAGE_KEY, resolved);
        } catch (e) {
          // Ignore storage errors
        }
      }

      this.updateToggleButton();
      this.translateDom();

      window.dispatchEvent(
        new CustomEvent('lang-changed', {
          detail: { lang: resolved }
        })
      );
    }

    updateToggleButton() {
      if (!this.toggleBtnEl) return;
      // Display the current active language code or switch label clearly
      const isDe = this.currentLang === 'de';
      this.toggleBtnEl.textContent = isDe ? 'DE' : 'EN';
      const label = isDe ? 'Sprache wechseln (EN)' : 'Switch language (DE)';
      this.toggleBtnEl.setAttribute('aria-label', label);
      this.toggleBtnEl.setAttribute('title', label);
    }

    t(key, params) {
      const dict = TRANSLATIONS[this.currentLang] || TRANSLATIONS.en;
      let str = dict[key] !== undefined ? dict[key] : TRANSLATIONS.en[key] || key;
      if (params) {
        Object.keys(params).forEach((k) => {
          str = str.replace(`{${k}}`, String(params[k]));
        });
      }
      return str;
    }

    translateDom() {
      document.querySelectorAll('[data-i18n]').forEach((el) => {
        const key = el.getAttribute('data-i18n');
        if (key) {
          el.textContent = this.t(key);
        }
      });
      document.querySelectorAll('[data-i18n-placeholder]').forEach((el) => {
        const key = el.getAttribute('data-i18n-placeholder');
        if (key) {
          el.setAttribute('placeholder', this.t(key));
        }
      });
      document.querySelectorAll('[data-i18n-title]').forEach((el) => {
        const key = el.getAttribute('data-i18n-title');
        if (key) {
          el.setAttribute('title', this.t(key));
        }
      });
    }
  }

  window.ShoeTracker.i18n = new I18nManager();
})();
