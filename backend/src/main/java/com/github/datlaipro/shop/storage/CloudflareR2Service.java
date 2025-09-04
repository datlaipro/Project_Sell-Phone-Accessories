package com.github.datlaipro.shop.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CloudflareR2Service {

  private final S3Client s3;
  private final String bucket;
  private final String publicBaseUrl;

  public CloudflareR2Service(
      S3Client r2S3Client,
      @Value("${r2.bucket}") String bucket,
      @Value("${r2.public-base-url}") String publicBaseUrl
  ) {
    this.s3 = r2S3Client;
    this.bucket = bucket;
    this.publicBaseUrl = publicBaseUrl.replaceAll("/+$", "");
  }

  public String uploadOne(MultipartFile file, String keyPrefix) throws IOException {
    if (file == null || file.isEmpty()) return null;

    String original = file.getOriginalFilename();
    String ext = (original != null && original.contains(".")) ?
        original.substring(original.lastIndexOf('.')) : "";
    String safePrefix = keyPrefix == null ? "" : keyPrefix.replaceAll("^/+", "").replaceAll("/+$", "");
    String key = String.format("%s/%s/%s%s",
        safePrefix.isEmpty() ? "uploads" : safePrefix,
        LocalDate.now(), UUID.randomUUID(), ext);

    PutObjectRequest req = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .contentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
        .build();

    s3.putObject(req, RequestBody.fromBytes(file.getBytes()));

    // Tạo URL public (bucket phải public hoặc bạn proxy qua CDN)
    String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8).replace("+", "%20");
    return publicBaseUrl + "/" + bucket + "/" + encodedKey;
  }

  public List<String> uploadMany(MultipartFile[] files, String keyPrefix) throws IOException {
    List<String> urls = new ArrayList<>();
    if (files == null) return urls;
    for (MultipartFile f : files) {
      String url = uploadOne(f, keyPrefix);
      if (url != null) urls.add(url);
    }
    return urls;
  }
}
