package com.naiomi.http.interfaces.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class HomeController {

  @GetMapping("/")
  @Hidden
  public RedirectView home(HttpServletRequest request) {
    String redirectUrl = request.getRequestURL().toString();
    redirectUrl = redirectUrl.replaceFirst("/$", "") + "/swagger-ui.html";
    RedirectView redirectView = new RedirectView(redirectUrl);
    redirectView.setExposeModelAttributes(false);
    return redirectView;
  }
}

