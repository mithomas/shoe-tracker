---
type: story
title: "Consume Leaflet via mvnpm Maven Dependency for Automated Tracking"
parent: "epic-spotting-polish"
covers: []
sources: []
companions: []
after: []
risk: low
refined: false
---

# Consume Leaflet via mvnpm Maven Dependency for Automated Tracking

## Description

Replaces raw vendored Leaflet assets in `META-INF/resources/vendor/leaflet/` by adding `org.mvnpm:leaflet:1.9.4` to `build.gradle.kts`. This allows GitHub Dependabot to automatically track Leaflet for security vulnerabilities and version updates alongside backend dependencies, while maintaining a pure-JVM build with zero Node.js or npm tooling.

## Requirements

- Add `implementation("org.mvnpm:leaflet:1.9.4")` into `build.gradle.kts`.
- Configure Quarkus or static resource mapping to serve mvnpm Leaflet CSS, JS, and image assets directly from the classpath jar.
- Update `index.html` asset tags to consume Leaflet from the mvnpm endpoint without CDN dependencies.
- Remove raw committed static files in `src/main/resources/META-INF/resources/vendor/leaflet/`.
- Automated test verifies that Leaflet CSS and JS are served with HTTP 200 OK and Dependabot tracks `org.mvnpm:leaflet`.

## References

- parent — _bmad-output/initiative-shoe-tracker/tickets/epic-spotting-polish/epic-spotting-polish.md
