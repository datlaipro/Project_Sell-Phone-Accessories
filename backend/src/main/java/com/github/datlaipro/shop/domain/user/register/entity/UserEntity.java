package com.github.datlaipro.shop.domain.user.entity;

import jakarta.persistence.*;
import java.util.Locale;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(name = "uq_users_email", columnNames = "email")
})
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 255)
  private String name;

  @Column(nullable = false, length = 255)
  private String email;

  @Column(name = "password_hash", nullable = false, length = 255)
  private String passwordHash;

  @Column(length = 255)
  private String avatar;

  @Column(length = 255)
  private String address;

  @Column(nullable = false, length = 20)
  private String role = "user";

  @Column(name = "created_by_admin_id")
  private Long createdByAdminId;

  @Column(name = "updated_by_admin_id")
  private Long updatedByAdminId;

  // ===== Helpers =====
  private static String trim(String s) { return s == null ? null : s.trim(); }
  private static String normEmail(String s) {
    return s == null ? null : s.trim().toLowerCase(Locale.ROOT);
  }
  private static String normRole(String s) {
    return (s == null || s.isBlank()) ? "user" : s.trim().toLowerCase(Locale.ROOT);
  }

  // ===== Lifecycle: luôn chuẩn hoá trước khi INSERT/UPDATE =====
  @PrePersist @PreUpdate
  private void normalize() {
    this.email = normEmail(this.email);
    this.name  = trim(this.name);
    this.role  = normRole(this.role);
    this.avatar = trim(this.avatar);
    this.address = trim(this.address);
  }

  // --- getters/setters ---
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getName() { return name; }
  public void setName(String name) { this.name = trim(name); }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = normEmail(email); } // 👈 chuẩn hoá ngay khi set

  public String getPasswordHash() { return passwordHash; }
  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

  public String getAvatar() { return avatar; }
  public void setAvatar(String avatar) { this.avatar = trim(avatar); }

  public String getAddress() { return address; }
  public void setAddress(String address) { this.address = trim(address); }

  public String getRole() { return role; }
  public void setRole(String role) { this.role = normRole(role); }

  public Long getCreatedByAdminId() { return createdByAdminId; }
  public void setCreatedByAdminId(Long createdByAdminId) { this.createdByAdminId = createdByAdminId; }

  public Long getUpdatedByAdminId() { return updatedByAdminId; }
  public void setUpdatedByAdminId(Long updatedByAdminId) { this.updatedByAdminId = updatedByAdminId; }
}
