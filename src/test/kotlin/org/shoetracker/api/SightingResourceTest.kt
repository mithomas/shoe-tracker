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
        val exception = IllegalStateException("Corrupt SQLite database block")
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
            .doesNotContain("Corrupt SQLite database block")
    }
}
