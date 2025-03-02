package com.naiomi.http.interfaces.controller;

import com.naiomi.http.interfaces.clients.UserRestClient;
import com.naiomi.http.interfaces.controllers.UserController;
import com.naiomi.http.interfaces.model.User;
import com.naiomi.http.interfaces.model.Address;
import com.naiomi.http.interfaces.model.Company;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRestClient userRestClient;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    private User createTestUser(Integer id, String name) {
        return new User(
                id,
                name,
                name.toLowerCase(), // username
                name.toLowerCase() + "@example.com", // email
                new Address("Street 123", "Suite 1", "CityX", "12345", null), // Address with null Geo
                "123-456-7890",
                "www." + name.toLowerCase() + ".com",
                new Company(name + " Inc.", "Innovating " + name, "Tech") // Company details
        );
    }

    @Test
    void testFindAll() throws Exception {
        List<User> users = Arrays.asList(
                createTestUser(1, "Alice"),
                createTestUser(2, "Bob")
        );

        when(userRestClient.findAll()).thenReturn(users);

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(users.size()))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[0].email").value("alice@example.com"));

        verify(userRestClient, times(1)).findAll();
    }

    @Test
    void testFindById() throws Exception {
        User user = createTestUser(1, "Alice");
        when(userRestClient.findById(1)).thenReturn(user);

        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));

        verify(userRestClient, times(1)).findById(1);
    }
}
