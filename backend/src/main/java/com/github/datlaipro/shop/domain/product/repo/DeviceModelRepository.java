package com.github.datlaipro.shop.domain.product.repo;

import com.github.datlaipro.shop.domain.product.entity.DeviceModelEntity;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
public interface DeviceModelRepository extends JpaRepository<DeviceModelEntity, Long> {

 @Query("""
    select m from DeviceModelEntity m
    where lower(m.slug) in :slugs
  """)
  List<DeviceModelEntity> findBySlugInIgnoreCase(@Param("slugs") Collection<String> slugs);

    Optional<DeviceModelEntity> findBySlugIgnoreCase(String slug);

  // (Tuỳ chọn) Nếu bạn muốn giữ service gọi findBySlug(...) y nguyên:
  default Optional<DeviceModelEntity> findBySlug(String slug) {
    return findBySlugIgnoreCase(slug);
  }
}
