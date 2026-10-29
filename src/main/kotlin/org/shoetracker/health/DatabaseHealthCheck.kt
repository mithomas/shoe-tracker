package org.shoetracker.health

import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.health.HealthCheck
import org.eclipse.microprofile.health.HealthCheckResponse
import org.eclipse.microprofile.health.Readiness
import javax.sql.DataSource

@Readiness
@ApplicationScoped
class DatabaseHealthCheck(
    private val dataSource: DataSource,
) : HealthCheck {
    override fun call(): HealthCheckResponse {
        val builder = HealthCheckResponse.named("SQLite database health check")
        return try {
            dataSource.connection.use { conn ->
                conn.createStatement().use { stmt ->
                    stmt.executeQuery("SELECT 1").close()
                }
            }
            builder.up().withData("database", "sqlite").build()
        } catch (e: Exception) {
            builder.down().withData("error", e.message ?: "unknown").build()
        }
    }
}
