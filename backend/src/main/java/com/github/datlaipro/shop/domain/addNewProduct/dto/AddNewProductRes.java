package com.github.datlaipro.shop.domain.addNewProduct.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AddNewProductRes {

  private Long id;
  private String name;
  private String description;
  private BigDecimal price;
  private BigDecimal rate;     // response không cần @DecimalMin
  private String color;
  private Integer quantity;
  private String brand;

  private Long categoryId;
  private String categoryName;

  private Boolean isActive;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // Tuỳ có dùng hay không
  private Long productTypeId;
  private Long createdByAdminId;
  private Long updatedByAdminId;

  // ===== Getters / Setters =====
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public BigDecimal getPrice() { return price; }
  public void setPrice(BigDecimal price) { this.price = price; }

  public BigDecimal getRate() { return rate; }
  public void setRate(BigDecimal rate) { this.rate = rate; }

  public String getColor() { return color; }
  public void setColor(String color) { this.color = color; }

  public Integer getQuantity() { return quantity; }
  public void setQuantity(Integer quantity) { this.quantity = quantity; }

  public String getBrand() { return brand; }
  public void setBrand(String brand) { this.brand = brand; }

  public Long getCategoryId() { return categoryId; }
  public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

  public String getCategoryName() { return categoryName; }
  public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

  public Boolean getIsActive() { return isActive; }
  public void setIsActive(Boolean isActive) { this.isActive = isActive; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

  public Long getProductTypeId() { return productTypeId; }
  public void setProductTypeId(Long productTypeId) { this.productTypeId = productTypeId; }

  public Long getCreatedByAdminId() { return createdByAdminId; }
  public void setCreatedByAdminId(Long createdByAdminId) { this.createdByAdminId = createdByAdminId; }

  public Long getUpdatedByAdminId() { return updatedByAdminId; }
  public void setUpdatedByAdminId(Long updatedByAdminId) { this.updatedByAdminId = updatedByAdminId; }
}
