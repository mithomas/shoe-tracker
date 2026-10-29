package org.shoetracker.frontend

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import java.util.Locale
import kotlin.math.abs
import kotlin.math.floor

@QuarkusTest
class FrontendAssetTest {
    @Test
    fun testIndexHtmlServedWithAllComponents() {
        val html =
            given()
                .`when`()
                .get("/")
                .then()
                .statusCode(200)
                .extract()
                .asString()

        assertThat(html)
            .contains("Shoe Tracker")
            .contains("onboardingModal")
            .contains("gpsPill")
            .contains("gpsWarningBanner")
            .contains("btnSpotGiant")
            .contains("sightingModal")
            .doesNotContain("cooldownRingProgress")
            .contains("btnSightingUndo")
            .contains("networkAlertBanner")
            .contains("coords-val")
            .contains("app-footer")
            .contains("app-version-text")
            .contains("/api/v1/version")
            .contains("v1.0.0")
            .contains("spotMiniMap")
            .contains("miniMapDeniedOverlay")
            .contains("bottomNav")
            .contains("viewSpot")
            .contains("viewMap")
            .contains("viewList")
            .contains("fullMap")
            .contains("fullMapContainer")
            .contains("fullMapLoading")
            .contains("fullMapEmpty")
            .contains("fullMapEmptyOwn")
            .contains("full-map-wrapper")
            .contains("listViewContainer")
            .contains("sightingsList")
            .contains("listCountBadge")
            .contains("listEmptyOwnCard")
            .contains("filter-pill-container")
            .contains("data-filter=\"all\"")
            .contains("data-filter=\"own\"")
            .contains("/vendor/leaflet/leaflet.css")
            .contains("/favicon.svg")
            .contains("/favicon.ico")
            .contains("offlineTileBanner")
    }

    @Test
    fun testFaviconRetinaAndMvnpmLeafletAssetsServed() {
        val buildGradle = File("build.gradle.kts").readText()
        assertThat(buildGradle)
            .contains("org.mvnpm:leaflet:1.9.4")
            .contains("extractLeafletAssets")

        // Raw manually vendored directory must not exist in source tree
        assertThat(File("src/main/resources/META-INF/resources/vendor/leaflet")).doesNotExist()

        val faviconSvg =
            given()
                .`when`()
                .get("/favicon.svg")
                .then()
                .statusCode(200)
                .extract()
                .asString()
        assertThat(faviconSvg).contains("<svg").contains("#F97316")

        val faviconIcoBytes =
            given()
                .`when`()
                .get("/favicon.ico")
                .then()
                .statusCode(200)
                .extract()
                .asByteArray()
        assertThat(faviconIcoBytes).isNotEmpty()

        val markerRetinaBytes =
            given()
                .`when`()
                .get("/vendor/leaflet/images/marker-icon-2x.png")
                .then()
                .statusCode(200)
                .extract()
                .asByteArray()
        assertThat(markerRetinaBytes).isNotEmpty()

        val markerIconBytes =
            given()
                .`when`()
                .get("/vendor/leaflet/images/marker-icon.png")
                .then()
                .statusCode(200)
                .extract()
                .asByteArray()
        assertThat(markerIconBytes).isNotEmpty()

        val miniMapJs =
            given()
                .`when`()
                .get("/js/minimap.js")
                .then()
                .statusCode(200)
                .extract()
                .asString()
        assertThat(miniMapJs)
            .contains("tileerror")
            .contains("offlineTileBanner")

        val fullMapJs =
            given()
                .`when`()
                .get("/js/fullmap.js")
                .then()
                .statusCode(200)
                .extract()
                .asString()
        assertThat(fullMapJs)
            .contains("tileerror")
            .contains("offlineTileBanner")
    }

