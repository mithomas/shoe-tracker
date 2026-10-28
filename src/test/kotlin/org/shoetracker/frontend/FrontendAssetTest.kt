package org.shoetracker.frontend

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.Locale
import kotlin.math.abs
import kotlin.math.floor

@QuarkusTest
class FrontendAssetTest {

    @Test
    fun testIndexHtmlServedWithAllComponents() {
        given()
            .`when`()
            .get("/")
            .then()
            .statusCode(200)
            .body(containsString("Shoe Tracker"))
            .body(containsString("onboardingModal"))
            .body(containsString("gpsPill"))
            .body(containsString("gpsWarningBanner"))
            .body(containsString("telemetryCoords"))
            .body(containsString("btnSpotGiant"))
            .body(containsString("sightingModal"))
            .body(containsString("cooldownRingProgress"))
            .body(containsString("btnSightingUndo"))
            .body(containsString("networkAlertBanner"))
            .body(containsString("coords-val"))
            .body(containsString("app-footer"))
            .body(containsString("v1.0.0"))
    }

    @Test
    fun testStaticAssetsServed() {
        given()
            .`when`()
            .get("/css/style.css")
            .then()
            .statusCode(200)
            .body(containsString("--surface-base"))
            .body(containsString("btn-spot-giant"))
            .body(containsString("cooldown-ring"))
            .body(containsString("btn-modal-tertiary"))
            .body(containsString("coords-val"))
            .body(containsString("app-footer"))

        given()
            .`when`()
            .get("/js/format.js")
            .then()
            .statusCode(200)
            .body(containsString("formatCoordinate"))
            .body(containsString("formatLocationMultiline"))
            .body(containsString("60.000"))

        given()
            .`when`()
            .get("/js/geo.js")
            .then()
            .statusCode(200)
            .body(containsString("watchPosition"))
            .body(containsString("5000"))

        given()
            .`when`()
            .get("/js/onboarding.js")
            .then()
            .statusCode(200)
            .body(containsString("localStorage"))

        given()
            .`when`()
            .get("/js/modal.js")
            .then()
            .statusCode(200)
            .body(containsString("showSighting"))
            .body(containsString("handleUndo"))

        given()
            .`when`()
            .get("/js/spot.js")
            .then()
            .statusCode(200)
            .body(containsString("startCooldown"))
            .body(containsString("cooldownSeconds"))
            .body(containsString("setGpsStatus"))
    }

    @Test
    fun testCoordinateFormattingParity() {
        fun formatCoordinate(coord: Double, isLat: Boolean): String {
            val dir = if (isLat) {
                if (coord >= 0) "N" else "S"
            } else {
                if (coord >= 0) "E" else "W"
            }
            val absVal = abs(coord)
            var degrees = floor(absVal).toInt()
            val minutes = (absVal - degrees) * 60.0
            var minFormatted = "%.3f".format(Locale.US, minutes)

            // Test rollover parity
            if (minFormatted == "60.000") {
                degrees += 1
                minFormatted = "00.000"
            }

            val degStr = if (isLat) "%02d".format(degrees) else "%03d".format(degrees)
            val minPadded = if (minFormatted.indexOf('.') == 1) "0$minFormatted" else minFormatted
            val minStr = minPadded.replace('.', ',')
            return "$dir $degStr° $minStr'"
        }

        // Test Frankfurt coordinates: 50.1109, 8.6821
        assertEquals("N 50° 06,654'", formatCoordinate(50.1109, true))
        assertEquals("E 008° 40,926'", formatCoordinate(8.6821, false))

        // Test Southern / Western hemisphere coordinates
        assertEquals("S 33° 51,900'", formatCoordinate(-33.8650, true))
        assertEquals("W 071° 03,600'", formatCoordinate(-71.0600, false))

        // Test 60-minute rounding rollover
        assertEquals("N 51° 00,000'", formatCoordinate(50.9999999, true))
    }
}
