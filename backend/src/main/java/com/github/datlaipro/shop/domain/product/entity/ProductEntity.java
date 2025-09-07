package com.github.datlaipro.shop.domain.product.entity;

import com.github.datlaipro.shop.domain.addNewProduct.entity.CategoryEntity;
import com.github.datlaipro.shop.domain.product.entity.DeviceModelEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "products")
public class ProductEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false, length = 255)
  private String name;

  

  @Column(name = "price", nullable = false, precision = 12, scale = 2)
  private BigDecimal price;

  @Column(name = "rate", nullable = false, precision = 3, scale = 2)
  private BigDecimal rate = BigDecimal.ZERO;

  @Column(name = "color", length = 100)
  private String color;

  @Column(name = "quantity", nullable = false)
  private Integer quantity = 0;

  @Column(name = "brand", nullable = false, length = 100)
  private String brand;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private CategoryEntity category;

  @Column(name = "sub_slug", length = 160)
  private String subSlug;

  @Column(name = "is_active", nullable = false)
  private boolean active = true;

  @Column(name = "created_by_admin_id")
  private Long createdByAdminId;

  @Column(name = "updated_by_admin_id")
  private Long updatedByAdminId;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "discount", nullable = false, precision = 5, scale = 2)
  private BigDecimal discount = BigDecimal.ZERO;

  @Column(name = "product_type_id")
  private Long productTypeId;

  @ManyToMany
  @JoinTable(
    name = "product_device_fit",
    joinColumns = @JoinColumn(name = "product_id"),
    inverseJoinColumns = @JoinColumn(name = "device_model_id")
  )
  private Set<DeviceModelEntity> deviceFits = new HashSet<>();

  // --- getters/setters ---

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }


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

  public CategoryEntity getCategory() { return category; }
  public void setCategory(CategoryEntity category) { this.category = category; }

  public String getSubSlug() { return subSlug; }
  public void setSubSlug(String subSlug) { this.subSlug = subSlug; }

  public boolean isActive() { return active; }
  public void setActive(boolean active) { this.active = active; }

  public Long getCreatedByAdminId() { return createdByAdminId; }
  public void setCreatedByAdminId(Long createdByAdminId) { this.createdByAdminId = createdByAdminId; }

  public Long getUpdatedByAdminId() { return updatedByAdminId; }
  public void setUpdatedByAdminId(Long updatedByAdminId) { this.updatedByAdminId = updatedByAdminId; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

  public BigDecimal getDiscount() { return discount; }
  public void setDiscount(BigDecimal discount) { this.discount = discount; }

  public Long getProductTypeId() { return productTypeId; }
  public void setProductTypeId(Long productTypeId) { this.productTypeId = productTypeId; }

  public Set<DeviceModelEntity> getDeviceFits() { return deviceFits; }
  public void setDeviceFits(Set<DeviceModelEntity> deviceFits) { this.deviceFits = deviceFits; }

  // --- equals/hashCode/toString ---

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ProductEntity)) return false;
    ProductEntity that = (ProductEntity) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() { return 31; }

  @Override
  public String toString() {
    return "ProductEntity{id=" + id + ", name='" + name + "'}";
  }
}
