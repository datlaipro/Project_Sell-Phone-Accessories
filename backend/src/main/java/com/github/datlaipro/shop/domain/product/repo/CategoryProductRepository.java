package com.github.datlaipro.shop.domain.product.repo;

import com.github.datlaipro.shop.domain.addNewProduct.entity.CategoryEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface CategoryProductRepository extends JpaRepository<CategoryEntity, Long> {

  Optional<CategoryEntity> findBySlug(String slug);

  Optional<CategoryEntity> findByNameIgnoreCase(String name);

  // Nếu chỉ cần lấy id cho nhanh
  @Query("select c.id from CategoryEntity c where lower(c.slug) = lower(:slug)")
  Optional<Long> findIdBySlug(@Param("slug") String slug);

  @Query("select c.id from CategoryEntity c where lower(c.name) = lower(:name)")
  Optional<Long> findIdByNameIgnoreCase(@Param("name") String name);
}
