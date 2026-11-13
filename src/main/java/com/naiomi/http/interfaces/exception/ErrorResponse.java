package com.naiomi.http.interfaces.exception;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ErrorResponse", description = "Standard error response")
public record ErrorResponse(
    @Schema(description = "HTTP status code", example = "404") int status,
    @Schema(description = "Error message", example = "User not found with id: 123") String message,
    @Schema(description = "Error type", example = "UserNotFoundException") String error,
    @Schema(description = "Request path", example = "/users/123") String path,
    @Schema(description = "Timestamp of error", example = "2026-11-13T21:32:24.512+00:00") LocalDateTime timestamp,
    @Schema(description = "Trace ID for debugging", example = "8b1d7c9e-4f2a-11e7-a919-92ebcb67fe33") String traceId) {

  public ErrorResponse {
    if (timestamp == null) {
      timestamp = LocalDateTime.now();
    }
  }

  public static ErrorResponse of(
      int status,
      String message,
      String error,
      String path,
      String traceId) {
    return new ErrorResponse(status, message, error, path, LocalDateTime.now(), traceId);
  }

  public static ErrorResponse of(
      int status,
      String message,
      String error,
      String path) {
    return of(status, message, error, path, null);
  }
}
