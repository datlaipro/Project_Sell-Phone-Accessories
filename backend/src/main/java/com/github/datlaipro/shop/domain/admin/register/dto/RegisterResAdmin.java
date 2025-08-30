package com.github.datlaipro.shop.domain.admin.register.dto;

public class RegisterResAdmin {
    private Long id;
    private String name;
    private String email;
    private String avatar;
    private String role;

  public RegisterResAdmin(Long id, String name, String email, String avatar, String role) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.avatar = avatar;
    this.role = role;
  }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatar() {
        return avatar;
    }

   

    public String getRole() {
        return role;
    }
}
