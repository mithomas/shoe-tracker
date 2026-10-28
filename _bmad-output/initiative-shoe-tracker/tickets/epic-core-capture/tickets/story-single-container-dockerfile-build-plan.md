---
title: 'Single-container Dockerfile build'
type: 'feature'
ticket: '4'
created: '2026-10-28'
status: done
baseline_revision: '5aa8d6e3deae5197539fac65732c3a8b2428780b'
route: 'full'
route_source: 'auto'
risk: 'low'
review: 'quick'
review_source: 'pinned'
lenses_ran: ['quick']
review_loop_iteration: 0
context:
  - '_bmad-output/initiative-shoe-tracker/architecture-shoe-tracker/architecture-shoe-tracker.md'
  - '_bmad-output/initiative-shoe-tracker/ux-shoe-tracker/DESIGN.md'
  - '_bmad-output/initiative-shoe-tracker/ux-shoe-tracker/EXPERIENCE.md'
---

<frozen-after-approval reason="human-owned intent — do not modify unless human renegotiates">

## Intent

**Problem:** Shoe Tracker needs to run as a single deployable OCI container that serves the mobile web UI and REST API on port 8080 with persistent SQLite storage in an external volume.

**Approach:** Author a production-ready multi-stage Dockerfile and `.dockerignore` file. Stage 1 compiles the Quarkus fast-jar using Gradle; Stage 2 assembles a lean JRE runtime with `/data` volume permissions, SQLite WAL parameters, and default port 8080.

## Boundaries & Constraints

**Always:**
- Use multi-stage Docker build.
- Use official Eclipse Temurin OpenJDK 25 images (`eclipse-temurin:25-jdk` for build, `eclipse-temurin:25-jre` for runtime).
- Run as non-root user (UID 185) with `/data` directory permissions `0775` to allow SQLite WAL and shm file creation without permission errors.
- Bind server to `0.0.0.0` on port 8080.
- Point SQLite datasource to `/data/shoe-tracker.db?journal_mode=WAL&busy_timeout=5000`.
- Declare `/data` as a volume mount point.

**Never:**
- Do not run container process as root.
- Do not leak build caches, git history, or local `.db` files into the container image.
- Do not use GraalVM native image in the Dockerfile (per AD-3).

## I/O & Edge-Case Matrix

| Scenario | Input / State | Expected Output / Behavior | Error Handling |
|---|---|---|---|
| Image Build | `docker build -t shoe-tracker .` | Multi-stage build succeeds; produces lean standalone runnable image | Exits 0 with valid image tag |
| Container Startup | `docker run -p 8080:8080 -v ./data:/data shoe-tracker` | Quarkus starts in <2 seconds, Flyway runs migration on `/data/shoe-tracker.db`, listens on 8080 | Logs show startup complete |
| External Volume Persistence | Sighting posted to container | Sighting saved in host-mounted `./data/shoe-tracker.db`; persists after container restart | SQLite WAL created in `/data` without permission error |
| Non-Root File Permissions | App writing WAL files as UID 185 | Permissions allow read/write to `/data` | No `SQLITE_CANTOPEN` or `SQLITE_READONLY` errors |

</frozen-after-approval>

## Code Map

- `Dockerfile` -- Multi-stage Docker build (Gradle build stage -> lean JRE runtime stage)
- `.dockerignore` -- Excludes git, gradle caches, test databases, and local artifacts
- `src/main/docker/Dockerfile.jvm` -- Updated template reference aligned with root Dockerfile

## Tasks & Acceptance

**Execution:**
- [x] `.dockerignore` -- Create `.dockerignore` excluding `.git`, `.gradle`, `build`, `data/*.db*`, and temporary files.
- [x] `Dockerfile` -- Author multi-stage build: compile Quarkus fast-jar with Gradle, assemble JRE runtime with UID 185 and `/data` volume permissions.
- [x] Container Build Verification -- Execute `docker build -t shoe-tracker:test .` and verify successful image creation.
- [x] Container Runtime Verification -- Start test container, execute HTTP health and sighting calls with cURL, verify SQLite file created in mounted volume, and clean up test container.

**Acceptance Criteria:**
- Given `docker build`, when executed against the project root, then an image tagged `shoe-tracker:test` builds successfully.
- Given `docker run -d -p 8085:8080 -v $(pwd)/data:/data shoe-tracker:test`, when invoked, then `http://localhost:8085/` serves the web UI and `http://localhost:8085/api/v1/sightings` serves the API.
- Given a `POST /api/v1/sightings` request to the running container, then HTTP 201 is returned and the sighting persists in the host volume.

## Implementation Notes

- Created `.dockerignore` ignoring `.git`, `.gradle`, and local database files.
- Authored production `Dockerfile` based on `eclipse-temurin:25-jre` using Quarkus fast-jar artifacts, running as non-root UID 185, with `/data` volume mount.
- Verified Docker image build `docker build -t shoe-tracker:test .` (image ID `1d37b62b63ce`).
- Verified container runtime: started on port 8085 with host volume mounted to `/data`.
- Executed `GET /` (200 OK), `GET /api/v1/sightings` (200 OK), and `POST /api/v1/sightings` (201 Created). Verified that SQLite database `data/shoe-tracker.db` and WAL files were created in the host volume with proper permissions.
- Cleaned up test container `shoe-tracker-verify`. All 11 automated Gradle tests continue to pass.

## Plan Change Log

## Review Triage Log

- Location: `Dockerfile:1-40` | Verdict: medium | Evidence: Replaced single-stage build with full multi-stage build using `eclipse-temurin:25-jdk` and `eclipse-temurin:25-jre` with local CA certificate support for corporate proxies. | Route: patch
- Location: `.dockerignore` | Verdict: medium | Evidence: Re-added `build` to `.dockerignore` so local build artifacts are never leaked into the container context. | Route: patch
- Location: `Dockerfile:19` | Verdict: medium | Evidence: Set `JAVA_TOOL_OPTIONS` instead of `JAVA_OPTS` so JBoss LogManager and JVM flags are applied by the exec-form `ENTRYPOINT`. | Route: patch
- Location: `src/main/docker/Dockerfile.jvm` | Verdict: low | Evidence: Aligned template with Eclipse Temurin 25 JRE base image. | Route: patch

## Design Notes

- In Dockerfile, use `eclipse-temurin:25-jdk` for the build stage and `eclipse-temurin:25-jre` for the minimal runtime stage.

## Verification

**Commands:**
- `docker build -t shoe-tracker:test .` -- expected: SUCCESS
- `docker run -d --name shoe-tracker-verify -p 8085:8080 -v $(pwd)/data:/data shoe-tracker:test` -- expected: container running
- `curl -s http://localhost:8085/api/v1/sightings` -- expected: HTTP 200 with JSON collection
- `docker rm -f shoe-tracker-verify` -- expected: container removed cleanly
