package com.github.datlaipro.shop.domain.addNewProduct.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(
  name = "categories",
  uniqueConstraints = {
    @UniqueConstraint(name = "uq_categories_name", columnNames = "name"),
    @UniqueConstraint(name = "uq_categories_slug", columnNames = "slug")
  }
)
public class CategoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false, length = 255)
  private String name;

  @Column(name = "slug", nullable = false, length = 160)
  private String slug;

  // self-reference: categories.parent_id -> categories.id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_categories_parent"))
  private CategoryEntity parent;

  // Quan hệ ngược, không tạo cột mới
  @OneToMany(mappedBy = "parent")
  private Set<CategoryEntity> children = new HashSet<>();

  // Nếu DB set DEFAULT CURRENT_TIMESTAMP / ON UPDATE CURRENT_TIMESTAMP
  // bạn có thể để insertable/updatable=false để dùng giá trị do DB sinh ra
  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime updatedAt;

  // -------- Getters / Setters --------
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getSlug() { return slug; }
  public void setSlug(String slug) { this.slug = slug; }

  public CategoryEntity getParent() { return parent; }
  public void setParent(CategoryEntity parent) { this.parent = parent; }

  public Set<CategoryEntity> getChildren() { return children; }
  public void setChildren(Set<CategoryEntity> children) { this.children = children; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

  // -------- equals/hashCode/toString --------
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CategoryEntity)) return false;
    CategoryEntity that = (CategoryEntity) o;
    return id != null && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() { return 31; }

  @Override
  public String toString() {
    return "CategoryEntity{id=" + id + ", slug='" + slug + "'}";
  }
}
