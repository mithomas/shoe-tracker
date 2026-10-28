package org.shoetracker.api

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import jakarta.inject.Inject
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.shoetracker.repository.SightingRepository

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
        val payload = """
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
            .body("id", notNullValue())
            .body("user.id", `is`("Tom"))
            .body("location.latitude", `is`(50.1109f))
            .body("location.longitude", `is`(8.6821f))
            .body("recorded_at", `is`("2026-10-28T14:32:00Z"))
            .body("created_at", notNullValue())
    }

    @Test
    fun testGetSightingsWrappedCollection() {
        // Record a sighting first to verify item content
        val payload = """
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

        given()
            .`when`()
            .get("/api/v1/sightings?limit=10&offset=0")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("items", notNullValue())
            .body("items[0].user.id", `is`("Tom"))
            .body("items[0].location.latitude", `is`(50.1109f))
            .body("total", `is`(1))
            .body("limit", `is`(10))
            .body("offset", `is`(0))
    }

    @Test
    fun testRecordSightingInvalidCoordinates() {
        val payload = """
            {
              "user_id": "Tom",
              "location": {
                "latitude": 120.0,
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
            .statusCode(400)
            .contentType("application/problem+json")
            .body("status", `is`(400))
            .body("title", `is`("Bad Request"))
            .body("detail", containsString("Latitude must be between -90 and 90"))
    }

    @Test
    fun testRecordSightingBlankUserId() {
        val payload = """
            {
              "user_id": "   ",
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
            .statusCode(400)
            .contentType("application/problem+json")
            .body("status", `is`(400))
            .body("title", `is`("Bad Request"))
            .body("detail", containsString("user_id must not be blank"))
    }

    @Test
    fun testRecordSightingMissingRecordedAt() {
        val payload = """
            {
              "user_id": "Tom",
              "location": {
                "latitude": 50.1109,
                "longitude": 8.6821
              }
            }
        """.trimIndent()

        given()
            .contentType(ContentType.JSON)
            .body(payload)
            .`when`()
            .post("/api/v1/sightings")
            .then()
            .statusCode(400)
            .contentType("application/problem+json")
            .body("status", `is`(400))
            .body("detail", containsString("recorded_at is required"))
    }

    @Test
    fun testGetSightingsInvalidLimit() {
        given()
            .`when`()
            .get("/api/v1/sightings?limit=-5")
            .then()
            .statusCode(400)
            .contentType("application/problem+json")
            .body("status", `is`(400))
            .body("detail", containsString("limit must be between 1 and 500"))
    }

    @Test
    fun testDeleteSighting() {
        val payload = """
            {
              "user_id": "Tom",
              "location": {
                "latitude": 50.1109,
                "longitude": 8.6821
              },
              "recorded_at": "2026-10-28T14:32:00Z"
            }
        """.trimIndent()

        val id = given()
            .contentType(ContentType.JSON)
            .body(payload)
            .`when`()
            .post("/api/v1/sightings")
            .then()
            .statusCode(201)
            .extract()
            .path<String>("id")

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
}
