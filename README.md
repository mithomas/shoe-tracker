# 👟 Shoe Tracker

> A lean, playful mobile web application for highway passengers to log lone shoes spotted on the Autobahn with a single tap.

---

## Overview

Traveling at 150 km/h leaves only seconds to spot and record roadside curiosities. Traditional forms and delayed GPS locks record coordinates hundreds of meters past the actual sighting.

**Shoe Tracker** solves this with a purpose-built mobile interface:
- **Zero-Latency GPS:** Actively caches coordinates in the background via `navigator.geolocation.watchPosition` so coordinates are hot the instant you tap.
- **Giant Spot Button:** Oversized, glanceable 210px touch target with a 3-second cooldown ring and confirmation modal to prevent accidental double-taps.
- **Navigational Precision:** Coordinates rendered in standard highway GPS notation (`N XX° MM,MMM'` / `E XXX° MM,MMM'`).
- **Interactive Map & Timeline:** Displays all recorded shoes on an OpenStreetMap / Leaflet map, distinctly highlighting your own shoes with an instant toggle to filter out other users.
- **Single-Container Deployment:** Quarkus backend and modern frontend packaged together in a single container with local SQLite persistence.

---

## Tech Stack

| Layer | Technology | Details |
| :--- | :--- | :--- |
| **Backend** | Kotlin + Quarkus 3.40 | JVM mode, Java 21/25, Gradle Kotlin DSL |
| **API** | Quarkus REST (RESTEasy Reactive) | Versioned REST API at `/api/v1/sightings`, RFC 7807 error format |
| **Database** | SQLite 3 | Direct JDBC via Agroal pool, WAL mode, Flyway migrations |
| **Frontend** | Vanilla TypeScript + Vite | Leaflet.js, OpenStreetMap, mobile-first responsive dark theme |
| **Packaging** | OCI Container / Docker | Multi-stage build serving frontend static assets and backend API |

---

## Quickstart

### Prerequisites
- **JDK 21+** installed (`java -version`)
- Standard build tools (Git, curl)

### Run Locally (Development Mode)

Start Quarkus in live development mode with hot reload:
```bash
./gradlew quarkusDev
```
*Note: If port 8080 is already occupied on your machine, you can specify any alternative port (such as 8085):*
```bash
./gradlew quarkusDev -Dquarkus.http.port=8085
```
Once started, open [http://localhost:8080](http://localhost:8080) (or your chosen port) in your browser.

### Run Packaged Application Locally

Build the standard JVM runnable application and run the jar:
```bash
# Build the application
./gradlew build

# Run the packaged runner jar
java -jar build/quarkus-app/quarkus-run.jar

# Or with a custom port:
java -Dquarkus.http.port=8085 -jar build/quarkus-app/quarkus-run.jar
```

### Run Automated Tests
```bash
./gradlew test
```

### Manual Testing with cURL
```bash
# 1. Record a sighting
curl -i -X POST http://localhost:8080/api/v1/sightings \
  -H "Content-Type: application/json" \
  -d '{
    "user_id": "Tom",
    "location": { "latitude": 50.110924, "longitude": 8.682127 },
    "recorded_at": "2026-10-28T14:32:00.123Z"
  }'

# 2. Retrieve sightings
curl -s http://localhost:8080/api/v1/sightings | jq .
```

---

## API Reference

All endpoints are versioned under `/api/v1`:

### 1. Record a Sighting
`POST /api/v1/sightings`

**Request Payload:**
```json
{
  "user_id": "Tom",
  "location": {
    "latitude": 50.110924,
    "longitude": 8.682127
  },
  "recorded_at": "2026-10-28T14:32:00.123Z"
}
```

**Response (`201 Created`):**
```json
{
  "id": "0192e1b4-2391-7f32-84b2-e6123456789a",
  "user": {
    "id": "Tom"
  },
  "location": {
    "latitude": 50.110924,
    "longitude": 8.682127
  },
  "recorded_at": "2026-10-28T14:32:00.123Z",
  "created_at": "2026-10-28T14:32:00.456Z"
}
```

### 2. Retrieve Sightings
`GET /api/v1/sightings?user_id=Tom&limit=50&offset=0`

**Response (`200 OK`):**
```json
{
  "items": [
    {
      "id": "0192e1b4-2391-7f32-84b2-e6123456789a",
      "user": {
        "id": "Tom"
      },
      "location": {
        "latitude": 50.110924,
        "longitude": 8.682127
      },
      "recorded_at": "2026-10-28T14:32:00.123Z",
      "created_at": "2026-10-28T14:32:00.456Z"
    }
  ],
  "total": 1,
  "limit": 50,
  "offset": 0
}
```

---

## Roadmap & Backlog

- [x] **AGPL-3.0 License:** Add root licensing files and copyleft notices.
- [ ] **CI/CD:** GitHub Actions workflow building and publishing multi-stage container to Docker Hub.
- [ ] **Real-Time Live Map:** Stream newly spotted shoes via Server-Sent Events (SSE).
- [ ] **Dedicated Accounts:** User profile management and optional LDAP/OAuth integration.
- [ ] **Shoe Classification:** Metadata tagging for shoe types (sneakers, boots) and colors.
- [ ] **Offline Queue:** LocalStorage / IndexedDB queue for network dead zones on remote highways.

---

## License

This project is licensed under the [GNU Affero General Public License v3.0 (AGPL-3.0)](LICENSE) - see the [LICENSE](LICENSE) file for details.

As network server software, users interacting with Shoe Tracker remotely through a computer network are entitled to access the corresponding source code under Section 13 of the AGPL-3.0. Complete source code is available in the [Shoe Tracker GitHub Repository](https://github.com/mitch/shoe-tracker) or via the in-app footer link.
