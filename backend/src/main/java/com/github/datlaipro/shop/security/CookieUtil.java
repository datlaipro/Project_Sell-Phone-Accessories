package com.github.datlaipro.shop.security;

import org.springframework.http.ResponseCookie;

import java.time.Duration;

public class CookieUtil {
  private final String domain;
  private final boolean secure;
  private final String sameSite; // Lax, Strict, None

  public CookieUtil(String domain, boolean secure, String sameSite) {
    this.domain = domain; this.secure = secure; this.sameSite = sameSite;
  }

  public ResponseCookie build(String name, String value, Duration maxAge) {
    return ResponseCookie.from(name, value)
        .httpOnly(true)
        .secure(secure)
        .sameSite(sameSite)
        .path("/")
        .domain(domain)
        .maxAge(maxAge)
        .build();
  }

  public ResponseCookie clear(String name) {
    return ResponseCookie.from(name, "")
        .httpOnly(true)
        .secure(secure)
        .sameSite(sameSite)
        .path("/")
        .domain(domain)
        .maxAge(Duration.ZERO)
        .build();
  }
}
