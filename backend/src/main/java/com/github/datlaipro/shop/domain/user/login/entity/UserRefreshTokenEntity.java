package com.github.datlaipro.shop.domain.user.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
// định nghĩa ra đối tượng user và ràng buộc với db
@Entity
@Table(name = "user_refresh_tokens", uniqueConstraints = {
    @UniqueConstraint(name = "uq_user_rt_jti", columnNames = "jti")
}, indexes = {
    @Index(name = "idx_user_rt_user", columnList = "user_id"),
    @Index(name = "idx_user_rt_expires", columnList = "expires_at"),
    @Index(name = "idx_user_rt_family_revoked", columnList = "family_id, revoked_at")
})
public class UserRefreshTokenEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_user_rt_user"))
  private UserEntity user;

  @Column(name = "jti", length = 36, nullable = false)
  private String jti;

  @Column(name = "family_id", length = 36, nullable = false)
  private String familyId;

  @Column(name = "token_hash", length = 255, nullable = false)
  private String tokenHash;

  @Column(name = "device_id", length = 64)
  private String deviceId;

  @Column(name = "ip", length = 45)
  private String ip;

  @Column(name = "user_agent", length = 255)
  private String userAgent;

  @Column(name = "issued_at", nullable = false)
  private LocalDateTime issuedAt;

  @Column(name = "last_used_at")
  private LocalDateTime lastUsedAt;

  @Column(name = "expires_at", nullable = false)
  private LocalDateTime expiresAt;

  @Column(name = "revoked_at")
  private LocalDateTime revokedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "replaced_by_id", foreignKey = @ForeignKey(name = "fk_user_rt_replaced"))
  private UserRefreshTokenEntity replacedBy;

  // getters/setters ...
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public UserEntity getUser() {// trả về đối tượng user 
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
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

  public UserRefreshTokenEntity getReplacedBy() {
    return replacedBy;
  }

  public void setReplacedBy(UserRefreshTokenEntity replacedBy) {
    this.replacedBy = replacedBy;
  }

  @Transient
  public boolean isRevoked() {
    return revokedAt != null;
  }
}
