package org.shoetracker.api.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class GeoLocationDto(
    val latitude: Double,
    val longitude: Double,
)

data class UserDto(
    val id: String,
)

data class CreateSightingRequest(
    @JsonProperty("user_id")
    val userId: String?,
    val location: GeoLocationDto?,
    @JsonProperty("recorded_at")
    val recordedAt: Instant?,
)

data class SightingResponse(
    val id: String,
    val user: UserDto,
    val location: GeoLocationDto,
    @JsonProperty("recorded_at")
    val recordedAt: Instant,
    @JsonProperty("created_at")
    val createdAt: Instant,
)

data class SightingListResponse(
    val items: List<SightingResponse>,
    val total: Long,
    val limit: Int,
    val offset: Int,
)

data class ProblemDetails(
    val type: String = "about:blank",
    val title: String,
    val status: Int,
    val detail: String,
    val instance: String? = null,
)
