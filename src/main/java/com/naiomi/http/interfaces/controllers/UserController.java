package com.naiomi.http.interfaces.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.naiomi.http.interfaces.clients.UserHttpClient;
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
  public List<User> findAll() {
    return userHttpClient.findAll();
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get user by ID", description = "Retrieves a specific user by their ID")
  @ApiResponse(responseCode = "200", description = "User found", content = @Content(
      schema = @Schema(implementation = User.class)))
  @ApiResponse(responseCode = "404", description = "User not found")
  public User findById(
      @Parameter(description = "User ID", required = true) @PathVariable Integer id) {
    return userHttpClient.findById(id);
  }
}
