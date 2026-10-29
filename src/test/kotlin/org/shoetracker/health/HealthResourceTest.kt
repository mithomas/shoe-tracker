package org.shoetracker.health

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

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
        val dbCheck = checks.find { it["name"] == "SQLite database health check" }
        assertThat(dbCheck).isNotNull
        assertThat(dbCheck!!["status"]).isEqualTo("UP")
        val data = dbCheck["data"] as Map<*, *>
        assertThat(data["database"]).isEqualTo("sqlite")
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
