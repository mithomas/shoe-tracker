package org.shoetracker.api

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces

@Path("/api/test/fault")
class TestFaultResource {
    @GET
    @Path("/unhandled")
    @Produces("application/json")
    fun throwUnhandled(): String = throw RuntimeException("Simulated internal database explosion")
}
