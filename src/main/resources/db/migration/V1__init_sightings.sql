-- Flyway Migration V1__init_sightings.sql
-- Database: SQLite 3
-- Note: PRAGMAs (journal_mode, busy_timeout) are managed via Agroal DataSource connection initialization

CREATE TABLE IF NOT EXISTS sightings (
    id          TEXT PRIMARY KEY,                                                   -- UUIDv7 time-sortable string identifier
    user_id     TEXT NOT NULL,                                                      -- User identifier (stores entered user name for MVP)
    latitude    REAL NOT NULL,                                                      -- WGS84 latitude coordinate in decimal degrees
    longitude   REAL NOT NULL,                                                      -- WGS84 longitude coordinate in decimal degrees
    recorded_at TEXT NOT NULL,                                                      -- ISO-8601 UTC timestamp of sighting (e.g. 2026-10-28T14:32:00.123Z)
    created_at  TEXT NOT NULL DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now'))       -- ISO-8601 UTC timestamp of database insertion
);

-- Optimize queries for chronological timeline and user filtering
CREATE INDEX IF NOT EXISTS idx_sightings_recorded_at ON sightings(recorded_at DESC);
CREATE INDEX IF NOT EXISTS idx_sightings_user_id ON sightings(user_id);
