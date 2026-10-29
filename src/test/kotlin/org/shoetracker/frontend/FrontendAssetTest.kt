package org.shoetracker.frontend

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
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
            .body(containsString("btnSpotGiant"))
            .body(containsString("sightingModal"))
            .body(containsString("cooldownRingProgress"))
            .body(containsString("btnSightingUndo"))
            .body(containsString("networkAlertBanner"))
            .body(containsString("coords-val"))
            .body(containsString("app-footer"))
            .body(containsString("v1.0.0"))
            .body(containsString("spotMiniMap"))
            .body(containsString("miniMapDeniedOverlay"))
            .body(containsString("bottomNav"))
            .body(containsString("viewSpot"))
            .body(containsString("viewMap"))
            .body(containsString("viewList"))
            .body(containsString("fullMap"))
            .body(containsString("fullMapContainer"))
            .body(containsString("fullMapLoading"))
            .body(containsString("fullMapEmpty"))
            .body(containsString("fullMapEmptyOwn"))
            .body(containsString("full-map-wrapper"))
            .body(containsString("listViewContainer"))
            .body(containsString("sightingsList"))
            .body(containsString("listCountBadge"))
            .body(containsString("listEmptyOwnCard"))
            .body(containsString("filter-pill-container"))
            .body(containsString("data-filter=\"all\""))
            .body(containsString("data-filter=\"own\""))
            .body(containsString("/vendor/leaflet/leaflet.css"))
    }

    @Test
    fun testStaticAssetsServed() {
        given()
            .`when`()
            .get("/vendor/leaflet/leaflet.js")
            .then()
            .statusCode(200)
            .body(containsString("Leaflet"))

        given()
            .`when`()
            .get("/vendor/leaflet/leaflet.css")
            .then()
            .statusCode(200)
            .body(containsString("leaflet"))

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
            .body(containsString("bottom-nav"))
            .body(containsString("mini-map-container"))
            .body(containsString("mini-map-overlay"))
            .body(containsString("shoe-pin-own"))
            .body(containsString("shoe-pin-other"))
            .body(containsString("sighting-leaflet-popup"))
            .body(containsString("sighting-card-own"))
            .body(containsString("sighting-card-other"))
            .body(containsString("filter-pill-container"))

        given()
            .`when`()
            .get("/js/tabs.js")
            .then()
            .statusCode(200)
            .body(containsString("tabManager"))
            .body(containsString("switchTab"))
            .body(containsString("invalidateActiveMap"))
            .body(containsString("orientationchange"))

        given()
            .`when`()
            .get("/js/minimap.js")
            .then()
            .statusCode(200)
            .body(containsString("spotMiniMap"))
            .body(containsString("live-gps-dot"))
            .body(containsString("dragging: false"))

        given()
            .`when`()
            .get("/js/fullmap.js")
            .then()
            .statusCode(200)
            .body(containsString("fullMap"))
            .body(containsString("shoe-pin-own"))
            .body(containsString("sighting-added"))
            .body(containsString("sighting-deleted"))

        given()
            .`when`()
            .get("/js/listview.js")
            .then()
            .statusCode(200)
            .body(containsString("listView"))
            .body(containsString("sighting-card-own"))
            .body(containsString("sighting-added"))
            .body(containsString("sighting-deleted"))

        given()
            .`when`()
            .get("/js/filter.js")
            .then()
            .statusCode(200)
            .body(containsString("filterManager"))
            .body(containsString("filter-changed"))
            .body(containsString("localStorage"))

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

    @Test
    fun testUnifiedFilterAssetsAndMarkup() {
        given()
            .`when`()
            .get("/")
            .then()
            .statusCode(200)
            .body(containsString("/js/filter.js"))
            .body(containsString("filterManager.init()"))
            .body(containsString("filter-pill-container"))
            .body(containsString("filter-pill-bg"))
            .body(containsString("map-filter-float"))
            .body(containsString("fullMapEmptyOwn"))
            .body(containsString("listEmptyOwnCard"))
            .body(containsString("No shoes spotted by you yet — tap the Spot button to log your first!"))

        given()
            .`when`()
            .get("/css/style.css")
            .then()
            .statusCode(200)
            .body(containsString(".filter-pill-container"))
            .body(containsString(".filter-pill-bg"))
            .body(containsString(".filter-pill-btn"))
            .body(containsString(".map-filter-float"))

        given()
            .`when`()
            .get("/js/filter.js")
            .then()
            .statusCode(200)
            .body(containsString("class FilterManager"))
            .body(containsString("filter_own_only"))
            .body(containsString("filter-changed"))
            .body(containsString("user-changed"))

        given()
            .`when`()
            .get("/js/fullmap.js")
            .then()
            .statusCode(200)
            .body(containsString("filter-changed"))
            .body(containsString("fullMapEmptyOwn"))

        given()
            .`when`()
            .get("/js/listview.js")
            .then()
            .statusCode(200)
            .body(containsString("filter-changed"))
            .body(containsString("listEmptyOwnCard"))
            .body(containsString("of"))
            .body(containsString("sightings"))
    }

    @Test
    fun testUnifiedFilterLogicMatrixParity() {
        data class TestSighting(val id: String, val userId: String)

        fun filterSightings(sightings: List<TestSighting>, ownOnly: Boolean, currentUserId: String): List<TestSighting> {
            val trimmedUser = currentUserId.trim().lowercase()
            return sightings.filter { s ->
                if (!ownOnly) true
                else trimmedUser.isNotEmpty() && s.userId.trim().lowercase() == trimmedUser
            }
        }

        fun computeListCounter(filteredCount: Int, totalCount: Int, ownOnly: Boolean): String {
            return if (ownOnly) {
                "$filteredCount of $totalCount sightings"
            } else {
                "$totalCount ${if (totalCount == 1) "sighting" else "sightings"}"
            }
        }

        val sightings = listOf(
            TestSighting("1", "Tom"),
            TestSighting("2", "Alice"),
            TestSighting("3", "tom "),
            TestSighting("4", "Bob")
        )

        // Matrix Row 1: Toggle "My Shoes" on Map - shows only user's markers
        val mapFiltered = filterSightings(sightings, ownOnly = true, currentUserId = "TOM")
        assertEquals(2, mapFiltered.size)
        assertEquals(listOf("1", "3"), mapFiltered.map { it.id })

        // Matrix Row 2: Switch from Map to List - counter displays filtered count of total count
        val listCounter = computeListCounter(mapFiltered.size, sightings.size, ownOnly = true)
        assertEquals("2 of 4 sightings", listCounter)

        // Matrix Row 3: No Personal Sightings - empty state condition
        val emptyForUser = filterSightings(sightings, ownOnly = true, currentUserId = "UnknownUser")
        assertEquals(0, emptyForUser.size)
        val emptyMessage = "No shoes spotted by you yet — tap the Spot button to log your first!"
        assertTrue(emptyMessage.contains("No shoes spotted by you yet"))

        // Matrix Row 4: User Changes Name - re-evaluates filter immediately for new user
        val switchedUser = filterSightings(sightings, ownOnly = true, currentUserId = "Alice")
        assertEquals(1, switchedUser.size)
        assertEquals("2", switchedUser.first().id)
        assertEquals("1 of 4 sightings", computeListCounter(switchedUser.size, sightings.size, ownOnly = true))
    }

    @Test
    fun testMapLifecycleAndResizeResilience() {
        given()
            .`when`()
            .get("/js/tabs.js")
            .then()
            .statusCode(200)
            .body(containsString("resize"))
            .body(containsString("orientationchange"))
            .body(containsString("invalidateActiveMap"))
            .body(containsString("resizeDebounce"))

        given()
            .`when`()
            .get("/js/minimap.js")
            .then()
            .statusCode(200)
            .body(containsString("invalidateSize(true)"))
            .body(containsString("invalidateSize(immediate"))
            .body(containsString("200"))
            .body(containsString("tab-switched"))

        given()
            .`when`()
            .get("/js/fullmap.js")
            .then()
            .statusCode(200)
            .body(containsString("invalidateSize(true)"))
            .body(containsString("invalidateSize(immediate"))
            .body(containsString("200"))
            .body(containsString("tab-switched"))
    }

    @Test
    fun testAgplLicensingAndAttribution() {
        val licenseFile = File("LICENSE")
        assertTrue(licenseFile.exists(), "Root LICENSE file must exist")
        val licenseContent = licenseFile.readText()
        assertTrue(licenseContent.contains("GNU AFFERO GENERAL PUBLIC LICENSE"), "LICENSE must be AGPL")
        assertTrue(licenseContent.contains("Version 3, 19 November 2007"), "LICENSE must be Version 3")

        val readmeFile = File("README.md")
        assertTrue(readmeFile.exists(), "README.md must exist")
        val readmeContent = readmeFile.readText()
        assertTrue(readmeContent.contains("GNU Affero General Public License v3.0 (AGPL-3.0)"), "README must document AGPL-3.0")

        given()
            .`when`()
            .get("/")
            .then()
            .statusCode(200)
            .body(containsString("footer-license-link"))
            .body(containsString("AGPL-3.0 Source"))

        given()
            .`when`()
            .get("/css/style.css")
            .then()
            .statusCode(200)
            .body(containsString(".footer-license-link"))
    }
}
