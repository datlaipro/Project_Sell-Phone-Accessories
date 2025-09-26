package com.github.datlaipro.shop.domain.product.repo;

import com.github.datlaipro.shop.domain.product.entity.ProductEntity;
import java.math.BigDecimal;
import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

        // Lấy danh sách theo category slug + model slug
        Page<ProductEntity> findDistinctByActiveTrueAndCategory_SlugAndDeviceFits_Slug(
                        String categorySlug, String modelSlug, Pageable pageable);

        // Tên gần đúng
        Page<ProductEntity> findDistinctByActiveTrueAndCategory_SlugAndDeviceFits_SlugAndNameContainingIgnoreCase(
                        String categorySlug, String modelSlug, String name, Pageable pageable);

        // Hỗ trợ nhiều model slug (OR)
        Page<ProductEntity> findDistinctByActiveTrueAndCategory_SlugAndDeviceFits_SlugIn(
                        String categorySlug, Collection<String> modelSlugs, Pageable pageable);

        // Lọc thêm khoảng giá (tuỳ chọn)
        Page<ProductEntity> findDistinctByActiveTrueAndCategory_SlugAndDeviceFits_SlugAndPriceBetween(
                        String categorySlug, String modelSlug, BigDecimal minPrice, BigDecimal maxPrice,
                        Pageable pageable);

        // Phiên bản JPQL linh hoạt (tên có thể null để bỏ qua)
        @Query("select p from ProductEntity p " +
                        "join p.category c " +
                        "where p.active = true " +
                        "  and c.slug = :categorySlug " +
                        "  and exists (" +
                        "    select 1 from p.deviceFits dm where dm.slug = :modelSlug" +
                        "  ) " +
                        "  and (:name is null or lower(p.name) like lower(concat('%', :name, '%')))")
        Page<ProductEntity> searchByCategoryAndModel(
                        @Param("categorySlug") String categorySlug,
                        @Param("modelSlug") String modelSlug,
                        @Param("name") String name,
                        Pageable pageable);

}
