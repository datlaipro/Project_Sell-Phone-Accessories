package com.github.datlaipro.shop.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class JwtService {
  private final Key key;
  private final String issuer;

  public JwtService(String secret, String issuer) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes());
    this.issuer = issuer;
  }

  public String createToken(String subject, Map<String, Object> claims, long expiresAtEpochSeconds) {
    return Jwts.builder()
        .setIssuer(issuer)
        .setSubject(subject) // ví dụ userId
        .addClaims(claims)
        .setExpiration(new Date(expiresAtEpochSeconds * 1000))
        .setIssuedAt(new Date())
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public Jws<Claims> parse(String jwt) {
    return Jwts.parserBuilder().setSigningKey(key).requireIssuer(issuer).build().parseClaimsJws(jwt);
  }

  public boolean isExpired(Jws<Claims> jws) {
    Date exp = jws.getBody().getExpiration();
    return exp == null || exp.toInstant().isBefore(Instant.now());
  }
}
