package com.naiomi.http.interfaces.exception;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(Integer userId) {
    super("User not found with id: " + userId);
  }

  public UserNotFoundException(String message) {
    super(message);
  }
}
