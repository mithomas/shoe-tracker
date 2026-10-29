package org.shoetracker.api

import com.fasterxml.jackson.core.JsonProcessingException
import jakarta.ws.rs.ProcessingException
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import org.shoetracker.api.dto.ProblemDetails
import org.sqlite.SQLiteErrorCode
import org.sqlite.SQLiteException

@Provider
class IllegalArgumentExceptionMapper : ExceptionMapper<IllegalArgumentException> {
    override fun toResponse(exception: IllegalArgumentException): Response {
        val problem =
            ProblemDetails(
                title = "Bad Request",
                status = Response.Status.BAD_REQUEST.statusCode,
                detail = exception.message ?: "Invalid request parameters.",
                instance = "/api/v1/sightings",
            )
        return Response
            .status(Response.Status.BAD_REQUEST)
            .type("application/problem+json")
            .entity(problem)
            .build()
    }
}

@Provider
class JsonProcessingExceptionMapper : ExceptionMapper<JsonProcessingException> {
    override fun toResponse(exception: JsonProcessingException): Response {
        val problem =
            ProblemDetails(
                title = "Bad Request",
                status = Response.Status.BAD_REQUEST.statusCode,
                detail = "Malformed JSON payload: ${exception.originalMessage}",
                instance = "/api/v1/sightings",
            )
        return Response
            .status(Response.Status.BAD_REQUEST)
            .type("application/problem+json")
            .entity(problem)
            .build()
    }
}

@Provider
class ProcessingExceptionMapper : ExceptionMapper<ProcessingException> {
    override fun toResponse(exception: ProcessingException): Response {
        val problem =
            ProblemDetails(
                title = "Bad Request",
                status = Response.Status.BAD_REQUEST.statusCode,
                detail = "Malformed JSON payload: ${exception.cause?.message ?: exception.message ?: "Invalid syntax"}",
                instance = "/api/v1/sightings",
            )
        return Response
            .status(Response.Status.BAD_REQUEST)
            .type("application/problem+json")
            .entity(problem)
            .build()
    }
}

@Provider
class WebApplicationExceptionMapper : ExceptionMapper<WebApplicationException> {
    override fun toResponse(exception: WebApplicationException): Response {
        val status = exception.response?.status ?: Response.Status.BAD_REQUEST.statusCode
        val problem =
            ProblemDetails(
                title = exception.response?.statusInfo?.reasonPhrase ?: "Client Error",
                status = status,
                detail = exception.message ?: "Invalid request parameter.",
                instance = "/api/v1/sightings",
            )
        return Response
            .status(status)
            .type("application/problem+json")
            .entity(problem)
            .build()
    }
}

@Provider
class SQLiteExceptionMapper : ExceptionMapper<SQLiteException> {
    private val logger = org.jboss.logging.Logger.getLogger(SQLiteExceptionMapper::class.java)

    override fun toResponse(exception: SQLiteException): Response {
        val errorId = java.util.UUID.randomUUID().toString()
        val isBusyOrLocked = isBusyOrLocked(exception.resultCode)

        if (isBusyOrLocked) {
            logger.warn("SQLite lock contention [error_id=$errorId, code=${exception.resultCode}]: ${exception.message}")
            val problem =
                ProblemDetails(
                    title = "Service Unavailable",
                    status = Response.Status.SERVICE_UNAVAILABLE.statusCode,
                    detail = "Database is temporarily busy. Please retry in 1 second.",
                    instance = "/api/v1/sightings",
                    errorId = errorId,
                )
            return Response
                .status(Response.Status.SERVICE_UNAVAILABLE)
                .header("Retry-After", "1")
                .type("application/problem+json")
                .entity(problem)
                .build()
        } else {
            logger.error(
                "SQLite database error [error_id=$errorId, code=${exception.resultCode}]: ${exception.message}",
                exception,
            )
            val problem =
                ProblemDetails(
                    title = "Internal Server Error",
                    status = Response.Status.INTERNAL_SERVER_ERROR.statusCode,
                    detail = "A database error occurred. Please reference error ID: $errorId",
                    instance = "/api/v1/sightings",
                    errorId = errorId,
                )
            return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .type("application/problem+json")
                .entity(problem)
                .build()
        }
    }

    private fun isBusyOrLocked(code: SQLiteErrorCode?): Boolean {
        if (code == null) return false
        val name = code.name
        return name.startsWith("SQLITE_BUSY") || name.startsWith("SQLITE_LOCKED")
    }
}

@Provider
class ThrowableExceptionMapper : ExceptionMapper<Throwable> {
    private val logger = org.jboss.logging.Logger.getLogger(ThrowableExceptionMapper::class.java)

    override fun toResponse(exception: Throwable): Response {
        val errorId = java.util.UUID.randomUUID().toString()
        logger.error("Unhandled server exception [error_id=$errorId]: ${exception.message}", exception)

        val problem =
            ProblemDetails(
                title = "Internal Server Error",
                status = Response.Status.INTERNAL_SERVER_ERROR.statusCode,
                detail = "An unexpected server error occurred. Please reference error ID: $errorId",
                instance = "/api/v1/sightings",
                errorId = errorId,
            )
        return Response
            .status(Response.Status.INTERNAL_SERVER_ERROR)
            .type("application/problem+json")
            .entity(problem)
            .build()
    }
}
