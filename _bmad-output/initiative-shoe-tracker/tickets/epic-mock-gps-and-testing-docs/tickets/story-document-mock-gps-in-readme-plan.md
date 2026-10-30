---
ticket: '2'
status: 'done'
---

# Plan: Document Mock GPS Simulation Mode in README (Story 9.2)

## Approach
1. Update `README.md`:
   - Add a `### Simulated GPS Mode (Testing without Location Services)` subsection under `## Quickstart` (right above `### Manual Testing with cURL`) documenting:
     - `?mockGps=true` (e.g., `http://localhost:8080/?mockGps=true`): activates the moving highway GPS simulation along the A5 near Frankfurt (`50.110924, 8.682127`) and persists in `localStorage`.
     - `?mockGps=<lat>,<lon>` (e.g., `http://localhost:8080/?mockGps=48.1351,11.5820`): locks simulated GPS onto stationary custom WGS84 coordinates (`-90` to `90` latitude, `-180` to `180` longitude) for the current page session without moving and without persisting to `localStorage`.
     - `?mockGps=false` (e.g., `http://localhost:8080/?mockGps=false`): clears any persisted `localStorage` mock GPS state and resumes live browser `navigator.geolocation` tracking.
   - Remove the completed `"Simulated GPS Testing & Docs"` item from `## Roadmap & Backlog` in `README.md`.
2. Add verification assertions in `src/test/kotlin/org/shoetracker/devops/DevOpsAssetTest.kt` to verify `README.md` documents `?mockGps=true`, `?mockGps=<lat>,<lon>`, and `?mockGps=false`, and no longer lists `"Simulated GPS Testing & Docs"` in `## Roadmap & Backlog`.
