package org.shoetracker.api

import io.quarkus.runtime.annotations.RegisterForReflection
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import java.util.Properties

@RegisterForReflection
data class VersionResponse(
    val version: String,
    val commit: String,
)

@Path("/api/v1/version")
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
class VersionResource {
    private val versionInfo: VersionResponse by lazy {
        val props = Properties()
        val stream = javaClass.classLoader.getResourceAsStream("version.properties")
        if (stream != null) {
            stream.use { props.load(it) }
        }
        val ver = props.getProperty("version")?.trim()?.takeIf { it.isNotEmpty() } ?: "1.0.0"
        val com = props.getProperty("commit")?.trim()?.takeIf { it.isNotEmpty() } ?: "unknown"
        VersionResponse(
            version = ver,
            commit = com,
        )
    }

    @GET
    fun getVersion(): VersionResponse = versionInfo
}
