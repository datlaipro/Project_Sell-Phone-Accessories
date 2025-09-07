package com.github.datlaipro.shop.domain.product.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductRes {
  private Long id;
  private String name;
  private String description;
  private BigDecimal price;
  private BigDecimal rate;
  private String color;
  private Integer quantity;
  private String brand;
  private boolean active;
  private BigDecimal discount;
  private String subSlug;

  private String categoryName;
  private String categorySlug;

  private List<String> deviceModelNames = new ArrayList<>();
  private List<String> deviceModelSlugs = new ArrayList<>();

  // 👇 NEW: ảnh đại diện (is_cover = true)
  private Long coverImageId;
  private String coverImageUrl;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public ProductRes() {}

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

  public boolean isActive() { return active; }
  public void setActive(boolean active) { this.active = active; }

  public BigDecimal getDiscount() { return discount; }
  public void setDiscount(BigDecimal discount) { this.discount = discount; }

  public String getSubSlug() { return subSlug; }
  public void setSubSlug(String subSlug) { this.subSlug = subSlug; }

  public String getCategoryName() { return categoryName; }
  public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

  public String getCategorySlug() { return categorySlug; }
  public void setCategorySlug(String categorySlug) { this.categorySlug = categorySlug; }

  public List<String> getDeviceModelNames() { return deviceModelNames; }
  public void setDeviceModelNames(List<String> deviceModelNames) { this.deviceModelNames = deviceModelNames; }

  public List<String> getDeviceModelSlugs() { return deviceModelSlugs; }
  public void setDeviceModelSlugs(List<String> deviceModelSlugs) { this.deviceModelSlugs = deviceModelSlugs; }

  public Long getCoverImageId() { return coverImageId; }
  public void setCoverImageId(Long coverImageId) { this.coverImageId = coverImageId; }

  public String getCoverImageUrl() { return coverImageUrl; }
  public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
