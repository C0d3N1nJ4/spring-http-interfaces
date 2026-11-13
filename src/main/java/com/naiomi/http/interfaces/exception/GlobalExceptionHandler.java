package com.naiomi.http.interfaces.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
@Hidden
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(
      UserNotFoundException ex,
      HttpServletRequest request) {
    logger.warn("User not found: {}", ex.getMessage());
    ErrorResponse errorResponse = ErrorResponse.of(
        HttpStatus.NOT_FOUND.value(),
        ex.getMessage(),
        "UserNotFoundException",
        request.getRequestURI());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(ExternalApiException.class)
  public ResponseEntity<ErrorResponse> handleExternalApiException(
      ExternalApiException ex,
      HttpServletRequest request) {
    logger.error("External API error: {}", ex.getMessage(), ex);
    ErrorResponse errorResponse = ErrorResponse.of(
        HttpStatus.BAD_GATEWAY.value(),
        "Failed to communicate with external API: " + ex.getMessage(),
        "ExternalApiException",
        request.getRequestURI());
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(errorResponse);
  }

  @ExceptionHandler(HttpClientErrorException.class)
  public ResponseEntity<ErrorResponse> handleHttpClientError(
      HttpClientErrorException ex,
      HttpServletRequest request) {
    logger.error("HTTP client error: {} - {}", ex.getStatusCode(), ex.getMessage());
    ErrorResponse errorResponse = ErrorResponse.of(
        ex.getStatusCode().value(),
        "External API returned client error: " + ex.getStatusCode(),
        "HttpClientErrorException",
        request.getRequestURI());
    return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
  }

  @ExceptionHandler(HttpServerErrorException.class)
  public ResponseEntity<ErrorResponse> handleHttpServerError(
      HttpServerErrorException ex,
      HttpServletRequest request) {
    logger.error("HTTP server error: {} - {}", ex.getStatusCode(), ex.getMessage());
    ErrorResponse errorResponse = ErrorResponse.of(
        ex.getStatusCode().value(),
        "External API returned server error: " + ex.getStatusCode(),
        "HttpServerErrorException",
        request.getRequestURI());
    return ResponseEntity.status(ex.getStatusCode()).body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(
      Exception ex,
      HttpServletRequest request) {
    logger.error("Unexpected error occurred", ex);
    ErrorResponse errorResponse = ErrorResponse.of(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "An unexpected error occurred. Please try again later.",
        "InternalServerError",
        request.getRequestURI());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}
