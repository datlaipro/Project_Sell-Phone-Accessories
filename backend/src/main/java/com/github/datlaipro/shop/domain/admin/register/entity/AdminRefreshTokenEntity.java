package com.github.datlaipro.shop.domain.admin.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin_refresh_tokens", uniqueConstraints = @UniqueConstraint(name = "uq_admin_rt_jti", columnNames = "jti"))
public class AdminRefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admin_id", nullable = false, foreignKey = @ForeignKey(name = "fk_admin_rt_admin"))
    private AdminEntity admin;

    @Column(name = "jti", nullable = false, length = 36)
    private String jti; // CHAR(36)

    @Column(name = "family_id", nullable = false, length = 36)
    private String familyId; // CHAR(36)

    @Column(name = "token_hash", nullable = false, length = 255)
    private String tokenHash;

    @Column(name = "device_id", length = 64)
    private String deviceId;

    @Column(name = "ip", length = 45)
    private String ip;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt; // default CURRENT_TIMESTAMP ở DB

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "replaced_by_id", foreignKey = @ForeignKey(name = "fk_admin_rt_replaced"))
    private AdminRefreshTokenEntity replacedBy;

    // Getters/Setters ...
    // ===== Getters & Setters =====
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AdminEntity getAdmin() {
        return admin;
    }

    public void setAdmin(AdminEntity admin) {
        this.admin = admin;
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public LocalDateTime getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(LocalDateTime lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(LocalDateTime revokedAt) {
        this.revokedAt = revokedAt;
    }

    public AdminRefreshTokenEntity getReplacedBy() {
        return replacedBy;
    }

    public void setReplacedBy(AdminRefreshTokenEntity replacedBy) {
        this.replacedBy = replacedBy;
    }

}
