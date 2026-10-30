/**
 * Continuous Geolocation Watcher for Shoe Tracker.
 * Maintains hot coordinates in background and emits telemetry updates every 5 seconds.
 * Supports ?mockGps=true, ?mockGps=<lat>,<lon>, ?mockGps=false, or manual simulation toggle for development/testing environments.
 */
(function() {
  window.ShoeTracker = window.ShoeTracker || {};

  const DEFAULT_MOCK_LAT = 50.110924;
  const DEFAULT_MOCK_LON = 8.682127;

  class GeolocationWatcher {
    constructor() {
      this.watchId = null;
      this.state = 'locating'; // 'locating' | 'ready' | 'no-gps'
      this.latestPosition = null;
      this.statusListeners = [];
      this.telemetryListeners = [];
      this.intervalTimer = null;
      this.isMock = false;
      this.isStationaryMock = false;
      this.mockLat = DEFAULT_MOCK_LAT;
      this.mockLon = DEFAULT_MOCK_LON;
    }

    isValidCoordinatePair(lat, lon) {
      return (
        typeof lat === 'number' &&
        typeof lon === 'number' &&
        Number.isFinite(lat) &&
        Number.isFinite(lon) &&
        lat >= -90 &&
        lat <= 90 &&
        lon >= -180 &&
        lon <= 180
      );
    }

    parseMockCoordinates(raw) {
      if (typeof raw !== 'string') return null;
      const parts = raw.split(',');
      if (parts.length !== 2) return null;
      const latStr = parts[0].trim();
      const lonStr = parts[1].trim();
      if (latStr === '' || lonStr === '') return null;
      const lat = Number(latStr);
      const lon = Number(lonStr);
      if (!this.isValidCoordinatePair(lat, lon)) return null;
      return { latitude: lat, longitude: lon };
    }

    start(skipUrlCheck = false) {
      // Check for URL parameter ?mockGps=true, ?mockGps=<lat>,<lon>, ?mockGps=false, or localStorage flag
      if (!skipUrlCheck) {
        const params = new URLSearchParams(window.location.search);
        const mockParam = params.get('mockGps');
        if (mockParam !== null) {
          const normalizedParam = mockParam.trim().toLowerCase();
          if (normalizedParam === 'false') {
            this.isMock = false;
            this.isStationaryMock = false;
            localStorage.removeItem('mockGps');
            this.stop();
            this.latestPosition = null;
          } else {
            const customCoords = this.parseMockCoordinates(mockParam);
            if (customCoords) {
              this.enableMockMode(customCoords.latitude, customCoords.longitude);
            } else {
              this.enableMockMode();
            }
            return;
          }
        } else if (localStorage.getItem('mockGps') === 'true') {
          this.enableMockMode();
          return;
        }
      } else if (localStorage.getItem('mockGps') === 'true') {
        this.enableMockMode();
        return;
      }

      if (!('geolocation' in navigator)) {
        this.setState('no-gps', 'Geolocation API not supported in this browser.');
        return;
      }

      this.setState('locating', 'Searching for satellites...');

      const options = {
        enableHighAccuracy: true,
        maximumAge: 5000,
        timeout: 10000
      };

      try {
        this.watchId = navigator.geolocation.watchPosition(
          (pos) => this.handleSuccess(pos),
          (err) => this.handleError(err),
          options
        );
      } catch (e) {
        this.handleError({ code: 1, message: e.message });
      }

      // Dedicated 5-second telemetry interval
      if (this.intervalTimer === null) {
        this.intervalTimer = setInterval(() => {
          if (this.state === 'ready' && this.latestPosition) {
            this.emitTelemetry();
          }
        }, 5000);
      }
    }

    enableMockMode(lat, lon) {
      this.stop();
      this.isMock = true;

      if (this.isValidCoordinatePair(lat, lon)) {
        this.isStationaryMock = true;
        this.mockLat = lat;
        this.mockLon = lon;
      } else {
        this.isStationaryMock = false;
        this.mockLat = DEFAULT_MOCK_LAT;
        this.mockLon = DEFAULT_MOCK_LON;
        localStorage.setItem('mockGps', 'true');
      }

      this.latestPosition = {
        latitude: this.mockLat,
        longitude: this.mockLon,
        accuracy: 4.5,
        timestamp: new Date().toISOString()
      };

      this.setState('ready', 'Simulated GPS Active');
      this.emitTelemetry();

      // Emit telemetry every 5 seconds; move south along A5 only when not in stationary custom-coordinate mode
      this.intervalTimer = setInterval(() => {
        if (!this.isStationaryMock) {
          // Move ~100m south each 5s tick
          this.mockLat -= 0.0009;
          this.mockLon += 0.0001;
        }
        this.latestPosition = {
          latitude: this.mockLat,
          longitude: this.mockLon,
          accuracy: this.isStationaryMock ? 4.5 : 4.2,
          timestamp: new Date().toISOString()
        };
        this.emitTelemetry();
      }, 5000);
    }

    disableMockMode() {
      this.isMock = false;
      this.isStationaryMock = false;
      localStorage.removeItem('mockGps');
      this.stop();
      this.latestPosition = null;
      this.start(true);
    }

    stop() {
      if (this.watchId !== null) {
        navigator.geolocation.clearWatch(this.watchId);
        this.watchId = null;
      }
      if (this.intervalTimer !== null) {
        clearInterval(this.intervalTimer);
        this.intervalTimer = null;
      }
    }

    handleSuccess(position) {
      if (this.isMock) return;
      const isFirstFix = (this.latestPosition === null);

      this.latestPosition = {
        latitude: position.coords.latitude,
        longitude: position.coords.longitude,
        accuracy: position.coords.accuracy,
        timestamp: new Date(position.timestamp).toISOString()
      };

      if (this.state !== 'ready') {
        this.setState('ready');
      }

      if (isFirstFix) {
        this.emitTelemetry();
      }
    }

    handleError(error) {
      if (this.isMock) return;
      if (error && error.code === 1) { // PERMISSION_DENIED
        this.latestPosition = null;
        this.stop();
        this.setState('no-gps', 'Location permission denied or blocked by OS policy.');
      } else {
        this.setState('locating', 'Searching for satellites...');
      }
    }

    setState(newState, message = '') {
      this.state = newState;
      this.statusListeners.forEach((fn) => fn(newState, message, this.isMock));
    }

    emitTelemetry() {
      if (this.state === 'no-gps') return;
      this.telemetryListeners.forEach((fn) => fn(this.latestPosition, this.state, this.isMock));
    }

    onStatusChange(fn) {
      this.statusListeners.push(fn);
      if (this.state) fn(this.state, '', this.isMock);
    }

    onTelemetryUpdate(fn) {
      this.telemetryListeners.push(fn);
      if (this.latestPosition && this.state === 'ready') {
        fn(this.latestPosition, this.state, this.isMock);
      }
    }

    getHotCoordinates() {
      if (this.state !== 'ready' || !this.latestPosition) return null;
      return {
        latitude: this.latestPosition.latitude,
        longitude: this.latestPosition.longitude,
        recorded_at: new Date().toISOString()
      };
    }
  }

  window.ShoeTracker.geoWatcher = new GeolocationWatcher();
})();
