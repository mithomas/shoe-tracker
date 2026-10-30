# 👟 Shoe Tracker

> A lean, playful mobile web application for highway passengers to log lone shoes spotted on the Autobahn with a single tap.

---

## Overview

Traveling at 150 km/h leaves only seconds to spot and record roadside curiosities. Traditional forms and delayed GPS locks record coordinates hundreds of meters past the actual sighting.

**Shoe Tracker** solves this with a purpose-built mobile interface:
- **Zero-Latency GPS:** Actively caches coordinates in the background via `navigator.geolocation.watchPosition` so coordinates are hot the instant you tap.
- **Giant Spot Button:** Oversized, glanceable 210px touch target with an in-flight submission lock and confirmation modal to prevent accidental double-taps.
- **Navigational Precision:** Coordinates rendered in standard highway GPS notation (`N XX° MM,MMM'` / `E XXX° MM,MMM'`).
- **Interactive Map & Timeline:** Displays all recorded shoes on an OpenStreetMap / Leaflet map, distinctly highlighting your own shoes with an instant toggle to filter out other users.
- **Single-Container Deployment:** Quarkus backend and modern frontend packaged together in a single container with embedded H2 (`MODE=MySQL`) file persistence or external MariaDB/MySQL support.

---

## Tech Stack

| Layer | Technology | Details |
| :--- | :--- | :--- |
| **Backend** | Kotlin + Quarkus 3.40 | JVM or GraalVM Native mode, Java 25, Gradle Kotlin DSL |
| **API** | Quarkus REST (RESTEasy Reactive) | Versioned REST API at `/api/v1/sightings`, RFC 7807 error format |
| **Database** | H2 (`MODE=MySQL`) / MariaDB / MySQL | Direct JDBC via Agroal pool with statement caching, Flyway migrations |
| **Frontend** | Vanilla HTML5 / CSS / ES6 JS | Leaflet.js, OpenStreetMap, served via Quarkus root classpath (`META-INF/resources`) |
| **Packaging** | OCI Container / Docker | Multi-stage JVM fast-jar and GraalVM native container builds |

---

## Quickstart

### Prerequisites
- **JDK 25** installed (`java -version`)
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

### Run with Docker Compose

Deploy the published `mithomas/shoe-tracker` container image (`mithomas/shoe-tracker:latest` or `mithomas/shoe-tracker:dev`) locally with persistent data storage:
```bash
# Start with the default mithomas/shoe-tracker:latest image
docker compose up -d

# Or run the latest main branch build (mithomas/shoe-tracker:dev)
DOCKERHUB_IMAGE=mithomas/shoe-tracker:dev docker compose up -d
```
The application will be accessible at [http://localhost:8080](http://localhost:8080) with the embedded H2 database persisted to `./data/shoe-tracker.mv.db`.

### Database Configuration (H2 vs MariaDB/MySQL)

Shoe Tracker bundles both the pure-Java H2 driver (`io.quarkus:quarkus-jdbc-h2`) and the MariaDB/MySQL driver (`io.quarkus:quarkus-jdbc-mariadb`), routing connections automatically at startup based on the single `QUARKUS_DATASOURCE_JDBC_URL` environment variable:

- **Default (Embedded H2 File in MySQL Compatibility Mode):**
  ```bash
  QUARKUS_DATASOURCE_JDBC_URL="jdbc:h2:file:/data/shoe-tracker;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE"
  ```
- **External MariaDB or MySQL Server:**
  ```bash
  QUARKUS_DATASOURCE_JDBC_URL="jdbc:mariadb://mariadb:3306/shoetracker"
  # (or jdbc:mysql://mysql:3306/shoetracker)
  QUARKUS_DATASOURCE_USERNAME="shoetracker"
  QUARKUS_DATASOURCE_PASSWORD="secret"
  ```

### GraalVM Native Image Build

Compile a standalone Linux native binary using a Mandrel container build (achieving sub-50ms startup) and package it with `src/main/docker/Dockerfile.native`:
```bash
# 1. Compile the native runner binary via Mandrel container build
./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true -x test

# 2. Package the native runner into a minimal UBI9 container image (running as non-root UID 185)
docker build -f src/main/docker/Dockerfile.native -t shoe-tracker:native .
```
*Note: Because Quarkus's H2 GraalVM substitution disables embedded `jdbc:h2:file:` mode inside native binaries, point the native binary at an external MariaDB/MySQL server (`QUARKUS_DATASOURCE_JDBC_URL=jdbc:mariadb://...`) or an H2 TCP server (`QUARKUS_DATASOURCE_JDBC_URL=jdbc:h2:tcp://...;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE`).*

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

- **HUD & Map Navigation Controls:** Compact `+`/`-` zoom buttons inside the HUD mini-map and optional manual and automatic GPS bearing map rotation across both maps.
- **Simulated GPS Testing & Docs:** Custom starting coordinate URL parameters for `?mockGps` simulation mode and local testing documentation in the README.
- **Real-Time Live Map:** Stream newly spotted shoes to connected clients via Server-Sent Events (SSE).
- **Dedicated Accounts:** User profile management, changeable display names, and optional LDAP/OAuth authentication.
- **Shoe Classification & Offline Queue:** Metadata tagging for shoe types/colors and local queuing for network dead zones on remote highways.

---

## License

This project is licensed under the [GNU Affero General Public License v3.0 (AGPL-3.0)](LICENSE) - see the [LICENSE](LICENSE) file for details.

As network server software, users interacting with Shoe Tracker remotely through a computer network are entitled to access the corresponding source code under Section 13 of the AGPL-3.0. Complete source code is available in the [Shoe Tracker GitHub Repository](https://github.com/mitch/shoe-tracker) or via the in-app footer link.
