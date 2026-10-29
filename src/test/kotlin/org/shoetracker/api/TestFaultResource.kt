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
    @Path("/sqlite-busy")
    @Produces("application/json")
    fun throwSqliteBusy(): String = throw SQLException("database is locked", "40001", 50200)

    @GET
    @Path("/sqlite-error")
    @Produces("application/json")
    fun throwSqliteError(): String = throw SQLException("database disk image is malformed", "HY000", 90030)
}
