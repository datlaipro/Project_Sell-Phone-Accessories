package com.github.datlaipro.shop.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CloudflareR2Service {

  private final S3Client s3;

  @Value("${r2.bucket}")
  private String bucket;

  // Khớp với cấu hình hiện tại của bạn: r2.public-base-url=...
  // Nếu bạn đổi sang r2.public-base thì sửa @Value ở đây cho phù hợp.
  @Value("${r2.public-base-url}")
  private String publicBase; // vd: https://pub-1ab7e6422ad14900bae96f39393904f5.r2.dev

  // 👇 Constructor TỰ VIẾT để gán s3 (tránh lỗi "variable s3 not initialized")
  public CloudflareR2Service(S3Client s3) {
    this.s3 = s3;
  }

  /** Upload 1 file, trả về URL public (null nếu file rỗng). */
  public String uploadOne(MultipartFile file, String prefix) {
    if (file == null || file.isEmpty()) return null;

    String ext = guessExt(file.getOriginalFilename(), file.getContentType()); // ".png", ".webp", ...
    String key = buildKey(prefix, ext); // ví dụ: product/2025-09-07/uuid.webp

    PutObjectRequest req = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .contentType(safeContentType(file.getContentType()))
        .cacheControl("public, max-age=31536000, immutable")
        .build();

    try {
      s3.putObject(req, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
    } catch (IOException e) {
      throw new RuntimeException("Upload R2 thất bại: " + e.getMessage(), e);
    }

    return toPublicUrl(key);
  }

  /** Upload nhiều file. */
  public List<String> uploadMany(MultipartFile[] files, String prefix) {
    List<String> urls = new ArrayList<>();
    if (files == null) return urls;
    for (MultipartFile f : files) {
      String u = uploadOne(f, prefix);
      if (u != null) urls.add(u);
    }
    return urls;
  }

  /** Xoá object theo URL public. */
  public void deleteByPublicUrl(String publicUrl) {
    String key = keyFromPublicUrl(publicUrl);
    if (key == null || key.isBlank()) return;
    s3.deleteObject(DeleteObjectRequest.builder()
        .bucket(bucket).key(key).build());
  }

  // ===== Helpers =====
  private String buildKey(String prefix, String ext) {
    String date = LocalDate.now().toString(); // YYYY-MM-DD
    return (prefix == null || prefix.isBlank() ? "" : prefix.trim() + "/")
        + date + "/" + UUID.randomUUID() + (ext == null ? "" : ext);
  }

  /** Ghép URL public: publicBase/bucket/<key>, encode theo từng segment (giữ nguyên dấu "/"). */
  public String toPublicUrl(String key) {
    String safeKey = Arrays.stream(key.split("/"))
        .map(CloudflareR2Service::encSeg)
        .collect(Collectors.joining("/"));
    return publicBase  + "/" + safeKey;
  }

  /** Lấy lại key từ URL public (để xoá). */
  public String keyFromPublicUrl(String publicUrl) {
    if (publicUrl == null) return null;
    String prefix = publicBase + "/" + bucket + "/";
    if (!publicUrl.startsWith(prefix)) return null;
    return publicUrl.substring(prefix.length());
  }

  private static String encSeg(String s) {
    return URLEncoder.encode(s, StandardCharsets.UTF_8).replace("+","%20");
  }

  private static String safeContentType(String ct) {
    if (ct == null || ct.isBlank()) return "application/octet-stream";
    return ct;
  }

  private static String guessExt(String filename, String ct) {
    if (filename != null && filename.contains(".")) {
      String e = filename.substring(filename.lastIndexOf('.')).toLowerCase();
      if (e.matches("\\.(png|jpg|jpeg|webp|gif|avif|bmp)")) return e;
    }
    if (ct != null) {
      if (ct.contains("png"))  return ".png";
      if (ct.contains("jpeg")) return ".jpg";
      if (ct.contains("webp")) return ".webp";
    }
    return "";
  }
}
