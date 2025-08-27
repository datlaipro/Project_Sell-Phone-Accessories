package com.github.datlaipro.shop.domain.user.controller;

import com.github.datlaipro.shop.domain.user.dto.RegisterReq;
import com.github.datlaipro.shop.domain.user.dto.UserRes;
import com.github.datlaipro.shop.domain.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController// tự động chuyển thành json 
@RequestMapping("/auth")
public class AuthController {
  private final UserService userService;

  public AuthController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  public ResponseEntity<UserRes> register(@RequestBody @Valid RegisterReq req) {// validate dữ liệu đầu vào của dto/RegisterReq
    return ResponseEntity.status(201).body(userService.register(req));
  }
}
