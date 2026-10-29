-- Flyway Migration V1__init_sightings.sql
-- Database: H2 (MODE=MySQL) / MariaDB / MySQL

CREATE TABLE IF NOT EXISTS sightings (
    id          VARCHAR(36) PRIMARY KEY,                                            -- UUIDv7 time-sortable string identifier
    user_id     VARCHAR(255) NOT NULL,                                              -- User identifier (stores entered user name for MVP)
    latitude    DOUBLE NOT NULL,                                                    -- WGS84 latitude coordinate in decimal degrees
    longitude   DOUBLE NOT NULL,                                                    -- WGS84 longitude coordinate in decimal degrees
    recorded_at TIMESTAMP(3) NOT NULL,                                              -- UTC timestamp of sighting at millisecond precision
    created_at  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3)                  -- UTC timestamp of database insertion at millisecond precision
);

-- Optimize queries for chronological timeline and user filtering
CREATE INDEX IF NOT EXISTS idx_sightings_recorded_at ON sightings(recorded_at DESC);
CREATE INDEX IF NOT EXISTS idx_sightings_user_id ON sightings(user_id);
