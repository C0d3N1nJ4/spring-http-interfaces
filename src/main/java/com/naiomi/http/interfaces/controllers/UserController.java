package com.naiomi.http.interfaces.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.naiomi.http.interfaces.clients.UserHttpClient;
import com.naiomi.http.interfaces.model.User;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserHttpClient userHttpClient;

  public UserController(UserHttpClient userHttpClient) {
    this.userHttpClient = userHttpClient;
  }

  @GetMapping
  public List<User> findAll() {
    return userHttpClient.findAll();
  }

  @GetMapping("/{id}")
  public User findById(@PathVariable Integer id) {
    return userHttpClient.findById(id);
  }
}
