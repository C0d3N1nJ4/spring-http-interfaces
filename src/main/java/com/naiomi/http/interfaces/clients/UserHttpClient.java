package com.naiomi.http.interfaces.clients;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.naiomi.http.interfaces.model.User;

@HttpExchange("/users")
public interface UserHttpClient {

  @GetExchange
  List<User> findAll();

  @GetExchange("/{id}")
  User findById(@PathVariable("id") Integer id);
}
