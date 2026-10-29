package org.shoetracker.api

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import org.sqlite.SQLiteErrorCode
import org.sqlite.SQLiteException

@Path("/api/test/fault")
class TestFaultResource {
    @GET
    @Path("/unhandled")
    @Produces("application/json")
    fun throwUnhandled(): String = throw RuntimeException("Simulated internal database explosion")

    @GET
    @Path("/sqlite-busy")
    @Produces("application/json")
    fun throwSqliteBusy(): String = throw SQLiteException("database is locked", SQLiteErrorCode.SQLITE_BUSY)

    @GET
    @Path("/sqlite-error")
    @Produces("application/json")
    fun throwSqliteError(): String = throw SQLiteException("database disk image is malformed", SQLiteErrorCode.SQLITE_CORRUPT)
}
