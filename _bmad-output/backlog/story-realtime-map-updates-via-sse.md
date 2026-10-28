---
type: story
title: "Real-Time Map Updates via Server-Sent Events (SSE)"
parent: ""
covers: []
sources: []
companions: []
after: []
risk: low
refined: false
---

# Real-Time Map Updates via Server-Sent Events (SSE)

## Description

Broadcasts newly spotted shoes in real time to connected web clients using in-process event-driven architecture and Server-Sent Events (SSE).

## Background & Architecture Comment

During the architecture review for the MVP, the team evaluated Event-Driven Architecture (EDA):
1. **MVP Simplicity:** Full external message brokers (Kafka/RabbitMQ) are rejected to preserve single-container simplicity and zero-latency SQLite persistence. MVP uses standard REST retrieval (`GET /api/v1/sightings`).
2. **Implementation Path:** When implementing this backlog item:
   - Use in-process Quarkus CDI Events (`Event<SightingRecordedEvent>`) or Vert.x EventBus.
   - When a sighting is saved via `POST /api/v1/sightings`, fire an in-memory event.
   - Expose an SSE stream endpoint (e.g. `GET /api/v1/sightings/stream` producing `text/event-stream`).
   - The frontend connects via `EventSource` and drops new markers onto the Leaflet map live without requiring manual page reloads or polling.

## Boundaries

- Sighting write latency must not be blocked by SSE broadcast failures.
- Single-container deployment must remain intact (no external message broker).
