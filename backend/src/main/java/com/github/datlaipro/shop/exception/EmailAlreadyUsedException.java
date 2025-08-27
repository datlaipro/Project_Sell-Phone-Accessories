package com.github.datlaipro.shop.domain.user.exception;

public class EmailAlreadyUsedException extends RuntimeException {
  public EmailAlreadyUsedException(String email) {
    super("Email already in use: " + email);
  }
}
