package com.github.datlaipro.shop.domain.admin.login.dto;
import com.github.datlaipro.shop.domain.admin.register.entity.AdminEntity;

// đối tượng user gồm các thông tin dưới 
public class LoginAdminRes {
  private Long id;
  private String email;
  private String name;
  private String role;

    public LoginAdminRes() {} // optional, tốt cho Jackson

  public LoginAdminRes(Long id, String email, String name, String role) {
    this.id = id;
    this.email = email;
    this.name = name;
   this.role = role; 
  }
  public LoginAdminRes(Long id, String email, String name, AdminEntity.Role role) {
    this(id, email, name, role != null ? role.name() : null); // ❗ chuyển enum → String
  }
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }
}