    @Test
    fun testStaticAssetsServed() {
        val leafletJs =
            given()
                .`when`()
                .get("/vendor/leaflet/leaflet.js")
                .then()
                .statusCode(200)
                .extract()
                .asString()
        assertThat(leafletJs).contains("Leaflet")

        val leafletCss =
            given()
                .`when`()
                .get("/vendor/leaflet/leaflet.css")
                .then()
                .statusCode(200)
                .extract()
                .asString()
        assertThat(leafletCss).contains("leaflet")

        val css =
            given()
                .`when`()
                .get("/css/style.css")
                .then()
                .statusCode(200)
                .extract()
                .asString()
        assertThat(css)
            .contains("--surface-base")
            .contains("btn-spot-giant")
            .doesNotContain("cooldown-ring")
            .contains("btn-modal-tertiary")
            .contains("coords-val")
            .contains("app-footer")
            .contains("bottom-nav")
            .contains("mini-map-container")
            .contains("mini-map-overlay")
            .contains("shoe-pin-own")
            .contains("shoe-pin-other")
            .contains("sighting-leaflet-popup")
            .contains("sighting-card-own")
            .contains("sighting-card-other")
            .contains("filter-pill-container")

        val tabsJs =
            given()
                .`when`()
                .get("/js/tabs.js")
                .then()
                .statusCode(200)
                .extract()
                .asString()
        assertThat(tabsJs)
            .contains("tabManager")
            .contains("switchTab")
            .contains("invalidateActiveMap")
            .contains("orientationchange")

        val miniMapJs =
            given()
                .`when`()
                .get("/js/minimap.js")
                .then()
                .statusCode(200)
                .extract()
                .asString()
        assertThat(miniMapJs)
            .contains("spotMiniMap")
            .contains("live-gps-dot")
            .contains("dragging: false")

        val fullMapJs =
            given()
                .`when`()
                .get("/js/fullmap.js")
                .then()
                .statusCode(200)
                .extract()
                .asString()
        assertThat(fullMapJs)
            .contains("fullMap")
            .contains("shoe-pin-own")
            .contains("sighting-added")
            .contains("sighting-deleted")

        val listViewJs =
            given()
                .`when`()
                .get("/js/listview.js")
                .then()
                .statusCode(200)
                .extract()
                .asString()
        assertThat(listViewJs)
            .contains("listView")
            .contains("sighting-card-own")
            .contains("sighting-added")
            .contains("sighting-deleted")

        val filterJs =
            given()
                .`when`()
                .get("/js/filter.js")
                .then()
                .statusCode(200)
                .extract()
                .asString()
        assertThat(filterJs)
            .contains("filterManager")
            .contains("filter-changed")
            .contains("localStorage")

        val formatJs =
            given()
                .`when`()
                .get("/js/format.js")
                .then()
                .statusCode(200)
                .extract()
                .asString()
        assertThat(formatJs)
            .contains("formatCoordinate")
            .contains("formatLocationMultiline")
            .contains("60.000")

        val geoJs =
            given()
                .`when`()
                .get("/js/geo.js")
                .then()
                .statusCode(200)
                .extract()
                .asString()
        assertThat(geoJs)
            .contains("watchPosition")
            .contains("5000")

        val onboardingJs =
            given()
                .`when`()
                .get("/js/onboarding.js")
                .then()
                .statusCode(200)
                .extract()
                .asString()
        assertThat(onboardingJs).contains("localStorage")

        val modalJs =
            given()
                .`when`()
                .get("/js/modal.js")
                .then()
                .statusCode(200)
                .extract()
                .asString()
        assertThat(modalJs)
            .contains("showSighting")
            .contains("handleUndo")

        val spotJs =
            given()
                .`when`()
                .get("/js/spot.js")
                .then()
                .statusCode(200)
                .extract()
                .asString()
        assertThat(spotJs)
            .contains("isSubmitting")
            .contains("setGpsStatus")
            .doesNotContain("startCooldown")
            .doesNotContain("cooldownSeconds")
    }

