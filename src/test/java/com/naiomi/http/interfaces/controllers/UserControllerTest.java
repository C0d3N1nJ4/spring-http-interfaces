package com.naiomi.http.interfaces.controllers;

import static com.naiomi.http.interfaces.support.UserFixtures.user;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import com.naiomi.http.interfaces.clients.UserHttpClient;
import com.naiomi.http.interfaces.exception.GlobalExceptionHandler;
import com.naiomi.http.interfaces.model.User;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  private MockMvc mockMvc;

  @Mock private UserHttpClient userHttpClient;

  @BeforeEach
  void setUp() {
    UserController userController = new UserController(userHttpClient);
    mockMvc = MockMvcBuilders.standaloneSetup(userController)
        .setControllerAdvice(GlobalExceptionHandler.class)
        .build();
  }

  @Test
  void shouldReturnAllUsers() throws Exception {
    List<User> users = List.of(user(1, "Alice"), user(2, "Bob"));
    when(userHttpClient.findAll()).thenReturn(users);

    mockMvc
        .perform(get("/users").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(users.size()))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("Alice"))
        .andExpect(jsonPath("$[0].email").value("alice@example.com"));

    verify(userHttpClient, times(1)).findAll();
  }

  @Test
  void shouldReturnUserById() throws Exception {
    User oneUser = user(1, "Alice");
    when(userHttpClient.findById(1)).thenReturn(oneUser);

    mockMvc
        .perform(get("/users/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Alice"))
        .andExpect(jsonPath("$.email").value("alice@example.com"));

    verify(userHttpClient, times(1)).findById(1);
  }

  @Test
  void shouldReturn404WhenUserNotFound() throws Exception {
    when(userHttpClient.findById(999))
        .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

    mockMvc
        .perform(get("/users/999").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("UserNotFoundException"))
        .andExpect(jsonPath("$.message").exists())
        .andExpect(jsonPath("$.path").value("/users/999"))
        .andExpect(jsonPath("$.timestamp").exists());

    verify(userHttpClient, times(1)).findById(999);
  }

  @Test
  void shouldReturn502WhenExternalApiServerError() throws Exception {
    when(userHttpClient.findAll())
        .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

    mockMvc
        .perform(get("/users").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadGateway())
        .andExpect(jsonPath("$.status").value(502))
        .andExpect(jsonPath("$.error").value("ExternalApiException"))
        .andExpect(jsonPath("$.message").value("Failed to communicate with external API: " +
            "Failed to fetch users from external API: 500 INTERNAL_SERVER_ERROR"))
        .andExpect(jsonPath("$.path").value("/users"))
        .andExpect(jsonPath("$.timestamp").exists());

    verify(userHttpClient, times(1)).findAll();
  }

  @Test
  void shouldReturn502WhenFindByIdExternalApiServerError() throws Exception {
    when(userHttpClient.findById(1))
        .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

    mockMvc
        .perform(get("/users/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadGateway())
        .andExpect(jsonPath("$.status").value(502))
        .andExpect(jsonPath("$.error").value("ExternalApiException"))
        .andExpect(jsonPath("$.message").exists())
        .andExpect(jsonPath("$.path").value("/users/1"))
        .andExpect(jsonPath("$.timestamp").exists());

    verify(userHttpClient, times(1)).findById(1);
  }

  @Test
  void shouldReturn404WhenFindByIdWithInvalidId() throws Exception {
    mockMvc
        .perform(get("/users/0").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("UserNotFoundException"))
        .andExpect(jsonPath("$.message").value("Invalid user ID: 0"))
        .andExpect(jsonPath("$.path").value("/users/0"));
  }

  @Test
  void shouldReturn404WhenFindByIdWithNegativeId() throws Exception {
    mockMvc
        .perform(get("/users/-5").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("UserNotFoundException"))
        .andExpect(jsonPath("$.message").value("Invalid user ID: -5"))
        .andExpect(jsonPath("$.path").value("/users/-5"));
  }

  @Test
  void shouldReturn502WhenExternalApiClientError() throws Exception {
    when(userHttpClient.findAll())
        .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

    mockMvc
        .perform(get("/users").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadGateway())
        .andExpect(jsonPath("$.status").value(502))
        .andExpect(jsonPath("$.error").value("ExternalApiException"))
        .andExpect(jsonPath("$.message").exists())
        .andExpect(jsonPath("$.path").value("/users"));
  }

  @Test
  void shouldReturnErrorResponseWithCorrectFields() throws Exception {
    when(userHttpClient.findById(999))
        .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

    mockMvc
        .perform(get("/users/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").isNumber())
        .andExpect(jsonPath("$.message").isString())
        .andExpect(jsonPath("$.error").isString())
        .andExpect(jsonPath("$.path").isString())
        .andExpect(jsonPath("$.timestamp").exists());
  }
}
