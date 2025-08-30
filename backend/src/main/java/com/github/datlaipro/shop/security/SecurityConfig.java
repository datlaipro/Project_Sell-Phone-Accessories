package com.github.datlaipro.shop.config;

import com.github.datlaipro.shop.domain.user.repo.UserRepository;
import com.github.datlaipro.shop.security.CookieUtil;
import com.github.datlaipro.shop.security.JwtAuthenticationFilter;
import com.github.datlaipro.shop.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

// === bổ sung import cho admin ===
import org.springframework.core.annotation.Order;
import com.github.datlaipro.shop.domain.admin.login.repo.AdminRefreshTokenRepository;
import com.github.datlaipro.shop.security.JwtAdminAuthenticationFilter;

@Configuration
public class SecurityConfig {

  @Value("${app.jwt.secret}")
  private String secret;
  @Value("${app.jwt.issuer}")
  private String issuer;

  @Value("${app.cookie.domain}")
  private String cookieDomain;
  @Value("${app.cookie.secure:true}")
  private boolean cookieSecure;
  @Value("${app.cookie.same-site:Lax}")
  private String sameSite;

  // (tùy chọn) cấu hình origin FE qua properties
  @Value("${app.cors.allowed-origin:http://localhost:4200}")
  private String allowedOrigin;

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public JwtService jwtService() {
    return new JwtService(secret, issuer);
  }

  @Bean
  public CookieUtil cookieUtil() {
    return new CookieUtil(cookieDomain, cookieSecure, sameSite);
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration conf = new CorsConfiguration();
    conf.addAllowedHeader("*");
    conf.addAllowedMethod("*");
    conf.setAllowCredentials(true);
    // KHÔNG dùng "*" khi allowCredentials=true
    conf.addAllowedOriginPattern(allowedOrigin);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", conf);
    return source;
  }

  // ===== ADMIN CHAIN =====
  @Bean
  @Order(1)
  public SecurityFilterChain adminChain(HttpSecurity http, JwtService jwtService, AdminRefreshTokenRepository adminRepo)
      throws Exception {
    http.securityMatcher("/admin/**");
    http.csrf(csrf -> csrf.disable());
    http.cors(Customizer.withDefaults());
    http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    http.authorizeHttpRequests(auth -> auth
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

        // Public
        .requestMatchers(HttpMethod.POST, "/admin/register", "/admin/login", "/admin/refresh").permitAll()

        // Phải đăng nhập
        .requestMatchers(HttpMethod.GET, "/admin/me").authenticated()
        .requestMatchers(HttpMethod.POST, "/admin/logout").authenticated()

        // Các route khác
        .requestMatchers("/error").permitAll()

        .requestMatchers(HttpMethod.GET, "/public/**").permitAll()
        .anyRequest().authenticated());

    // http.exceptionHandling(e -> e
    // .authenticationEntryPoint((req, res, ex) -> res.sendError(401)));

    // Admin filter riêng, dùng cookie ADMIN_ACCESS_TOKEN
    http.addFilterBefore(
        new JwtAdminAuthenticationFilter(jwtService, adminRepo, "ADMIN_ACCESS_TOKEN"),
        UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  // ===== USER CHAIN (giữ nguyên comment & logic gốc, bỏ match admin ra khỏi đây) =====
  @Bean
  @Order(2)
  public SecurityFilterChain userChain(HttpSecurity http, JwtService jwtService, UserRepository userRepo)
      throws Exception {
    http.csrf(csrf -> csrf.disable());
    http.cors(Customizer.withDefaults());
    http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    http.authorizeHttpRequests(auth -> auth
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

        // Public
        .requestMatchers(HttpMethod.POST, "/auth/register", "/auth/login", "/auth/refresh").permitAll()
        .requestMatchers(HttpMethod.GET, "/auth/ping").permitAll()

        // Phải đăng nhập
        .requestMatchers(HttpMethod.GET, "/auth/me").authenticated()
        .requestMatchers(HttpMethod.POST, "/auth/logout").authenticated()

        // Các route khác
        .requestMatchers("/error").permitAll()

        .requestMatchers(HttpMethod.GET, "/public/**").permitAll()
        .anyRequest().authenticated());
    // http.exceptionHandling(e -> e
    // .authenticationEntryPoint((req, res, ex) -> res.sendError(401)));

    http.addFilterBefore(new JwtAuthenticationFilter(jwtService, userRepo),
        UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
