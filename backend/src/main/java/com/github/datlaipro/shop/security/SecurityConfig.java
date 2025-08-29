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

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, JwtService jwtService, UserRepository userRepo)
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
