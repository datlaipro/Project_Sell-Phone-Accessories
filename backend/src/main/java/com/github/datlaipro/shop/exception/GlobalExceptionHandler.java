package com.github.datlaipro.shop.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  // ---------- 401 UNAUTHORIZED ----------
  // Sai email/mật khẩu
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<Map<String, String>> handleBadCreds(BadCredentialsException ex) {
    log.debug("BadCredentials: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(Map.of("message", "Email hoặc mật khẩu không đúng"));
  }

  // Các lỗi auth khác do Spring Security ném ra
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<Map<String, String>> handleAuth(AuthenticationException ex) {
    log.debug("AuthenticationException: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(Map.of("message", "Unauthorized"));
  }

  // JWT không hợp lệ / hết hạn
  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<Map<String, String>> handleExpiredJwt(ExpiredJwtException ex) {
    log.debug("ExpiredJwtException: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .header(HttpHeaders.WWW_AUTHENTICATE, "Bearer error=\"invalid_token\", error_description=\"token expired\"")
        .body(Map.of("message", "Token hết hạn"));
  }

  @ExceptionHandler(JwtException.class)
  public ResponseEntity<Map<String, String>> handleJwt(JwtException ex) {
    log.debug("JwtException: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .header(HttpHeaders.WWW_AUTHENTICATE, "Bearer error=\"invalid_token\"")
        .body(Map.of("message", "Token không hợp lệ"));
  }

  // ---------- 403 FORBIDDEN ----------
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
    log.debug("AccessDeniedException: {}", ex.getMessage());
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
            this::resolvePath,
            ConstraintViolation::getMessage,
            (a, b) -> b,
            HashMap::new
        ));
    Map<String, Object> body = new HashMap<>();
    body.put("message", "Dữ liệu không hợp lệ");
    body.put("errors", errors);
    return ResponseEntity.badRequest().body(body);
  }

  // Binding error cho query/path (khác với @RequestBody)
  @ExceptionHandler(BindException.class)
  public ResponseEntity<Map<String, Object>> handleBind(BindException ex) {
    Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> b, HashMap::new));
    Map<String, Object> body = new HashMap<>();
    body.put("message", "Dữ liệu không hợp lệ");
    body.put("errors", errors);
    return ResponseEntity.badRequest().body(body);
  }

  private String resolvePath(ConstraintViolation<?> v) {
    var it = v.getPropertyPath().iterator();
    String last = "";
    while (it.hasNext()) last = it.next().getName();
    return last == null || last.isBlank() ? v.getPropertyPath().toString() : last;
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Map<String, String>> handleMalformedJson(HttpMessageNotReadableException ex) {
    log.debug("HttpMessageNotReadableException: {}", ex.getMessage());
    return ResponseEntity.badRequest().body(Map.of("message", "JSON không hợp lệ"));
  }

  // Sai kiểu dữ liệu cho tham số
  @ExceptionHandler({ MethodArgumentTypeMismatchException.class, TypeMismatchException.class, ConversionFailedException.class })
  public ResponseEntity<Map<String, String>> handleTypeMismatch(Exception ex) {
    log.debug("TypeMismatch: {}", ex.getMessage());
    return ResponseEntity.badRequest().body(Map.of("message", "Kiểu dữ liệu không đúng"));
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<Map<String, String>> handleMissingParam(MissingServletRequestParameterException ex) {
    log.debug("MissingServletRequestParameter: {}", ex.getParameterName());
    return ResponseEntity.badRequest().body(Map.of("message", "Thiếu tham số bắt buộc: " + ex.getParameterName()));
  }

  @ExceptionHandler(MissingPathVariableException.class)
  public ResponseEntity<Map<String, String>> handleMissingPathVar(MissingPathVariableException ex) {
    log.error("MissingPathVariable: {}", ex.getVariableName());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("message", "Thiếu biến trên URL: " + ex.getVariableName()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
    log.debug("IllegalArgumentException: {}", ex.getMessage());
    return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage() != null ? ex.getMessage() : "Yêu cầu không hợp lệ"));
  }

  // ---------- 404 / 405 ----------
  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<Map<String, String>> handleNoHandler(NoHandlerFoundException ex) {
    log.debug("NoHandlerFound: {} {}", ex.getHttpMethod(), ex.getRequestURL());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Map.of("message", "Không tìm thấy endpoint"));
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<Map<String, String>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
    log.debug("MethodNotSupported: {}", ex.getMethod());
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
        .body(Map.of("message", "Method không được hỗ trợ"));
  }

  // Khi service chủ động ném ResponseStatusException
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String, String>> handleResponseStatus(ResponseStatusException ex) {
    log.debug("ResponseStatusException: {} - {}", ex.getStatusCode(), ex.getReason());
    String msg = ex.getReason() != null ? ex.getReason() : ex.getStatusCode().toString();
    return ResponseEntity.status(ex.getStatusCode()).body(Map.of("message", msg));
  }

  // Not found các loại entity
  @ExceptionHandler({ EntityNotFoundException.class, NoSuchElementException.class })
  public ResponseEntity<Map<String, String>> handleEntityNotFound(RuntimeException ex) {
    log.debug("EntityNotFound/NoSuchElement: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Map.of("message", "Không tìm thấy tài nguyên"));
  }

  // ---------- 406 / 415 ----------
  @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
  public ResponseEntity<Map<String, String>> handleNotAcceptable(HttpMediaTypeNotAcceptableException ex) {
    log.debug("NotAcceptable: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
        .body(Map.of("message", "Không thoả mãn định dạng Accept"));
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<Map<String, String>> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
    log.debug("UnsupportedMediaType: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
        .body(Map.of("message", "Content-Type không được hỗ trợ"));
  }

  // ---------- Upload/file ----------
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<Map<String, String>> handleMaxUpload(MaxUploadSizeExceededException ex) {
    log.debug("MaxUploadSizeExceeded: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
        .body(Map.of("message", "File quá lớn"));
  }

  @ExceptionHandler(MultipartException.class)
  public ResponseEntity<Map<String, String>> handleMultipart(MultipartException ex) {
    log.debug("MultipartException: {}", ex.getMessage());
    return ResponseEntity.badRequest().body(Map.of("message", "Lỗi upload file"));
  }

  // ---------- 409 CONFLICT & lỗi ràng buộc DB ----------
  // Chi tiết hoá lỗi DB: duplicate/null/foreign key...
  @ExceptionHandler({ DataIntegrityViolationException.class, SQLIntegrityConstraintViolationException.class, DuplicateKeyException.class })
  public ResponseEntity<Map<String, String>> handleSql(Exception ex) {
    String msg = ex.getMessage();
    if (ex instanceof DataIntegrityViolationException dive && dive.getMostSpecificCause() != null) {
      msg = dive.getMostSpecificCause().getMessage();
    }
    if (msg == null) msg = "";

    log.debug("DB Constraint: {}", msg);

    String lower = msg.toLowerCase();

    if (lower.contains("duplicate entry") || lower.contains("unique") || lower.contains("uq_")) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Tài nguyên đã tồn tại"));
    }
    if (lower.contains("cannot be null") || lower.contains("null") && lower.contains("column")) {
      return ResponseEntity.badRequest().body(Map.of("message", "Thiếu dữ liệu bắt buộc"));
    }
    if (lower.contains("a foreign key constraint fails") || lower.contains("foreign key")) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Vi phạm ràng buộc khóa ngoại"));
    }
    // Mặc định coi là dữ liệu không hợp lệ
    return ResponseEntity.badRequest().body(Map.of("message", "Dữ liệu không hợp lệ"));
  }

  // ---------- 500 INTERNAL SERVER ERROR ----------
  @ExceptionHandler(HttpMessageNotWritableException.class)
  public ResponseEntity<Map<String, String>> handleNotWritable(HttpMessageNotWritableException ex) {
    log.error("HttpMessageNotWritableException", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("message", "Không thể ghi phản hồi"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleOther(Exception ex) {
    log.error("Unexpected error", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("message", "Internal error"));
  }
}
