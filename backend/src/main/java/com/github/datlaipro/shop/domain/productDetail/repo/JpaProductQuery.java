package com.github.datlaipro.shop.domain.productDetail.repo;

import com.github.datlaipro.shop.domain.productDetail.repo.projection.ProductDetailRow;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JpaProductQuery extends JpaRepository<com.github.datlaipro.shop.domain.productDetail.entity.ProductImage, Long> {

    // Lấy hàng chi tiết sản phẩm + tên category (native để khớp snake_case)
    @Query(value = """
      SELECT
        p.id,
        p.name,
        p.price,
        p.discount,
        p.color,
        p.quantity,
        p.brand,
        p.rate,
        c.name AS categoryName
      FROM products p
      LEFT JOIN categories c ON p.category_id = c.id
      WHERE p.id = :id AND p.is_active = 1
      """, nativeQuery = true)
  Optional<ProductDetailRow> findDetail(@Param("id") Long id);

    // Ảnh cover (1 tấm)
    @Query(value = """
        SELECT pi.image_url 
        FROM product_images pi
        WHERE pi.product_id = :pid AND pi.is_cover = 1
        ORDER BY pi.sort_order ASC, pi.id ASC
        LIMIT 1
        """, nativeQuery = true)
    String findCoverUrl(@Param("pid") Long productId);

    // Ảnh phụ (loại ảnh cover), giới hạn số lượng
    @Query(value = """
        SELECT pi.image_url
        FROM product_images pi
        WHERE pi.product_id = :pid AND pi.is_cover = 0
        ORDER BY pi.sort_order ASC, pi.id ASC
        LIMIT :limit
        """, nativeQuery = true)
    java.util.List<String> findNonCoverUrls(@Param("pid") Long productId, @Param("limit") int limit);

    // sort_order cần nếu bạn muốn trả kèm sort để FE sắp
    @Query(value = """
        SELECT CONCAT(pi.image_url, '||', pi.sort_order) as packed
        FROM product_images pi
        WHERE pi.product_id = :pid AND pi.is_cover = 0
        ORDER BY pi.sort_order ASC, pi.id ASC
        LIMIT :limit
        """, nativeQuery = true)
    java.util.List<String> findNonCoverPacked(@Param("pid") Long productId, @Param("limit") int limit);
}
