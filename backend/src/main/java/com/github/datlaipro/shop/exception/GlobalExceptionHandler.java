package com.github.datlaipro.shop.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // ---------- 401 UNAUTHORIZED ----------
  // Sai email/mật khẩu
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<Map<String, String>> handleBadCreds(BadCredentialsException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(Map.of("message", "Email hoặc mật khẩu không đúng"));
  }

  // Các lỗi auth khác do Spring Security ném ra
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<Map<String, String>> handleAuth(AuthenticationException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(Map.of("message", "Unauthorized"));
  }

  // JWT không hợp lệ / hết hạn
  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<Map<String, String>> handleExpiredJwt(ExpiredJwtException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .header(HttpHeaders.WWW_AUTHENTICATE, "Bearer error=\"invalid_token\", error_description=\"token expired\"")
        .body(Map.of("message", "Token hết hạn"));
  }

  @ExceptionHandler(JwtException.class)
  public ResponseEntity<Map<String, String>> handleJwt(JwtException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .header(HttpHeaders.WWW_AUTHENTICATE, "Bearer error=\"invalid_token\"")
        .body(Map.of("message", "Token không hợp lệ"));
  }

  // ---------- 403 FORBIDDEN ----------
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(Map.of("message", "Forbidden"));
  }

  // ---------- 400 BAD REQUEST (validation/body/param) ----------
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    for (var e : ex.getBindingResult().getAllErrors()) {
      String field = (e instanceof FieldError fe) ? fe.getField() : e.getObjectName();
      errors.put(field, e.getDefaultMessage());
    }
    Map<String, Object> body = new HashMap<>();
    body.put("message", "Dữ liệu không hợp lệ");
    body.put("errors", errors);
    return ResponseEntity.badRequest().body(body);
  }

  // @Validated trên path/query param
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
    Map<String, String> errors = ex.getConstraintViolations().stream()
        .collect(Collectors.toMap(
            v -> resolvePath(v),
            ConstraintViolation::getMessage,
            (a, b) -> b,
            HashMap::new
        ));
    Map<String, Object> body = new HashMap<>();
    body.put("message", "Dữ liệu không hợp lệ");
    body.put("errors", errors);
    return ResponseEntity.badRequest().body(body);
  }

  private String resolvePath(ConstraintViolation<?> v) {
    var it = v.getPropertyPath().iterator();
    String last = "";
    while (it.hasNext()) last = it.next().getName();
    return last.isBlank() ? v.getPropertyPath().toString() : last;
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String, String>> handleMalformedJson(HttpMessageNotReadableException ex) {
    return ResponseEntity.badRequest().body(Map.of("message", "JSON không hợp lệ"));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    return ResponseEntity.badRequest().body(Map.of("message", "Kiểu dữ liệu không đúng"));
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<Map<String, String>> handleMissingParam(MissingServletRequestParameterException ex) {
    return ResponseEntity.badRequest().body(Map.of("message", "Thiếu tham số bắt buộc: " + ex.getParameterName()));
  }

  // ---------- 404 / 405 ----------
  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<Map<String, String>> handleNoHandler(NoHandlerFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Map.of("message", "Không tìm thấy endpoint"));
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<Map<String, String>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
        .body(Map.of("message", "Method không được hỗ trợ"));
  }

  // ---------- 409 CONFLICT & lỗi ràng buộc DB ----------
  // Chi tiết hoá lỗi DB: duplicate/null/foreign key...
  @ExceptionHandler({ DataIntegrityViolationException.class, SQLIntegrityConstraintViolationException.class })
  public ResponseEntity<Map<String, String>> handleSql(DataIntegrityViolationException ex) {
    String msg = (ex.getMostSpecificCause() != null) ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
    if (msg == null) msg = "";

    if (msg.contains("Duplicate entry")) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Tài nguyên đã tồn tại"));
    }
    if (msg.contains("cannot be null")) {
      return ResponseEntity.badRequest().body(Map.of("message", "Thiếu dữ liệu bắt buộc"));
    }
    if (msg.contains("a foreign key constraint fails")) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Vi phạm ràng buộc khóa ngoại"));
    }
    // Mặc định coi là dữ liệu không hợp lệ
    return ResponseEntity.badRequest().body(Map.of("message", "Dữ liệu không hợp lệ"));
  }

  // ---------- 500 INTERNAL SERVER ERROR ----------
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleOther(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("message", "Internal error"));
  }
}
