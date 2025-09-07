package com.github.datlaipro.shop.domain.product.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(
  name = "device_models",
  indexes = {
    @Index(name = "idx_device_models_brand", columnList = "brand_id"),
    @Index(name = "uq_device_models_slug", columnList = "slug", unique = true)
  }
)
public class DeviceModelEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Nếu chưa map BrandEntity, để raw id
  @Column(name = "brand_id", nullable = false)
  private Long brandId;

  @Column(name = "name", nullable = false, length = 150)
  private String name;

  @Column(name = "slug", nullable = false, length = 160)
  private String slug;

  @ManyToMany(mappedBy = "deviceFits")
  private Set<com.github.datlaipro.shop.domain.product.entity.ProductEntity> products = new HashSet<>();

  // --- getters/setters ---

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Long getBrandId() { return brandId; }
  public void setBrandId(Long brandId) { this.brandId = brandId; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getSlug() { return slug; }
  public void setSlug(String slug) { this.slug = slug; }

  public Set<com.github.datlaipro.shop.domain.product.entity.ProductEntity> getProducts() { return products; }
  public void setProducts(Set<com.github.datlaipro.shop.domain.product.entity.ProductEntity> products) { this.products = products; }

  // --- equals/hashCode/toString ---

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof DeviceModelEntity)) return false;
    DeviceModelEntity that = (DeviceModelEntity) o;
    return id != null && id.equals(that.id);
  }

  @Override
  public int hashCode() { return 31; }

  @Override
  public String toString() {
    return "DeviceModelEntity{id=" + id + ", slug='" + slug + "'}";
  }
}
