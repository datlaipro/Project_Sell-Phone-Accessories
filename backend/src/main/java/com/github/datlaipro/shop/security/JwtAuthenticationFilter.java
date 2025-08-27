package com.github.datlaipro.shop.security;

import com.github.datlaipro.shop.domain.user.entity.UserEntity;
import com.github.datlaipro.shop.domain.user.repo.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserRepository userRepo;

  public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepo) {
    this.jwtService = jwtService;
    this.userRepo = userRepo;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain chain) throws ServletException, IOException {
    String token = readCookie(request, "ACCESS_TOKEN").orElse(null);
    if (token != null) {
      try {
        Jws<Claims> jws = jwtService.parse(token);
        String userId = jws.getBody().getSubject();
        Optional<UserEntity> ou = userRepo.findById(Long.valueOf(userId));
        if (ou.isPresent()) {
          UserEntity u = ou.get();
          var auth = new UsernamePasswordAuthenticationToken(
              u.getId(), null,
              Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + u.getRole().toUpperCase()))
          );
          SecurityContextHolder.getContext().setAuthentication(auth);
          response.setHeader(HttpHeaders.VARY, "Cookie");
        }
      } catch (Exception ignored) { /* rơi qua anonymous */ }
    }
    chain.doFilter(request, response);
  }

  private Optional<String> readCookie(HttpServletRequest req, String name) {
    Cookie[] cookies = req.getCookies();
    if (cookies == null) return Optional.empty();
    for (Cookie c : cookies) if (name.equals(c.getName())) return Optional.ofNullable(c.getValue());
    return Optional.empty();
  }
}
