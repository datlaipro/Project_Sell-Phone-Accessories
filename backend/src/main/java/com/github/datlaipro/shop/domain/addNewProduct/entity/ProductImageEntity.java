package com.github.datlaipro.shop.domain.addNewProduct.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_images")
public class ProductImageEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // product_images.product_id -> products.id
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_images_product"))
  private AddNewProductEntity product;

  @Column(name = "image_url", nullable = false, length = 500)
  private String imageUrl;

  @Column(name = "sort_order", nullable = false)
  private Integer sortOrder = 0;
  @Column(name = "is_cover", nullable = false)
  private Boolean isCover = false; // <— thêm

  // DB tự set CURRENT_TIMESTAMP
  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;

  // ===== Getters / Setters =====
  public Long getId() {
    return id;
  }

  // Getter/Setter cho isCover
  public Boolean getIsCover() {
    return isCover;
  }

  public void setIsCover(Boolean isCover) {
    this.isCover = isCover;
  }

  // (tuỳ chọn) Overload để khớp lời gọi setIsCover(boolean) ở service
  public void setIsCover(boolean isCover) {
    this.isCover = Boolean.valueOf(isCover);
  }

  public void setId(Long id) {
    this.id = id;
  }

  public AddNewProductEntity getProduct() {
    return product;
  }

  public void setProduct(AddNewProductEntity product) {
    this.product = product;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public Integer getSortOrder() {
    return sortOrder;
  }

  public void setSortOrder(Integer sortOrder) {
    this.sortOrder = sortOrder;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
