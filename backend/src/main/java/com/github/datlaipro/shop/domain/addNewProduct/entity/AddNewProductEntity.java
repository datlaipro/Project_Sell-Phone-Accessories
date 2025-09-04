package com.github.datlaipro.shop.domain.addNewProduct.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "products")
public class AddNewProductEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 255)
  private String name;

  @Lob
  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal price;

  @Column(nullable = false, precision = 3, scale = 2)
  private BigDecimal rate = new BigDecimal("0.00"); // default 0.00

  @Column(length = 100)
  private String color;

  @Column(nullable = false)
  private Integer quantity = 0; // default 0

  @Column(nullable = false, length = 100)
  private String brand;

  // Khóa ngoại map bằng ID để dễ tạo mới từ FE
  @Column(name = "category_id")
  private Long categoryId;

  @Column(name = "product_type_id")
  private Long productTypeId;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true; // default 1

  @Column(name = "created_by_admin_id")
  private Long createdByAdminId;

  @Column(name = "updated_by_admin_id")
  private Long updatedByAdminId;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;
  // AddNewProductEntity.java
  @Column(name = "discount", nullable = false, precision = 5, scale = 2)
  private BigDecimal discount = BigDecimal.ZERO;

  @PrePersist
  public void prePersist() {
    if (discount == null)
      discount = BigDecimal.ZERO;
  }

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  // ======= Getters & Setters =======

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public BigDecimal getRate() {
    return rate;
  }

  public void setRate(BigDecimal rate) {
    this.rate = rate;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public Integer getQuantity() {
    return quantity;
  }

    // ===== getters / setters =====
  public BigDecimal getDiscount() {
    return discount;
  }
  public void setDiscount(BigDecimal discount) {
    this.discount = discount;
  }


  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }

  public Long getProductTypeId() {
    return productTypeId;
  }

  public void setProductTypeId(Long productTypeId) {
    this.productTypeId = productTypeId;
  }

  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  public Long getCreatedByAdminId() {
    return createdByAdminId;
  }

  public void setCreatedByAdminId(Long createdByAdminId) {
    this.createdByAdminId = createdByAdminId;
  }

  public Long getUpdatedByAdminId() {
    return updatedByAdminId;
  }

  public void setUpdatedByAdminId(Long updatedByAdminId) {
    this.updatedByAdminId = updatedByAdminId;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
