package com.github.datlaipro.shop.domain.user.dto;

public class UserRes {
  private Long id;
  private String name;
  private String email;
  private String avatar;
  private String address;
  private String role;

  public UserRes(Long id, String name, String email, String avatar, String address, String role) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.avatar = avatar;
    this.address = address;
    this.role = role;
  }

  public Long getId() { return id; }
  public String getName() { return name; }
  public String getEmail() { return email; }
  public String getAvatar() { return avatar; }
  public String getAddress() { return address; }
  public String getRole() { return role; }
}
