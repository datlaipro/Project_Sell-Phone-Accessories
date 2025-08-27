package com.github.datlaipro.shop.domain.user.service;

import com.github.datlaipro.shop.domain.user.dto.AuthRes;
import com.github.datlaipro.shop.domain.user.dto.LoginReq;
import com.github.datlaipro.shop.domain.user.entity.UserEntity;
import com.github.datlaipro.shop.domain.user.entity.UserRefreshTokenEntity;
import com.github.datlaipro.shop.domain.user.repo.UserRefreshTokenRepository;
import com.github.datlaipro.shop.domain.user.repo.UserRepository;
import com.github.datlaipro.shop.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.BadCredentialsException;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final UserRefreshTokenRepository rtRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    private final long accessMinutes;
    private final long refreshDays;

    public AuthService(UserRepository userRepo, UserRefreshTokenRepository rtRepo,
            PasswordEncoder encoder, JwtService jwt,
            org.springframework.core.env.Environment env) {
        this.userRepo = userRepo;
        this.rtRepo = rtRepo;
        this.encoder = encoder;
        this.jwt = jwt;
        this.accessMinutes = Long
                .parseLong(Objects.requireNonNullElse(env.getProperty("app.jwt.access.minutes"), "5"));
        this.refreshDays = Long.parseLong(Objects.requireNonNullElse(env.getProperty("app.jwt.refresh.days"), "30"));
    }

    // ===== Utilities =====
    private static String uuid() {
        return UUID.randomUUID().toString();
    }

    private static String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] out = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(out.length * 2);
            for (byte b : out)
                sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String createAccessToken(UserEntity u) {
        long exp = LocalDateTime.now().plusMinutes(accessMinutes).toEpochSecond(ZoneOffset.UTC);
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", u.getEmail());
        claims.put("role", u.getRole());
        return jwt.createToken(String.valueOf(u.getId()), claims, exp);
        // subject = userId
    }

    private String createRefreshTokenAndStore(UserEntity u, String familyId, String deviceId, String ip, String ua) {
        String jti = uuid();
        long expSec = LocalDateTime.now().plusDays(refreshDays).toEpochSecond(ZoneOffset.UTC);
        Map<String, Object> claims = new HashMap<>();
        claims.put("typ", "refresh");
        claims.put("jti", jti);
        claims.put("fid", familyId);
        String token = jwt.createToken(String.valueOf(u.getId()), claims, expSec);

        // store hashed token
        UserRefreshTokenEntity e = new UserRefreshTokenEntity();
        e.setUser(u);
        e.setJti(jti);
        e.setFamilyId(familyId);
        e.setTokenHash(sha256(token));
        e.setDeviceId(deviceId);
        e.setIp(ip);
        e.setUserAgent(ua);
        e.setIssuedAt(LocalDateTime.now());
        e.setExpiresAt(LocalDateTime.ofEpochSecond(expSec, 0, ZoneOffset.UTC));
        rtRepo.save(e);

        return token;
    }

    // ===== Public APIs =====

    @Transactional
    public Tokens login(LoginReq req, HttpServletRequest httpReq) {
        UserEntity u = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Email hoặc mật khẩu không đúng"));
        if (!encoder.matches(req.getPassword(), u.getPasswordHash())) {
            throw new BadCredentialsException("Email hoặc mật khẩu không đúng");
        }

        String access = createAccessToken(u);
        String familyId = uuid();
        String refresh = createRefreshTokenAndStore(
                u, familyId,
                httpReq.getHeader("X-Device-Id"),
                httpReq.getRemoteAddr(),
                Optional.ofNullable(httpReq.getHeader("User-Agent")).orElse(""));

        return new Tokens(access, refresh, new AuthRes(u.getId(), u.getEmail(), u.getName(), u.getRole()));
    }

    @Transactional
    public Tokens refresh(String refreshToken, HttpServletRequest httpReq) {
        if (refreshToken == null || refreshToken.isBlank())
            throw new RuntimeException("Missing refresh token");

        var jws = jwt.parse(refreshToken);
        if (jwt.isExpired(jws))
            throw new RuntimeException("Refresh token expired");

        String jti = jws.getBody().get("jti", String.class);
        String fid = jws.getBody().get("fid", String.class);
        Long userId = Long.valueOf(jws.getBody().getSubject());

        UserRefreshTokenEntity row = rtRepo.findByJti(jti)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        // Reuse detection: DB chưa bị revoke nhưng token hash không khớp -> reuse
        String hash = sha256(refreshToken);
        if (!hash.equals(row.getTokenHash()) || row.isRevoked() || row.getExpiresAt().isBefore(LocalDateTime.now())) {
            // revoke toàn bộ family
            // đơn giản: mark revoked cho tất cả token cùng family còn sống
            // (tùy DB: ở đây demo nhẹ - production dùng update batch)
            rtRepo.findAll().stream()
                    .filter(t -> fid.equals(t.getFamilyId()) && t.getRevokedAt() == null)
                    .forEach(t -> {
                        t.setRevokedAt(LocalDateTime.now());
                        rtRepo.save(t);
                    });
            throw new RuntimeException("Detected token reuse. Please login again.");
        }

        // rotate
        UserEntity u = row.getUser();
        String newAccess = createAccessToken(u);
        String newRefresh = createRefreshTokenAndStore(
                u, fid,
                httpReq.getHeader("X-Device-Id"),
                httpReq.getRemoteAddr(),
                Optional.ofNullable(httpReq.getHeader("User-Agent")).orElse(""));

        // revoke old & link replaced_by
        row.setRevokedAt(LocalDateTime.now());
        // tìm record mới theo hash
        String newHash = sha256(newRefresh);
        UserRefreshTokenEntity newRow = rtRepo.findAll().stream()
                .filter(t -> newHash.equals(t.getTokenHash()))
                .findFirst().orElse(null);
        if (newRow != null) {
            row.setReplacedBy(newRow);
        }
        rtRepo.save(row);

        return new Tokens(newAccess, newRefresh,
                new AuthRes(u.getId(), u.getEmail(), u.getName(), u.getRole()));
    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank())
            return;
        try {
            var jws = jwt.parse(refreshToken);
            String jti = jws.getBody().get("jti", String.class);
            rtRepo.findByJtiAndRevokedAtIsNull(jti).ifPresent(row -> {
                row.setRevokedAt(LocalDateTime.now());
                rtRepo.save(row);
            });
        } catch (Exception ignored) {
        }
    }

    // ===== Helper class =====
    public static class Tokens {
        public final String accessToken;
        public final String refreshToken;
        public final AuthRes user;

        public Tokens(String a, String r, AuthRes u) {
            this.accessToken = a;
            this.refreshToken = r;
            this.user = u;
        }
    }
}
