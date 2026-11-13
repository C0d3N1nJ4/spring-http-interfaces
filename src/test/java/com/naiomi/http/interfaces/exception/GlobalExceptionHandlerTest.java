package com.naiomi.http.interfaces.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

class GlobalExceptionHandlerTest {

  private MockMvc mockMvc;

  @RestController
  static class TestController {

    @GetMapping("/test/user-not-found")
    void throwUserNotFound() {
      throw new UserNotFoundException(123);
    }

    @GetMapping("/test/external-api-error")
    void throwExternalApiError() {
      throw new ExternalApiException("API is down", new Exception("Connection timeout"));
    }

    @GetMapping("/test/http-404")
    void throwHttp404() {
      throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/test/http-500")
    void throwHttp500() {
      throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/test/generic-error")
    void throwGenericError() {
      throw new RuntimeException("Something went wrong");
    }
  }

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
        .setControllerAdvice(GlobalExceptionHandler.class)
        .build();
  }

  @Test
  void shouldHandleUserNotFoundException() throws Exception {
    mockMvc
        .perform(get("/test/user-not-found").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("UserNotFoundException"))
        .andExpect(jsonPath("$.message").value("User not found with id: 123"))
        .andExpect(jsonPath("$.path").value("/test/user-not-found"))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void shouldHandleExternalApiException() throws Exception {
    mockMvc
        .perform(get("/test/external-api-error").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadGateway())
        .andExpect(jsonPath("$.status").value(502))
        .andExpect(jsonPath("$.error").value("ExternalApiException"))
        .andExpect(jsonPath("$.message").value("Failed to communicate with external API: " +
            "API is down"))
        .andExpect(jsonPath("$.path").value("/test/external-api-error"))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void shouldHandleHttpClientErrorException() throws Exception {
    mockMvc
        .perform(get("/test/http-404").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("HttpClientErrorException"))
        .andExpect(jsonPath("$.message").value("External API returned client error: 404 NOT_FOUND"))
        .andExpect(jsonPath("$.path").value("/test/http-404"));
  }

  @Test
  void shouldHandleHttpServerErrorException() throws Exception {
    mockMvc
        .perform(get("/test/http-500").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status").value(500))
        .andExpect(jsonPath("$.error").value("HttpServerErrorException"))
        .andExpect(jsonPath("$.message").value("External API returned server error: " +
            "500 INTERNAL_SERVER_ERROR"))
        .andExpect(jsonPath("$.path").value("/test/http-500"));
  }

  @Test
  void shouldHandleGenericException() throws Exception {
    mockMvc
        .perform(get("/test/generic-error").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.status").value(500))
        .andExpect(jsonPath("$.error").value("InternalServerError"))
        .andExpect(jsonPath("$.message").value(
            "An unexpected error occurred. Please try again later."))
        .andExpect(jsonPath("$.path").value("/test/generic-error"))
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void shouldIncludeTimestampInAllErrors() throws Exception {
    mockMvc
        .perform(get("/test/user-not-found").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.timestamp").isNotEmpty());
  }

  @Test
  void shouldIncludePathInAllErrors() throws Exception {
    mockMvc
        .perform(get("/test/external-api-error").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadGateway())
        .andExpect(jsonPath("$.path").value("/test/external-api-error"));
  }
}
