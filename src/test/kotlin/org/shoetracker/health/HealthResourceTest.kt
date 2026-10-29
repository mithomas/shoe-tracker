package org.shoetracker.health

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.eclipse.microprofile.health.HealthCheckResponse
import org.junit.jupiter.api.Test
import java.io.PrintWriter
import java.lang.reflect.Proxy
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.SQLFeatureNotSupportedException
import java.sql.Statement
import java.util.logging.Logger
import javax.sql.DataSource

@QuarkusTest
class HealthResourceTest {
    @Test
    fun testHealthLiveEndpoint() {
        val response =
            given()
                .`when`()
                .get("/q/health/live")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .response()

        val status = response.jsonPath().getString("status")
        assertThat(status).isEqualTo("UP")
    }

    @Test
    fun testHealthReadyEndpoint() {
        val response =
            given()
                .`when`()
                .get("/q/health/ready")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .response()

        val status = response.jsonPath().getString("status")
        assertThat(status).isEqualTo("UP")

        val checks = response.jsonPath().getList<Map<String, Any>>("checks")
        assertThat(checks).isNotEmpty
        val dbCheck = checks.find { it["name"] == "Database connection health check" }
        assertThat(dbCheck).isNotNull
        assertThat(dbCheck!!["status"]).isEqualTo("UP")
        val data = dbCheck["data"] as Map<*, *>
        assertThat(data["database"]).isEqualTo("H2")
    }

    @Test
    fun testDatabaseHealthCheckReportsDownWhenDataSourceFails() {
        val failingDataSource =
            object : DataSource {
                override fun getConnection(): Connection = throw SQLException("Connection refused")

                override fun getConnection(
                    username: String?,
                    password: String?,
                ): Connection = throw SQLException("Connection refused")

                override fun getLogWriter(): PrintWriter? = null

                override fun setLogWriter(out: PrintWriter?) {}

                override fun setLoginTimeout(seconds: Int) {}

                override fun getLoginTimeout(): Int = 0

                override fun getParentLogger(): Logger = throw SQLFeatureNotSupportedException()

                override fun <T : Any?> unwrap(iface: Class<T>?): T = throw SQLException()

                override fun isWrapperFor(iface: Class<*>?): Boolean = false
            }

        val check = DatabaseHealthCheck(failingDataSource)
        val result = check.call()
        assertThat(result.name).isEqualTo("Database connection health check")
        assertThat(result.status).isEqualTo(HealthCheckResponse.Status.DOWN)
        assertThat(result.data.orElse(emptyMap())["error"]).isEqualTo("Connection refused")

        // Verify "Unknown" fallback when conn.metaData is null
        val dummyResultSet =
            Proxy.newProxyInstance(
                ResultSet::class.java.classLoader,
                arrayOf(ResultSet::class.java),
            ) { _, _, _ -> null } as ResultSet
        val dummyStatement =
            Proxy.newProxyInstance(
                Statement::class.java.classLoader,
                arrayOf(Statement::class.java),
            ) { _, method, _ ->
                if (method.name == "executeQuery") dummyResultSet else null
            } as Statement
        val dummyConnection =
            Proxy.newProxyInstance(
                Connection::class.java.classLoader,
                arrayOf(Connection::class.java),
            ) { _, method, _ ->
                when (method.name) {
                    "createStatement" -> dummyStatement
                    "getMetaData" -> null
                    else -> null
                }
            } as Connection
        val nullMetaDataSource =
            object : DataSource by failingDataSource {
                override fun getConnection(): Connection = dummyConnection
            }

        val unknownResult = DatabaseHealthCheck(nullMetaDataSource).call()
        assertThat(unknownResult.status).isEqualTo(HealthCheckResponse.Status.UP)
        assertThat(unknownResult.data.orElse(emptyMap())["database"]).isEqualTo("Unknown")

        // Verify dynamic product name reporting when connected to MariaDB
        val mariaDbMetaData =
            Proxy.newProxyInstance(
                java.sql.DatabaseMetaData::class.java.classLoader,
                arrayOf(java.sql.DatabaseMetaData::class.java),
            ) { _, method, _ ->
                if (method.name == "getDatabaseProductName") "MariaDB" else null
            } as java.sql.DatabaseMetaData
        val mariaDbConnection =
            Proxy.newProxyInstance(
                Connection::class.java.classLoader,
                arrayOf(Connection::class.java),
            ) { _, method, _ ->
                when (method.name) {
                    "createStatement" -> dummyStatement
                    "getMetaData" -> mariaDbMetaData
                    else -> null
                }
            } as Connection
        val mariaDbDataSource =
            object : DataSource by failingDataSource {
                override fun getConnection(): Connection = mariaDbConnection
            }

        val mariaDbResult = DatabaseHealthCheck(mariaDbDataSource).call()
        assertThat(mariaDbResult.name).isEqualTo("Database connection health check")
        assertThat(mariaDbResult.status).isEqualTo(HealthCheckResponse.Status.UP)
        assertThat(mariaDbResult.data.orElse(emptyMap())["database"]).isEqualTo("MariaDB")
    }

    @Test
    fun testCombinedHealthEndpoint() {
        val response =
            given()
                .`when`()
                .get("/q/health")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .response()

        val status = response.jsonPath().getString("status")
        assertThat(status).isEqualTo("UP")
    }
}