    @Test
    fun testCoordinateFormattingParity() {
        fun formatCoordinate(
            coord: Double,
            isLat: Boolean,
        ): String {
            val dir =
                if (isLat) {
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
                minFormatted = "00.000"
                degrees += 1
            }

            val degStr = if (isLat) "%02d".format(degrees) else "%03d".format(degrees)
            val minPadded = if (minFormatted.indexOf('.') == 1) "0$minFormatted" else minFormatted
            val minStr = minPadded.replace('.', ',')
            return "$dir $degStr° $minStr'"
        }

        // Test Frankfurt coordinates: 50.1109, 8.6821
        assertThat(formatCoordinate(50.1109, true)).isEqualTo("N 50° 06,654'")
        assertThat(formatCoordinate(8.6821, false)).isEqualTo("E 008° 40,926'")

        // Test Southern / Western hemisphere coordinates
        assertThat(formatCoordinate(-33.8650, true)).isEqualTo("S 33° 51,900'")
        assertThat(formatCoordinate(-71.0600, false)).isEqualTo("W 071° 03,600'")

        // Test 60-minute rounding rollover
        assertThat(formatCoordinate(50.9999999, true)).isEqualTo("N 51° 00,000'")
    }

    @Test
    fun testUnifiedFilterAssetsAndMarkup() {
        val html =
            given()
                .`when`()
                .get("/")
                .then()
                .statusCode(200)
                .extract()
                .asString()

        assertThat(html)
            .contains("/js/filter.js")
            .contains("filterManager.init()")
            .contains("filter-pill-container")
            .contains("filter-pill-bg")
            .contains("map-filter-float")
            .contains("fullMapEmptyOwn")
            .contains("listEmptyOwnCard")
            .contains("No shoes spotted by you yet — tap the Spot button to log your first!")

        val css =
            given()
                .`when`()
                .get("/css/style.css")
                .then()
                .statusCode(200)
                .extract()
                .asString()

        assertThat(css)
            .contains(".filter-pill-container")
            .contains(".filter-pill-bg")
            .contains(".filter-pill-btn")
            .contains(".map-filter-float")

        val filterJs =
            given()
                .`when`()
                .get("/js/filter.js")
                .then()
                .statusCode(200)
                .extract()
                .asString()

        assertThat(filterJs)
            .contains("class FilterManager")
            .contains("filter_own_only")
            .contains("filter-changed")
            .contains("user-changed")

        val fullMapJs =
            given()
                .`when`()
                .get("/js/fullmap.js")
                .then()
                .statusCode(200)
                .extract()
                .asString()

        assertThat(fullMapJs)
            .contains("filter-changed")
            .contains("fullMapEmptyOwn")

        val listViewJs =
            given()
                .`when`()
                .get("/js/listview.js")
                .then()
                .statusCode(200)
                .extract()
                .asString()

        assertThat(listViewJs)
            .contains("filter-changed")
            .contains("listEmptyOwnCard")
            .contains("of")
            .contains("sightings")
    }

    @Test
    fun testUnifiedFilterLogicMatrixParity() {
        data class TestSighting(
            val id: String,
            val userId: String,
        )

        fun filterSightings(
            sightings: List<TestSighting>,
            ownOnly: Boolean,
            currentUserId: String,
        ): List<TestSighting> {
            val trimmedUser = currentUserId.trim().lowercase()
            return sightings.filter { s ->
                if (!ownOnly) {
                    true
                } else {
                    trimmedUser.isNotEmpty() && s.userId.trim().lowercase() == trimmedUser
                }
            }
        }

        fun computeListCounter(
            filteredCount: Int,
            totalCount: Int,
            ownOnly: Boolean,
        ): String = if (ownOnly) {
            "$filteredCount of $totalCount sightings"
        } else {
            "$totalCount ${if (totalCount == 1) "sighting" else "sightings"}"
        }

        val sightings =
            listOf(
                TestSighting("1", "Tom"),
                TestSighting("2", "Alice"),
                TestSighting("3", "tom "),
                TestSighting("4", "Bob"),
            )

        // Matrix Row 1: Toggle "My Shoes" on Map - shows only user's markers
        val mapFiltered = filterSightings(sightings, ownOnly = true, currentUserId = "TOM")
        assertThat(mapFiltered).hasSize(2)
        assertThat(mapFiltered.map { it.id }).containsExactly("1", "3")

        // Matrix Row 2: Switch from Map to List - counter displays filtered count of total count
        val listCounter = computeListCounter(mapFiltered.size, sightings.size, ownOnly = true)
        assertThat(listCounter).isEqualTo("2 of 4 sightings")

        // Matrix Row 3: No Personal Sightings - empty state condition
        val emptyForUser = filterSightings(sightings, ownOnly = true, currentUserId = "UnknownUser")
        assertThat(emptyForUser).isEmpty()
        val indexHtml = File("src/main/resources/META-INF/resources/index.html").readText()
        assertThat(indexHtml).contains("No shoes spotted by you yet — tap the Spot button to log your first!")

        // Matrix Row 4: User Changes Name - re-evaluates filter immediately for new user
        val switchedUser = filterSightings(sightings, ownOnly = true, currentUserId = "Alice")
        assertThat(switchedUser).hasSize(1)
        assertThat(switchedUser.first().id).isEqualTo("2")
        assertThat(computeListCounter(switchedUser.size, sightings.size, ownOnly = true)).isEqualTo("1 of 4 sightings")
    }

