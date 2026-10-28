package org.shoetracker.domain

import java.time.Instant

data class GeoLocation(
    val latitude: Double,
    val longitude: Double
) {
    init {
        require(!latitude.isNaN() && !latitude.isInfinite() && latitude in -90.0..90.0) {
            "Latitude must be between -90 and 90 degrees."
        }
        require(!longitude.isNaN() && !longitude.isInfinite() && longitude in -180.0..180.0) {
            "Longitude must be between -180 and 180 degrees."
        }
    }
}

data class Sighting(
    val id: String,
    val userId: String,
    val location: GeoLocation,
    val recordedAt: Instant,
    val createdAt: Instant = Instant.now()
) {
    init {
        require(userId.isNotBlank()) {
            "user_id must not be blank."
        }
        require(userId.length <= 64) {
            "user_id must not exceed 64 characters."
        }
    }
}
