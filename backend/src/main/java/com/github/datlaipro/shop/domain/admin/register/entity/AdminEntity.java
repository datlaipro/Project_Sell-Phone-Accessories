package com.github.datlaipro.shop.domain.admin.register.entity;
import com.github.datlaipro.shop.domain.admin.login.entity.AdminRefreshTokenEntity;
import com.github.datlaipro.shop.domain.admin.register.entity.AdminAuditLogEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "admins", uniqueConstraints = @UniqueConstraint(name = "uq_admins_email", columnNames = "email"))
public class AdminEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", length = 255)
  private String name;

  @Column(name = "email", length = 255, nullable = false)
  private String email;

  @Column(name = "password_hash", length = 255, nullable = false)
  private String passwordHash;

  @Column(name = "avatar", length = 255)
  private String avatar;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, length = 20)
  private Role role = Role.editor; // DB default 'editor'

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private Status status = Status.active; // DB default 'active'

  @Column(name = "mfa_enabled", nullable = false)
  private boolean mfaEnabled; // DB default 0

  @Column(name = "totp_secret", length = 64)
  private String totpSecret;

  @Column(name = "last_login_at")
  private LocalDateTime lastLoginAt; // DATETIME

  // TIMESTAMP do DB quản lý: default CURRENT_TIMESTAMP / ON UPDATE
  @Column(name = "created_at", updatable = false, insertable = false)
  private Instant createdAt;

  @Column(name = "updated_at", updatable = false, insertable = false)
  private Instant updatedAt;

  // Quan hệ tham khảo (có thể bỏ nếu chưa cần):
  @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<AdminRefreshTokenEntity> refreshTokens = new HashSet<>();

  @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<AdminAuditLogEntity> auditLogs = new HashSet<>();


  // Getters/Setters ...
  // ===== Enums =====
  public enum Role {
    superadmin, manager, editor, viewer
  }

  public enum Status {
    active, suspended
  }

  // ===== Getters & Setters =====
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public boolean isMfaEnabled() {
    return mfaEnabled;
  }

  public void setMfaEnabled(boolean mfaEnabled) {
    this.mfaEnabled = mfaEnabled;
  }

  public String getTotpSecret() {
    return totpSecret;
  }

  public void setTotpSecret(String totpSecret) {
    this.totpSecret = totpSecret;
  }

  public LocalDateTime getLastLoginAt() {
    return lastLoginAt;
  }

  public void setLastLoginAt(LocalDateTime lastLoginAt) {
    this.lastLoginAt = lastLoginAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Set<AdminRefreshTokenEntity> getRefreshTokens() {
    return refreshTokens;
  }

  public void setRefreshTokens(Set<AdminRefreshTokenEntity> refreshTokens) {
    this.refreshTokens = refreshTokens;
  }

  public Set<AdminAuditLogEntity> getAuditLogs() {
    return auditLogs;
  }

  public void setAuditLogs(Set<AdminAuditLogEntity> auditLogs) {
    this.auditLogs = auditLogs;
  }
}
