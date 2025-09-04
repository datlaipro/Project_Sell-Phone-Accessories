package com.github.datlaipro.shop.domain.addNewProduct.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
  name = "categories",
  uniqueConstraints = @UniqueConstraint(name = "uq_categories_name", columnNames = "name")
)
public class CategoryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false, length = 255)
  private String name;

  // self-reference: categories.parent_id -> categories.id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_categories_parent"))
  private CategoryEntity parent;

  // 1-n ngược lại, không tạo cột mới (mappedBy = "parent")
  @OneToMany(mappedBy = "parent")
  private List<CategoryEntity> children = new ArrayList<>();

  // DB set mặc định CURRENT_TIMESTAMP và auto-update, nên để insertable/updatable = false
  @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
  private LocalDateTime updatedAt;

  // ===== Getters / Setters =====
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public CategoryEntity getParent() { return parent; }
  public void setParent(CategoryEntity parent) { this.parent = parent; }

  public List<CategoryEntity> getChildren() { return children; }
  public void setChildren(List<CategoryEntity> children) { this.children = children; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
