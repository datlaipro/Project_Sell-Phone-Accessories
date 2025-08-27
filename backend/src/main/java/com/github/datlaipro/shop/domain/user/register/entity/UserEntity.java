package com.github.datlaipro.shop.domain.user.entity;

import jakarta.persistence.*;

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

  // DB enum('user','admin_proxy') -> dùng chuỗi thường để khỏi cần converter
  @Column(nullable = false, length = 20)
  private String role = "user";

  @Column(name = "created_by_admin_id")
  private Long createdByAdminId;

  @Column(name = "updated_by_admin_id")
  private Long updatedByAdminId;

  // --- getters/setters ---
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public String getPasswordHash() { return passwordHash; }
  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

  public String getAvatar() { return avatar; }
  public void setAvatar(String avatar) { this.avatar = avatar; }

  public String getAddress() { return address; }
  public void setAddress(String address) { this.address = address; }

  public String getRole() { return role; }
  public void setRole(String role) { this.role = role; }

  public Long getCreatedByAdminId() { return createdByAdminId; }
  public void setCreatedByAdminId(Long createdByAdminId) { this.createdByAdminId = createdByAdminId; }

  public Long getUpdatedByAdminId() { return updatedByAdminId; }
  public void setUpdatedByAdminId(Long updatedByAdminId) { this.updatedByAdminId = updatedByAdminId; }
}
