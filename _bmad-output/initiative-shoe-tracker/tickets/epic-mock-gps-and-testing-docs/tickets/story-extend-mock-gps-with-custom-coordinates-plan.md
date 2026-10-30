---
ticket: '1'
status: 'done'
---

# Plan: Extend Mock GPS Mode with Optional Custom Coordinate Parameters (Story 9.1)

## Approach
1. Update `GeolocationWatcher` in `src/main/resources/META-INF/resources/js/geo.js`:
   - Define `DEFAULT_MOCK_LAT = 50.110924` and `DEFAULT_MOCK_LON = 8.682127` constants and track `this.isStationaryMock = false`.
   - Add `isValidCoordinatePair(lat, lon)` to validate finite numbers within WGS84 bounds (`lat` in `[-90, 90]`, `lon` in `[-180, 180]`, including `0`).
   - Add `parseMockCoordinates(raw)` to split `<lat>,<lon>`, reject empty/whitespace tokens or non-2-part strings, parse numbers, and validate via `isValidCoordinatePair`.
   - Update `start(skipUrlCheck = false)` to inspect `params.get('mockGps')`:
     - If `'false'` (case-insensitive, trimmed), clear `localStorage.removeItem('mockGps')`, reset `isMock` and `isStationaryMock`, stop any mock timer, clear `latestPosition`, and continue to standard `navigator.geolocation.watchPosition`.
     - If valid `<lat>,<lon>`, invoke `enableMockMode(lat, lon)` and return.
     - If any other non-null `mockGps` parameter (such as `'true'` or invalid coordinate values), invoke `enableMockMode()` (default moving Frankfurt A5 simulation) and return.
     - If `mockGps` query parameter is absent and `localStorage.getItem('mockGps') === 'true'`, invoke `enableMockMode()` and return.
   - Update `enableMockMode(lat, lon)` to accept optional custom coordinates:
     - When `isValidCoordinatePair(lat, lon)` is true, set `isStationaryMock = true`, lock `mockLat = lat` and `mockLon = lon`, do not persist to `localStorage`, and keep coordinates stationary across 5-second telemetry ticks.
     - Otherwise, set `isStationaryMock = false`, reset `mockLat = DEFAULT_MOCK_LAT` and `mockLon = DEFAULT_MOCK_LON`, persist `localStorage.setItem('mockGps', 'true')`, and apply simulated highway movement every 5 seconds.
2. Add verification test `testMockGpsCustomCoordinatesAndDisableParameter()` in `src/test/kotlin/org/shoetracker/frontend/FrontendAssetTest.kt` and execute a Node.js behavior test inside the test or verify all contracts.
