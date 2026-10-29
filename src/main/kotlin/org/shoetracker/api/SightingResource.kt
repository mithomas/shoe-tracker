package org.shoetracker.api

import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.DefaultValue
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.shoetracker.api.dto.CreateSightingRequest
import org.shoetracker.api.dto.SightingListResponse
import org.shoetracker.service.SightingService

@Path("/api/v1/sightings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class SightingResource(
    private val service: SightingService,
) {
    @POST
    fun createSighting(request: CreateSightingRequest?): Response {
        val created = service.recordSighting(request)
        return Response.status(Response.Status.CREATED).entity(created).build()
    }

    @GET
    fun getSightings(
        @QueryParam("user_id") userId: String?,
        @QueryParam("limit") @DefaultValue("100") limit: Int,
        @QueryParam("offset") @DefaultValue("0") offset: Int,
    ): SightingListResponse = service.listSightings(userId, limit, offset)

    @DELETE
    @Path("/{id}")
    fun deleteSighting(
        @PathParam("id") id: String,
    ): Response {
        val deleted = service.deleteSighting(id)
        return if (deleted) {
            Response.noContent().build()
        } else {
            Response.status(Response.Status.NOT_FOUND).build()
        }
    }
}
