package org.shoetracker.api

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@QuarkusTest
class VersionResourceTest {

    @Test
    fun testGetVersionEndpoint() {
        val response = given()
            .`when`()
            .get("/api/v1/version")
            .then()
            .statusCode(200)
            .extract()
            .`as`(VersionResponse::class.java)

        assertThat(response.version).isNotBlank()
        assertThat(response.commit).matches("^[0-9a-f]{7}$")
    }
}
