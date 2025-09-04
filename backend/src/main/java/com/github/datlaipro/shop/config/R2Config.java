package com.github.datlaipro.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class R2Config {

  @Value("${r2.access-key-id}")     private String accessKey;
  @Value("${r2.secret-access-key}") private String secretKey;
  @Value("${r2.endpoint}")          private String endpoint;
  @Value("${r2.region:auto}")       private String region;

  @Bean
  public S3Client r2S3Client() {
    return S3Client.builder()
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)))
        .endpointOverride(URI.create(endpoint))
        .region(Region.of(region))
        .serviceConfiguration(S3Configuration.builder()
            .pathStyleAccessEnabled(true) // R2 khuyến nghị path-style
            .build())
        .build();
  }
}
