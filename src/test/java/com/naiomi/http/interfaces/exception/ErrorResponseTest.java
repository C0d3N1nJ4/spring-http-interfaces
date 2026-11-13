package com.naiomi.http.interfaces.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class ErrorResponseTest {

  @Test
  void shouldCreateErrorResponseWithFactory() {
    ErrorResponse response = ErrorResponse.of(404, "User not found", "UserNotFoundException",
        "/users/123");

    assertThat(response.status()).isEqualTo(404);
    assertThat(response.message()).isEqualTo("User not found");
    assertThat(response.error()).isEqualTo("UserNotFoundException");
    assertThat(response.path()).isEqualTo("/users/123");
    assertThat(response.timestamp()).isNotNull();
    assertThat(response.traceId()).isNull();
  }

  @Test
  void shouldCreateErrorResponseWithTraceId() {
    String traceId = "trace-123";
    ErrorResponse response = ErrorResponse.of(502, "API error", "ExternalApiException",
        "/users", traceId);

    assertThat(response.status()).isEqualTo(502);
    assertThat(response.message()).isEqualTo("API error");
    assertThat(response.error()).isEqualTo("ExternalApiException");
    assertThat(response.path()).isEqualTo("/users");
    assertThat(response.timestamp()).isNotNull();
    assertThat(response.traceId()).isEqualTo(traceId);
  }

  @Test
  void shouldCreateErrorResponseDirectly() {
    LocalDateTime now = LocalDateTime.now();
    ErrorResponse response = new ErrorResponse(500, "Internal error", "RuntimeException",
        "/endpoint", now, null);

    assertThat(response.status()).isEqualTo(500);
    assertThat(response.message()).isEqualTo("Internal error");
    assertThat(response.error()).isEqualTo("RuntimeException");
    assertThat(response.path()).isEqualTo("/endpoint");
    assertThat(response.timestamp()).isEqualTo(now);
    assertThat(response.traceId()).isNull();
  }

  @Test
  void shouldHaveNonNullTimestamp() {
    ErrorResponse response = ErrorResponse.of(404, "Not found", "NotFoundException",
        "/test");

    assertThat(response.timestamp()).isNotNull();
    assertThat(response.timestamp()).isBeforeOrEqualTo(LocalDateTime.now());
  }

  @Test
  void shouldExcludeNullFieldsInSerialization() {
    ErrorResponse response = ErrorResponse.of(404, "Not found", "NotFoundException",
        "/test");

    assertThat(response.traceId()).isNull();
  }

  @Test
  void shouldSerializeToString() {
    ErrorResponse response = ErrorResponse.of(404, "Not found", "NotFoundException",
        "/users/123");

    String string = response.toString();

    assertThat(string).contains("404");
    assertThat(string).contains("Not found");
    assertThat(string).contains("NotFoundException");
    assertThat(string).contains("/users/123");
  }

  @Test
  void shouldHaveAllRequiredFields() {
    ErrorResponse response = ErrorResponse.of(503, "Service unavailable",
        "ServiceUnavailableException", "/api/users");

    assertThat(response.status()).isPositive();
    assertThat(response.message()).isNotBlank();
    assertThat(response.error()).isNotBlank();
    assertThat(response.path()).isNotBlank();
    assertThat(response.timestamp()).isNotNull();
  }
}
