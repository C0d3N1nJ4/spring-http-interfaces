package com.naiomi.http.interfaces.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import com.naiomi.http.interfaces.clients.UserHttpClient;
import com.naiomi.http.interfaces.exception.ErrorResponse;
import com.naiomi.http.interfaces.exception.UserNotFoundException;
import com.naiomi.http.interfaces.model.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "User management API endpoints")
public class UserController {

  private final UserHttpClient userHttpClient;

  public UserController(UserHttpClient userHttpClient) {
    this.userHttpClient = userHttpClient;
  }

  @GetMapping
  @Operation(summary = "List all users", description = "Retrieves a list of all users")
  @ApiResponse(responseCode = "200", description = "Successfully retrieved users", content = @Content(
      schema = @Schema(implementation = User.class)))
  @ApiResponse(responseCode = "502", description = "External API error", content = @Content(
      schema = @Schema(implementation = ErrorResponse.class)))
  public List<User> findAll() {
    try {
      return userHttpClient.findAll();
    } catch (HttpClientErrorException | HttpServerErrorException ex) {
      throw new com.naiomi.http.interfaces.exception.ExternalApiException(
          "Failed to fetch users from external API: " + ex.getStatusCode(), ex);
    }
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get user by ID", description = "Retrieves a specific user by their ID")
  @ApiResponse(responseCode = "200", description = "User found", content = @Content(
      schema = @Schema(implementation = User.class)))
  @ApiResponse(responseCode = "404", description = "User not found", content = @Content(
      schema = @Schema(implementation = ErrorResponse.class)))
  @ApiResponse(responseCode = "502", description = "External API error", content = @Content(
      schema = @Schema(implementation = ErrorResponse.class)))
  public User findById(
      @Parameter(description = "User ID", required = true) @PathVariable Integer id) {
    if (id == null || id <= 0) {
      throw new UserNotFoundException("Invalid user ID: " + id);
    }
    try {
      User user = userHttpClient.findById(id);
      if (user == null) {
        throw new UserNotFoundException(id);
      }
      return user;
    } catch (HttpClientErrorException ex) {
      if (ex.getStatusCode().value() == 404) {
        throw new UserNotFoundException(id);
      }
      throw new com.naiomi.http.interfaces.exception.ExternalApiException(
          "Failed to fetch user from external API: " + ex.getStatusCode(), ex);
    } catch (HttpServerErrorException ex) {
      throw new com.naiomi.http.interfaces.exception.ExternalApiException(
          "External API server error: " + ex.getStatusCode(), ex);
    }
  }
}
