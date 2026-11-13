package com.naiomi.http.interfaces.controllers;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class HomeController {

  @GetMapping("/")
  @Hidden
  public void home(HttpServletResponse response) throws IOException {
    response.sendRedirect("swagger-ui.html");
  }
}

