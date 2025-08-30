package com.github.datlaipro.shop.domain.admin.register.controller;

import com.github.datlaipro.shop.domain.admin.register.dto.RegisterReqAdmin;
import com.github.datlaipro.shop.domain.admin.register.dto.RegisterResAdmin;
import com.github.datlaipro.shop.domain.admin.register.service.RegisterAdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@RestController// tự động chuyển thành json 
@RequestMapping("/admin")
public class RegisterAdminController {
  private final RegisterAdminService adminService;

  public RegisterAdminController(RegisterAdminService adminService) {
    this.adminService = adminService;
  }

  @PostMapping("/register")
   public ResponseEntity<RegisterResAdmin> register(@RequestBody @Valid RegisterReqAdmin req) {
    RegisterResAdmin res = adminService.register(req);
    return ResponseEntity.status(HttpStatus.CREATED).body(res);
  }
}
