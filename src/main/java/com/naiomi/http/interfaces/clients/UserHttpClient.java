package com.naiomi.http.interfaces.clients;

import com.naiomi.http.interfaces.model.User;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;

public interface UserHttpClient {

    @GetExchange("/users")
    List<User> findAll();

    @GetExchange("/users/{id}")
    User findById(Integer id);
}
