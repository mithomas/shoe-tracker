package org.shoetracker.api

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import java.sql.SQLException

@Path("/api/test/fault")
class TestFaultResource {
    @GET
    @Path("/unhandled")
    @Produces("application/json")
    fun throwUnhandled(): String = throw RuntimeException("Simulated internal database explosion")

    @GET
    @Path("/db-busy")
    @Produces("application/json")
    fun throwDatabaseBusy(): String = throw SQLException("database is locked", "40001", 50200)

    @GET
    @Path("/db-error")
    @Produces("application/json")
    fun throwDatabaseError(): String = throw SQLException("database disk image is malformed", "HY000", 90030)
}
