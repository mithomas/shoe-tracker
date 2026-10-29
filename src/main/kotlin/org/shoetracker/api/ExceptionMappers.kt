package org.shoetracker.api

import com.fasterxml.jackson.core.JsonProcessingException
import jakarta.ws.rs.ProcessingException
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import org.shoetracker.api.dto.ProblemDetails

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
