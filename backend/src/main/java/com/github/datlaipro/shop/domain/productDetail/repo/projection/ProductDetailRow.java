package com.github.datlaipro.shop.domain.productDetail.repo.projection;

import java.math.BigDecimal;

/**
 * Projection cho truy vấn chi tiết sản phẩm (header).
 *
 * Lưu ý:
 * - Trường {@code description} KHÔNG lấy từ bảng products nữa.
 *   Repository cần SELECT alias "description" từ bảng product_specs
 *   (ví dụ: mô tả lấy từ attr='Description' hoặc fallback attr='Features').
 *
 *   Ví dụ native SQL tối thiểu:
 *
 *   SELECT
 *     p.id,
 *     p.name,
 *     p.price,
 *     p.discount,
 *     p.color,
 *     p.quantity,
 *     p.brand,
 *     p.rate,
 *     c.name AS categoryName,
 *     (
 *       SELECT GROUP_CONCAT(ps.val SEPARATOR '\n')
 *       FROM product_specs ps
 *       WHERE ps.product_id = p.id
 *         AND (ps.attr = 'Description' OR ps.attr = 'Features')
 *     ) AS description
 *   FROM products p
 *   LEFT JOIN categories c ON p.category_id = c.id
 *   WHERE p.id = :id AND p.is_active = 1
 *
 * - Khi không có mô tả trong product_specs, field này có thể null.
 */
public interface ProductDetailRow {
    Long getId();
    String getName();

    /** Mô tả lấy từ product_specs (alias "description" trong câu SELECT), có thể null */
    String getDescription();

    BigDecimal getPrice();
    BigDecimal getDiscount(); // từ bảng products
    String getColor();
    Integer getQuantity();
    String getBrand();
    Double getRate();
    String getCategoryName(); // từ categories.name
}
