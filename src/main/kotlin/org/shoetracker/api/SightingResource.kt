package org.shoetracker.api

import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.shoetracker.api.dto.CreateSightingRequest
import org.shoetracker.api.dto.SightingListResponse
import org.shoetracker.domain.SightingService

@Path("/api/v1/sightings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class SightingResource(private val service: SightingService) {

    @POST
    fun createSighting(request: CreateSightingRequest?): Response {
        val created = service.recordSighting(request)
        return Response.status(Response.Status.CREATED).entity(created).build()
    }

    @GET
    fun getSightings(
        @QueryParam("user_id") userId: String?,
        @QueryParam("limit") @DefaultValue("100") limit: Int,
        @QueryParam("offset") @DefaultValue("0") offset: Int
    ): SightingListResponse {
        return service.listSightings(userId, limit, offset)
    }

    @DELETE
    @Path("/{id}")
    fun deleteSighting(@PathParam("id") id: String): Response {
        val deleted = service.deleteSighting(id)
        return if (deleted) {
            Response.noContent().build()
        } else {
            Response.status(Response.Status.NOT_FOUND).build()
        }
    }
}