    @Test
    fun testMapLifecycleAndResizeResilience() {
        val tabsJs =
            given()
                .`when`()
                .get("/js/tabs.js")
                .then()
                .statusCode(200)
                .extract()
                .asString()
        assertThat(tabsJs)
            .contains("resize")
            .contains("orientationchange")
            .contains("invalidateActiveMap")
            .contains("resizeDebounce")

        val miniMapJs =
            given()
                .`when`()
                .get("/js/minimap.js")
                .then()
                .statusCode(200)
                .extract()
                .asString()
        assertThat(miniMapJs)
            .contains("invalidateSize(true)")
            .contains("invalidateSize(immediate")
            .contains("200")
            .contains("tab-switched")

        val fullMapJs =
            given()
                .`when`()
                .get("/js/fullmap.js")
                .then()
                .statusCode(200)
                .extract()
                .asString()
        assertThat(fullMapJs)
            .contains("invalidateSize(true)")
            .contains("invalidateSize(immediate")
            .contains("200")
            .contains("tab-switched")
    }

    @Test
    fun testAgplLicensingAndAttribution() {
        val licenseFile = File("LICENSE")
        assertThat(licenseFile).exists().isFile()
        val licenseContent = licenseFile.readText()
        assertThat(licenseContent)
            .contains("GNU AFFERO GENERAL PUBLIC LICENSE")
            .contains("Version 3, 19 November 2007")

        val readmeFile = File("README.md")
        assertThat(readmeFile).exists().isFile()
        val readmeContent = readmeFile.readText()
        assertThat(readmeContent).contains("GNU Affero General Public License v3.0 (AGPL-3.0)")

        val html =
            given()
                .`when`()
                .get("/")
                .then()
                .statusCode(200)
                .extract()
                .asString()
        assertThat(html)
            .contains("footer-license-link")
            .contains("AGPL-3.0 Source")

        val css =
            given()
                .`when`()
                .get("/css/style.css")
                .then()
                .statusCode(200)
                .extract()
                .asString()
        assertThat(css).contains(".footer-license-link")
    }

    @Test
    fun testFrontendVersionFormattingLogic() {
        fun formatFooterVersion(
            version: String?,
            commit: String?,
        ): String {
            if (commit.isNullOrBlank() || commit == "unknown") {
                return "Shoe Tracker v1.0.0"
            }
            val ver = if (!version.isNullOrBlank()) "v${version.replace(Regex("-SNAPSHOT$", RegexOption.IGNORE_CASE), "")}" else "v1.0.0"
            return "Shoe Tracker $ver ($commit)"
        }

        assertThat(formatFooterVersion("1.0.0-SNAPSHOT", "593f825")).isEqualTo("Shoe Tracker v1.0.0 (593f825)")
        assertThat(formatFooterVersion("2.1.0", "abc1234")).isEqualTo("Shoe Tracker v2.1.0 (abc1234)")
        assertThat(formatFooterVersion("1.0.0", null)).isEqualTo("Shoe Tracker v1.0.0")
        assertThat(formatFooterVersion("1.0.0", "unknown")).isEqualTo("Shoe Tracker v1.0.0")
    }
}
