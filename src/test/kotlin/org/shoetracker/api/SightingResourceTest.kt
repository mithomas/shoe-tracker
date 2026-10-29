package org.shoetracker.api

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import jakarta.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.shoetracker.api.dto.ProblemDetails
import org.shoetracker.api.dto.SightingListResponse
import org.shoetracker.api.dto.SightingResponse
import org.shoetracker.repository.SightingRepository
import java.time.Instant

@QuarkusTest
class SightingResourceTest {
    @Inject
    lateinit var repository: SightingRepository

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
    }

    @Test
    fun testRecordSightingSuccess() {
        val payload =
            """
            {
              "user_id": "Tom",
              "location": {
                "latitude": 50.1109,
                "longitude": 8.6821
              },
              "recorded_at": "2026-10-28T14:32:00Z"
            }
            """.trimIndent()

        val response =
            given()
                .contentType(ContentType.JSON)
                .body(payload)
                .`when`()
                .post("/api/v1/sightings")
                .then()
                .statusCode(201)
                .extract()
                .`as`(SightingResponse::class.java)

        assertThat(response.id).isNotBlank()
        assertThat(response.user.id).isEqualTo("Tom")
        assertThat(response.location.latitude).isEqualTo(50.1109)
        assertThat(response.location.longitude).isEqualTo(8.6821)
        assertThat(response.recordedAt).isEqualTo(Instant.parse("2026-10-28T14:32:00Z"))
        assertThat(response.createdAt).isNotNull()
    }

    @Test
    fun testGetSightingsWrappedCollection() {
        // Record a sighting first to verify item content
        val payload =
            """
            {
              "user_id": "Tom",
              "location": {
                "latitude": 50.1109,
                "longitude": 8.6821
              },
              "recorded_at": "2026-10-28T14:32:00Z"
            }
            """.trimIndent()

        given()
            .contentType(ContentType.JSON)
            .body(payload)
            .`when`()
            .post("/api/v1/sightings")
            .then()
            .statusCode(201)

        val listResponse =
            given()
                .`when`()
                .get("/api/v1/sightings?limit=10&offset=0")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .`as`(SightingListResponse::class.java)

        assertThat(listResponse.items).hasSize(1)
        val item = listResponse.items[0]
        assertThat(item.user.id).isEqualTo("Tom")
        assertThat(item.location.latitude).isEqualTo(50.1109)
        assertThat(item.location.longitude).isEqualTo(8.6821)
        assertThat(listResponse.total).isEqualTo(1)
        assertThat(listResponse.limit).isEqualTo(10)
        assertThat(listResponse.offset).isEqualTo(0)
    }

    @Test
    fun testRecordSightingInvalidCoordinates() {
        val payload =
            """
            {
              "user_id": "Tom",
              "location": {
                "latitude": 120.0,
                "longitude": 8.6821
              },
              "recorded_at": "2026-10-28T14:32:00Z"
            }
            """.trimIndent()

        val problem =
            given()
                .contentType(ContentType.JSON)
                .body(payload)
                .`when`()
                .post("/api/v1/sightings")
                .then()
                .statusCode(400)
                .contentType("application/problem+json")
                .extract()
                .`as`(ProblemDetails::class.java)

        assertThat(problem.status).isEqualTo(400)
        assertThat(problem.title).isEqualTo("Bad Request")
        assertThat(problem.detail).contains("Latitude must be between -90 and 90")
    }

    @Test
    fun testRecordSightingBlankUserId() {
        val payload =
            """
            {
              "user_id": "   ",
              "location": {
                "latitude": 50.1109,
                "longitude": 8.6821
              },
              "recorded_at": "2026-10-28T14:32:00Z"
            }
            """.trimIndent()

        val problem =
            given()
                .contentType(ContentType.JSON)
                .body(payload)
                .`when`()
                .post("/api/v1/sightings")
                .then()
                .statusCode(400)
                .contentType("application/problem+json")
                .extract()
                .`as`(ProblemDetails::class.java)

        assertThat(problem.status).isEqualTo(400)
        assertThat(problem.title).isEqualTo("Bad Request")
        assertThat(problem.detail).contains("user_id must not be blank")
    }

    @Test
    fun testRecordSightingMissingRecordedAt() {
        val payload =
            """
            {
              "user_id": "Tom",
              "location": {
                "latitude": 50.1109,
                "longitude": 8.6821
              }
            }
            """.trimIndent()

        val problem =
            given()
                .contentType(ContentType.JSON)
                .body(payload)
                .`when`()
                .post("/api/v1/sightings")
                .then()
                .statusCode(400)
                .contentType("application/problem+json")
                .extract()
                .`as`(ProblemDetails::class.java)

        assertThat(problem.status).isEqualTo(400)
        assertThat(problem.detail).contains("recorded_at is required")
    }

    @Test
    fun testGetSightingsInvalidLimit() {
        val problem =
            given()
                .`when`()
                .get("/api/v1/sightings?limit=-5")
                .then()
                .statusCode(400)
                .contentType("application/problem+json")
                .extract()
                .`as`(ProblemDetails::class.java)

        assertThat(problem.status).isEqualTo(400)
        assertThat(problem.detail).contains("limit must be between 1 and 500")
    }

    @Test
    fun testDeleteSighting() {
        val payload =
            """
            {
              "user_id": "Tom",
              "location": {
                "latitude": 50.1109,
                "longitude": 8.6821
              },
              "recorded_at": "2026-10-28T14:32:00Z"
            }
            """.trimIndent()

        val id =
            given()
                .contentType(ContentType.JSON)
                .body(payload)
                .`when`()
                .post("/api/v1/sightings")
                .then()
                .statusCode(201)
                .extract()
                .path<String>("id")

        assertThat(id).isNotBlank()

        given()
            .`when`()
            .delete("/api/v1/sightings/$id")
            .then()
            .statusCode(204)

        given()
            .`when`()
            .delete("/api/v1/sightings/non-existent-id")
            .then()
            .statusCode(404)
    }

    @Test
    fun testRecordSightingMalformedJson() {
        val malformedPayload = "{ user_id: broken-json }"

        given()
            .contentType(ContentType.JSON)
            .body(malformedPayload)
            .`when`()
            .post("/api/v1/sightings")
            .then()
            .statusCode(400)
    }

    @Test
    fun testGlobalUnhandledServerExceptionProducesRfc7807ProblemDetails() {
        val problem =
            given()
                .`when`()
                .get("/api/test/fault/unhandled")
                .then()
                .statusCode(500)
                .contentType("application/problem+json")
                .extract()
                .`as`(ProblemDetails::class.java)

        assertThat(problem.status).isEqualTo(500)
        assertThat(problem.title).isEqualTo("Internal Server Error")
        assertThat(problem.errorId).isNotNull().matches("^[0-9a-fA-F-]{36}$")
        assertThat(problem.detail)
            .contains("An unexpected server error occurred")
            .contains(problem.errorId)
            .doesNotContain("Simulated internal database explosion")
    }

    @Test
    fun testThrowableExceptionMapperDirectUnitContract() {
        val mapper = ThrowableExceptionMapper()
        val exception = IllegalStateException("Corrupt database block")
        val response = mapper.toResponse(exception)

        assertThat(response.status).isEqualTo(500)
        assertThat(response.mediaType.toString()).contains("application/problem+json")

        val entity = response.entity as ProblemDetails
        assertThat(entity.status).isEqualTo(500)
        assertThat(entity.title).isEqualTo("Internal Server Error")
        assertThat(entity.errorId).isNotNull().matches("^[0-9a-fA-F-]{36}$")
        assertThat(entity.detail)
            .contains("An unexpected server error occurred")
            .contains(entity.errorId)
            .doesNotContain("Corrupt database block")

        // Wrapped SQLException inside RuntimeException should still return 503 Retry-After: 1
        val wrappedBusy = RuntimeException("Transaction wrapper failed", java.sql.SQLException("Lock timeout", "40001", 50200))
        val wrappedResponse = mapper.toResponse(wrappedBusy)
        assertThat(wrappedResponse.status).isEqualTo(503)
        assertThat(wrappedResponse.getHeaderString("Retry-After")).isEqualTo("1")
    }

    @Test
    fun testDatabaseBusyProducesRfc7807ServiceUnavailableWithRetryAfterHeader() {
        val response =
            given()
                .`when`()
                .get("/api/test/fault/db-busy")
                .then()
                .statusCode(503)
                .contentType("application/problem+json")
                .header("Retry-After", "1")
                .extract()

        val problem = response.`as`(ProblemDetails::class.java)
        assertThat(problem.status).isEqualTo(503)
        assertThat(problem.title).isEqualTo("Service Unavailable")
        assertThat(problem.errorId).isNotNull().matches("^[0-9a-fA-F-]{36}$")
        assertThat(problem.detail).contains("Database is temporarily busy. Please retry in 1 second.")
    }

    @Test
    fun testDatabaseCorruptProducesRfc7807InternalServerError() {
        val problem =
            given()
                .`when`()
                .get("/api/test/fault/db-error")
                .then()
                .statusCode(500)
                .contentType("application/problem+json")
                .extract()
                .`as`(ProblemDetails::class.java)

        assertThat(problem.status).isEqualTo(500)
        assertThat(problem.title).isEqualTo("Internal Server Error")
        assertThat(problem.errorId).isNotNull().matches("^[0-9a-fA-F-]{36}$")
        assertThat(problem.detail)
            .contains("A database error occurred")
            .doesNotContain("database disk image is malformed")
    }

    @Test
    fun testDatabaseContentionExceptionMapperDirectUnitContract() {
        val mapper = DatabaseContentionExceptionMapper()

        // Test SQLState 40001 / H2 lock timeout (50200)
        val busyEx = java.sql.SQLException("database locked", "40001", 50200)
        val busyResp = mapper.toResponse(busyEx)
        assertThat(busyResp.status).isEqualTo(503)
        assertThat(busyResp.getHeaderString("Retry-After")).isEqualTo("1")
        val busyProblem = busyResp.entity as ProblemDetails
        assertThat(busyProblem.title).isEqualTo("Service Unavailable")

        // Test H2 deadlock / lock error code (90039, 90072)
        val lockedEx = java.sql.SQLException("table locked", "HY000", 90039)
        val lockedResp = mapper.toResponse(lockedEx)
        assertThat(lockedResp.status).isEqualTo(503)
        assertThat(mapper.toResponse(java.sql.SQLException("lock timeout", "HY000", 90072)).status).isEqualTo(503)

        // Test ANSI SQLState contention codes (40P01, 57P01, HYT00)
        assertThat(mapper.toResponse(java.sql.SQLException("deadlock", "40P01", 0)).status).isEqualTo(503)
        assertThat(mapper.toResponse(java.sql.SQLException("admin shutdown", "57P01", 0)).status).isEqualTo(503)
        assertThat(mapper.toResponse(java.sql.SQLException("timeout expired", "HYT00", 0)).status).isEqualTo(503)

        // Test MariaDB/MySQL lock wait timeout (1205) and deadlock (1213)
        assertThat(mapper.toResponse(java.sql.SQLException("Lock wait timeout exceeded", "HY000", 1205)).status).isEqualTo(503)
        assertThat(mapper.toResponse(java.sql.SQLException("Deadlock found", "40001", 1213)).status).isEqualTo(503)

        // Test wrapped causal chain via initCause and setNextException
        val wrapperWithCause = java.sql.SQLException("Pool wrapper error", "HY000", 0)
        wrapperWithCause.initCause(java.sql.SQLException("Inner MariaDB lock timeout", "HY000", 1205))
        assertThat(mapper.toResponse(wrapperWithCause).status).isEqualTo(503)

        val wrapperWithNext = java.sql.SQLException("Batch execution failed", "HY000", 0)
        wrapperWithNext.setNextException(java.sql.SQLException("Inner H2 lock", "40001", 50200))
        assertThat(mapper.toResponse(wrapperWithNext).status).isEqualTo(503)

        // Test branching causal chain where outer SQLException has BOTH non-contention nextException AND contention cause
        val wrapperWithBoth = java.sql.SQLException("Batch wrapper error", "HY000", 0)
        wrapperWithBoth.setNextException(java.sql.SQLException("Secondary warning", "01000", 0))
        wrapperWithBoth.initCause(java.sql.SQLException("Underlying deadlock", "40001", 1213))
        assertThat(mapper.toResponse(wrapperWithBoth).status).isEqualTo(503)

        // Test non-busy error (e.g. HY000 corrupt file)
        val corruptEx = java.sql.SQLException("corrupt file", "HY000", 90030)
        val corruptResp = mapper.toResponse(corruptEx)
        assertThat(corruptResp.status).isEqualTo(500)
        val corruptProblem = corruptResp.entity as ProblemDetails
        assertThat(corruptProblem.title).isEqualTo("Internal Server Error")
        assertThat(corruptProblem.detail).doesNotContain("corrupt file")
    }
}
