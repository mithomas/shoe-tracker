package org.shoetracker.api

import com.fasterxml.jackson.core.JsonProcessingException
import jakarta.ws.rs.ProcessingException
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import org.shoetracker.api.dto.ProblemDetails
import java.sql.SQLException
import java.util.ArrayDeque
import java.util.Collections
import java.util.IdentityHashMap

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
class DatabaseContentionExceptionMapper : ExceptionMapper<SQLException> {
    private val logger = org.jboss.logging.Logger.getLogger(DatabaseContentionExceptionMapper::class.java)

    override fun toResponse(exception: SQLException): Response {
        val errorId = java.util.UUID.randomUUID().toString()
        val contentionEx = findContentionException(exception)

        if (contentionEx != null) {
            return buildContentionResponse(contentionEx, errorId)
        } else {
            logger.error(
                "Database error [error_id=$errorId, sqlState=${exception.sqlState}, " +
                    "code=${exception.errorCode}]: ${exception.message}",
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

    companion object {
        private val logger = org.jboss.logging.Logger.getLogger(DatabaseContentionExceptionMapper::class.java)
        private val CONTENTION_SQL_STATES = setOf("40001", "40P01", "57P01", "HYT00")
        private val CONTENTION_ERROR_CODES = setOf(50200, 90039, 90072, 1205, 1213)
        private const val MAX_TRAVERSAL_NODES = 16

        fun findContentionException(root: Throwable?): SQLException? {
            if (root == null) return null
            val queue = ArrayDeque<Throwable>()
            val visited = Collections.newSetFromMap(IdentityHashMap<Throwable, Boolean>())
            queue.add(root)
            visited.add(root)
            var inspected = 0

            while (queue.isNotEmpty() && inspected < MAX_TRAVERSAL_NODES) {
                val current = queue.removeFirst()
                inspected++
                if (current is SQLException && isBusyOrLocked(current)) {
                    return current
                }
                if (current is SQLException) {
                    val next = current.nextException
                    if (next != null && visited.add(next)) {
                        queue.addLast(next)
                    }
                }
                val cause = current.cause
                if (cause != null && visited.add(cause)) {
                    queue.addLast(cause)
                }
            }
            return null
        }

        fun buildContentionResponse(
            contentionEx: SQLException,
            errorId: String = java.util.UUID.randomUUID().toString(),
        ): Response {
            logger.warn(
                "Database lock contention [error_id=$errorId, sqlState=${contentionEx.sqlState}, " +
                    "code=${contentionEx.errorCode}]: ${contentionEx.message}",
            )
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
        }

        private fun isBusyOrLocked(exception: SQLException): Boolean = exception.sqlState in CONTENTION_SQL_STATES ||
            exception.errorCode in CONTENTION_ERROR_CODES
    }
}

@Provider
class ThrowableExceptionMapper : ExceptionMapper<Throwable> {
    private val logger = org.jboss.logging.Logger.getLogger(ThrowableExceptionMapper::class.java)

    override fun toResponse(exception: Throwable): Response {
        val contentionEx = DatabaseContentionExceptionMapper.findContentionException(exception)
        if (contentionEx != null) {
            return DatabaseContentionExceptionMapper.buildContentionResponse(contentionEx)
        }

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
