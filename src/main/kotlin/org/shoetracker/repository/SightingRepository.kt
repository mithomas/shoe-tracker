package org.shoetracker.repository

import io.quarkus.runtime.Startup
import jakarta.annotation.PostConstruct
import jakarta.enterprise.context.ApplicationScoped
import org.shoetracker.domain.GeoLocation
import org.shoetracker.domain.Sighting
import java.sql.ResultSet
import java.sql.Timestamp
import java.util.Calendar
import java.util.TimeZone
import javax.sql.DataSource

@Startup
@ApplicationScoped
class SightingRepository(
    private val dataSource: DataSource,
) {
    @PostConstruct
    fun init() {
        // Fail-fast connectivity check
        dataSource.connection.use { conn ->
            conn.createStatement().use { stmt ->
                stmt.executeQuery("SELECT 1").close()
            }
        }
    }

    fun insert(sighting: Sighting): Sighting {
        dataSource.connection.use { conn ->
            conn.prepareStatement(INSERT_SQL).use { stmt ->
                stmt.setString(1, sighting.id)
                stmt.setString(2, sighting.userId)
                stmt.setDouble(3, sighting.location.latitude)
                stmt.setDouble(4, sighting.location.longitude)
                stmt.setTimestamp(5, Timestamp.from(sighting.recordedAt), utcCalendar())
                stmt.setTimestamp(6, Timestamp.from(sighting.createdAt), utcCalendar())
                stmt.executeUpdate()
            }
        }
        return sighting
    }

    fun findSightings(
        userId: String?,
        limit: Int,
        offset: Int,
    ): List<Sighting> {
        dataSource.connection.use { conn ->
            val sql = if (userId != null) FIND_WITH_USER_SQL else FIND_ALL_SQL
            conn.prepareStatement(sql).use { stmt ->
                if (userId != null) {
                    stmt.setString(1, userId)
                    stmt.setInt(2, limit)
                    stmt.setInt(3, offset)
                } else {
                    stmt.setInt(1, limit)
                    stmt.setInt(2, offset)
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
    }

    fun countSightings(userId: String?): Long {
        dataSource.connection.use { conn ->
            val sql = if (userId != null) COUNT_WITH_USER_SQL else COUNT_ALL_SQL
            conn.prepareStatement(sql).use { stmt ->
                if (userId != null) {
                    stmt.setString(1, userId)
                }
                stmt.executeQuery().use { rs ->
                    return if (rs.next()) rs.getLong(1) else 0L
                }
            }
        }
    }

    fun deleteAll() {
        dataSource.connection.use { conn ->
            conn.prepareStatement(DELETE_ALL_SQL).use { stmt ->
                stmt.executeUpdate()
            }
        }
    }

    fun deleteById(id: String): Boolean {
        dataSource.connection.use { conn ->
            conn.prepareStatement(DELETE_BY_ID_SQL).use { stmt ->
                stmt.setString(1, id)
                return stmt.executeUpdate() > 0
            }
        }
    }

    private fun mapRow(rs: ResultSet): Sighting = Sighting(
        id = rs.getString("id"),
        userId = rs.getString("user_id"),
        location =
        GeoLocation(
            latitude = rs.getDouble("latitude"),
            longitude = rs.getDouble("longitude"),
        ),
        recordedAt = rs.getTimestamp("recorded_at", utcCalendar()).toInstant(),
        createdAt = rs.getTimestamp("created_at", utcCalendar()).toInstant(),
    )

    private fun utcCalendar(): Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    companion object {
        private const val INSERT_SQL =
            "INSERT INTO sightings (id, user_id, latitude, longitude, recorded_at, created_at) VALUES (?, ?, ?, ?, ?, ?)"
        private const val FIND_WITH_USER_SQL =
            "SELECT id, user_id, latitude, longitude, recorded_at, created_at " +
                "FROM sightings WHERE user_id = ? ORDER BY recorded_at DESC LIMIT ? OFFSET ?"
        private const val FIND_ALL_SQL =
            "SELECT id, user_id, latitude, longitude, recorded_at, created_at " +
                "FROM sightings ORDER BY recorded_at DESC LIMIT ? OFFSET ?"
        private const val COUNT_WITH_USER_SQL = "SELECT COUNT(*) FROM sightings WHERE user_id = ?"
        private const val COUNT_ALL_SQL = "SELECT COUNT(*) FROM sightings"
        private const val DELETE_ALL_SQL = "DELETE FROM sightings"
        private const val DELETE_BY_ID_SQL = "DELETE FROM sightings WHERE id = ?"
    }
}
