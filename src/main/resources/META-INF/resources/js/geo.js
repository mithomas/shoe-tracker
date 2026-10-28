/**
 * Continuous Geolocation Watcher for Shoe Tracker.
 * Maintains hot coordinates in background and emits telemetry updates every 5 seconds.
 * Supports ?mockGps=true or manual simulation toggle for development/testing environments.
 */
(function() {
  window.ShoeTracker = window.ShoeTracker || {};

  class GeolocationWatcher {
    constructor() {
      this.watchId = null;
      this.state = 'locating'; // 'locating' | 'ready' | 'denied'
      this.latestPosition = null;
      this.statusListeners = [];
      this.telemetryListeners = [];
      this.intervalTimer = null;
      this.isMock = false;
      this.mockLat = 50.110924;
      this.mockLon = 8.682127;
    }

    start() {
      // Check for URL parameter ?mockGps=true or localStorage flag
      const params = new URLSearchParams(window.location.search);
      if (params.get('mockGps') === 'true' || localStorage.getItem('mockGps') === 'true') {
        this.enableMockMode();
        return;
      }

      if (!('geolocation' in navigator)) {
        this.setState('denied', 'Geolocation API not supported in this browser.');
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

    enableMockMode() {
      this.isMock = true;
      localStorage.setItem('mockGps', 'true');
      this.stop();

      this.latestPosition = {
        latitude: this.mockLat,
        longitude: this.mockLon,
        accuracy: 4.5,
        timestamp: new Date().toISOString()
      };

      this.setState('ready', 'Simulated GPS Active');
      this.emitTelemetry();

      // Simulate highway movement every 5 seconds (moving south along A5)
      this.intervalTimer = setInterval(() => {
        // Move ~100m south each 5s tick
        this.mockLat -= 0.0009;
        this.mockLon += 0.0001;
        this.latestPosition = {
          latitude: this.mockLat,
          longitude: this.mockLon,
          accuracy: 4.2,
          timestamp: new Date().toISOString()
        };
        this.emitTelemetry();
      }, 5000);
    }

    disableMockMode() {
      this.isMock = false;
      localStorage.removeItem('mockGps');
      this.stop();
      this.latestPosition = null;
      this.start();
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
        this.setState('denied', 'Location permission denied or blocked by OS policy.');
      } else {
        this.setState('locating', 'Searching for satellites...');
      }
    }

    setState(newState, message = '') {
      this.state = newState;
      this.statusListeners.forEach((fn) => fn(newState, message, this.isMock));
    }

    emitTelemetry() {
      if (this.state === 'denied') return;
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
