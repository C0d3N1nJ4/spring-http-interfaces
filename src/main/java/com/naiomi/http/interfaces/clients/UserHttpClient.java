package com.naiomi.http.interfaces.clients;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.naiomi.http.interfaces.model.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@HttpExchange("/users")
@Tag(name = "Users", description = "User management endpoints")
public interface UserHttpClient {

  @GetExchange
  @Operation(summary = "Get all users", description = "Retrieves a list of all users from the external API")
  @ApiResponse(responseCode = "200", description = "Successfully retrieved users", content = @Content(
      schema = @Schema(implementation = User.class)))
  List<User> findAll();

  @GetExchange("/{id}")
  @Operation(summary = "Get user by ID", description = "Retrieves a specific user by their ID from the external API")
  @ApiResponse(responseCode = "200", description = "User found", content = @Content(
      schema = @Schema(implementation = User.class)))
  @ApiResponse(responseCode = "404", description = "User not found")
  User findById(
      @Parameter(description = "User ID", required = true) @PathVariable("id") Integer id);
}
