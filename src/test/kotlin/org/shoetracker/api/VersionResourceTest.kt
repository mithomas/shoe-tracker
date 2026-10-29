package org.shoetracker.api

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers.matchesPattern
import org.junit.jupiter.api.Test

@QuarkusTest
class VersionResourceTest {

    @Test
    fun testGetVersionEndpoint() {
        given()
            .`when`()
            .get("/api/v1/version")
            .then()
            .statusCode(200)
            .body("version", notNullValue())
            .body("commit", notNullValue())
            .body("commit", matchesPattern("^[0-9a-f]{7}$"))
    }
}
