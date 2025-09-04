package com.github.datlaipro.shop.domain.admin.login.controller;

import com.github.datlaipro.shop.domain.admin.login.dto.LoginAdminReq;
import com.github.datlaipro.shop.domain.admin.login.dto.LoginAdminRes;
import com.github.datlaipro.shop.domain.admin.login.service.AdminLoginService;
import com.github.datlaipro.shop.security.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/admin")
public class LoginAdminController {

  private final AdminLoginService auth;
  private final CookieUtil cookies;

  @Value("${app.jwt.access.minutes}")
  private long accessMinutes;
  @Value("${app.jwt.refresh.days}")
  private long refreshDays;

  // ✅ Constructor — KHÔNG gắn @PostMapping vào đây
  public LoginAdminController(AdminLoginService auth, CookieUtil cookies) {// gán tham chiếu của class AuthService và
                                                                           // CookieUtil vào biến thành viên của class
                                                                           // này
    this.auth = auth;
    this.cookies = cookies;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginAdminRes> login(@RequestBody @Valid LoginAdminReq req,// nhận dữ liệu từ client gửi lên  

      HttpServletRequest httpReq,
      HttpServletResponse httpRes) {
    var t = auth.login(req, httpReq);//

    ResponseCookie c1 = cookies.build("ADMIN_ACCESS_TOKEN", t.accessToken, Duration.ofMinutes(accessMinutes));// gắn
                                                                                                              // accessToken
                                                                                                              // vào
                                                                                                              // cookie
    ResponseCookie c2 = cookies.build("ADMIN_REFRESH_TOKEN", t.refreshToken, Duration.ofDays(refreshDays));
    httpRes.addHeader("Set-Cookie", c1.toString());
    httpRes.addHeader("Set-Cookie", c2.toString());

    return ResponseEntity.ok(t.user);
  }

  @PostMapping("/refresh")
  public ResponseEntity<LoginAdminRes> refresh(HttpServletRequest httpReq,
      HttpServletResponse httpRes,
      @CookieValue(name = "ADMIN_REFRESH_TOKEN", required = false) String refreshCookie) {
    var t = auth.refresh(refreshCookie, httpReq);

    ResponseCookie c1 = cookies.build("ADMIN_ACCESS_TOKEN", t.accessToken, Duration.ofMinutes(accessMinutes));
    ResponseCookie c2 = cookies.build("ADMIN_REFRESH_TOKEN", t.refreshToken, Duration.ofDays(refreshDays));
    httpRes.addHeader("Set-Cookie", c1.toString());
    httpRes.addHeader("Set-Cookie", c2.toString());

    return ResponseEntity.ok(t.user);
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@CookieValue(name = "ADMIN_REFRESH_TOKEN", required = false) String refreshCookie,
      HttpServletResponse httpRes) {
    auth.logout(refreshCookie);
    httpRes.addHeader("Set-Cookie", cookies.clear("ADMIN_ACCESS_TOKEN").toString());
    httpRes.addHeader("Set-Cookie", cookies.clear("ADMIN_REFRESH_TOKEN").toString());
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/me")
  public ResponseEntity<LoginAdminRes> me(@AuthenticationPrincipal LoginAdminRes principal) {
    if (principal == null)
      return ResponseEntity.status(401).build();

    return ResponseEntity.ok(principal);
  }
}
