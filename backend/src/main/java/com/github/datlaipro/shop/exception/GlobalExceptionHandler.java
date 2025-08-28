package com.github.datlaipro.shop.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // 👇 Cái quan trọng: map BadCredentials -> 401
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<Map<String, String>> handleBadCreds(BadCredentialsException ex) {
    Map<String, String> body = Map.of("message", "Email hoặc mật khẩu không đúng");
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
  }

  // (khuyên) map lỗi validate -> 400
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("message", "Dữ liệu không hợp lệ");
    Map<String, String> errors = new HashMap<>();
    for (var e : ex.getBindingResult().getAllErrors()) {
      String field = e instanceof FieldError fe ? fe.getField() : e.getObjectName();
      errors.put(field, e.getDefaultMessage());
    }
    body.put("errors", errors);
    return ResponseEntity.badRequest().body(body);
  }

  // (khuyên) map trùng unique -> 409
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Map<String, String>> handleDup(DataIntegrityViolationException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(Map.of("message", "Tài nguyên đã tồn tại"));
  }

  // Fallback cuối cùng -> 500
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleOther(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("message", "Internal error"));
  }
}
