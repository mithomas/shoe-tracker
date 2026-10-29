package org.shoetracker.repository

import jakarta.annotation.PreDestroy
import jakarta.enterprise.context.ApplicationScoped
import org.shoetracker.domain.GeoLocation
import org.shoetracker.domain.Sighting
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.concurrent.locks.ReentrantLock
import javax.sql.DataSource
import kotlin.concurrent.withLock

@ApplicationScoped
class SightingRepository(
    private val dataSource: DataSource,
) : AutoCloseable {
    private val writeLock = ReentrantLock()
    private val readLock = ReentrantLock()

    // Formatter ensuring fixed-length 3-digit milliseconds for strict lexicographical sorting in SQLite TEXT columns
    private val isoFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSX").withZone(ZoneOffset.UTC)

    // Dedicated connection holding prepared statements compiled once on initialization
    private val connection: Connection by lazy {
        dataSource.connection
    }

    private val insertStmt: PreparedStatement by lazy {
        connection.prepareStatement(
            "INSERT INTO sightings (id, user_id, latitude, longitude, recorded_at, created_at) VALUES (?, ?, ?, ?, ?, ?)",
        )
    }

    private val findWithUserStmt: PreparedStatement by lazy {
        connection.prepareStatement(
            "SELECT id, user_id, latitude, longitude, recorded_at, created_at FROM sightings WHERE user_id = ? ORDER BY recorded_at DESC LIMIT ? OFFSET ?",
        )
    }

    private val findAllStmt: PreparedStatement by lazy {
        connection.prepareStatement(
            "SELECT id, user_id, latitude, longitude, recorded_at, created_at FROM sightings ORDER BY recorded_at DESC LIMIT ? OFFSET ?",
        )
    }

    private val countWithUserStmt: PreparedStatement by lazy {
        connection.prepareStatement("SELECT COUNT(*) FROM sightings WHERE user_id = ?")
    }

    private val countAllStmt: PreparedStatement by lazy {
        connection.prepareStatement("SELECT COUNT(*) FROM sightings")
    }

    private val deleteAllStmt: PreparedStatement by lazy {
        connection.prepareStatement("DELETE FROM sightings")
    }

    fun insert(sighting: Sighting): Sighting {
        writeLock.withLock {
            insertStmt.clearParameters()
            insertStmt.setString(1, sighting.id)
            insertStmt.setString(2, sighting.userId)
            insertStmt.setDouble(3, sighting.location.latitude)
            insertStmt.setDouble(4, sighting.location.longitude)
            insertStmt.setString(5, isoFormatter.format(sighting.recordedAt))
            insertStmt.setString(6, isoFormatter.format(sighting.createdAt))
            insertStmt.executeUpdate()
        }
        return sighting
    }

    fun findSightings(
        userId: String?,
        limit: Int,
        offset: Int,
    ): List<Sighting> {
        readLock.withLock {
            val stmt =
                if (userId != null) {
                    findWithUserStmt.clearParameters()
                    findWithUserStmt.setString(1, userId)
                    findWithUserStmt.setInt(2, limit)
                    findWithUserStmt.setInt(3, offset)
                    findWithUserStmt
                } else {
                    findAllStmt.clearParameters()
                    findAllStmt.setInt(1, limit)
                    findAllStmt.setInt(2, offset)
                    findAllStmt
                }

            stmt.executeQuery().use { rs ->
                val list = mutableListOf<Sighting>()
                while (rs.next()) {
                    list.add(mapRow(rs))
                }
                return list
            }
        }
    }

    fun countSightings(userId: String?): Long {
        readLock.withLock {
            val stmt =
                if (userId != null) {
                    countWithUserStmt.clearParameters()
                    countWithUserStmt.setString(1, userId)
                    countWithUserStmt
                } else {
                    countAllStmt.clearParameters()
                    countAllStmt
                }

            stmt.executeQuery().use { rs ->
                return if (rs.next()) rs.getLong(1) else 0L
            }
        }
    }

    fun deleteAll() {
        writeLock.withLock {
            deleteAllStmt.executeUpdate()
        }
    }

    fun deleteById(id: String): Boolean {
        val sql = "DELETE FROM sightings WHERE id = ?"
        return writeLock.withLock {
            connection.prepareStatement(sql).use { stmt ->
                stmt.setString(1, id)
                stmt.executeUpdate() > 0
            }
        }
    }

    @PreDestroy
    override fun close() {
        runCatching { insertStmt.close() }
        runCatching { findWithUserStmt.close() }
        runCatching { findAllStmt.close() }
        runCatching { countWithUserStmt.close() }
        runCatching { countAllStmt.close() }
        runCatching { deleteAllStmt.close() }
        runCatching { connection.close() }
    }

    private fun mapRow(rs: ResultSet): Sighting = Sighting(
        id = rs.getString("id"),
        userId = rs.getString("user_id"),
        location =
        GeoLocation(
            latitude = rs.getDouble("latitude"),
            longitude = rs.getDouble("longitude"),
        ),
        recordedAt = Instant.parse(rs.getString("recorded_at")),
        createdAt = Instant.parse(rs.getString("created_at")),
    )
}
