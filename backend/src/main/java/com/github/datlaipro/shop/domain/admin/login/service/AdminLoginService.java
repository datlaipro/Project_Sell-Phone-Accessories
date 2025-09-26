package com.github.datlaipro.shop.domain.admin.login.service;

import com.github.datlaipro.shop.domain.admin.login.dto.LoginAdminRes;
import com.github.datlaipro.shop.domain.admin.login.dto.LoginAdminReq;
import com.github.datlaipro.shop.domain.admin.register.entity.AdminEntity;
import com.github.datlaipro.shop.domain.admin.login.entity.AdminRefreshTokenEntity;
import com.github.datlaipro.shop.domain.admin.login.repo.AdminRefreshTokenRepository;
import com.github.datlaipro.shop.domain.admin.register.repo.AdminRepository;
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
// imports:
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// trong class AuthService:

@Service
public class AdminLoginService {
    private static final Logger log = LoggerFactory.getLogger(AdminLoginService.class);

    private final AdminRepository userRepo;// gọi tham chiếu tới truy vấn db cho đối tượng admin
    private final AdminRefreshTokenRepository rtRepo;// gọi tham chiếu tới truy vấn db cho đối tượng refreshToken
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    private final long accessMinutes;
    private final long refreshDays;

    public AdminLoginService(AdminRepository userRepo, AdminRefreshTokenRepository rtRepo,
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

    private String createAccessToken(AdminEntity u) {
        long exp = LocalDateTime.now().plusMinutes(accessMinutes).toEpochSecond(ZoneOffset.UTC);
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", u.getEmail());
        claims.put("role", u.getRole().name());
        return jwt.createToken(String.valueOf(u.getId()), claims, exp);
        // subject = userId
    }

    private String createRefreshTokenAndStore(AdminEntity u, String familyId, String deviceId, String ip, String ua) {
        String jti = uuid();
        long expSec = LocalDateTime.now().plusDays(refreshDays).toEpochSecond(ZoneOffset.UTC);
        Map<String, Object> claims = new HashMap<>();
        claims.put("typ", "refresh");
        claims.put("jti", jti);
        claims.put("fid", familyId);
        String token = jwt.createToken(String.valueOf(u.getId()), claims, expSec);

        // store hashed token
        AdminRefreshTokenEntity e = new AdminRefreshTokenEntity();
        e.setAdmin(u);
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
    public Tokens login(LoginAdminReq req, HttpServletRequest httpReq) {
        final String email = Optional.ofNullable(req.getEmail()).orElse("").trim().toLowerCase();
        final String pass = Optional.ofNullable(req.getPassword()).orElse("");

        // gọi repo 1 lần, rồi log kết quả có tìm thấy không
        var ou = userRepo.findByEmailIgnoreCase(email);
        log.debug("login email={}, passLen={}, found={}", email, pass.length(), ou.isPresent());

        var u = ou.orElseThrow(() -> new BadCredentialsException("Email hoặc mật khẩu không đúng"));
        if (!encoder.matches(pass, u.getPasswordHash())) {
            log.debug("login failed: password mismatch for email={}", email);
            throw new BadCredentialsException("Email hoặc mật khẩu không đúng");
        }

        String access = createAccessToken(u);
        String familyId = uuid();
        String refresh = createRefreshTokenAndStore(
                u, familyId,
                httpReq.getHeader("X-Device-Id"),
                httpReq.getRemoteAddr(),
                Optional.ofNullable(httpReq.getHeader("User-Agent")).orElse(""));

        return new Tokens(access, refresh, new LoginAdminRes(u.getId(), u.getEmail(), u.getName(), u.getRole().name()));
    }

    @Transactional
    public Tokens refresh(String refreshToken, HttpServletRequest httpReq) {
        if (refreshToken == null || refreshToken.isBlank())
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "Missing refresh token");

        var jws = jwt.parse(refreshToken);
        String jti = jws.getBody().get("jti", String.class);
        String fid = jws.getBody().get("fid", String.class);

        // 1) HẾT HẠN => 401 (KHÔNG coi là reuse, KHÔNG revoke cả family)
        if (jwt.isExpired(jws)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }

        // 2) Lấy bản ghi và KHÓA để tránh race (2 refresh song song)
        AdminRefreshTokenEntity row = rtRepo.findByJtiForUpdate(jti)
                .orElseThrow(() -> {
                    // Không tìm thấy: nhiều khả năng reuse (token cũ đã bị rotate)
                    // Revoke family để chặn chuỗi reuse tiếp theo
                    rtRepo.revokeFamily(fid, LocalDateTime.now());
                    return new org.springframework.web.server.ResponseStatusException(
                            org.springframework.http.HttpStatus.UNAUTHORIZED,
                            "Detected token reuse. Please login again.");
                });

        String nowMsg = "Detected token reuse. Please login again.";
        LocalDateTime now = LocalDateTime.now();

        // 3) Hash mismatch => chắc chắn reuse -> revoke cả family + 401
        String hash = sha256(refreshToken);
        if (!hash.equals(row.getTokenHash())) {
            rtRepo.revokeFamily(fid, now);
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, nowMsg);
        }

        // 4) Token đã bị thay thế hoặc đã bị revoke => reuse -> revoke family + 401
        if (row.getReplacedBy() != null || row.getRevokedAt() != null) {
            rtRepo.revokeFamily(fid, now);
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, nowMsg);
        }

        // 5) Hết hạn (double-check theo DB) => 401 (không revoke family)
        if (row.getExpiresAt() != null && row.getExpiresAt().isBefore(now)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }

        // 6) Phát hành token mới (trong CÙNG transaction)
        AdminEntity u = row.getAdmin();
        String newAccess = createAccessToken(u);
        String newRefresh = createRefreshTokenAndStore(
                u, fid,
                httpReq.getHeader("X-Device-Id"),
                httpReq.getRemoteAddr(),
                Optional.ofNullable(httpReq.getHeader("User-Agent")).orElse(""));

        // 7) Gắn replaced_by + đánh dấu revoke token cũ (chặn reuse)
        row.setLastUsedAt(now);
        row.setRevokedAt(now);
        // Tìm newRow theo jti của token mới: tốt nhất bạn encode jti vào claims, rồi
        // lấy jtiNew trực tiếp
        // Ở đây lấy theo hash cho nhanh:
        String newHash = sha256(newRefresh);
        AdminRefreshTokenEntity newRow = rtRepo.findAliveByFamily(fid).stream()
                .filter(t -> newHash.equals(t.getTokenHash()))
                .findFirst().orElse(null);
        if (newRow != null) {
            row.setReplacedBy(newRow);
        }
        rtRepo.save(row);

        return new Tokens(newAccess, newRefresh,
                new LoginAdminRes(u.getId(), u.getEmail(), u.getName(), u.getRole().name()));
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
    public static class Tokens {// thực hiện trả accessToken và refreshToken và user
        public final String accessToken;
        public final String refreshToken;
        public final LoginAdminRes user;

        public Tokens(String a, String r, LoginAdminRes u) {
            this.accessToken = a;
            this.refreshToken = r;
            this.user = u;
        }
    }
}
