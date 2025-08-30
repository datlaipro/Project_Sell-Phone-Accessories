// com.github.datlaipro.shop.security.JwtAdminAuthenticationFilter.java
package com.github.datlaipro.shop.security;


import com.github.datlaipro.shop.domain.admin.login.dto.LoginAdminRes;
import com.github.datlaipro.shop.domain.admin.register.entity.AdminEntity;
import com.github.datlaipro.shop.domain.admin.login.repo.AdminRefreshTokenRepository;
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

public class JwtAdminAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AdminRepository adminRepo;
    private final String cookieName; // ví dụ: "ADMIN_ACCESS_TOKEN"

    public JwtAdminAuthenticationFilter(JwtService jwtService, AdminRepository adminRepo, String cookieName) {
        this.jwtService = jwtService;
        this.adminRepo = adminRepo;
        this.cookieName = cookieName;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String token = readCookie(request, cookieName).orElse(null);
        if (token != null) {
            try {
                Jws<Claims> jws = jwtService.parse(token);
                Long adminId = Long.valueOf(jws.getBody().getSubject());

                adminRepo.findById(adminId).ifPresent(a -> {
                    var principal = new LoginAdminRes(a.getId(), a.getEmail(), a.getName(), a.getRole());
                    var authority = new SimpleGrantedAuthority("ROLE_ADMIN_" + a.getRole().name().toUpperCase());
                    var auth = new UsernamePasswordAuthenticationToken(principal, null,
                            Collections.singletonList(authority));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    response.setHeader(HttpHeaders.VARY, "Cookie");
                });
            } catch (Exception ignored) {
                /* anonymous */ }
        }
        chain.doFilter(request, response);
    }

    private Optional<String> readCookie(HttpServletRequest req, String name) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null)
            return Optional.empty();
        for (Cookie c : cookies)
            if (name.equals(c.getName()))
                return Optional.ofNullable(c.getValue());
        return Optional.empty();
    }
}
