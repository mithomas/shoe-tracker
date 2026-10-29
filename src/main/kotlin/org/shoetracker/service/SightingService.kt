package org.shoetracker.service

import jakarta.enterprise.context.ApplicationScoped
import org.shoetracker.api.dto.CreateSightingRequest
import org.shoetracker.api.dto.GeoLocationDto
import org.shoetracker.api.dto.SightingListResponse
import org.shoetracker.api.dto.SightingResponse
import org.shoetracker.api.dto.UserDto
import org.shoetracker.domain.GeoLocation
import org.shoetracker.domain.Sighting
import org.shoetracker.domain.UUIDv7
import org.shoetracker.repository.SightingRepository

@ApplicationScoped
class SightingService(
    private val repository: SightingRepository,
) {
    fun recordSighting(request: CreateSightingRequest?): SightingResponse {
        requireNotNull(request) { "Request body must not be null." }
        requireNotNull(request.userId) { "user_id is required." }
        val trimmedUserId = request.userId.trim()
        require(trimmedUserId.isNotBlank()) { "user_id must not be blank." }
        require(trimmedUserId.length <= 64) { "user_id must not exceed 64 characters." }

        requireNotNull(request.location) { "location is required." }
        val location =
            GeoLocation(
                latitude = request.location.latitude,
                longitude = request.location.longitude,
            )

        requireNotNull(request.recordedAt) { "recorded_at is required." }

        val sighting =
            Sighting(
                id = UUIDv7.generate().toString(),
                userId = trimmedUserId,
                location = location,
                recordedAt = request.recordedAt,
            )

        val saved = repository.insert(sighting)
        return saved.toResponse()
    }

    fun listSightings(
        userId: String?,
        limit: Int,
        offset: Int,
    ): SightingListResponse {
        require(limit in 1..500) { "limit must be between 1 and 500." }
        require(offset >= 0) { "offset must be non-negative." }

        val trimmedUserId = userId?.trim()?.ifBlank { null }
        val items = repository.findSightings(trimmedUserId, limit, offset).map { it.toResponse() }
        val total = repository.countSightings(trimmedUserId)

        return SightingListResponse(
            items = items,
            total = total,
            limit = limit,
            offset = offset,
        )
    }

    fun deleteSighting(id: String): Boolean {
        require(id.isNotBlank()) { "id must not be blank." }
        return repository.deleteById(id.trim())
    }

    private fun Sighting.toResponse(): SightingResponse = SightingResponse(
        id = id,
        user = UserDto(id = userId),
        location =
        GeoLocationDto(
            latitude = location.latitude,
            longitude = location.longitude,
        ),
        recordedAt = recordedAt,
        createdAt = createdAt,
    )
}
