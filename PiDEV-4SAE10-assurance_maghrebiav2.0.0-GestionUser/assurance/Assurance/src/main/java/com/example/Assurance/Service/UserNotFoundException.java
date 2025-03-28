package com.example.Assurance.Service;

public class UserNotFoundException extends RuntimeException {

  // Constructor that accepts a message
  public UserNotFoundException(String message) {
    super(message);
  }

  // Optional: Constructor that accepts a message and a cause (for nested exceptions)
  public UserNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
