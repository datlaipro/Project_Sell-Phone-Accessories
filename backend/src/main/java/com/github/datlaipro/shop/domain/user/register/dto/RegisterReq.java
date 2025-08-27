package com.github.datlaipro.shop.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
// định nghĩa validate dữ liệu 
public class RegisterReq {
  @Size(max = 255)
  private String name;

  @NotBlank @Email @Size(max = 255)
  private String email;

  @NotBlank @Size(min = 6, max = 100)
  private String password;

  @Size(max = 255)
  private String avatar;

  @Size(max = 255)
  private String address;

  // getters/setters
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }

  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }

  public String getAvatar() { return avatar; }
  public void setAvatar(String avatar) { this.avatar = avatar; }

  public String getAddress() { return address; }
  public void setAddress(String address) { this.address = address; }
}
