package com.naiomi.http.interfaces.controllers;

import com.naiomi.http.interfaces.clients.UserRestClient;
import com.naiomi.http.interfaces.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static com.naiomi.http.interfaces.support.UserFixtures.user;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRestClient userRestClient;

    @BeforeEach
    void setUp() {
        UserController userController = new UserController(userRestClient);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void shouldReturnAllUsers() throws Exception {
        List<User> users = List.of(user(1, "Alice"), user(2, "Bob"));
        when(userRestClient.findAll()).thenReturn(users);

        mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(users.size()))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[0].email").value("alice@example.com"));

        verify(userRestClient, times(1)).findAll();
    }

    @Test
    void shouldReturnUserById() throws Exception {
        User oneUser = user(1, "Alice");
        when(userRestClient.findById(1)).thenReturn(oneUser);

        mockMvc.perform(get("/users/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));

        verify(userRestClient, times(1)).findById(1);
    }
}